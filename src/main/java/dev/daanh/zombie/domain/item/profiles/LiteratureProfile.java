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
public class LiteratureProfile implements ItemProfile {
    private String skillToImprove; // e.g., "MECHANICS", "COOKING"
    private int xpGranted;
    private int moraleBoost;
    private int readingTimeMinutes;
}
