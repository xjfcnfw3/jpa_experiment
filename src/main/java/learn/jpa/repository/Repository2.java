package learn.jpa.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import learn.jpa.domain.member.Member;
import org.springframework.stereotype.Repository;

@Repository
public class Repository2 {

    @PersistenceContext
    EntityManager em;

    public Member findMember() {
        return em.find(Member.class, "id1");
    }
}
