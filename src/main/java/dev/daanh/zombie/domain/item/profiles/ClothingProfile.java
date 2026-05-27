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
public class ClothingProfile implements ItemProfile {
    private String slotType; // e.g., "HEAD", "TORSO", "LEGS"
    private int defenseBite;
    private int defenseScratch;
    private int warmth;
    private int windResistance;
    private int waterproofness;
}
