package inhatc.k8sProject.fineDust.repository.gyeonggi;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import inhatc.k8sProject.fineDust.domain.gyeonggi.GyeonggiAirQuality;

@Repository
public interface GyeonggiAirQualityRepository extends JpaRepository<GyeonggiAirQuality, Long> {
}
