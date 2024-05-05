package inhatc.k8sProject.fineDust.repository.gyeonggi;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import inhatc.k8sProject.fineDust.domain.gyeonggi.GyeonggiAirQuality;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface GyeonggiAirQualityRepository extends JpaRepository<GyeonggiAirQuality, Long> {
    Optional<GyeonggiAirQuality> findFirstByStationNameOrderByDataTimeDesc(String stationName);



}

