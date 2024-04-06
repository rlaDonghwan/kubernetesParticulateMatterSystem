package inhatc.k8sProject.fineDust.repository.chungcheong;

import inhatc.k8sProject.fineDust.domain.chungcheong.ChungcheongAirQuality;
import inhatc.k8sProject.fineDust.domain.gangwon.GangwonAirQuality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChungcheongAirQualityRepository extends JpaRepository<ChungcheongAirQuality, Long> {
}
