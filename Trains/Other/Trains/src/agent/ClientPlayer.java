package agent;

import map.TrainsMap;

public class ClientPlayer extends PlayerAgent {

    private final TrainsMap map;

    /**
     * Default constructor to construct an instance of a ClientPlayer. Ensures
     * that the strategy is not null and the name is between 1 and 50 letters.
     * Assigns the birthday of this ClientPlayer to the current time. Always
     * returns the given map in `start()`.
     */
    public ClientPlayer(String name, String strategyPath, TrainsMap map) {
        super(name, strategyPath);
        this.map = map;
    }

    @Override
    public TrainsMap start() {
        return this.map;
    }
}
