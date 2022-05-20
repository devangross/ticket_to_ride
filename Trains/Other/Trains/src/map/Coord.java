package map;

/**
 * Coordinate representing a location in 2 dimensions as (float, float) Provides
 * functions to construct a TrainsMap.Coord and getX or getY Fields are final
 */
public class Coord {
    private final float x;
    private final float y;

    public Coord(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }

    @Override
    public boolean equals(Object b) {
        if (b instanceof Coord) {
            Coord c = (Coord) b;
            return Math.abs(this.x - c.x) < 0.001f && Math.abs(this.y - c.y) < 0.001f;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Math.round(this.x * this.y * 11);
    }
}
