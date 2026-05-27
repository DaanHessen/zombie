package dev.daanh.zombie.domain.world;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoundingBox {
    private double minLatitude;
    private double maxLatitude;
    private double minLongitude;
    private double maxLongitude;

    public static BoundingBox fromCenterAndRadius(Coordinates center, double radiusDegrees) {
        return new BoundingBox(
                center.getLatitude() - radiusDegrees,
                center.getLatitude() + radiusDegrees,
                center.getLongitude() - radiusDegrees,
                center.getLongitude() + radiusDegrees
        );
    }

    public boolean contains(Coordinates coordinates) {
        return coordinates.getLatitude() >= minLatitude &&
               coordinates.getLatitude() <= maxLatitude &&
               coordinates.getLongitude() >= minLongitude &&
               coordinates.getLongitude() <= maxLongitude;
    }
}
