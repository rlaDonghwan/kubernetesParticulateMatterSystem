package inhatc.k8sProject.fineDust.repository.gangwon;

import inhatc.k8sProject.fineDust.domain.gangwon.GangwonAirQuality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GangwonAirQualityRepository extends JpaRepository<GangwonAirQuality, Long> {
}
