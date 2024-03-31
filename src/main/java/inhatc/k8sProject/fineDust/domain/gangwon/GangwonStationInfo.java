package inhatc.k8sProject.fineDust.domain.gangwon;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
public class GangwonStationInfo {

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
