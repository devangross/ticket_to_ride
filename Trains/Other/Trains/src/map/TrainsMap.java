package map;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a single gameboard map in 'TrainsMap.Trains' Provides constructor
 * of TrainsMap.TrainsMap which accepts 2d string arrays to represent cities and
 * their connections. Provides getters to get the Set of cities and array of
 * strings representing city names and to get the Set of direct connections
 * between cities. Provides method to get all feasible "destinations" (two
 * cities connected by a path on the game board)
 */
public class TrainsMap {

    private final Set<City> cities;
            // set of cities on map
    private final Set<DirectConnection> directConnections;
            // set of connections on map
    private final int height;
    private final int width;
    private Set<Destination> availableDestinations;

    /**
     * Main constructor for TrainsMap.TrainsMap, verifies that connections are
     * valid - between two cities in the given cities set
     *
     * @param cities set of cities
     * @param width  of the Map in pixels
     * @param height of the Map in pixels
     */
    public TrainsMap(Set<City> cities, Set<DirectConnection> connections,
                     int width, int height) {
        if (width < 10 || height < 10 || width > 800 || height > 800) {
            throw new IllegalArgumentException(
                    "Map must be have height and width of at least 10 pixels");
        }
        for (DirectConnection currConn : connections) {
            City city0 = currConn.getCity0();
            City city1 = currConn.getCity1();
            if (!(cities.contains(city0) && cities.contains(city1))) {
                throw new IllegalArgumentException(
                        "Connections must be between cities in given set");
            }
        }
        this.cities = Set.copyOf(cities);
        this.directConnections = Set.copyOf(connections);
        this.width = width;
        this.height = height;
        buildAllFeasibleDestinations();
    }

    public static TrainsMap createTrainsMapWithDefaultSize(Set<City> cities,
                                                           Set<DirectConnection> connections) {
        return new TrainsMap(cities, connections, 800, 800);
    }

    /**
     * Getter for height.
     *
     * @return height of Map in pixels
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * Getter for width.
     *
     * @return width of the Map in pixels
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * @return String object representing this TrainsMap.TrainsMap
     */
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("map\n");
        out.append(" Cities: \n");
        for (City c : cities) {
            out.append(" " + c.toString() + " "); // print each city
        }
        out.append("\n");
        for (DirectConnection c : directConnections) {
            out.append(c.toString());
        }
        return out.toString();
    }

    /**
     * @return duplicate Set of the cities in this TrainsMap.TrainsMap
     */
    public Set<City> getCities() {
        Set<City> result = new HashSet<>();
        for (City c : this.cities) {
            result.add(c);
        }
        return result;
    }

    /**
     * @return Set of strings representing the city names on this
     * TrainsMap.TrainsMap
     */
    public Set<String> getCityNames() {
        Set<String> names = new HashSet<>();
        for (City c : this.cities) {
            names.add(c.getName());
        }
        return names;
    }

    /**
     * @return duplicate Set of direct connections between cities on this
     * TrainsMap.TrainsMap
     */
    public Set<DirectConnection> getDirectConnections() {
        Set<DirectConnection> result = new HashSet<>();
        for (DirectConnection c : this.directConnections) {
            result.add(c);
        }
        return result;
    }

    /**
     * Method to calculate all feasible destinations (two cities connected by a
     * path on the gameboard). Converts TrainMap into TrainsMap.Graph object and
     * uses getFeasibleDestinationDFSUtil
     *
     * @return Set of destinations which represent pairs of cities that are
     * connected by a path
     */
    public Set<Destination> getAllFeasibleDestinations() {
        return this.availableDestinations;
    }

    /**
     * Method to calculate all feasible destinations (two cities connected by a
     * path on the gameboard). Converts TrainMap into TrainsMap.Graph object and
     * uses getFeasibleDestinationDFSUtil
     *
     * @return Set of destinations which represent pairs of cities that are
     * connected by a path
     */
    private void buildAllFeasibleDestinations() {
        Graph graph = convertToGraph();
        Set<Destination> feasibleDestinations = new HashSet<>();

        for (City c : this.cities) { // call recursive DFS Util on each city
            Set<City> visited = new LinkedHashSet<>();
            Graph.getFeasibleDestinationsDFSUtil(graph, c, visited,
                    feasibleDestinations); // mutates feasibles
        }
        this.availableDestinations = feasibleDestinations;
    }

    /**
     * Private method in order to create TrainsMap.Graph object from TrainMap
     * object
     *
     * @return graph representation of this map
     */
    private Graph convertToGraph() {
        // construct an adjacency list of cities (length of connection is
        // irrelevant here)
        Graph graph = new Graph();
        City root = null;
        for (City c : this.cities) { // add a vertex for each city
            if (root == null) {
                root =
                        c;                               // store the first
                // city as root
            }
            graph.addDisconnectedCity(c);
        }

        for (DirectConnection conn : this.directConnections) { // add an edge
            // for each direct connection
            graph.addEdge(conn.getCity0(), conn.getCity1());
        }
        return graph;
    }

    /**
     * Function to return whether two cities are connected by a path on this
     * map.
     *
     * @param city0 - City 1 object
     * @param city1 - City 2 object
     *
     * @return boolean representing whether cities are connected
     */
    public boolean areCitiesConnected(City city0, City city1) {
        if (city0 == null || city1 == null || city0.equals(city1)) {
            return false;
        }
        // get a graph representation of this map
        Graph map = this.convertToGraph();

        // call graph function to return whether edges are connected
        Set<City> visited = new HashSet<>();
        Graph.getConnectedCitiesDFSUtil(map, city0, visited);

        return visited.contains(city1);
    }

    /**
     * Method to get the coordinates of a city given its name. Used when
     * constructing a player game state after parsing a map where we only have
     * the name of the city without its coordinates
     *
     * @param cityName a name of a city on this map
     *
     * @return Coord corresponding to the given city
     */
    public Coord getCoordGivenCityName(String cityName) {
        for (City city : this.cities) {
            if (city.getName().equals(cityName)) {
                return city.getLocation();
            }
        }
        throw new IllegalArgumentException(
                "Unable to getCoordOnMapGivenCityName. The given city " +
                "name: " + cityName + " does not " + "exist on the map.");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TrainsMap trainsMap = (TrainsMap) o;
        return height == trainsMap.height && width == trainsMap.width &&
               cities.equals(trainsMap.cities) &&
               directConnections.equals(trainsMap.directConnections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cities, directConnections, height, width);
    }
}
