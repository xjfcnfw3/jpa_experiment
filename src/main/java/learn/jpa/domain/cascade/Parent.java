package learn.jpa.domain.cascade;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Parent {

    @Id
    @GeneratedValue()
    private Long id;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.PERSIST, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Child> child = new HashSet<>();
}
