package dev.daanh.zombie.domain.survivor;

import dev.daanh.zombie.domain.survivor.enums.SkillType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Embeddable
@Getter
@Setter
public class SkillsProfile {
    @ElementCollection
    private Map<SkillType, Skill> skills = new HashMap<>();

    public void addXp(SkillType type, int amount) {
        skills.putIfAbsent(type, new Skill());
        skills.get(type).addXp(amount);
    }
}
