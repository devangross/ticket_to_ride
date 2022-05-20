package map;

import org.junit.jupiter.api.Test;
import viz.MapGUI;
import viz.MapPanel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MapPanelTest {

    private static TrainsMap createExampleMultipleConnectionMap() {
        Set<City> cities = new HashSet<City>(4);
        Set<DirectConnection> connections = new HashSet<DirectConnection>(4);
        City LA = new City("LA", new Coord(.4f, .8f));
        City SF = new City("SF", new Coord(.3f, .2f));
        City SAC = new City("Sac", new Coord(.4f, .24f));
        City SLO = new City("SLO", new Coord(.31f, .65f));
        City vegas = new City("Las Vegas", new Coord(.8f, .65f));
        cities.add(LA);
        cities.add(SF);
        cities.add(SAC);
        cities.add(SLO);
        cities.add(vegas);

        DirectConnection d1 =
                new DirectConnection(LA, SLO, 3, ColorTrains.GREEN);
        DirectConnection d5 = new DirectConnection(LA, SLO, 5, ColorTrains.RED);

        DirectConnection d2 =
                new DirectConnection(SLO, SAC, 5, ColorTrains.WHITE);
        DirectConnection d3 = new DirectConnection(SLO, SF, 5, ColorTrains.RED);
        DirectConnection d4 = new DirectConnection(SF, SAC, 4, ColorTrains.RED);
        DirectConnection d6 =
                new DirectConnection(SLO, vegas, 3, ColorTrains.BLUE);
        DirectConnection d7 =
                new DirectConnection(vegas, SLO, 3, ColorTrains.BLUE);
        DirectConnection d8 =
                new DirectConnection(vegas, SLO, 3, ColorTrains.RED);
        DirectConnection d9 =
                new DirectConnection(vegas, SLO, 3, ColorTrains.GREEN);
        DirectConnection d10 =
                new DirectConnection(vegas, SLO, 3, ColorTrains.WHITE);
        DirectConnection d11 =
                new DirectConnection(vegas, SLO, 4, ColorTrains.BLUE);
        DirectConnection d12 =
                new DirectConnection(vegas, SLO, 4, ColorTrains.RED);
        DirectConnection d13 =
                new DirectConnection(vegas, SLO, 4, ColorTrains.GREEN);
        DirectConnection d14 =
                new DirectConnection(vegas, SLO, 4, ColorTrains.WHITE);

        connections.add(d1);
        connections.add(d2);
        connections.add(d3);
        connections.add(d4);
        connections.add(d5);
        connections.add(d6);
        connections.add(d7);
        connections.add(d8);
        connections.add(d9);
        connections.add(d10);
        connections.add(d11);
        connections.add(d12);
        connections.add(d13);
        connections.add(d14);

        TrainsMap map =
                TrainsMap.createTrainsMapWithDefaultSize(cities, connections);
        return map;
    }

    @Test
    public void testShouldAllowValidMapPanelConstruction() {
        TrainsMap exampleMap = createExampleMultipleConnectionMap();
        MapGUI mapGUI = new MapGUI(exampleMap);
        MapPanel panel = mapGUI.makeMapPanel();
        assertNotNull(panel);
    }

    @Test
    public void testShouldGetNumConnectionsFromMultiConnectionSet() {
        Set<City> cities = new HashSet<City>(4);
        Set<DirectConnection> connections = new HashSet<DirectConnection>(4);
        City LA = new City("LA", new Coord(.4f, .8f));
        City SF = new City("SF", new Coord(.3f, .2f));
        City mexico = new City("mexico", new Coord(.3f, .2f));
        cities.add(LA);
        cities.add(SF);
        DirectConnection d1 =
                new DirectConnection(LA, SF, 3, ColorTrains.GREEN);
        DirectConnection d4 = new DirectConnection(SF, LA, 4, ColorTrains.RED);
        DirectConnection d3 =
                new DirectConnection(SF, mexico, 4, ColorTrains.RED);

        connections.add(d1);
        connections.add(d3);
        connections.add(d4);

        HashMap<HashSet<City>, ArrayList<DirectConnection>> expected =
                new HashMap<>();
        expected.put(new HashSet<>(Arrays.asList(mexico, SF)),
                new ArrayList<>(Arrays.asList(d3)));
        expected.put(new HashSet<>(Arrays.asList(LA, SF)),
                new ArrayList<>(Arrays.asList(d1, d4)));
        HashMap<HashSet<City>, ArrayList<DirectConnection>> result =
                MapPanel.getNumMultiConnectionsByCities(connections);

        for (HashSet<City> key : result.keySet()) {
            for (DirectConnection dc : result.get(key)) {
                assertTrue(expected.get(key).contains(dc));
            }
        }

    }

    @Test
    public void testShouldGetNumConnectionsFromComplexDirectConnections() {
        City LA = new City("LA", new Coord(.4f, .8f));
        City SF = new City("SF", new Coord(.3f, .2f));
        City SAC = new City("Sac", new Coord(.4f, .24f));
        City SLO = new City("SLO", new Coord(.31f, .65f));
        City vegas = new City("Las Vegas", new Coord(.8f, .65f));

        TrainsMap map = createExampleMultipleConnectionMap();

        HashMap<HashSet<City>, ArrayList<DirectConnection>> result =
                MapPanel.getNumMultiConnectionsByCities(
                        map.getDirectConnections());

        assertEquals(
                result.get(new HashSet<>(Arrays.asList(SLO, vegas))).size(), 8);
        assertEquals(result.get(new HashSet<>(Arrays.asList(SAC, SLO))).size(),
                1);
        assertEquals(result.get(new HashSet<>(Arrays.asList(SLO, LA))).size(),
                2);
    }
}
