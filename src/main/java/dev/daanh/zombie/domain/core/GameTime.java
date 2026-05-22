package dev.daanh.zombie.domain.core;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameTime {
    private static final Instant START_TIME =  Instant.parse("2026-01-01T00:00:00Z");

    @Setter(AccessLevel.NONE)
    private long ticks;

    public GameTime plus(long ticks) {
        return new GameTime(this.ticks + ticks);
    }

    public Instant toGlobalInstant() {
        return START_TIME.plus(this.ticks, ChronoUnit.MINUTES);
    }

    public String getGlobalCalenderDate() {
        Instant instant = this.toGlobalInstant();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy");

        return formatter.format(instant.atZone(ZoneOffset.UTC));
    }
}
