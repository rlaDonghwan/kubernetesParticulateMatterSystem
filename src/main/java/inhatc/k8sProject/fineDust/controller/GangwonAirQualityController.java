package inhatc.k8sProject.fineDust.controller;

import inhatc.k8sProject.fineDust.service.gangwon.GangwonAirQualityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/air-quality")
public class GangwonAirQualityController {

    private final GangwonAirQualityService gangwonAirQualityService;

    @GetMapping("/gangwon")
    public ResponseEntity<String> updateAirQualityData(
            @RequestParam(defaultValue = "강원") String sidoNames) {
        List<String> sidoList = Arrays.asList(sidoNames.split(",")); //쉼표 공백제거

        StringBuilder resultBuilder = new StringBuilder();

        for (String sidoName : sidoList) { //리스트의 있는 값을 sidoName의 저장
            String result = gangwonAirQualityService.fetchAndSaveGangwonAirQualityData(sidoName.trim()); // 공백 제거 후 저장
            resultBuilder.append(sidoName).append(": ").append(result).append("; ");
        }

        // 모든 지역에 대한 처리 결과를 문자열로 반환
        return ResponseEntity.ok(resultBuilder.toString());
    }
}