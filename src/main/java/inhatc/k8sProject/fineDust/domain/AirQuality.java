package inhatc.k8sProject.fineDust.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class AirQuality {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime dataTime; //측정시간
    private String stationName; //측성소 이름
    private String sidoName; // 지역이름
    private double pm10Value;
    private double pm25Value;

    @ManyToOne // 여러 개의 미세먼지 데이터가 하나의 측정소에 연결될 수 있음
    @JoinColumn(name = "station_id") // 연결할 외래키 지정
    private StationInfo stationInfo; // 측정소 정보
}
