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
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
@RequiredArgsConstructor
public class GangwonAirQualityService {

    private final GangwonAirQualityRepository gangwonAirQualityRepository;
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 파싱을 위한 ObjectMapper
    private static final Logger log = LoggerFactory.getLogger(GangwonAirQualityService.class);

    //프로퍼티에서 API 키 값을 받아오는 어노테이션
    @Value("${service.key}") // 애플리케이션 속성 파일에서 가져온 값
    private String serviceKey;

    @Scheduled(cron = "0 20 * * * *")
    public void updateAirQualityDataAutomatically() {
        String sidoName = "강원"; // 대상 지역 이름
        fetchAndSaveGangwonAirQualityData(sidoName); // 해당 지역의 대기 질 데이터 가져와 저장
    }
    //--------------------------------------------------------------------------------------------------------------------------------------

    // 강원도 대기 질 데이터를 가져와 저장하는 메서드
    @Transactional("gangwonTransactionManager")
    public void fetchAndSaveGangwonAirQualityData(String sidoName) {
        try {
            // API 요청을 위한 URL 구성
            StringBuilder requestUrlBuilder = new StringBuilder("https://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty?");
            requestUrlBuilder.append("sidoName=").append(URLEncoder.encode(sidoName, StandardCharsets.UTF_8));
            requestUrlBuilder.append("&pageNo=").append(URLEncoder.encode("1", StandardCharsets.UTF_8));
            requestUrlBuilder.append("&numOfRows=").append(URLEncoder.encode("200", StandardCharsets.UTF_8));
            requestUrlBuilder.append("&returnType=").append(URLEncoder.encode("json", StandardCharsets.UTF_8));
            requestUrlBuilder.append("&serviceKey=").append(serviceKey); // 서비스 키 추가
            requestUrlBuilder.append("&ver=").append(URLEncoder.encode("1.0", StandardCharsets.UTF_8));

            // URL 객체 생성
            URL url = new URL(requestUrlBuilder.toString());
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
            if (!response.trim().startsWith("{")) {
                log.error("예상하지 않은 JSON 형식의 응답입니다. 응답: " + response);
            }

            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode items = rootNode.path("response").path("body").path("items");

            // JSON 데이터 파싱 및 저장
            items.forEach(item -> {
                GangwonAirQuality gangwonAirQuality = parseAirQualityData(item);
                gangwonAirQualityRepository.save(gangwonAirQuality);
            });

        } catch (IOException e) {
            log.error("대기 질 데이터를 가져오고 저장하는 데 실패했습니다", e);
        }
    }
    //--------------------------------------------------------------------------------------------------------------------------------------

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
    //--------------------------------------------------------------------------------------------------------------------------------------
}
