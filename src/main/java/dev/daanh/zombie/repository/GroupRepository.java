package dev.daanh.zombie.repository;

import dev.daanh.zombie.domain.person.social.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID> {
}
