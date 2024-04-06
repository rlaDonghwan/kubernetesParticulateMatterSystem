package inhatc.k8sProject.fineDust.service.chungcheong;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import inhatc.k8sProject.fineDust.domain.chungcheong.ChungcheongStationInfo;
import inhatc.k8sProject.fineDust.domain.gyeongsang.GyeongsangStationInfo;
import inhatc.k8sProject.fineDust.repository.chungcheong.ChungcheongStationInfoRepository;
import inhatc.k8sProject.fineDust.repository.gyeongsang.GyeongsangStationInfoRepository;
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
public class ChungcheongStationInfoService {

    private final ChungcheongStationInfoRepository chungcheongStationInfoRepository;
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 파싱을 위한 ObjectMapper
    private static final Logger log = LoggerFactory.getLogger(GyeongsangStationInfoRepository.class);

    @Value("${service.key}")
    private String serviceKey;

    // 스케줄링된 작업: 주석 처리하여 현재는 스케줄링이 비활성화되어 있음
    @Scheduled(fixedRate = 1800000)
    public void updateAirQualityDataAutomatically() {
        // 스케줄링된 작업: 일정 간격으로 대기 질 데이터를 업데이트하는 메소드
        List<String> sidoList = Arrays.asList("충남", "충북", "세종", "대전");
        sidoList.forEach(this::fetchAndSaveChungcheongStationInfo);
    }

    // 대기 측정소 정보를 가져와 저장하는 메서드
    @Transactional("chungcheongTransactionManager")
    public String fetchAndSaveChungcheongStationInfo(String address) {
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
            if (response.trim().startsWith("<")) {
                log.error("예상하지 않은 JSON 형식의 응답입니다. 응답: " + response);
                return "예상치 않은 응답 형식으로 실패";
            }

            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode items = rootNode.path("response").path("body").path("items");

            for (JsonNode item : items) {
                ChungcheongStationInfo chungcheongStationInfo = parseStationInfoData(item);
                chungcheongStationInfoRepository.save(chungcheongStationInfo);
            }

            return "성공";
        } catch (IOException e) {
            log.error("측정소 정보를 가져오고 저장하는 데 실패했습니다", e);
            return "실패";
        }
    }

    // JSON 데이터를 파싱하여 GyeongsangStationInfo 객체로 변환하는 메서드
    private ChungcheongStationInfo parseStationInfoData(JsonNode item) {
        ChungcheongStationInfo chungcheongStationInfo = new ChungcheongStationInfo();
        chungcheongStationInfo.setStationName(item.path("stationName").asText(null)); // 측정소 이름 설정
        chungcheongStationInfo.setAddr(item.path("addr").asText(null)); // 주소 설정
        chungcheongStationInfo.setDmX(item.path("dmX").asDouble()); // X 좌표 설정
        chungcheongStationInfo.setDmY(item.path("dmY").asDouble()); // Y 좌표 설정
        return chungcheongStationInfo;
    }

}
