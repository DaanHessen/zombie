package dev.daanh.zombie.domain.survivor;

import dev.daanh.zombie.domain.core.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Memory extends BaseEntity {
    private String eventType;
    private String targetName;
    private long tickTimestamp;
    private int intensity;
    private boolean permanent;

    public void delete() {
        if (this.permanent) { return; }

        super.setDeleted(true);
    }
}
