package inhatc.k8sProject.fineDust.repository.jeju;

import inhatc.k8sProject.fineDust.domain.jeju.JejuStationInfo;
import inhatc.k8sProject.fineDust.domain.jeolla.JeollaStationInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface JejuStationInfoRepository extends JpaRepository<JejuStationInfo, Long> {


    JejuStationInfo save(JejuStationInfo jejuStationInfo);

}
