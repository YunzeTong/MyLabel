package RedCloudRule.bs.repositories;
import RedCloudRule.bs.models.Mylabel;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MylabelRepository extends JpaRepository<Mylabel, Long> {
    
    List<Mylabel> findByowner(String owner); 

    List<Mylabel> findByIdIn(Long[] id_list);
}
