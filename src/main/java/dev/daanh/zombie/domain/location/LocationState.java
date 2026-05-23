package dev.daanh.zombie.domain.location;

import dev.daanh.zombie.domain.core.BaseState;
import dev.daanh.zombie.domain.location.enums.LocationCondition;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class LocationState extends BaseState {

    @ManyToOne(fetch = FetchType.LAZY)
    private Location location;

    @Enumerated(EnumType.STRING)
    private LocationCondition condition;

    private boolean enterable;

    private boolean searchable;

    public void search() {
        if (!this.searchable) { return; }

        this.searchable = false;
    }

    public void degradeCondition() {
        if (this.condition == null) { return; }

        if (this.condition == LocationCondition.INTACT) {
            this.condition = LocationCondition.WORN;
        } else if (this.condition == LocationCondition.WORN) {
            this.condition = LocationCondition.DAMAGED;
        }
    }
}
