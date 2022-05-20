package agent;

import map.TrainsMap;
import state.ColorCard;
import strategy.IAdminStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The purpose of the manager is to take players as input and run a tournament
 * until a set of winners is determined.
 */
public class Manager {
    static final int SINGLE_GAME_MIN_PLAYERS = 2;
    static final int SINGLE_GAME_MAX_PLAYERS = 8;
    private final IAdminStrategy strategy;
    private final List<ColorCard> colorCardList;
    private final Set<IPlayer> allInitialPlayers;
    private Set<IPlayer> standingPlayers;
    private final Set<IPlayer> allMisbehavingPlayers;
    private TrainsMap tournamentMap;
    private boolean chosenMap;

    /**
     * Main constructor for a Manager which creates a Manager given a list of
     * players sorted in descending order by age. Initializes a set of
     * badPlayers to empty.
     */
    public Manager(List<IPlayer> allInitialPlayers, List<ColorCard> cards,
                   IAdminStrategy adminStrategy) {
        if (allInitialPlayers.size() < SINGLE_GAME_MIN_PLAYERS) {
            // if we tell players the tournament isn't starting then
            // informPlayersOfStart(false);
            throw new IllegalArgumentException(
                    "Manager cannot run a tournament with less than " +
                    SINGLE_GAME_MIN_PLAYERS + " players");
        }
        this.allInitialPlayers = new HashSet<>(allInitialPlayers); // players sorted by ref on construction
        this.standingPlayers = new HashSet<>(allInitialPlayers); // initially all players are standing
        this.allMisbehavingPlayers = new HashSet<>();
        this.colorCardList = cards;
        this.strategy = adminStrategy;
        this.chosenMap = false;
    }

    /**
     * Secondary constructor that uses the given map rather than choosing
     * a player-provided map.
     */
    public Manager(List<IPlayer> allInitialPlayers, List<ColorCard> cards,
                   IAdminStrategy adminStrategy, TrainsMap map) {
        this(allInitialPlayers, cards, adminStrategy);
        this.tournamentMap = map;
        this.chosenMap = true;
    }

    /**
     * Primary public method to play a tournament after constructing a manager
     *
     * @return Map of "winners"=[IPlayer] and "misbehavers"=[IPlayer]
     */
    public Map<String, Set<IPlayer>> playTournament() {
        List<TrainsMap> suggestedMaps = informPlayersOfStart();
        if (!this.chosenMap) {
            this.chooseTrainsMap(suggestedMaps);
        }
        return runGames();
    }

    /**
     * Method called by playTournament to run Games until we meet an end
     * tournament condition.
     */
    private Map<String, Set<IPlayer>> runGames() {
        Map<String, Set<IPlayer>> winnersAndMisbehavers = new HashMap<>();

        boolean finalRound = false;
        boolean tournamentOver = false;

        while (this.standingPlayers.size() >= SINGLE_GAME_MIN_PLAYERS &&
               !tournamentOver) {
            Set<IPlayer> winnersThisRound = new HashSet<>();
            List<IPlayer> playersToAllocate =
                    new ArrayList<>(this.standingPlayers);
            Collections.sort(
                    playersToAllocate); // initially already sorted, not
            // sorted after a round
            List<LinkedList<IPlayer>> allocatedGameGroups =
                    allocateGameGroups(playersToAllocate);

            if (allocatedGameGroups.size() == 1) {
                finalRound = true;
            }

            for (LinkedList<IPlayer> gameGroup : allocatedGameGroups) { //
                // run each game in a round
                runGame(winnersThisRound, gameGroup);
            }
            // check if the previous standingPlayers is the same as the
            // winners this round
            if (winnersThisRound.equals(this.standingPlayers) ||
                winnersThisRound.size() < SINGLE_GAME_MIN_PLAYERS ||
                finalRound) {
                this.standingPlayers = winnersThisRound;
                // update standingPlayers as winners for notification

                notifyAllPlayersTournamentEnd();
                winnersAndMisbehavers.put("winners", winnersThisRound);
                winnersAndMisbehavers.put("misbehavers", this.allMisbehavingPlayers);
                tournamentOver = true;
            }
            this.standingPlayers = winnersThisRound;
        }

        return winnersAndMisbehavers;
    }

    /**
     * Helper method to run a single game. Takes a list of winnersThisRound and
     * a LinkedList of players to run the game with. It runs the game and
     * mutates winnersThisRound
     */
    private void runGame(Set<IPlayer> winnersThisRound, LinkedList<IPlayer> gameGroup) {
        // create the referee and play this game
        RefereeAgent referee = new RefereeAgent(this.tournamentMap, gameGroup,
                this.colorCardList, this.strategy);
        Map<Integer, Set<IPlayer>> finalScoresWithMisbehavers =
                referee.playGame();

        // get the winners and misbehavers from this game
        Set<IPlayer> misbehavers = finalScoresWithMisbehavers.remove(null);

        if (!finalScoresWithMisbehavers.isEmpty()) {
            Integer highestScore = Collections.max(
                    finalScoresWithMisbehavers.keySet()); // max score
            Set<IPlayer> winners = finalScoresWithMisbehavers.get(highestScore);

            winnersThisRound.addAll(winners);
        }

        this.allMisbehavingPlayers.addAll(misbehavers);
    }

    /**
     * Method called at the beginning of playTournament to inform all initial
     * players that the tournament is starting and get a suggested map to choose
     * from. The manager currently randomly chooses a map.
     *
     * @return list of maps suggested by players
     */
    private List<TrainsMap> informPlayersOfStart() {
        List<TrainsMap> suggestedMaps = new ArrayList<>();

        for (IPlayer player : this.allInitialPlayers) {
            try {
                TrainsMap suggestedMap = player.start();
                suggestedMaps.add(suggestedMap);
            } catch (Exception e) {
                this.allMisbehavingPlayers.add(player);
                this.standingPlayers.remove(player);
            }
        }

        return suggestedMaps;
    }

