package agent;

import map.Destination;
import map.TrainsMap;
import state.ColorCard;
import state.PlayerGameState;
import strategy.Move;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface to represent the Player as an agent of actions in a game.
 * Specifically, the player makes decisions about destinations to choose in the
 * game setup and which action on a turn to take (Strategy package). Constructed
 * by the tournament manager. Players will only interact with the Referee.
 */
public interface IPlayer extends Comparable {

    /**
     * sets up this player with the basic game pieces
     *
     * @param map   for the current game
     * @param rails to act as connection length currency (default 45)
     * @param cards to act as connection color currency (default 5 random
     *              cards)
     *
     * @return the created PlayerGameState object for testing
     */
    void setup(TrainsMap map, int rails, List<ColorCard> cards);

    /**
     * Asks this player to pick some destinations for the game relative to the
     * given map.
     *
     * @return array of destinations not chosen by the player.
     **/
    List<Destination> pick(List<Destination> destChoices);

    /**
     * This player has been granted a turn. Given the current game state, they
     * can reply with their move which is one of: - an indication to get
     * more_cards -> response from referee more_cards below - a DirectConnection
     * of the connection to acquire -> void response from referee/win(false) is
     * invalid connection to acquire
     *
     * @param PGS current state of the game from the perspective of this
     *            player.
     */
    Move play(PlayerGameState PGS);

    /**
     * Hands this player some cards (2).
     */
    void more(List<ColorCard> more);

    /**
     * Did this player win?
     *
     * @param b true if yes, false otherwise.
     */
    void win(Boolean b);

    String getName();

    LocalDateTime getBirthday();

    /**
     * Called by the referee to indicate a tournament this player registered for
     * is starting. This player responds with a trainsMap object determined by
     * its strategy.
     */
    TrainsMap start();

    void end(boolean winner);

}
