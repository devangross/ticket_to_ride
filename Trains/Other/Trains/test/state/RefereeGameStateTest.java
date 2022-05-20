package state;

import map.City;
import map.ColorTrains;
import map.Coord;
import map.Destination;
import map.DirectConnection;
import map.ExampleMap;
import map.TrainsMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RefereeGameStateTest {

    @Test
    public void testShouldCreateTestPlayerWithFixedInputs() {
        TrainsMap map = ExampleMap.createExampleMultipleConnectionMap();

        City SF = new City("SF", new Coord(.3f, .2f));
        City SAC = new City("Sac", new Coord(.4f, .24f));
        City LA = new City("LA", new Coord(.4f, .8f));
        City SLO = new City("SLO", new Coord(.31f, .65f));
        City vegas = new City("Las Vegas", new Coord(.8f, .65f));

        Destination d1 = new Destination(SF, SAC);
        Destination d2 = new Destination(LA, SLO);

        DirectConnection acquire =
                new DirectConnection(vegas, SLO, 3, ColorTrains.RED);

        List<Destination> fixedDestinations = new ArrayList<>();
        fixedDestinations.add(d1);
        fixedDestinations.add(d2);

        HashMap<ColorTrains, Integer> fixedColorCardsInput = new HashMap<>();
        fixedColorCardsInput.put(ColorTrains.RED, 4); // initialize values
        fixedColorCardsInput.put(ColorTrains.GREEN, 0);
        fixedColorCardsInput.put(ColorTrains.BLUE, 0);
        fixedColorCardsInput.put(ColorTrains.WHITE, 0);

        RefereeGameState rgs =
                RefereeGameState.RefereeGameStateShuffledCards(map);

        HashSet<DirectConnection> expectedDC = new HashSet<>();
        expectedDC.add(acquire);

        PlayerHand ph =
                rgs.addPlayerTest(fixedColorCardsInput, fixedDestinations,
                        expectedDC, 45);
        assertEquals(ph.getColorCardCount(), fixedColorCardsInput);
        assertEquals(ph.getDestinations().get(0), d1);
        assertEquals(ph.getDestinations().get(1), d2);
    }

    @Test
    public void testShouldOKValidMove() {
        TrainsMap map = ExampleMap.createExampleMultipleConnectionMap();

        City SF = new City("SF", new Coord(.3f, .2f));
        City SAC = new City("Sac", new Coord(.4f, .24f));
        City LA = new City("LA", new Coord(.4f, .8f));
        City SLO = new City("SLO", new Coord(.31f, .65f));
        City vegas = new City("Las Vegas", new Coord(.8f, .65f));

        Destination d1 = new Destination(SF, SAC);
        Destination d2 = new Destination(LA, SLO);

        DirectConnection acquire =
                new DirectConnection(vegas, SLO, 3, ColorTrains.RED);

        List<Destination> fixedDestinations = new ArrayList<>();
        fixedDestinations.add(d1);
        fixedDestinations.add(d2);

        HashMap<ColorTrains, Integer> fixedColorCardsInput = new HashMap<>();
        fixedColorCardsInput.put(ColorTrains.RED, 4); // initialize values
        fixedColorCardsInput.put(ColorTrains.GREEN, 0);
        fixedColorCardsInput.put(ColorTrains.BLUE, 0);
        fixedColorCardsInput.put(ColorTrains.WHITE, 0);

        RefereeGameState rgs =
                RefereeGameState.RefereeGameStateShuffledCards(map);

        HashSet<DirectConnection> noOwnedDCs = new HashSet<>();

        rgs.addPlayerTest(fixedColorCardsInput, fixedDestinations, noOwnedDCs,
                45);
        assertTrue(rgs.canCurrentPlayerAcquire(acquire));
    }

    @Test
    public void testShouldRejectMoveWithNotEnoughRails() {
        TrainsMap map = ExampleMap.createExampleMultipleConnectionMap();

        City SF = new City("SF", new Coord(.3f, .2f));
        City SAC = new City("Sac", new Coord(.4f, .24f));
        City LA = new City("LA", new Coord(.4f, .8f));
        City SLO = new City("SLO", new Coord(.31f, .65f));
        City vegas = new City("Las Vegas", new Coord(.8f, .65f));

        Destination d1 = new Destination(SF, SAC);
        Destination d2 = new Destination(LA, SLO);

        DirectConnection acquire =
                new DirectConnection(vegas, SLO, 3, ColorTrains.RED);

        List<Destination> fixedDestinations = new ArrayList<>();
        fixedDestinations.add(d1);
        fixedDestinations.add(d2);

        HashMap<ColorTrains, Integer> fixedColorCardsInput = new HashMap<>();
        fixedColorCardsInput.put(ColorTrains.RED, 4); // initialize values
        fixedColorCardsInput.put(ColorTrains.GREEN, 0);
        fixedColorCardsInput.put(ColorTrains.BLUE, 0);
        fixedColorCardsInput.put(ColorTrains.WHITE, 0);

        RefereeGameState rgs =
                RefereeGameState.RefereeGameStateShuffledCards(map);

        HashSet<DirectConnection> expectedDC = new HashSet<>();
        expectedDC.add(acquire);

        rgs.addPlayerTest(fixedColorCardsInput, fixedDestinations, expectedDC,
                2);

        assertFalse(rgs.canCurrentPlayerAcquire(acquire));
    }

    @Test
    public void testShouldRejectMoveWhenConnectionAlreadyOwned() {
        TrainsMap map = ExampleMap.createExampleMultipleConnectionMap();

        City SF = new City("SF", new Coord(.3f, .2f));
        City LA = new City("LA", new Coord(.4f, .8f));
        City SLO = new City("SLO", new Coord(.31f, .65f));
        City vegas = new City("Las Vegas", new Coord(.8f, .65f));

        Destination d1 = new Destination(SF, vegas);
        Destination d2 = new Destination(LA, vegas);

        DirectConnection acquire =
                new DirectConnection(vegas, SLO, 3, ColorTrains.RED);

        List<Destination> fixedDestinations = new ArrayList<>();
        fixedDestinations.add(d1);
        fixedDestinations.add(d2);

        HashMap<ColorTrains, Integer> fixedColorCardsInput = new HashMap<>();
        fixedColorCardsInput.put(ColorTrains.RED, 4); // initialize values
        fixedColorCardsInput.put(ColorTrains.GREEN, 0);
        fixedColorCardsInput.put(ColorTrains.BLUE, 0);
        fixedColorCardsInput.put(ColorTrains.WHITE, 0);

        HashMap<Integer, HashSet<DirectConnection>> ownedConnections =
                new HashMap<>();
        ownedConnections.put(0,
                new HashSet<>()); // add this player w no connections
        HashSet<DirectConnection> otherOwnedDc = new HashSet<>();
        otherOwnedDc.add(acquire);
        ownedConnections.put(1, otherOwnedDc);

        RefereeGameState rgs =
                RefereeGameState.RefereeGameStateShuffledCards(map);

        HashSet<DirectConnection> expectedDC = new HashSet<>();
        expectedDC.add(acquire);

        rgs.addPlayerTest(fixedColorCardsInput, fixedDestinations, expectedDC,
                45);

        assertFalse(rgs.canCurrentPlayerAcquire(acquire));
    }

    @Test
    public void testShouldRejectInvalidMoveNoGreenCards() {
        TrainsMap map = ExampleMap.createExampleMultipleConnectionMap();

        City SF = new City("SF", new Coord(.3f, .2f));
        City SAC = new City("Sac", new Coord(.4f, .24f));
        City LA = new City("LA", new Coord(.4f, .8f));
        City SLO = new City("SLO", new Coord(.31f, .65f));
        City vegas = new City("Las Vegas", new Coord(.8f, .65f));

        Destination d1 = new Destination(SF, SAC);
        Destination d2 = new Destination(LA, SLO);

        DirectConnection acquire =
                new DirectConnection(LA, SLO, 3, ColorTrains.GREEN);

        List<Destination> fixedDestinations = new ArrayList<>();
        fixedDestinations.add(d1);
        fixedDestinations.add(d2);

        HashMap<ColorTrains, Integer> fixedColorCardsInput = new HashMap<>();
        fixedColorCardsInput.put(ColorTrains.RED, 4); // initialize values
        fixedColorCardsInput.put(ColorTrains.GREEN, 0);
        fixedColorCardsInput.put(ColorTrains.BLUE, 0);
        fixedColorCardsInput.put(ColorTrains.WHITE, 0);

        HashMap<Integer, HashSet<DirectConnection>> ownedConnections =
                new HashMap<>();
        ownedConnections.put(0,
                new HashSet<>()); // add this player w no connections

        RefereeGameState rgs =
                RefereeGameState.RefereeGameStateShuffledCards(map);

        HashSet<DirectConnection> expectedDC = new HashSet<>();
        expectedDC.add(acquire);
        HashMap<Integer, HashSet<DirectConnection>> expectedOwnedConnections =
                new HashMap<>();
        expectedOwnedConnections.put(0, expectedDC);
        rgs.addPlayerTest(fixedColorCardsInput, fixedDestinations, expectedDC,
                45);

        // this player currently has 4 red colored cards and is trying
        // to acquire green connection of length 3
        assertFalse(rgs.canCurrentPlayerAcquire(acquire));
    }

    @Test
    public void testShouldRejectMoveWhenPlayerHasInsufficientCards() {
        TrainsMap map = ExampleMap.createExampleMultipleConnectionMap();

        City SF = new City("SF", new Coord(.3f, .2f));
        City SAC = new City("Sac", new Coord(.4f, .24f));
        City LA = new City("LA", new Coord(.4f, .8f));
        City SLO = new City("SLO", new Coord(.31f, .65f));
        City vegas = new City("Las Vegas", new Coord(.8f, .65f));

        Destination d1 = new Destination(SF, SAC);
        Destination d2 = new Destination(LA, SLO);

        DirectConnection acquire =
                new DirectConnection(vegas, SLO, 3, ColorTrains.RED);

        List<Destination> fixedDestinations = new ArrayList<>();
        fixedDestinations.add(d1);
        fixedDestinations.add(d2);

        HashMap<ColorTrains, Integer> fixedColorCardsInput = new HashMap<>();
        fixedColorCardsInput.put(ColorTrains.RED, 1); // initialize values
        fixedColorCardsInput.put(ColorTrains.GREEN, 0);
        fixedColorCardsInput.put(ColorTrains.BLUE, 0);
        fixedColorCardsInput.put(ColorTrains.WHITE, 0);

        HashMap<Integer, HashSet<DirectConnection>> ownedConnections =
                new HashMap<>();
        ownedConnections.put(0,
                new HashSet<>()); // add this player w no connections

        RefereeGameState rgs =
                RefereeGameState.RefereeGameStateShuffledCards(map);

        HashSet<DirectConnection> expectedDC = new HashSet<>();
        expectedDC.add(acquire);
        rgs.addPlayerTest(fixedColorCardsInput, fixedDestinations,
                new HashSet<>(), 45);

        PlayerHand beforePH = rgs.getCurrentPlayerHand();
        assertEquals(0, beforePH.getOwnedConnections().size());

        // trying to acquire direct connection that does not exist on board
        assertFalse(rgs.canCurrentPlayerAcquire(acquire));
    }

    @Test
    public void testShouldAddPlayerValid() {
        TrainsMap map = ExampleMap.createExampleMultipleConnectionMap();
        RefereeGameState rgs =
                RefereeGameState.RefereeGameStateShuffledCards(map);
        PlayerHand ph = rgs.addPlayer();
        assertEquals(0, ph.getOwnedConnections().size()); // initially zero
        assertNotNull(ph.getDestinations().get(0));
        assertNotNull(ph.getDestinations().get(1));
        assertEquals(45, ph.getRails()); // initially 45
    }

    @Test
    public void testShouldAddTwoPlayersValid() {
        TrainsMap map = ExampleMap.createExampleMultipleConnectionMap();
        RefereeGameState rgs =
                RefereeGameState.RefereeGameStateShuffledCards(map);
        PlayerHand ph = rgs.addPlayer();

        assertEquals(ph, rgs.getCurrentPlayerHand()); //ensure added
        assertEquals(0, ph.getOwnedConnections().size()); // initially zero
        assertNotNull(ph.getDestinations().get(0));
        assertNotNull(ph.getDestinations().get(1));
        assertEquals(45, ph.getRails()); // initially 45

        PlayerHand pgsTwo = rgs.addPlayer();
        assertEquals(0, ph.getOwnedConnections().size()); // initially zero

        assertNotNull(pgsTwo.getDestinations().get(0));
        assertNotNull(pgsTwo.getDestinations().get(1));
        assertEquals(45, pgsTwo.getRails()); // initially 45
        assertEquals(ph,
                rgs.getCurrentPlayerHand()); //ensure the first added is
        // still first

    }

    // ensure that getTwoRandDestFromMap returns two elements and they are
    // not equal
    @Test
    public void testShouldTestGetTwoRandomDestFromMap() {
        TrainsMap map = ExampleMap.createComplicatedMap();
        RefereeGameState rgs =
                RefereeGameState.RefereeGameStateShuffledCards(map);
        List<Destination> twoDestinations =
                rgs.getTwoRandomDestinationsFromMap();
        assertEquals(twoDestinations.size(), 2);
        assertNotEquals(twoDestinations.get(0), twoDestinations.get(1));
    }

    @Test
    public void testShouldThrowWhenOneFeasibleDestinationGetTwoRandomDest() {
        // ensure that it returns two elements and they are not equal
        TrainsMap map = ExampleMap.createSimpleDoubleConnectionMap();
        RefereeGameState rgs =
                RefereeGameState.RefereeGameStateShuffledCards(map);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            rgs.getTwoRandomDestinationsFromMap();
        });
    }

    @Test
    public void testShouldTestRemovePlayer() {
        TrainsMap map = ExampleMap.createExampleMultipleConnectionMap();
        RefereeGameState rgs =
                RefereeGameState.RefereeGameStateShuffledCards(map);
        PlayerHand firstPH = rgs.addPlayer();
        PlayerHand lastPH = rgs.addPlayer();

        assertEquals(firstPH, rgs.getCurrentPlayerHand());
        // we have two players
        rgs.removePlayer(); // remove the last player
        assertEquals(lastPH, rgs.getCurrentPlayerHand());
    }

    @Test
    public void testShouldGetCurrentPlayerGameState() {
        TrainsMap map = ExampleMap.createExampleMultipleConnectionMap();
        RefereeGameState rgs =
                RefereeGameState.RefereeGameStateShuffledCards(map);
        PlayerHand ph = rgs.addPlayer();
        assertEquals(ph, rgs.getCurrentPlayerHand()); // first player id is 0
        PlayerGameState pgs = rgs.getCurrentPlayerGameState();

        assertEquals(map, pgs.getTrainsMap());

        assertEquals(0, pgs.getOwnedConnections().size()); // initially zero
        assertNotNull(pgs.getDestinations().get(0));
        assertNotNull(pgs.getDestinations().get(1));
        assertEquals(45, pgs.getRails()); // initially 45
        assertEquals(1, pgs.getAllOwnedConnections().size());
    }

    @Test
    public void testdrawTwoColoredCardsShouldDrawTwo() {
        TrainsMap map = ExampleMap.createExampleMultipleConnectionMap();
        RefereeGameState rgs =
                RefereeGameState.RefereeGameStateShuffledCards(map);
        rgs.addPlayer();
        assertEquals(2, rgs.drawTwoColoredCardsMove().size());
    }

    @Test
    public void testdrawTwoColoredCardsShouldDrawUntilOnlyTwoRemaining() {
        TrainsMap map = ExampleMap.createExampleMultipleConnectionMap();
        RefereeGameState rgs =
                RefereeGameState.RefereeGameStateShuffledCards(map);
        rgs.addPlayer();
        while (rgs.getNumRemainingCards() > 2) {
            rgs.drawTwoColoredCardsMove(); // draw cards until less than 3
            // remaining
        }
        assertEquals(2, rgs.drawTwoColoredCardsMove().size());
        assertEquals(0, rgs.drawTwoColoredCardsMove().size());

    }

    @Test
    public void testShouldDetectGameStateIsFinalRound() {
        TrainsMap map = ExampleMap.createExampleMultipleConnectionMap();

        City SF = new City("SF", new Coord(.3f, .2f));
        City SAC = new City("Sac", new Coord(.4f, .24f));
        City LA = new City("LA", new Coord(.4f, .8f));
        City SLO = new City("SLO", new Coord(.31f, .65f));
        City vegas = new City("Las Vegas", new Coord(.8f, .65f));

        Destination d1 = new Destination(SF, SAC);
        Destination d2 = new Destination(LA, SLO);

        DirectConnection acquire =
                new DirectConnection(vegas, SLO, 3, ColorTrains.RED);

        List<Destination> dests = new ArrayList<>();
        dests.add(d1);
        dests.add(d2);

        HashMap<ColorTrains, Integer> cards = new HashMap<>();
        cards.put(ColorTrains.RED, 4); // initialize values
        cards.put(ColorTrains.GREEN, 0);
        cards.put(ColorTrains.BLUE, 0);
        cards.put(ColorTrains.WHITE, 0);

        RefereeGameState rgs =
                RefereeGameState.RefereeGameStateShuffledCards(map);

        HashSet<DirectConnection> expectedDC = new HashSet<>();
        expectedDC.add(acquire);

        rgs.addPlayerTest(cards, dests, new HashSet<>(), 4);
        assertFalse(rgs.isNextRoundFinal());
        rgs.addPlayerTest(cards, dests, new HashSet<>(), 2);
        assertTrue(rgs.isNextRoundFinal());
    }

    @Test
    public void testShouldAddValidConnectionAcquisition() {
        TrainsMap map = ExampleMap.createExampleMultipleConnectionMap();

        City SF = new City("SF", new Coord(.3f, .2f));
        City SAC = new City("Sac", new Coord(.4f, .24f));
        City LA = new City("LA", new Coord(.4f, .8f));
        City SLO = new City("SLO", new Coord(.31f, .65f));
        City vegas = new City("Las Vegas", new Coord(.8f, .65f));

        Destination d1 = new Destination(SF, SAC);
        Destination d2 = new Destination(LA, SLO);

        DirectConnection acquire =
                new DirectConnection(vegas, SLO, 3, ColorTrains.RED);

        List<Destination> fixedDestinations = new ArrayList<>();
        fixedDestinations.add(d1);
        fixedDestinations.add(d2);

        HashMap<ColorTrains, Integer> fixedColorCardsInput = new HashMap<>();
        fixedColorCardsInput.put(ColorTrains.RED, 4); // initialize values
        fixedColorCardsInput.put(ColorTrains.GREEN, 0);
        fixedColorCardsInput.put(ColorTrains.BLUE, 0);
        fixedColorCardsInput.put(ColorTrains.WHITE, 0);

        RefereeGameState rgs =
                RefereeGameState.RefereeGameStateShuffledCards(map);

        HashSet<DirectConnection> noOwnedDCs = new HashSet<>();

        rgs.addPlayerTest(fixedColorCardsInput, fixedDestinations, noOwnedDCs,
                45);
        assertEquals(new HashSet<>(),
                rgs.getCurrentPlayerHand().getOwnedConnections());
        rgs.addAcquiredConnectionMove(acquire);
        PlayerHand cPGS = rgs.getCurrentPlayerHand();
        HashSet<DirectConnection> expectedOwnedAfterAcquisition =
                new HashSet<>();
        expectedOwnedAfterAcquisition.add(acquire);
        assertEquals(expectedOwnedAfterAcquisition, cPGS.getOwnedConnections());
    }

    @Test
    public void testShouldAddMultipleValidConnections() {
        TrainsMap map = ExampleMap.createExampleMultipleConnectionMap();

        City SF = new City("SF", new Coord(.3f, .2f));
        City SAC = new City("Sac", new Coord(.4f, .24f));
        City LA = new City("LA", new Coord(.4f, .8f));
        City SLO = new City("SLO", new Coord(.31f, .65f));
        Destination d1 = new Destination(SF, SAC);
        Destination d2 = new Destination(LA, SLO);
        DirectConnection acquire1 =
                new DirectConnection(LA, SLO, 3, ColorTrains.GREEN);
        DirectConnection acquire2 =
                new DirectConnection(LA, SLO, 5, ColorTrains.RED);

        List<Destination> fixedDestinations = new ArrayList<>();
        fixedDestinations.add(d1);
        fixedDestinations.add(d2);

        HashMap<ColorTrains, Integer> fixedColorCardsInput = new HashMap<>();
        fixedColorCardsInput.put(ColorTrains.RED, 5); // initialize values
        fixedColorCardsInput.put(ColorTrains.GREEN, 3);
        fixedColorCardsInput.put(ColorTrains.BLUE, 0);
        fixedColorCardsInput.put(ColorTrains.WHITE, 0);

        RefereeGameState rgs =
                RefereeGameState.RefereeGameStateShuffledCards(map);

        HashSet<DirectConnection> noOwnedDCs = new HashSet<>();

        rgs.addPlayerTest(fixedColorCardsInput, fixedDestinations, noOwnedDCs,
                10);
        // acquire the first connection
        assertEquals(new HashSet<>(),
                rgs.getCurrentPlayerHand().getOwnedConnections());
        rgs.addAcquiredConnectionMove(acquire1);
        HashSet<DirectConnection> expectedOwnedAfterAcquisition =
                new HashSet<>();
        expectedOwnedAfterAcquisition.add(acquire1);
        assertEquals(expectedOwnedAfterAcquisition,
                rgs.getCurrentPlayerHand().getOwnedConnections());
        // acquire the second connection
        rgs.addAcquiredConnectionMove(acquire2);
        expectedOwnedAfterAcquisition.add(acquire2);
        assertEquals(expectedOwnedAfterAcquisition,
                rgs.getCurrentPlayerHand().getOwnedConnections());
        // THE PLAYER ONLY HAS 2 RAILS LEFT SO NEXT ROUND IS FINAL
        assertEquals(2, rgs.getCurrentPlayerHand().getRails());
        //assertTrue(rgs.isNextRoundFinal());
    }

}
