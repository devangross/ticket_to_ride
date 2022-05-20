package strategy;

import map.City;
import map.ColorTrains;
import map.Coord;
import map.Destination;
import map.DirectConnection;
import map.ExampleMap;
import map.TrainsMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import state.PlayerGameState;
import state.RefereeGameState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HoldTenStrategyTest {

    @Test
    public void testShouldUseHoldTenToPickFirstTwoLexStrategy() {
        City brookline = new City("Brookline", new Coord(.1f, .2f));
        City common = new City("Common", new Coord(.5f, .3f));
        City chinatown = new City("Chinatown", new Coord(.6f, .4f));
        City financial_district =
                new City("Financial District  East", new Coord(.80f, .20f));
        Set<Destination> givenDests = new HashSet<>();
        Destination bToCh = new Destination(brookline, chinatown);
        Destination bToF = new Destination(chinatown, financial_district);
        Destination bToCo = new Destination(financial_district, common);
        Destination coToCh = new Destination(common, chinatown);
        Destination fToCh = new Destination(financial_district, chinatown);

        givenDests.add(bToCh);
        givenDests.add(bToF);
        givenDests.add(bToCo);
        givenDests.add(coToCh);
        givenDests.add(fToCh);

        HoldTenStrategy strategy = new HoldTenStrategy();

        Pair<Destination> chosen = strategy.chooseTwoDestinations(givenDests);
        assertEquals(new Destination(brookline, chinatown), chosen.getFirst());
        assertEquals(new Destination(chinatown, common), chosen.getSecond());
    }

    @Test
    public void testShouldUseHoldTenToPickFirstTwoLexStrategySameFirstNames() {
        City brookline = new City("Brookline", new Coord(.1f, .2f));
        City common = new City("Common", new Coord(.5f, .3f));
        City chinatown = new City("Chinatown", new Coord(.6f, .4f));
        City financial_district =
                new City("Financial District  East", new Coord(.80f, .20f));
        City seaport = new City("Seaport", new Coord(.90f, .45f));
        City cambridge = new City("Cambridge", new Coord(.11f, .101f));

        Set<Destination> givenDests = new HashSet<>();
        givenDests.add(new Destination(brookline, common));
        givenDests.add(new Destination(brookline, chinatown));
        givenDests.add(new Destination(brookline, financial_district));
        givenDests.add(new Destination(brookline, seaport));
        givenDests.add(new Destination(brookline, cambridge));

        HoldTenStrategy strategy = new HoldTenStrategy();

        Pair<Destination> chosen = strategy.chooseTwoDestinations(givenDests);
        assertEquals(new Destination(brookline, cambridge), chosen.getFirst());
        assertEquals(new Destination(brookline, chinatown), chosen.getSecond());
    }

    @Test
    public void testbuyNowShouldThrowWhenGivenLessThanTwoDests() {
        City brookline = new City("Brookline", new Coord(.1f, .2f));
        City common = new City("Common", new Coord(.5f, .3f));

        Set<Destination> givenDests = new HashSet<>();
        givenDests.add(new Destination(brookline, common));

        HoldTenStrategy strategy = new HoldTenStrategy();

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> strategy.chooseTwoDestinations(givenDests));
    }

    @Test
    public void testShouldReqCardsWhenLessThan10() {
        TrainsMap map = ExampleMap.createAlphabeticMap();

        City A = new City("A", new Coord(.1f, .1f));
        City B = new City("B", new Coord(.9f, .1f));
        City C = new City("C", new Coord(.1f, .9f));
        City D = new City("D", new Coord(.9f, .9f));
        Destination d1 = new Destination(A, B);
        Destination d2 = new Destination(C, D);

        List<Destination> dests = new ArrayList<>();
        dests.add(d1);
        dests.add(d2);

        HashMap<ColorTrains, Integer> cards = new HashMap<>();
        cards.put(ColorTrains.RED, 2);
        cards.put(ColorTrains.GREEN, 2);
        cards.put(ColorTrains.BLUE, 3);
        cards.put(ColorTrains.WHITE, 2);

        RefereeGameState rgs =
                RefereeGameState.RefereeGameStateShuffledCards(map);

        rgs.addPlayerTest(cards, dests, new HashSet<>(), 10);

        HoldTenStrategy strategy = new HoldTenStrategy();
        PlayerGameState currentPGS = rgs.getCurrentPlayerGameState();
        Move move = strategy.makeMove(currentPGS);
        assertEquals(true, move.getMove());
    }

    @Test
    public void testShouldReqCardsWhenZeroCards() {
        TrainsMap map = ExampleMap.createAlphabeticMap();

        City A = new City("A", new Coord(.1f, .1f));
        City B = new City("B", new Coord(.9f, .1f));
        City C = new City("C", new Coord(.1f, .9f));
        City D = new City("D", new Coord(.9f, .9f));
        Destination d1 = new Destination(A, B);
        Destination d2 = new Destination(C, D);

        List<Destination> dests = new ArrayList<>();
        dests.add(d1);
        dests.add(d2);

        HashMap<ColorTrains, Integer> cards = new HashMap<>();
        cards.put(ColorTrains.RED, 0);
        cards.put(ColorTrains.GREEN, 0);
        cards.put(ColorTrains.BLUE, 0);
        cards.put(ColorTrains.WHITE, 0);

        RefereeGameState rgs =
                RefereeGameState.RefereeGameStateShuffledCards(map);

        rgs.addPlayerTest(cards, dests, new HashSet<>(), 10);

        HoldTenStrategy strategy = new HoldTenStrategy();
        PlayerGameState currentPGS = rgs.getCurrentPlayerGameState();
        Move move = strategy.makeMove(currentPGS);
        assertEquals(true, move.getMove());
    }

    @Test
    public void testShouldAcquireWhenSufficientCards() {
        TrainsMap map = ExampleMap.createAlphabeticMap();

        City A = new City("A", new Coord(.1f, .1f));
        City B = new City("B", new Coord(.9f, .1f));
        City C = new City("C", new Coord(.1f, .9f));
        City D = new City("D", new Coord(.9f, .9f));
        Destination d1 = new Destination(A, B);
        Destination d2 = new Destination(C, D);

        List<Destination> dests = new ArrayList<>();
        dests.add(d1);
        dests.add(d2);

        HashMap<ColorTrains, Integer> cards = new HashMap<>();
        cards.put(ColorTrains.RED, 10);
        cards.put(ColorTrains.GREEN, 10);
        cards.put(ColorTrains.BLUE, 10);
        cards.put(ColorTrains.WHITE, 10);

        RefereeGameState rgs =
                RefereeGameState.RefereeGameStateShuffledCards(map);

        rgs.addPlayerTest(cards, dests, new HashSet<>(), 10);

        HoldTenStrategy strategy = new HoldTenStrategy();
        PlayerGameState currentPGS = rgs.getCurrentPlayerGameState();
        Move move = strategy.makeMove(currentPGS);
        assertEquals(new DirectConnection(A, B, 4, ColorTrains.RED),
                move.getMove());
    }

    @Test
    public void testShouldAcquireBlueWhenOnlyTwoReds() {
        TrainsMap map = ExampleMap.createAlphabeticMap();

        City A = new City("A", new Coord(.1f, .1f));
        City B = new City("B", new Coord(.9f, .1f));
        City C = new City("C", new Coord(.1f, .9f));
        City D = new City("D", new Coord(.9f, .9f));
        Destination d1 = new Destination(A, B);
        Destination d2 = new Destination(C, D);

        List<Destination> dests = new ArrayList<>();
        dests.add(d1);
        dests.add(d2);

        HashMap<ColorTrains, Integer> cards = new HashMap<>();
        cards.put(ColorTrains.RED, 2);
        cards.put(ColorTrains.GREEN, 1);
        cards.put(ColorTrains.BLUE, 4);
        cards.put(ColorTrains.WHITE, 3);

        RefereeGameState rgs =
                RefereeGameState.RefereeGameStateShuffledCards(map);

        rgs.addPlayerTest(cards, dests, new HashSet<>(), 10);

        HoldTenStrategy strategy = new HoldTenStrategy();
        PlayerGameState currentPGS = rgs.getCurrentPlayerGameState();
        Move move = strategy.makeMove(currentPGS);
        assertEquals(new DirectConnection(B, C, 4, ColorTrains.BLUE),
                move.getMove());
    }

    @Test
    public void testShouldAcquireLastGreenWhenOnlyGreen() {
        TrainsMap map = ExampleMap.createAlphabeticMap();

        City A = new City("A", new Coord(.1f, .1f));
        City B = new City("B", new Coord(.9f, .1f));
        City C = new City("C", new Coord(.1f, .9f));
        City D = new City("D", new Coord(.9f, .9f));
        Destination d1 = new Destination(A, B);
        Destination d2 = new Destination(C, D);

        List<Destination> dests = new ArrayList<>();
        dests.add(d1);
        dests.add(d2);

        HashMap<ColorTrains, Integer> cards = new HashMap<>();
        cards.put(ColorTrains.RED, 0);
        cards.put(ColorTrains.GREEN, 10);
        cards.put(ColorTrains.BLUE, 0);
        cards.put(ColorTrains.WHITE, 0);

        RefereeGameState rgs =
                RefereeGameState.RefereeGameStateShuffledCards(map);

        rgs.addPlayerTest(cards, dests, new HashSet<>(), 10);

        HoldTenStrategy strategy = new HoldTenStrategy();
        PlayerGameState currentPGS = rgs.getCurrentPlayerGameState();
        Move move = strategy.makeMove(currentPGS);
        assertEquals(new DirectConnection(D, C, 5, ColorTrains.GREEN),
                move.getMove());
    }

    @Test
    public void testShouldTieBreakOnColorsWhenEqualConnectionNamesAndSegments() {
        TrainsMap map =
                ExampleMap.createSimpleDoubleConnectionWithSameConnectionNames();

        City LA = new City("LA", new Coord(.8f, .4f));
        City SF = new City("SF", new Coord(.3f, .8f));
        Destination d1 = new Destination(LA, SF);
        Destination d2 = new Destination(SF, LA);

        List<Destination> dests = new ArrayList<>();
        dests.add(d1);
        dests.add(d2);

        HashMap<ColorTrains, Integer> cards = new HashMap<>();
        cards.put(ColorTrains.RED, 10);
        cards.put(ColorTrains.GREEN, 10);
        cards.put(ColorTrains.BLUE, 10);
        cards.put(ColorTrains.WHITE, 10);

        RefereeGameState rgs =
                RefereeGameState.RefereeGameStateShuffledCards(map);

        rgs.addPlayerTest(cards, dests, new HashSet<>(), 10);

        HoldTenStrategy strategy = new HoldTenStrategy();
        PlayerGameState currentPGS = rgs.getCurrentPlayerGameState();
        Move move = strategy.makeMove(currentPGS);
        assertEquals(new DirectConnection(LA, SF, 4, ColorTrains.BLUE),
                move.getMove());
    }

    @Test
    public void testShouldTieBreakOnSegmentsWhenSameNameConnections() {
        TrainsMap map = ExampleMap.createSimpleDoubleConnectionMap();

        City LA = new City("LA", new Coord(.8f, .4f));
        City SF = new City("SF", new Coord(.3f, .8f));
        Destination d1 = new Destination(LA, SF);
        Destination d2 = new Destination(SF, LA);

        List<Destination> dests = new ArrayList<>();
        dests.add(d1);
        dests.add(d2);

        HashMap<ColorTrains, Integer> cards = new HashMap<>();
        cards.put(ColorTrains.RED, 10);
        cards.put(ColorTrains.GREEN, 10);
        cards.put(ColorTrains.BLUE, 10);
        cards.put(ColorTrains.WHITE, 10);

        RefereeGameState rgs =
                RefereeGameState.RefereeGameStateShuffledCards(map);

        rgs.addPlayerTest(cards, dests, new HashSet<>(), 10);

        HoldTenStrategy strategy = new HoldTenStrategy();
        PlayerGameState currentPGS = rgs.getCurrentPlayerGameState();
        Move move = strategy.makeMove(currentPGS);
        assertEquals(new DirectConnection(LA, SF, 3, ColorTrains.GREEN),
                move.getMove());
    }

}
