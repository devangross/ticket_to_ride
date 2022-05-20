package map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import strategy.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TrainsMapTest {

    @Test
    public void testShouldConstructTrainsMapFromObjects() {
        City la = new City("LA", new Coord(.1f, .1f));
        City sf = new City("sf", new Coord(.9f, .9f));
        DirectConnection c1 =
                new DirectConnection(la, sf, 4, ColorTrains.WHITE);
        DirectConnection c2 =
                new DirectConnection(sf, la, 3, ColorTrains.GREEN);
        Set<City> cities = new HashSet<>(2);
        Set<DirectConnection> connections = new HashSet<>(2);
        cities.add(la);
        cities.add(sf);
        connections.add(c1);
        connections.add(c2);
        TrainsMap map =
                TrainsMap.createTrainsMapWithDefaultSize(cities, connections);
        assertNotNull(map);
    }

    //test verifies that TrainsMap.City objects used in the connection
    // attribute of the same TrainsMap.TrainsMap objects are equivalent
    @Test
    public void testShouldUseSameCityInDirectConnections() {
        City la = new City("LA", new Coord(.1f, .1f));
        City sf = new City("sf", new Coord(.9f, .9f));
        DirectConnection c1 =
                new DirectConnection(la, sf, 4, ColorTrains.WHITE);
        Set<City> cities = new HashSet<>(2);
        Set<DirectConnection> connections = new HashSet<>(2);
        cities.add(la);
        cities.add(sf);
        connections.add(c1);
        TrainsMap map =
                TrainsMap.createTrainsMapWithDefaultSize(cities, connections);
        Set<City> setCities = map.getCities();
        boolean test = true;
        for (DirectConnection d : map.getDirectConnections()) {
            assertTrue(setCities.contains(d.getCity0()) &&
                       setCities.contains(d.getCity1()));
        }
    }

    @Test
    public void testShouldGetCitiesInMap() {
        TrainsMap m = createExampleMap();
        Set<String> expected = new HashSet<>();
        expected.add("LA");
        expected.add("SF");
        expected.add("Sac");
        expected.add("SLO");

        for (City c : m.getCities()) {
            assertTrue(expected.contains(c.getName()));
        }
    }

    @Test
    public void testShouldGetCityNamesInMap() {
        TrainsMap m = createExampleMap();
        Set<String> expected = new HashSet<>();
        expected.add("LA");
        expected.add("SF");
        expected.add("Sac");
        expected.add("SLO");

        for (String name : m.getCityNames()) {
            assertTrue(expected.contains(name));
        }
    }

    @Test
    public void testShouldGetDirectConnectionsInMap() {
        TrainsMap m = createExampleMap();

        HashSet<String> expected = new HashSet<>();
        expected.add("LA");
        expected.add("SF");
        expected.add("Sac");
        expected.add("SLO");

        for (DirectConnection d : m.getDirectConnections()) {
            City c0 = d.getCity0();
            City c1 = d.getCity1();

            assertTrue(expected.contains(c0.getName()));
            assertTrue(expected.contains(c1.getName()));
        }
    }

    @Test
    public void testShouldGetFeasibleDestinationsCorrectly() {
        TrainsMap m = createExampleMap();
        List<String> sloLA = new ArrayList<>(Arrays.asList("SLO", "LA"));
        List<String> sacSF = new ArrayList<>(Arrays.asList("Sac", "SF"));
        List<String> sloSF = new ArrayList<>(Arrays.asList("SF", "SLO"));
        List<String> laSF = new ArrayList<>(Arrays.asList("LA", "SF"));
        List<String> sacSLO = new ArrayList<>(Arrays.asList("Sac", "SLO"));
        List<String> sacLA = new ArrayList<>(Arrays.asList("Sac", "LA"));

        HashSet<List<String>> expected = new HashSet<>();
        expected.add(sloLA);
        expected.add(sacSF);
        expected.add(sacLA);
        expected.add(sloSF);
        expected.add(sacSLO);
        expected.add(laSF);

        HashSet<List<String>> result = new HashSet<>();

        for (Destination d : m.getAllFeasibleDestinations()) {
            Pair<City> cities = d.getVertices();
            List<String> destinationCityNames = new ArrayList<>();
            destinationCityNames.add(cities.getFirst().getName());
            destinationCityNames.add(cities.getSecond().getName());

            result.add(destinationCityNames);
        }
        for (List<String> names : expected) {
            boolean normal = result.contains(names);
            Collections.reverse(names); // because we do not care about order
            boolean reverseExpected = result.contains(names);
            assertTrue(normal || reverseExpected);
        }
    }

    @Test
    public void testShouldGetDestinationsOnMultipleConnectedComponents() {
        Set<City> cities = new HashSet<>(4);
        Set<DirectConnection> connections = new HashSet<>(2);
        City LA = new City("LA", new Coord(.1f, .1f));
        City SF = new City("SF", new Coord(.1f, .4f));
        City SAC = new City("Sac", new Coord(.4f, .1f));
        City SLO = new City("SLO", new Coord(.8f, .8f));
        cities.add(LA);
        cities.add(SF);
        cities.add(SAC);
        cities.add(SLO);
        DirectConnection d1 =
                new DirectConnection(LA, SLO, 3, ColorTrains.GREEN);
        DirectConnection d4 = new DirectConnection(SF, SAC, 4, ColorTrains.RED);
        connections.add(d1);
        connections.add(d4);
        TrainsMap map =
                TrainsMap.createTrainsMapWithDefaultSize(cities, connections);
        Set<Destination> gen = map.getAllFeasibleDestinations();
        ArrayList<Destination> exp = new ArrayList<>();
        exp.add(new Destination(LA, SLO));
        exp.add(new Destination(SAC, SF));
        for (Destination d : exp) {
            assertTrue(gen.contains(d));
        }
        assertEquals(gen.size(), exp.size());
    }

    private TrainsMap createExampleMap() {
        Set<City> cities = new HashSet<>(4);
        Set<DirectConnection> connections = new HashSet<>(4);
        City LA = new City("LA", new Coord(.1f, .1f));
        City SF = new City("SF", new Coord(.1f, .4f));
        City SAC = new City("Sac", new Coord(.4f, .1f));
        City SLO = new City("SLO", new Coord(.8f, .8f));
        cities.add(LA);
        cities.add(SF);
        cities.add(SAC);
        cities.add(SLO);
        DirectConnection d1 =
                new DirectConnection(LA, SLO, 3, ColorTrains.GREEN);
        DirectConnection d2 =
                new DirectConnection(SLO, SAC, 5, ColorTrains.WHITE);
        DirectConnection d3 = new DirectConnection(SLO, SF, 5, ColorTrains.RED);
        DirectConnection d4 = new DirectConnection(SF, SAC, 4, ColorTrains.RED);
        connections.add(d1);
        connections.add(d2);
        connections.add(d3);
        connections.add(d4);
        TrainsMap map =
                TrainsMap.createTrainsMapWithDefaultSize(cities, connections);
        return map;
    }

    private TrainsMap createExampleDisconnectedMap() {
        // removed connection from slo to SF from above
        Set<City> cities = new HashSet<>(4);
        Set<DirectConnection> connections = new HashSet<>(4);
        City LA = new City("LA", new Coord(.1f, .1f));
        City SF = new City("SF", new Coord(.1f, .4f));
        City SAC = new City("Sac", new Coord(.4f, .1f));
        City SLO = new City("SLO", new Coord(.8f, .8f));
        cities.add(LA);
        cities.add(SF);
        cities.add(SAC);
        cities.add(SLO);
        DirectConnection d1 =
                new DirectConnection(LA, SLO, 3, ColorTrains.GREEN);
        DirectConnection d4 = new DirectConnection(SF, SAC, 4, ColorTrains.RED);
        connections.add(d1);
        connections.add(d4);
        TrainsMap map =
                TrainsMap.createTrainsMapWithDefaultSize(cities, connections);
        return map;
    }

    // test of TrainsMap.areCitiesConnected
    @Test
    public void testShouldTrueWhenCitiesConnected() {
        TrainsMap m = createExampleMap();
        City LA = new City("LA", new Coord(.1f, .1f));
        City SF = new City("SF", new Coord(.1f, .4f));

        assertTrue(m.areCitiesConnected(LA, SF));
    }

    // test of TrainsMap.areCitiesConnected
    @Test
    public void testShouldFalseWhenCitiesDisconnected() {
        TrainsMap m = createExampleDisconnectedMap();
        City LA = new City("LA", new Coord(.1f, .1f));
        City SF = new City("SF", new Coord(.1f, .4f));

        assertFalse(m.areCitiesConnected(LA, SF));
    }

    @Test
    public void testShouldThrowWhenMapWidthSmallerThan10Pixels() {
        Set<City> cities = new HashSet<>(4);
        Set<DirectConnection> connections = new HashSet<>(4);
        City LA = new City("LA", new Coord(.1f, .1f));
        City SF = new City("SF", new Coord(.1f, .4f));
        City SAC = new City("Sac", new Coord(.4f, .1f));
        City SLO = new City("SLO", new Coord(.8f, .8f));
        cities.add(LA);
        cities.add(SF);
        cities.add(SAC);
        cities.add(SLO);
        DirectConnection d1 =
                new DirectConnection(LA, SLO, 3, ColorTrains.GREEN);
        DirectConnection d4 = new DirectConnection(SF, SAC, 4, ColorTrains.RED);
        connections.add(d1);
        connections.add(d4);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new TrainsMap(cities, connections, 9, 10);
        });
    }

    @Test
    public void testShouldThrowWhenMapHeightSmallerThan10Pixels() {
        Set<City> cities = new HashSet<>(4);
        Set<DirectConnection> connections = new HashSet<>(4);
        City LA = new City("LA", new Coord(.1f, .1f));
        City SF = new City("SF", new Coord(.1f, .4f));
        City SAC = new City("Sac", new Coord(.4f, .1f));
        City SLO = new City("SLO", new Coord(.8f, .8f));
        cities.add(LA);
        cities.add(SF);
        cities.add(SAC);
        cities.add(SLO);
        DirectConnection d1 =
                new DirectConnection(LA, SLO, 3, ColorTrains.GREEN);
        DirectConnection d4 = new DirectConnection(SF, SAC, 4, ColorTrains.RED);
        connections.add(d1);
        connections.add(d4);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new TrainsMap(cities, connections, 10, 9);
        });
    }

    @Test
    public void testShouldOKWhenMap10by10Pixels() {
        Set<City> cities = new HashSet<>(4);
        Set<DirectConnection> connections = new HashSet<>(4);
        City LA = new City("LA", new Coord(.1f, .1f));
        City SF = new City("SF", new Coord(.1f, .4f));
        City SAC = new City("Sac", new Coord(.4f, .1f));
        City SLO = new City("SLO", new Coord(.8f, .8f));
        cities.add(LA);
        cities.add(SF);
        cities.add(SAC);
        cities.add(SLO);
        DirectConnection d1 =
                new DirectConnection(LA, SLO, 3, ColorTrains.GREEN);
        DirectConnection d4 = new DirectConnection(SF, SAC, 4, ColorTrains.RED);
        connections.add(d1);
        connections.add(d4);
        TrainsMap m = new TrainsMap(cities, connections, 10, 10);
        assertEquals(m.getHeight(), 10);
        assertEquals(m.getWidth(), 10);

    }

    @Test
    public void testShouldGetCoordOfCityOnMap() {
        TrainsMap m = ExampleMap.createExampleMap();
        assertEquals(new Coord(.4f, .8f), m.getCoordGivenCityName("LA"));
        assertEquals(new Coord(.31f, .65f), m.getCoordGivenCityName("SLO"));
    }

    @Test
    public void testShouldNotThrowWhenEmptyConnections() {
        Set<City> cities = new HashSet<>(4);
        Set<DirectConnection> connections = new HashSet<>(4);
        City LA = new City("LA", new Coord(.1f, .1f));
        City SF = new City("SF", new Coord(.1f, .4f));
        City SAC = new City("Sac", new Coord(.4f, .1f));
        City SLO = new City("SLO", new Coord(.8f, .8f));
        cities.add(LA);
        cities.add(SF);
        cities.add(SAC);
        cities.add(SLO);
        TrainsMap m = new TrainsMap(cities, connections, 10, 10);
    }

}
