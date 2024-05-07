package inhatc.k8sProject.fineDust.repository.jeju;

import inhatc.k8sProject.fineDust.domain.gyeonggi.GyeonggiAirQuality;
import inhatc.k8sProject.fineDust.domain.jeju.JejuAirQuality;
import inhatc.k8sProject.fineDust.domain.jeolla.JeollaAirQuality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JejuAirQualityRepository extends JpaRepository<JejuAirQuality, Long> {
    /**
     * 주어진 측정소 이름(stationName)을 기준으로 최신 데이터 시간(dataTime)에 따른
     * 가장 최근의 제주도 대기질 데이터를 찾습니다.
     *
     * @param stationName 측정소 이름
     * @return Optional로 감싸진 제주도 대기질 데이터 (해당 측정소의 가장 최근 데이터)
     */
    Optional<JejuAirQuality> findFirstByStationNameOrderByDataTimeDesc(String stationName);
}