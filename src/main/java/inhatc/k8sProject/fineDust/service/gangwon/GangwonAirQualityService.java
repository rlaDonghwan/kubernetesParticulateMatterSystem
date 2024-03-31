package inhatc.k8sProject.fineDust.service.gangwon;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import inhatc.k8sProject.fineDust.domain.gangwon.GangwonAirQuality;
import inhatc.k8sProject.fineDust.repository.gangwon.GangwonAirQualityRepository;
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
import java.time.format.DateTimeParseException;

@Service
@RequiredArgsConstructor
public class GangwonAirQualityService {

    private final GangwonAirQualityRepository gangwonAirQualityRepository;
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 파싱을 위해 추가
    private static final Logger log = LoggerFactory.getLogger(GangwonAirQualityService.class);

    @Value("${service.key}")
    private String serviceKey;


//    @Scheduled(fixedRate = 60000)//10000이 10분
    public void updateAirQualityDataAutomatically() {
        String sidoName = "강원";
        fetchAndSaveGangwonAirQualityData(sidoName);
    }

    @Transactional("gangwonTransactionManager")
    public String fetchAndSaveGangwonAirQualityData(String sidoName) {
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
                GangwonAirQuality gangwonAirQuality = parseAirQualityData(item);
                gangwonAirQualityRepository.save(gangwonAirQuality);
            });

            return "Success";
        } catch (IOException e) {
            log.error("Failed to fetch and save air quality data", e);
            return "Failure";
        }
    }


    private GangwonAirQuality parseAirQualityData(JsonNode item) {
        GangwonAirQuality gangwonAirQuality = new GangwonAirQuality();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String dateTimeStr = item.path("dataTime").asText(null);
        LocalDateTime dateTime = null;
        if (dateTimeStr != null) {
            try {
                dateTime = LocalDateTime.parse(dateTimeStr, formatter);
            } catch (DateTimeParseException e) {
                log.error("Failed to parse date time: " + dateTimeStr, e);
                // Handle the error or set a default date-time
                dateTime = LocalDateTime.now(); // Example default, adjust as necessary
            }
        } else {
            // Handle null case or set a default date-time
            dateTime = LocalDateTime.now(); // Example default, adjust as necessary
        }
        LocalDateTime kstDateTime = dateTime.plusHours(9); // Adjust for KST if needed
        gangwonAirQuality.setDataTime(kstDateTime);
        gangwonAirQuality.setStationName(item.path("stationName").asText(null)); // 측정소 이름
        gangwonAirQuality.setSidoName(item.path("sidoName").asText(null)); // 지역 이름
        gangwonAirQuality.setPm10Value(item.path("pm10Value").asDouble(0)); // PM10 값
        gangwonAirQuality.setPm25Value(item.path("pm25Value").asDouble(0)); // PM10 값
        return gangwonAirQuality;
    }


}
