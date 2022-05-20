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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerHandTest {

    @Test
    public void testShouldThrowWhenInputColorCardsMissingColor() {
        HashSet<DirectConnection> ownedConnections = new HashSet<>();

        City LA = new City("LA", new Coord(.1f, .1f));
        City SF = new City("SF", new Coord(.9f, .1f));
        City SLO = new City("SLO", new Coord(.9f, .9f));
        Destination LAtoSF = new Destination(LA, SF);
        Destination LAtoSLO = new Destination(LA, SLO);
        List<Destination> picked =
                new ArrayList<>(Arrays.asList(LAtoSF, LAtoSLO));

        HashMap<ColorTrains, Integer> colorCards = new HashMap<>();
        colorCards.put(ColorTrains.GREEN, 10);
        colorCards.put(ColorTrains.BLUE, 10);
        colorCards.put(ColorTrains.WHITE, 10);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new PlayerHand(ownedConnections, colorCards, 45, picked));
    }

    @Test
    public void testShouldThrowWhenInputColorCardsNegative() {
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
        colorCards.put(ColorTrains.GREEN, -1);
        colorCards.put(ColorTrains.BLUE, 10);
        colorCards.put(ColorTrains.WHITE, 10);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new PlayerHand(ownedConnections, colorCards, 45, picked));
    }

    @Test
    public void testShouldGetRailsAfterRemoving() {
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
        PlayerHand newPH = pH.handleAddConnection(
                new DirectConnection(LA, SF, 5, ColorTrains.RED));
        assertEquals(40, newPH.getRails());
    }

    @Test
    public void testShouldUpdateAfterAcquiredConnection() {
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

        HashMap<ColorTrains, Integer> expectedCardsMap = new HashMap<>();
        expectedCardsMap.put(ColorTrains.RED, 5);
        expectedCardsMap.put(ColorTrains.GREEN, 10);
        expectedCardsMap.put(ColorTrains.BLUE, 10);
        expectedCardsMap.put(ColorTrains.WHITE, 10);
        PlayerHand pH =
                new PlayerHand(ownedConnections, colorCards, 45, picked);

        PlayerHand newPH = pH.handleAddConnection(
                new DirectConnection(LA, SF, 5, ColorTrains.RED));
        assertEquals(40, newPH.getRails());
        assertEquals(expectedCardsMap, newPH.getColorCardCount());
    }
}
