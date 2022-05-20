package map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import strategy.Pair;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Class to run tests on the TrainsMap.Destination class
 */
public class DestinationTest {
    @Test
    public void testShouldConstructValidDestination() {
        City la = new City("LA", new Coord(.1f, .1f));
        City sf = new City("sf", new Coord(.9f, .9f));
        Destination routeOne = new Destination(la, sf);
        assertNotNull(routeOne);
    }

    @Test
    public void testShouldGetTwoCities() {
        City la = new City("LA", new Coord(.1f, .1f));
        City sf = new City("sf", new Coord(.9f, .9f));
        Destination routeOne = new Destination(la, sf);
        Pair<City> vertices = routeOne.getVertices();
        assertTrue(vertices.getFirst().equals(la));
    }

    @Test
    public void testShouldReturnHashCodeOfDestination() {
        City la = new City("LA", new Coord(.1f, .1f));
        City sf = new City("sf", new Coord(.9f, .9f));
        Destination routeOne = new Destination(la, sf);
        assertNotNull(routeOne.hashCode());
    }

    @Test
    public void testShouldDetectEqualDestinations() {
        City la = new City("LA", new Coord(.1f, .1f));
        City sf = new City("sf", new Coord(.9f, .9f));
        Destination routeOne = new Destination(la, sf);
        Destination routeTwo = new Destination(la, sf);
        assertEquals(routeOne, routeTwo);
    }

    @Test
    public void testShouldDetectEqualDestinationsAlt() {
        City la = new City("LA", new Coord(.1f, .1f));
        City sf = new City("sf", new Coord(.9f, .9f));
        City laWithDifferentID = new City("LA", new Coord(.1f, .1f));

        Destination routeOne = new Destination(la, sf);
        Destination routeTwo = new Destination(laWithDifferentID, sf);
        assertEquals(routeOne, routeTwo);
    }

    @Test
    public void testShouldThrowIfCompareNonDestination() {
        City la = new City("LA", new Coord(.1f, .1f));
        City sf = new City("sf", new Coord(.9f, .9f));
        Destination d1 = new Destination(la, sf);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            d1.compareTo(la);
        });
    }

    @Test
    public void testShouldReturnZeroIfSameDestinationCompared() {
        City la = new City("LA", new Coord(.1f, .1f));
        City sf = new City("sf", new Coord(.9f, .9f));
        Destination d1 = new Destination(la, sf);
        assertEquals(0, d1.compareTo(d1));
    }

    @Test
    public void testShouldCompareDiffFirstDestinations() {
        City la = new City("LA", new Coord(.1f, .1f));
        City sf = new City("sf", new Coord(.9f, .9f));
        Destination d1 = new Destination(la, sf);
        City sj = new City("SJ", new Coord(.2f, .3f));
        City oak = new City("OAK", new Coord(.4f, .3f));
        Destination d2 = new Destination(sj, oak);
        assertEquals(-1, d1.compareTo(d2));
        assertEquals(1, d2.compareTo(d1));
    }

    @Test
    public void testShouldCompareSameFirstDiffSecondDestinations() {
        City la = new City("LA", new Coord(.1f, .1f));
        City sf = new City("sf", new Coord(.9f, .9f));
        Destination d1 = new Destination(la, sf);
        City sj = new City("SJ", new Coord(.2f, .3f));
        Destination d2 = new Destination(sj, la);
        assertEquals(1, d1.compareTo(d2));
        assertEquals(-1, d2.compareTo(d1));
    }

}
