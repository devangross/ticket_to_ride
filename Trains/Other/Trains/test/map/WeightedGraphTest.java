package map;

import map.WeightedGraph.DistHolder;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WeightedGraphTest {

    @Test
    public void testMakeGraphFromVerticesEdges() {
        TrainsMap map = ExampleMap.createExampleMap();
        WeightedGraph weightedGraph = new WeightedGraph();
        ArrayList<City> cities = new ArrayList<>(map.getCities());
        Collections.sort(cities); // order for consistent test output
        WeightedGraph newG = WeightedGraph.makeGraphFromVerticesEdges(cities,
                new ArrayList<>(map.getDirectConnections()));
        Map<City, List<WeightedGraph.ToEdge>> adjacent = newG.getAdjVertices();
        //verify all adjacent edges are correctly initialized

        assertEquals(1, adjacent.get(cities.get(0)).size()); //LA
        assertEquals(1, adjacent.get(cities.get(1)).size()); //SF
        assertEquals(2, adjacent.get(cities.get(2)).size()); //SLO
        assertEquals(2, adjacent.get(cities.get(3)).size()); //Sac
    }

    @Test
    public void testShouldFindLongestPathFromMiddleVertex() {
        TrainsMap map = ExampleMap.createExampleMap();
        WeightedGraph weightedGraph = new WeightedGraph();
        ArrayList<City> cities = new ArrayList<>(map.getCities());
        Collections.sort(cities); // order for consistent test output
        WeightedGraph newG = WeightedGraph.makeGraphFromVerticesEdges(cities,
                new ArrayList<>(map.getDirectConnections()));
        DistHolder longest = newG.getLongestPathFromCity(cities.get(2));
        assertEquals(cities.get(1), longest.getCity());
        assertEquals(9, longest.getDistance());
    }

    @Test
    public void testShouldFindLongestPathWithMutliPath() {
        TrainsMap map = ExampleMap.createExampleMap();
        WeightedGraph weightedGraph = new WeightedGraph();
        ArrayList<City> cities = new ArrayList<>(map.getCities());
        Collections.sort(cities); // order for consistent test output
        WeightedGraph newG = WeightedGraph.makeGraphFromVerticesEdges(cities,
                new ArrayList<>(map.getDirectConnections()));
        DistHolder longest = newG.getLongestPathFromCity(cities.get(1));
        assertEquals(cities.get(0), longest.getCity());
        assertEquals(12, longest.getDistance());
    }

    @Test
    public void testShouldFindAbolsuteLongestPathGivenCenterNode() {
        TrainsMap map = ExampleMap.createExampleMap();
        WeightedGraph weightedGraph = new WeightedGraph();
        ArrayList<City> cities = new ArrayList<>(map.getCities());
        Collections.sort(cities); // order for consistent test output
        WeightedGraph newG = WeightedGraph.makeGraphFromVerticesEdges(cities,
                new ArrayList<>(map.getDirectConnections()));
        int longestPath = newG.findAbsoluteLongestPath();
        assertEquals(12, longestPath);
    }
}
