package inhatc.k8sProject.fineDust.controller;

import inhatc.k8sProject.fineDust.domain.gangwon.GangwonStationInfo;
import inhatc.k8sProject.fineDust.repository.gangwon.GangwonAirQualityRepository;
import inhatc.k8sProject.fineDust.service.gangwon.GangwonAirQualityService;
import inhatc.k8sProject.fineDust.service.gangwon.GangwonStationInfoService;
import inhatc.k8sProject.fineDust.service.gyeonggi.GyeonggiStationInfoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/stations")
@AllArgsConstructor
public class GangwonStationInfoController {
    private final GangwonStationInfoService gangwonStationInfoService;

    @GetMapping("/gangwon")
    public ResponseEntity<String> updateStationInfoData(
            @RequestParam(defaultValue = "강원") String stationNames) {

        List<String> stationList = Arrays.asList(stationNames.split(",")); //쉼표 공백제거

        StringBuilder resultBuilder = new StringBuilder();

        for (String stationName : stationList) { //리스트의 있는 값을 sidoName의 저장
            String result = gangwonStationInfoService.fetchAndSaveStationInfo(stationName.trim()); // 공백 제거 후 저장
            resultBuilder.append(stationName).append(": ").append(result).append("; ");
        }

        // 모든 지역에 대한 처리 결과를 문자열로 반환
        return ResponseEntity.ok(resultBuilder.toString());
    }
}