package RedCloudRule.bs.repositories;
import RedCloudRule.bs.models.Label;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LabelRepository extends JpaRepository<Label, Long>{
    List<Label> findBypublisheraccount(String pub_acc);

    @Query("select t from Label t where t.publisheraccount = ?1 and t.passornot = ?2")
    List<Label> getwithPubandJudge(String publisher_account, boolean judge);

    @Query("select t from Label t where t.publisheraccount = ?1 and t.passornot = ?2 and t.missionid = ?3")
    List<Label> getwithPubandJudgeandMission(String publisher_account, boolean judge, Long mission_id);

    //以任务mission做区分
    @Query("select distinct t.missionid from Label t where t.publisheraccount = ?1 and t.passornot = ?2")
    List<Long> separateMissionid(String publisher_account, boolean judge);

    //通过自己的id查找
    @Query("select t from Label t where t.id = ?1")
    Label labelInfoget(Long id);

    List<Label> findByIdIn(Long[] id_list);

    List<Label> findBytaskid(Long id);
}
