package inhatc.k8sProject.particulateMatter.dto;

import inhatc.k8sProject.particulateMatter.domain.chungcheong.ChungcheongAirQuality;
import inhatc.k8sProject.particulateMatter.domain.chungcheong.ChungcheongStationInfo;
import inhatc.k8sProject.particulateMatter.domain.gangwon.GangwonAirQuality;
import inhatc.k8sProject.particulateMatter.domain.gangwon.GangwonStationInfo;
import inhatc.k8sProject.particulateMatter.domain.gyeonggi.GyeonggiAirQuality;
import inhatc.k8sProject.particulateMatter.domain.gyeonggi.GyeonggiStationInfo;
import inhatc.k8sProject.particulateMatter.domain.gyeongsang.GyeongsangAirQuality;
import inhatc.k8sProject.particulateMatter.domain.gyeongsang.GyeongsangStationInfo;
import inhatc.k8sProject.particulateMatter.domain.jeju.JejuAirQuality;
import inhatc.k8sProject.particulateMatter.domain.jeju.JejuStationInfo;
import inhatc.k8sProject.particulateMatter.domain.jeolla.JeollaAirQuality;
import inhatc.k8sProject.particulateMatter.domain.jeolla.JeollaStationInfo;
import lombok.Data;

// @Data 어노테이션은 Lombok을 사용하여 getter, setter, toString, equals, hashCode 등의 메서드를 자동 생성합니다.
@Data
public class StationAirQualityInfoDTO {
    // 경기도 측정소 정보와 대기질 정보를 담을 필드
    private GyeonggiStationInfo gyeonggiStationInfo;
    private GyeonggiAirQuality gyeonggiAirQuality;

    // 강원도 측정소 정보와 대기질 정보를 담을 필드
    private GangwonAirQuality gangwonAirQuality;
    private GangwonStationInfo gangwonStationInfo;

    // 충청도 측정소 정보와 대기질 정보를 담을 필드
    private ChungcheongAirQuality chungcheongAirQuality;
    private ChungcheongStationInfo chungcheongStationInfo;

    // 경상도 측정소 정보와 대기질 정보를 담을 필드
    private GyeongsangStationInfo gyeongsangStationInfo;
    private GyeongsangAirQuality gyeongsangAirQuality;

    // 제주도 측정소 정보와 대기질 정보를 담을 필드
    private JejuStationInfo jejuStationInfo;
    private JejuAirQuality jejuAirQuality;

    // 전라도 측정소 정보와 대기질 정보를 담을 필드
    private JeollaStationInfo jeollaStationInfo;
    private JeollaAirQuality jeollaAirQuality;

    // 경기도 측정소 정보와 대기질 정보를 초기화하는 생성자
    public StationAirQualityInfoDTO(GyeonggiStationInfo gyeonggiStationInfo, GyeonggiAirQuality gyeonggiAirQuality) {
        this.gyeonggiStationInfo = gyeonggiStationInfo;
        this.gyeonggiAirQuality = gyeonggiAirQuality;
    }

    // 강원도 측정소 정보와 대기질 정보를 초기화하는 생성자
    public StationAirQualityInfoDTO(GangwonStationInfo gangwonStationInfo, GangwonAirQuality gangwonAirQuality) {
        this.gangwonStationInfo = gangwonStationInfo;
        this.gangwonAirQuality = gangwonAirQuality;
    }

    // 충청도 측정소 정보와 대기질 정보를 초기화하는 생성자
    public StationAirQualityInfoDTO(ChungcheongStationInfo chungcheongStationInfo, ChungcheongAirQuality chungcheongAirQuality) {
        this.chungcheongStationInfo = chungcheongStationInfo;
        this.chungcheongAirQuality = chungcheongAirQuality;
    }

    // 경상도 측정소 정보와 대기질 정보를 초기화하는 생성자
    public StationAirQualityInfoDTO(GyeongsangStationInfo gyeongsangStationInfo, GyeongsangAirQuality gyeongsangAirQuality) {
        this.gyeongsangStationInfo = gyeongsangStationInfo;
        this.gyeongsangAirQuality = gyeongsangAirQuality;
    }

    // 제주도 측정소 정보와 대기질 정보를 초기화하는 생성자
    public StationAirQualityInfoDTO(JejuStationInfo jejuStationInfo, JejuAirQuality jejuAirQuality) {
        this.jejuStationInfo = jejuStationInfo;
        this.jejuAirQuality = jejuAirQuality;
    }

    // 전라도 측정소 정보와 대기질 정보를 초기화하는 생성자
    public StationAirQualityInfoDTO(JeollaStationInfo jeollaStationInfo, JeollaAirQuality jeollaAirQuality) {
        this.jeollaStationInfo = jeollaStationInfo;
        this.jeollaAirQuality = jeollaAirQuality;
    }
}
