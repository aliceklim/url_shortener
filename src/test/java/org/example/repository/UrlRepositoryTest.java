package org.example.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(SpringExtension.class)
class UrlRepositoryTest {
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
    private UrlRepository urlRepository;

    @PersistenceContext
    private EntityManager em;

    @BeforeEach
    void setupDb() {
        em.createNativeQuery("""
            CREATE TABLE IF NOT EXISTS url (
                id SERIAL PRIMARY KEY,
                hash VARCHAR(255) UNIQUE,
                url TEXT NOT NULL,
                created_at TIMESTAMP DEFAULT now()
            );
        """).executeUpdate();

        em.createNativeQuery("""
            CREATE TABLE IF NOT EXISTS hash (
                id SERIAL PRIMARY KEY,
                hash VARCHAR(255) UNIQUE NOT NULL
            );
        """).executeUpdate();
        em.flush();
    }

    @Test
    void testSaveAndFindUrl() {
        urlRepository.save("abc123", "https://example.com");

        Optional<String> result = urlRepository.findUrlByHash("abc123");

        assertTrue(result.isPresent());
        assertEquals("https://example.com", result.get());
    }

    @Test
    void testDeleteExpiredHashes() {
        em.createNativeQuery("INSERT INTO url (hash, url, created_at) VALUES ('oldhash', 'http://old.com', now() - interval '2 days')").executeUpdate();
        em.createNativeQuery("INSERT INTO url (hash, url, created_at) VALUES ('newhash', 'http://new.com', now())").executeUpdate();
        em.flush();

        LocalDateTime cutoff = LocalDateTime.now().minusDays(1);

        List<String> expiredHashes = urlRepository.deleteExpiredHashes(cutoff);

        assertEquals(1, expiredHashes.size());
        assertEquals("oldhash", expiredHashes.get(0));

        Optional<String> stillThere = urlRepository.findUrlByHash("newhash");
        assertTrue(stillThere.isPresent());

        Optional<String> gone = urlRepository.findUrlByHash("oldhash");
        assertFalse(gone.isPresent());
    }
}