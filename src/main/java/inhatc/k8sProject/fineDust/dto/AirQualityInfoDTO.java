package inhatc.k8sProject.fineDust.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter

public class AirQualityInfoDTO {
    private LocalDateTime measurementTime;
    private double pm10Value;
    private String pm10Flag;
    private double pm25Value;
    private String pm25Flag;
    private String sidoName;

}
