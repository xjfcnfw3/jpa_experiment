package learn.jpa.domain.query;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Iterator;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import learn.jpa.domain.member.Address;
import learn.jpa.domain.member.Member;
import learn.jpa.domain.team.Team;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class QueryTest {

    @Autowired
    private EntityManagerFactory emf;

    private EntityManager em;
    private EntityTransaction tx;

    @BeforeEach
    void init() {
        em = emf.createEntityManager();
        tx = em.getTransaction();
        tx.begin();
        Member member = new Member();
        member.setId("test");
        member.setUsername("kim");
        member.setHomeAddress(new Address("서울", "압구정", "1111"));
        em.persist(member);
        em.flush();
        System.out.println("============== start ===============");
    }

    @Test
    void selectMember() {
        String jpql = "select m from Member m where m.username = 'kim'";
        List<Member> resultList = em.createQuery(jpql, Member.class).getResultList();
    }

    @Test
    void startCriteria() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Member> query = cb.createQuery(Member.class);

        Root<Member> m = query.from(Member.class);

        CriteriaQuery<Member> cq = query.select(m).where(cb.equal(m.get("username"), "kim"));
        List<Member> resultList = em.createQuery(cq).getResultList();
        assertThat(resultList.size()).isNotEqualTo(0);
    }

    @Test
    void binding() {
        String usernameParam = "kim";
        TypedQuery<Member> query = em
            .createQuery("select m from Member m where m.username = :username", Member.class);
        query.setParameter("username", usernameParam);
        List<Member> resultList = query.getResultList();
        assertThat(resultList.size()).isNotEqualTo(0);
    }

    @Test
    void embedded() {
        String query = "select m.homeAddress from Member m";
        List<Address> resultList = em.createQuery(query, Address.class).getResultList();
    }

    @Test
    void multiMember() {
        saveUser();

        List<Object[]> resultList = em.createQuery("select m.username, m.age from Member m").getResultList();

        for (Object[] row : resultList){
            String username = (String) row[0];
            Integer age = (Integer) row[1];
            System.out.println("username = " + username + ", age = " + age);
        }
    }

    @Test
    void newQuery() {
        saveUser();

        TypedQuery<MemberDto> query = em.createQuery("select new learn.jpa.domain.query.MemberDto(m.username, m"
                + ".age) from Member m", MemberDto.class);
        query.getResultList().forEach(dto -> System.out.println("dto = " + dto));
    }

    @Test
    void paging() {
        saveUser();
        TypedQuery<Member> query = em.createQuery("select m from Member m ORDER BY m.username desc", Member.class);
        query.setFirstResult(5);
        query.setMaxResults(3);
        List<Member> resultList = query.getResultList();
        System.out.println("resultList = " + resultList);
    }

    @Test
    void join() {
        saveJoinUser();
        String teamName = "Team1";
        String query = "select m from Member m join m.team t where t.name = :teamName";
        List<Member> members = em.createQuery(query, Member.class)
            .setParameter("teamName", teamName)
            .getResultList();
        System.out.println("members = " + members);
    }

    @Test
    void outerJoin() {
        saveJoinUser();
        saveUser();
        String query = "select m from Member m left outer join m.team t";
        List<Member> members = em.createQuery(query, Member.class)
            .getResultList();
        System.out.println("members = " + members.size());
    }

    @Test
    void lazyLoadingJoin() {
        saveJoinUser();
        saveUser();
        String query = "select m from Member m join m.team t";
        em.createQuery(query, Member.class).getResultList()
            .forEach(member -> System.out.println("member.name = " + member.getUsername()
                + "member.team=" + member.getTeam()));
    }

    @Test
    void fetchJoin() {
        saveJoinUser();
        saveUser();
        String query = "select m from Member m join fetch m.team";
        em.createQuery(query, Member.class).getResultList()
            .forEach(member -> System.out.println("member.name = " + member
                + "member.team=" + member.getTeam()));
    }

    @Test
    void collectionFetchJoin() {
        saveJoinUser();
        String query = "select t from Team t join fetch t.members where t.name='Team1'";
        em.createQuery(query, Team.class).getResultList()
            .forEach(team -> System.out.println("team.getMembers() = " + team.getMembers()));
    }

    @Test
    void collectionFetchDistinctJoin() {
        saveJoinUser();
        String query = "select distinct t from Team t join fetch t.members where t.name='Team1'";
        em.createQuery(query, Team.class).getResultList()
            .forEach(team -> System.out.println("team.getMembers() = " + team.getMembers()));
    }

    private void saveJoinUser() {
        Team team = new Team("팀1", "Team1");
        em.persist(team);

        Member member = new Member();
        member.setId("멤버1");
        member.setUsername("member1");
        member.setTeam(team);
        em.persist(member);

        Member member2 = new Member();
        member2.setId("멤버2");
        member2.setUsername("member2");
        member2.setTeam(team);
        em.persist(member2);

        em.flush();
        em.clear();
        System.out.println("============== test begin ===============");
    }

    private void saveUser() {
        for (int i=1; i<=10;i++) {
            Member member = new Member();
            member.setId("m" + i);
            member.setUsername("Member" + i);
            member.setAge(20 + i);
            em.persist(member);
        }
        em.flush();
        em.clear();
        System.out.println("============== test begin ===============");
    }

    @AfterEach
    void end() {
        em.createQuery("delete from Member m");
        em.createQuery("delete from Team  t");
        em.flush();
        tx.rollback();
    }
}
