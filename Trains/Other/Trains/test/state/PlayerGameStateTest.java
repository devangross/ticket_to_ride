package state;

import com.fasterxml.jackson.core.JsonProcessingException;
import map.City;
import map.ColorTrains;
import map.Coord;
import map.Destination;
import map.DirectConnection;
import map.ExampleMap;
import map.TrainsMap;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PlayerGameStateTest {

    @Test
    public void testShouldConstructValidPGS() {
        TrainsMap map = ExampleMap.createSquareMap();
        HashSet<DirectConnection> ownedConnections = new HashSet<>();

        City LA = new City("LA", new Coord(.1f, .1f));
        City SF = new City("SF", new Coord(.9f, .1f));
        City SLO = new City("SLO", new Coord(.9f, .9f));
        Destination LAtoSF = new Destination(LA, SF);
        Destination LAtoSLO = new Destination(LA, SLO);
        List<Destination> picked =
                new ArrayList<>(Arrays.asList(LAtoSF, LAtoSLO));

        HashMap<ColorTrains, Integer> colorCards = new HashMap<>();
        colorCards.put(ColorTrains.RED, 10);
        colorCards.put(ColorTrains.GREEN, 10);
        colorCards.put(ColorTrains.BLUE, 10);
        colorCards.put(ColorTrains.WHITE, 10);
        PlayerHand pH =
                new PlayerHand(ownedConnections, colorCards, 45, picked);
        PlayerGameState player0 =
                new PlayerGameState(map, pH, new LinkedList<>());
        assertNotNull(player0);
    }

    @Test
    public void testgetOwnedConnections() {
        TrainsMap map = ExampleMap.createSquareMap();
        City LA = new City("LA", new Coord(.1f, .1f));
        City SF = new City("SF", new Coord(.9f, .1f));
        City SLO = new City("SLO", new Coord(.9f, .9f));
        City SAC = new City("Sac", new Coord(.1f, .9f));
        Destination LAtoSF = new Destination(LA, SF);
        Destination LAtoSLO = new Destination(LA, SLO);
        List<Destination> picked =
                new ArrayList<>(Arrays.asList(LAtoSF, LAtoSLO));

        DirectConnection d1 = new DirectConnection(LA, SLO, 3, ColorTrains.RED);
        DirectConnection d2 =
                new DirectConnection(SLO, SAC, 5, ColorTrains.GREEN);
        DirectConnection d3 =
                new DirectConnection(SLO, SF, 5, ColorTrains.WHITE);

        HashSet<DirectConnection> p0Owned = new HashSet<>();
        p0Owned.add(d1);
        p0Owned.add(d2);
        p0Owned.add(d3);

        HashMap<ColorTrains, Integer> colorCards = new HashMap<>();
        colorCards.put(ColorTrains.RED, 10);
        colorCards.put(ColorTrains.GREEN, 10);
        colorCards.put(ColorTrains.BLUE, 10);
        colorCards.put(ColorTrains.WHITE, 10);
        PlayerHand pH = new PlayerHand(p0Owned, colorCards, 45, picked);
        PlayerGameState player0 =
                new PlayerGameState(map, pH, new LinkedList<>());
        assertEquals(p0Owned, player0.getOwnedConnections());
    }

    @Test
    public void testShouldGetCardsMapCorrectly() {
        TrainsMap map = ExampleMap.createSquareMap();
        HashSet<DirectConnection> ownedConnections = new HashSet<>();

        City LA = new City("LA", new Coord(.1f, .1f));
        City SF = new City("SF", new Coord(.9f, .1f));
        City SLO = new City("SLO", new Coord(.9f, .9f));
        Destination LAtoSF = new Destination(LA, SF);
        Destination LAtoSLO = new Destination(LA, SLO);
        List<Destination> picked =
                new ArrayList<>(Arrays.asList(LAtoSF, LAtoSLO));

        HashMap<ColorTrains, Integer> colorCards = new HashMap<>();
        colorCards.put(ColorTrains.RED, 10);
        colorCards.put(ColorTrains.GREEN, 10);
        colorCards.put(ColorTrains.BLUE, 10);
        colorCards.put(ColorTrains.WHITE, 10);
        PlayerHand pH =
                new PlayerHand(ownedConnections, colorCards, 45, picked);
        PlayerGameState player0 =
                new PlayerGameState(map, pH, new LinkedList<>());
        assertEquals(colorCards, player0.getCardsMap());
    }

    @Test
    public void testShouldGetInitial45Rails() {
        TrainsMap map = ExampleMap.createSquareMap();
        HashSet<DirectConnection> ownedConnections = new HashSet<>();

        City LA = new City("LA", new Coord(.1f, .1f));
        City SF = new City("SF", new Coord(.9f, .1f));
        City SLO = new City("SLO", new Coord(.9f, .9f));
        Destination LAtoSF = new Destination(LA, SF);
        Destination LAtoSLO = new Destination(LA, SLO);
        List<Destination> picked =
                new ArrayList<>(Arrays.asList(LAtoSF, LAtoSLO));

        HashMap<ColorTrains, Integer> colorCards = new HashMap<>();
        colorCards.put(ColorTrains.RED, 10);
        colorCards.put(ColorTrains.GREEN, 10);
        colorCards.put(ColorTrains.BLUE, 10);
        colorCards.put(ColorTrains.WHITE, 10);
        PlayerHand pH =
                new PlayerHand(ownedConnections, colorCards, 45, picked);
        PlayerGameState player0 =
                new PlayerGameState(map, pH, new LinkedList<>());
        assertEquals(player0.getRails(), 45);
    }

    @Test
    public void testShouldGetD1AndD2Correctly() {
        TrainsMap map = ExampleMap.createSquareMap();
        HashSet<DirectConnection> ownedConnections = new HashSet<>();

        City LA = new City("LA", new Coord(.1f, .1f));
        City SF = new City("SF", new Coord(.9f, .1f));
        City SLO = new City("SLO", new Coord(.9f, .9f));
        Destination LAtoSF = new Destination(LA, SF);
        Destination LAtoSLO = new Destination(LA, SLO);
        List<Destination> picked =
                new ArrayList<>(Arrays.asList(LAtoSF, LAtoSLO));

        HashMap<ColorTrains, Integer> colorCards = new HashMap<>();
        colorCards.put(ColorTrains.RED, 10);
        colorCards.put(ColorTrains.GREEN, 10);
        colorCards.put(ColorTrains.BLUE, 10);
        colorCards.put(ColorTrains.WHITE, 10);
        PlayerHand pH =
                new PlayerHand(ownedConnections, colorCards, 45, picked);
        PlayerGameState player0 =
                new PlayerGameState(map, pH, new LinkedList<>());
        assertEquals(player0.getDestinations().get(0), LAtoSF);
        assertEquals(player0.getDestinations().get(1), LAtoSLO);
    }

    @Test
    public void testShouldDetermineAvailableConnections() {
        TrainsMap map = ExampleMap.createSimpleDoubleConnectionMap();

        City LA = new City("LA", new Coord(.8f, .4f));
        City SF = new City("SF", new Coord(.3f, .8f));
        Destination LAtoSF = new Destination(LA, SF);
        Destination SFtoLA = new Destination(SF, LA);
        List<Destination> picked =
                new ArrayList<>(Arrays.asList(LAtoSF, SFtoLA));

        HashMap<ColorTrains, Integer> colorCards = new HashMap<>();
        colorCards.put(ColorTrains.RED, 10);
        colorCards.put(ColorTrains.GREEN, 10);
        colorCards.put(ColorTrains.BLUE, 10);
        colorCards.put(ColorTrains.WHITE, 10);

        DirectConnection d1 =
                new DirectConnection(LA, SF, 3, ColorTrains.GREEN);
        DirectConnection d2 = new DirectConnection(SF, LA, 5, ColorTrains.RED);
        DirectConnection d4 = new DirectConnection(SF, LA, 4, ColorTrains.RED);
        HashSet<DirectConnection> expectedAvailableConnections =
                new HashSet<>();
        expectedAvailableConnections.add(d4);
        expectedAvailableConnections.add(d1);
        expectedAvailableConnections.add(d2);

        PlayerHand pH = new PlayerHand(new HashSet<>(), colorCards, 45, picked);
        PlayerGameState player0 =
                new PlayerGameState(map, pH, new LinkedList<>());
        assertEquals(expectedAvailableConnections,
                player0.determineAvailableConnections());
    }

    @Test
    public void testShouldDetermineAvailableConnectionsWithDifferentPlayersOwningMultipleConnections() {
        TrainsMap map = ExampleMap.createSimpleDoubleConnectionMap();

        City LA = new City("LA", new Coord(.8f, .4f));
        City SF = new City("SF", new Coord(.3f, .8f));
        Destination LAtoSF = new Destination(LA, SF);
        Destination SFtoLA = new Destination(SF, LA);
        List<Destination> picked =
                new ArrayList<>(Arrays.asList(LAtoSF, SFtoLA));

        HashMap<ColorTrains, Integer> colorCards = new HashMap<>();
        colorCards.put(ColorTrains.RED, 10);
        colorCards.put(ColorTrains.GREEN, 10);
        colorCards.put(ColorTrains.BLUE, 10);
        colorCards.put(ColorTrains.WHITE, 10);

        DirectConnection d1 =
                new DirectConnection(LA, SF, 3, ColorTrains.GREEN);
        DirectConnection d2 = new DirectConnection(SF, LA, 5, ColorTrains.RED);
        DirectConnection d4 = new DirectConnection(SF, LA, 4, ColorTrains.RED);
        HashSet<DirectConnection> expectedAvailableConnections =
                new HashSet<>();
        expectedAvailableConnections.add(d4);

        LinkedList<HashSet<DirectConnection>> ownedConnections =
                new LinkedList<>();
        HashSet<DirectConnection> owned1 = new HashSet<>();
        HashSet<DirectConnection> owned2 = new HashSet<>();
        owned1.add(d1);
        owned2.add(d2);
        ownedConnections.add(owned1);
        ownedConnections.add(owned2);

        PlayerHand pH = new PlayerHand(owned1, colorCards, 45, picked);
        PlayerGameState player0 =
                new PlayerGameState(map, pH, ownedConnections);
        assertEquals(expectedAvailableConnections,
                player0.determineAvailableConnections());
    }

    @Test
    public void testShouldGetNewPGSWithSameMap() {
        TrainsMap map = ExampleMap.createExampleMultipleConnectionMap();

        City LA = new City("LA", new Coord(.8f, .4f));
        City SF = new City("SF", new Coord(.3f, .8f));
        Destination LAtoSF = new Destination(LA, SF);
        Destination SFtoLA = new Destination(SF, LA);
        List<Destination> picked =
                new ArrayList<>(Arrays.asList(LAtoSF, SFtoLA));

        HashMap<ColorTrains, Integer> colorCards = new HashMap<>();
        colorCards.put(ColorTrains.RED, 10);
        colorCards.put(ColorTrains.GREEN, 10);
        colorCards.put(ColorTrains.BLUE, 10);
        colorCards.put(ColorTrains.WHITE, 10);

        DirectConnection d1 =
                new DirectConnection(LA, SF, 3, ColorTrains.GREEN);
        DirectConnection d2 = new DirectConnection(SF, LA, 5, ColorTrains.RED);
        DirectConnection d4 = new DirectConnection(SF, LA, 4, ColorTrains.RED);

        LinkedList<HashSet<DirectConnection>> ownedConnections =
                new LinkedList<>();
        HashSet<DirectConnection> owned1 = new HashSet<>();
        HashSet<DirectConnection> owned2 = new HashSet<>();
        owned1.add(d1);
        owned2.add(d2);
        ownedConnections.add(owned1);
        ownedConnections.add(owned2);

        RefereeGameState rgs =
                RefereeGameState.RefereeGameStateShuffledCards(map);
        rgs.addPlayer();
        PlayerGameState current = rgs.getCurrentPlayerGameState();

        owned1.add(d4);

        HashSet<DirectConnection> expectedOwned = new HashSet<>();
        expectedOwned.add(d1);
        expectedOwned.add(d2);
        expectedOwned.add(d4);

        PlayerHand pH = new PlayerHand(expectedOwned, colorCards, 45, picked);

        LinkedList<HashSet<DirectConnection>> updateOwned = new LinkedList<>();
        updateOwned.add(expectedOwned);
        PlayerGameState updated = current.newPGSDefaultMap(pH, updateOwned);

        assertEquals(expectedOwned, updated.getOwnedConnections());
    }

}
