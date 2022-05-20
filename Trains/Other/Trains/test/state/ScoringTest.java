package state;

import agent.RefereeAgentTest;
import map.City;
import map.ColorTrains;
import map.Coord;
import map.Destination;
import map.DirectConnection;
import map.ExampleMap;
import map.TrainsMap;
import org.junit.jupiter.api.Test;
import strategy.OrderedDestSameCards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScoringTest {

    @Test
    public void testShouldGetScoreOfSimpleGameWithDestAndLongestPath() {

        TrainsMap map = ExampleMap.createExampleMultipleConnectionMap();
        City SF = new City("SF", new Coord(.3f, .2f));
        City SAC = new City("Sac", new Coord(.4f, .24f));
        City LA = new City("LA", new Coord(.4f, .8f));
        City SLO = new City("SLO", new Coord(.31f, .65f));
        City vegas = new City("Las Vegas", new Coord(.8f, .65f));

        Destination d1 = new Destination(SF, SAC);
        Destination d2 = new Destination(LA, SLO);

        DirectConnection acquire1 =
                new DirectConnection(vegas, SLO, 3, ColorTrains.RED);
        DirectConnection acquire2 =
                new DirectConnection(SF, SAC, 4, ColorTrains.RED);
        DirectConnection acquire3 =
                new DirectConnection(LA, SLO, 3, ColorTrains.GREEN);

        List<Destination> fixedDestinations = new ArrayList<>();
        fixedDestinations.add(d1);
        fixedDestinations.add(d2);

        HashMap<ColorTrains, Integer> fixedColorCardsInput = new HashMap<>();
        fixedColorCardsInput.put(ColorTrains.RED, 20); // initialize values
        fixedColorCardsInput.put(ColorTrains.GREEN, 20);
        fixedColorCardsInput.put(ColorTrains.BLUE, 20);
        fixedColorCardsInput.put(ColorTrains.WHITE, 20);

        RefereeGameState rgs = new RefereeGameState(map,
                RefereeAgentTest.getConstantCardListLengthN(200),
                new OrderedDestSameCards());

        HashSet<DirectConnection> noOwnedDCs = new HashSet<>();

        rgs.addPlayerTest(fixedColorCardsInput, fixedDestinations, noOwnedDCs,
                45);
        assertEquals(new HashSet<>(),
                rgs.getCurrentPlayerHand().getOwnedConnections());
        rgs.addAcquiredConnectionMove(acquire1);
        rgs.addAcquiredConnectionMove(acquire2);
        rgs.addAcquiredConnectionMove(acquire3);

        List<PlayerHand> lPGS = new ArrayList<>();
        lPGS.add(rgs.getCurrentPlayerHand());
        Map<Integer, Set<PlayerHand>> ranking = Scoring.getRanking(lPGS);
        assertEquals(new HashSet<>(List.of(50)), ranking.keySet());
    }

    @Test
    public void testShouldGetScoreOfSimpleGameWithoutDest() {

        TrainsMap map = ExampleMap.createExampleMultipleConnectionMap();
        City SF = new City("SF", new Coord(.3f, .2f));
        City SAC = new City("Sac", new Coord(.4f, .24f));
        City LA = new City("LA", new Coord(.4f, .8f));
        City SLO = new City("SLO", new Coord(.31f, .65f));
        City vegas = new City("Las Vegas", new Coord(.8f, .65f));

        Destination d1 = new Destination(SF, SAC);
        Destination d2 = new Destination(LA, SLO);

        DirectConnection acquire1 =
                new DirectConnection(vegas, SLO, 3, ColorTrains.RED);
        DirectConnection acquire2 =
                new DirectConnection(SF, SAC, 4, ColorTrains.RED);
        DirectConnection acquire3 =
                new DirectConnection(LA, SLO, 3, ColorTrains.GREEN);

        List<Destination> fixedDestinations = new ArrayList<>();
        fixedDestinations.add(d1);
        fixedDestinations.add(d2);

        HashMap<ColorTrains, Integer> fixedColorCardsInput = new HashMap<>();
        fixedColorCardsInput.put(ColorTrains.RED, 20); // initialize values
        fixedColorCardsInput.put(ColorTrains.GREEN, 20);
        fixedColorCardsInput.put(ColorTrains.BLUE, 20);
        fixedColorCardsInput.put(ColorTrains.WHITE, 20);

        RefereeGameState rgs = new RefereeGameState(map,
                RefereeAgentTest.getConstantCardListLengthN(200),
                new OrderedDestSameCards());

        HashSet<DirectConnection> noOwnedDCs = new HashSet<>();

        rgs.addPlayerTest(fixedColorCardsInput, fixedDestinations, noOwnedDCs,
                45);

        assertEquals(new HashSet<>(),
                rgs.getCurrentPlayerHand().getOwnedConnections());
        rgs.addAcquiredConnectionMove(acquire1);
        rgs.addAcquiredConnectionMove(acquire2);
        rgs.addAcquiredConnectionMove(acquire3);

        List<PlayerHand> lPGS = new ArrayList<>();
        lPGS.add(rgs.getCurrentPlayerHand());
        Set<PlayerHand> winningPLayers = Scoring.getWinner(lPGS);
        assertEquals(new HashSet<>(List.of(50)),
                Scoring.getRanking(lPGS).keySet());
        assertEquals(1, winningPLayers.size());
    }

    @Test
    public void testShouldGetSumSegmentsEmptyList() {
        assertEquals(0, Scoring.getSumSegments(new ArrayList<>()));
    }

    @Test
    public void testShouldGetSumSegmentsMedium() {
        List<DirectConnection> connectionsList = new ArrayList<>(
                ExampleMap.createExampleMap().getDirectConnections());
        assertEquals(12, Scoring.getSumSegments(connectionsList));
    }

    @Test
    public void testShouldGetSumSegmentsHuge() {
        List<DirectConnection> connectionsList = new ArrayList<>(
                ExampleMap.createBigBostonMap().getDirectConnections());
        assertEquals(99, Scoring.getSumSegments(connectionsList));
    }

    @Test
    public void testShouldGetNegativeTwentyDestinationPoints() {
        City financial_district =
                new City("Financial District", new Coord(.80f, .3f));
        City seaport = new City("Seaport", new Coord(.90f, .6f));
        City cambridge = new City("Cambridge", new Coord(.11f, .101f));
        City brookline = new City("Brookline", new Coord(.1f, .5f));
        City common = new City("Common", new Coord(.5f, .4f));

        List<Destination> playerChosenDestinations = new ArrayList<>();
        playerChosenDestinations.add(
                new Destination(brookline, financial_district));
        playerChosenDestinations.add(new Destination(seaport, cambridge));

        List<List<City>> givenConnectedComponents =
                new ArrayList<>(new ArrayList<>());
        givenConnectedComponents.add(new ArrayList<>(
                Arrays.asList(common, financial_district, cambridge)));
        assertEquals(-20, Scoring.getDestinationPoints(givenConnectedComponents,
                playerChosenDestinations));
    }

    @Test
    public void testShouldGetZeroDestinationPoints() {
        City financial_district =
                new City("Financial District", new Coord(.80f, .3f));
        City seaport = new City("Seaport", new Coord(.90f, .6f));
        City cambridge = new City("Cambridge", new Coord(.11f, .101f));
        City brookline = new City("Brookline", new Coord(.1f, .5f));
        City common = new City("Common", new Coord(.5f, .4f));

        List<Destination> playerChosenDestinations = new ArrayList<>();
        playerChosenDestinations.add(
                new Destination(brookline, financial_district));
        playerChosenDestinations.add(new Destination(seaport, cambridge));

        List<List<City>> givenConnectedComponents =
                new ArrayList<>(new ArrayList<>());
        givenConnectedComponents.add(new ArrayList<>(
                Arrays.asList(common, financial_district, cambridge,
                        brookline)));
        assertEquals(0, Scoring.getDestinationPoints(givenConnectedComponents,
                playerChosenDestinations));
    }

    @Test
    public void testShouldGetZeroDestinationPointsOther() {
        City financial_district =
                new City("Financial District", new Coord(.80f, .3f));
        City seaport = new City("Seaport", new Coord(.90f, .6f));
        City cambridge = new City("Cambridge", new Coord(.11f, .101f));
        City brookline = new City("Brookline", new Coord(.1f, .5f));
        City common = new City("Common", new Coord(.5f, .4f));

        List<Destination> playerChosenDestinations = new ArrayList<>();
        playerChosenDestinations.add(
                new Destination(brookline, financial_district));
        playerChosenDestinations.add(new Destination(seaport, cambridge));

        List<List<City>> givenConnectedComponents =
                new ArrayList<>(new ArrayList<>());
        givenConnectedComponents.add(new ArrayList<>(
                Arrays.asList(common, seaport, cambridge, brookline)));
        assertEquals(0, Scoring.getDestinationPoints(givenConnectedComponents,
                playerChosenDestinations));
    }

    @Test
    public void testShouldGetZeroDestinationPointsDisjoint() {
        City financial_district =
                new City("Financial District", new Coord(.80f, .3f));
        City seaport = new City("Seaport", new Coord(.90f, .6f));
        City cambridge = new City("Cambridge", new Coord(.11f, .101f));
        City brookline = new City("Brookline", new Coord(.1f, .5f));
        City common = new City("Common", new Coord(.5f, .4f));

        List<Destination> playerChosenDestinations = new ArrayList<>();
        playerChosenDestinations.add(new Destination(cambridge, brookline));
        playerChosenDestinations.add(
                new Destination(seaport, financial_district));

        List<List<City>> givenConnectedComponents =
                new ArrayList<>(new ArrayList<>());
        givenConnectedComponents.add(
                new ArrayList<>(Arrays.asList(seaport, financial_district)));
        givenConnectedComponents.add(
                new ArrayList<>(Arrays.asList(common, cambridge)));
        assertEquals(0, Scoring.getDestinationPoints(givenConnectedComponents,
                playerChosenDestinations));
    }

    @Test
    public void testShouldGetTwentyDestinationPointsDisjoint() {
        City financial_district =
                new City("Financial District", new Coord(.80f, .3f));
        City seaport = new City("Seaport", new Coord(.90f, .6f));
        City cambridge = new City("Cambridge", new Coord(.11f, .101f));
        City brookline = new City("Brookline", new Coord(.1f, .5f));
        City common = new City("Common", new Coord(.5f, .4f));

        List<Destination> playerChosenDestinations = new ArrayList<>();
        playerChosenDestinations.add(new Destination(cambridge, brookline));
        playerChosenDestinations.add(
                new Destination(seaport, financial_district));

        List<List<City>> givenConnectedComponents =
                new ArrayList<>(new ArrayList<>());
        givenConnectedComponents.add(
                new ArrayList<>(Arrays.asList(seaport, financial_district)));
        givenConnectedComponents.add(
                new ArrayList<>(Arrays.asList(cambridge, brookline)));
        assertEquals(20, Scoring.getDestinationPoints(givenConnectedComponents,
                playerChosenDestinations));
    }

    @Test
    public void testShouldGetTwentyDestinationPoints() {
        City financial_district =
                new City("Financial District", new Coord(.80f, .3f));
        City seaport = new City("Seaport", new Coord(.90f, .6f));
        City cambridge = new City("Cambridge", new Coord(.11f, .101f));
        City brookline = new City("Brookline", new Coord(.1f, .5f));
        City common = new City("Common", new Coord(.5f, .4f));

        List<Destination> playerChosenDestinations = new ArrayList<>();
        playerChosenDestinations.add(new Destination(cambridge, seaport));
        playerChosenDestinations.add(
                new Destination(brookline, financial_district));

        List<List<City>> givenConnectedComponents =
                new ArrayList<>(new ArrayList<>());
        givenConnectedComponents.add(new ArrayList<>(
                Arrays.asList(financial_district, common, brookline, seaport,
                        cambridge)));
        assertEquals(20, Scoring.getDestinationPoints(givenConnectedComponents,
                playerChosenDestinations));
    }

    @Test
    public void testShouldGetLengthLongestPathWhenCycle() {
        City brookline = new City("Brookline", new Coord(.1f, .5f));
        City common = new City("Common", new Coord(.5f, .4f));
        City chinatown = new City("Chinatown", new Coord(.68f, .42f));
        City financial_district =
                new City("Financial District", new Coord(.80f, .3f));
        City seaport = new City("Seaport", new Coord(.90f, .6f));
        City cambridge = new City("Cambridge", new Coord(.11f, .101f));

        DirectConnection d8 =
                new DirectConnection(brookline, cambridge, 5, ColorTrains.RED);
        DirectConnection d11 =
                new DirectConnection(brookline, common, 4, ColorTrains.BLUE);
        DirectConnection d2 =
                new DirectConnection(brookline, common, 3, ColorTrains.GREEN);

        DirectConnection d1 =
                new DirectConnection(cambridge, common, 3, ColorTrains.RED);
        DirectConnection d5 =
                new DirectConnection(cambridge, financial_district, 5,
                        ColorTrains.RED);
        DirectConnection d12 =
                new DirectConnection(cambridge, financial_district, 4,
                        ColorTrains.BLUE);

        DirectConnection d3 =
                new DirectConnection(common, chinatown, 3, ColorTrains.WHITE);
        DirectConnection d6 =
                new DirectConnection(common, financial_district, 4,
                        ColorTrains.WHITE);
        DirectConnection d4 =
                new DirectConnection(common, seaport, 4, ColorTrains.BLUE);

        DirectConnection d7 =
                new DirectConnection(financial_district, seaport, 4,
                        ColorTrains.WHITE);

        List<DirectConnection> ownedConnections = new ArrayList<>();
        ownedConnections.add(d5);
        ownedConnections.add(d2);

        List<List<City>> givenConnectedComponents = new ArrayList<>();
        givenConnectedComponents.add(
                new ArrayList<>(Arrays.asList(cambridge, financial_district)));

        assertEquals(5, Scoring.getLengthLongestPath(ownedConnections,
                givenConnectedComponents));
    }

    @Test
    public void testShouldGetLengthLongestPathWhenLong() {
        City brookline = new City("Brookline", new Coord(.1f, .5f));
        City common = new City("Common", new Coord(.5f, .4f));
        City chinatown = new City("Chinatown", new Coord(.68f, .42f));
        City financial_district =
                new City("Financial District", new Coord(.80f, .3f));
        City seaport = new City("Seaport", new Coord(.90f, .6f));
        City cambridge = new City("Cambridge", new Coord(.11f, .101f));

        DirectConnection d8 =
                new DirectConnection(brookline, cambridge, 5, ColorTrains.RED);
        DirectConnection d11 =
                new DirectConnection(brookline, common, 4, ColorTrains.BLUE);
        DirectConnection d2 =
                new DirectConnection(brookline, common, 3, ColorTrains.GREEN);

        DirectConnection d1 =
                new DirectConnection(cambridge, common, 3, ColorTrains.RED);
        DirectConnection d5 =
                new DirectConnection(cambridge, financial_district, 5,
                        ColorTrains.RED);
        DirectConnection d12 =
                new DirectConnection(cambridge, financial_district, 4,
                        ColorTrains.BLUE);

        DirectConnection d3 =
                new DirectConnection(common, chinatown, 3, ColorTrains.WHITE);
        DirectConnection d6 =
                new DirectConnection(common, financial_district, 4,
                        ColorTrains.WHITE);
        DirectConnection d4 =
                new DirectConnection(common, seaport, 4, ColorTrains.BLUE);

        DirectConnection d7 =
                new DirectConnection(financial_district, seaport, 4,
                        ColorTrains.WHITE);

        List<DirectConnection> ownedConnections = new ArrayList<>();
        ownedConnections.add(d11);
        ownedConnections.add(d8);
        ownedConnections.add(d5);
        ownedConnections.add(d7);

        List<List<City>> givenConnectedComponents = new ArrayList<>();
        givenConnectedComponents.add(new ArrayList<>(
                Arrays.asList(common, brookline, cambridge, financial_district,
                        seaport)));

        assertEquals(18, Scoring.getLengthLongestPath(ownedConnections,
                givenConnectedComponents));
    }

    @Test
    public void testShouldGetLengthLongestPathWhenHugeCycle() {
        City brookline = new City("Brookline", new Coord(.1f, .5f));
        City common = new City("Common", new Coord(.5f, .4f));
        City chinatown = new City("Chinatown", new Coord(.68f, .42f));
        City financial_district =
                new City("Financial District", new Coord(.80f, .3f));
        City seaport = new City("Seaport", new Coord(.90f, .6f));
        City cambridge = new City("Cambridge", new Coord(.11f, .101f));

        DirectConnection broCam =
                new DirectConnection(brookline, cambridge, 5, ColorTrains.RED);
        DirectConnection broCom =
                new DirectConnection(brookline, common, 4, ColorTrains.BLUE);
        DirectConnection d2 =
                new DirectConnection(brookline, common, 3, ColorTrains.GREEN);

        DirectConnection d1 =
                new DirectConnection(cambridge, common, 3, ColorTrains.RED);
        DirectConnection camFin =
                new DirectConnection(cambridge, financial_district, 5,
                        ColorTrains.RED);
        DirectConnection d12 =
                new DirectConnection(cambridge, financial_district, 4,
                        ColorTrains.BLUE);

        DirectConnection comChi =
                new DirectConnection(common, chinatown, 3, ColorTrains.WHITE);
        DirectConnection comFi =
                new DirectConnection(common, financial_district, 4,
                        ColorTrains.WHITE);
        DirectConnection comSea =
                new DirectConnection(common, seaport, 4, ColorTrains.BLUE);

        DirectConnection finSea =
                new DirectConnection(financial_district, seaport, 4,
                        ColorTrains.WHITE);

        List<DirectConnection> ownedConnections = new ArrayList<>();
        ownedConnections.add(broCom);
        ownedConnections.add(broCam);
        ownedConnections.add(camFin);
        ownedConnections.add(finSea);
        ownedConnections.add(comChi);
        ownedConnections.add(comFi);
        ownedConnections.add(comSea);

        List<List<City>> givenConnectedComponents = new ArrayList<>();
        givenConnectedComponents.add(new ArrayList<>(
                Arrays.asList(common, brookline, cambridge, financial_district,
                        seaport, chinatown)));

        assertEquals(17, Scoring.getLengthLongestPath(ownedConnections,
                givenConnectedComponents));
    }

    // TODO add unit tests for getPlayerScores
    @Test
    public void testShouldFindPlayerScoreofOne() {
        City brookline = new City("Brookline", new Coord(.1f, .5f));
        City common = new City("Common", new Coord(.5f, .4f));
        City chinatown = new City("Chinatown", new Coord(.68f, .42f));
        City financial_district =
                new City("Financial District", new Coord(.80f, .3f));
        City seaport = new City("Seaport", new Coord(.90f, .6f));
        City cambridge = new City("Cambridge", new Coord(.11f, .101f));

        HashMap<ColorTrains, Integer> cardsMap = new HashMap<>();
        cardsMap.put(ColorTrains.RED, 5);
        cardsMap.put(ColorTrains.GREEN, 0);
        cardsMap.put(ColorTrains.BLUE, 0);
        cardsMap.put(ColorTrains.WHITE, 0);

        List<Destination> destinations0 = new ArrayList<>();
        destinations0.add(new Destination(brookline, cambridge));
        destinations0.add(new Destination(financial_district, seaport));

        HashSet<DirectConnection> ownedConns0 = new HashSet<>();
        DirectConnection broCam =
                new DirectConnection(brookline, cambridge, 5, ColorTrains.RED);
        DirectConnection camFin =
                new DirectConnection(cambridge, financial_district, 5,
                        ColorTrains.RED);
        DirectConnection finSea =
                new DirectConnection(financial_district, seaport, 4,
                        ColorTrains.WHITE);

        ownedConns0.add(broCam);
        ownedConns0.add(camFin);
        ownedConns0.add(finSea);

        PlayerHand p0 = new PlayerHand(ownedConns0, cardsMap, 0, destinations0);
        List<PlayerHand> players = new ArrayList<>();
        players.add(p0);
        assertEquals(new HashSet<>(Arrays.asList(54)),
                Scoring.getRanking(players).keySet());
    }

    @Test
    public void testShouldFindPlayerScoreofTwo() {
        City brookline = new City("Brookline", new Coord(.1f, .5f));
        City common = new City("Common", new Coord(.5f, .4f));
        City chinatown = new City("Chinatown", new Coord(.68f, .42f));
        City financial_district =
                new City("Financial District", new Coord(.80f, .3f));
        City seaport = new City("Seaport", new Coord(.90f, .6f));
        City cambridge = new City("Cambridge", new Coord(.11f, .101f));

        HashMap<ColorTrains, Integer> cardsMap = new HashMap<>();
        cardsMap.put(ColorTrains.RED, 5);
        cardsMap.put(ColorTrains.GREEN, 0);
        cardsMap.put(ColorTrains.BLUE, 0);
        cardsMap.put(ColorTrains.WHITE, 0);

        List<Destination> destinations0 = new ArrayList<>();
        destinations0.add(new Destination(brookline, cambridge));
        destinations0.add(new Destination(financial_district, seaport));

        List<Destination> destinations1 = new ArrayList<>();
        destinations1.add(new Destination(brookline, cambridge));
        destinations1.add(new Destination(financial_district, seaport));

        HashSet<DirectConnection> ownedConns0 = new HashSet<>();
        DirectConnection broCam =
                new DirectConnection(brookline, cambridge, 5, ColorTrains.RED);
        DirectConnection camFin =
                new DirectConnection(cambridge, financial_district, 5,
                        ColorTrains.RED);
        DirectConnection finSea =
                new DirectConnection(financial_district, seaport, 4,
                        ColorTrains.WHITE);

        ownedConns0.add(broCam);
        ownedConns0.add(camFin);
        ownedConns0.add(finSea);

        HashSet<DirectConnection> ownedConns1 = new HashSet<>();

        PlayerHand p0 = new PlayerHand(ownedConns0, cardsMap, 0, destinations0);
        PlayerHand p1 = new PlayerHand(ownedConns1, cardsMap, 0, destinations1);

        List<PlayerHand> players = new ArrayList<>();
        players.add(p0);
        players.add(p1);
        assertEquals(new HashSet<>(Arrays.asList(54, -20)),
                Scoring.getRanking(players).keySet());
    }
}
