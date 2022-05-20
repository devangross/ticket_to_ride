package json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import map.City;
import map.ColorTrains;
import map.Coord;
import map.Destination;
import map.DirectConnection;
import map.TrainsMap;
import state.ColorCard;
import state.PlayerGameState;
import state.PlayerHand;
import strategy.Move;
import strategy.Pair;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility methods for converting to and from JSON.
 */
public final class JsonConverter {

    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Converts JSON to a deck of cards.
     */
    public static List<ColorCard> jsonToCards(JsonNode node) {
        List<ColorCard> cards = new ArrayList<>();
        node.elements().forEachRemaining(card -> {
            cards.add(new ColorCard(
                    ColorTrains.valueOf(card.asText().toUpperCase())));
        });
        return cards;
    }

    /**
     * Converts JSON to a move object.
     */
    public static Move jsonToMove(Collection<City> cities, JsonNode node) {
        final Move move = new Move();
        if (node.isTextual() && node.asText().equals("more cards")) {
            move.setMove(true);
            return move;
        }
        if (node.isArray()) {
            final String city0Name = node.get(0).asText();
            final String city1Name = node.get(1).asText();
            final ColorTrains color =
                    ColorTrains.valueOf(node.get(2).asText().toUpperCase());
            final int length = node.get(3).asInt();
            final City city0 = findCityByName(cities, city0Name);
            final City city1 = findCityByName(cities, city1Name);
            move.setMove(new DirectConnection(city0, city1, length, color));
            return move;
        }
        throw new IllegalArgumentException("Unknown action: " + node);
    }

    /**
     * Converts JSON to a player game state.
     */
    public static PlayerGameState jsonToPlayerState(TrainsMap map,
                                                    JsonNode node) {
        JsonNode destination1Node = node.get("this").get("destination1");
        JsonNode destination2Node = node.get("this").get("destination2");
        Destination destination1 = new Destination(findCityByName(map.getCities(),
                destination1Node.get(0).asText()),
                findCityByName(map.getCities(),
                        destination1Node.get(1).asText()));
        Destination destination2 = new Destination(findCityByName(map.getCities(),
                destination2Node.get(0).asText()),
                findCityByName(map.getCities(),
                        destination2Node.get(1).asText()));
        int rails = node.get("this").get("rails").asInt();
        Map<ColorTrains, Integer> cards = new HashMap<>();
        node.get("this").get("cards").fields().forEachRemaining(field -> {
            cards.put(ColorTrains.valueOf(field.getKey().toUpperCase()),
                    Math.abs(BigInteger.valueOf(field.getValue().asLong()).intValue()));
        });
        Set<DirectConnection> thisAcquired = new HashSet<>();
        node.get("this").get("acquired").elements().forEachRemaining(conn -> {
            thisAcquired.add(new DirectConnection(
                    findCityByName(map.getCities(), conn.get(0).asText()),
                    findCityByName(map.getCities(), conn.get(1).asText()),
                    conn.get(3).asInt(),
                    ColorTrains.valueOf(conn.get(2).asText().toUpperCase())));
        });
        LinkedList<HashSet<DirectConnection>> acquired = new LinkedList<>();
        node.get("acquired").elements().forEachRemaining(player -> {
            HashSet<DirectConnection> playerAcquired = new HashSet<>();
            player.elements().forEachRemaining(conn -> {
                playerAcquired.add(new DirectConnection(
                        findCityByName(map.getCities(), conn.get(0).asText()),
                        findCityByName(map.getCities(), conn.get(1).asText()),
                        conn.get(3).asInt(), ColorTrains.valueOf(
                        conn.get(2).asText().toUpperCase())));
            });
            acquired.add(playerAcquired);
        });
        return new PlayerGameState(map,
                new PlayerHand(thisAcquired, cards, rails,
                        List.of(destination1, destination2)), acquired);
    }

