package state;

import map.ColorTrains;
import map.Destination;
import map.DirectConnection;
import map.TrainsMap;
import strategy.IAdminStrategy;
import strategy.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Class to represent the Gamestate for a Referee. Maintains true gamestates for
 * all the players and handles turns, valid moves and pieces in the control of
 * the referee (color cards not owned by players).
 */
public class RefereeGameState {
    private final LinkedList<PlayerHand> playerHands;
    // initially ordered by age, current first PlayerHand is current player

    private final LinkedList<HashSet<DirectConnection>> allOwnedConnections;
    // same strategy as playerHands, current HS<DC> is current player's owned

    private final List<ColorCard> remainingCards;
    private final TrainsMap map;
    // stores available feasible destination (cards) to be given to players
    List<Destination> availableDestinations;
    private IAdminStrategy rulebook;

    /**
     * Main constructor of a RefereeGameState. It creates a new LinkedList of
     * playerHands and allOwnedConnections and stores the given TrainsMap. Then
     * it orders the cards and destinations arrays if the rulebook is not null,
     * then assigns to remainingCards and availableDestinations.
     *
     * @param map      a TrainsMap
     * @param cards    a List of ColorCard objects
     * @param rulebook an IRefereeRule for optionally ordering
     *                 cards/destinations
     */
    public RefereeGameState(TrainsMap map, List<ColorCard> cards,
                            IAdminStrategy rulebook) {
        this.playerHands = new LinkedList<>();
        this.allOwnedConnections = new LinkedList<>();
        if (map == null) {
            throw new IllegalArgumentException("TrainsMap must not be null.");
        }
        this.map = map;
        if (rulebook == null) {
            this.remainingCards = new ArrayList<>(cards);
            this.availableDestinations =
                    new ArrayList<>(map.getAllFeasibleDestinations());
        } else {
            this.remainingCards =
                    rulebook.orderColorCards(new ArrayList<>(cards));
            this.availableDestinations = rulebook.orderDestinations(
                    new ArrayList<>(map.getAllFeasibleDestinations()));
            this.rulebook = rulebook;
        }

    }

    /**
     * Simplified RGS constructor which only takes a map and initializes the
     * cards to a random shuffled list. Also does not apply an IRefereeRule.
     *
     * @param map a TrainsMap
     *
     * @return constructed RefereeGameState
     */
    public static RefereeGameState RefereeGameStateShuffledCards(
            TrainsMap map) {
        return new RefereeGameState(map, initializeShuffledColoredCards(),
                null);
    }

