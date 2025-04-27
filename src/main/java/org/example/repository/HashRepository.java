package org.example.repository;

import org.example.entity.Hash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface HashRepository extends JpaRepository<Hash, Long> {

    @Query(nativeQuery = true, value = """
            SELECT nextval('unique_number_seq') FROM generate_series(1, ?)
            """)
    public Set<Long> getUniqueNumbers(Long max);

    @Query(nativeQuery = true, value = """
            DELETE FROM hash
            WHERE hash IN (
                SELECT hash FROM hash
                ORDER BY RANDOM()
                LIMIT ?
            )
            RETURNING *
            """)
    public List<Hash> getHashAndDelete(long amount);
}
