package inhatc.k8sProject.fineDust.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import inhatc.k8sProject.fineDust.domain.AirQuality;
import inhatc.k8sProject.fineDust.domain.StationInfo;
import inhatc.k8sProject.fineDust.repository.AirQualityRepository;
import inhatc.k8sProject.fineDust.repository.StationInfoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class AirQualityService {

    private final AirQualityRepository airQualityRepository;
    private final StationInfoRepository stationInfoRepository;
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 파싱을 위해 추가
    private static final Logger log = LoggerFactory.getLogger(AirQualityService.class);

    @Value("${airquality.serviceKey}")
    private String serviceKey;

    @Transactional
    public String fetchAndSaveAirQualityData(String sidoName) {
        try {
            String encodedServiceKey = URLEncoder.encode(serviceKey, "UTF-8");
            String requestUrl = String.format("https://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty?serviceKey=%s&returnType=json&numOfRows=100&pageNo=1&sidoName=%s&ver=1.0",
                    encodedServiceKey, URLEncoder.encode(sidoName, "UTF-8"));

            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            BufferedReader rd;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                responseBuilder.append(line);
            }
            String response = responseBuilder.toString();

            rd.close();
            conn.disconnect();

            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode items = rootNode.path("response").path("body").path("items");

            for (JsonNode item : items) {
                AirQuality airQuality = parseAirQualityData(item);
                airQualityRepository.save(airQuality);
            }

            return "Success";
        } catch (IOException e) {
            log.error("Failed to fetch and save air quality data", e);
            return "Failure";
        }
    }

    private AirQuality parseAirQualityData(JsonNode item) {
        AirQuality airQuality = new AirQuality();

        // 측정 시간 설정: API로부터 받은 시간을 파싱하여 LocalDateTime 객체로 변환하고, KST(한국 표준시)로 조정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(item.path("dataTime").asText(), formatter);
        LocalDateTime kstDateTime = dateTime.plusHours(9); // KST로 변환
        airQuality.setDataTime(kstDateTime);

        // 나머지 값들 설정: JSON 객체에서 각 대기 질 지표 값을 읽어와서 AirQuality 객체의 속성으로 설정
        airQuality.setStationName(item.path("stationName").asText(null)); // 측정소 이름
        airQuality.setSidoName(item.path("sidoName").asText(null)); // 지역 이름
        airQuality.setPm10Value(item.path("pm10Value").asDouble(0)); // PM10 값
        String stationName = item.path("stationName").asText(null);
        StationInfo stationInfo = stationInfoRepository.findByStationName(stationName)
                .orElseGet(() -> {
                    StationInfo newStation = new StationInfo();
                    newStation.setStationName(stationName);
                    // 필요한 경우 추가적인 StationInfo 속성 설정
                    return stationInfoRepository.save(newStation);
                });
        airQuality.setStationInfo(stationInfo);

        return airQuality; // 설정된 값을 담은 AirQuality 객체 반환

    }

}
