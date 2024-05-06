package inhatc.k8sProject.fineDust.repository.jeolla;

import inhatc.k8sProject.fineDust.domain.gyeonggi.GyeonggiAirQuality;
import inhatc.k8sProject.fineDust.domain.jeolla.JeollaAirQuality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JeollaAirQualityRepository extends JpaRepository<JeollaAirQuality, Long> {
    Optional<JeollaAirQuality> findFirstByStationNameOrderByDataTimeDesc(String stationName);
}
