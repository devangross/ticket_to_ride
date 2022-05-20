package state;

import map.City;
import map.Destination;
import map.DirectConnection;
import map.Kruskal;
import map.WeightedGraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Util class used to calculate the game score of a player.
 */
public class Scoring {
    private static final int LONGEST_PATH_POINTS = 20;

    /**
     * Determines the index of the winning Player given a PlayerGameState. Index
     * corresponds to ending linkedlist of Player.
     *
     * @return list of indices representing all winning players
     */
    public static Set<PlayerHand> getWinner(List<PlayerHand> gameStates) {
        Map<Integer, Set<PlayerHand>> rankings = getRanking(gameStates);
        if (rankings.keySet().isEmpty()) {
            return new HashSet<>();
        }
        int maxScore = Collections.max(rankings.keySet());
        return rankings.get(maxScore);
    }

    /**
     * Given a list of the Player's Hand's it determines their score. Initially
     * sums the number of segments they have acquired. Then determines
     * destination points by checking whether connected components contain none,
     * either, or both destinations. Then determines the longest path that each
     * player acquired and adds points to the player's score who built the
     * longest path.
     */
    public static Map<Integer, Set<PlayerHand>> getRanking(
            List<PlayerHand> finalPlayerHands) {
        Map<Integer, Set<PlayerHand>> longestPathToPlayers = new HashMap<>();
        Map<Integer, Set<PlayerHand>> ranking = new HashMap<>();
        // currentScores.get(0) is the calculated score so far for player 0,
        // in our Ref's LinkedList

        for (PlayerHand currPlayer : finalPlayerHands) {
            List<DirectConnection> ownedConnections =
                    new ArrayList<>(currPlayer.getOwnedConnections());

            int numSegments =
                    getSumSegments(ownedConnections); // count segments

            List<List<City>> connectedCities =
                    getConnectedCities(ownedConnections); // run kruskal

            int destPoints = getDestinationPoints(connectedCities,
                    currPlayer.getDestinations());

            int longestPath =
                    getLengthLongestPath(ownedConnections, connectedCities);

            Set<PlayerHand> playersSoFarPath =
                    longestPathToPlayers.getOrDefault(longestPath,
                            new HashSet<>());
            playersSoFarPath.add(
                    currPlayer); // add this player to the set of players
            // with the same longest path length
            longestPathToPlayers.put(longestPath, playersSoFarPath);

            int scoreSoFar = destPoints + numSegments;
            Set<PlayerHand> playersSoFarScore =
                    ranking.getOrDefault(scoreSoFar, new HashSet<>());
            playersSoFarScore.add(
                    currPlayer); // add this player to the set of players
            // with the same score
            ranking.put(scoreSoFar, playersSoFarScore);
        }
        int gameMaxPath = 0;
        // handles longest path for multiple players being the same
        if (!longestPathToPlayers.keySet().isEmpty()) {
            gameMaxPath = Collections.max(
                    longestPathToPlayers.keySet()); // gets the length of
            // longest path
        } else {
            // if there is no longest path (everyone cheated)
            return ranking;
        }

        Set<PlayerHand> playersWithLongestPath =
                longestPathToPlayers.get(gameMaxPath);

        handleMultipleLongestPath(ranking, playersWithLongestPath);

        return ranking;
    }

