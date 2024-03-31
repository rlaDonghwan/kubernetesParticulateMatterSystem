package inhatc.k8sProject.fineDust.repository.gyeonggi;

import inhatc.k8sProject.fineDust.domain.gyeonggi.GyeonggiStationInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface GyeonggiStationInfoRepository extends JpaRepository<GyeonggiStationInfo, Long> {


    GyeonggiStationInfo save(GyeonggiStationInfo gyeonggiStationInfo);

}
