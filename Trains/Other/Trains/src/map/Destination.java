package map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import strategy.Pair;

import java.util.Arrays;
import java.util.List;

/**
 * Class to represent a TrainsMap.Destination in TrainsMap.Trains
 * TrainsMap.Destination - a pair of cities on the game map, not necessary
 * connected with a direct connection but guaranteed to be connected by a path.
 */
public class Destination implements Comparable {
    private final City city0;
    private final City city1;

    /**
     * @param c1 one of the cities in the destination pair
     * @param c2 the other city in the destination pair
     */
    public Destination(City c1, City c2) {
        if (c1.equals(c2)) {
            throw new IllegalArgumentException(
                    "Cities in a destination cannot be equal.");
        }
        if (c1.getName().compareTo(c2.getName()) <= 0) {
            this.city0 = c1;
            this.city1 = c2;
        } else {
            this.city0 = c2;
            this.city1 = c1;
        }
    }

    /**
     * Constructor to create Destination given the names of cities for use with
     * thew testing tasks. Creates cities at .1, .1
     */
    public static Destination createDestinationFromCityNames(String c1Name,
                                                             String c2Name) {
        City c1 = new City(c1Name, new Coord(.1f, .1f));
        City c2 = new City(c2Name, new Coord(.9f, .9f));
        return new Destination(c1, c2);
    }

    /**
     * @return a copy of the HashSet of this destination's cities, currently two
     */
    public Pair<City> getVertices() {
        return new Pair<>(city0, city1);
    }

    /**
     * Used to print this TrainsMap.Destination.
     *
     * @return a String representing this TrainsMap.Destination
     */
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Destination.cities: \n");

        result.append(this.city0 + "\n");
        result.append(this.city1 + "\n");

        return result.toString();
    }

    /**
     * This function was overriden because we use a Set of destinations in our
     * getFeasibleDestinations in TrainsMap.TrainsMap. This will produce the
     * same hashcode for a TrainsMap.Destination where the cities are switched.
     *
     * @return int - hashCode of this TrainsMap.Destination
     */
    @Override
    public int hashCode() {
        int result = 7;

        result = result * this.city0.hashCode();
        result = result * this.city1.hashCode();

        return result;
    }

    /**
     * Returns true if this object is equal to the input object.
     * TrainsMap.Destination SF -> LA is the same as TrainsMap.Destination LA ->
     * SF
     *
     * @param o Other destination object
     *
     * @return if this object is equal to other
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Destination)) {
            return false;
        }
        Destination other = (Destination) o;
        boolean result = true;
        Pair<City> otherCities = other.getVertices();
        result = result && (otherCities.getFirst().getName().equals(this.city0.getName()) ||
                            otherCities.getFirst().getName().equals(this.city1.getName()));
        result = result && (otherCities.getSecond().getName().equals(this.city0.getName()) ||
                            otherCities.getSecond().getName().equals(this.city1.getName()));

        return result;
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * @param o the object to be compared.
     *
     * @return a negative integer, zero, or a positive integer as this object is
     * less than, equal to, or greater than the specified object.
     *
     * @throws IllegalArgumentException if the given object is not an instance
     *                                  of Destination
     */
    @Override
    public int compareTo(Object o) {
        if (!(o instanceof Destination)) {
            throw new IllegalArgumentException(
                    "Unable to compare non Destination Object to Destination");
        }
        Destination d = (Destination) o;
        if (this.equals(d)) {
            return 0;
        }
        Pair<City> otherPairCities = d.getVertices();
        if (this.city0
                    .getName()
                    .compareTo(otherPairCities.getFirst().getName()) < 0) {
            return -1;
        } else if (this.city0
                           .getName()
                           .compareTo(otherPairCities.getFirst().getName()) ==
                   0 && this.city1
                                .getName()
                                .compareTo(
                                        otherPairCities.getSecond().getName()) <
                        0) {
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * Method to return representation of Destination as JSON. Destination is a
     * two-element array of the city names.
     *
     * @return string representing JSON array of city names in this destination.
     */
    public String toJSON() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        List cityNames =
                Arrays.asList(this.city0.getName(), this.city1.getName());
        String ret = objectMapper.writeValueAsString(cityNames);
        return ret;
    }
}
