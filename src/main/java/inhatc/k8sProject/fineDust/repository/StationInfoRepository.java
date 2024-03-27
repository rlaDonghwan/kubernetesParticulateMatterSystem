package inhatc.k8sProject.fineDust.repository;

import inhatc.k8sProject.fineDust.domain.StationInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StationInfoRepository extends JpaRepository<StationInfo, Long> {
    Optional<StationInfo> findByStationName(String stationName);
}
