package inhatc.k8sProject.fineDust.repository.gangwon;

import inhatc.k8sProject.fineDust.domain.gangwon.GangwonAirQuality;
import inhatc.k8sProject.fineDust.domain.gyeonggi.GyeonggiAirQuality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GangwonAirQualityRepository extends JpaRepository<GangwonAirQuality, Long> {
    Optional<GangwonAirQuality> findFirstByStationNameOrderByDataTimeDesc(String stationName);


}