    /**
     * Method to call handleTournamentEnd on all players that did not misbehave
     * that this manager was constructed with to indicate whether they won or
     * lost this tournament. Handles failure during notification by moving the
     * player to the set of allBadPlayers
     */
    private void notifyAllPlayersTournamentEnd() {
        for (IPlayer player : this.allInitialPlayers) {
            if (this.standingPlayers.contains(player)) {
                try {
                    player.end(true);
                } catch (Exception e) {
                    this.allMisbehavingPlayers.add(player);
                }
            } else if (!this.allMisbehavingPlayers.contains(player)) {
                try {
                    player.end(false);
                } catch (Exception e) {
                    this.allMisbehavingPlayers.add(player);
                }
            }
            // don't notify misbehavers
        }
    }

    /**
     * The allocation of players to games works as follows. The manager starts
     * by assigning them to games with the maximal number of participants
     * permitted in descending order of age. Once the number of remaining
     * players drops below the maximal number and can’t run a game with the
     * remainder, the manager backtracks by one game and tries games of size one
     * less than the maximal number and so on until all players are assigned.
     */
    List<LinkedList<IPlayer>> allocateGameGroups(List<IPlayer> players) {
        List<LinkedList<IPlayer>> resultGroups = new ArrayList<>();
        List<IPlayer> playersToAllocate = new ArrayList<>(players);

        if (players.size() % SINGLE_GAME_MAX_PLAYERS !=
            1) { // we know we can allocate into groups of 8 and a group of 2-7
            while (playersToAllocate.size() >= SINGLE_GAME_MAX_PLAYERS) {
                LinkedList<IPlayer> group =
                        allocateGameMaxPlayersGroup(playersToAllocate);
                resultGroups.add(group);
            }
            if (playersToAllocate.size() != 0) {
                // we have between 2 and 7 players remaining, so we create a
                // single game group
                LinkedList<IPlayer> finalGroup =
                        allocateUntilNPlayers(playersToAllocate, 0);
                resultGroups.add(finalGroup);
            }
        } else { // our last group would contain 1 player with above strategy
            while (playersToAllocate.size() > SINGLE_GAME_MAX_PLAYERS + 1) {
                LinkedList<IPlayer> group =
                        allocateGameMaxPlayersGroup(playersToAllocate);
                resultGroups.add(group);
            }
            // we have 9 remaining players to allocate, so we make two groups
            LinkedList<IPlayer> groupOfSeven =
                    allocateUntilNPlayers(playersToAllocate, 2);
            resultGroups.add(groupOfSeven);
            LinkedList<IPlayer> groupOfTwo =
                    allocateUntilNPlayers(playersToAllocate, 0);
            resultGroups.add(groupOfTwo);
        }
        return resultGroups;
    }

    /**
     * Helper to allocate into a group until N players left.
     */
    private LinkedList<IPlayer> allocateUntilNPlayers(
            List<IPlayer> playersToAllocate, int n) {
        LinkedList<IPlayer> group = new LinkedList<>();
        while (playersToAllocate.size() > n) {
            IPlayer p = playersToAllocate.get(0);
            playersToAllocate.remove(0);
            group.add(p);
        }
        return group;
    }

    /**
     * Helper to allocate SINGLE_GAME_MAX_PLAYERS into a group
     *
     * @return a group of SINGLE_GAME_MAX_PLAYERS players
     */
    private LinkedList<IPlayer> allocateGameMaxPlayersGroup(
            List<IPlayer> playersToAllocate) {
        LinkedList<IPlayer> group = new LinkedList<>();
        for (int i = 0; i < SINGLE_GAME_MAX_PLAYERS; i++) {
            IPlayer p = playersToAllocate.get(0);
            playersToAllocate.remove(0);
            group.add(p);
        }
        return group;
    }

    /**
     * Choose a TrainsMap given choices. Chooses the map with the most
     * Direct Connections. If all maps have no connections we will choose the
     * first map.
     *
     * @throws IllegalArgumentException if the list of choices is empty (all
     *                                  players failed to suggest)
     */
    private void chooseTrainsMap(List<TrainsMap> choices) {
        if (choices.isEmpty()) {
            throw new IllegalArgumentException(
                    "Cannot choose map from empty list");
        }

        this.tournamentMap = choices.get(0); // initially choose first option

        // choose the map with the most direct connections
        int mostFeas = 0;
        for (TrainsMap map : choices) {
            if (map != null) { // avoid choosing a null map
                int numFeas = map
                        .getAllFeasibleDestinations()
                        .size(); // cached feasible
                if (numFeas > mostFeas) {
                    mostFeas = numFeas;
                    this.tournamentMap = map;
                }
            }
        }
        this.chosenMap = true;

        // if only null maps were suggested, we were unable to choose a map for tournament, so we throw
        if (this.tournamentMap == null) {
            throw new IllegalArgumentException("Unable to choose a map.");
        }

        // check if map has enough destinations to accommodate all games in this tournament
        int numDests = this.tournamentMap.getAllFeasibleDestinations().size();
        int playersToAccommodate = Math.min(this.allInitialPlayers.size(), 8);
        int minDestsRequired = playersToAccommodate * 2 + 3;

        if (numDests < minDestsRequired) {
            throw new IllegalArgumentException(
                    "Chosen map has " + numDests + " destinations, need " +
                    minDestsRequired + " to run tournament");
        }
    }
}
