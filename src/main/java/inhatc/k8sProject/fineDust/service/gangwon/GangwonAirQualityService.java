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
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 파싱을 위한 ObjectMapper
    private static final Logger log = LoggerFactory.getLogger(GangwonAirQualityService.class);

    @Value("${service.key}") // 애플리케이션 속성 파일에서 가져온 값
    private String serviceKey;

    // 일정 시간마다 대기 질 데이터를 업데이트하는 예약된 작업
    @Scheduled(fixedRate = 1800000) // 30분마다
    public void updateAirQualityDataAutomatically() {
        String sidoName = "강원"; // 대상 지역 이름
        fetchAndSaveGangwonAirQualityData(sidoName); // 해당 지역의 대기 질 데이터 가져와 저장
    }

    // 대기 질 데이터를 가져와 저장하는 메서드
    @Transactional("gangwonTransactionManager")
    public String fetchAndSaveGangwonAirQualityData(String sidoName) {
        try {
            // API 요청을 위한 URL 구성
            StringBuilder requestUrlBuilder = new StringBuilder("https://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty?");
            requestUrlBuilder.append("sidoName=").append(URLEncoder.encode(sidoName, "UTF-8"));
            requestUrlBuilder.append("&pageNo=").append(URLEncoder.encode("1", "UTF-8"));
            requestUrlBuilder.append("&numOfRows=").append(URLEncoder.encode("100", "UTF-8"));
            requestUrlBuilder.append("&returnType=").append(URLEncoder.encode("json", "UTF-8"));
            requestUrlBuilder.append("&serviceKey=").append(serviceKey); // 서비스 키 추가
            requestUrlBuilder.append("&ver=").append(URLEncoder.encode("1.0", "UTF-8"));

            URL url = new URL(requestUrlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            // HTTP 응답 코드 확인
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                log.error("HTTP 오류 코드 : " + conn.getResponseCode());
                return "HTTP 오류로 실패";
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
                log.error("예상하지 않은 JSON 형식의 응답입니다. 응답: " + response);
                return "예상치 않은 응답 형식으로 실패";
            }

            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode items = rootNode.path("response").path("body").path("items");

            // JSON 데이터 파싱 및 저장
            items.forEach(item -> {
                GangwonAirQuality gangwonAirQuality = parseAirQualityData(item);
                gangwonAirQualityRepository.save(gangwonAirQuality);
            });

            return "성공";
        } catch (IOException e) {
            log.error("대기 질 데이터를 가져오고 저장하는 데 실패했습니다", e);
            return "실패";
        }
    }

    // JSON 데이터를 파싱하여 GangwonAirQuality 객체로 변환하는 메서드
    private GangwonAirQuality parseAirQualityData(JsonNode item) {
        GangwonAirQuality gangwonAirQuality = new GangwonAirQuality();

        // 측정 시간을 파싱하고 KST(Korean Standard Time)로 조정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String dateTimeStr = item.path("dataTime").asText(null);
        LocalDateTime dateTime = null;
        if (dateTimeStr != null) {
            try {
                dateTime = LocalDateTime.parse(dateTimeStr, formatter);
            } catch (DateTimeParseException e) {
                log.error("날짜 시간을 구문 분석하지 못했습니다: " + dateTimeStr, e);
                // 오류 처리 또는 기본 날짜 시간 설정
                dateTime = LocalDateTime.now(); // 예시 기본값, 필요에 따라 조정
            }
        } else {
            // null 경우 처리 또는 기본 날짜 시간 설정
            dateTime = LocalDateTime.now(); // 예시 기본값, 필요에 따라 조정
        }
        LocalDateTime kstDateTime = dateTime.plusHours(9); // KST로 변환
        gangwonAirQuality.setDataTime(kstDateTime); // 측정 시간 설정

        gangwonAirQuality.setStationName(item.path("stationName").asText(null)); // 측정소 이름 설정
        gangwonAirQuality.setSidoName(item.path("sidoName").asText(null)); // 지역 이름 설정
        gangwonAirQuality.setPm10Value(item.path("pm10Value").asDouble(0)); // PM10 값 설정
        gangwonAirQuality.setPm25Value(item.path("pm25Value").asDouble(0)); // PM2.5 값 설정

        return gangwonAirQuality;
    }
}
