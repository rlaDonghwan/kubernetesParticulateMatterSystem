package inhatc.k8sProject.fineDust.dto;

import inhatc.k8sProject.fineDust.domain.chungcheong.ChungcheongAirQuality;
import inhatc.k8sProject.fineDust.domain.chungcheong.ChungcheongStationInfo;
import inhatc.k8sProject.fineDust.domain.gangwon.GangwonAirQuality;
import inhatc.k8sProject.fineDust.domain.gangwon.GangwonStationInfo;
import inhatc.k8sProject.fineDust.domain.gyeonggi.GyeonggiAirQuality;
import inhatc.k8sProject.fineDust.domain.gyeonggi.GyeonggiStationInfo;
import inhatc.k8sProject.fineDust.domain.gyeongsang.GyeongsangAirQuality;
import inhatc.k8sProject.fineDust.domain.gyeongsang.GyeongsangStationInfo;
import inhatc.k8sProject.fineDust.domain.jeju.JejuAirQuality;
import inhatc.k8sProject.fineDust.domain.jeju.JejuStationInfo;
import inhatc.k8sProject.fineDust.domain.jeolla.JeollaAirQuality;
import inhatc.k8sProject.fineDust.domain.jeolla.JeollaStationInfo;
import lombok.Data;

@Data
public class StationAirQualityInfoDTO {
    private GyeonggiStationInfo gyeonggiStationInfo;
    private GyeonggiAirQuality gyeonggiAirQuality;

    private GangwonAirQuality gangwonAirQuality;
    private GangwonStationInfo gangwonStationInfo;

    private ChungcheongAirQuality chungcheongAirQuality;
    private ChungcheongStationInfo chungcheongStationInfo;

    private GyeongsangStationInfo gyeongsangStationInfo;
    private GyeongsangAirQuality gyeongsangAirQuality;

    private JejuStationInfo jejuStationInfo;
    private JejuAirQuality jejuAirQuality;

    private JeollaStationInfo jeollaStationInfo;;
    private JeollaAirQuality jeollaAirQuality;

    public StationAirQualityInfoDTO(GyeonggiStationInfo gyeonggiStationInfo, GyeonggiAirQuality gyeonggiAirQuality) {
        this.gyeonggiStationInfo = gyeonggiStationInfo;
        this.gyeonggiAirQuality = gyeonggiAirQuality;
    }


    public StationAirQualityInfoDTO(GangwonStationInfo gangwonStationInfo, GangwonAirQuality gangwonAirQuality) {
        this.gangwonStationInfo = gangwonStationInfo;
        this.gangwonAirQuality = gangwonAirQuality;
    }

    public StationAirQualityInfoDTO(ChungcheongStationInfo chungcheongStationInfo, ChungcheongAirQuality chungcheongAirQuality) {
        this.chungcheongStationInfo = chungcheongStationInfo;
        this.chungcheongAirQuality = chungcheongAirQuality;
    }


    public StationAirQualityInfoDTO(GyeongsangStationInfo gyeongsangStationInfo, GyeongsangAirQuality gyeongsangAirQuality) {
        this.gyeongsangStationInfo = gyeongsangStationInfo;
        this.gyeongsangAirQuality = gyeongsangAirQuality;
    }

    public StationAirQualityInfoDTO(JejuStationInfo jejuStationInfo, JejuAirQuality jejuAirQuality) {
        this.jejuStationInfo = jejuStationInfo;
        this.jejuAirQuality = jejuAirQuality;
    }

    public StationAirQualityInfoDTO(JeollaStationInfo jeollaStationInfo, JeollaAirQuality jeollaAirQuality) {
        this.jeollaStationInfo = jeollaStationInfo;
        this.jeollaAirQuality = jeollaAirQuality;
    }
}