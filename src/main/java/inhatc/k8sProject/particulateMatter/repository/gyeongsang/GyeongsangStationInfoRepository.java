package inhatc.k8sProject.particulateMatter.repository.gyeongsang;

import inhatc.k8sProject.particulateMatter.domain.gyeongsang.GyeongsangStationInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface GyeongsangStationInfoRepository extends JpaRepository<GyeongsangStationInfo, Long> {

    // 기존의 데이터 저장 메소드
    GyeongsangStationInfo save(GyeongsangStationInfo gyeongsangStationInfo);

    // 가장 최근의 데이터 엔트리를 조회
    Optional<GyeongsangStationInfo> findTopByOrderByInPutDataTimeDesc();

    // 주어진 시간 범위 내의 데이터 조회
    List<GyeongsangStationInfo> findByInPutDataTimeBetween(LocalDateTime lastDataTime, LocalDateTime nowDateTime);

    // 주소 필드에 특정 문자열을 포함하는 모든 측정소 정보를 반환하는 메소드
    List<GyeongsangStationInfo> findByAddrContaining(String addrSnippet);


}
