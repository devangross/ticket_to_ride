package map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Class to run tests on the TrainsMap.Graph class
 */
public class GraphTest {
    @Test
    public void testShouldConstructValidGraph() {
        assertNotNull(new Graph());
    }

    @Test
    public void testShouldAddDisconnectedCity() {
        City la = new City("LA", new Coord(.1f, .1f));

        Graph g = new Graph();
        g.addDisconnectedCity(la);
        // verify new vertex was added by checking adjacency list exists and
        // is empty
        assertEquals(g.getAdjVertices(la), new ArrayList<City>());
    }

    @Test
    public void testShouldIgnoreAddDuplicateCity() {
        City la = new City("LA", new Coord(.1f, .1f));

        Graph g = new Graph();
        g.addDisconnectedCity(la);
        g.addDisconnectedCity(la);
        assertEquals(g.getAdjVertices(la), new ArrayList<City>());
    }

    @Test
    public void testShouldRemoveCityCorrectly() {
        City la = new City("LA", new Coord(.1f, .1f));
        City sf = new City("sf", new Coord(.9f, .9f));

        Graph g = new Graph();
        g.addDisconnectedCity(la);
        g.addDisconnectedCity(sf);
        g.removeCity(la);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            g.getAdjVertices(la);
        });
    }

    @Test
    public void testShouldRemoveCityFromAdjacencyLists() {
        City la = new City("LA", new Coord(.1f, .1f));
        City sf = new City("sf", new Coord(.9f, .9f));

        Graph g = new Graph();
        g.addDisconnectedCity(la);
        g.addDisconnectedCity(sf);
        g.addEdge(la, sf);
        g.removeCity(la);
        assertEquals(g.getAdjVertices(sf), new ArrayList<City>());
    }

    @Test
    public void testShouldRemoveCityFromMultipleAdjacencyLists() {
        City la = new City("LA", new Coord(.1f, .1f));
        City sf = new City("sf", new Coord(.9f, .9f));
        City slo = new City("slo", new Coord(.8f, .9f));
        Graph g = new Graph();
        g.addDisconnectedCity(la);
        g.addDisconnectedCity(sf);
        g.addDisconnectedCity(slo);
        g.addEdge(la, sf);
        g.addEdge(la, slo);
        g.removeCity(la);
        assertEquals(g.getAdjVertices(sf), new ArrayList<City>());
        assertEquals(g.getAdjVertices(slo), new ArrayList<City>());
    }

    @Test
    public void testShouldAddEdgesCorrectly() {
        City la = new City("LA", new Coord(.1f, .1f));
        City sf = new City("sf", new Coord(.9f, .9f));
        City slo = new City("slo", new Coord(.8f, .9f));
        Graph g = new Graph();
        g.addDisconnectedCity(la);
        g.addDisconnectedCity(sf);
        g.addDisconnectedCity(slo);
        g.addEdge(la, sf);
        g.addEdge(la, slo);
        g.addEdge(sf, slo);
        assertEquals(g.getAdjVertices(sf), Arrays.asList(la, slo));
        assertEquals(g.getAdjVertices(slo), Arrays.asList(la, sf));
    }

    @Test
    public void testShouldThrowIllegalArgWhenAddEdgeWithNonexistentCity() {
        City la = new City("LA", new Coord(.1f, .1f));
        City sf = new City("sf", new Coord(.9f, .9f));
        City slo = new City("slo", new Coord(.8f, .9f));
        Graph g = new Graph();
        g.addDisconnectedCity(la);
        g.addDisconnectedCity(sf);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            g.addEdge(la, slo);
        });

    }

    @Test
    public void testShouldRemoveEdgesCorrectly() {
        City la = new City("LA", new Coord(.1f, .1f));
        City sf = new City("sf", new Coord(.9f, .9f));
        City slo = new City("slo", new Coord(.8f, .9f));
        Graph g = new Graph();
        g.addDisconnectedCity(la);
        g.addDisconnectedCity(sf);
        g.addDisconnectedCity(slo);
        g.addEdge(la, sf);
        g.addEdge(la, slo);
        g.addEdge(sf, slo);
        g.removeEdge(la, sf);
        assertEquals(g.getAdjVertices(sf), Arrays.asList(slo));
        assertEquals(g.getAdjVertices(slo), Arrays.asList(la, sf));
    }

    @Test
    public void testShouldRemoveIfOneVertexNonexistent() {
        City la = new City("LA", new Coord(.1f, .1f));
        City sf = new City("sf", new Coord(.9f, .9f));
        City slo = new City("slo", new Coord(.8f, .9f));
        Graph g = new Graph();
        g.addDisconnectedCity(la);
        g.addDisconnectedCity(sf);
        g.addEdge(la, sf);
        g.removeEdge(la, slo);
        assertEquals(g.getAdjVertices(sf), Arrays.asList(la));
        assertEquals(g.getAdjVertices(la), Arrays.asList(sf));
    }

    @Test
    public void testShouldCorrectlyFindFeasibleDests() {
        City la = new City("LA", new Coord(.1f, .1f));
        City sf = new City("sf", new Coord(.9f, .9f));
        City slo = new City("slo", new Coord(.8f, .9f));
        Graph g = new Graph();
        g.addDisconnectedCity(la);
        g.addDisconnectedCity(sf);
        g.addDisconnectedCity(slo);
        g.addEdge(la, sf);
        g.addEdge(la, slo);
        g.addEdge(sf, slo);
        Set<City> visited = new HashSet<>();
        Set<Destination> feasibleDestinations = new HashSet<>();
        Graph.getFeasibleDestinationsDFSUtil(g, la, visited,
                feasibleDestinations);
        Set<Destination> expected = new HashSet<>();
        expected.add(new Destination(la, sf));
        expected.add(new Destination(la, slo));
        expected.add(new Destination(sf, slo));
        expected.add(new Destination(la, slo));
        for (Destination d : feasibleDestinations) {
            assertTrue(expected.contains(d));
        }
    }

    /**
     * Test to see if one long line of connections produce proper destinations.
     */
    @Test
    public void testShouldHandleLongDestinations() {
        Graph g = new Graph();
        City a = new City("Alb", new Coord(.2f, .3f));
        City b = new City("Syr", new Coord(.1f, .3f));
        City c = new City("Buf", new Coord(.7f, .3f));
        City d = new City("TrainsMap.City", new Coord(.9f, .8f));
        g.addDisconnectedCity(a);
        g.addDisconnectedCity(b);
        g.addDisconnectedCity(c);
        g.addDisconnectedCity(d);
        g.addEdge(a, b);
        g.addEdge(b, c);
        g.addEdge(c, d);

        Set<City> visited = new HashSet<>();
        Set<Destination> feasibleDestinations = new HashSet<>();
        Graph.getFeasibleDestinationsDFSUtil(g, a, visited,
                feasibleDestinations);
        Destination dest1 = new Destination(a, b);
        Destination dest2 = new Destination(a, c);
        Destination dest3 = new Destination(a, d);
        Destination dest4 = new Destination(b, c);
        Destination dest5 = new Destination(b, d);
        Destination dest6 = new Destination(c, d);
        ArrayList<Destination> expected = new ArrayList<>(
                Arrays.asList(dest1, dest2, dest3, dest4, dest5, dest6));
        for (Destination des : feasibleDestinations) {
            assertTrue(expected.contains(des));
        }
        assertEquals(feasibleDestinations.size(), expected.size());
    }

    //TODO test getConnectedCitiesUtil
    @Test
    public void testShouldRecognizeTwoSimpleConnectedVertices() {
        Graph g = new Graph();
        City a = new City("Alb", new Coord(.2f, .3f));
        City b = new City("Syr", new Coord(.1f, .3f));
        City c = new City("Buf", new Coord(.7f, .3f));
        g.addDisconnectedCity(a);
        g.addDisconnectedCity(b);
        g.addDisconnectedCity(c);
        g.addEdge(a, b);
        Set<City> visited = new HashSet<>();

        Graph.getConnectedCitiesDFSUtil(g, a, visited);

        assertTrue(visited.contains(b));
    }

    @Test
    public void testShouldRecognizeTwoSimpleDisconnectedVertices() {
        Graph g = new Graph();
        City a = new City("Alb", new Coord(.2f, .3f));
        City b = new City("Syr", new Coord(.1f, .3f));
        City c = new City("Buf", new Coord(.7f, .3f));
        g.addDisconnectedCity(a);
        g.addDisconnectedCity(b);
        g.addDisconnectedCity(c);
        g.addEdge(a, b);
        Set<City> visited = new HashSet<>();

        Graph.getConnectedCitiesDFSUtil(g, a, visited);

        assertFalse(visited.contains(c));
    }

    @Test
    public void testShouldRecognizeTwoComplexConnectedCities() {
        Graph g = new Graph();
        City brookline = new City("Brookline", new Coord(.1f, .12f));
        City common = new City("Common", new Coord(.5f, .3f));
        City chinatown = new City("Chinatown", new Coord(.6f, .4f));
        City financial_district =
                new City("Financial District", new Coord(.80f, .20f));
        City seaport = new City("seaport", new Coord(.90f, .45f));
        City cambridge = new City("cambridge", new Coord(.11f, .101f));
        City maine = new City("Maine", new Coord(1f, 0f));

        g.addDisconnectedCity(common);
        g.addDisconnectedCity(chinatown);
        g.addDisconnectedCity(financial_district);
        g.addDisconnectedCity(seaport);
        g.addDisconnectedCity(brookline);
        g.addDisconnectedCity(cambridge);
        g.addDisconnectedCity(maine);

        g.addEdge(common, cambridge);
        g.addEdge(brookline, common);
        g.addEdge(common, chinatown);
        g.addEdge(common, seaport);
        g.addEdge(cambridge, financial_district);
        g.addEdge(common, financial_district);
        g.addEdge(seaport, financial_district);
        g.addEdge(brookline, cambridge);

        Set<City> visited = new HashSet<>();

        Graph.getConnectedCitiesDFSUtil(g, brookline, visited);

        assertTrue(visited.contains(financial_district));
    }

    @Test
    public void testShouldRecognizeTwoComplexDisconnectedCities() {
        Graph g = new Graph();
        City brookline = new City("Brookline", new Coord(.1f, .12f));
        City common = new City("Common", new Coord(.5f, .3f));
        City chinatown = new City("Chinatown", new Coord(.6f, .4f));
        City financial_district =
                new City("Financial District", new Coord(.80f, .20f));
        City seaport = new City("seaport", new Coord(.90f, .45f));
        City cambridge = new City("cambridge", new Coord(.11f, .101f));
        City maine = new City("Maine", new Coord(1f, 0f));

        g.addDisconnectedCity(common);
        g.addDisconnectedCity(chinatown);
        g.addDisconnectedCity(financial_district);
        g.addDisconnectedCity(seaport);
        g.addDisconnectedCity(brookline);
        g.addDisconnectedCity(cambridge);
        g.addDisconnectedCity(maine);

        g.addEdge(common, cambridge);
        g.addEdge(brookline, common);
        g.addEdge(common, chinatown);
        g.addEdge(common, seaport);
        g.addEdge(cambridge, financial_district);
        g.addEdge(common, financial_district);
        g.addEdge(seaport, financial_district);
        g.addEdge(brookline, cambridge);

        Set<City> visited = new HashSet<>();

        Graph.getConnectedCitiesDFSUtil(g, common, visited);

        assertFalse(visited.contains(maine));
    }

}
