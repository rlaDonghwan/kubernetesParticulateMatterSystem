package inhatc.k8sProject.fineDust.repository.gyeongsang;

import inhatc.k8sProject.fineDust.domain.gyeonggi.GyeonggiAirQuality;
import inhatc.k8sProject.fineDust.domain.gyeongsang.GyeongsangAirQuality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GyeongsangAirQualityRepository extends JpaRepository<GyeongsangAirQuality, Long> {
}
