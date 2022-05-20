package state;

import map.ColorTrains;

/**
 * Class to represent Colored Card objects in the game state
 */
public class ColorCard {
    private final ColorTrains color;

    /**
     * Maine constructor for a Colored card, ensures not null
     */
    public ColorCard(ColorTrains ct) {
        if (ct != null) {
            this.color = ct;
        } else {
            throw new IllegalArgumentException();
        }

    }

    /**
     * Getter to get this ColorCard's color as ColorTrains
     */
    public ColorTrains getColor() {
        return color;
    }

    @Override
    public String toString() {
        return String.valueOf(this.color);
    }
}
