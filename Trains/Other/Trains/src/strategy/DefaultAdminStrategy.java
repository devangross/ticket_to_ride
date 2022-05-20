package strategy;

import map.Destination;
import state.ColorCard;

import java.util.Collections;
import java.util.List;

public abstract class DefaultAdminStrategy implements IAdminStrategy {

    /**
     * Method to shuffle the list of Destinations, default strategy for a
     * referee.
     *
     * @param allDestinations all destinations derived from the TrainsMap
     *
     * @return shuffled list
     */
    List<Destination> shuffleDestinations(List<Destination> allDestinations) {
        Collections.shuffle(allDestinations);
        return allDestinations;
    }

    /**
     * Shuffles the given list of ColorCard, default strategy for Referee (250
     * cards)
     *
     * @param gameCards list of colored cards initialized in the Trains game
     *
     * @return shuffled list of colored cards
     */
    List<ColorCard> shuffleColoredCards(List<ColorCard> gameCards) {
        Collections.shuffle(gameCards);
        return gameCards;
    }

}

