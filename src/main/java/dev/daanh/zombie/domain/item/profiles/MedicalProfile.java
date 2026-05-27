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
public class MedicalProfile implements ItemProfile {
    private int healAmount;
    private boolean stopsBleeding;
    private boolean curesInfection;
    private boolean providesPainRelief;
    private int applicationTimeMinutes;
}
