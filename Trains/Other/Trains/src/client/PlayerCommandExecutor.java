package client;

import agent.IPlayer;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import json.JsonConverter;
import map.TrainsMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Receives commands from a server, interprets them, and calls methods on a
 * player to play a game.
 */
public final class PlayerCommandExecutor {

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private static final JsonFactory JSON_FACTORY = new JsonFactory(JSON_MAPPER)
            .configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false)
            .configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);

    private final IPlayer player;
    private final JsonGenerator out;
    private final InputStream is;
    private boolean done;
    private TrainsMap map;
    private final Map<String, Function<List<JsonNode>, JsonNode>> commandMap =
            Map.of("start", this::onStart,
                    "setup", this::onSetup,
                    "pick", this::onPick,
                    "play", this::onPlay,
                    "more", this::onMore,
                    "win", this::onWin,
                    "end", this::onEnd);

    public PlayerCommandExecutor(IPlayer player, InputStream in,
                                 OutputStream out) throws IOException {
        this.player = player;
        this.is = in;
        this.out = JSON_FACTORY.createGenerator(out);
    }

    private static JsonNode createVoidNode() {
        try {
            return JSON_MAPPER.readTree("\"void\"");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Starts a command executor and blocks until it receives an 'end' command.
     */
    public void start() throws IOException {
        this.out.writeString(this.player.getName());
        this.out.flush();
        final JsonParser in = JSON_FACTORY.createParser(this.is);
        while (!this.done) {
            in.nextValue();
            this.execute(in.readValueAs(JsonNode.class));
        }
    }

    /**
     * Executes the appropriate method on the player given a command JSON node.
     */
    private void execute(JsonNode node) throws IOException {
        final String command = node.get(0).asText();
        final List<JsonNode> args = new ArrayList<>();
        node.get(1).elements().forEachRemaining(args::add);
        final JsonNode response = this.commandMap.get(command).apply(args);
        this.out.writeTree(response);
        this.out.flush();
    }

    private JsonNode onStart(List<JsonNode> args) {
        return JsonConverter.mapToJson(this.player.start());
    }

    private JsonNode onSetup(List<JsonNode> args) {
        this.map = JsonConverter.jsonToMap(args.get(0));
        this.player.setup(this.map, args.get(1).asInt(),
                JsonConverter.jsonToCards(args.get(2)));
        return createVoidNode();
    }

    private JsonNode onPick(List<JsonNode> args) {
        return JsonConverter.destinationsToJson(this.player.pick(
                JsonConverter.jsonToDestinations(args.get(0))));
    }

    private JsonNode onPlay(List<JsonNode> args) {
        return JsonConverter.moveToJson(this.player.play(
                JsonConverter.jsonToPlayerState(this.map, args.get(0))));
    }

    private JsonNode onMore(List<JsonNode> args) {
        this.player.more(JsonConverter.jsonToCards(args.get(0)));
        return createVoidNode();
    }

    private JsonNode onWin(List<JsonNode> args) {
        this.player.win(args.get(0).asBoolean());
        return createVoidNode();
    }

    private JsonNode onEnd(List<JsonNode> args) {
        this.player.end(args.get(0).asBoolean());
        this.done = true;
        return createVoidNode();
    }
}
