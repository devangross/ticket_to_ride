package xtasks;

import agent.IPlayer;
import agent.PlayerAgent;
import agent.RefereeAgent;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import json.JsonConverter;
import map.ColorTrains;
import map.TrainsMap;
import state.ColorCard;
import strategy.OrderedDestSameCards;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class XRef {

    public static void main(String[] args) {
        TrainsMap map;
        LinkedList<IPlayer> players;
        List<ColorCard> cards;

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonFactory factory = new JsonFactory();
            JsonParser parser;

            BufferedInputStream inputStream = new BufferedInputStream(System.in);
            parser = factory.createParser(inputStream);
            parser.setCodec(new ObjectMapper());
            ObjectReader objectReader = objectMapper.reader();
            parser.nextToken();
            map = JsonConverter.jsonToMap(parser.readValueAsTree());
            parser.nextToken();
            players = XRef.parsePlayerInstances(
                    objectReader.forType(ArrayList.class).readValue(parser));
            parser.nextToken();
            cards = XRef.parseColorCards(
                    objectReader.forType(ArrayList.class).readValue(parser));
        } catch (IOException e) {
            throw new IllegalArgumentException("JSON input invalid: " + e);
        }

        try {
            RefereeAgent referee = new RefereeAgent(map, players, cards,
                    new OrderedDestSameCards());
            Map<Integer, Set<IPlayer>> gameRanking = referee.playGame();

            List<String> misbehaverNames = gameRanking
                    .get(null)
                    .stream()
                    .map(IPlayer::getName).sorted().collect(Collectors.toList());
            ArrayNode misbehaverArrayNode = objectMapper.createArrayNode();
            for (String name : misbehaverNames) {
                misbehaverArrayNode.add(name);
            }
            gameRanking.remove(null);

            Map<Integer, Set<IPlayer>> sortedMap = new TreeMap<>(gameRanking);
            ArrayNode arrayPlayerNamesAtScoreNode =
                    objectMapper.createArrayNode();

            List<Set<IPlayer>> descendingListPlayersAtScore =
                    new ArrayList<>(sortedMap.values());
            Collections.reverse(descendingListPlayersAtScore);
            for (Set<IPlayer> playersAtScore : descendingListPlayersAtScore) {
                List<String> playerNamesAtScore = new ArrayList<>();
                for (IPlayer p : playersAtScore) {
                    playerNamesAtScore.add(p.getName());
                }
                Collections.sort(playerNamesAtScore);
                ArrayNode playerNamesAtScoreNode =
                        objectMapper.createArrayNode();
                for (String name : playerNamesAtScore) {
                    playerNamesAtScoreNode.add(name);
                }

                arrayPlayerNamesAtScoreNode.add(playerNamesAtScoreNode);
            }

            ArrayNode outerOutputArray = objectMapper.createArrayNode();
            outerOutputArray.add(arrayPlayerNamesAtScoreNode);
            outerOutputArray.add(misbehaverArrayNode);

            System.out.println(
                    objectMapper.writeValueAsString(outerOutputArray));
        } catch (Exception e) {
            System.out.println("\"error: not enough destinations\"");
        }
    }

    static List<ColorCard> parseColorCards(ArrayList<Object> cardsObjects) {
        List<ColorCard> colorCards = new ArrayList<>();
        for (Object cardObj : cardsObjects) {
            ColorTrains colorTrains =
                    ColorTrains.valueOf(String.valueOf(cardObj).toUpperCase());
            colorCards.add(new ColorCard(colorTrains));
        }

        return colorCards;
    }

    /**
     * Helper method to parse a given list of Objects representing players in
     * format ["name", "strategy"] into a List of internal representation
     * IPlayers
     */
    static LinkedList<IPlayer> parsePlayerInstances(ArrayList<Object> players) {
        LinkedList<IPlayer> iPlayers = new LinkedList<>();
        for (Object playerObj : players) {
            ArrayList<Object> single = (ArrayList<Object>) playerObj;
            String name = String.valueOf(single.get(0));
            String strategyString = String.valueOf(single.get(1));
            String strategyPath = stringStrategyToFilePath(strategyString);
            iPlayers.add(new PlayerAgent(name, strategyPath));
        }
        return iPlayers;
    }

    /**
     * Helper to generate the file path to one of the test input strategy
     * options: "Buy-Now" or "Hold-10" or "Cheat" which link to BuyNowStrategy,
     * HoldTenStrategy, and CheaterStrategy respectively
     */
    private static String stringStrategyToFilePath(String stringStrategy) {
        Map<String, String> stringStratToPath = new HashMap<>();
        String currentDir = System.getProperty("user.dir");

        int folderLocation = currentDir.lastIndexOf('/');
        String pathToRepo = currentDir.substring(0, folderLocation);

        String pathToBuyNow =
                pathToRepo + "/Trains/Other/Trains/src/strategy.BuyNowStrategy";
        String pathToHold = pathToRepo +
                            "/Trains/Other/Trains/src/strategy.HoldTenStrategy";
        String pathToCheat = pathToRepo +
                             "/Trains/Other/Trains/src/strategy.CheaterStrategy";

        stringStratToPath.put("Buy-Now", pathToBuyNow);
        stringStratToPath.put("Hold-10", pathToHold);
        stringStratToPath.put("Cheat", pathToCheat);

        return stringStratToPath.get(stringStrategy);
    }
}
