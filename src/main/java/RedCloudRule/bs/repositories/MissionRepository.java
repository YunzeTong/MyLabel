package RedCloudRule.bs.repositories;
import RedCloudRule.bs.models.Mission;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;


public interface MissionRepository extends JpaRepository<Mission, Long>{
    List<Mission> findBypublisheraccount(String publisheraccount);

    //配合all_mission_get使用，找不为该账号发布的任务
    List<Mission> findBypublisheraccountNot(String publisheraccount);

}
