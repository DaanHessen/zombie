package dev.daanh.zombie.repository;

import dev.daanh.zombie.domain.item.ItemStack;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ItemInstanceRepository extends JpaRepository<ItemStack, UUID> {
}