    /**
     * Converts a player game state to JSON.
     */
    public static JsonNode playerStateToJson(PlayerGameState state) {
        ObjectNode node = mapper.createObjectNode();
        ObjectNode thisObj = mapper.createObjectNode();
        List<List<String>> destinations = state
                .getDestinations()
                .stream()
                .map(JsonConverter::toSortedDestination)
                .sorted()
                .map(destination -> {
                    final Pair<City> verts = destination.getVertices();
                    return List.of(verts.getFirst().getName(),
                            verts.getSecond().getName());
                })
                .collect(Collectors.toList());
        thisObj.set("destination1", mapper.valueToTree(destinations.get(0)));
        thisObj.set("destination2", mapper.valueToTree(destinations.get(1)));
        thisObj.put("rails", state.getRails());
        thisObj.set("cards", mapper.valueToTree(state
                .getCardsMap()
                .entrySet()
                .stream()
                .collect(
                        Collectors.<Map.Entry<ColorTrains, Integer>, String,
                                Integer>toMap(
                                e -> e.getKey().name().toLowerCase(),
                                Map.Entry::getValue))));
        thisObj.set("acquired", createPlayer(state.getOwnedConnections()));
        node.set("this", thisObj);
        ArrayNode acquired = mapper.createArrayNode();
        state
                .getAllOwnedConnections()
                .forEach(conns -> acquired.add(createPlayer(conns)));
        node.set("acquired", acquired);
        return node;
    }

    /**
     * Creates a Player given their connections.
     */
    private static JsonNode createPlayer(Collection<DirectConnection> conns) {
        ArrayNode player = mapper.createArrayNode();
        conns.forEach(conn -> {
            conn = toSortedConnection(conn);
            player.add(mapper
                    .createArrayNode()
                    .add(conn.getCity0().getName())
                    .add(conn.getCity1().getName())
                    .add(conn.getColor().name().toLowerCase())
                    .add(conn.getLength()));
        });
        return player;
    }

    /**
     * Converts JSON to a list of destinations.
     */
    public static List<Destination> jsonToDestinations(JsonNode node) {
        List<Destination> destinations = new ArrayList<>();
        node.elements().forEachRemaining(destination -> {
            String city0Name = destination.get(0).asText();
            String city1Name = destination.get(1).asText();
            destinations.add(
                    new Destination(new City(city0Name, new Coord(0f, 0f)),
                            new City(city1Name, new Coord(0f, 0f))));
        });
        return destinations;
    }

    /**
     * Converts a list of destinations to JSON.
     */
    public static JsonNode destinationsToJson(List<Destination> dest) {
        ArrayNode dests = mapper.createArrayNode();
        dest.forEach(d -> dests.add(destinationToJson(d)));
        return dests;
    }

