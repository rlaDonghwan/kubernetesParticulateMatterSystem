package inhatc.k8sProject.fineDust.domain.chungcheong;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class ChungcheongAirQuality {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime dataTime; // 측정시간
    private String stationName; // 측정소 이름
    private String sidoName; // 지역이름
    private double pm10Value;
    private double pm25Value;


}

