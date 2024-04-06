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
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GyeonggiAirQualityService {

    private final GyeonggiAirQualityRepository gyeonggiAirQualityRepository;
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 파싱을 위한 ObjectMapper
    private static final Logger log = LoggerFactory.getLogger(GyeonggiAirQualityService.class);

    @Value("${service.key}") // 애플리케이션 속성 파일에서 가져온 값
    private String serviceKey;


    @Scheduled(fixedRate = 1800000)
    public void updateAirQualityDataAutomatically() {
        List<String> sidoList = Arrays.asList("서울", "경기", "인천");
        sidoList.forEach(this::fetchAndSaveGyeonggiAirQualityData);
    }

    // 주어진 지역의 대기 질 데이터를 가져와 저장하는 메서드
    @Transactional("gyeonggiTransactionManager")
    public String fetchAndSaveGyeonggiAirQualityData(String sidoName) {
        try {
            // 대기 질 데이터를 가져오기 위한 API 요청을 위한 URL 구성
            StringBuilder requestUrlBuilder = new StringBuilder("https://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty?");
            requestUrlBuilder.append("sidoName=").append(URLEncoder.encode(sidoName, "UTF-8"));
            requestUrlBuilder.append("&pageNo=").append(URLEncoder.encode("1", "UTF-8"));
            requestUrlBuilder.append("&numOfRows=").append(URLEncoder.encode("100", "UTF-8"));
            requestUrlBuilder.append("&returnType=").append(URLEncoder.encode("json", "UTF-8"));
            requestUrlBuilder.append("&serviceKey=").append(serviceKey); // 서비스 키 추가
            requestUrlBuilder.append("&ver=").append(URLEncoder.encode("1.0", "UTF-8"));

            URL url = new URL(requestUrlBuilder.toString()); // URL 객체 생성
            HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // 연결 열기
            conn.setRequestMethod("GET"); // 요청 메서드 설정
            conn.setRequestProperty("Content-type", "application/json"); // 컨텐츠 타입 설정

            // HTTP 응답 코드 확인
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                log.error("HTTP 오류 코드 : " + conn.getResponseCode());
                return "HTTP 오류로 실패";
            }

            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream())); // 응답 읽기
            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                responseBuilder.append(line);
            }
            rd.close();
            conn.disconnect();

            String response = responseBuilder.toString(); // 응답을 문자열로 변환

            // 응답 형식 검증
            if (!response.trim().startsWith("{")) {
                log.error("예상하지 않은 JSON 형식의 응답입니다. 응답: " + response);
                return "예상치 않은 응답 형식으로 실패";
            }

            JsonNode rootNode = objectMapper.readTree(response); // JSON 응답 파싱
            JsonNode items = rootNode.path("response").path("body").path("items"); // 관련 데이터 추출

            items.forEach(item -> {
                GyeonggiAirQuality gyeonggiAirQuality = parseAirQualityData(item); // 대기 질 데이터 파싱
                gyeonggiAirQualityRepository.save(gyeonggiAirQuality); // 파싱된 데이터 저장
            });

            return "성공"; // 성공 메시지 반환
        } catch (IOException e) {
            log.error("대기 질 데이터를 가져오고 저장하는 데 실패했습니다", e);
            return "실패"; // 실패 메시지 반환
        }
    }




    // JSON 노드에서 대기 질 데이터를 파싱하는 메서드
    private GyeonggiAirQuality parseAirQualityData(JsonNode item) {
        GyeonggiAirQuality gyeonggiAirQuality = new GyeonggiAirQuality();

        // 측정 시간을 파싱하고 KST(Korean Standard Time)로 조정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(item.path("dataTime").asText(), formatter);
        LocalDateTime kstDateTime = dateTime.plusHours(9); // KST로 변환
        gyeonggiAirQuality.setDataTime(kstDateTime); // 측정 시간 설정

        // JSON 객체에서 다른 값들 설정
        gyeonggiAirQuality.setStationName(item.path("stationName").asText(null)); // 측정소 이름 설정
        gyeonggiAirQuality.setSidoName(item.path("sidoName").asText(null)); // 지역 이름 설정
        gyeonggiAirQuality.setPm10Value(item.path("pm10Value").asDouble(0)); // PM10 값 설정
        gyeonggiAirQuality.setPm25Value(item.path("pm25Value").asDouble(0)); // PM2.5 값 설정

        return gyeonggiAirQuality; // 파싱된 값을 담은 GyeonggiAirQuality 객체 반환
    }
}




















