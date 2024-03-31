package inhatc.k8sProject.fineDust.service.gyeonggi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import inhatc.k8sProject.fineDust.domain.gyeonggi.GyeonggiStationInfo;
import inhatc.k8sProject.fineDust.repository.gyeonggi.GyeonggiStationInfoRepository;
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

@Service
@RequiredArgsConstructor
public class GyeonggiStationInfoService {

    private final GyeonggiStationInfoRepository gyeonggiStationInfoRepository;
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 파싱을 위해 추가
    private static final Logger log = LoggerFactory.getLogger(GyeonggiStationInfoService.class);

    @Value("${service.key}")
    private String serviceKey;


//    @Scheduled(fixedRate = 60000)
    public void updateStationInfoDataAutomatically() {
        String sidoName = "서울";
        fetchAndSaveStationInfo(sidoName);
    }

    @Transactional("gyeonggiTransactionManager")
    public String fetchAndSaveStationInfo(String address) {
        try {
            StringBuilder requestUrlBuilder = new StringBuilder("https://apis.data.go.kr/B552584/MsrstnInfoInqireSvc/getMsrstnList?");
            requestUrlBuilder.append("&addr=").append(URLEncoder.encode(address, "UTF-8"));
            requestUrlBuilder.append("&pageNo=").append(URLEncoder.encode("1", "UTF-8"));
            requestUrlBuilder.append("&numOfRows=").append(URLEncoder.encode("100", "UTF-8"));
            requestUrlBuilder.append("&serviceKey=").append(URLEncoder.encode(serviceKey, "UTF-8")); // Replace "서비스키" with your actual service key
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
                GyeonggiStationInfo gyeonggiStationInfo = parseStationInfoData(item);
                gyeonggiStationInfoRepository.save(gyeonggiStationInfo);
            }

            return "Success";
        } catch (IOException e) {
            log.error("Failed to fetch and save station info", e);
            return "Failure";
        }
    }

    private GyeonggiStationInfo parseStationInfoData(JsonNode item) {
        GyeonggiStationInfo gyeonggiStationInfo = new GyeonggiStationInfo();
        gyeonggiStationInfo.setStationName(item.path("stationName").asText(null)); // 측정소 이름
        gyeonggiStationInfo.setAddr(item.path("addr").asText(null)); // 주소
        gyeonggiStationInfo.setDmX(item.path("dmX").asDouble()); // X 좌표
        gyeonggiStationInfo.setDmY(item.path("dmY").asDouble()); // Y 좌표
        return gyeonggiStationInfo;
    }

}
