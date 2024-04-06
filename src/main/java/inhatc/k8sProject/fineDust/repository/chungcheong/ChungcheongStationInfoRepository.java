package inhatc.k8sProject.fineDust.repository.chungcheong;

import inhatc.k8sProject.fineDust.domain.chungcheong.ChungcheongStationInfo;
import inhatc.k8sProject.fineDust.domain.gangwon.GangwonStationInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ChungcheongStationInfoRepository extends JpaRepository<ChungcheongStationInfo, Long> {
    ChungcheongStationInfo save(ChungcheongStationInfo chungcheongStationInfo);

}
