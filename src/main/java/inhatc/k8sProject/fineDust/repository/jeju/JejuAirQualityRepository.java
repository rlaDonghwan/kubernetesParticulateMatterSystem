package inhatc.k8sProject.fineDust.repository.jeju;

import inhatc.k8sProject.fineDust.domain.gyeonggi.GyeonggiAirQuality;
import inhatc.k8sProject.fineDust.domain.jeju.JejuAirQuality;
import inhatc.k8sProject.fineDust.domain.jeolla.JeollaAirQuality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JejuAirQualityRepository extends JpaRepository<JejuAirQuality, Long> {

    Optional<JejuAirQuality> findFirstByStationNameOrderByDataTimeDesc(String stationName);
}
