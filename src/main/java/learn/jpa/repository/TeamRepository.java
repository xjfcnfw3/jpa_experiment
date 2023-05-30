package learn.jpa.repository;

import java.util.List;
import learn.jpa.domain.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TeamRepository extends JpaRepository<Team, String> {

    @Query("select distinct t from Team t join fetch t.members")
    List<Team> findAllByJPQL();
}
