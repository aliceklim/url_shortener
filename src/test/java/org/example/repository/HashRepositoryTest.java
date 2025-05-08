package org.example.repository;

import org.example.entity.Hash;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(SpringExtension.class)
class HashRepositoryTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private HashRepository hashRepository;

    @PersistenceContext
    private EntityManager em;

    private Hash hash1;
    private Hash hash2;
    private Hash hash3;
    private Hash hash4;

    @BeforeEach
    void setUp() {
        hash1 = Hash.builder().hash("abc").build();
        hash2 = Hash.builder().hash("def").build();
        hash3 = Hash.builder().hash("ghi").build();
        hash4 = Hash.builder().hash("jkl").build();
    }

    @BeforeEach
    void setupSchema() {
        em.createNativeQuery("CREATE SEQUENCE IF NOT EXISTS unique_number_seq START 1;").executeUpdate();
        em.createNativeQuery("""
            CREATE TABLE IF NOT EXISTS hash (
                id SERIAL PRIMARY KEY,
                hash VARCHAR(255) UNIQUE NOT NULL
            );
        """).executeUpdate();
        em.flush();
    }

    @Test
    void testGetUniqueNumbers() {
        Set<Long> numbers = hashRepository.getUniqueNumbers(5L);

        assertNotNull(numbers);
        assertEquals(5, numbers.size());
        assertTrue(numbers.stream().allMatch(n -> n > 0));
    }

    @Test
    void testGetHashAndDelete() {
        hashRepository.save(hash1);
        hashRepository.save(hash2);
        hashRepository.save(hash3);
        hashRepository.save(hash4);

        List<String> deleted = hashRepository.getHashAndDelete(2);

        assertEquals(2, deleted.size());
        List<Hash> remaining = hashRepository.findAll();
        assertEquals(2, remaining.size());
    }
}
