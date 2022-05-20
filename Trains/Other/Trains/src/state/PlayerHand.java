package state;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import map.ColorTrains;
import map.Destination;
import map.DirectConnection;
import strategy.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents board game information specific to a player and not viewable to
 * other players (minus ownedConnections) in a PlayerGameState. True versions of
 * this class will be maintained by the Referee to ensure validity of moves.
 * Represents the PRIVATE info about a player whereas PGS represents PRIVATE +
 * PUBLIC info about a player
 */
public class PlayerHand {
    // HashSet<DirectConnection> represents the Direct Connections owned by
    // that player
    private final HashSet<DirectConnection> ownedConnections;
    private final HashMap<ColorTrains, Integer> colorCardCount;
    private final int rails;
    private final List<Destination> destinations;
            // List to avoid null before picking destinations

    /**
     * Main Constructor for a new PlayerHand
     *
     * @param ownedConnections set of all connections owned by this player
     * @param colorCards       map of color to num cards available to this
     * @param rails            initial number of rails
     * @param destinations     the chosen destinations, initialized as empty
     *                         list before choosing
     */
    public PlayerHand(Set<DirectConnection> ownedConnections,
                      Map<ColorTrains, Integer> colorCards, int rails,
                      List<Destination> destinations) {
        this.ownedConnections = new HashSet<>(ownedConnections);
        if (!colorCardsIsValid(colorCards)) {
            throw new IllegalArgumentException(
                    "Colored cards input must have an entry for each " +
                    "color RED, GREEN, BLUE, or WHITE and values >= 0");
        }
        this.colorCardCount = new HashMap<>(colorCards);
        this.rails = rails;
        this.destinations = destinations;
    }

    /**
     * Constructor to initialize a player hand before the player chooses their
     * destinations.
     *
     * @param rails rails
     * @param cards initial drawing of colored cards
     *
     * @return the new PlayerHand
     */
    public static PlayerHand initializePlayerHand(int rails,
                                                  List<ColorCard> cards) {
        HashMap<ColorTrains, Integer> emptyCardsMap = new HashMap<>();
        emptyCardsMap.put(ColorTrains.RED, 0);
        emptyCardsMap.put(ColorTrains.GREEN, 0);
        emptyCardsMap.put(ColorTrains.BLUE, 0);
        emptyCardsMap.put(ColorTrains.WHITE, 0);
        return new PlayerHand(new HashSet<>(),
                addColorCardArrayToCountMap(emptyCardsMap, cards), rails,
                new ArrayList<>());
    }

    /**
     * Method to add a ColorCard[] to a HashMap<ColorTrains, Integer>
     */
    public static HashMap<ColorTrains, Integer> addColorCardArrayToCountMap(
            HashMap<ColorTrains, Integer> initialCardsMap,
            List<ColorCard> cards) {

        for (ColorCard c : cards) { // for each card in the array, add1 to
            // existing count in map
            switch (c.getColor()) {
                case RED:
                    initialCardsMap.put(ColorTrains.RED,
                            initialCardsMap.get(ColorTrains.RED) + 1);
                    break;
                case BLUE:
                    initialCardsMap.put(ColorTrains.BLUE,
                            initialCardsMap.get(ColorTrains.BLUE) + 1);
                    break;
                case GREEN:
                    initialCardsMap.put(ColorTrains.GREEN,
                            initialCardsMap.get(ColorTrains.GREEN) + 1);
                    break;
                case WHITE:
                    initialCardsMap.put(ColorTrains.WHITE,
                            initialCardsMap.get(ColorTrains.WHITE) + 1);
                    break;
                default:
                    throw new IllegalArgumentException(
                            "Invalid Color passed to " +
                            "initializePlayerHand: " + c.getColor());
            }
        }
        return initialCardsMap;

    }

    /**
     * Get an empty initialized cards map.
     */
    public static HashMap<ColorTrains, Integer> getEmptyCardsMap() {
        HashMap<ColorTrains, Integer> cardsMap = new HashMap<>();
        cardsMap.put(ColorTrains.RED, 0);
        cardsMap.put(ColorTrains.GREEN, 0);
        cardsMap.put(ColorTrains.BLUE, 0);
        cardsMap.put(ColorTrains.WHITE, 0);
        return cardsMap;
    }

    /**
     * Return a copy of this PlayerHand with the given cards array added.
     */
    public PlayerHand addCards(List<ColorCard> cards) {
        HashMap<ColorTrains, Integer> currentCards =
                new HashMap<>(this.colorCardCount);
        HashMap<ColorTrains, Integer> updatedCards =
                addColorCardArrayToCountMap(currentCards, cards);
        return new PlayerHand(this.getOwnedConnections(), updatedCards,
                this.rails, this.destinations);
    }

