package dev.daanh.zombie.domain.item;

import dev.daanh.zombie.domain.core.BaseEntity;

import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemStack extends BaseEntity {

    @Column(name = "item_template_id", nullable = false)
    private UUID itemTemplateId;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    @Builder.Default
    private double durability = 1.0;
    //TODO: implement durability

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata = new HashMap<>();

    
}
