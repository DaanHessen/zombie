package dev.daanh.zombie.repository;

import dev.daanh.zombie.application.dto.response.WorldStatsResponse;
import dev.daanh.zombie.domain.world.World;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WorldRepository extends JpaRepository<World, UUID> {

    @Query(value = """
        SELECT new dev.daanh.zombie.application.dto.response.WorldStatsResponse(
            (SELECT COUNT(c) FROM Continent c),
            (SELECT COUNT(co) FROM Country co),
            (SELECT COUNT(r) FROM Region r),
            (SELECT COUNT(s) FROM Settlement s),
            (SELECT COUNT(w) FROM WaterBody w)
        )
    """)
    WorldStatsResponse getWorldStats();
}
