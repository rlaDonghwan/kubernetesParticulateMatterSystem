package inhatc.k8sProject.fineDust.repository.gyeongsang;

import inhatc.k8sProject.fineDust.domain.gyeonggi.GyeonggiStationInfo;
import inhatc.k8sProject.fineDust.domain.gyeongsang.GyeongsangStationInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface GyeongsangStationInfoRepository extends JpaRepository<GyeongsangStationInfo, Long> {


    GyeongsangStationInfo save(GyeongsangStationInfo gyeongsangStationInfo);

}
