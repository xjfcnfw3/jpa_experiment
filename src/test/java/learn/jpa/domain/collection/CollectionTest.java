package learn.jpa.domain.collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import learn.jpa.domain.cascade.Child;
import learn.jpa.domain.cascade.Parent;
import learn.jpa.domain.duck.Duck;
import learn.jpa.domain.member.Member;
import learn.jpa.domain.team.Team;
import learn.jpa.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class CollectionTest {

    @Autowired
    private EntityManagerFactory emf;

    @Autowired
    private MemberRepository memberRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    void persisTest() {
        Team team = new Team();
        team.setId("12");
        System.out.println("team.getMembers().getClass() = " + team.getMembers().getClass());
        em.persist(team);
        System.out.println("team.getMembers().getClass() = " + team.getMembers().getClass());
    }

    /**
     * 리스트와 달리 Set은 내부에 중복이 있는지 체크하기 때문에 지연 로딩 엔티티의 초기화가 발생
     */
    @Test
    void setAddTest() {
        Parent parent = new Parent();
        em.persist(parent);
        parent.getChild().add(new Child());
    }

    @Test
    void listener() {
        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        Duck duck = new Duck();
        entityManager.persist(duck);
        entityManager.flush();
        duck.setName("duck");
        transaction.commit();
        entityManager.close();
        EntityManager manager = emf.createEntityManager();
        Duck duck2 = manager.find(Duck.class, 1L);
        System.out.println("duck2.getName() = " + duck2.getName());
    }

    @Test
    @Transactional
    void sameInstance() {
        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        Member save = memberRepository.save(new Member("hello", "123"));
        Optional<Member> byId = memberRepository.findById(save.getId());
        Member member = byId.get();
        assertTrue(save == member);
        transaction.rollback();
    }
}
