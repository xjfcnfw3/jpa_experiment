package learn.jpa.domain.member;

import javax.persistence.Embeddable;

@Embeddable
public class Zipcode {
    private String zip;
    private String plusFour;
}
