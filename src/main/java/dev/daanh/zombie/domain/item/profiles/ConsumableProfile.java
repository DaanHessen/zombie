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
public class ConsumableProfile implements ItemProfile {
    private int calories;
    private int protein;
    private int carbs;
    private int hydration;
    private int energy;
}
