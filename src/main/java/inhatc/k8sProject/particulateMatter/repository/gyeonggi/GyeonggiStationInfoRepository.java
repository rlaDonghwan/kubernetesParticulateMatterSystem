package inhatc.k8sProject.particulateMatter.repository.gyeonggi;

import inhatc.k8sProject.particulateMatter.domain.gyeonggi.GyeonggiStationInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GyeonggiStationInfoRepository extends JpaRepository<GyeonggiStationInfo, Long> {

    // 기존의 데이터 저장 메소드
    GyeonggiStationInfo save(GyeonggiStationInfo gyeonggiStationInfo);

    // 가장 최근의 데이터 엔트리를 조회
    Optional<GyeonggiStationInfo> findTopByOrderByInPutDataTimeDesc();

    // 주어진 시간 범위 내의 데이터 조회
    List<GyeonggiStationInfo> findByInPutDataTimeBetween(LocalDateTime lastDataTime, LocalDateTime nowDateTime);

    // 주소 필드에 특정 문자열을 포함하는 모든 측정소 정보를 반환하는 메소드
    List<GyeonggiStationInfo> findByAddrContaining(String addrSnippet);




}
