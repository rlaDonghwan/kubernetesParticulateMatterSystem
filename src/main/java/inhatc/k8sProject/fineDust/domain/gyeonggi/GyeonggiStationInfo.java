package inhatc.k8sProject.fineDust.domain.gyeonggi;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Data
public class GyeonggiStationInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String stationName;

    @Column(length = 100)
    private String addr;

    private double dmX;
    private double dmY;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime inPutDataTime; // 입력 시간


}
