package strategy;

import map.City;
import map.ColorTrains;
import map.Coord;
import map.DirectConnection;
import state.PlayerGameState;

/**
 * Class to represent a cheater strategy which is identical to BuyNowStrategy
 * except this player will suggest a random map on
 */
public class CheaterStrategy extends BuyNowStrategy {
    private final City cat =
            new City("catti13lydepartureplaymat", new Coord(.5f, 1f));
    private final City vegan =
            new City("vegantrappedeve578nmosaic", new Coord(.2f, .2f));

    /**
     * This strategy will choose a random connection to acquire when asked to
     * make a move. Returns a connection between two cities generated with a
     * random hash to prevent cities from being valid on map.
     *
     * @param currentPGS the current PlayerGameState
     */
    @Override
    public Move makeMove(PlayerGameState currentPGS) {
        Move<DirectConnection> move = new Move<>();
        move.setMove(new DirectConnection(cat, vegan, 5, ColorTrains.BLUE));
        return move;
    }
}
