package inhatc.k8sProject.particulateMatter.service.gangwon;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import inhatc.k8sProject.particulateMatter.domain.gangwon.GangwonAirQuality;
import inhatc.k8sProject.particulateMatter.domain.gangwon.GangwonStationInfo;
import inhatc.k8sProject.particulateMatter.dto.StationAirQualityInfoDTO;
import inhatc.k8sProject.particulateMatter.repository.gangwon.GangwonAirQualityRepository;
import inhatc.k8sProject.particulateMatter.repository.gangwon.GangwonStationInfoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GangwonStationInfoService {

    private final GangwonStationInfoRepository gangwonStationInfoRepository;
    private final GangwonAirQualityRepository gangwonAirQualityRepository;
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 파싱을 위한 ObjectMapper
    private static final Logger log = LoggerFactory.getLogger(GangwonStationInfoService.class);

    //프로퍼티에서 API 키 값을 받아오는 어노테이션
    @Value("${service.key}") // 애플리케이션 속성 파일에서 가져온 값
    private String serviceKey;


    @Scheduled(cron = "0 */20 * * * *")
    public void updateStationInfoDataAutomatically() {
        String sidoName = "강원"; // 대상 지역 이름
        fetchAndSaveGangwonStationInfo(sidoName); // 해당 지역의 측정소 정보 가져와 저장
    }
    //------------------------------------------------------------------------------------------------------------------------

    // 강원도 측정소 정보를 가져와 저장하는 메서드
    @Transactional("gangwonTransactionManager")
    public void fetchAndSaveGangwonStationInfo(String sidoName) {
        try {
            // API 요청을 위한 URL 구성
            String requestUrlBuilder = "https://apis.data.go.kr/B552584/MsrstnInfoInqireSvc/getMsrstnList?" + "&addr=" + URLEncoder.encode(sidoName, StandardCharsets.UTF_8) +
                    "&pageNo=" + URLEncoder.encode("1", StandardCharsets.UTF_8) +
                    "&numOfRows=" + URLEncoder.encode("100", StandardCharsets.UTF_8) +
                    "&serviceKey=" + serviceKey + // 서비스 키 추가
                    "&returnType=" + URLEncoder.encode("json", StandardCharsets.UTF_8);

            URL url = new URL(requestUrlBuilder);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            // HTTP 응답 코드 확인
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                log.error("HTTP 오류 코드 : " + conn.getResponseCode());
            }

            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                responseBuilder.append(line);
            }
            rd.close();
            conn.disconnect();

            String response = responseBuilder.toString();

            // 응답 형식 검증
            if (response.trim().startsWith("<")) {
                log.error("예상하지 않은 JSON 형식의 응답입니다. 응답: " + response);
            }

            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode items = rootNode.path("response").path("body").path("items");

            deleteExistingStationInfo(sidoName);


            // JSON 데이터 파싱 및 저장
            for (JsonNode item : items) {
                GangwonStationInfo stationInfo = parseStationInfoData(item);
                gangwonStationInfoRepository.save(stationInfo);
            }

        } catch (IOException e) {
            log.error("측정소 정보를 가져오고 저장하는 데 실패했습니다", e);
        }
    }
    //------------------------------------------------------------------------------------------------------------------------

    // 해당 지역의 기존 측정소 정보를 삭제하는 메소드
    private void deleteExistingStationInfo(String sidoName) {
        List<GangwonStationInfo> existingStations = gangwonStationInfoRepository.findByAddrContaining(sidoName);
        gangwonStationInfoRepository.deleteAll(existingStations);
    }
    //--------------------------------------------------------------------------------------------------------------------------------------

    // JSON 데이터를 파싱하여 GangwonStationInfo 객체로 변환하는 메서드
    private GangwonStationInfo parseStationInfoData(JsonNode item) {
        GangwonStationInfo gangwonStationInfo = new GangwonStationInfo();
        gangwonStationInfo.setStationName(item.path("stationName").asText(null)); // 측정소 이름 설정
        gangwonStationInfo.setAddr(item.path("addr").asText(null)); // 주소 설정
        gangwonStationInfo.setDmX(item.path("dmX").asDouble()); // X 좌표 설정
        gangwonStationInfo.setDmY(item.path("dmY").asDouble()); // Y 좌표 설정

        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime kstDateTime = currentDateTime.plusHours(9); // KST로 변환
        gangwonStationInfo.setInPutDataTime(kstDateTime);
        return gangwonStationInfo;
    }
    //------------------------------------------------------------------------------------------------------------------------


    // 가장 최근에 입력된 측정소 데이터를 조회하는 메소드
    @Transactional(readOnly = true)
    public List<GangwonStationInfo> findRecentGangownStationsData() {
        // 데이터베이스에서 가장 최근에 입력된 데이터의 시간을 조회
        Optional<GangwonStationInfo> nearestEntry = gangwonStationInfoRepository.findTopByOrderByInPutDataTimeDesc();

        // 만약 최근 데이터가 존재하면 해당 데이터의 시간을 사용하고, 존재하지 않으면 현재 시간에서 5분을 뺀 시간을 기준으로 설정
        LocalDateTime lastDataTime = nearestEntry.map(GangwonStationInfo::getInPutDataTime)
                .orElse(LocalDateTime.now().minusMinutes(5));

        // 계산된 시간부터 현재 시간까지의 데이터를 조회
        return gangwonStationInfoRepository.findByInPutDataTimeBetween(lastDataTime, LocalDateTime.now());
    }
    //--------------------------------------------------------------------------------------------------------------------------------------

    // 최근 측정소와 그에 대한 공기질 정보를 조회하는 메소드
    @Transactional(readOnly = true)
    public List<StationAirQualityInfoDTO> findRecentGangwonStationsWithAirQuality() {
        // 모든 측정소 정보를 조회
        List<GangwonStationInfo> stations = gangwonStationInfoRepository.findAll();
        log.info("Fetched {} stations data.", stations.size()); // 조회된 측정소 수 로그 기록
        // 각 측정소에 대해 공기질 정보를 가져와 StationAirQualityInfo로 매핑하고 필터링하여 반환
        return stations.stream().map(station -> {
            // 각 측정소에 대한 최신 공기질 정보를 조회
            Optional<GangwonAirQuality> airQualityOptional = gangwonAirQualityRepository
                    .findFirstByStationNameOrderByDataTimeDesc(station.getStationName());
            // 공기질 정보가 존재할 경우 로그 기록 및 StationAirQualityInfo 생성 후 반환, 존재하지 않을 경우 null 반환
            airQualityOptional.ifPresent(aq -> log.info("Air quality data for {}: PM10 = {}, PM2.5 = {}", station.getStationName(), aq.getPm10Value(), aq.getPm25Value()));
            return airQualityOptional.map(aq -> new StationAirQualityInfoDTO(station, aq)).orElse(null);
        }).filter(Objects::nonNull).collect(Collectors.toList()); // null이 아닌 StationAirQualityInfo만 필터링하여 반환
    }
    //--------------------------------------------------------------------------------------------------------------------------------------



}
