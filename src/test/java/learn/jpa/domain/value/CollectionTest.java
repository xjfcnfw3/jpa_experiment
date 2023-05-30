package learn.jpa.domain.value;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import learn.jpa.domain.member.Address;
import learn.jpa.domain.member.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class CollectionTest {

    @Autowired
    private EntityManagerFactory emf;

    @Test
    void insertCollection() {
        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();

        Member member = new Member();
        member.setId("test1");

        member.setHomeAddress(new Address("부산", "남구", "111-1111"));

        member.getFavoriteFoods().add("짬뽕");
        member.getFavoriteFoods().add("짜장");
        member.getFavoriteFoods().add("탕수육");

        member.getAddressesHistory().add(new Address("서울", "강남", "123-000"));
        member.getAddressesHistory().add(new Address("서울", "강북", "000-000"));
        entityManager.persist(member);
        entityManager.flush();
        tx.commit();
    }
}
