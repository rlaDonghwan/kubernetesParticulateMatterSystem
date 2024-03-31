package inhatc.k8sProject.fineDust.repository.gangwon;

import inhatc.k8sProject.fineDust.domain.gangwon.GangwonStationInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface GangwonStationInfoRepository extends JpaRepository<GangwonStationInfo, Long> {
    GangwonStationInfo save(GangwonStationInfo gangwonStationInfo);

}
