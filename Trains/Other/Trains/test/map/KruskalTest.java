package map;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KruskalTest {

    @Test
    public void testShouldProduceSimpleSpanningTree() {
        City cambridge = new City("Cambridge", new Coord(.11f, .101f));
        City financial_district =
                new City("Financial District", new Coord(.80f, .3f));
        City common = new City("Common", new Coord(.5f, .4f));
        City seaport = new City("Seaport", new Coord(.90f, .6f));

        DirectConnection d12 =
                new DirectConnection(cambridge, financial_district, 4,
                        ColorTrains.BLUE);
        DirectConnection d1 =
                new DirectConnection(cambridge, common, 3, ColorTrains.RED);
        DirectConnection d4 =
                new DirectConnection(common, seaport, 4, ColorTrains.BLUE);

        List<DirectConnection> givenOwned = new ArrayList<>();
        givenOwned.add(d1);
        givenOwned.add(d4);
        givenOwned.add(d12);

        List<City> givenCities = new ArrayList<>();
        givenCities.add(cambridge);
        givenCities.add(financial_district);
        givenCities.add(common);
        givenCities.add(seaport);

        Kruskal k = new Kruskal(givenOwned);
        List<DirectConnection> expectedSpanningTree = new ArrayList<>();
        expectedSpanningTree.add(d12);
        expectedSpanningTree.add(d4);
        expectedSpanningTree.add(d1);

        assertEquals(expectedSpanningTree, k.run());
    }

    @Test
    public void testShouldProduceComplexSpanningTree() {
        TrainsMap tester = ExampleMap.createBostonMap();
        // assume someone owns every connection in boston map
        City brookline = new City("Brookline", new Coord(.1f, .5f));
        City common = new City("Common", new Coord(.5f, .4f));
        City chinatown = new City("Chinatown", new Coord(.68f, .42f));
        City financial_district =
                new City("Financial District", new Coord(.80f, .3f));
        City seaport = new City("Seaport", new Coord(.90f, .6f));
        City cambridge = new City("Cambridge", new Coord(.11f, .101f));

        DirectConnection d8 =
                new DirectConnection(brookline, cambridge, 5, ColorTrains.RED);
        DirectConnection d5 =
                new DirectConnection(cambridge, financial_district, 5,
                        ColorTrains.RED);
        DirectConnection d3 =
                new DirectConnection(common, chinatown, 3, ColorTrains.WHITE);
        DirectConnection d6 =
                new DirectConnection(common, financial_district, 4,
                        ColorTrains.WHITE);
        DirectConnection d7 =
                new DirectConnection(financial_district, seaport, 4,
                        ColorTrains.WHITE);

        Kruskal k = new Kruskal(new ArrayList<>(tester.getDirectConnections()));

        List<DirectConnection> expectedSpanningTree = new ArrayList<>();
        expectedSpanningTree.add(d5);
        expectedSpanningTree.add(d8);
        expectedSpanningTree.add(d6);
        expectedSpanningTree.add(d7);
        expectedSpanningTree.add(d3);

        List<DirectConnection> result = k.run();

        assertEquals(expectedSpanningTree, result);
    }

    @Test
    public void testShouldProduceSpanningTreeManyMultiEdges() {
        TrainsMap tester = ExampleMap.createExampleMultipleConnectionMap();
        // assume someone owns every connection in boston map
        City LA = new City("LA", new Coord(.4f, .8f));
        City SF = new City("SF", new Coord(.3f, .2f));
        City SAC = new City("Sac", new Coord(.4f, .24f));
        City SLO = new City("SLO", new Coord(.31f, .65f));
        City vegas = new City("Las Vegas", new Coord(.8f, .65f));

        DirectConnection d15 =
                new DirectConnection(vegas, SLO, 5, ColorTrains.BLUE);
        DirectConnection d16 =
                new DirectConnection(vegas, SLO, 5, ColorTrains.RED);
        DirectConnection d17 =
                new DirectConnection(vegas, SLO, 5, ColorTrains.GREEN);
        DirectConnection d18 =
                new DirectConnection(vegas, SLO, 5, ColorTrains.WHITE);

        DirectConnection d5 = new DirectConnection(LA, SLO, 5, ColorTrains.RED);
        DirectConnection d3 = new DirectConnection(SLO, SF, 5, ColorTrains.RED);
        DirectConnection d2 =
                new DirectConnection(SLO, SAC, 5, ColorTrains.WHITE);

        List<DirectConnection> sortedInputDCs =
                new ArrayList<>(tester.getDirectConnections());
        Collections.sort(sortedInputDCs);

        List<City> sortedInputCities = new ArrayList<>(tester.getCities());
        Collections.sort(sortedInputCities);

        Kruskal k = new Kruskal(sortedInputDCs);

        List<DirectConnection> expectedSpanningTree = new ArrayList<>();
        expectedSpanningTree.add(d5);
        expectedSpanningTree.add(d3);
        expectedSpanningTree.add(d17);
        expectedSpanningTree.add(d2);

        List<DirectConnection> result = k.run();

        assertTrue(result.contains(d5));
        assertTrue(result.contains(d3));
        assertTrue(result.contains(d15) || result.contains(d16) ||
                   result.contains(d17) || result.contains(d18));
        assertTrue(result.contains(d2));

//    assertEquals(expectedSpanningTree, result);
    }

    @Test
    public void testShouldFindSingleEdgeSpanningTree() {
        TrainsMap tester = ExampleMap.createBostonMap();
        // assume someone owns every connection in boston map
        City brookline = new City("Brookline", new Coord(.1f, .5f));
        City financial_district =
                new City("Financial District", new Coord(.80f, .3f));
        City seaport = new City("Seaport", new Coord(.90f, .6f));
        City cambridge = new City("Cambridge", new Coord(.11f, .101f));

        DirectConnection d5 =
                new DirectConnection(cambridge, financial_district, 5,
                        ColorTrains.RED);
        DirectConnection d12 =
                new DirectConnection(cambridge, financial_district, 4,
                        ColorTrains.BLUE);

        DirectConnection d8 =
                new DirectConnection(brookline, cambridge, 5, ColorTrains.RED);
        DirectConnection d7 =
                new DirectConnection(financial_district, seaport, 4,
                        ColorTrains.WHITE);

        List<DirectConnection> givenOwnedDisjoint = new ArrayList<>();
        givenOwnedDisjoint.add(d8);
        givenOwnedDisjoint.add(d7);
        givenOwnedDisjoint.add(d5);
        givenOwnedDisjoint.add(d12);

        Kruskal k = new Kruskal(givenOwnedDisjoint);

        List<DirectConnection> expectedSpanningTree = new ArrayList<>();
        expectedSpanningTree.add(d5);
        expectedSpanningTree.add(d8);
        expectedSpanningTree.add(d7);

        List<DirectConnection> result = k.run();

        assertEquals(expectedSpanningTree, result);
    }

    @Test
    public void testShouldGetConnectedComponents() {
        TrainsMap map = ExampleMap.createExampleDisconnectedMap();
        List<City> cities = new ArrayList<>(map.getCities());
        List<DirectConnection> allConnections =
                new ArrayList<>(map.getDirectConnections());
        Kruskal k = new Kruskal(allConnections);
        k.run();
        List<List<City>> list = k.deriveConnectedComponents();
        assertEquals(2, list.size());
        assertEquals(2, list.get(0).size());
        assertEquals(4, list.get(1).size());
    }

    @Test
    public void testShouldGetSingleConnectedComponent() {
        TrainsMap map = ExampleMap.createExampleMap();
        List<City> cities = new ArrayList<>(map.getCities());
        List<DirectConnection> allConnections =
                new ArrayList<>(map.getDirectConnections());
        Kruskal k = new Kruskal(allConnections);
        k.run();
        List<List<City>> list = k.deriveConnectedComponents();
        assertEquals(1, list.size());
        assertEquals(4, list.get(0).size());
    }

}