    /**
     * Gets a shuffled equally distributed List of 200 ColorCards
     */
    public static List<ColorCard> initializeShuffledColoredCards() {
        List<ColorCard> gameCards = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            if (i % 4 == 0) {
                gameCards.add(new ColorCard(ColorTrains.WHITE));
            } else if (i % 4 == 1) {
                gameCards.add(new ColorCard(ColorTrains.RED));
            } else if (i % 4 == 2) {
                gameCards.add(new ColorCard(ColorTrains.GREEN));
            } else {
                gameCards.add(new ColorCard(ColorTrains.BLUE));
            }
        }
        Collections.shuffle(gameCards);
        return gameCards;
    }

    /**
     * Handle adding chosen destinations by a player after setup. Verifies that
     * they rejected 3 Destinations and that the rejected destinations are in
     * the choices. The referee generated choices, so we can trust that they are
     * valid w the map.
     *
     * @param choices Destination
     */
    public void handleDestinationSelection(List<Destination> choices,
                                           List<Destination> rejected)
            throws IllegalArgumentException {
        if (rejected.size() != 3 || !choices.containsAll(rejected)) {
            throw new IllegalArgumentException("Invalid chosen Destinations.");
        }
        List<Destination> chosen = new ArrayList<>(choices);
        chosen.removeAll(rejected);

        // the chosen destinations were valid so remove the chosen from this
        // .availableDestinations
        this.availableDestinations.removeAll(chosen);

        // then update the current stored playerHand
        Pair<Destination> chosenPair = new Pair<>(chosen.get(0), chosen.get(1));
        PlayerHand current = this.playerHands.remove(0);
        PlayerHand withDestinations = current.addDestinations(chosenPair);
        this.allOwnedConnections.remove();
        this.allOwnedConnections.addLast(
                withDestinations.getOwnedConnections());
        this.playerHands.addLast(withDestinations);
    }

    /**
     * Method called by the referee when a player requests additional
     * coloredCards MUTATION: update of the current player's hand with cards and
     * progression of turn to the next player. NOTE this method will throw an
     * illegalStateException when referee has less than two cards
     *
     * @return List of two randomly drawn colored cards unless there is 1 left,
     * gives last card
     */
    public List<ColorCard> drawTwoColoredCardsMove() {
        if (this.remainingCards.size() < 1) {
            return new ArrayList<>();
            // if there aren't enough cards
        }
        List<ColorCard> giveCards = new ArrayList<>();
        if (this.remainingCards.size() == 1) {
            ColorCard c1 = this.remainingCards.remove(0);
            giveCards.add(c1);
        } else {
            ColorCard c1 = this.remainingCards.remove(0);
            ColorCard c2 = this.remainingCards.remove(0);
            giveCards.add(c1);
            giveCards.add(c2);
        }

        // remove the current player hand set of ownedConnections from the
        // top of linked lists
        PlayerHand currentPlayer = this.playerHands.remove();
        this.allOwnedConnections.remove();

        PlayerHand newHand = currentPlayer.addCards(giveCards);
        this.playerHands.addLast(newHand);
        this.allOwnedConnections.addLast(newHand.getOwnedConnections());
        return giveCards;
    }

    /**
     * Method to be called by the referee once an acquireConnection move is
     * validated. MUTATES this.allOwnedConnections to add a given connection to
     * the set of DirectConnection's owned by the CURRENT PLAYER Returns a
     * boolean representing success or failure.
     */
    public void addAcquiredConnectionMove(DirectConnection connection) {
        if (!canCurrentPlayerAcquire(connection)) {
            throw new IllegalArgumentException("Cannot addAcquiredConnection.");
        }
        // remove the current player hand set of ownedConnections from the
        // top of linked lists
        PlayerHand currentPlayer = this.playerHands.remove();
        this.allOwnedConnections.remove();

        HashSet<DirectConnection> currentOwned =
                currentPlayer.getOwnedConnections();
        currentOwned.add(connection);
        //add the new set to the end of the linked list
        this.allOwnedConnections.addLast(currentOwned);
        // get the current player state, call handle add, put the result back
        PlayerHand updatedHand = currentPlayer.handleAddConnection(connection);
        this.playerHands.addLast(updatedHand);
    }

    /**
     * Method to remove a player from playerGameStates, equivalent to kicking
     * from game Simply remove their hand and owned connections from the linked
     * list. Other players will see that these connections are no longer owned
     * on the next update but cards will be lost currently.
     */
    public void removePlayer() {
        this.allOwnedConnections.remove();
        this.playerHands.remove();
    }

    /**
     * Method to determine if the next round is the final round based on the
     * current RefereeGameState. Definition of isNextRoundFinal: When one of the
     * player’s number of rails drops to 2, 1, or 0 at the end of a turn, each
     * of the remaining players get to take one more turn. Called after
     * acquireConnection - this is the only time that rails number is mutated.
     */
    public boolean isNextRoundFinal() {
        // check the most recent player's number of rails
        return this.playerHands.peekLast().getRails() < 3;
    }

    /**
     * Method to determine the number of unowned connections in the current RGS
     */
    public int getNumRemainingAvailConnections() {
        int numOwned = 0;
        for (HashSet<DirectConnection> ownedByAPlayer :
                this.allOwnedConnections) {
            numOwned += ownedByAPlayer.size();
        }
        int numDCsOnMap = this.map.getDirectConnections().size();

        return numDCsOnMap - numOwned;
    }

    /**
     * Method to yield the first five destinations in availableDestinations to
     * be sent to players in RefereeAgent.handleDestinationChoices.
     *
     * @return List<Destination> choices
     */
    public List<Destination> getFirstFiveDestinations() {
        // resort the available destinations after removing choices
        this.availableDestinations =
                rulebook.orderDestinations(this.availableDestinations);

        List<Destination> firstFive = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            firstFive.add(this.availableDestinations.get(i));
        }
        return firstFive;
    }

    /**
     * Function to get a copy of the PlayerHand of the player whose turn it is,
     *
     * @return PlayerHand representing the knowledge of the current player
     */
    public PlayerHand getCurrentPlayerHand() {
        return this.playerHands.peek().getCopy();
    }

    /**
     * Method to construct the current PlayerGameState for use in strategy
     * testing
     *
     * @return constructed PlayerGameState from the map, copy of playerHand and
     * copy of allOwnedConnections
     */
    public PlayerGameState getCurrentPlayerGameState() {
        return new PlayerGameState(this.map, this.getCurrentPlayerHand(),
                this.getAllOwnedConnections());
    }

    /**
     * Method to return a copy of this referee's allOwnedConnections Map.
     *
     * @return deep copy of this.allOwnedConnections.
     */
    public LinkedList<HashSet<DirectConnection>> getAllOwnedConnections() {
        return new LinkedList<>(this.allOwnedConnections);
        //Direct connections are immutable
    }

    /**
     * Getter to retrieve the number of remaining cards for game observance and
     * testing purposes
     *
     * @return integer representing remaining number of Color Cards
     */
    public int getNumRemainingCards() {
        return this.remainingCards.size();
    }

    /**
     * Method to return a list of Pla
     */
    public List<PlayerHand> getAllPlayerHands() {
        return this.playerHands;
    }

    /**
     * Function to determine set of connections that have not been acquired
     * Note: Returns all unowned connections regardless of this player's
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
        Set<DirectConnection> allConnections = this.map.getDirectConnections();
        //find the set difference between list of all connections and all
        // owned connections to get all available
        allConnections.removeIf(ownedConnectionsFlat::contains);
        return allConnections;
    }

    /**
     * Function to determine if it is legal to acquire this connection for the
     * currently active player Invariant: this playerHand's list of owned
     * connections is updated before calling this method
     *
     * @param dc connection to check the validity of
     *
     * @return true if the move is valid, else false
     */
    public boolean canCurrentPlayerAcquire(DirectConnection dc) {
        Set<DirectConnection> openConnections = determineAvailableConnections();

        if (!openConnections.contains(dc)) {
            return false;
        }

        PlayerHand currentPlayerHand = this.playerHands.peek();
        return currentPlayerHand.hasSufficientRailsAndCards(dc);
    }

    /**
     * Method to construct a player in this RGS. Given num rails it produces a
     * player with the first four cards on remainingCards, empty
     * ownedConnections, and empty destinations It mutates this.playerHands
     * LinkedList to add constructed player hand to the end and adds an empty
     * initial Set<Destination> to this.allOwnedConnections
     */
    public List<ColorCard> initializePlayerWithRailsAndDraw(int rails) {
        HashSet<DirectConnection> ownedConnections = new HashSet<>();
        this.allOwnedConnections.addLast(
                new HashSet<>()); // add this player to allOwnedConnections
        // with same Set stored in our managed PlayerGameState

        List<ColorCard> colorCards = new ArrayList<>();
        for (int i = 0; i < 4; i++) { // 4 times
            colorCards.add(this.remainingCards.remove(0));
        }

        HashMap<ColorTrains, Integer> cardsMap =
                PlayerHand.addColorCardArrayToCountMap(
                        PlayerHand.getEmptyCardsMap(), colorCards);
        PlayerHand newPlayerHand =
                new PlayerHand(ownedConnections, cardsMap, rails,
                        new ArrayList<>());
        this.playerHands.addLast(newPlayerHand);
        return colorCards;
    }

    /**
     * @return String representing this object
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("RefereeGameState\n");
        result.append("TrainsMap: " + this.map.toString());

        result.append("PlayerHand s: \n");
        for (PlayerHand k : this.playerHands) {
            result.append("ID: " + k);
            result.append(" -> PlayerHand: " + k.toString() + "\n");
        }

        result.append("Available Dests: \n");

        for (Destination d : this.availableDestinations) {
            result.append(d.toString());
        }
        return result.toString();
    }

    /////////// Below methods used only in tests ///////////

    /**
     * draw 4 random cards from this.remainingCards and return as a map of
     * ColorTrains to numCards MUTATES this.remainingCards by removing drawn
     * cards
     */
    private HashMap<ColorTrains, Integer> drawFourRandomColoredCards() {
        List<ColorCard> cards = new ArrayList<>(this.remainingCards);
        int size = cards.size();
        if (size < 4) {
            throw new IllegalArgumentException(
                    "Unable to draw, less than 4 remaining cards");
        }
        HashMap<ColorTrains, Integer> result = new HashMap<>();
        result.put(ColorTrains.RED, 0); // initialize values
        result.put(ColorTrains.GREEN, 0);
        result.put(ColorTrains.BLUE, 0);
        result.put(ColorTrains.WHITE, 0);

        // initialize all to zero
        int index1 = 0, index2 = 0, index3 = 0, index4 = 0;

        Random rand = new Random();
        while (index1 == index2 || index1 == index3 || index1 == index4 ||
               index2 == index3 || index2 == index4 || index3 == index4) {
            // reroll while indices are equal
            index1 = rand.nextInt(size);
            index2 = Math.max(rand.nextInt(size) - 1, 0);
            index3 = Math.max(rand.nextInt(size) - 2, 0);
            index4 = Math.max(rand.nextInt(size) - 3, 0); //  account for removed
        } // indices are not equal

        int[] items = new int[] {index1, index2, index3, index4};
        for (int randomIndex : items) {
            ColorTrains c = cards.get(randomIndex).getColor();
            result.put(c, result.get(c) + 1);
            this.remainingCards.remove(randomIndex);

        }
        return result;
    }

    /**
     * Initializes a new player game state in this referee's playerGameStates
     * to use the two destinations chosen by the Player. MUTATES
     * this.playerGameStates to add a player Returns the new PlayerGameState
     * (mainly for testing)
     */
    public PlayerHand addPlayer() {
        // initialize with no owned connections
        HashSet<DirectConnection> ownedConnections = new HashSet<>();
        this.allOwnedConnections.addLast(
                new HashSet<>()); // add this player to allOwnedConnections
        // with same Set stored in our managed PlayerGameState

        // initialize with four random colored cards
        HashMap<ColorTrains, Integer> colorCards = drawFourRandomColoredCards();
        // initialize with two random Destinations
        List<Destination> twoDestinations = getTwoRandomDestinationsFromMap();
        if (twoDestinations.size() < 2) {
            throw new IllegalArgumentException(
                    "Trying to add player, must initialize with at " +
                            "least two destinations: " + twoDestinations);
        }

        PlayerHand newPlayerHand =
                new PlayerHand(ownedConnections, colorCards, 45,
                        twoDestinations);

        this.playerHands.addLast(newPlayerHand);
        return newPlayerHand.getCopy(); // return a copy so the
        // PlayerGameState stored
        // internally cannot be modified
    }

    /**
     * Method to create a player with given colorCards and input dests to remove
     * randomness for testing game
     *
     * @return test PLayerGameState
     */
    public PlayerHand addPlayerTest(HashMap<ColorTrains, Integer> colorCards,
                                    List<Destination> inputDests,
                                    HashSet<DirectConnection> ownedConnections,
                                    int rails) {
        if (inputDests.size() < 2) {
            throw new IllegalArgumentException(
                    "Trying to add player, must initialize with at " +
                    "least two destinations: " + inputDests);
        }

        PlayerHand newPlayerHand =
                new PlayerHand(ownedConnections, colorCards, rails, inputDests);

        this.allOwnedConnections.addLast(ownedConnections);
        this.playerHands.addLast(newPlayerHand);

        return newPlayerHand.getCopy(); // return a copy so the
        // PlayerGameState stored
        // internally cannot be modified
    }

    /**
     * Helper method to get two random direct connections from the map Will be
     * replaced by getting 5 random DC from map, sending to player, then setting
     * chosen two throws IAE if there are not at least 2 feasible Destinations
     * MUTATES availableDestinations, removes destination after drawing,
     * ensuring next player does not get the same destination(s)
     * availableDestinations
     */
    public List<Destination> getTwoRandomDestinationsFromMap() {
        Set<Destination> destinationSet =
                new HashSet(this.availableDestinations); // get copy
        int size = destinationSet.size();
        if (size <
            2) { // check size is valid because we are removing on each call
            // to this method
            throw new IllegalArgumentException(
                    "Not enough feasible destinations to draw two " +
                    "distinct random destinations. feasibleDests: " +
                    destinationSet);
        }

        List<Destination> resultTwoDestinations = new ArrayList<>();

        // initialize both random picks to zero
        int item1 = 0, item2 = 0;

        while (item1 == item2) { // reroll while items are equal
            item1 = new Random().nextInt(size);
            item2 = new Random().nextInt(size);
        } // items are not equal

        int i = 0; // counter
        for (Destination d : destinationSet) {
            if (i == item1) {
                resultTwoDestinations.add(d);
                this.availableDestinations.remove(d);
            } else if (i == item2) {
                resultTwoDestinations.add(d);
                this.availableDestinations.remove(d);
            }
            i++;
        }
        return resultTwoDestinations;
    }

    /**
     * Method to return the number of current players in this game state. Used
     * for testing eliminated cheaters.
     */
    public int getNumPlayers() {
        return this.playerHands.size();
    }
}
