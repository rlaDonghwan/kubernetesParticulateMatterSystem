package inhatc.k8sProject.fineDust.domain.chungcheong;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ChungcheongStationInfo {

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
