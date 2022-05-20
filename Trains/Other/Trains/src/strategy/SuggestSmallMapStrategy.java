package strategy;

import map.Destination;
import map.ExampleMap;
import map.TrainsMap;
import state.PlayerGameState;

import java.util.Set;

public class SuggestSmallMapStrategy extends AbstractStrategy implements IPlayerStrategy {
    /**
     * Given a set of destinations, a strategy will return a pair of chosen
     * destinations.
     *
     * @return two destinations
     */
    @Override
    public Pair<Destination> chooseTwoDestinations(
            Set<Destination> givenDestinations) {
        return this.chooseLastDestinations(givenDestinations);
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

    @Override
    public TrainsMap suggestMap() {
        return ExampleMap.createExampleMap();
    }
}
