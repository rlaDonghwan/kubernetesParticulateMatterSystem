package inhatc.k8sProject.particulateMatter.domain.gyeongsang;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class GyeongsangStationInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String stationName; //측정소 이름

    @Column(length = 100)
    private String addr; //주소

    private double dmX; //위도
    private double dmY; //경도
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime inPutDataTime; // 입력 시간

}
