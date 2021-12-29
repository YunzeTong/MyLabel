package RedCloudRule.bs.repositories;
import RedCloudRule.bs.models.Task;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface TaskRepository extends JpaRepository<Task, Long>{
    List<Task> findByclaimeraccount(String claimer_account);
    List<Task> findBylabelornot(boolean judge);       //这个前端要传false，或者controller传
    
    @Query("select t from Task t where t.publisheraccount = ?1 and t.labelornot = ?2")
    List<Task> getwithPubandJudge(String publisher_account, boolean judge);
    
    @Query("select t from Task t where t.missionid = ?1 and t.claimeraccount = ?2")
    List<Task> getSpecificTask(Long mission_id, String claimer_account);
    // Task findBymissionidAndclaimeraccount(Long mission_id, String claimer_account);

    @Query("select t from Task t where t.id = ?1")
    Task chooseTask(Long task_id);
}
