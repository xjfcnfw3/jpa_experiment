package learn.jpa.domain.team;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import learn.jpa.domain.member.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@ToString
@NoArgsConstructor
public class Team {

    @Id
    @Column(name = "TEAM_ID")
    private String id;
    private String name;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID")
    private List<Member> members = new ArrayList<>();

    public Team(final String id, final String name) {
        this.id = id;
        this.name = name;
    }
}
