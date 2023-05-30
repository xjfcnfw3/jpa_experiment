package learn.jpa.domain.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
public class MemberDto {
    private String username;
    private Integer age;

    public MemberDto(String username, Integer age) {
        this.username = username;
        this.age = age;
    }

    @Override
    public String toString() {
        return "[username=" + username + ", age=" + age + "]" ;
    }
}
