package strategy;

import map.Destination;
import map.ExampleMap;
import map.TrainsMap;
import state.PlayerGameState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Class to represent the Hold Ten Strategy. It extends the abstract
 * getFirstLexConnStrategy class to use acquireFirstLexConn. Choose
 * destinations: sorts given destinations and chooses the first two. Make move:
 * Requests more cards if the player has less than 10, else uses
 * acquireFirstLexConnection to choose the first connection it can acquire from
 * the available.
 */
public class HoldTenStrategy extends AbstractStrategy implements IPlayerStrategy {
    /**
     * Given a set of destinations, this strategy will sort the given set
     * lexicographically and returns the first two elements of the sorted list.
     *
     * @return a Pair of Destination
     */
    @Override
    public Pair<Destination> chooseTwoDestinations(
            Set<Destination> givenDestinations) {
        if (givenDestinations.size() < MIN_DESTINATIONS) {
            throw new IllegalArgumentException(
                    "Must be given " + MIN_DESTINATIONS + "destinations to " +
                    "chooseTwoDestinations.");
        }
        // get a copy so we don't sort the passed list
        List<Destination> listDests = new ArrayList<>(givenDestinations);

        Collections.sort(listDests);
        Destination d1 = listDests.get(0);
        Destination d2 = listDests.get(1);

        return new Pair<>(d1, d2);
    }

    /**
     * Given a current player game state, a strategy will return the updated
     * player game state mutated with their chosen move.
     *
     * @return Move to acquire connection or request cards.
     */
    @Override
    public Move makeMove(PlayerGameState currentPGS) {
        int totalCards = currentPGS
                .getCardsMap()
                .values()
                .stream()
                .reduce(0, Integer::sum);
        if (totalCards < 10) {
            Move<Boolean> cards = new Move<>();
            cards.setMove(true);
            return cards;
        } else { //  we have 10 cards
            return this.acquireFirstLexConnection(currentPGS);
        }
    }

    /**
     * Method to suggest a map when called by referee on notification of
     * tournament start. NOTE: hardcoded a certain internal map because
     * strategies are written as hardcode
     */
    @Override
    public TrainsMap suggestMap() {
        return ExampleMap.createBigBostonMap();
    }
}
