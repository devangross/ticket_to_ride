package map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Class to run tests on the TrainsMap.City class
 */
public class CityTest {
    @Test
    public void testShouldConstructValidCity() {
        assertNotNull(new City("LA", new Coord(.1f, .1f)));
    }

    @Test
    public void testShouldGetCityName() {
        City city = new City("LA", new Coord(.1f, .1f));
        assertEquals(city.getName(), "LA");
    }

    @Test
    public void testShouldGetCityLocation() {
        City city = new City("LA", new Coord(.1f, .2f));
        assertEquals(city.getLocation().getX(), .1f);
        assertEquals(city.getLocation().getY(), .2f);
    }

    // test that tests .equals for the same object
    @Test
    public void testShouldReturnEqualObjects() {
        City la1 = new City("LA", new Coord(.1f, .2f));
        City la2 = new City("LA", new Coord(.1f, .2f));

        assertTrue(la1.equals(la2));
    }

    @Test
    public void testShouldGenerateEquivalentHashForSameObj() {
        City city = new City("LA", new Coord(.1f, .2f));
        assertEquals(city.hashCode(), city.hashCode());
    }

    @Test
    public void testShouldProduceDifferentIDWhenTwoCitiesSameName() {
        City city1 = new City("LA", new Coord(.1f, .2f));
        City city2 = new City("LA", new Coord(.1f, .6f));

        assertNotEquals(city1, city2);
    }

    // TrainsMap.City should throw an illegal argument exception when name is
    // null
    @Test
    public void testShouldFailToConstructWithNullName() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            City c = new City(null, new Coord(.1f, .2f));
        });
    }

    @Test
    public void testShouldFailToConstructWithInvalidCoord() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            City c = new City("Alb", new Coord(1.2f, .3f));
        });
    }

    // testing city name regex
    @Test
    public void testShouldConstructValidCityAllSpaces() {
        assertNotNull(
                new City("                         ", new Coord(.1f, .1f)));
    }

    @Test
    public void testShouldConstructValidCityAllPeriods() {
        assertNotNull(
                new City(".........................", new Coord(.1f, .1f)));
    }

    @Test
    public void testShouldConstructValidCityRandomString() {
        assertNotNull(new City("jfkdn781. o w dfg8skw", new Coord(.1f, .1f)));
    }

    @Test
    public void testShouldThrowWhenCityName26Spaces() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            City c =
                    new City("                          ", new Coord(.1f, .1f));
        });
    }

}
