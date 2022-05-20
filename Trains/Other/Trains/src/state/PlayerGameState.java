package state;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import map.ColorTrains;
import map.Destination;
import map.DirectConnection;
import map.TrainsMap;
import strategy.Pair;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Class to represent information known to an individual player in a game.
 * Specifically this includes their number of rails, color cards, two chosen
 * destinations, their owned connections, all connections owned by other players
 * and the TrainsMap agreed upon by all players. PRIVATE + PUBLIC info about a
 * player.
 * <p>
 * This will be the object handed to a player when it is their turn - created
 * from the referee true representations of all player's PlayerHand, Map of all
 * acquired connections (seen by all players), and the TrainsMap.
 */
public class PlayerGameState {
    // Note: this will probably be taken out of the class once we establish
    // protocol for registering players
    private final TrainsMap trainsMap;
    private final PlayerHand playerHand;
    private final LinkedList<HashSet<DirectConnection>> allOwnedConnections;
    // HashSet<DirectConnection> represents the Direct Connections owned by
    // that player

    /**
     * Constructor for PlayerGameState, all fields are final so the Referee must
     * create new PGS to update
     *
     * @param map                 the given TrainsMap
     * @param playerHand          the given PlayerHand
     * @param allOwnedConnections given
     */
    public PlayerGameState(TrainsMap map, PlayerHand playerHand,
                           LinkedList<HashSet<DirectConnection>> allOwnedConnections) {
        this.trainsMap = map;
        this.playerHand = playerHand;
        this.allOwnedConnections = allOwnedConnections;
    }

    /**
     * Method to generate a new player game state without a TrainsMap. To be
     * used by the referee class when creating a new updated player game state.
     */
    public PlayerGameState newPGSDefaultMap(PlayerHand playerHand,
                                            LinkedList<HashSet<DirectConnection>> ownedConnections) {
        return new PlayerGameState(this.trainsMap, playerHand,
                ownedConnections);
    }

    /**
     * Method to return a copy of this PlayerGameState but with the given pair
     * of destinations added to this playerHand TODO: used for testing, remove
     * if we handle the destinations in the referee
     */
    public PlayerGameState addChosenDestinations(Pair<Destination> chosen) {
        PlayerHand withChosen = this.playerHand.addDestinations(chosen);
        return newPGSDefaultMap(withChosen, this.allOwnedConnections);
    }

    /**
     * Method to return a copy of this PlayerGameState but with the given color
     * cards added to this playerHand
     */
    public PlayerGameState addCards(List<ColorCard> cards) {
        PlayerHand withAddedCards = this.playerHand.addCards(cards);
        return newPGSDefaultMap(withAddedCards, this.allOwnedConnections);
    }

    /**
     * Get a copy of this player game state, used to expose an unassociated copy
     * of this method this.trainsMap is final and other getters return copies or
     * immutable values
     *
     * @return copy of the PlayerGameState
     */
    public PlayerGameState getCopy() {
        PlayerHand copyPH =
                new PlayerHand(this.playerHand.getOwnedConnections(),
                        this.playerHand.getColorCardCount(),
                        this.playerHand.getRails(),
                        this.playerHand.getDestinations());
        return new PlayerGameState(this.trainsMap, copyPH,
                this.allOwnedConnections);
    }

    /**
     * Getter for TrainsMap in this PlayerGameState. Used by Player to view map
     * or retrieve for displaying on GUI.
     *
     * @return TrainsMap agreed upon by players
     */
    public TrainsMap getTrainsMap() {
        return this.trainsMap;
    }

    /**
     * Method to return a readonly list of all connections owned by all players
     *
     * @return unmodifiable map of Integer to HashSet of Direct Connections
     */
    public List<Set<DirectConnection>> getAllOwnedConnections() {
        return Collections.unmodifiableList(this.allOwnedConnections);
    }

    /**
     * Getter for the current HashSet of owned connections by this player
     *
     * @return HashSet
     */
    public HashSet<DirectConnection> getOwnedConnections() {
        return this.playerHand.getOwnedConnections();
    }

    /**
     * Getter for this.colorCardCount
     *
     * @return a mapping of color to number of cards
     */
    public HashMap<ColorTrains, Integer> getCardsMap() {
        return this.playerHand.getColorCardCount();
    }

    /**
     * Getter for this PGS's number of remaining rails
     *
     * @return int representing num rails
     */
    public int getRails() {
        return this.playerHand.getRails();
    }

    /**
     * Getter for destination 1, no need to copy as vertex fields are final.
     */
    public List<Destination> getDestinations() {
        return this.playerHand.getDestinations();
    }

    /**
     * Function to determine set of connections that have not been acquired
     * Note: Returns all unowned connections regardless of this players
     * resources
     *
     * @return set of direct connections on the TrainsMap that haven't been
     * acquired yet
     */
    public Set<DirectConnection> determineAvailableConnections() {
        HashSet<DirectConnection> ownedConnectionsFlat = new HashSet<>();

        for (HashSet<DirectConnection> set : this.allOwnedConnections) {
            ownedConnectionsFlat.addAll(set);
        }
        Set<DirectConnection> allConnections =
                this.trainsMap.getDirectConnections();
        //find the set difference between list of all connections and all
        // owned connections to get all available
        allConnections.removeIf(ownedConnectionsFlat::contains);
        return allConnections;
    }

    /**
     * Method to determine if this PlayerGameState is able to acquire the given
     */
    public boolean canAcquire(DirectConnection dc) {
        Set<DirectConnection> openConnections = determineAvailableConnections();
        if (!openConnections.contains(
                dc)) {// if the connection is already owned or invalid,
            // return false
            return false;
        }
        return this.playerHand.hasSufficientRailsAndCards(dc);
    }

    /**
     * @return String representing this PlayerGameState
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("PGS: ");

        result.append("Color Cards: ");
        Set<ColorTrains> colors = this.playerHand.getColorCardCount().keySet();
        for (ColorTrains color : colors) {
            result.append(color + ": ");
            result.append(this.playerHand.getColorCardCount().get(color) + " ");
        }

        result.append("rails: " + this.playerHand.getRails() + " ");

        for (Destination d : this.playerHand.getDestinations()) {
            result.append("Destination: " + d + " ");
        }

        return result.toString();
    }
}
