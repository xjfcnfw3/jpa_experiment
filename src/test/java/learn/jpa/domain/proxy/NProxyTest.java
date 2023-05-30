package learn.jpa.domain.proxy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import learn.jpa.domain.member.Member;
import learn.jpa.domain.team.Team;
import learn.jpa.repository.MemberRepository;
import learn.jpa.repository.TeamRepository;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class NProxyTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamRepository teamRepository;

    @BeforeEach
    void init() {
        Team team = new Team("team", "t");
        List<Member> members = new ArrayList<>();
        List<Team> teams = new ArrayList<>();
        teamRepository.save(team);
        for (int i = 1; i <= 10; i++) {
            Member member = new Member();
            member.setId("member" + i);
            member.setUsername("m" + i);
            member.setTeam(team);
            members.add(member);
        }

        for (int i =1; i <= 5; i++) {
            Team otherTeam = new Team("t" + i, "Team" + i);
            teams.add(otherTeam);
        }
        teamRepository.saveAll(teams);
        memberRepository.saveAll(members);
        em.flush();
        em.clear();
    }

    @Test
    void findAll() {
        System.out.println("============= go loop member =============");
//        List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();
//        assertThat(members.size()).isEqualTo(10);
        System.out.println("============= go loop team =============");
        List<Team> teams = teamRepository.findAll();
        System.out.println("============= go loop =============");
        for (Team team : teams) {
            System.out.println("team.getName() = " + team.getMembers().getClass());
        }

        System.out.println("============= not loop =============");
        List<Team> teamsByJPQL = teamRepository.findAllByJPQL();
        for (Team team : teamsByJPQL) {
            System.out.println("team.getName() = " + team.getMembers().getClass());
        }
        assertThat(teams.size()).isEqualTo(6);
    }
}
