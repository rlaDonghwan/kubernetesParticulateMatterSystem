package inhatc.k8sProject.fineDust.service.jeolla;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import inhatc.k8sProject.fineDust.domain.jeolla.JeollaStationInfo;
import inhatc.k8sProject.fineDust.repository.gyeongsang.GyeongsangStationInfoRepository;
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

@Service
@RequiredArgsConstructor
public class JeollaStationInfoService {

    private final JeollaStationInfoRepository jeollaStationInfoRepository;
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 파싱을 위해 추가
    private static final Logger log = LoggerFactory.getLogger(GyeongsangStationInfoRepository.class);

    @Value("${service.key}")
    private String serviceKey;


    @Scheduled(cron = "0 10 * * * *") // 매 시간의 10분에 실행
    public void updateAirQualityDataAutomatically() {
        // 스케줄링된 작업: 일정 간격으로 대기 질 데이터를 업데이트하는 메소드
        List<String> sidoList = Arrays.asList("전북", "전남", "광주");
        sidoList.forEach(this::fetchAndSaveJeollaStationInfo);
    }

    @Transactional("jeollaTransactionManager")
    public String fetchAndSaveJeollaStationInfo(String address) {
        try {
            StringBuilder requestUrlBuilder = new StringBuilder("https://apis.data.go.kr/B552584/MsrstnInfoInqireSvc/getMsrstnList?");
            requestUrlBuilder.append("&addr=").append(URLEncoder.encode(address, "UTF-8"));
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

}
