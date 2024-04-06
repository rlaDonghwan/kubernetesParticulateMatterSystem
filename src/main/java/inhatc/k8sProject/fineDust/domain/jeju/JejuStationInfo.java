package inhatc.k8sProject.fineDust.domain.jeju;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class JejuStationInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String stationName;

    @Column(length = 100)
    private String addr;

    private double dmX;
    private double dmY;


}
