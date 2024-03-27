package inhatc.k8sProject.fineDust.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import inhatc.k8sProject.fineDust.domain.StationInfo;
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

@Service
@RequiredArgsConstructor
public class StationInfoService {

    private final StationInfoRepository stationInfoRepository;
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 파싱을 위해 추가
    private static final Logger log = LoggerFactory.getLogger(StationInfoService.class);

    @Value("${stationinfo.serviceKey}")
    private String serviceKey;

    @Transactional
    public String fetchAndSaveStationInfo(String address) {
        try {
            String encodedServiceKey = URLEncoder.encode(serviceKey, "UTF-8");
            String requestUrl = String.format("https://apis.data.go.kr/B552584/MsrstnInfoInqireSvc/getMsrstnList?serviceKey=%s&returnType=json&numOfRows=100&pageNo=1&addr=%s",
                    encodedServiceKey, URLEncoder.encode(address, "UTF-8"));

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
                StationInfo stationInfo = parseStationInfoData(item);
                stationInfoRepository.save(stationInfo);
            }

            return "Success";
        } catch (IOException e) {
            log.error("Failed to fetch and save station info", e);
            return "Failure";
        }
    }

    private StationInfo parseStationInfoData(JsonNode item) {
        StationInfo stationInfo = new StationInfo();

        stationInfo.setStationName(item.path("stationName").asText(null)); // 측정소 이름
//        stationInfo.setYear(item.path("year").asInt(0)); // 설립 년도, 기본값 0
        stationInfo.setAddr(item.path("addr").asText(null)); // 주소
//        stationInfo.setItem(item.path("item").asText(null)); // 측정 항목
//        stationInfo.setMangName(item.path("mangName").asText(null)); // 관리 기관명
        stationInfo.setDmX(item.path("dmX").asDouble()); // X 좌표
        stationInfo.setDmY(item.path("dmY").asDouble()); // Y 좌표

        return stationInfo;
    }

}
