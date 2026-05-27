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
public class ToolProfile implements ItemProfile {
    private int toolLevel;
    private boolean canChopWood;
    private boolean canLockpick;
    private boolean canDig;
    private boolean canDismantle;
}
