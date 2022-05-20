package strategy;

import map.Destination;
import map.DirectConnection;
import state.PlayerGameState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Class to represent an abstract strategy which implements
 * acquireFirstLexConnection for reuse across multiple strategies.
 */
public abstract class AbstractStrategy implements IPlayerStrategy {

    /**
     * The strategy acquires the first connection from a list of available
     * connections for which it has enough cards and rails; if there aren’t any,
     * it asks for additional cards. Given a PlayerGameState it determines
     * available DirectConnections, sorts them, and iterates through attempting
     * to acquire each, if no DC can be acquired return additionalCards Move.
     */
    public Move<?> acquireFirstLexConnection(PlayerGameState currentPGS) {
        List<DirectConnection> availConns =
                new ArrayList<>(currentPGS.determineAvailableConnections());
        Collections.sort(availConns);
        for (DirectConnection connection : availConns) {
            if (currentPGS.canAcquire(connection)) {
                Move<DirectConnection> retMove = new Move<>();
                retMove.setMove(connection);
                return retMove;
            }
        }
        Move<Boolean> retMove = new Move<>();
        retMove.setMove(true);
        return retMove;
        // request additional cards
    }

    /**
     * Abstract method which chooses the last two from a set of given
     * destinations.
     */
    public Pair<Destination> chooseLastDestinations(
            Set<Destination> givenDestinations) {
        if (givenDestinations.size() < MIN_DESTINATIONS) {
            throw new IllegalArgumentException(
                    "Must be given " + MIN_DESTINATIONS + "destinations to " +
                    "chooseTwoDestinations.");
        }
        List<Destination> listDests = new ArrayList<>(givenDestinations);
        Collections.sort(listDests);
        Destination d1 = listDests.get(listDests.size() - 1);
        Destination d2 = listDests.get(listDests.size() - 2);

        return new Pair<>(d1, d2);
    }
}
