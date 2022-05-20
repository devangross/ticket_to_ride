package map;

import java.util.HashSet;
import java.util.Set;

/**
 * Class containing the main function which creates dummy inputs cities and
 * connections and constructs a TrainsMap.TrainsMap then gets all feasible
 * destinations and prints them before exiting.
 */
public class Trains {
    public static void main(String[] args) {
        TrainsMap gameTrainsMap = createExampleMap();
        Set<Destination> feasibleDestinations =
                gameTrainsMap.getAllFeasibleDestinations();

        System.exit(0);
    }

    private static TrainsMap createExampleMap() {
        Set<City> cities = new HashSet<City>(4);
        Set<DirectConnection> connections = new HashSet<DirectConnection>(4);
        City LA = new City("LA", new Coord(.1f, .1f));
        City SF = new City("SF", new Coord(.1f, .4f));
        City SAC = new City("Sac", new Coord(.4f, .1f));
        City SLO = new City("SlO", new Coord(.8f, .8f));
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

}
