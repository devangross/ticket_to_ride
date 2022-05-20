package agent;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import json.JsonConverter;
import map.ColorTrains;
import map.Destination;
import map.TrainsMap;
import state.ColorCard;
import state.PlayerGameState;
import strategy.Move;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A proxy tournament player that interacts with the manager and referee on the server
 * side. This player receives functions calls from the manager, serializes them to JSON
 * to be sent to players on the client side, receives their responses, and deserializes
 * the JSON responses to return to the caller.
 */
public class PlayerProxy implements IPlayer {

    public static final ExecutorService executor = Executors.newCachedThreadPool();

    private static final int TIMEOUT_MS = 2_000; // time to wait for player call & return

    private static final ObjectMapper mapper = new ObjectMapper();
    private final JsonParser jsonIn;
    private final JsonGenerator jsonOut;

    private final String name;
    private final LocalDateTime birthday;

    public PlayerProxy(InputStream jsonIn, OutputStream jsonOut, String name) {
        this.name = name;
        this.birthday = LocalDateTime.now();
        final JsonFactory factory = new JsonFactory(new ObjectMapper())
                .configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false)
                .configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
        try {
            this.jsonIn = factory.createParser(jsonIn);
            this.jsonOut = factory.createGenerator(jsonOut);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public PlayerProxy(JsonParser jsonIn, JsonGenerator jsonOut, String name) {
        this.jsonIn = jsonIn;
        this.jsonOut = jsonOut;
        this.name = name;
        if (!name.matches("^[a-zA-Z]{1,50}$")) {
            throw new IllegalArgumentException(
                    "Name must consist of at least one and at most 50 " +
                    "alphabetical ASCII chars.");
        }
        this.birthday = LocalDateTime.now();
    }

    public PlayerProxy(InputStream jsonIn, OutputStream jsonOut) {
        this(jsonIn, jsonOut, null);
    }

    @Override
    public void setup(TrainsMap map, int rails, List<ColorCard> cards) {
        this.doCall(() -> {
            this.sendFunctionCall("setup", JsonConverter.mapToJson(map), rails,
                    cards
                            .stream()
                            .map(ColorCard::getColor)
                            .map(ColorTrains::toString)
                            .collect(Collectors.toList()));
            return this.expectVoid();
        });
    }

    @Override
    public List<Destination> pick(List<Destination> destChoices) {
        return this.doCall(() -> {
            this.sendFunctionCall("pick", destChoices
                    .stream()
                    .map(JsonConverter::destinationToJson)
                    .collect(Collectors.toList()));
            return this.getResponse(JsonConverter::jsonToDestinations);
        });
    }

    @Override
    public Move play(PlayerGameState pgs) {
        return this.doCall(() -> {
            this.sendFunctionCall("play", JsonConverter.playerStateToJson(pgs));
            return this.getResponse(node -> JsonConverter.jsonToMove(
                    pgs.getTrainsMap().getCities(), node));
        });
    }

    @Override
    public void more(List<ColorCard> more) {
        this.doCall(() -> {
            this.sendFunctionCall("more", more
                    .stream()
                    .map(colorCard -> colorCard.getColor().name().toLowerCase())
                    .collect(Collectors.toList()));
            return this.expectVoid();
        });
    }

    @Override
    public void win(Boolean b) {
        this.doCall(() -> {
            this.sendFunctionCall("win", b);
            return this.expectVoid();
        });
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public LocalDateTime getBirthday() {
        return this.birthday;
    }

    @Override
    public TrainsMap start() {
        return this.doCall(() -> {
            this.sendFunctionCall("start", true);
            return this.getResponse(JsonConverter::jsonToMap);
        });
    }

    @Override
    public void end(boolean winner) {
        this.doCall(() -> {
            this.sendFunctionCall("end", winner);
            return this.expectVoid();
        });
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof IPlayer) {
            IPlayer other = (IPlayer) o;

            if (this.birthday.isBefore(other.getBirthday())) {
                return -1;
            } else if (this.birthday.isAfter(other.getBirthday())) {
                return 1;
            } else { // the birthdays are equal
                return 0;
            }
        } else {
            throw new ClassCastException(
                    "Cannot compare PlayerProxy to non IPlayer object");
        }
    }

    /**
     * Sends a call to a client player and receives its response. Times out after
     * two seconds.
     *
     * @param action the action to be executed (send a call, receive a response)
     * @param <T>    the return type of the action, that matches the return type
     *               of the call sent
     */
    private <T> T doCall(Supplier<T> action) {
        final AtomicBoolean timedOut = new AtomicBoolean(false);
        try {
            return executor.submit(() -> {
                try {
                    return action.get();
                } catch (Exception e) {
                    if (!timedOut.get()) {
                        throw e;
                    }
                    return null;
                }
            }).get(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            timedOut.set(true);
            throw new RuntimeException("Player call and return timed out");
        }
    }

    /**
     * Writes the name and parameters of the next method to be called on
     * a client player to the output stream
     *
     * @param command the name of the method to be called
     * @param args    the parameters of the method to be called
     */
    private void sendFunctionCall(String command, Object... args) {
        final ArrayNode node = PlayerProxy.mapper.createArrayNode();
        node.add(command);
        node.add(mapper.valueToTree(args));

        try {
            this.jsonOut.writeTree(node);
            this.jsonOut.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Ensures that the response from a method call was "void"
     */
    private Void expectVoid() {
        final String response = this.getResponse(JsonNode::asText);
        if (!response.equals("void")) {
            throw new IllegalArgumentException("Expected void in response");
        }
        return null;
    }

    /**
     * Uses the given function to convert the latest JSON response to its
     * correct internal data representation
     *
     * @param converter the function to deserialize the JSON response
     * @param <T>       the type of the deserialized response
     */
    private <T> T getResponse(Function<JsonNode, T> converter) {
        final JsonNode node;
        try {
            this.jsonIn.nextValue();
            node = this.jsonIn.readValueAs(JsonNode.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return converter.apply(node);
    }
}
