package inhatc.k8sProject.fineDust.controller;

import inhatc.k8sProject.fineDust.dto.StationAirQualityInfoDTO;
import inhatc.k8sProject.fineDust.service.chungcheong.ChungcheongStationInfoService;
import inhatc.k8sProject.fineDust.service.gangwon.GangwonStationInfoService;
import inhatc.k8sProject.fineDust.service.gyeonggi.GyeonggiStationInfoService;
import inhatc.k8sProject.fineDust.service.gyeongsang.GyeongsangStationInfoService;
import inhatc.k8sProject.fineDust.service.jeju.JejuStationInfoService;
import inhatc.k8sProject.fineDust.service.jeolla.JeollaStationInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller // 이 클래스를 스프링 MVC 컨트롤러로 등록
@RequiredArgsConstructor // 롬복 라이브러리를 사용하여 생성자 주입 방식을 자동으로 처리
public class MapController {

    private final GyeonggiStationInfoService gyeonggiStationInfoService; // 경기도 지역 측정소 정보 서비스
    private final GangwonStationInfoService gangwonStationInfoService;
    private final ChungcheongStationInfoService chungcheongStationInfoService;
    private final GyeongsangStationInfoService gyeongsangStationInfoService;
    private final JejuStationInfoService jejuStationInfoService;
    private final JeollaStationInfoService jeollaStationInfoService;

    @GetMapping("/map")
    public ModelAndView map() {
        ModelAndView modelAndView = new ModelAndView("map");
        // 경기 지역 최근 측정소 데이터를 뷰에 전달
        modelAndView.addObject("gyeonggiStations", gyeonggiStationInfoService.findRecentGyeonggiStationsData());
        // 강원 지역 최근 측정소 데이터를 뷰에 전달
        modelAndView.addObject("gangwonStations", gangwonStationInfoService.findRecentGangownStationsData());
        // 충청 지역 최근 측정소 데이터를 뷰에 전달
        modelAndView.addObject("chungcheongStations", chungcheongStationInfoService.findRecentChungcheongStationsData());
        // 경상 지역 최근 측정소 데이터를 뷰에 전달
        modelAndView.addObject("gyeongsangStations", gyeongsangStationInfoService.findRecentGyeongsangStationsData());
        // 제주 지역 최근 측정소 데이터를 뷰에 전달
        modelAndView.addObject("jejuStations", jejuStationInfoService.findRecentJejuStationsData());
        // 전라 지역 최근 측정소 데이터를 뷰에 전달
        modelAndView.addObject("jeollaStations", jeollaStationInfoService.findRecentJeollaStationsData());

        return modelAndView; // map 뷰 반환
    }
    //--------------------------------------------------------------------------------------------------------------------------------------

    // 최근 측정된 공기질 정보를 반환하는 API 엔드포인트 정의
    @GetMapping("/stations/recent/gyeonggi")
    @ResponseBody // 응답 본문에 직접 결과를 반환
    public ResponseEntity<List<StationAirQualityInfoDTO>> getGyeonggiRecentStationsWithAirQuality() {
        // 경기도 지역 측정소에서 최근에 측정한 공기질 정보를 가져오는 서비스 메소드 호출
        List<StationAirQualityInfoDTO> gyeonggiStations = gyeonggiStationInfoService.findRecentGyeonggiStationsWithAirQuality();
        return ResponseEntity.ok(gyeonggiStations);
    }
    //--------------------------------------------------------------------------------------------------------------------------------------

    @GetMapping("/stations/recent/gangwon")
    @ResponseBody // 응답 본문에 직접 결과를 반환
    public ResponseEntity<List<StationAirQualityInfoDTO>> getGangwonRecentStationsWithAirQuality() {
        // 경기도 지역 측정소에서 최근에 측정한 공기질 정보를 가져오는 서비스 메소드 호출
        List<StationAirQualityInfoDTO> gangwonStations = gangwonStationInfoService.findRecentGangwonStationsWithAirQuality();
        return ResponseEntity.ok(gangwonStations);
    }
    //--------------------------------------------------------------------------------------------------------------------------------------
    @GetMapping("/stations/recent/chungcheong")
    @ResponseBody // 응답 본문에 직접 결과를 반환
    public ResponseEntity<List<StationAirQualityInfoDTO>> getChungcheongRecentStationsWithAirQuality() {
        // 경기도 지역 측정소에서 최근에 측정한 공기질 정보를 가져오는 서비스 메소드 호출
        List<StationAirQualityInfoDTO> chungcheongStations = chungcheongStationInfoService.findRecentChungcheongStationsWithAirQuality();
        return ResponseEntity.ok(chungcheongStations);
    }
    //--------------------------------------------------------------------------------------------------------------------------------------
    @GetMapping("/stations/recent/gyeongsang")
    @ResponseBody // 응답 본문에 직접 결과를 반환
    public ResponseEntity<List<StationAirQualityInfoDTO>> getGyeongsangRecentStationsWithAirQuality() {
        // 경기도 지역 측정소에서 최근에 측정한 공기질 정보를 가져오는 서비스 메소드 호출
        List<StationAirQualityInfoDTO> gyeongsangStations = gyeongsangStationInfoService.findRecentGyeongsangStationsWithAirQuality();
        return ResponseEntity.ok(gyeongsangStations);
    }
    //--------------------------------------------------------------------------------------------------------------------------------------

    @GetMapping("/stations/recent/jeju")
    @ResponseBody // 응답 본문에 직접 결과를 반환
    public ResponseEntity<List<StationAirQualityInfoDTO>> getJejuRecentStationsWithAirQuality() {
        // 경기도 지역 측정소에서 최근에 측정한 공기질 정보를 가져오는 서비스 메소드 호출
        List<StationAirQualityInfoDTO> jejuStations = jejuStationInfoService.findRecentJejuStationsWithAirQuality();
        return ResponseEntity.ok(jejuStations);
    }
    //--------------------------------------------------------------------------------------------------------------------------------------
    @GetMapping("/stations/recent/jeolla")
    @ResponseBody // 응답 본문에 직접 결과를 반환
    public ResponseEntity<List<StationAirQualityInfoDTO>> getJeollaRecentStationsWithAirQuality() {
        // 경기도 지역 측정소에서 최근에 측정한 공기질 정보를 가져오는 서비스 메소드 호출
        List<StationAirQualityInfoDTO> jeollaStations = jeollaStationInfoService.findRecentJeollaStationsWithAirQuality();
        return ResponseEntity.ok(jeollaStations);
    }
    //--------------------------------------------------------------------------------------------------------------------------------------
}
