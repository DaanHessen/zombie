package dev.daanh.zombie.domain.person.identity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Skill {
    private int level = 0;
    private int currentXp = 0;
    private int xpToNextLevel = 100;

    public void addXp(int amount) {
        this.currentXp += amount;
        while (this.currentXp >= this.xpToNextLevel) {
            this.level++;
            this.currentXp -= this.xpToNextLevel;
            // Simple scaling curve: each level requires 50 more XP than the last
            this.xpToNextLevel += 50; 
        }
    }
}
