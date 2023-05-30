package learn.jpa.domain.proxy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import learn.jpa.domain.member.Member;
import learn.jpa.domain.team.Team;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ProxyTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private EntityManagerFactory factory;

    @BeforeEach
    void init() {
        Team team = new Team("team1", "1");
        em.persist(team);
        Member member = new Member();
        member.setUsername("tester");
        member.setId("test");
        member.setTeam(team);
        member.setAge(12);
        em.persist(member);
        em.flush();
        em.clear();
    }

    @Test
    void proxyTest() {
        Member member = new Member();
        member.setUsername("test");
        member.setId("id1");
        member.setAge(12);
        em.persist(member);

        // 단 준영속 상태의 프록시를 초기화하면 예외가 발생
        Member reference = em.getReference(Member.class, "id1");
        System.out.println("reference.getUsername() = " + reference.getUsername());
    }

    @Test
    void detachedProxy() {
        EntityManager entityManager = factory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();
        Member member = new Member();
        member.setUsername("test");
        member.setId("id1");
        member.setAge(12);
        entityManager.persist(member);
        entityManager.detach(member);

        // 단 준영속 상태의 프록시를 초기화하면 예외가 발생
        Member reference = entityManager.getReference(Member.class, "id1");
        transaction.commit();
        entityManager.close();

        // 다음과 같이 준영속 상태의 객체를 프록시로 참조한다면 예외가 발생한다.
        assertThatThrownBy(reference::getUsername)
            .isInstanceOf(LazyInitializationException.class);
        // 하지만 getReference 파라미터인 식별자는 프록시에 저장하고 참조해도 초기화되지 않아 예외가 발생하지 않는다.
        reference.getId();
    }

    /**
     * 이 테스트는 좀더 공부를 해야겠음
     * getReference로 프록시를 구하는데 만약 해당 객체가 영속 상태가 아니면 예외가 발생
     * 근데 타켓 객체를 영속 상태로 만들어도 오류가 발생 -> equal 재정의
     */
    @Test
    void initProxy() {
        EntityManager entityManager = factory.createEntityManager();
        Member reference = entityManager.getReference(Member.class, "test");
        boolean beforeLoaded = entityManager.getEntityManagerFactory().getPersistenceUnitUtil().isLoaded(reference);
        assertThat(beforeLoaded).isEqualTo(false);
        System.out.println(reference.getClass());
        System.out.println("reference.getId() = " + reference.getId());
        beforeLoaded = entityManager.getEntityManagerFactory().getPersistenceUnitUtil().isLoaded(reference);
        assertThat(beforeLoaded).isEqualTo(false);
        entityManager.close();
    }

    /**
     * 즉시 로딩시 - 엔티티를 즉시로딩
     * 지연 로딩시 - 엔티티를 프록시로 로딩 -> 이때 프록시에 값이 없는 필드를 조회시 select 문으로 조회 후 초기화 -> n + 1 문제
     */
    @Test
    void loading() {
        Member member = em.find(Member.class, "test");
        Team team = member.getTeam();
        System.out.println("team = " + team.getClass());
        team.getName();
        System.out.println("team.getClass() = " + team.getClass());
    }

    @Test
    void n_1_test() {
        saveMember();
        Team team = em.find(Team.class, "bigTeam");
        List<Member> members = team.getMembers();
        assertThat(members.size()).isEqualTo(10);
        System.out.println("+++++++++++++++++++++++");
        members.stream().forEach(member -> member.getUsername());
        em.clear();
    }

    private void saveMember() {
        EntityManager em = factory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        Team team = new Team("bigTeam", "7");
        em.persist(team);
        for (int i = 1; i <= 10; i++) {
            Member member = new Member();
            member.setUsername("tester" + i);
            member.setId("test" + i);
            member.setTeam(team);
            member.setAge(12);
            em.persist(member);
        }

        for (int i = 1; i<=4; i++) {
            Team otherTeam = new Team("Team" + i, "t" + i);
            em.persist(otherTeam);
        }
        em.flush();
        transaction.commit();
        em.clear();
    }
}
