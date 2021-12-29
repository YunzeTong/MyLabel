package RedCloudRule.bs.repositories;
import RedCloudRule.bs.models.Picture;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PictureRepository extends JpaRepository<Picture,Long>{
    List<Picture> findByowneraccount(String owner_account);
    List<Picture> findBypath(String path_);
    
    @Query("select t from Picture t where t.id = ?1")
    Picture choosePicture(Long pic_id);

    @Query("select t.width from Picture t where t.path = ?1")
    int chooseWidth(String pic_id);

    @Query("select t.height from Picture t where t.path = ?1")
    int chooseHeight(String pic_id);
}
