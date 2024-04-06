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
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GyeonggiStationInfoService {

    private final GyeonggiStationInfoRepository gyeonggiStationInfoRepository;
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 파싱을 위한 ObjectMapper
    private static final Logger log = LoggerFactory.getLogger(GyeonggiStationInfoService.class);

    @Value("${service.key}") // 애플리케이션 속성 파일에서 가져온 값
    private String serviceKey;


    @Scheduled(fixedRate = 1800000)
    public void updateAirQualityDataAutomatically() {
        List<String> sidoList = Arrays.asList("서울", "경기", "인천");
        sidoList.forEach(this::fetchAndSaveGyeonggiStationInfo);
    }

    @Transactional("gyeonggiTransactionManager")
    public String fetchAndSaveGyeonggiStationInfo(String sidoName) {
        try {
            // API 요청을 위한 URL 구성
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
                log.error("예상하지 않은 JSON 형식의 응답입니다. 응답: " + response);
                return "예상치 않은 응답 형식으로 실패";
            }

            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode items = rootNode.path("response").path("body").path("items");

            for (JsonNode item : items) {
                GyeonggiStationInfo gyeonggiStationInfo = parseStationInfoData(item);
                gyeonggiStationInfoRepository.save(gyeonggiStationInfo);
            }

            return "성공";
        } catch (IOException e) {
            log.error("측정소 정보를 가져오고 저장하는 데 실패했습니다", e);
            return "실패";
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
