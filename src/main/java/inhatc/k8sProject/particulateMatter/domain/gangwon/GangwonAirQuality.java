package inhatc.k8sProject.particulateMatter.domain.gangwon;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class GangwonAirQuality {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime dataTime; // 측정시간
    private String stationName; // 측정소 이름
    private String sidoName; // 지역이름
    private double pm10Value;
    private double pm25Value;


}