    /**
     * Method to handle adding scores to multiple players with the longest path.
     * Given a ranking and Set of players with the longest path, for each player
     * with a longest path, it finds the player in the ranking that has a
     * longest path and adds to its score.
     */
    private static void handleMultipleLongestPath(
            Map<Integer, Set<PlayerHand>> ranking,
            Set<PlayerHand> playersWithLongestPath) {
        for (PlayerHand player : playersWithLongestPath) { // for each player
            // with the longest path
            Map<Integer, Set<PlayerHand>> ranking2 = new HashMap<>(ranking);
            Iterator<Map.Entry<Integer, Set<PlayerHand>>> iter =
                    ranking2.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<Integer, Set<PlayerHand>> entry = iter.next();
                int score = entry.getKey();
                Set<PlayerHand> playersAtScore = entry.getValue();
                if (playersAtScore.contains(player)) {
                    playersAtScore.remove(
                            player); // remove player from their old place in
                    // the ranking
                    if (!playersAtScore.isEmpty()) {
                        ranking.put(score, playersAtScore);
                    } else {
                        ranking.remove(score);
                    }
                    int newScore = score + LONGEST_PATH_POINTS;
                    Set<PlayerHand> playersSoFarNewScore =
                            ranking.getOrDefault(newScore, new HashSet<>());
                    playersSoFarNewScore.add(
                            player); // add player to their new place in the
                    // ranking
                    ranking.put(newScore, playersSoFarNewScore);
                }
            }
        }
    }

    /**
     * Method to determine the length of the longest path given a list of owned
     * connections and a list of groups of connected components
     */
    public static int getLengthLongestPath(
            List<DirectConnection> ownedConnections,
            List<List<City>> connectedCities) {
        List<List<DirectConnection>> connectedDCs =
                deriveConnectedDCsFromConnectedCities(ownedConnections,
                        connectedCities);
        return getMaxPathFromConnectedComponents(connectedCities, connectedDCs);
    }

    /**
     * Given a List of List of connectedCities and a List of List of
     * connectedDCs it finds the MAXimum spanning tree in each connected
     * component and determines the longest path in that spanning tree. Then
     * returns the longest path found.
     */
    private static int getMaxPathFromConnectedComponents(
            List<List<City>> connectedCities,
            List<List<DirectConnection>> connectedDCs) {
        // Determine maximum spanning tree and longest path on those trees
        List<Integer> longestPaths = new ArrayList<>();
        for (int i = 0; i < connectedCities.size(); i++) {
            Kruskal k = new Kruskal(connectedDCs.get(i));
            List<DirectConnection> sTree = k.run(); // maximum spanning tree
            WeightedGraph wg = WeightedGraph.makeGraphFromVerticesEdges(
                    connectedCities.get(i), sTree);

            int longestPath = wg.findAbsoluteLongestPath();
            longestPaths.add(longestPath);
        }
        int maxPath = 0;
        if (longestPaths.size() > 0) {
            maxPath = Collections.max(longestPaths);
        }
        return maxPath;
    }

    /**
     * Given a List of ownedConnections and List of List of connectedCities this
     * derives the List of List of DirectConnections.
     * example: input ownedConnections = [(LA, SLO), (SLO, vegas), (SF, SAC)]
     * input connectedCities = [(LA, SLO, vegas), (SF, SAC)] output = [[(LA,
     * SLO), (SLO, vegas)], [(SF, SAC)]]
     */
    public static List<List<DirectConnection>> deriveConnectedDCsFromConnectedCities(
            List<DirectConnection> ownedConnections,
            List<List<City>> connectedCities) {
        List<List<DirectConnection>> directConnInComponents = new ArrayList<>();
        // initialize empty list of connected DC's for each list of connected
        // cities
        for (int x = 0; x < connectedCities.size(); x++) {
            directConnInComponents.add(new ArrayList<>());
        }

        for (DirectConnection currConn : ownedConnections) { // for each of
            // this p's ownedConns
            for (int i = 0;
                 i < connectedCities.size();
                 i++) { // for each connected component
                if (connectedCities.get(i).contains(currConn.getCity0())) {
                    if (!directConnInComponents
                            .get(i)
                            .contains(currConn)) { // dup check
                        directConnInComponents.get(i).add(currConn);
                        // add connection at corresponding index in list of
                        // list of
                        // directConnection
                    }
                }
            }
        }
        return directConnInComponents;
    }

    /**
     * Uses Kruskal to derive a 2d list of connected cities given owned
     * connections. If this returns a List of two lists, those lists represent
     * components that are not connected by the input ownedConnections.
     */
    private static List<List<City>> getConnectedCities(
            List<DirectConnection> ownedConnections) {
        Kruskal kc = new Kruskal(ownedConnections);
        kc.run(); // run kruskals
        return kc.deriveConnectedComponents();
    }

    /**
     * Given a list of DirectConnections, this sums the number of segments
     * (lengths of each DC). example: input: [(la, slo, 4, RED), (slo, vegas, 5,
     * BLUE), (slo, vegas, 3, WHITE)] result: 12
     */
    public static int getSumSegments(List<DirectConnection> ownedConnections) {
        return ownedConnections
                .stream()
                .mapToInt(DirectConnection::getLength)
                .sum();
    }

    /**
     * Helper method to determine the number of points to give a player for
     * connecting or not connecting their destinations. A player receives +10
     * points for every pair of cities in a destination that have been connected
     * and -10 for every unconnected destination.
     * Receives a 2d Array of connected components. ex: [[a, b], [c, d, e]]
     * where cities a and b are the same connected component and c, d, and e are
     * the same connected component. Receives a list of the players chosen
     * destinations. Then checks whether those destination cities exist in any
     * connected components.
     * ex input: connComponents = [[a, b], [c, d, e]], playerDestinations = (a,
     * b), (c, b) result: totalPoints = 10 bc one connected component contains
     * all cities in a dest
     *
     * @param connComponents      list of cities in connected components of
     *                            owned connections
     * @param playersDestinations destinations a player picked
     *
     * @return int corresponding to points derived from destinations
     */
    // TODO test this with all 3 cases none, either both
    public static int getDestinationPoints(List<List<City>> connComponents,
                                           List<Destination> playersDestinations) {
        int totalPoints = 0;

        for (Destination destination : playersDestinations) {
            boolean connected = false;
            City city1 = destination.getVertices().getFirst();
            City city2 = destination.getVertices().getSecond();

            for (List<City> component : connComponents) {
                if (component.containsAll(
                        new ArrayList<>(Arrays.asList(city1, city2)))) {
                    connected = true;
                }
            }

            if (connected) {
                totalPoints += 10;
            } else {
                totalPoints -= 10;
            }
        }

        return totalPoints;
    }
}
