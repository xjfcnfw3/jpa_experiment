package learn.jpa.domain.team;

import java.util.List;
import java.util.stream.Collectors;
import learn.jpa.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TeamDto {

    private String id;
    private String name;
    private List<String> members;

    public static TeamDto of(Team team) {
        return new TeamDto(team.getId(), team.getName(),
            team.getMembers().stream().map(Member::getUsername).collect(Collectors.toList()));
    }
}
