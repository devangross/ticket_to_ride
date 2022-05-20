package map;

import java.awt.*;

/**
 * Represents the color of an edge on the game board They are assigned one of
 * TrainsMap.Color.RED, TrainsMap.Color.GREEN, TrainsMap.Color.BLUE,
 * TrainsMap.Color.WHITE
 */
public enum ColorTrains {
    RED, GREEN, BLUE, WHITE;

    public static Color getAWTColor(ColorTrains enumColor) {
        Color awtColor;
        switch (enumColor) {
            case RED:
                awtColor = Color.red;
                break;
            case GREEN:
                awtColor = Color.green;
                break;
            case BLUE:
                awtColor = Color.blue;
                break;
            case WHITE:
                awtColor = Color.white;
                break;
            default:
                throw new IllegalStateException(
                        "Unexpected value: " + enumColor);
        }
        return awtColor;
    }

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}


