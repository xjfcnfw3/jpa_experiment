package learn.jpa.domain.member;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import learn.jpa.domain.locker.Locker;
import learn.jpa.domain.prodect.Product;
import learn.jpa.domain.team.Team;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
//@Table(name = "MEMBER", uniqueConstraints = {@UniqueConstraint(
//    name = "NAME_AGE_UNIQUE",
//    columnNames = {"NAME", "AGE"}
//)})
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private String id;

    @Column(name = "NAME")
    private String username;

    private Integer age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID")
    private Team team;

    @OneToOne
    @JoinColumn(name = "LOCKER_ID")
    private Locker locker;

    @ManyToMany
    @JoinTable(name = "MEMBER_PRODUCT",
    joinColumns = @JoinColumn(name = "MEMBER_ID"),
    inverseJoinColumns = @JoinColumn(name = "PRODUCT_ID"))
    private List<Product> products = new ArrayList<>();


    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    @Lob
    private String description;

    @Embedded
    private Period period;

    /**
     * 임베디드 타입은 clone을 통해서 복사하지 않으면 공유 참조가 되어 하나의 엔티티의 값이 변경되면 다른 엔티티의 값이 변경될 수 있다.
     * -> 수정자를 만들지 않는 방법을 추천
     */
    @Embedded
    private Address homeAddress;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "city", column = @Column(name = "COMPANY_CITY")),
        @AttributeOverride(name = "street", column = @Column(name = "COMPANY_STREET")),
        @AttributeOverride(name = "zipcode", column = @Column(name = "COMPANY_ZIPCODE"))
    })
    private Address companyAddress;

    @ElementCollection
    @CollectionTable(name = "FAVORITE_FOODS",
        joinColumns = @JoinColumn(name = "MEMBER_ID"))
    @Column(name = "FOOD_NAME")
    private Set<String> favoriteFoods = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "ADDRESS", joinColumns = @JoinColumn(name = "MEMBER_ID"))
    private List<Address> addressesHistory = new ArrayList<>();


    public Member(final String id, final String username) {
        this.id = id;
        this.username = username;
    }

    public void setTeam(Team team) {
        if (this.team != null) {
            this.team.getMembers().remove(this);
        }
        this.team = team;
        team.getMembers().add(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof Member)) {
            return false;
        }

        Member member = (Member) o;

        if (!Objects.equals(username, member.getUsername()))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, age, team, locker, products, roleType, createDate, lastModifiedDate,
            description,
            period, homeAddress, companyAddress, favoriteFoods, addressesHistory);
    }
}
