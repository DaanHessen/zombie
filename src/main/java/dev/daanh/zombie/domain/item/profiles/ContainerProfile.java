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
public class ContainerProfile implements ItemProfile {
    private int bonusWeightCapacityGrams;
    private double bonusVolumeCapacityLitres;
    private int weightReductionPercentage; // e.g., 20 means items inside weigh 20% less
}
