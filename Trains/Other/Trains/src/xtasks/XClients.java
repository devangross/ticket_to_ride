package xtasks;

import agent.ClientPlayer;
import agent.IPlayer;
import client.PlayerCommandExecutor;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import json.JsonConverter;
import map.TrainsMap;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.*;

public class XClients {

    public static void main(String[] args) throws IOException, InterruptedException {
        TrainsMap map;
        LinkedList<IPlayer> players;

        ObjectMapper objectMapper = new ObjectMapper()
                .configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false)
                .configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);

        try {
            JsonFactory factory = new JsonFactory();
            JsonParser parser;
            BufferedInputStream inputStream = new BufferedInputStream(System.in);
            parser = factory.createParser(inputStream);
            parser.setCodec(new ObjectMapper());
            ObjectReader objectReader = objectMapper.reader();
            parser.nextValue();
            map = JsonConverter.jsonToMap(parser.readValueAsTree());
            parser.nextValue();
            players = parsePlayerInstances(
                    objectReader.forType(ArrayList.class).readValue(parser),
                    map);
        } catch (IOException e) {
            throw new IllegalArgumentException("JSON input invalid: " + e);
        }

        if (args.length < 2) {
            throw new IllegalArgumentException("Expected port number");
        }
        final int port = Integer.parseInt(args[1]);
        final String host = args.length > 2 ? args[2] : "127.0.0.1";

        players.forEach(player -> {
            // Uncomment the following to ensure deterministic(ish) signup order.
            /*try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            new Thread(() -> {
                var runAgain = true;
                while (runAgain) {
                    runAgain = false;
                    // open a new socket for each player
                    try (Socket socket = new Socket(host, port)) {
                        new PlayerCommandExecutor(player, socket.getInputStream(),
                                socket.getOutputStream()).start();
                    } catch (ConnectException e) {
                        runAgain = true;
                        try {
                            Thread.sleep(1_000);
                        } catch (InterruptedException ignored) {
                            // don't worry about what happens here
                        }
                    } catch (JsonMappingException ignored) {
                        // cheaters will be stuck waiting for JSON when their
                        // sockets close, so we ignore those exceptions here
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
        });
    }

    /**
     * Helper method to parse a given list of Objects representing players in
     * format ["name", "strategy"] into a List of internal representation
     * IPlayers
     */
    static LinkedList<IPlayer> parsePlayerInstances(ArrayList<Object> players,
                                                    TrainsMap map) {
        LinkedList<IPlayer> iPlayers = new LinkedList<>();
        for (Object playerObj : players) {
            ArrayList<Object> single = (ArrayList<Object>) playerObj;
            String name = String.valueOf(single.get(0));
            String strategyString = String.valueOf(single.get(1));
            String strategyPath = stringStrategyToFilePath(strategyString);
            iPlayers.add(new ClientPlayer(name, strategyPath, map));
        }
        return iPlayers;
    }

    /**
     * Helper to generate the file path to one of the test input strategy
     * options: "Buy-Now" or "Hold-10" or "Cheat" which link to
     * BuyNowStrategy,
     * HoldTenStrategy, and CheaterStrategy respectively
     */
    private static String stringStrategyToFilePath(String stringStrategy) {
        Map<String, String> stringStratToPath = new HashMap<>();
        String currentDir = System.getProperty("user.dir");

        int folderLocation = currentDir.lastIndexOf('/');
        String pathToRepo = currentDir.substring(0, folderLocation);

        String pathToBuyNow = pathToRepo + "/Trains/Other/Trains/src/strategy" +
                              ".BuyNowStrategy";
        String pathToHold = pathToRepo + "/Trains/Other/Trains/src/strategy" +
                            ".HoldTenStrategy";
        String pathToCheat = pathToRepo + "/Trains/Other/Trains/src/strategy" +
                             ".CheaterStrategy";

        stringStratToPath.put("Buy-Now", pathToBuyNow);
        stringStratToPath.put("Hold-10", pathToHold);
        stringStratToPath.put("Cheat", pathToCheat);

        return stringStratToPath.get(stringStrategy);
    }
}
