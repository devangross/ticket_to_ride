package strategy;

import map.City;
import map.ColorTrains;
import map.Coord;
import map.Destination;
import map.DirectConnection;
import map.TrainsMap;
import state.PlayerGameState;

import java.util.Set;

public class NonSequiturStrategy implements IPlayerStrategy {
    private final City kinder = new City("Kindergarten", new Coord(0f, 0f));
    private final City whiteHouse =
            new City("White House", new Coord(.5f, .5f));
    private final City land = new City("Land", new Coord(.5f, 1f));
    private final City sea = new City("Sea", new Coord(.2f, .2f));

    /**
     * Given a set of destinations, a strategy will return a pair of chosen
     * destinations.
     *
     * @return two destinations
     */
    @Override
    public Pair<Destination> chooseTwoDestinations(
            Set<Destination> givenDestinations) {
        return new Pair<>(new Destination(kinder, whiteHouse),
                new Destination(land, sea));
    }

    /**
     * Given a current player game state, a strategy will return a move based on
     * it's strategy implementation.
     *
     * @return a destination or true, corresponding to a destination to acquire
     * or request two more color cards
     */
    @Override
    public Move makeMove(PlayerGameState currentPGS) {
        Move<DirectConnection> move = new Move<>();
        move.setMove(new DirectConnection(land, sea, 5, ColorTrains.BLUE));
        return move;
    }

    /**
     * Method to suggest a map when called by referee on notification of
     * tournament start. NOTE: hardcoded a certain internal map because
     * strategies are written as hardcode
     */
    @Override
    public TrainsMap suggestMap() {
        return null;
    }
}
