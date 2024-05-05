package inhatc.k8sProject.fineDust.dto;

import inhatc.k8sProject.fineDust.domain.gangwon.GangwonAirQuality;
import inhatc.k8sProject.fineDust.domain.gangwon.GangwonStationInfo;
import inhatc.k8sProject.fineDust.domain.gyeonggi.GyeonggiAirQuality;
import inhatc.k8sProject.fineDust.domain.gyeonggi.GyeonggiStationInfo;
import lombok.Data;

@Data
public class StationAirQualityInfoDTO {
    private GyeonggiStationInfo gyeonggiStationInfo;
    private GyeonggiAirQuality gyeonggiAirQuality;

    private GangwonAirQuality gangwonAirQuality;
    private GangwonStationInfo gangwonStationInfo;

    public StationAirQualityInfoDTO(GyeonggiStationInfo gyeonggiStationInfo, GyeonggiAirQuality gyeonggiAirQuality) {
        this.gyeonggiStationInfo = gyeonggiStationInfo;
        this.gyeonggiAirQuality = gyeonggiAirQuality;
    }


    public StationAirQualityInfoDTO(GangwonStationInfo gangwonStationInfo, GangwonAirQuality gangwonAirQuality) {
        this.gangwonStationInfo = gangwonStationInfo;
        this.gangwonAirQuality = gangwonAirQuality;
    }
}