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
public class WeaponProfile implements ItemProfile {
    private int baseDamage;
    private int criticalChance;
    private double attackSpeed;
    private int maxRange;
    private String ammoType;
    private boolean isMelee;
    private int noiseLevel;
}
