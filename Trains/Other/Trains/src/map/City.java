package map;

/**
 * Class to represent a TrainsMap.City on the TrainsMap.Trains gameboard
 * Provides functions to get String name, TrainsMap.Coord location
 * <p>
 * TrainsMap.Coord coord field represents coordinates of this city relative to
 * the gameboard as [0-1],[0-1] Equality is determined by the string name and
 * coordinate, relies on the assumption that a city is uniquely identified by
 * the name and coordinate
 */
public class City implements Comparable {
    private final String name;
    private final Coord coord;

    /**
     * @param name  String name of city
     * @param coord Coordinate representing location of city on the gameboard as
     *              scalars [0-1],[0-1]
     */
    public City(String name, Coord coord) {
        if (name == null || !isValidCityName(name)) {
            throw new IllegalArgumentException(
                    "City name cannot be null, must satisfy the " +
                    "regular expression \"[a-zA-Z0-9\\\\ \\\\.\\\\,]+\" and " +
                    "has at most 25 ASCII characters");
        }
        if (!isValidCoord(coord)) {
            throw new IllegalArgumentException(
                    "TrainsMap.Coord must contain values between 0 and 1 " +
                    "inclusive");
        }
        this.name = name;
        this.coord = coord;
    }

    public String getName() {
        return this.name;
    }

    public Coord getLocation() {
        return new Coord(this.coord.getX(), this.coord.getY());
    }

    @Override
    public String toString() {
        return "City.name: " + this.name + " City.coord: " +
               this.coord.toString() + "\n";
    }

    /**
     * Overridden equals, two TrainsMap.City are equal if they have the same
     * name and coord -> assumes cities are unqiuely identified by their name +
     * coord
     *
     * @param o object to test equality
     *
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof City) {
            City c = (City) o;
            return this.name.equals(c.name) && this.coord.equals(c.coord);
        }
        return false;
    }

    /**
     * Hashcode override as we also override equals, assumes TrainsMap.City
     * uniquely identified by name and coord
     *
     * @return integer hashcde to identify TrainsMap.City
     */
    @Override
    public int hashCode() {
        return 7 * name.hashCode() * coord.hashCode();
    }

    private boolean isValidCoord(Coord c) {
        float x = c.getX();
        float y = c.getY();
        return x >= 0f && x <= 1f && y >= 0f && y <= 1f;
    }

    /**
     * city’s Name satisfies the regular expression "[a-zA-Z0-9\\ \\.\\,]+" and
     * has at most 25 ASCII characters
     */
    private boolean isValidCityName(String s) {
        return s.matches("[a-zA-Z0-9\\ \\.\\,]+") && s.length() <= 25;
    }

    /**
     * Returns a copy of this city for purposes of adding to direct
     * connections.
     */
    public City getCityCopy() {
        return new City(this.name, this.coord);
    }

    /**
     * Convenience compareTo to order Lists of cities.
     * TODO: Assumes unique city names
     */
    @Override
    public int compareTo(Object o) {
        if (!(o instanceof City)) {
            throw new IllegalArgumentException(
                    "CompareTo only applies to two City objects");
        }
        City otherCity = (City) o;
        return this.getName().compareTo(otherCity.getName());
    }
}
