package learn.jpa.domain.member;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import javax.persistence.EntityManager;
import learn.jpa.repository.MemberRepository;
import learn.jpa.domain.prodect.Product;
import learn.jpa.domain.team.Team;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@Slf4j
@DataJpaTest
class MemberTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void test() {
        saveTeam();
    }

    @Test
    void query() {
        saveTeam();
        String jpql = "select m from Member m join m.team t where t.name = :teamName";
        List<Member> resultList = em.createQuery(jpql, Member.class)
            .setParameter("teamName", "팀1")
            .getResultList();

        for (Member member : resultList) {
            System.out.println("[query] member.name = " + member.getUsername());
        }
    }

    @Test
    void update() {
        saveTeam();

        Team team2 = new Team("team2", "팀2");
        em.persist(team2);

        Member member = em.find(Member.class, "member1");
        member.setTeam(team2);
    }

    @Test
    void biDirection() {
        saveTeam();
        Team team1 = em.find(Team.class, "team1");
        List<Member> members = team1.getMembers();
        assertThat(members.size()).isEqualTo(2);
    }

    @Test
    void saveTeam() {
        Team team1 = new Team("team1", "팀1");
        em.persist(team1);

        Member member1 = new Member("member1", "회원1");

        member1.setTeam(team1);          // 연관관계 설정 member1 -> team1
        em.persist(member1);

        Member member2 = new Member("member2", "회원2");

        member2.setTeam(team1);          // 연관관계 설정 member2 -> team1
        em.persist(member2);
        em.flush();
    }

    @Test
    void saveProduct() {
        Product productA = new Product();
        productA.setName("상품A");
        em.persist(productA);

        Member member1 = new Member();
        member1.setId("member1");
        member1.setUsername("회원1");
        member1.getProducts().add(productA);
        em.persist(member1);
    }

    @Test
    void findProduct() {
        Product productA = new Product();
        productA.setName("상품A");
        em.persist(productA);

        Member member1 = new Member();
        member1.setId("member1");
        member1.setUsername("회원1");
        member1.getProducts().add(productA);
        em.persist(member1);

        Member member = em.find(Member.class, "member1");
        List<Product> products = member.getProducts();
        products.forEach((product -> System.out.println("product = " + product)));
    }


    @Test
    void n_1_Test() {
        Team team1 = new Team("team1", "팀1");
        em.persist(team1);
        for (int i = 0; i< 10; i++) {
            Member member = new Member(String.valueOf(i), "회원" + String.valueOf(i));
            member.setTeam(team1);
            memberRepository.save(member);
        }
        em.clear();
        List<Member> members = memberRepository.findAllJPQL();
        assertThat(members.size()).isEqualTo(10);
    }
}
