package strategy;

import map.Destination;
import state.ColorCard;

import java.util.Collections;
import java.util.List;

public class OrderedDestSameCards implements IAdminStrategy {
    /**
     * Method to abstract of the order of which to select (enough) destinations
     * from the map for players to pick from
     *
     * @param allFeasible all feasible destinations in the current game's map.
     *
     * @return ordered array of destinations based on implementation.
     */
    @Override
    public List<Destination> orderDestinations(List<Destination> allFeasible) {
        Collections.sort(allFeasible);
        return allFeasible;
    }

    /**
     * Method to abstract over the sequence color cards are distributed to
     * players. This implementation returns the card without changing there
     * order
     *
     * @return ColorCard[] ordered based on implementation of selection
     * sequence.
     */
    @Override
    public List<ColorCard> orderColorCards(List<ColorCard> allCards) {
        return allCards;
    }
}
