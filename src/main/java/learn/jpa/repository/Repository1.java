package learn.jpa.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class Repository1 {

    @PersistenceContext
    EntityManager em;

    public void hello() {
        em.createQuery("select t from Team t");
    }
}