    /**
     * Return a copy of this PlayerHand with the given destinations added.
     */
    public PlayerHand addDestinations(Pair<Destination> chosenDests) {
        List<Destination> chosenList = new ArrayList<>(
                Arrays.asList(chosenDests.getFirst(), chosenDests.getSecond()));
        return new PlayerHand(this.getOwnedConnections(),
                this.getColorCardCount(), this.rails, chosenList);
    }

    /**
     * Method to determine whether a given Map<ColorTrains, Integer> is valid.
     * It is valid if it contains every color as a key and all Integer values
     * are greater than or equal to zero.
     */
    private boolean colorCardsIsValid(Map<ColorTrains, Integer> colorCards) {
        boolean result;
        //check that we have valid map keys
        result = colorCards.containsKey(ColorTrains.RED) &&
                 colorCards.containsKey(ColorTrains.GREEN) &&
                 colorCards.containsKey(ColorTrains.BLUE) &&
                 colorCards.containsKey(ColorTrains.WHITE);
        // check that values for keys are valid
        result = result && colorCards.get(ColorTrains.RED) >= 0 &&
                 colorCards.get(ColorTrains.GREEN) >= 0 &&
                 colorCards.get(ColorTrains.BLUE) >= 0 &&
                 colorCards.get(ColorTrains.WHITE) >= 0;
        return result;
    }

    public HashSet<DirectConnection> getOwnedConnections() {
        return new HashSet<>(this.ownedConnections);
        // shallow copy because DirectConnections are immutable
    }

    public HashMap<ColorTrains, Integer> getColorCardCount() {
        return new HashMap<>(this.colorCardCount);
        // shallow copy because ColorTrains and Integer are immutable
    }

    public int getRails() {
        return this.rails;
    }

    public List<Destination> getDestinations() {
        return this.destinations;
    }

    /**
     * Mutator method in order to update the internal fields after a move.
     * Referee will only call this after validating move, ensuring proper
     * mutation of the internal representation of all player game states.
     *
     * @param connection to be added
     *
     * @return new updated PlayerHand after acquiring DirectConnection NOTE:
     * This method alone DOES allow for acquiring a connection owned by another
     * player but the referee will ensure that that it is an illegal move and
     * kick the player if so.
     */
    public PlayerHand handleAddConnection(DirectConnection connection) {
        int rails = connection.getLength();

        if (this.rails >= rails &&
            this.colorCardCount.get(connection.getColor()) >=
            connection.getLength()) {
            HashSet<DirectConnection> newConnect =
                    new HashSet<>(this.ownedConnections);
            newConnect.add(connection);
            HashMap<ColorTrains, Integer> newCards =
                    new HashMap<>(this.colorCardCount);
            newCards.put(connection.getColor(),
                    newCards.get(connection.getColor()) -
                    connection.getLength());
            int new_rail = this.rails - rails;

            this.ownedConnections.add(connection);
            return new PlayerHand(newConnect, newCards, new_rail,
                    this.destinations);

        }
        throw new IllegalStateException(
                "Insufficient game pieces to acquire given connection: " +
                connection);
    }

    /**
     * Determine if the Player has sufficient rails and cards to acquire a given
     * connection.
     *
     * @param dc DirectConnection to acquire
     *
     * @return true if they have sufficient game pieces.
     */
    public boolean hasSufficientRailsAndCards(DirectConnection dc) {
        int curr_rails = this.getRails();
        if (curr_rails <
            dc.getLength()) { // if player doesn't have enough rails to
            // exchange, return false
            return false;
        }
        // if player doesn't have enough color cards for connection
        return this.getColorCardCount().get(dc.getColor()) >= dc.getLength();
    }

    /**
     * Get a copy of this PlayerHand by safely copying each field and
     * constructing a new PlayerHand.
     */
    public PlayerHand getCopy() {
        HashMap<ColorTrains, Integer> colorCardsCopy = new HashMap<>();
        for (ColorTrains color : this.colorCardCount.keySet()) {
            colorCardsCopy.put(color, this.colorCardCount.get(color));
            // no ref exposed bc java.lang.Integer is immutable
        }
        return new PlayerHand(new HashSet<>(this.ownedConnections),
                colorCardsCopy, this.rails, this.destinations);
        // no reference exposed because:
        // ownedConns - we make a shallow copy, DC's are immutable
        // colorCards is deep copied into a new Map
        // rails - ints are passed by value
        // Destinations are immutable
    }

    /**
     * Overridden equals method to compare fields in a PlayerHand
     *
     * @param o Other object
     *
     * @return true if the given object's fields are equal to this object
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof PlayerHand) {
            PlayerHand other = (PlayerHand) o;
            boolean result;

            result = other.getOwnedConnections().containsAll(this.ownedConnections);
            result = result &&
                     this.colorCardCount.equals(other.getColorCardCount());
            result = result && (this.rails == other.getRails());
            result = result &&
                     (this.destinations.containsAll(other.getDestinations()));
            return result;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Integer i : this.colorCardCount.values()) {
            result.append(i.toString());
        }
        result.append("\n" + this.hashCode());
        return result.toString();
    }
}
