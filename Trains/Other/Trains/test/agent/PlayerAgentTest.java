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
import strategy.BuyNowStrategy;
import strategy.HoldTenStrategy;
import strategy.IAdminStrategy;
import strategy.Move;
import strategy.Pair;
import strategy.RandomAdminStrategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerAgentTest {
    public List<ColorCard> getNRedCards(int n) {
        List<ColorCard> cards = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            cards.add(new ColorCard(ColorTrains.RED));
        }

        return cards;
    }

    public List<ColorCard> getNBlueCards(int n) {
        List<ColorCard> cards = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            cards.add(new ColorCard(ColorTrains.BLUE));
        }

        return cards;
    }

    public List<ColorCard> getNWhiteCards(int n) {
        List<ColorCard> cards = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            cards.add(new ColorCard(ColorTrains.WHITE));
        }

        return cards;
    }

    @Test
    public void testShouldSetupAPlayerAgentCards() {
        TrainsMap map = ExampleMap.createBostonMap();
        PlayerAgent player0 =
                new PlayerAgent("playerzero", new HoldTenStrategy());

        HashMap<ColorTrains, Integer> expectedCardsMap = new HashMap<>();
        expectedCardsMap.put(ColorTrains.RED, 5);
        expectedCardsMap.put(ColorTrains.GREEN, 0);
        expectedCardsMap.put(ColorTrains.BLUE, 0);
        expectedCardsMap.put(ColorTrains.WHITE, 0);

        player0.setup(map, 45, getNRedCards(5));
        assertEquals(expectedCardsMap, player0.getGameState().getCardsMap());
    }

    @Test
    public void testShouldSetupAPlayerAgentRails() {
        TrainsMap map = ExampleMap.createBostonMap();
        PlayerAgent player0 =
                new PlayerAgent("playerzero", new HoldTenStrategy());

        player0.setup(map, 45, getNRedCards(5));
        assertEquals(45, player0.getGameState().getRails());
    }

    @Test
    public void testShouldConstructAPlayerWithStrat() {
        TrainsMap map = ExampleMap.createBostonMap();

        City LA = new City("LA", new Coord(.4f, .8f));
        City SF = new City("SF", new Coord(.3f, .2f));
        City SAC = new City("Sac", new Coord(.4f, .24f));
        City SLO = new City("SLO", new Coord(.31f, .65f));
        City vegas = new City("Las Vegas", new Coord(.8f, .65f));
        Destination d1 = new Destination(LA, SF);
        Destination d2 = new Destination(SF, SAC);
        Destination d3 = new Destination(vegas, SF);
        Destination d4 = new Destination(SLO, SF);
        Destination d5 = new Destination(SAC, vegas);

        List<Destination> given =
                new ArrayList<>(Arrays.asList(d1, d2, d3, d4, d5));

        PlayerAgent player0 =
                new PlayerAgent("playerzero", new HoldTenStrategy());
        player0.setup(map, 45, getNRedCards(5));
        List<Destination> rejected = player0.pick(given);
        given.removeAll(rejected);
        assertEquals(2, given.size());
        assertEquals(new Destination(LA, SF), given.get(0));
        assertEquals(new Destination(vegas, SF), given.get(1));
    }

    @Test
    public void testShouldConstructAPlayerWithStratByPath() {
        TrainsMap map = ExampleMap.createBostonMap();

        City LA = new City("LA", new Coord(.4f, .8f));
        City SF = new City("SF", new Coord(.3f, .2f));
        City SAC = new City("Sac", new Coord(.4f, .24f));
        City SLO = new City("SLO", new Coord(.31f, .65f));
        City vegas = new City("Las Vegas", new Coord(.8f, .65f));
        Destination d1 = new Destination(LA, SF);
        Destination d2 = new Destination(SF, SAC);
        Destination d3 = new Destination(vegas, SF);
        Destination d4 = new Destination(SLO, SF);
        Destination d5 = new Destination(SAC, vegas);

        List<Destination> given =
                new ArrayList<>(Arrays.asList(d1, d2, d3, d4, d5));

        String path = "Trains/out/production/Trains/strategy.HoldTenStrategy";

        PlayerAgent player0 = new PlayerAgent("playerzero", path);
        player0.setup(map, 45, getNRedCards(5));
        List<Destination> rejected = player0.pick(given);
        given.removeAll(rejected);
        assertEquals(2, given.size());
        assertEquals(new Destination(LA, SF), given.get(0));
        assertEquals(new Destination(vegas, SF), given.get(1));
    }

    @Test
    public void testShouldThrowPlayerWithStratByPath() {
        String path = "Trains/out/production/Trains/strategy.Unknown";
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new PlayerAgent("playerzero", path));
    }

    @Test
    public void testShouldSetupAPlayerHoldTenStrategy() {
        TrainsMap map = ExampleMap.createBostonMap();
        PlayerAgent player0 =
                new PlayerAgent("playerzero", new HoldTenStrategy());
        player0.setup(map, 45, getNRedCards(5));

        Set<Destination> mapDestinations = map.getAllFeasibleDestinations();

        List<Destination> mapFeasibleDestList =
                new ArrayList<>(mapDestinations);

        IAdminStrategy randomRefereeRule = new RandomAdminStrategy();
        List<Destination> refSortedDests =
                randomRefereeRule.orderDestinations(mapFeasibleDestList);
        assertEquals(15, refSortedDests.size());
    }

    @Test
    public void testShouldPickFirstTwoDestsWhenHoldTenPlayer() {
        TrainsMap map = ExampleMap.createBostonMap();
        PlayerAgent player0 =
                new PlayerAgent("playerzero", new HoldTenStrategy());
        player0.setup(map, 45, getNRedCards(5));

        Set<Destination> mapDestinations = map.getAllFeasibleDestinations();

        List<Destination> mapFeasibleDestList =
                new ArrayList<>(mapDestinations);
        Collections.sort(mapFeasibleDestList);

        List<Destination> fiveDestToChooseFrom = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            fiveDestToChooseFrom.add(mapFeasibleDestList.get(i));
        }
        List<Destination> rejected = player0.pick(fiveDestToChooseFrom);
        fiveDestToChooseFrom.removeAll(rejected);
        assertEquals(3, rejected.size());
        assertEquals(2, fiveDestToChooseFrom.size());
        // now fiveDestToChooseFrom only contains the Destinations picked by p0

        ArrayList<Destination> chosenAsList =
                new ArrayList<>(fiveDestToChooseFrom);
        Pair<City> firstChosenDestCities = chosenAsList.get(0).getVertices();
        Pair<City> secondChosenDestCities = chosenAsList.get(1).getVertices();

        assertEquals("Brookline", firstChosenDestCities.getFirst().getName());
        assertEquals("Cambridge", firstChosenDestCities.getSecond().getName());

        assertEquals("Brookline", secondChosenDestCities.getFirst().getName());
        assertEquals("Chinatown", secondChosenDestCities.getSecond().getName());
    }

    @Test
    public void testShouldPickLastTwoDestsWhenBuyNowPlayer() {
        TrainsMap map = ExampleMap.createBostonMap();
        PlayerAgent player0 =
                new PlayerAgent("playerzero", new BuyNowStrategy());
        player0.setup(map, 45, getNRedCards(5));

        Set<Destination> mapDestinations = map.getAllFeasibleDestinations();

        List<Destination> mapFeasibleDestList =
                new ArrayList<>(mapDestinations);
        Collections.sort(mapFeasibleDestList);
        Collections.reverse(mapFeasibleDestList);
        List<Destination> fiveDestToChooseFrom = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            fiveDestToChooseFrom.add(mapFeasibleDestList.get(i));
        }

        List<Destination> rejected = player0.pick(fiveDestToChooseFrom);

        fiveDestToChooseFrom.removeAll(rejected);
        assertEquals(3, rejected.size());
        assertEquals(2, fiveDestToChooseFrom.size());
        // now fiveDestToChooseFrom only contains the Destinations picked by p0

        ArrayList<Destination> chosenAsList =
                new ArrayList<>(fiveDestToChooseFrom);
        Pair<City> firstChosenDestCities = chosenAsList.get(0).getVertices();
        Pair<City> secondChosenDestCities = chosenAsList.get(1).getVertices();

        assertEquals("Financial District",
                firstChosenDestCities.getFirst().getName());
        assertEquals("Seaport", firstChosenDestCities.getSecond().getName());
        assertEquals("Common", secondChosenDestCities.getFirst().getName());
        assertEquals("Seaport", secondChosenDestCities.getSecond().getName());
    }

    @Test
    public void testShouldReqCardsWhenHoldTenPlay() {
        TrainsMap map = ExampleMap.createBostonMap();
        PlayerAgent player0 =
                new PlayerAgent("playerzero", new HoldTenStrategy());
        player0.setup(map, 45, getNRedCards(5));

        Set<Destination> mapDestinations = map.getAllFeasibleDestinations();

        List<Destination> mapFeasibleDestList =
                new ArrayList<>(mapDestinations);

        IAdminStrategy randomRefereeRule = new RandomAdminStrategy();
        List<Destination> refSortedDests =
                randomRefereeRule.orderDestinations(mapFeasibleDestList);
        assertEquals(15, refSortedDests.size());

        Move move = player0.play(player0.getGameState());
        assertEquals(true, move.getMove());
    }

    @Test
    public void testShouldAcquireWhenHoldTenPlayerWithTenRed() {
        TrainsMap map = ExampleMap.createBostonMap();
        PlayerAgent player0 =
                new PlayerAgent("playerzero", new HoldTenStrategy());
        player0.setup(map, 45, getNRedCards(10));

        Set<Destination> mapDestinations = map.getAllFeasibleDestinations();

        List<Destination> mapFeasibleDestList =
                new ArrayList<>(mapDestinations);

        IAdminStrategy randomRefereeRule = new RandomAdminStrategy();
        List<Destination> refSortedDests =
                randomRefereeRule.orderDestinations(mapFeasibleDestList);
        assertEquals(15, refSortedDests.size());

        //we cast to avoid instance of in test
        Move<DirectConnection> move = player0.play(player0.getGameState());
        DirectConnection chosen = move.getMove();
        String dcCity0Name = chosen.getCity0().getName();
        String dcCity1Name = chosen.getCity1().getName();
        assertEquals("Brookline", dcCity0Name);
        assertEquals("Cambridge", dcCity1Name);
    }

    @Test
    public void testShouldAcquireWhenHoldTenPlayerWithTenBlue() {
        TrainsMap map = ExampleMap.createBostonMap();
        PlayerAgent player0 =
                new PlayerAgent("playerzero", new HoldTenStrategy());
        player0.setup(map, 45, getNBlueCards(10));

        Set<Destination> mapDestinations = map.getAllFeasibleDestinations();

        List<Destination> mapFeasibleDestList =
                new ArrayList<>(mapDestinations);

        IAdminStrategy randomRefereeRule = new RandomAdminStrategy();
        List<Destination> refSortedDests =
                randomRefereeRule.orderDestinations(mapFeasibleDestList);
        assertEquals(15, refSortedDests.size());

        Move<DirectConnection> move = player0.play(player0.getGameState());
        DirectConnection chosen = move.getMove();
        String dcCity0Name = chosen.getCity0().getName();
        String dcCity1Name = chosen.getCity1().getName();
        assertEquals("Brookline", dcCity0Name);
        assertEquals("Common", dcCity1Name);
    }

    @Test
    public void testShouldAcquireWhenHoldTenPlayerWithTenWhite() {
        TrainsMap map = ExampleMap.createBostonMap();
        PlayerAgent player0 =
                new PlayerAgent("playerzero", new HoldTenStrategy());
        player0.setup(map, 45, getNWhiteCards(10));

        Set<Destination> mapDestinations = map.getAllFeasibleDestinations();

        List<Destination> mapFeasibleDestList =
                new ArrayList<>(mapDestinations);

        IAdminStrategy randomRefereeRule = new RandomAdminStrategy();
        List<Destination> refSortedDests =
                randomRefereeRule.orderDestinations(mapFeasibleDestList);
        assertEquals(15, refSortedDests.size());

        Move<DirectConnection> move = player0.play(player0.getGameState());
        DirectConnection chosen = move.getMove();
        String dcCity0Name = chosen.getCity0().getName();
        String dcCity1Name = chosen.getCity1().getName();
        assertEquals("Chinatown", dcCity0Name);
        assertEquals("Common", dcCity1Name);
    }

    @Test
    public void testbuyNowPlayerAgentWithFiveCardsShouldAcquire() {
        TrainsMap map = ExampleMap.createBostonMap();
        PlayerAgent player0 =
                new PlayerAgent("playerzero", new BuyNowStrategy());
        player0.setup(map, 45, getNRedCards(5));

        Set<Destination> mapDestinations = map.getAllFeasibleDestinations();

        List<Destination> mapFeasibleDestList =
                new ArrayList<>(mapDestinations);

        IAdminStrategy randomRefereeRule = new RandomAdminStrategy();
        List<Destination> refSortedDests =
                randomRefereeRule.orderDestinations(mapFeasibleDestList);
        assertEquals(15, refSortedDests.size());

        Move<DirectConnection> move = player0.play(player0.getGameState());
        DirectConnection chosen = move.getMove();
        String dcCity0Name = chosen.getCity0().getName();
        String dcCity1Name = chosen.getCity1().getName();
        assertEquals("Brookline", dcCity0Name);
        assertEquals("Cambridge", dcCity1Name);
    }

    @Test
    public void testbuyNowPlayerAgentWithNoCardsShouldRequestMore() {
        TrainsMap map = ExampleMap.createBostonMap();
        PlayerAgent player0 =
                new PlayerAgent("playerzero", new BuyNowStrategy());
        player0.setup(map, 45, getNRedCards(0));

        Move move = player0.play(player0.getGameState());
        assertEquals(true, move.getMove());
    }

    @Test
    public void testShouldAddCardsWhenPlayerGetsMoreCards() {
        TrainsMap map = ExampleMap.createBostonMap();
        PlayerAgent player0 =
                new PlayerAgent("playerzero", new BuyNowStrategy());
        player0.setup(map, 45, getNRedCards(5));
        assertEquals(5,
                player0.getGameState().getCardsMap().get(ColorTrains.RED));
        player0.more(getNRedCards(2));
        assertEquals(7,
                player0.getGameState().getCardsMap().get(ColorTrains.RED));
    }

    @Test
    public void testShouldAddBlueCardsWhenPlayerGetsMoreCards() {
        TrainsMap map = ExampleMap.createBostonMap();
        PlayerAgent player0 =
                new PlayerAgent("playerzero", new BuyNowStrategy());
        player0.setup(map, 45, getNRedCards(5));
        assertEquals(5,
                player0.getGameState().getCardsMap().get(ColorTrains.RED));
        player0.more(getNBlueCards(2));
        assertEquals(5,
                player0.getGameState().getCardsMap().get(ColorTrains.RED));
        assertEquals(2,
                player0.getGameState().getCardsMap().get(ColorTrains.BLUE));
        assertEquals(0,
                player0.getGameState().getCardsMap().get(ColorTrains.GREEN));
        assertEquals(0,
                player0.getGameState().getCardsMap().get(ColorTrains.WHITE));
    }

}
