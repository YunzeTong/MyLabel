package RedCloudRule.bs.repositories;
import RedCloudRule.bs.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByaccount(String account);

    @Query("select t.username from User t where t.account = ?1")
    String getNamewithAcc(String acc);
}
