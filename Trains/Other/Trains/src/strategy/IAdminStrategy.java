package strategy;

import map.Destination;
import state.ColorCard;

import java.util.List;

public interface IAdminStrategy {

    /**
     * Method to abstract of the order of which to select (enough) destinations
     * from the map for players to pick from
     *
     * @param allFeasible all feasible destinations in the current game's map.
     *
     * @return ordered array of destinations based on implementation.
     */
    List<Destination> orderDestinations(List<Destination> allFeasible);

    /**
     * Method to abstract over the sequence color cards are distributed to
     * players.
     *
     * @return ColorCard[] ordered based on implementation of selection
     * sequence.
     */
    List<ColorCard> orderColorCards(List<ColorCard> allCards);
}