    /**
     * Converts a move to JSON.
     */
    public static JsonNode moveToJson(Move move) {
        if (move.getMove().equals(true)) {
            try {
                return mapper.readTree("\"more cards\"");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            DirectConnection conn = (DirectConnection) move.getMove();
            String city0 = conn.getCity0().getName();
            String city1 = conn.getCity1().getName();
            List<String> cities = new ArrayList<>(List.of(city0, city1));
            Collections.sort(cities);
            return mapper
                    .createArrayNode()
                    .add(cities.get(0))
                    .add(cities.get(1))
                    .add(conn.getColor().name().toLowerCase())
                    .add(conn.getLength());
        }
    }

    /**
     * Converts a destination to JSON.
     */
    public static JsonNode destinationToJson(Destination dest) {
        Pair<City> cities = dest.getVertices();
        String city0Name = cities.getFirst().getName();
        String city1Name = cities.getSecond().getName();
        ArrayNode arr = mapper.createArrayNode();

        if (city0Name.compareTo(city1Name) < 0) {
            return arr.add(city0Name).add(city1Name);
        }
        return arr.add(city1Name).add(city0Name);
    }

    /**
     * Converts JSON to a Trains game map.
     */
    public static TrainsMap jsonToMap(JsonNode node) {
        int width = node.get("width").asInt();
        int height = node.get("height").asInt();
        Set<City> cities = new HashSet<>();
        node.get("cities").elements().forEachRemaining(city -> {
            String name = city.get(0).asText();
            JsonNode posn = city.get(1);
            float x = (float) posn.get(0).asDouble();
            float y = (float) posn.get(1).asDouble();
            float cityX = x > 1 ? x / width : x; // normalize x if necessary
            float cityY = y > 1 ? y / height : y; // normalize y if necessary
            cities.add(new City(name, new Coord(cityX, cityY)));
        });
        Set<DirectConnection> connections = new HashSet<>();
        node.get("connections").fields().forEachRemaining((connsField) -> {
            String name0 = connsField.getKey();
            connsField.getValue().fields().forEachRemaining((targetField) -> {
                String name1 = targetField.getKey();
                targetField
                        .getValue()
                        .fields()
                        .forEachRemaining((segmentField) -> {
                            String color = segmentField.getKey();
                            int length = segmentField.getValue().asInt();
                            City city0 = findCityByName(cities, name0);
                            City city1 = findCityByName(cities, name1);
                            connections.add(
                                    new DirectConnection(city0, city1, length,
                                            ColorTrains.valueOf(
                                                    color.toUpperCase())));
                        });
            });
        });
        return new TrainsMap(cities, connections, width, height);
    }

    /**
     * Finds a city by name in the given collection of cities.
     */
    public static City findCityByName(Collection<City> cities, String search) {
        return cities
                .stream()
                .filter(city -> city.getName().equals(search))
                .findFirst()
                .orElseThrow();
    }

    /**
     * Converts a Trains game map to JSON.
     */
    public static JsonNode mapToJson(TrainsMap map) {
        ObjectNode node = mapper.createObjectNode();
        node.put("width", map.getWidth());
        node.put("height", map.getHeight());
        List<ArrayNode> cities = map.getCities().stream().sorted().map(city -> {
            ArrayNode arr = mapper.createArrayNode();
            arr.add(city.getName());
            Coord loc = city.getLocation();
            arr.add(mapper.createArrayNode().add(loc.getX()).add(loc.getY()));
            return arr;
        }).collect(Collectors.toList());
        node.set("cities", mapper.valueToTree(cities));
        ObjectNode conns = mapper.createObjectNode();
        map
                .getDirectConnections()
                .forEach((conn) -> mergeConnections(conns,
                        toSortedConnection(conn)));
        node.set("connections", conns);
        return node;
    }

    /**
     * Merges a direction connection into the existing connections object.
     */
    private static void mergeConnections(ObjectNode connections,
                                         DirectConnection conn) {
        City city0 = conn.getCity0();
        if (connections.has(city0.getName())) {
            mergeTargets((ObjectNode) connections.get(city0.getName()), conn);
        } else {
            connections.set(city0.getName(),
                    mergeTargets(mapper.createObjectNode(), conn));
        }
    }

    /**
     * Merges the given direct connection into the existing target object.
     */
    private static ObjectNode mergeTargets(ObjectNode target,
                                           DirectConnection conn) {
        City city1 = conn.getCity1();
        if (target.has(city1.getName())) {
            mergeSegments((ObjectNode) target.get(city1.getName()), conn);
        } else {
            target.set(city1.getName(),
                    mergeSegments(mapper.createObjectNode(), conn));
        }
        return target;
    }

    /**
     * Merges the given direct connection into an existing segment object.
     */
    private static ObjectNode mergeSegments(ObjectNode segment,
                                            DirectConnection conn) {
        segment.put(conn.getColor().name().toLowerCase(), conn.getLength());
        return segment;
    }

    /**
     * Converts a destination into its sorted equivalent. That is, the same
     * destination but with city0 and city1 in lexicographic order.
     */
    private static Destination toSortedDestination(Destination destination) {
        City city0 = destination.getVertices().getFirst();
        City city1 = destination.getVertices().getSecond();

        if (city0.compareTo(city1) < 0) {
            return destination;
        }
        return new Destination(city1, city0);
    }

    /**
     * Converts a connection into its sorted equivalent. That is, the same
     * connection but with city0 and city1 in lexicographic order.
     */
    private static DirectConnection toSortedConnection(DirectConnection conn) {
        City city0 = conn.getCity0();
        City city1 = conn.getCity1();

        if (city0.compareTo(city1) < 0) {
            return conn;
        }
        return new DirectConnection(city1, city0, conn.getLength(), conn.getColor());
    }
}
