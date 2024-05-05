package inhatc.k8sProject.fineDust.service.jeju;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import inhatc.k8sProject.fineDust.domain.jeju.JejuStationInfo;
import inhatc.k8sProject.fineDust.repository.jeju.JejuStationInfoRepository;
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

@Service
@RequiredArgsConstructor
public class JejuStationInfoService {

    private final JejuStationInfoRepository jejuStationInfoRepository;
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 파싱을 위해 추가
    private static final Logger log = LoggerFactory.getLogger(JejuStationInfoRepository.class);

    @Value("${service.key}")
    private String serviceKey;

    // 주기적으로 측정소 정보 업데이트
    @Scheduled(cron = "0 10 * * * *") // 매 시간의 10분에 실행
    public void updateAirQualityDataAutomatically() {
        String sidoName = "제주"; // 제주도의 시도명
        fetchAndSaveJejuStationInfo(sidoName); // 제주도 측정소 정보 가져와 저장
    }

    // 측정소 정보를 가져와 저장하는 메서드
    @Transactional("jejuTransactionManager")
    public String fetchAndSaveJejuStationInfo(String address) {
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
            if (response.trim().startsWith("<")) {
                log.error("예상치 않은 JSON 형식의 응답입니다. 응답: " + response);
                return "예상치 않은 응답 형식으로 실패";
            }

            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode items = rootNode.path("response").path("body").path("items");

            for (JsonNode item : items) {
                JejuStationInfo jejuStationInfo = parseStationInfoData(item);
                jejuStationInfoRepository.save(jejuStationInfo);
            }

            return "성공";
        } catch (IOException e) {
            log.error("측정소 정보를 가져오고 저장하는 데 실패했습니다", e);
            return "실패";
        }
    }

    // JSON 데이터를 파싱하여 JejuStationInfo 객체로 변환하는 메서드
    private JejuStationInfo parseStationInfoData(JsonNode item) {
        JejuStationInfo jejuStationInfo = new JejuStationInfo();
        jejuStationInfo.setStationName(item.path("stationName").asText(null)); // 측정소 이름
        jejuStationInfo.setAddr(item.path("addr").asText(null)); // 주소
        jejuStationInfo.setDmX(item.path("dmX").asDouble()); // X 좌표
        jejuStationInfo.setDmY(item.path("dmY").asDouble()); // Y 좌표
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime kstDateTime = currentDateTime.plusHours(9); // KST로 변환
        jejuStationInfo.setInPutDataTime(kstDateTime);
        return jejuStationInfo;
    }

}
