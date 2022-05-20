package strategy;

import map.Destination;
import map.TrainsMap;
import state.PlayerGameState;

import java.util.Set;

/**
 * Interface for Player strategies. Must be able to select destinations or
 * makeMove.
 */
public interface IPlayerStrategy {
    int MIN_DESTINATIONS = 2; // public final by default

    /**
     * Given a set of destinations, a strategy will return a pair of chosen
     * destinations.
     *
     * @return two destinations
     */
    Pair<Destination> chooseTwoDestinations(Set<Destination> givenDestinations);

    /**
     * Given a current player game state, a strategy will return a move based on
     * it's strategy implementation.
     *
     * @return a destination or true, corresponding to a destination to acquire
     * or request two more color cards
     */
    Move makeMove(PlayerGameState currentPGS);

    TrainsMap suggestMap();
}
