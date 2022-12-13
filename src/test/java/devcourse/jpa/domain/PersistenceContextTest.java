package devcourse.jpa.domain;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

@Slf4j
@SpringBootTest // Spring Context
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

        Customer customer = new Customer(); // 비영속 상태
        customer.setId(1L);
        customer.setFirstName("First");
        customer.setLastName("Last");

        entityManager.persist(customer); // 비영속 -> 영속 (영속화)
        transaction.commit(); // entityManager.flush();

        entityManager.detach(customer); // 영속 -> 준영속

        Customer selected = entityManager.find(Customer.class, 1L);
        log.info("{} {}", selected.getFirstName(), selected.getLastName());
    }

    @Test
    @DisplayName("조회 - 1차 캐시 이용")
    void Query_FirstCash() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("First");
        customer.setLastName("Last");

        entityManager.persist(customer);
        transaction.commit();

        // entityManager.detach(customer); // DB 조회와 달리, 영속 상태를 유지하기 위해 주석 처리했다.

        Customer selected = entityManager.find(Customer.class, 1L);
        log.info("{} {}", selected.getFirstName(), selected.getLastName());
    }

    @Test
    @DisplayName("수정 - 변경감지(Dirty Checking)")
    void dirtyChecking() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("Seonho");
        customer.setLastName("Kim");

        // JPA 는 엔티티를 영속성 컨텍스트에 보관할 때, 최초 상태를 복사해서 저장한다(스냅샷).
        entityManager.persist(customer); // 스냅샷 저장.
        transaction.commit();

        // 새 트랜잭션을 시작해서 변경감지가 발생하는지 확인
        transaction.begin();
        customer.setFirstName("Changed Seonho");
        customer.setLastName("Changed Kim");

        // 플러시 시점에 스냅샷과 엔티티를 비교해서 변경된 엔티티를 찾는다.
        // 스냅샷과 비교하여 변경점이 있다면, 업데이트 쿼리를 수행한다.
        // 변경감지는 영속성 컨텍스트가 관리하는 '영속 상태의 엔티티'에 대해서만 적용된다.
        transaction.commit();
    }

    @Test
    @DisplayName("삭제")
    void delete() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("Seonho");
        customer.setLastName("Kim");

        entityManager.persist(customer); // 스냅샷 저장
        transaction.commit();

        // EntityManager - remove()
        transaction.begin();
        entityManager.remove(customer);
        transaction.commit(); // flush -> DELETE Query
    }
}
