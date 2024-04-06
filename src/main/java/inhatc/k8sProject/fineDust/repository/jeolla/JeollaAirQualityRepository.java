package inhatc.k8sProject.fineDust.repository.jeolla;

import inhatc.k8sProject.fineDust.domain.jeolla.JeollaAirQuality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JeollaAirQualityRepository extends JpaRepository<JeollaAirQuality, Long> {
}
