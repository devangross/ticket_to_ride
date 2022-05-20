package map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DirectConnectionTest {
    @Test
    public void testShouldConstructValidDirectConnection() {
        City la = new City("LA", new Coord(.1f, .1f));
        City sf = new City("sf", new Coord(.9f, .9f));

        assertNotNull(new DirectConnection(la, sf, 4, ColorTrains.WHITE));
    }

    @Test
    public void testShouldGetBothCitiesFromDC() {
        City la = new City("LA", new Coord(.1f, .1f));
        City sf = new City("sf", new Coord(.9f, .9f));
        DirectConnection dc = new DirectConnection(la, sf, 4, ColorTrains.RED);
        assertEquals(dc.getCity0(), la);
        assertEquals(dc.getCity1(), sf);
    }

    @Test
    public void testShouldGetLengthOfDC() {
        City la = new City("LA", new Coord(.1f, .1f));
        City sf = new City("sf", new Coord(.9f, .9f));
        DirectConnection dc = new DirectConnection(la, sf, 4, ColorTrains.BLUE);
        assertEquals(dc.getLength(), 4);
    }

    // tests isValidLength
    @Test
    public void testShouldRejectInvalidLengthOnConstruction() {
        City la = new City("LA", new Coord(.1f, .1f));
        City sf = new City("sf", new Coord(.9f, .9f));
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new DirectConnection(la, sf, 0, ColorTrains.WHITE);
        });
    }

    // tests isValidLength
    @Test
    public void testShouldRejectNegativeLength() {
        City la = new City("LA", new Coord(.1f, .1f));
        City sf = new City("sf", new Coord(.9f, .9f));
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new DirectConnection(la, sf, -3, ColorTrains.GREEN);
        });
    }

    @Test
    public void testShouldEnsureEqualDCsAreEqual() {
        String cityName1 = "LA";
        String cityName2 = "SF";
        int length = 3;
        City laCity = new City(cityName1, new Coord(.4f, .8f));
        City sfCity = new City(cityName2, new Coord(.2f, .1f));

        DirectConnection LAToSF =
                new DirectConnection(laCity, sfCity, length, ColorTrains.BLUE);
        DirectConnection copyLAToSF =
                new DirectConnection(laCity, sfCity, length, ColorTrains.BLUE);
        assertTrue(LAToSF.equals(copyLAToSF));
    }

    @Test
    public void testShouldEnsureReverseOrderEqualDCsAreEqual() {
        String cityName1 = "LA";
        String cityName2 = "SF";
        int length = 3;
        City laCity = new City(cityName1, new Coord(.4f, .8f));
        City sfCity = new City(cityName2, new Coord(.2f, .1f));

        DirectConnection LAToSF =
                new DirectConnection(laCity, sfCity, length, ColorTrains.BLUE);
        DirectConnection copyLAToSF =
                new DirectConnection(sfCity, laCity, length, ColorTrains.BLUE);
        assertTrue(LAToSF.equals(copyLAToSF));
    }

    // Prevent direct connection to self
    @Test
    public void testShouldPreventCreatingDCFromCityToItself() {
        String cityName1 = "LA";
        int length = 3;
        City laCity = new City(cityName1, new Coord(.4f, .8f));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            DirectConnection LAToSF =
                    new DirectConnection(laCity, laCity, length,
                            ColorTrains.BLUE);
        });
    }

    @Test
    public void testShouldThrowComparisonWithNonDirectConnections() {
        City la = new City("LA", new Coord(.1f, .1f));
        City sf = new City("sf", new Coord(.9f, .9f));
        DirectConnection dc = new DirectConnection(la, sf, 4, ColorTrains.BLUE);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            dc.compareTo(la);
        });
    }

    @Test
    public void testShouldReturnZeroWhenConnectionComparedToItself() {
        City la = new City("LA", new Coord(.1f, .1f));
        City sf = new City("sf", new Coord(.9f, .9f));
        DirectConnection dc = new DirectConnection(la, sf, 4, ColorTrains.BLUE);
        assertEquals(0, dc.compareTo(dc));
    }

    @Test
    public void testShouldCompareFirstTwoCityNames() {
        City la = new City("LA", new Coord(.1f, .1f));
        City sf = new City("sf", new Coord(.9f, .9f));
        DirectConnection dc1 =
                new DirectConnection(la, sf, 4, ColorTrains.BLUE);
        City sj = new City("SJ", new Coord(.2f, .3f));
        City oak = new City("OAK", new Coord(.4f, .3f));
        DirectConnection dc2 =
                new DirectConnection(sj, oak, 3, ColorTrains.RED);
        assertEquals(-1, dc1.compareTo(dc2));
        assertEquals(1, dc2.compareTo(dc1));
    }

    @Test
    public void testShouldCompareSecondCitiesAfterEqualFirstCities() {
        City la = new City("LA", new Coord(.1f, .1f));
        City sf = new City("SF", new Coord(.9f, .9f));
        DirectConnection dc1 =
                new DirectConnection(la, sf, 4, ColorTrains.BLUE);
        City sj = new City("SJ", new Coord(.2f, .3f));
        DirectConnection dc2 = new DirectConnection(sj, la, 3, ColorTrains.RED);
        assertEquals(-1, dc1.compareTo(dc2));
        assertEquals(1, dc2.compareTo(dc1));
    }

    @Test
    public void testShouldCompareSegmentLengthWithEqualCityNames() {
        City la = new City("LA", new Coord(.1f, .1f));
        City sf = new City("SF", new Coord(.9f, .9f));
        DirectConnection dc1 =
                new DirectConnection(la, sf, 3, ColorTrains.BLUE);
        DirectConnection dc2 = new DirectConnection(sf, la, 4, ColorTrains.RED);
        assertEquals(-1, dc1.compareTo(dc2));
        assertEquals(1, dc2.compareTo(dc1));
    }

    @Test
    public void testShouldCompareColorWithEqualCityNamesAndLength() {
        City la = new City("LA", new Coord(.1f, .1f));
        City sf = new City("SF", new Coord(.9f, .9f));
        DirectConnection dc1 =
                new DirectConnection(la, sf, 3, ColorTrains.BLUE);
        DirectConnection dc2 = new DirectConnection(sf, la, 3, ColorTrains.RED);
        assertEquals(-1, dc1.compareTo(dc2));
        assertEquals(1, dc2.compareTo(dc1));
    }
}
