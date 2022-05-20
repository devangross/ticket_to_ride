package agent;

import map.Destination;
import map.TrainsMap;
import state.ColorCard;
import state.PlayerGameState;
import state.PlayerHand;
import strategy.IPlayerStrategy;
import strategy.Move;
import strategy.Pair;
import strategy.StrategyLoader;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class PlayerAgent implements IPlayer {
    private final LocalDateTime birthday;
    private final String name;
    private PlayerGameState gameState;
    private final IPlayerStrategy strategy;
    // boolean for testing winning conditions
    private boolean gameWinner;

    /**
     * Default constructor to construct an instance of a PlayerAgent. Ensures
     * that the strategy is not null and the name is between 1 and 50 letters.
     * Assigns the birthday of this PlayerAgent to the current time.
     */
    public PlayerAgent(String name, IPlayerStrategy strategy) {
        if (strategy == null || name.length() < 1 || name.length() > 50 ||
            !name.matches("[a-zA-Z]+")) {
            throw new IllegalArgumentException(
                    "Strategy cannot be null and name must be non empty, " +
                    "less than 50 characters and [a-zA-Z], name was: " + name);
        }
        this.name = name;
        this.strategy = strategy;
        this.birthday = LocalDateTime.now();
    }

    /**
     * Initializes a PlayerAgent with a Strategy from a given path. Ensures that
     * the strategy is not empty and the name is between 1 and 50 letters.
     * Assigns the birthday of this PlayerAgent to the current time.
     *
     * @throws IllegalArgumentException from StrategyLoader when the path is
     *                                  invalid or an IStrategy component is not
     *                                  found
     */
    public PlayerAgent(String name, String pathToStrategy) {
        if (pathToStrategy.isEmpty() || name.length() < 1 ||
            name.length() > 50 || !name.matches("[a-zA-Z]+")) {
            throw new IllegalArgumentException(
                    "Strategy cannot be empty and name must be non empty, " +
                    "less than 50 characters and [a-zA-Z]");
        }
        this.name = name;
        this.strategy = StrategyLoader.loadStrategyFromPath(pathToStrategy);
        this.birthday = LocalDateTime.now();
    }

    /**
     * sets up this player with the basic game pieces
     *
     * @param map   for the current game
     * @param rails to act as connection length currency (default 45)
     * @param cards to act as connection color currency (default 5 random
     *              cards)
     */
    @Override
    public void setup(TrainsMap map, int rails, List<ColorCard> cards) {
        PlayerHand hand = PlayerHand.initializePlayerHand(rails, cards);
        PlayerGameState gameStateNoDestinations =
                new PlayerGameState(map, hand, new LinkedList<>());
        this.gameState = gameStateNoDestinations;
    }

    /**
     * Asks this player to pick some destinations for the game relative to the
     * given map.
     *
     * @return array of destinations not chosen by the player.
     */
    @Override
    public List<Destination> pick(List<Destination> destChoices) {
        if (this.strategy == null) {
            throw new IllegalArgumentException(
                    "Unable to pick destinations, PlayerAgent strategy not " +
                    "yet set.");
        }

        // get chosen destinations according to this.strategy
        Set<Destination> destChoicesCopy = new HashSet<>(destChoices);
        Pair<Destination> chosen =
                this.strategy.chooseTwoDestinations(destChoicesCopy);

        // now that we've chosen our destinations we construct a new PGS
        // containing the chosen
        // destinations in its playerHand
        this.gameState = this.gameState.addChosenDestinations(chosen);

        HashSet<Destination> chosenSet = new HashSet<>();
        chosenSet.add(chosen.getFirst());
        chosenSet.add(chosen.getSecond());
        // remove the chosen destinations from the inputDestinations to get
        // rejected destinations
        destChoicesCopy.removeAll(chosenSet);
        return new ArrayList<>(destChoicesCopy);
    }

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
    @Override
    public Move play(PlayerGameState PGS) {
        this.gameState =
                PGS; // replace the current PGS with the updated before
        // making a move
        Move chosen = this.strategy.makeMove(this.gameState);
        return chosen;
    }

    /**
     * Hands this player some cards (2).
     */
    @Override
    public void more(List<ColorCard> more) {
        this.gameState = this.gameState.addCards(more);
    }

    /**
     * Did this player win?
     *
     * @param b true if yes, false otherwise.
     */
    @Override
    public void win(Boolean b) {
        //      System.out.println(this.name + " won!");
        //      System.out.println(this.name + " lost.");
        this.gameWinner = b;
    }

    /**
     * Method to get this player's player game state to check attributes for
     * testing.
     */
    public PlayerGameState getGameState() {
        return this.gameState.getCopy();
    }

    /**
     * Method to get the birth of this player for age sorting purposes.
     *
     * @return LocalDateTime birthday of construction time
     */
    public LocalDateTime getBirthday() {
        return this.birthday;
    }

    /**
     * Getter to get this player's name
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Called by the referee to indicate a tournament this player registered for
     * is starting. This player responds with a trainsMap object determined by
     * its strategy.
     */
    @Override
    public TrainsMap start() {
        //System.out.println("Your tournament is starting!");
        return this.strategy.suggestMap();
    }

    /**
     * Called by the referee on all players that participated in the tournament
     * after it has concluded to notify them whether they won the tournament or
     * not.
     */
    @Override
    public void end(boolean winner) {
        if (winner) {
            //System.out.println(name + " you won the tournament!");
        } else {
            //System.out.println(name + " you did not win the tournament.");
        }
    }

    /**
     * To string method for this player for testing output
     */
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("name: " + this.name + " ");
        result.append("birthday: " + this.birthday + ", ");
        if (this.gameState != null) {
            result.append("gs: " + this.gameState);
        } else {
            result.append("gs: null, ");

        }
        result.append(this.strategy.toString());
        result.append(", winner: " + this.gameWinner);
        return result.toString();
    }

    /**
     * Getter to determine if this player won or loss.
     *
     * @return true if win
     */
    public boolean isGameWinner() {
        return gameWinner;
    }

    /**
     * Overridden equals which assumes names are unique. Included for best
     * practices when using compareTo.
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof PlayerAgent) {
            PlayerAgent other = (PlayerAgent) o;

            return this.name.equals(other.getName());
        }
        return false;
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * @param o the object to be compared.
     *
     * @return a negative integer, zero, or a positive integer as this object is
     * less than, equal to, or greater than the specified object.
     *
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(Object o) {
        if (o instanceof IPlayer) {
            IPlayer other = (IPlayer) o;

            if (this.birthday.isBefore(other.getBirthday())) {
                return -1;
            } else if (this.birthday.isAfter(other.getBirthday())) {
                return 1;
            } else { // the birthdays are equal
                return 0;
            }
        } else {
            throw new ClassCastException(
                    "Cannot compare PlayerAgent to non PlayerAgent object");
        }
    }
}
