package xtasks;

import agent.IPlayer;
import agent.Manager;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import json.JsonConverter;
import map.TrainsMap;
import state.ColorCard;
import strategy.OrderedDestSameCards;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class XManager {

    public static void main(String[] args) {
        TrainsMap map;
        LinkedList<IPlayer> players;
        List<ColorCard> cards;

        try {
            JsonFactory factory = new JsonFactory();
            JsonParser parser;
            ObjectMapper objectMapper = new ObjectMapper();
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

        Manager manager = new Manager(players, cards, new OrderedDestSameCards(), map);
        XManager.runTournamentAndDisplayResults(manager);
    }

    /**
     * Tries to run a tournament with the given manager and displays either the results of the
     * tournament or the string "error: not enough destinations"
     *
     * @param manager      the Manager to run the tournament with
     * //@param objectMapper the ObjectMapper used to serialize results into JSON
     */
    public static void runTournamentAndDisplayResults(Manager manager) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Set<IPlayer>> tournamentResults = manager.playTournament();

            List<String> winnersNames = tournamentResults
                    .get("winners")
                    .stream()
                    .map(IPlayer::getName).sorted().collect(Collectors.toList());
            List<String> misbehaverNames = tournamentResults
                    .get("misbehavers")
                    .stream()
                    .map(IPlayer::getName).sorted().collect(Collectors.toList());

            ArrayNode winnersArrayNode = objectMapper.createArrayNode();
            for (String name : winnersNames) {
                winnersArrayNode.add(name);
            }
            ArrayNode misbehaverArrayNode = objectMapper.createArrayNode();
            for (String name : misbehaverNames) {
                misbehaverArrayNode.add(name);
            }

            ArrayNode outerOutputArray = objectMapper.createArrayNode();
            outerOutputArray.add(winnersArrayNode);
            outerOutputArray.add(misbehaverArrayNode);
            System.out.println(
                    objectMapper.writeValueAsString(outerOutputArray));
        } catch (Exception e) {
            System.out.println("\"error: not enough destinations\"");
        }
    }
}
