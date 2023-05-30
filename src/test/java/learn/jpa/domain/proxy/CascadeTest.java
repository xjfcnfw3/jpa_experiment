package learn.jpa.domain.proxy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import learn.jpa.domain.cascade.Child;
import learn.jpa.domain.cascade.Parent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class CascadeTest {

    @Autowired
    private EntityManagerFactory emf;

    @Test
    void cascadeTest1() {
        EntityManager em = emf.createEntityManager();
        Parent parent = new Parent();
        em.persist(parent);

        Child child1 = new Child();
        child1.setParent(parent);
        parent.getChild().add(child1);
        em.persist(child1);

        Child child2 = new Child();
        child2.setParent(parent);
        parent.getChild().add(child2);
        em.persist(child2);

//        checkPersist(em);
        findChild(List.of(child1, child2), em);
    }

    @Test
    void cascadeTest2() {
        EntityManager em = emf.createEntityManager();
        Child child1 = new Child();
        Child child2 = new Child();

        Parent parent = new Parent();
        child1.setParent(parent);
        child2.setParent(parent);
        parent.getChild().add(child1);
        parent.getChild().add(child2);

        em.persist(parent);
        findChild(List.of(child1, child2), em);
    }

    @Test
    void deleteChild() {
        EntityManager em = emf.createEntityManager();
        Child child1 = new Child();
        Child child2 = new Child();

        Parent parent = new Parent();
        child1.setParent(parent);
        child2.setParent(parent);
        parent.getChild().add(child1);
        parent.getChild().add(child2);

        em.persist(parent);

        Parent parent1 = em.find(Parent.class, parent.getId());
        parent1.getChild().clear();
        System.out.println("parent1 = " + parent1);
        clearChild(List.of(child1, child2), em);
    }

    private void clearChild(List<Child> child, EntityManager em) {
        child.forEach(childEntity -> {
            Child persistedChild = em.find(Child.class, childEntity.getId());
            System.out.println(persistedChild.getId() + " " + persistedChild.getClass());
        });
    }

    private void findChild(List<Child> child, EntityManager em) {
        child.forEach(childEntity -> {
            Child persistedChild = em.find(Child.class, childEntity.getId());
            System.out.println(persistedChild.getId() + " " + persistedChild.getClass());
            assertThat(persistedChild).isNotNull();
        });
    }

    /**
     * 해당코드는 잘못되었다. 왜냐하면 jpql은 영속 상태의 엔티티를 조회하는 것이아닌 DB에 조회하기 때문
     * 따라서 flush를 하지 않으면 조회할 수 없다.
     */
    private void checkPersist(EntityManager em) {
        List<Child> child = em.createQuery("select c from Child c", Child.class).getResultList();
        child.forEach(childEntity -> assertThat(childEntity).isNotNull());
        assertThat(child.size()).isNotEqualTo(0);
    }
}
