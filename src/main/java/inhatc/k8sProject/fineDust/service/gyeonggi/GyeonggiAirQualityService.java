package inhatc.k8sProject.fineDust.service.gyeonggi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import inhatc.k8sProject.fineDust.domain.gyeonggi.GyeonggiAirQuality;
import inhatc.k8sProject.fineDust.repository.gyeonggi.GyeonggiAirQualityRepository;
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
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class GyeonggiAirQualityService {

    private final GyeonggiAirQualityRepository gyeonggiAirQualityRepository;
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 파싱을 위해 추가
    private static final Logger log = LoggerFactory.getLogger(GyeonggiAirQualityService.class);

    @Value("${service.key}")
    private String serviceKey;
//    @Scheduled(fixedRate = 60000)//10000이 10분
    public void updateAirQualityDataAutomatically() {
        String sidoName = "서울";
        fetchAndSaveGyeonggiAirQualityData(sidoName);
    }

    @Transactional("gyeonggiTransactionManager")
    public String fetchAndSaveGyeonggiAirQualityData(String sidoName) {
        try {
            StringBuilder requestUrlBuilder = new StringBuilder("https://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty?");
            requestUrlBuilder.append("sidoName=").append(URLEncoder.encode(sidoName, "UTF-8"));
            requestUrlBuilder.append("&pageNo=").append(URLEncoder.encode("1", "UTF-8"));
            requestUrlBuilder.append("&numOfRows=").append(URLEncoder.encode("100", "UTF-8"));
            requestUrlBuilder.append("&returnType=").append(URLEncoder.encode("json", "UTF-8"));
            requestUrlBuilder.append("&serviceKey=").append(serviceKey); // 서비스키는 사용자의 실제 키로 변경해야 합니다.
            requestUrlBuilder.append("&ver=").append(URLEncoder.encode("1.0", "UTF-8"));

            URL url = new URL(requestUrlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            // HTTP 응답 코드 확인
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
            // 응답 형식 검증
            if (!response.trim().startsWith("{")) {
                log.error("Response is not in expected JSON format. Response: " + response);
                return "Failure due to unexpected response format";
            }

            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode items = rootNode.path("response").path("body").path("items");

            items.forEach(item -> {
                GyeonggiAirQuality gyeonggiAirQuality = parseAirQualityData(item);
                gyeonggiAirQualityRepository.save(gyeonggiAirQuality);
            });

            return "Success";
        } catch (IOException e) {
            log.error("Failed to fetch and save air quality data", e);
            return "Failure";
        }
    }


    private GyeonggiAirQuality parseAirQualityData(JsonNode item) {
        GyeonggiAirQuality gyeonggiAirQuality = new GyeonggiAirQuality();

        // 측정 시간 설정: API로부터 받은 시간을 파싱하여 LocalDateTime 객체로 변환하고, KST(한국 표준시)로 조정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(item.path("dataTime").asText(), formatter);
        LocalDateTime kstDateTime = dateTime.plusHours(9); // KST로 변환
        gyeonggiAirQuality.setDataTime(kstDateTime);

        // 나머지 값들 설정: JSON 객체에서 각 대기 질 지표 값을 읽어와서 AirQuality 객체의 속성으로 설정
        gyeonggiAirQuality.setStationName(item.path("stationName").asText(null)); // 측정소 이름
        gyeonggiAirQuality.setSidoName(item.path("sidoName").asText(null)); // 지역 이름
        gyeonggiAirQuality.setPm10Value(item.path("pm10Value").asDouble(0)); // PM10 값
        gyeonggiAirQuality.setPm25Value(item.path("pm25Value").asDouble(0)); // PM10 값
        return gyeonggiAirQuality; // 설정된 값을 담은 AirQuality 객체 반환

    }

}
