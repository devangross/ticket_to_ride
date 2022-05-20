package strategy;

import map.Destination;
import map.TrainsMap;
import state.PlayerGameState;

import java.util.Set;

public class ThrowDestException extends AbstractStrategy implements IPlayerStrategy {
    /**
     * Given a set of destinations, a strategy will return a pair of chosen
     * destinations.
     *
     * @return two destinations
     */
    @Override
    public Pair<Destination> chooseTwoDestinations(
            Set<Destination> givenDestinations) {
        throw new IllegalThreadStateException("haha");
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
        return this.acquireFirstLexConnection(currentPGS);
    }

    /**
     * Method to suggest a map when called by referee on notification of
     * tournament start. NOTE: hardcoded a certain internal map because
     * strategies are written as hardcode
     */
    @Override
    public TrainsMap suggestMap() {
        throw new IllegalArgumentException("no map");
    }
}
