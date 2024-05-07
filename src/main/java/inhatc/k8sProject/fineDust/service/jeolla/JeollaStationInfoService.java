package inhatc.k8sProject.fineDust.service.jeolla;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import inhatc.k8sProject.fineDust.domain.jeolla.JeollaAirQuality;
import inhatc.k8sProject.fineDust.domain.jeolla.JeollaStationInfo;
import inhatc.k8sProject.fineDust.dto.StationAirQualityInfoDTO;
import inhatc.k8sProject.fineDust.repository.gyeongsang.GyeongsangStationInfoRepository;
import inhatc.k8sProject.fineDust.repository.jeolla.JeollaAirQualityRepository;
import inhatc.k8sProject.fineDust.repository.jeolla.JeollaStationInfoRepository;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JeollaStationInfoService {

    private final JeollaStationInfoRepository jeollaStationInfoRepository;
    private final JeollaAirQualityRepository jeollaAirQualityRepository;
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 파싱을 위해 추가
    private static final Logger log = LoggerFactory.getLogger(GyeongsangStationInfoRepository.class);

    //프로퍼티에서 API 키 값을 받아오는 어노테이션
    @Value("${service.key}")
    private String serviceKey;


    @Scheduled(cron = "0 0,30 * * * *")
    public void updateAirQualityDataAutomatically() {
        // 스케줄링된 작업: 일정 간격으로 대기 질 데이터를 업데이트하는 메소드
        List<String> sidoList = Arrays.asList("전북", "전남", "광주");
        sidoList.forEach(this::fetchAndSaveJeollaStationInfo);
    }
    //--------------------------------------------------------------------------------------------------------------------------------------

    // 전북, 전남, 광주 지역의 측정소 정보를 가져와 저장하는 메소드
    @Transactional("jeollaTransactionManager")
    public String fetchAndSaveJeollaStationInfo(String sidoName) {
        try {
            StringBuilder requestUrlBuilder = new StringBuilder("https://apis.data.go.kr/B552584/MsrstnInfoInqireSvc/getMsrstnList?");
            requestUrlBuilder.append("&addr=").append(URLEncoder.encode(sidoName, "UTF-8"));
            requestUrlBuilder.append("&pageNo=").append(URLEncoder.encode("1", "UTF-8"));
            requestUrlBuilder.append("&numOfRows=").append(URLEncoder.encode("100", "UTF-8"));
            requestUrlBuilder.append("&serviceKey=").append(serviceKey);
            requestUrlBuilder.append("&returnType=").append(URLEncoder.encode("json", "UTF-8"));


            URL url = new URL(requestUrlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            // 응답 코드 확인
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                log.error("HTTP error code : " + conn.getResponseCode());
                return "Failure due to HTTP error";
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
            if (response.trim().startsWith("<")) {
                log.error("Response is not in expected JSON format. Response: " + response);
                return "Failure due to unexpected response format";
            }

            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode items = rootNode.path("response").path("body").path("items");

            //값을 불러 올때마다 해당 지역의 있는 측정소 정보를 삭제하는 메서드 호출
            deleteExistingStationInfo(sidoName);

            for (JsonNode item : items) {
                JeollaStationInfo jeollaStationInfo = parseStationInfoData(item);
                jeollaStationInfoRepository.save(jeollaStationInfo);
            }

            return "Success";
        } catch (IOException e) {
            log.error("Failed to fetch and save station info", e);
            return "Failure";
        }
    }
    //--------------------------------------------------------------------------------------------------------------------------------------

    // 해당 지역의 기존 측정소 정보를 삭제하는 메소드
    private void deleteExistingStationInfo(String sidoName) {
        List<JeollaStationInfo> existingStations = jeollaStationInfoRepository.findByAddrContaining(sidoName);
        jeollaStationInfoRepository.deleteAll(existingStations);
    }
    //--------------------------------------------------------------------------------------------------------------------------------------


    // JSON 데이터에서 측정소 정보를 파싱하는 메소드
    private JeollaStationInfo parseStationInfoData(JsonNode item) {
        JeollaStationInfo jeollaStationInfo = new JeollaStationInfo();
        jeollaStationInfo.setStationName(item.path("stationName").asText(null)); // 측정소 이름
        jeollaStationInfo.setAddr(item.path("addr").asText(null)); // 주소
        jeollaStationInfo.setDmX(item.path("dmX").asDouble()); // X 좌표
        jeollaStationInfo.setDmY(item.path("dmY").asDouble()); // Y 좌표
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime kstDateTime = currentDateTime.plusHours(9); // KST로 변환
        jeollaStationInfo.setInPutDataTime(kstDateTime);
        return jeollaStationInfo;
    }
    //--------------------------------------------------------------------------------------------------------------------------------------

    // 가장 최근에 입력된 측정소 데이터를 조회하는 메소드
    @Transactional(readOnly = true)
    public List<JeollaStationInfo> findRecentJeollaStationsData() {
        // 현재 시간 이하에서 가장 가까운 시간대의 데이터를 조회
        Optional<JeollaStationInfo> nearestEntry = jeollaStationInfoRepository.findTopByOrderByInPutDataTimeDesc();

        // 최근 데이터의 시간을 기준으로 설정
        LocalDateTime lastDataTime = nearestEntry.map(JeollaStationInfo::getInPutDataTime)
                .orElse(LocalDateTime.now().minusMinutes(5)); // 최근 데이터가 없을 경우 현재 시간에서 5분 전으로 설정
        System.out.println(LocalDateTime.now());
        System.out.println(lastDataTime);

        // 계산된 시간부터 현재 시간까지의 데이터를 조회
        return jeollaStationInfoRepository.findByInPutDataTimeBetween(lastDataTime, LocalDateTime.now());
    }
    //--------------------------------------------------------------------------------------------------------------------------------------

    // 최근 측정소와 그에 대한 공기질 정보를 조회하는 메소드
    @Transactional(readOnly = true)
    public List<StationAirQualityInfoDTO> findRecentJeollaStationsWithAirQuality() {
        List<JeollaStationInfo> stations = jeollaStationInfoRepository.findAll();
        log.info("Fetched {} stations data.", stations.size()); // 조회된 측정소 수 로그 기록
        return stations.stream().map(station -> {
            Optional<JeollaAirQuality> airQualityOptional = jeollaAirQualityRepository
                    .findFirstByStationNameOrderByDataTimeDesc(station.getStationName());
            airQualityOptional.ifPresent(aq -> log.info("Air quality data for {}: PM10 = {}, PM2.5 = {}", station.getStationName(), aq.getPm10Value(), aq.getPm25Value()));
            return airQualityOptional.map(aq -> new StationAirQualityInfoDTO(station, aq)).orElse(null);
        }).filter(Objects::nonNull).collect(Collectors.toList()); // null이 아닌 StationAirQualityInfo만 필터링하여 반환
    }
    //--------------------------------------------------------------------------------------------------------------------------------------


}
