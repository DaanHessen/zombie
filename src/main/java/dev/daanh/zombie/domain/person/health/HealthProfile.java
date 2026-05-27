package dev.daanh.zombie.domain.person.health;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Embeddable
@Getter
@Setter
public class HealthProfile {
    private int health;
    private int bloodVolume;
    private int pain;
    private int fatigue;
    private int sanity;
    private int morale;
    private int mood;

    @ElementCollection
    private List<BodyPartStatus> bodyParts;

    // like ticks as in amount to loose per hour / per tick
    public void reduceBloodVolume(int ticks) {
        this.bloodVolume = Math.max(0, this.bloodVolume - ticks);
    }

    // now it's in amounts so not ticks I guess
    public void increaseBloodVolume(int amount) {
        this.bloodVolume = Math.min(100, this.bloodVolume + amount);
    }

    public void increaseHealth(int amount) {
        this.health = Math.min(100, this.health + amount);
    }

    public void decreaseHealth(int amount) {
        this.health = Math.max(0, this.health - amount);
    }

    public void increaseSanity(int amount) {
        this.sanity = Math.min(100, this.sanity + amount);
    }

    public void decreaseSanity(int amount) {
        this.sanity = Math.max(0, this.sanity - amount);
    }

    public void increaseFatigue(int amount) {
        this.fatigue = Math.min(100, this.fatigue + amount);
    }

    public void decreaseFatigue(int amount) {
        this.fatigue = Math.max(0, this.fatigue - amount);
    }

    public void increaseMood(int amount) {
        this.mood = Math.min(100, this.mood + amount);
    }

    public void decreaseMood(int amount) {
        this.mood = Math.max(0, this.mood - amount);
    }

    public void increaseMorale(int amount) {
        this.morale = Math.min(100, this.morale + amount);
    }

    public void decreaseMorale(int amount) {
        this.morale = Math.max(0, this.morale - amount);
    }

    public void increasePain(int amount) {
        this.pain = Math.min(100, this.pain + amount);
    }

    public void decreasePain(int amount) {
        this.pain = Math.max(0, this.pain - amount);
    }

    // all of these might be redundant because of @setter and @getter but because we have Math.min / max it might be good right I DONT FUCKING KNOW
}
