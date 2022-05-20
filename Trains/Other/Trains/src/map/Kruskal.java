package map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Class to construct a mapping of representatives from which to run Kruskals
 * and generate the MAXimum spanning tree. Used to calculate the longest acyclic
 * path for scoring. NOTE this does NOT work when given edges that are not
 * connected to each other, so we run deriveConnectedComponent and run Kruskal's
 * on each connected component.
 */
public class Kruskal {
    private final HashMap<City, City> reps;
    private final List<DirectConnection> allEdges;
    private List<DirectConnection> spanningTree;
    private int numEdges;
    private final int nodeCount;

    /**
     * Main constructor for Kruskal's
     */
    public Kruskal(List<DirectConnection> allConnections) {
        HashSet<City> cities = new HashSet<>();
        // get the cities from direct connections
        for (DirectConnection dc : allConnections) {
            cities.add(dc.getCity0());
            cities.add(dc.getCity1());
        }

        HashMap<City, City> reps = new HashMap<>();
        for (City c : cities) {
            reps.put(c, c); // initialize each node's rep with itself
        }
        this.reps = reps;

        List<DirectConnection> noDuplicateEdges =
                takeMaximumWeightEdges(allConnections);
        Collections.sort(noDuplicateEdges, Comparator
                .comparing(DirectConnection::getLength)
                .reversed()); //sort dc by
        // descending length

        this.allEdges =
                noDuplicateEdges; // remove multi connections between cities,
        // keeps longest
        this.nodeCount = cities.size();
        this.numEdges = 0;
        this.spanningTree = new ArrayList<>();
    }

    /**
     * Eliminates any edges that have a lesser length as we are finding maximum
     * spanning tree.
     *
     * @param initial list of directconnections
     *
     * @return filtered list of directconnections with only longest lengths
     */
    public static List<DirectConnection> takeMaximumWeightEdges(
            List<DirectConnection> initial) {
        HashMap<HashSet<City>, DirectConnection> nonOverlap = new HashMap<>();
        for (DirectConnection dc : initial) {
            HashSet<City> cities = new HashSet<>();
            cities.add(dc.getCity0());
            cities.add(dc.getCity1());
            if (nonOverlap.get(cities) == null) {
                nonOverlap.put(cities, dc);
            } else {
                DirectConnection storedVal = nonOverlap.get(cities);
                if (storedVal.getLength() < dc.getLength()) {
                    nonOverlap.put(cities, dc);
                }
            }
        }
        return new ArrayList<>(nonOverlap.values());
    }

    /**
     * Visit an edge, check if its cyclic, if not add
     */
    private List<DirectConnection> visit(List<DirectConnection> spanningTree,
                                         DirectConnection e) {
        if (isNotCyclicAdd(e)) {
            spanningTree.add(e);
        }
        return spanningTree;
    }

    /**
     * Runs Kruskal's MAXimum spanning tree on this Kruskal Object
     *
     * @return the generated longest length edges spanning tree
     */
    public List<DirectConnection> run() {
        List<DirectConnection> acc = new ArrayList<>();
        for (DirectConnection edge : this.allEdges) {
            this.spanningTree = this.visit(acc, edge);
        }
        return this.spanningTree;
    }

    /**
     * Determines if adding this edge would create a cycle, if not adds the edge
     * to this.reps and increments numEdges
     */
    private boolean isNotCyclicAdd(DirectConnection dc) {
        if (this.numEdges > this.nodeCount - 1) { // if E > V - 1, terminate
            return false;
        }
        City fromTree = find(dc.getCity0());
        City toTree = find(dc.getCity1());
        boolean notCyclic = !fromTree.equals(toTree);
        if (notCyclic) {
            this.numEdges++;
            this.reps.put(fromTree, toTree);
        }
        return notCyclic;
    }

    /**
     * Kruskal helper to find the parent City in the representative map
     *
     * @param givenCity node
     *
     * @return parent node
     */
    private City find(City givenCity) {
        City parent = this.reps.get(givenCity);
        // so if a node map to itself, this is the representative (root)
        if (parent.equals(givenCity)) {
            return givenCity;
        } else {
            return find(parent); //recur to find the parent
        }
    }

    /**
     * Method to derive the list of cities that are connected in the given list
     * of edges.
     *
     * @return List of connected components defined by the edges that are
     * included.
     */
    public List<List<City>> deriveConnectedComponents() {
        Map<City, List<City>> components = new HashMap<>();
        for (City c : this.reps.keySet()) {
            City parent = this.find(this.reps.get(c));
            if (components.get(parent) == null) {
                ArrayList<City> cities = new ArrayList<>();
                cities.add(c);
                components.put(parent, cities);
            } else {
                components.get(parent).add(c);
            }
        }
        return new ArrayList(components.values());
    }
}
