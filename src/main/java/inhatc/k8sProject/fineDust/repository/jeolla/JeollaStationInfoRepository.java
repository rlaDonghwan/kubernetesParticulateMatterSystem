package inhatc.k8sProject.fineDust.repository.jeolla;

import inhatc.k8sProject.fineDust.domain.jeolla.JeollaStationInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface JeollaStationInfoRepository extends JpaRepository<JeollaStationInfo, Long> {


    JeollaStationInfo save(JeollaStationInfo jeollaStationInfo);

}
