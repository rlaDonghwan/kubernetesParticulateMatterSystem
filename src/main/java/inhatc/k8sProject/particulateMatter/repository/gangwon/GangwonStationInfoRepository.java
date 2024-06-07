package inhatc.k8sProject.particulateMatter.repository.gangwon;

import inhatc.k8sProject.particulateMatter.domain.gangwon.GangwonStationInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface GangwonStationInfoRepository extends JpaRepository<GangwonStationInfo, Long> {

    GangwonStationInfo save(GangwonStationInfo gangwonStationInfo);

    Optional<GangwonStationInfo> findTopByOrderByInPutDataTimeDesc();

    List<GangwonStationInfo> findByInPutDataTimeBetween(LocalDateTime lastDataTime, LocalDateTime nowDateTime);

    List<GangwonStationInfo> findByAddrContaining(String addrSnippet);
}
