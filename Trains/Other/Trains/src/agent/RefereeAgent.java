package agent;

import map.Destination;
import map.DirectConnection;
import map.TrainsMap;
import state.ColorCard;
import state.PlayerGameState;
import state.PlayerHand;
import state.RefereeGameState;
import state.Scoring;
import strategy.IAdminStrategy;
import strategy.Move;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class to represent our RefereeAgent which implements IReferee. IReferee does
 * not currently have any public interface methods as these will be defined by
 * the Manager<->Referee Protocol. Abnormal interaction handling: This
 * RefereeAgent class currently catches and handles all exceptions thrown by the
 * referee game state when handling player destination picks and player
 * connection acquisitions.
 * <p>
 * In the case that there is one remaining player, regardless of if they have
 * made a move/chosen connections, we send this final player the win.
 * <p>
 * Note: Tournament manager will hand all game pieces - map, color card deck,
 * and a rulebook(how to order the destinations, and color cards.
 * <p>
 */
public class RefereeAgent implements IReferee {
    private final TrainsMap map;
    private final RefereeGameState gameState;
    private final LinkedList<IPlayer> players;
    private final Set<IPlayer> badPlayers;

    private final Map<IPlayer, PlayerHand> iPlayerToPlayerHand;
    private final Map<PlayerHand, IPlayer> playerHandToIPlayer;

    private int initialRails = 45;
    private int numPlayersPlayedWithNoChange = 0;

    /**
     * Main constructor for a Referee Agent.
     *
     * @param map      TrainsMap to play this game on
     * @param players  given by the TournamentManager
     * @param cards    to be sorted using some strategy
     * @param rulebook to determine how cards and connections are ordered for
     *                 distribution to players
     */
    public RefereeAgent(TrainsMap map, LinkedList<IPlayer> players,
                        List<ColorCard> cards, IAdminStrategy rulebook) {
        if (map == null || players == null || cards == null ||
            rulebook == null) {
            throw new IllegalArgumentException(
                    "Arguments to RefereeAgent cannot be null.");
        }
        if (players.size() < Manager.SINGLE_GAME_MIN_PLAYERS ||
            players.size() > Manager.SINGLE_GAME_MAX_PLAYERS) {
            throw new IllegalArgumentException(
                    "Invalid players: " + Manager.SINGLE_GAME_MIN_PLAYERS +
                    " <= players.size() " + "<= " +
                    Manager.SINGLE_GAME_MAX_PLAYERS);
        }
        if (map.getAllFeasibleDestinations().size() <
            (players.size() * 2) + 3) {
            throw new IllegalArgumentException(
                    "Not enough destinations on given map for given number " +
                    "of players, given " + players.size() + " and map has " +
                    map.getAllFeasibleDestinations().size() + " dests avail");
        }
        LinkedList<IPlayer> sortedPlayers = new LinkedList<>(players);
        Collections.sort(sortedPlayers); // sorted by birthday
        this.map = map;
        this.gameState = new RefereeGameState(map, cards, rulebook);
        this.players = sortedPlayers;
        this.iPlayerToPlayerHand = new HashMap<>();
        this.playerHandToIPlayer = new HashMap<>();
        this.badPlayers = new HashSet<>();
    }

    /**
     * Secondary constructor for a Referee Agent with a specified number of rails for
     * testing a shorter length game.
     */
    public RefereeAgent(TrainsMap map, LinkedList<IPlayer> players,
                        List<ColorCard> cards, IAdminStrategy rulebook,
                        int rails) {
        this(map, players, cards, rulebook);
        this.initialRails = rails;
    }

    /**
     * Method to orchestrate playing an entire game after constructing this
     * RefereeAgent.
     */
    public Map<Integer, Set<IPlayer>> playGame() {
        this.setupPlayers();
        this.handleDestinationChoices();
        this.runTurns();
        this.sendGameResult();
        return this.getFinalScoresAndMisbehavers();
    }

    /**
     * Sends the map, number of rails, and initial cards to the current player.
     */
    void setupPlayers() {
        for (IPlayer p : this.players) {
            List<ColorCard> cards =
                    this.gameState.initializePlayerWithRailsAndDraw(
                            this.initialRails);
            try {
                p.setup(this.map, this.initialRails, cards);
            } catch (Exception e) {
                // TODO this.players.remove(p); ??
            }
        }
    }

    /**
     * Selects a set number of destinations to send to current player.
     * Destinations are ordered according to the Referee's IRefereeRule
     * (selectDestinations) implementation.
     */
    void handleDestinationChoices() {
        // get a copy of this.players to avoid concurrent modification
        LinkedList<IPlayer> playersCopy = new LinkedList<>(this.players);
        for (IPlayer p : playersCopy) {

            List<Destination> choices =
                    this.gameState.getFirstFiveDestinations();

            try {
                List<Destination> rejected = p.pick(choices);
                this.gameState.handleDestinationSelection(choices, rejected);
            } catch (Exception e) {
                this.eliminatePlayer(p);
                if (this.players.size() == 1) {
                    this.sendLastRemainingPlayerWin();
                    break;
                }
            }
        }
    }

    /**
     * Method to handle the sending of turns and response move until the game is
     * over. Must have more than one player to run. MUTATES: this.gamestate,
     * this.players with turn progression and moves
     */
    void runTurns() {
        while (this.players.size() > 0 && !this.gameState.isNextRoundFinal() &&
               this.numPlayersPlayedWithNoChange != this.players.size()) {
            playTurn();
        }
        // if we kick a player during the final round, this.players.size()
        // will decrease,
        // ensuring we do not let the player who dropped below the threshold
        // play again
        if (this.gameState.getNumRemainingAvailConnections() != 0 &&
            this.gameState.getNumRemainingCards() > 0) {
            // no need to run final round if there are no remaining available
            // direct connections
            for (int i = 0; i < this.players.size() - 1; i++) {
                playTurn();
                if (this.players.size() == 1) {
                    this.sendLastRemainingPlayerWin();
                    break;
                }
            }
        }
    }

    /**
     * Helper method to extract the logic for playing a single turn. Allows
     * reuse on final round. Sends the current PlayerGameState to the first
     * player in the linked list. Gets the response move, check validity, makes
     * internal gamestate changes, and adds the player to the end of the linked
     * list. If any part of the turn broke game rules, player is removed from
     * the game.
     */
    void playTurn() {
        PlayerGameState PGS = this.gameState.getCurrentPlayerGameState();
        IPlayer currPlayer = this.players.remove();
        try {
            Move moveResponse = currPlayer.play(PGS);

            if (moveResponse.getMove() instanceof Boolean) {

                handleAdditionalCards(currPlayer);
            } else if (moveResponse.getMove() instanceof DirectConnection) {
                // Move must be DirectConnection
                handleConnectionMove(currPlayer, moveResponse);
            } else {
                this.eliminatePlayer(currPlayer);
            }
        } catch (Exception e) {
            this.eliminatePlayer(currPlayer);
        }
    }

    /**
     * Method to encapsulate the two steps of eliminating a player from our
     * game. First eliminate them from the game state, then remove them from
     * this Referee Agent's list of players
     */
    void eliminatePlayer(IPlayer player) {
        this.gameState.removePlayer(); //Move is invalid
        this.players.remove(player);
        // after removing the player we add them to this ref's list of
        // badPlayers
        this.badPlayers.add(player);
    }

    /**
     * Helper method to handle mutation of players and gamestate to reflect the
     * connection move of the player. If the move is invalid, the player is not
     * added back to the linked list and the gamestate reflects this elimination
     * as well.
     *
     * @param currPlayer   the player that made the move
     * @param moveResponse the DirectConnection the player is trying to acquire
     */
    private void handleConnectionMove(IPlayer currPlayer, Move moveResponse) {
        DirectConnection dc = (DirectConnection) moveResponse.getMove();
        try {
            this.gameState.addAcquiredConnectionMove(
                    dc);   //throws if the connection is invalid
            this.players.addLast(currPlayer);   //continue ture
        } catch (IllegalArgumentException e) {
            this.eliminatePlayer(currPlayer);
            if (this.players.size() == 1) {
                this.sendLastRemainingPlayerWin();
            }
        }
    }

    /**
     * Helper method to handle mutation of players and gamestate to reflect the
     * more_cards move of the player. If there are no more cards to give, the
     * move is legal, but the player receives an empty list of cards.
     *
     * @param currPlayer that made the move
     */
    private void handleAdditionalCards(IPlayer currPlayer) {
        List<ColorCard> giveList = this.gameState.drawTwoColoredCardsMove();
        if (giveList.isEmpty()) {
            this.numPlayersPlayedWithNoChange++;
        }

        currPlayer.more(giveList);
        this.players.addLast(currPlayer);
    }

    /**
     * Sends boolean true if the current player won the game, otherwise false.
     */
    void sendGameResult() {
        this.buildBiDirectionalMaps();
        List<PlayerHand> allPH = this.gameState.getAllPlayerHands();
        Set<PlayerHand> winners = Scoring.getWinner(allPH);
        for (IPlayer p : this.players) {
            PlayerHand player = this.iPlayerToPlayerHand.get(p);
            boolean won = winners.contains(player);
            p.win(won);
        }
    }

    /**
     * Build bi-map (two maps IPlayer -> PlayerHand, PlayerHand -> IPlayer)
     */
    void buildBiDirectionalMaps() {
        List<PlayerHand> playerHands = this.gameState.getAllPlayerHands();
        for (int i = 0; i < this.players.size(); i++) {
            this.iPlayerToPlayerHand.put(this.players.get(i),
                    playerHands.get(i));
            this.playerHandToIPlayer.put(playerHands.get(i),
                    this.players.get(i));
        }
    }

    /**
     * Method to get a Map of Rank to set of players example: {20=Player1,
     * 30=(Player2, 3)} - player 2 and 3 won with scores of 30, player1 lost
     */
    Map<Integer, Set<PlayerHand>> getFinalScoresToPlayers() {
        List<PlayerHand> allPH = this.gameState.getAllPlayerHands();
        return Scoring.getRanking(allPH);
    }

    /**
     * Method to be called at the end of playGame to yield a map of scores to
     * players with a null entry for misbehavers as the result of the game to be
     * passed to the manager.
     * NOTE: this method returns a map entry with key NULL representing
     * eliminated players
     * example: {30=[player0, player3], 10=[player1, player4] null=[player5,
     * player2]} interpretation: player0 and player3 won with a score of 30,
     * player1 and player4 got second place with a score of 10, player5 and
     * player2 misbehaved and were eliminated
     *
     * @return map of scores to players with a null entry for misbehavers
     */
    Map<Integer, Set<IPlayer>> getFinalScoresAndMisbehavers() {
        Map<Integer, Set<IPlayer>> finalScoresWithNullMisbehavers =
                new HashMap<>();

        Map<Integer, Set<PlayerHand>> allPlayersRankings =
                this.getFinalScoresToPlayers();
        for (Integer score : allPlayersRankings.keySet()) {
            Set<PlayerHand> playerHandsWithThisScore =
                    allPlayersRankings.get(score);
            Set<IPlayer> iPlayersWithThisScore = new HashSet<>();
            for (PlayerHand hand : playerHandsWithThisScore) {
                iPlayersWithThisScore.add(playerHandToIPlayer.get(hand));
            }
            finalScoresWithNullMisbehavers.put(score, iPlayersWithThisScore);
        }

        finalScoresWithNullMisbehavers.put(null, this.badPlayers);

        return finalScoresWithNullMisbehavers;
    }

    /**
     * Method to retrieve number of players in gameState currently
     */
    int getNumPlayers() {
        return this.gameState.getNumPlayers();
    }

    /**
     * Method to retrieve the linked list of all owned connections for testing
     * purposes. This does not leak a mutable copy of the game state because
     * DirectConnections are final.
     */
    LinkedList<HashSet<DirectConnection>> getAllOwnedConnections() {
        return new LinkedList<>(this.gameState.getAllOwnedConnections());
        // shallow copy, getAllOwnedConns returns another copy
    }

    /**
     * Method to send win to the last remaining player. For example, everyone
     * cheats in destinations choices, last player should win before selecting
     * TODO: fix if rules change
     */
    private void sendLastRemainingPlayerWin() {
        this.players.get(0).win(true);
    }
}
