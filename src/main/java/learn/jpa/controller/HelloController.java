package learn.jpa.controller;

import javax.servlet.http.HttpServletRequest;
import learn.jpa.domain.member.Member;
import learn.jpa.domain.team.Team;
import learn.jpa.service.HelloService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HelloController {
    private final HelloService service;

    @GetMapping("/test1")
    public String test1() {
        Team team = service.getTeam("team1");
        System.out.println("team.getMembers() = " + team.getMembers());
        return "ok";
    }

    @GetMapping("/test2")
    public String test2() {
        Member member = service.getMember("member1");
        Team team = member.getTeam();
        System.out.println("team.getClass() = " + team.getClass());
        return team.getName() + team.getId();
    }

    @GetMapping("/test3")
    public String ip(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null) ip = request.getRemoteAddr();
        return ip;
    }
}
