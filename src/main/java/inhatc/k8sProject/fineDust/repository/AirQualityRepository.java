package inhatc.k8sProject.fineDust.repository;

import inhatc.k8sProject.fineDust.domain.AirQuality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AirQualityRepository extends JpaRepository<AirQuality, Long> {

}
