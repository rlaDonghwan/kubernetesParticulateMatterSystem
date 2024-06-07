package inhatc.k8sProject.particulateMatter.service.gyeongsang;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import inhatc.k8sProject.particulateMatter.domain.gyeongsang.GyeongsangAirQuality;
import inhatc.k8sProject.particulateMatter.domain.gyeongsang.GyeongsangStationInfo;
import inhatc.k8sProject.particulateMatter.dto.StationAirQualityInfoDTO;
import inhatc.k8sProject.particulateMatter.repository.gyeongsang.GyeongsangAirQualityRepository;
import inhatc.k8sProject.particulateMatter.repository.gyeongsang.GyeongsangStationInfoRepository;
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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GyeongsangStationInfoService {

    private final GyeongsangStationInfoRepository gyeongsangStationInfoRepository;
    private final GyeongsangAirQualityRepository gyeongsangAirQualityRepository;
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 파싱을 위한 ObjectMapper
    private static final Logger log = LoggerFactory.getLogger(GyeongsangStationInfoRepository.class);

    //프로퍼티에서 API 키 값을 받아오는 어노테이션
    @Value("${service.key}")
    private String serviceKey;


    @Scheduled(cron = "0 */20 * * * *")
    public void updateAirQualityDataAutomatically() {
        // 스케줄링된 작업: 일정 간격으로 대기 질 데이터를 업데이트하는 메소드
        List<String> sidoList = Arrays.asList("경북", "경남", "대구", "울산", "부산");
        sidoList.forEach(this::fetchAndSaveGyeongsangStationInfo);
    }

    // 경북, 경남, 대구, 울산, 부산 지역의 측정소 정보를 가져와 저장하는 메서드
    @Transactional("gyeongsangTransactionManager")
    public void fetchAndSaveGyeongsangStationInfo(String sidoName) {
        try {
            String requestUrlBuilder = "https://apis.data.go.kr/B552584/MsrstnInfoInqireSvc/getMsrstnList?" + "&addr=" + URLEncoder.encode(sidoName, StandardCharsets.UTF_8) +
                    "&pageNo=" + URLEncoder.encode("1", StandardCharsets.UTF_8) +
                    "&numOfRows=" + URLEncoder.encode("100", StandardCharsets.UTF_8) +
                    "&serviceKey=" + serviceKey +
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

            //값을 불러 올때마다 해당 지역의 있는 측정소 정보를 삭제하는 메서드 호출
            deleteExistingStationInfo(sidoName);

            for (JsonNode item : items) {
                GyeongsangStationInfo gyeongsangStationInfo = parseStationInfoData(item);
                gyeongsangStationInfoRepository.save(gyeongsangStationInfo);
            }

        } catch (IOException e) {
            log.error("측정소 정보를 가져오고 저장하는 데 실패했습니다", e);
        }
    }
    //--------------------------------------------------------------------------------------------------------------------------------------

    // 해당 지역의 기존 측정소 정보를 삭제하는 메소드
    private void deleteExistingStationInfo(String sidoName) {
        List<GyeongsangStationInfo> existingStations = gyeongsangStationInfoRepository.findByAddrContaining(sidoName);
        gyeongsangStationInfoRepository.deleteAll(existingStations);
    }
    //--------------------------------------------------------------------------------------------------------------------------------------

    // JSON 데이터에서 측정소 정보를 파싱하는 메소드
    private GyeongsangStationInfo parseStationInfoData(JsonNode item) {
        GyeongsangStationInfo gyeongsangStationInfo = new GyeongsangStationInfo();
        gyeongsangStationInfo.setStationName(item.path("stationName").asText(null)); // 측정소 이름 설정
        gyeongsangStationInfo.setAddr(item.path("addr").asText(null)); // 주소 설정
        gyeongsangStationInfo.setDmX(item.path("dmX").asDouble()); // X 좌표 설정
        gyeongsangStationInfo.setDmY(item.path("dmY").asDouble()); // Y 좌표 설정

        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime kstDateTime = currentDateTime.plusHours(9); // KST로 변환
        gyeongsangStationInfo.setInPutDataTime(kstDateTime);

        return gyeongsangStationInfo;
    }

    // 가장 최근에 입력된 측정소 데이터를 조회하는 메소드
    @Transactional(readOnly = true)
    public List<GyeongsangStationInfo> findRecentGyeongsangStationsData() {
        // 현재 시간 이하에서 가장 가까운 시간대의 데이터를 조회
        Optional<GyeongsangStationInfo> nearestEntry = gyeongsangStationInfoRepository.findTopByOrderByInPutDataTimeDesc();

        // 최근 데이터의 시간을 기준으로 설정
        LocalDateTime lastDataTime = nearestEntry.map(GyeongsangStationInfo::getInPutDataTime)
                .orElse(LocalDateTime.now().minusMinutes(5)); // 최근 데이터가 없을 경우 현재 시간에서 5분 전으로 설정
        System.out.println(LocalDateTime.now());
        System.out.println(lastDataTime);

        // 계산된 시간부터 현재 시간까지의 데이터를 조회
        return gyeongsangStationInfoRepository.findByInPutDataTimeBetween(lastDataTime, LocalDateTime.now());
    }

    //--------------------------------------------------------------------------------------------------------------------------------------

    // 최근 측정소와 그에 대한 공기질 정보를 조회하는 메소드
    @Transactional(readOnly = true)
    public List<StationAirQualityInfoDTO> findRecentGyeongsangStationsWithAirQuality() {
        List<GyeongsangStationInfo> stations = gyeongsangStationInfoRepository.findAll();
        log.info("Fetched {} stations data.", stations.size()); // 조회된 측정소 수 로그 기록
        return stations.stream().map(station -> {
            Optional<GyeongsangAirQuality> airQualityOptional = gyeongsangAirQualityRepository
                    .findFirstByStationNameOrderByDataTimeDesc(station.getStationName());
            airQualityOptional.ifPresent(aq -> log.info("Air quality data for {}: PM10 = {}, PM2.5 = {}", station.getStationName(), aq.getPm10Value(), aq.getPm25Value()));
            return airQualityOptional.map(aq -> new StationAirQualityInfoDTO(station, aq)).orElse(null);
        }).filter(Objects::nonNull).collect(Collectors.toList()); // null이 아닌 StationAirQualityInfo만 필터링하여 반환
    }
    //--------------------------------------------------------------------------------------------------------------------------------------

}
