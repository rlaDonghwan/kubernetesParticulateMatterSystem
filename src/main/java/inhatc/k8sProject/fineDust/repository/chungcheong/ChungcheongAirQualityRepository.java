package inhatc.k8sProject.fineDust.repository.chungcheong;

import inhatc.k8sProject.fineDust.domain.chungcheong.ChungcheongAirQuality;
import inhatc.k8sProject.fineDust.domain.gangwon.GangwonAirQuality;
import inhatc.k8sProject.fineDust.domain.gyeonggi.GyeonggiAirQuality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChungcheongAirQualityRepository extends JpaRepository<ChungcheongAirQuality, Long> {

    Optional<ChungcheongAirQuality> findFirstByStationNameOrderByDataTimeDesc(String stationName);

}
