package map;

import java.util.Arrays;

/**
 * Class representing a direct connection, two cities directly connected by a
 * path. Provides a constructor which takes two TrainsMap.City objects, a length
 * and a color
 */
public class DirectConnection implements Comparable {

    private final City city0;
    private final City city1;
    private final int length;
    private final ColorTrains connectionColor;

    /**
     * Constructor to make direct connection with two cities and a length.
     *
     * @param a      a city in this direct connection
     * @param b      the other city in this direct connection
     * @param length the length of the edge from TrainsMap.City a to
     *               TrainsMap.City b
     */
    public DirectConnection(City a, City b, int length,
                            ColorTrains colorTrains) {
        if (!a.equals(b) && isValidLength(length)) {
            if (a.getName().compareTo(b.getName()) <=
                0) { //ensure lexicographic ordering of cities with creation
                // of connections
                this.city0 = a;
                this.city1 = b;
            } else {
                this.city0 = b;
                this.city1 = a;
            }
            this.length = length;
            this.connectionColor = colorTrains;
        } else {
            throw new IllegalArgumentException(
                    "Invalid arguments, cities cannot be equal" +
                    " and 3 >= length <= 5");
        }
    }

    /**
     * Constructor to create Destination given the names of cities for use with
     * thew testing tasks. Creates cities at .1, .1
     */
    public static DirectConnection createDirectConnectionFromCityNames(
            String c1Name, String c2Name, int length, ColorTrains color) {
        City c1 = new City(c1Name, new Coord(.1f, .1f));
        City c2 = new City(c2Name, new Coord(.9f, .9f));
        return new DirectConnection(c1, c2, length, color);
    }

    /**
     * Getter method for the first city in the connection.
     *
     * @return City object
     */
    public City getCity0() {
        return city0;
    }

    /**
     * Getter method for the second city in the connection.
     *
     * @return City object
     */
    public City getCity1() {
        return city1;
    }

    /**
     * getter method for length.
     *
     * @return the length of the TrainsMap.DirectConnection
     */
    public int getLength() {
        return this.length;
    }

    /**
     * Getter method for the TrainsMap.Color.
     *
     * @return the TrainsMap.Color of the TrainsMap.DirectConnection
     */
    public ColorTrains getColor() {
        return this.connectionColor;
    }

    /**
     * Helper method to determine if the given length is valid, in {3,4,5}
     *
     * @param in value passed into constructor of TrainsMap.DirectConnection
     *
     * @return true if in is on of {3,4,5}, else false
     */
    private boolean isValidLength(int in) {
        return 3 <= in && in <= 5;
    }

    /**
     * Function to generate a string representing this direct connection
     *
     * @return String
     */
    public String toString() {
        return String.format("[%s %s %s %d]", city0.getName(), city1.getName(), this.getColor(), this.length);
    }

    public String toJSON() {
        String[] retArr = new String[] {"\"" + this.city0.getName() + "\"",
                "\"" + this.city1.getName() + "\"",
                "\"" + this.getColor().toString().toLowerCase() + "\"",
                String.valueOf(this.getLength())};
        return Arrays.toString(retArr);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DirectConnection) {
            DirectConnection other = (DirectConnection) o;
            boolean result = true;
            DirectConnection otherDirectConnection = (DirectConnection) o;
            City thisCity0 = this.city0;
            City thisCity1 = this.city1;
            City otherCity0 = other.getCity0();
            City otherCity1 = other.getCity1();

            result = result && (thisCity0.equals(otherCity0) ||
                                thisCity0.equals(otherCity1));
            result = result && (thisCity1.equals(otherCity0) ||
                                thisCity1.equals(otherCity1));
            result = result && this.length == otherDirectConnection.getLength();
            result = result && this.connectionColor.equals(
                    otherDirectConnection.getColor());
            return result;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 7 * city0.hashCode() * city1.hashCode();
        result = result * this.length * this.connectionColor.hashCode();
        return result;
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object. First compares
     * city0 names, then city1 names, then segment number, then string
     * representation of color.
     *
     * @param o the object to be compared.
     *
     * @return a negative integer, zero, or a positive integer as this object is
     * less than, equal to, or greater than the specified object.
     *
     * @throws IllegalArgumentException if compared to an object that is not a
     *                                  DirectConnection
     */
    @Override
    public int compareTo(Object o) {
        if (!(o instanceof DirectConnection)) {
            throw new IllegalArgumentException(
                    "Unable to compare non Destination Object to Destination");
        }
        DirectConnection d = (DirectConnection) o;
        if (this.equals(d)) {
            return 0;
        }
        if (this.city0.getName().compareTo(d.getCity0().getName()) < 0) {
            return -1;
        } else if (
                this.city0.getName().compareTo(d.getCity0().getName()) == 0 &&
                this.city1.getName().compareTo(d.getCity1().getName()) < 0) {
            return -1;
        } else if (
                this.city0.getName().compareTo(d.getCity0().getName()) == 0 &&
                this.city1.getName().compareTo(d.getCity1().getName()) == 0) {
            if (this.getLength() == d.getLength()) {
                if (this.getColor().name().compareTo(d.getColor().name()) < 0) {
                    return -1;
                }
                return 1;
            } else {
                return Integer.compare(this.getLength(), d.getLength());
            }
        } else {
            return 1;
        }
    }
}
