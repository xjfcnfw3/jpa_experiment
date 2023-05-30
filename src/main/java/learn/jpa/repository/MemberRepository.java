package learn.jpa.repository;

import java.util.List;
import learn.jpa.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, String> {

    @Query("select u from Member u left join fetch u.team")
    List<Member> findAllJPQL();
}
