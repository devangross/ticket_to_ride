package agent;

import map.City;
import map.ColorTrains;
import map.Coord;
import map.Destination;
import map.DirectConnection;
import map.ExampleMap;
import map.TrainsMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import state.ColorCard;
import state.RefereeGameState;
import strategy.BuyNowStrategy;
import strategy.CheaterStrategy;
import strategy.HoldTenStrategy;
import strategy.IAdminStrategy;
import strategy.NonSequiturStrategy;
import strategy.OrderedDestSameCards;
import strategy.RandomAdminStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RefereeAgentTest {

    public static List<ColorCard> getConstantCardListLengthN(int length) {
        List<ColorCard> gameCards = new ArrayList<>();
        for (int i = 0; i < length; i++) {
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
        return gameCards;
    }

    @Test
    public void testShouldConstructValidRefereeAgent() {
        TrainsMap map = ExampleMap.createBostonMap();

        String holdTen =
                "Trains/out/production/Trains/strategy.HoldTenStrategy";
        String buyNow = "Trains/out/production/Trains/strategy.BuyNowStrategy";

        PlayerAgent holdTenPlayer = new PlayerAgent("holdTen", holdTen);
        PlayerAgent buyNowPlayer = new PlayerAgent("buyNow", buyNow);
        LinkedList<IPlayer> players = new LinkedList<>();
        players.addLast(holdTenPlayer);
        players.addLast(buyNowPlayer);

        IAdminStrategy rulebook = new RandomAdminStrategy();

        RefereeAgent referee = new RefereeAgent(map, players,
                RefereeGameState.initializeShuffledColoredCards(), rulebook);
        assertNotNull(referee);
    }

    @Test
    public void testShouldSetupPlayersOnReferee() {
        TrainsMap map = ExampleMap.createBostonMap();

        PlayerAgent holdTenPlayer =
                new PlayerAgent("holdTenPlayer", new HoldTenStrategy());
        PlayerAgent buyNowPlayer =
                new PlayerAgent("buyNowPlayer", new BuyNowStrategy());
        LinkedList<IPlayer> players = new LinkedList<>();
        players.addLast(holdTenPlayer);
        players.addLast(buyNowPlayer);

        IAdminStrategy rulebook = new RandomAdminStrategy();
        RefereeAgent referee = new RefereeAgent(map, players,
                RefereeGameState.initializeShuffledColoredCards(), rulebook);
        referee.setupPlayers();

        assertEquals(45, holdTenPlayer.getGameState().getRails());
        assertEquals(45, buyNowPlayer.getGameState().getRails());
        assertEquals(4, holdTenPlayer
                .getGameState()
                .getCardsMap()
                .values()
                .stream()
                .reduce(0, Integer::sum));
        assertEquals(4, buyNowPlayer
                .getGameState()
                .getCardsMap()
                .values()
                .stream()
                .reduce(0, Integer::sum));
        assertEquals(map, buyNowPlayer.getGameState().getTrainsMap());
    }

    @Test
    public void testShouldHandleValidDestinationChoices() {
        TrainsMap map = ExampleMap.createBostonMap();
        City brookline = new City("Brookline", new Coord(.1f, .5f));
        City cambridge = new City("Cambridge", new Coord(.11f, .101f));
        City financial_district =
                new City("Financial District", new Coord(.80f, .3f));
        City chinatown = new City("Chinatown", new Coord(.68f, .42f));
        City seaport = new City("Seaport", new Coord(.90f, .6f));
        City common = new City("Common", new Coord(.5f, .4f));

        PlayerAgent holdTenPlayer =
                new PlayerAgent("holdTenPlayer", new HoldTenStrategy());
        PlayerAgent buyNowPlayer =
                new PlayerAgent("buyNowPlayer", new BuyNowStrategy());
        LinkedList<IPlayer> players = new LinkedList<>();
        players.addLast(holdTenPlayer);
        players.addLast(buyNowPlayer);

        IAdminStrategy rulebook = new OrderedDestSameCards();
        RefereeAgent referee = new RefereeAgent(map, players,
                RefereeGameState.initializeShuffledColoredCards(), rulebook);
        referee.setupPlayers();

        referee.handleDestinationChoices();
        assertEquals(new Destination(brookline, cambridge),
                holdTenPlayer.getGameState().getDestinations().get(0));
        assertEquals(new Destination(brookline, chinatown),
                holdTenPlayer.getGameState().getDestinations().get(1));
        assertEquals(new Destination(cambridge, common),
                buyNowPlayer.getGameState().getDestinations().get(0));
        assertEquals(new Destination(cambridge, chinatown),
                buyNowPlayer.getGameState().getDestinations().get(1));
    }

    @Test
    public void testShouldEliminateCheaterDestinationsPick() {
        TrainsMap map = ExampleMap.createBostonMap();

        PlayerAgent cheater =
                new PlayerAgent("cheater", new NonSequiturStrategy());
        PlayerAgent buyNowPlayer =
                new PlayerAgent("buyNowPlayer", new BuyNowStrategy());
        LinkedList<IPlayer> players = new LinkedList<>();
        players.addLast(cheater);
        players.addLast(buyNowPlayer);

        IAdminStrategy rulebook = new OrderedDestSameCards();
        RefereeAgent referee = new RefereeAgent(map, players,
                RefereeGameState.initializeShuffledColoredCards(), rulebook);
        referee.setupPlayers();
        assertEquals(2, referee.getNumPlayers());
        referee.handleDestinationChoices(); // we kick cheater on bad
        // destination pick
        assertEquals(1, referee.getNumPlayers());
        assertEquals(new ArrayList<>(),
                buyNowPlayer.getGameState().getDestinations());
        assertEquals(new ArrayList<>(),
                buyNowPlayer.getGameState().getDestinations());
    }

    @Test
    public void testShouldPlayGameWithTenRailsAndCheckOwned() {
        TrainsMap map = ExampleMap.createBostonMap();
        City brookline = new City("Brookline", new Coord(.1f, .5f));
        City cambridge = new City("Cambridge", new Coord(.11f, .101f));
        City financial_district =
                new City("Financial District", new Coord(.80f, .3f));
        City chinatown = new City("Chinatown", new Coord(.68f, .42f));
        City common = new City("Common", new Coord(.5f, .4f));

        PlayerAgent holdTenPlayer =
                new PlayerAgent("holdTenPlayer", new HoldTenStrategy());
        PlayerAgent buyNowPlayer =
                new PlayerAgent("buyNowPlayer", new BuyNowStrategy());
        LinkedList<IPlayer> players = new LinkedList<>();
        players.addLast(holdTenPlayer);
        players.addLast(buyNowPlayer);

        IAdminStrategy rulebook = new OrderedDestSameCards();
        RefereeAgent referee =
                new RefereeAgent(map, players, getConstantCardListLengthN(200),
                        rulebook, 10);
        referee.setupPlayers();
        referee.handleDestinationChoices();

        referee.runTurns();
        referee.sendGameResult();
        LinkedList<HashSet<DirectConnection>> allOwned =
                referee.getAllOwnedConnections();
        boolean someoneOwnsAConnection = allOwned.peekFirst().size() > 0;
        someoneOwnsAConnection =
                someoneOwnsAConnection || allOwned.peekLast().size() > 0;

        HashSet<DirectConnection> expectedOwnedP0 = new HashSet<>();
        expectedOwnedP0.add(
                new DirectConnection(cambridge, common, 3, ColorTrains.RED));
        expectedOwnedP0.add(
                new DirectConnection(chinatown, common, 3, ColorTrains.WHITE));
        expectedOwnedP0.add(new DirectConnection(common, financial_district, 4,
                ColorTrains.WHITE));

        HashSet<DirectConnection> expectedOwnedP1 = new HashSet<>();
        expectedOwnedP1.add(
                new DirectConnection(brookline, common, 4, ColorTrains.BLUE));
        expectedOwnedP1.add(
                new DirectConnection(brookline, common, 3, ColorTrains.GREEN));

        assertEquals(2, allOwned.size());
        //assertTrue(someoneOwnsAConnection);
        assertEquals(expectedOwnedP0, allOwned.remove());
        assertEquals(expectedOwnedP1, allOwned.remove());
    }

    // Losing player acquires cyclic graph, winning buy tree
    @Test
    public void testShouldPlayGameWithTwentyRailsAnd50Cards() {
        TrainsMap map = ExampleMap.createBostonMap();
        PlayerAgent holdTenPlayer =
                new PlayerAgent("holdTenPlayer", new HoldTenStrategy());
        PlayerAgent buyNowPlayer =
                new PlayerAgent("buyNowPlayer", new BuyNowStrategy());
        LinkedList<IPlayer> players = new LinkedList<>();
        players.addLast(holdTenPlayer);
        players.addLast(buyNowPlayer);

        IAdminStrategy rulebook = new OrderedDestSameCards();
        RefereeAgent referee =
                new RefereeAgent(map, players, getConstantCardListLengthN(100),
                        rulebook, 20);
        referee.setupPlayers();
        referee.handleDestinationChoices();

        referee.runTurns();

        LinkedList<HashSet<DirectConnection>> allOwned =
                referee.getAllOwnedConnections();
        boolean someoneOwnsAConnection = allOwned.peekFirst().size() > 0;
        someoneOwnsAConnection =
                someoneOwnsAConnection || allOwned.peekLast().size() > 0;

        assertEquals(2, allOwned.size());
        assertTrue(someoneOwnsAConnection);
        // both players acquire at least two connections
        assertTrue(allOwned.remove().size() > 2);
        assertTrue(allOwned.remove().size() > 2);
        referee.sendGameResult();
        assertFalse(holdTenPlayer.isGameWinner());
        assertTrue(buyNowPlayer.isGameWinner());
    }

    @Test
    public void testShouldPlayGameWithTwentyRailsAndTwoFiftyCardsBigMap() {
        TrainsMap map = ExampleMap.createBigBostonMap();
        PlayerAgent player1 =
                new PlayerAgent("buyNowPlayer", new BuyNowStrategy());
        PlayerAgent player2 =
                new PlayerAgent("buyNowPlayer", new BuyNowStrategy());
        PlayerAgent player3 =
                new PlayerAgent("buyNowPlayer", new BuyNowStrategy());
        PlayerAgent player4 =
                new PlayerAgent("buyNowPlayer", new BuyNowStrategy());

        LinkedList<IPlayer> players = new LinkedList<>();
        players.addLast(player1);
        players.addLast(player2);
        players.addLast(player3);
        players.addLast(player4);

        IAdminStrategy rulebook = new OrderedDestSameCards();
        RefereeAgent referee =
                new RefereeAgent(map, players, getConstantCardListLengthN(250),
                        rulebook, 45);
        referee.setupPlayers();
        referee.handleDestinationChoices();

        referee.runTurns();
        LinkedList<HashSet<DirectConnection>> allOwned =
                referee.getAllOwnedConnections();
        boolean someoneOwnsAConnection = allOwned.peekFirst().size() > 0;
        someoneOwnsAConnection =
                someoneOwnsAConnection || allOwned.peekLast().size() > 0;

        assertEquals(4, allOwned.size());
        assertTrue(someoneOwnsAConnection);
        // both players acquire at least two connections
        assertTrue(allOwned.remove().size() > 2);
        assertTrue(allOwned.remove().size() > 2);
    }

    @Test
    public void testShouldPlayGameWithThirtyRailsAndTwoFiftyCardsBigMapAndSendWin() {
        TrainsMap map = ExampleMap.createBigBostonMap();
        PlayerAgent player1 =
                new PlayerAgent("buyNowPlayer", new BuyNowStrategy());
        PlayerAgent player2 =
                new PlayerAgent("buyNowPlayer", new BuyNowStrategy());
        PlayerAgent player3 =
                new PlayerAgent("buyNowPlayer", new BuyNowStrategy());
        PlayerAgent player4 =
                new PlayerAgent("buyNowPlayer", new BuyNowStrategy());

        LinkedList<IPlayer> players = new LinkedList<>();
        players.addLast(player1);
        players.addLast(player2);
        players.addLast(player3);
        players.addLast(player4);

        IAdminStrategy rulebook = new OrderedDestSameCards();
        RefereeAgent referee =
                new RefereeAgent(map, players, getConstantCardListLengthN(250),
                        rulebook, 30);
        referee.setupPlayers();
        referee.handleDestinationChoices();

        referee.runTurns();
        LinkedList<HashSet<DirectConnection>> allOwned =
                referee.getAllOwnedConnections();
        boolean someoneOwnsAConnection = allOwned.peekFirst().size() > 0;
        someoneOwnsAConnection =
                someoneOwnsAConnection || allOwned.peekLast().size() > 0;

        Set<Integer> finalScores = referee.getFinalScoresToPlayers().keySet();
        assertTrue(
                finalScores.containsAll(new HashSet<>(List.of(2, 3, 7, 47))));

        assertEquals(4, allOwned.size());
        assertTrue(someoneOwnsAConnection);
        // both players acquire at least two connections
        assertTrue(allOwned.remove().size() > 2);
        assertTrue(allOwned.remove().size() > 2);
    }

    @Test
    public void testShouldPlayGameWithTenRailsAndEliminateCheater() {
        TrainsMap map = ExampleMap.createBigBostonMap();
        PlayerAgent player1 =
                new PlayerAgent("buyNowPlayer", new BuyNowStrategy());
        PlayerAgent player2 =
                new PlayerAgent("buyNowPlayer", new BuyNowStrategy());
        PlayerAgent player3 =
                new PlayerAgent("buyNowPlayer", new BuyNowStrategy());
        PlayerAgent cheater =
                new PlayerAgent("cheater", new NonSequiturStrategy());

        LinkedList<IPlayer> players = new LinkedList<>();
        players.addLast(player1);
        players.addLast(player2);
        players.addLast(player3);
        players.addLast(cheater);

        IAdminStrategy rulebook = new RandomAdminStrategy();
        RefereeAgent referee =
                new RefereeAgent(map, players, getConstantCardListLengthN(250),
                        rulebook, 10);
        referee.setupPlayers();
        referee.handleDestinationChoices();
        assertEquals(3, referee.getNumPlayers());
        referee.runTurns();
        assertEquals(3, referee.getNumPlayers());
    }

    @Test
    public void testShouldPlayGameWithThirtyRailsTwoTieAndEliminateCheater() {
        TrainsMap map = ExampleMap.createBigBostonMap();
        PlayerAgent player1 =
                new PlayerAgent("playerOne", new BuyNowStrategy());
        PlayerAgent player2 =
                new PlayerAgent("playerTwo", new BuyNowStrategy());
        PlayerAgent player3 =
                new PlayerAgent("playerThree", new BuyNowStrategy());
        PlayerAgent cheater =
                new PlayerAgent("cheater", new NonSequiturStrategy());

        LinkedList<IPlayer> players = new LinkedList<>();
        players.addLast(player1);
        players.addLast(player2);
        players.addLast(player3);
        players.addLast(cheater);

        IAdminStrategy rulebook = new OrderedDestSameCards();
        RefereeAgent referee =
                new RefereeAgent(map, players, getConstantCardListLengthN(250),
                        rulebook, 30);
        Map<Integer, Set<IPlayer>> finalScoresWMisbehavers = referee.playGame();

        assertEquals(1, finalScoresWMisbehavers.get(null).size());
        assertTrue(finalScoresWMisbehavers.get(null).contains(cheater));

        assertEquals(1, finalScoresWMisbehavers.get(27).size());
        assertTrue(finalScoresWMisbehavers.get(27).contains(player3));

        assertEquals(2, finalScoresWMisbehavers.get(6).size());
        assertTrue(finalScoresWMisbehavers.get(6).contains(player1));

    }

    // tests for running out of color cards, multiple longest paths, and
    // multiple winners
    @Test
    public void testShouldTerminateAfterRoundWhenNoMoreColorCards() {
        TrainsMap map = ExampleMap.createBigBostonMap();
        PlayerAgent player1 =
                new PlayerAgent("buyNowPlayerA", new BuyNowStrategy());
        PlayerAgent player2 =
                new PlayerAgent("buyNowPlayerB", new BuyNowStrategy());
        PlayerAgent player3 =
                new PlayerAgent("buyNowPlayerC", new BuyNowStrategy());

        LinkedList<IPlayer> players = new LinkedList<>();
        players.addLast(player1);
        players.addLast(player2);
        players.addLast(player3);

        IAdminStrategy rulebook = new OrderedDestSameCards();
        //should run only 1 request turn
        RefereeAgent referee =
                new RefereeAgent(map, players, getConstantCardListLengthN(13),
                        rulebook, 20);
        referee.setupPlayers();
        referee.handleDestinationChoices();
        referee.runTurns();
        referee.sendGameResult();
        assertEquals(new HashSet<>(List.of(0)),
                referee.getFinalScoresToPlayers().keySet());
        assertTrue(player1.isGameWinner());
        assertTrue(player2.isGameWinner());
        assertTrue(player3.isGameWinner());
    }

    @Test
    public void testShouldPlayGameWithAllCheaters() {
        TrainsMap map = ExampleMap.createBigBostonMap();
        PlayerAgent player1 =
                new PlayerAgent("cheater", new NonSequiturStrategy());
        PlayerAgent player2 =
                new PlayerAgent("cheater", new NonSequiturStrategy());
        PlayerAgent player3 =
                new PlayerAgent("cheater", new NonSequiturStrategy());
        PlayerAgent cheater =
                new PlayerAgent("cheater", new NonSequiturStrategy());

        LinkedList<IPlayer> players = new LinkedList<>();
        players.addLast(player1);
        players.addLast(player2);
        players.addLast(player3);
        players.addLast(cheater);

        IAdminStrategy rulebook = new RandomAdminStrategy();
        RefereeAgent referee =
                new RefereeAgent(map, players, getConstantCardListLengthN(250),
                        rulebook, 10);
        referee.setupPlayers();
        referee.handleDestinationChoices();
        assertEquals(1, referee.getNumPlayers());
        assertTrue(cheater.isGameWinner());

    }

    @Test
    public void testShouldThrowWhenOnly1Player() {
        TrainsMap map = ExampleMap.createBigBostonMap();
        PlayerAgent player1 =
                new PlayerAgent("buyNowPlayer", new BuyNowStrategy());

        LinkedList<IPlayer> players = new LinkedList<>();
        players.addLast(player1);

        IAdminStrategy rulebook = new RandomAdminStrategy();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new RefereeAgent(map, players, getConstantCardListLengthN(250),
                    rulebook, 20);
        });
    }

    @Test
    public void testShouldThrowWhenMapTooSmall() {
        TrainsMap map = ExampleMap.createExampleMap();
        PlayerAgent player1 =
                new PlayerAgent("buyNowPlayerA", new BuyNowStrategy());
        PlayerAgent player2 =
                new PlayerAgent("buyNowPlayerB", new BuyNowStrategy());
        PlayerAgent player3 =
                new PlayerAgent("buyNowPlayerC", new BuyNowStrategy());
        PlayerAgent player4 =
                new PlayerAgent("buyNowPlayerD", new BuyNowStrategy());

        LinkedList<IPlayer> players = new LinkedList<>();
        players.addLast(player1);
        players.addLast(player2);
        players.addLast(player3);
        players.addLast(player4);

        IAdminStrategy rulebook = new RandomAdminStrategy();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new RefereeAgent(map, players, getConstantCardListLengthN(250),
                    rulebook, 20);
        });
    }

    @Test
    public void testShouldThrowWhenMapTooSmallTwo() {
        TrainsMap map = ExampleMap.createExampleMap();
        PlayerAgent player1 =
                new PlayerAgent("buyNowPlayerA", new BuyNowStrategy());
        PlayerAgent player2 =
                new PlayerAgent("buyNowPlayerB", new BuyNowStrategy());

        LinkedList<IPlayer> players = new LinkedList<>();
        players.addLast(player1);
        players.addLast(player2);

        IAdminStrategy rulebook = new RandomAdminStrategy();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new RefereeAgent(map, players, getConstantCardListLengthN(250),
                    rulebook, 20);
        });
    }

    @Test
    public void testShouldOutputTwoHundredFiftyCards() {
        List<ColorCard> cards = getConstantCardListLengthN(250);

        for (ColorCard card : cards) {
            System.out.print(
                    "\"" + String.valueOf(card.getColor()).toLowerCase() +
                    "\",");
        }
    }

    @Test
    public void testShouldEmulateXRefThreeIn() {
        TrainsMap map = ExampleMap.createBostonMap();
        PlayerAgent zero = new PlayerAgent("zero", new BuyNowStrategy());
        PlayerAgent one = new PlayerAgent("one", new HoldTenStrategy());
        PlayerAgent two = new PlayerAgent("two", new CheaterStrategy());
        PlayerAgent three = new PlayerAgent("three", new HoldTenStrategy());
        PlayerAgent four = new PlayerAgent("four", new HoldTenStrategy());
        PlayerAgent five = new PlayerAgent("five", new HoldTenStrategy());

        LinkedList<IPlayer> players = new LinkedList<>();
        players.add(zero);
        players.add(one);
        players.add(two);
        players.add(three);
        players.add(four);
        players.add(five);

        IAdminStrategy rulebook = new OrderedDestSameCards();
        //should run only 1 request turn
        RefereeAgent referee =
                new RefereeAgent(map, players, getConstantCardListLengthN(250),
                        rulebook);
        Map<Integer, Set<IPlayer>> result = referee.playGame();

        assertTrue(three.isGameWinner());

        assertEquals(1, result.get(null).size());
        assertTrue(result.get(null).contains(two));

        result.remove(null);
        int highestScore = Collections.max(result.keySet());
        assertEquals(1, result.get(highestScore).size());
        assertTrue(result.get(highestScore).contains(three));
    }

    @Test
    public void testShouldEmulateXRefFourIn() {
        TrainsMap map = ExampleMap.createBostonMap();
        PlayerAgent zero = new PlayerAgent("zero", new CheaterStrategy());
        PlayerAgent one = new PlayerAgent("one", new CheaterStrategy());
        PlayerAgent two = new PlayerAgent("two", new CheaterStrategy());
        PlayerAgent three = new PlayerAgent("three", new CheaterStrategy());
        PlayerAgent four = new PlayerAgent("four", new CheaterStrategy());
        PlayerAgent five = new PlayerAgent("five", new CheaterStrategy());

        LinkedList<IPlayer> players = new LinkedList<>();
        players.add(zero);
        players.add(one);
        players.add(two);
        players.add(three);
        players.add(four);
        players.add(five);

        IAdminStrategy rulebook = new OrderedDestSameCards();
        //should run only 1 request turn
        RefereeAgent referee =
                new RefereeAgent(map, players, getConstantCardListLengthN(250),
                        rulebook);
        Map<Integer, Set<IPlayer>> result = referee.playGame();

        assertEquals(6, result.get(null).size());
        assertTrue(result.get(null).contains(two));

    }
}
