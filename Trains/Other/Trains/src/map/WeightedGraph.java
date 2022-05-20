package map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class WeightedGraph {

    private final Map<City, List<ToEdge>> adjVertices;

    /**
     * Constructor to initialize adjVertices as an empty HashMap
     */
    public WeightedGraph() {
        this.adjVertices = new HashMap<>();
    }

    public static WeightedGraph makeGraphFromVerticesEdges(List<City> cities,
                                                           List<DirectConnection> dc) {
        WeightedGraph fullGraph = new WeightedGraph();
        for (City c : cities) {
            fullGraph.addDisconnectedCity(c);
        }
        for (DirectConnection connection : dc) { //add adjacent cities
            fullGraph.addEdge(connection);
        }
        return fullGraph;
    }

    public Map<City, List<ToEdge>> getAdjVertices() {
        return adjVertices;
    }

    /**
     * Function to add a vertex with no connected vertices to this graph.
     *
     * @param c - city representing vertex to be added
     */
    public void addDisconnectedCity(City c) {
        adjVertices.putIfAbsent(c, new ArrayList<>());
    }

    /**
     * Function to remove a city/vertex from this graph AND removes that city
     * from the adjacency lists of other vertices
     *
     * @param c TrainsMap.City to be removed
     */
    public void removeCity(City c) {
        adjVertices.values().stream().forEach(e -> e.remove(c));
        adjVertices.remove(c);
    }

    /**
     * Function to add an edge from one existing city to another
     *
     * @param dc DirectConnection to add as an edge
     */
    public void addEdge(DirectConnection dc) {
        City c0 = dc.getCity0();
        City c1 = dc.getCity1();

        List<ToEdge> c1AdjList = adjVertices.get(c0);
        List<ToEdge> c2AdjList = adjVertices.get(c1);
        if (c1AdjList != null && c2AdjList != null) {
            adjVertices.get(c0).add(new ToEdge(c1, dc.getLength()));
            adjVertices.get(c1).add(new ToEdge(c0, dc.getLength()));
        } else {
            throw new IllegalArgumentException(
                    "Unable to find one of the provided cities " + c0 + " or " +
                    c1 + " in the hashmap");
        }
    }

    /**
     * Function to remove an edge from this TrainsMap.Graph.
     *
     * @param c1 one city on the edge
     * @param c2 another city on the edge
     */
    public void removeEdge(City c1, City c2) {
        List<ToEdge> eV1 = adjVertices.get(c1);
        List<ToEdge> eV2 = adjVertices.get(c2);
        if (eV1 != null) {
            eV1.remove(c2);
        }
        if (eV2 != null) {
            eV2.remove(c1);
        }
    }

    /**
     * Function to get the list of adjacent vertices to a given city/vertex in
     * our map
     *
     * @param c city
     */
    public List<ToEdge> getAdjVertices(City c) {
        List<ToEdge> result = adjVertices.get(c);
        if (result != null) {
            return result;
        } else {
            throw new IllegalArgumentException(
                    "Unable to find one of the provided city " + c +
                    " in the hashmap");
        }
    }

    /**
     * Method to determine the longest path of a graph (must be undirected tree)
     * from a given city in the graph
     *
     * @return int longest path
     */
    public DistHolder getLongestPathFromCity(City c) {
        List<City> cities = new ArrayList<>(this.adjVertices.keySet());

        Integer[] dis = new Integer[this.adjVertices.keySet().size()];

        // mark all distance with -1
        Arrays.fill(dis, -1);
        dis[cities.indexOf(c)] = 0;

        Queue<City> q = new LinkedList<>();
        q.add(c);

        while (q.peek() != null) {

            City currCity = q.poll();
            int fromCityIndex = cities.indexOf(currCity);
            // iterate through adjacent vertices and update distances
            for (int i = 0; i < this.adjVertices.get(currCity).size(); i++) {
                ToEdge toVert = this.adjVertices.get(currCity).get(i);
                int toCityIndex = cities.indexOf(toVert.destination);
                if (dis[toCityIndex] == -1) {
                    // if this hasn't been visited update distance to current
                    // node's distance plus weight of edge between them
                    q.add(toVert.destination);
                    dis[toCityIndex] = dis[fromCityIndex] + toVert.weight;
                }
            }
        }

        int maxDist = 0;
        City farthestCity = c;
        //find the furthest city and its distance
        for (int x = 0; x < dis.length; x++) {
            if (dis[x] > maxDist) {
                maxDist = dis[x];
                farthestCity = cities.get(x);
            }
        }
        DistHolder furthest = new DistHolder(farthestCity, maxDist);
        return furthest;
    }

    /**
     * Two calls to BFS longest path
     */
    public int findAbsoluteLongestPath() {
        ArrayList<City> cities = new ArrayList<>(this.adjVertices.keySet());
        DistHolder firstCall = this.getLongestPathFromCity(cities.get(0));
        DistHolder absoluteLongestPath =
                this.getLongestPathFromCity(firstCall.getCity());
        return absoluteLongestPath.getDistance();
    }

    /**
     * Static class to package a city and it's distance from a given city to
     * getLongestPathFromCity
     */
    public static class DistHolder {
        private final City c;
        private final int distance;

        public DistHolder(City c, int distance) {
            this.c = c;
            this.distance = distance;
        }

        public City getCity() {
            return c;
        }

        public int getDistance() {
            return distance;
        }
    }

    /**
     * Static class to represent weighted edges in adjacency list representation
     * of a graph
     */
    public static class ToEdge {
        private final City destination;
        private final int weight;

        public ToEdge(City destination, int weight) {
            this.destination = destination;
            this.weight = weight;
        }

        @Override
        public String toString() {
            return "destination: " + destination + ", weight: " + this.weight;
        }
    }
}
