package learn.jpa.service;

import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import learn.jpa.domain.member.Member;
import learn.jpa.domain.team.Team;
import learn.jpa.repository.MemberRepository;
import learn.jpa.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
//@Transactional
@RequiredArgsConstructor
public class HelloService {

    @PersistenceContext
    private EntityManager entityManager;

    private final TeamRepository repository;
    private final MemberRepository memberRepository;

    @PostConstruct
    public void init() {
        Member member = new Member();
        member.setId("member1");
        member.setUsername("m1");
        memberRepository.save(member);
        Team team = new Team("team1", "t1");
        member.setTeam(team);
        repository.save(team);
        entityManager.clear();
    }

    @Transactional
    public Team getTeam(String teamId) {
        Team team = repository.findById(teamId)
            .orElse(null);
        System.out.println("team.getMembers().get(0).getClass() = " + Objects.requireNonNull(team).getMembers().get(0).getClass());
        return team;
    }

    @Transactional
    public Member getMember(String memberId) {
        Member member = memberRepository.findById(memberId).orElse(null);
        System.out.println("member.getTeam() = " + Objects.requireNonNull(member).getTeam().getClass());
        return member;
    }
}
