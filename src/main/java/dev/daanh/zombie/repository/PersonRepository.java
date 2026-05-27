package dev.daanh.zombie.repository;

import dev.daanh.zombie.domain.person.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PersonRepository extends JpaRepository<Person, UUID> {
    List<Person> findByWorldIdAndDeletedFalse(UUID worldId);

    @Query("SELECT p FROM Person p WHERE p.worldId = :worldId AND p.status = 'ALIVE' AND p.deleted = false")
    List<Person> findAliveInWorld(@Param("worldId") UUID worldId);
}
