package devcourse.jpa.domain;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

@Slf4j
@SpringBootTest
//@DataJpaTest
// Spring Data JPA 를 사용하지 않고 지금처럼 직접 트랜잭션을 사용하는 경우, @Transactional 과 간섭을 일으킨다.
public class PersistenceContextTest {

    @Autowired
    CustomerRepository repository;

    @Autowired
    EntityManagerFactory entityManagerFactory;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("저장")
    void save() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();

        Customer customer = new Customer(); // 비영속 상태
        customer.setId(1L);
        customer.setFirstName("First");
        customer.setLastName("Last");

        entityManager.persist(customer); // 비영속 -> 영속 (영속화)

        transaction.commit(); // entityManager.flush();

        entityManager.detach(customer); // 영속 -> 준영속
    }

    @Test
    @DisplayName("조회 - DB조회")
    void DB_Query() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();

        Customer customer = new Customer(); // 비영속 상
        customer.setId(1L);
        customer.setFirstName("First");
        customer.setLastName("Last");

        entityManager.persist(customer); // 비영속 -> 영속 (영속화)
        transaction.commit(); // entityManager.flush();

        entityManager.detach(customer); // 영속 -> 준영속

        Customer selected = entityManager.find(Customer.class, 1L);
        log.info("{} {}", selected.getFirstName(), selected.getFirstName());
    }
}
