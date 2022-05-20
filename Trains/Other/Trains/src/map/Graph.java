package map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class which represents a TrainsMap.Graph as an adjacency list. Example: SF,
 * [Sac, SLO] - vertex SF is directly connected to vertices Sac and SLO Sac,
 * [SF, SLO] SLO, [SF, Sac, LA] LA, [SLO]
 * <p>
 * Utilized to separate graph operations from TrainsMap.TrainsMap objects.
 * Graphs representing TrainMap can be created privately in TrainMap using
 * convertToGraph
 * <p>
 * Provides constructor to initialize an empty HashMap<TrainsMap.City,
 * List<TrainsMap.City>>. TrainsMap.Graph is constructed by adding vertices with
 * addDisconnectedCity and adding edges with addEdge. Provides a function to get
 * adjacent vertices (the value in the HashMap). Provides a function to get all
 * feasible destinations (defined in TrainsMap.Destination) given a graph
 * object, initial root city, initially empty visited array, and an initially
 * empty feasible destinations array.
 */
public class Graph {
    private final Map<City, List<City>> adjVertices;

    /**
     * Constructor to initialize adjVertices as an empty HashMap
     */
    public Graph() {
        this.adjVertices = new HashMap<>();
    }

    /**
     * Recursive function which performs a depth first search on the given graph
     * to find all feasible Destinations. This function is static, it does not
     * rely on any instance fields. It takes all input as arguments and mutates
     * the initially empty set of destinations 'feasibles' with each distinct
     * destination which can be formed on the graph.
     *
     * @param graph     - TrainsMap.Graph to be traversed
     * @param root      - TrainsMap.City to start the traversal from
     * @param visited   - Set containing cities we have visited already
     * @param feasibles - Set containing accumulated resultant destinations
     */
    public static void getFeasibleDestinationsDFSUtil(Graph graph, City root,
                                                      Set<City> visited,
                                                      Set<Destination> feasibles) {
        visited.add(root);
        for (City innerCity : graph.getAdjVertices(root)) {
            if (!visited.contains(innerCity)) {
                for (City c : visited) {
                    Destination rootToInner = new Destination(c, innerCity);
                    boolean fDMissingVertex = !feasibles.contains(rootToInner);
                    if (fDMissingVertex) {
                        feasibles.add(rootToInner);
                    }
                }
                getFeasibleDestinationsDFSUtil(graph, innerCity, visited,
                        feasibles);
            }
        }
    }

    /**
     * Recursive function to traverse every connected City in the graph and
     * store in visited Set.
     */
    public static void getConnectedCitiesDFSUtil(Graph graph, City root,
                                                 Set<City> visited) {
        visited.add(root);
        for (City innerCity : graph.getAdjVertices(root)) {
            if (!visited.contains(innerCity)) {
                getConnectedCitiesDFSUtil(graph, innerCity, visited);
            }
        }
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
     * @param c1 one city/vertex in this graph
     * @param c2 another city/vertex in this graph
     */
    public void addEdge(City c1, City c2) {
        List<City> c1AdjList = adjVertices.get(c1);
        List<City> c2AdjList = adjVertices.get(c2);
        if (c1AdjList != null && c2AdjList != null) {
            adjVertices.get(c1).add(c2);
            adjVertices.get(c2).add(c1);
        } else {
            throw new IllegalArgumentException(
                    "Unable to find one of the provided cities " + c1 + " or " +
                    c2 + " in the hashmap");
        }

    }

    /**
     * Function to remove an edge from this TrainsMap.Graph.
     *
     * @param c1 one city on the edge
     * @param c2 another city on the edge
     */
    public void removeEdge(City c1, City c2) {
        List<City> eV1 = adjVertices.get(c1);
        List<City> eV2 = adjVertices.get(c2);
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
    public List<City> getAdjVertices(City c) {
        List<City> result = adjVertices.get(c);
        if (result != null) {
            return result;
        } else {
            throw new IllegalArgumentException(
                    "Unable to find one of the provided city " + c +
                    " in the hashmap");
        }
    }

}
