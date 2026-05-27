package dev.daanh.zombie.domain.person.mind;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class NeedsProfile {
    private int hunger;
    private int thirst;
    private int energy;
    private int stamina;
    private int calories;
    private int protein;
    private int carbs;

    // TODO: maybe in the future make something less linear
    public void consumeDrink(int amount) {
        this.thirst = Math.max(0, this.thirst - amount);
    }

    public void increaseThirst(int amount) {
        this.thirst = Math.min(100, this.thirst + amount);
    }

    public void consumeFood(int amount, int protein, int carbs, int calories) {
        this.hunger = Math.max(0, this.hunger - amount);
        this.protein = Math.min(100, this.protein + protein);
        this.carbs = Math.min(100, this.carbs + carbs);
        this.calories = Math.min(3000, this.calories + calories);
    }

    public void increaseHunger(int amount) {
        this.hunger = Math.min(100, this.hunger + amount);
    }

    public void sleep(int ticks) {
        this.energy = Math.min(100, this.energy + (ticks));
    }

    public void decreaseEnergy(int amount) {
        this.energy = Math.max(0, this.energy - amount);
    }
}
