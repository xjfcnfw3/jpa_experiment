package learn.jpa.domain.member;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Address {

    @Column(name = "city")
    private String city;
    private String street;
    private String zipcode;
}
