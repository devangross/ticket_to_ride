package strategy;

import map.Destination;
import state.ColorCard;

import java.util.List;

public class RandomAdminStrategy extends DefaultAdminStrategy implements IAdminStrategy {

    @Override
    public List<Destination> orderDestinations(List<Destination> allFeasible) {
        return this.shuffleDestinations(allFeasible);
    }

    @Override
    public List<ColorCard> orderColorCards(List<ColorCard> allCards) {
        return this.shuffleColoredCards(allCards);
    }
}
