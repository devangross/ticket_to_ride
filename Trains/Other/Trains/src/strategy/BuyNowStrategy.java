package strategy;

import map.Destination;
import map.ExampleMap;
import map.TrainsMap;
import state.PlayerGameState;

import java.util.Set;

/**
 * Class to represent the Buy Now Strategy. It extends the abstract
 * getFirstLexConnStrategy class to use acquireFirstLexConn. Choose
 * destinations: sorts given destinations and chooses the last two. Make move:
 * Attempt to acquire a connection if possible, if not possible it requests more
 * cards.
 */
public class BuyNowStrategy extends AbstractStrategy implements IPlayerStrategy {
    /**
     * Method to choose the last two destinations from a given set of
     * Destinations.
     *
     * @param givenDestinations from referee
     *
     * @return a Pair representing the two chosen destinations.
     *
     * @throws IllegalArgumentException if given less than two destinations.
     */
    @Override
    public Pair<Destination> chooseTwoDestinations(
            Set<Destination> givenDestinations) {
        return this.chooseLastDestinations(givenDestinations);
    }

    /**
     * Method to represent the move algorithm for a Buy Now Strategy. Takes the
     * current PlayerGameState and immediately attempts to acquire any
     * connection possible using the same algorithm as Hold Ten
     * (lexicographically first connection), if it is not able to acquire a
     * connection it returns a Move object with Move .additionalCards set to
     * true.
     *
     * @param currentPGS the current PlayerGameState
     *
     * @return Move: additionalCards or DirectConnection representing chosen
     * connection
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
        // TODO refactor to use map big enough for 8 player game
        return ExampleMap.createCaliforniaMap();
    }
}
