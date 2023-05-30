package learn.jpa.domain.proxy;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import learn.jpa.domain.member.Member;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ProxyAdvancedTest {

    @Autowired
    private EntityManagerFactory emf;

    private EntityManager em;
    private EntityTransaction tx;

    @BeforeEach
    void init() {
        em = emf.createEntityManager();
        tx = em.getTransaction();
        tx.begin();
    }

    @Test
    void persist_and_proxy() {
        Member newMember = new Member();
        newMember.setId("member");
        newMember.setUsername("회원1");
        em.persist(newMember);
        em.flush();
        em.clear();

        Member member = em.find(Member.class, "member");
        Member refMember = em.getReference(Member.class, "member");

        System.out.println("refMember.getClass() = " + refMember.getClass());
        System.out.println("member.getClass() = " + member.getClass());

        assertSame(refMember, member);
    }

    @Test
    void comparisonType() {
        Member newMember = new Member();
        newMember.setId("member");
        newMember.setUsername("회원1");
        em.persist(newMember);
        em.flush();
        em.clear();

        Member refMember = em.getReference(Member.class, "member");

        System.out.println("refMembers Type = " + refMember.getClass());

        assertNotSame(Member.class, refMember.getClass());
        assertTrue(refMember instanceof Member);
    }

    @Test
    void sameType() {
        Member newMember = new Member();
        newMember.setId("member");
        newMember.setUsername("회원1");
        em.persist(newMember);
        em.flush();
        em.clear();

        Member member = new Member();
        member.setId("member");
        member.setUsername("회원1");
        Member reference = em.getReference(Member.class, "member");

        assertTrue(member.equals(reference));

    }

    @AfterEach
    void after() {
        tx.rollback();
        em.close();
    }
}
