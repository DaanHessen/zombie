package dev.daanh.zombie.domain.item.profiles;

import dev.daanh.zombie.domain.item.ItemProfile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AmmunitionProfile implements ItemProfile {
    private String ammoType; // e.g., "9MM", "12GAUGE"
    private int damageModifier; // Can add extra damage (e.g. hollow point)
}
