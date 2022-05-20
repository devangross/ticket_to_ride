package map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Class to run tests on the TrainsMap.Coord class
 */
public class CoordTest {
    @Test
    public void testShouldConstructValidCoord() {
        assertNotNull(new Coord(.1f, .2f));
    }

    @Test
    public void testShouldConstructValidCoordOne() {
        assertNotNull(new Coord(1, 1));
    }

    @Test
    public void testShouldConstructValidCoordZero() {
        assertNotNull(new Coord(0, 0));
    }

    @Test
    public void testShouldGetXCoord() {
        Coord c = new Coord(.1f, .2f);
        assertEquals(c.getX(), .1f);
    }

    @Test
    public void testShouldGetYCoord() {
        Coord c = new Coord(.1f, .2f);
        assertEquals(c.getY(), .2f);
    }

    @Test
    public void testShouldBeEqualCoords() {
        Coord c1 = new Coord(.1f, .1f);
        Coord c2 = new Coord(.1f, .1f);
        assertTrue(c1.equals(c2));
    }

    @Test
    public void testShouldBeInEqualCoords() {
        Coord c1 = new Coord(.1f, .11f);
        Coord c2 = new Coord(.1f, .1f);
        assertFalse(c1.equals(c2));
    }
}
