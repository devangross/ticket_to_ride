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

public class BuyNowStrategyTest {
    @Test
    public void testShouldUseBuyNowToPickLastTwoLexStrategy() {
        City brookline = new City("Brookline", new Coord(.1f, .2f));
        City common = new City("Common", new Coord(.5f, .3f));
        City chinatown = new City("Chinatown", new Coord(.6f, .4f));
        City financial_district =
                new City("Financial District  East", new Coord(.80f, .20f));
        Set<Destination> givenDests = new HashSet<>();
        givenDests.add(new Destination(brookline, chinatown));
        givenDests.add(new Destination(brookline, financial_district));
        givenDests.add(new Destination(brookline, common));
        givenDests.add(new Destination(common, chinatown));
        givenDests.add(new Destination(financial_district, chinatown));

        BuyNowStrategy strategy = new BuyNowStrategy();

        Pair<Destination> chosen = strategy.chooseTwoDestinations(givenDests);
        assertEquals(new Destination(financial_district, chinatown),
                chosen.getFirst());
        assertEquals(new Destination(common, chinatown), chosen.getSecond());
    }

    @Test
    public void testShouldUseBuyNowToPickLastTwoLexSameFirstNames() {
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

        BuyNowStrategy strategy = new BuyNowStrategy();

        Pair<Destination> chosen = strategy.chooseTwoDestinations(givenDests);
        assertEquals(new Destination(brookline, financial_district),
                chosen.getSecond());
        assertEquals(new Destination(brookline, seaport), chosen.getFirst());
    }

    @Test
    public void testShouldThrowWhenGivenLessThanTwoDests() {
        City brookline = new City("Brookline", new Coord(.1f, .2f));
        City common = new City("Common", new Coord(.5f, .3f));
        Set<Destination> givenDests = new HashSet<>();
        givenDests.add(new Destination(brookline, common));

        BuyNowStrategy strategy = new BuyNowStrategy();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            strategy.chooseTwoDestinations(givenDests);
        });
    }

    @Test
    public void testShouldRequestCardsWhenInsufficient() {
        TrainsMap map = ExampleMap.createExampleMultipleConnectionMap();

        City SF = new City("SF", new Coord(.3f, .2f));
        City SAC = new City("Sac", new Coord(.4f, .24f));
        City LA = new City("LA", new Coord(.4f, .8f));
        City SLO = new City("SLO", new Coord(.31f, .65f));
        Destination d1 = new Destination(SF, SAC);
        Destination d2 = new Destination(LA, SLO);

        List<Destination> fixedDestinations = new ArrayList<>();
        fixedDestinations.add(d1);
        fixedDestinations.add(d2);

        HashMap<ColorTrains, Integer> fixedColorCardsInput = new HashMap<>();
        fixedColorCardsInput.put(ColorTrains.RED, 2); // initialize values
        fixedColorCardsInput.put(ColorTrains.GREEN, 2);
        fixedColorCardsInput.put(ColorTrains.BLUE, 0);
        fixedColorCardsInput.put(ColorTrains.WHITE, 0);

        RefereeGameState rgs =
                RefereeGameState.RefereeGameStateShuffledCards(map);

        HashSet<DirectConnection> noOwnedDCs = new HashSet<>();

        rgs.addPlayerTest(fixedColorCardsInput, fixedDestinations, noOwnedDCs,
                10);

        BuyNowStrategy strategy = new BuyNowStrategy();
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

        List<Destination> fixedDestinations = new ArrayList<>();
        fixedDestinations.add(d1);
        fixedDestinations.add(d2);

        HashMap<ColorTrains, Integer> fixedColorCardsInput = new HashMap<>();
        fixedColorCardsInput.put(ColorTrains.RED, 10);
        fixedColorCardsInput.put(ColorTrains.GREEN, 10);
        fixedColorCardsInput.put(ColorTrains.BLUE, 10);
        fixedColorCardsInput.put(ColorTrains.WHITE, 10);

        RefereeGameState rgs =
                RefereeGameState.RefereeGameStateShuffledCards(map);

        HashSet<DirectConnection> noOwnedDCs = new HashSet<>();

        rgs.addPlayerTest(fixedColorCardsInput, fixedDestinations, noOwnedDCs,
                10);

        BuyNowStrategy strategy = new BuyNowStrategy();
        PlayerGameState currentPGS = rgs.getCurrentPlayerGameState();
        Move move = strategy.makeMove(currentPGS);
        assertEquals(new DirectConnection(A, B, 4, ColorTrains.RED),
                move.getMove());
    }

    @Test
    public void testShouldAcquireSecondWhenSufficientCardsNoRed() {
        TrainsMap map = ExampleMap.createAlphabeticMap();

        City A = new City("A", new Coord(.1f, .1f));
        City B = new City("B", new Coord(.9f, .1f));
        City C = new City("C", new Coord(.1f, .9f));
        City D = new City("D", new Coord(.9f, .9f));
        Destination d1 = new Destination(A, B);
        Destination d2 = new Destination(C, D);

        List<Destination> fixedDestinations = new ArrayList<>();
        fixedDestinations.add(d1);
        fixedDestinations.add(d2);

        HashMap<ColorTrains, Integer> fixedColorCardsInput = new HashMap<>();
        fixedColorCardsInput.put(ColorTrains.RED, 0);
        fixedColorCardsInput.put(ColorTrains.GREEN, 10);
        fixedColorCardsInput.put(ColorTrains.BLUE, 10);
        fixedColorCardsInput.put(ColorTrains.WHITE, 10);

        RefereeGameState rgs =
                RefereeGameState.RefereeGameStateShuffledCards(map);

        HashSet<DirectConnection> noOwnedDCs = new HashSet<>();

        rgs.addPlayerTest(fixedColorCardsInput, fixedDestinations, noOwnedDCs,
                10);

        BuyNowStrategy strategy = new BuyNowStrategy();
        PlayerGameState currentPGS = rgs.getCurrentPlayerGameState();
        Move move = strategy.makeMove(currentPGS);
        assertEquals(new DirectConnection(A, C, 4, ColorTrains.WHITE),
                move.getMove());
    }

    @Test
    public void testShouldAcquireLastLexWhenOnlyGreenCards() {
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

        HashMap<ColorTrains, Integer> onlyGreenCards = new HashMap<>();
        onlyGreenCards.put(ColorTrains.RED, 0);
        onlyGreenCards.put(ColorTrains.GREEN, 10);
        onlyGreenCards.put(ColorTrains.BLUE, 0);
        onlyGreenCards.put(ColorTrains.WHITE, 0);

        RefereeGameState rgs =
                RefereeGameState.RefereeGameStateShuffledCards(map);

        HashSet<DirectConnection> noOwnedDCs = new HashSet<>();

        rgs.addPlayerTest(onlyGreenCards, dests, noOwnedDCs, 10);

        BuyNowStrategy strategy = new BuyNowStrategy();
        PlayerGameState currentPGS = rgs.getCurrentPlayerGameState();
        Move move = strategy.makeMove(currentPGS);
        assertEquals(new DirectConnection(C, D, 5, ColorTrains.GREEN),
                move.getMove());
    }

}
