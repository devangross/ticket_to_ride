package agent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import map.City;
import map.ColorTrains;
import map.Coord;
import map.Destination;
import map.DirectConnection;
import map.TrainsMap;
import org.junit.jupiter.api.Test;
import state.ColorCard;
import state.PlayerGameState;
import state.PlayerHand;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PlayerProxyTest {

    private ObjectMapper mapper;

    @Test
    public void testEndTrue() {
        var in = createIn("void");
        var out = createOut();
        new PlayerProxy(in, out).end(true);
        assertEquals("[\"end\",[true]]", out.toString());
    }

    @Test
    public void testEndNoVoid() {
        var in = createIn("voi");
        var out = createOut();
        assertThrows(RuntimeException.class, () -> {
            new PlayerProxy(in, out).end(true);
        });
    }

    @Test
    public void testEndFalse() {
        var in = createIn("void");
        var out = createOut();
        new PlayerProxy(in, out).end(false);
        assertEquals("[\"end\",[false]]", out.toString());
    }

    @Test
    public void testSetup() throws IOException {
        this.mapper = new ObjectMapper();
        var in = createIn("void");
        var out = createOut();
        new PlayerProxy(in, out).setup(createSmallTrainsMap(), 5,
                List.of(new ColorCard(ColorTrains.BLUE),
                        new ColorCard(ColorTrains.WHITE)));
        assertEquals(createFunctionCall("setup",
                        Map.of("width", 500, "height", 600, "cities",
                                List.of(List.of("boston", List.of(0.4f, 0.6f)),
                                        List.of("chicago", List.of(0.2f,
                                                0.8f))),
                                "connections",
                                Map.of("boston", Map.of("chicago", Map.of(
                                        "blue", 5)))),
                        5, List.of("blue", "white")),
                mapper.readTree(out.toString()));
    }

    @Test
    public void testSetupNoVoid() {
        var in = createIn("voi");
        var out = createOut();
        assertThrows(RuntimeException.class,
                () -> new PlayerProxy(in, out).setup(createSmallTrainsMap(), 5,
                        List.of(new ColorCard(ColorTrains.BLUE),
                                new ColorCard(ColorTrains.WHITE))));
    }

    @Test
    public void testMore() {
        var in = createIn("void");
        var out = createOut();
        new PlayerProxy(in, out).more(List.of(new ColorCard(ColorTrains.GREEN),
                new ColorCard(ColorTrains.RED)));
        assertEquals("[\"more\",[[\"green\",\"red\"]]]", out.toString());
    }

    @Test
    public void testMoreNoVoid() {
        var in = createIn("voi");
        var out = createOut();
        assertThrows(RuntimeException.class,
                () -> new PlayerProxy(in, out).more(
                        List.of(new ColorCard(ColorTrains.GREEN),
                                new ColorCard(ColorTrains.RED))));
    }

    @Test
    public void testWinTrue() {
        var in = createIn("void");
        var out = createOut();
        new PlayerProxy(in, out).win(true);
        assertEquals("[\"win\",[true]]", out.toString());
    }

    @Test
    public void testWinFalse() {
        var in = createIn("void");
        var out = createOut();
        new PlayerProxy(in, out).win(false);
        assertEquals("[\"win\",[false]]", out.toString());
    }

    @Test
    public void testWinNoVoid() {
        var in = createIn("voi");
        var out = createOut();
        assertThrows(RuntimeException.class,
                () -> new PlayerProxy(in, out).win(false));
    }

    @Test
    public void testPick() {
        var destinations =
                List.of(new Destination(new City("bos", new Coord(0f, 0f)),
                        new City("phl", new Coord(0f, 0f))));
        var in = createIn(List.of(List.of("bos", "phl")));
        var out = createOut();
        var result = new PlayerProxy(in, out).pick(destinations);
        assertEquals("[\"pick\",[[[\"bos\",\"phl\"]]]]", out.toString());
        assertEquals(destinations, result);
    }

    @Test
    public void testPickBadInput() {
        var destinations =
                List.of(new Destination(new City("bos", new Coord(0f, 0f)),
                        new City("phl", new Coord(0f, 0f))));
        var in = createIn(List.of("bos", "phl"));
        var out = createOut();
        assertThrows(RuntimeException.class,
                () -> new PlayerProxy(in, out).pick(destinations));
    }

    @Test
    public void testStart() {
        var in = createIn(Map.of("width", 500, "height", 600, "cities",
                List.of(List.of("boston", List.of(0.4f, 0.6f)),
                        List.of("chicago", List.of(0.2f, 0.8f))), "connections",
                Map.of("boston", Map.of("chicago", Map.of("blue", 5)))));
        var out = createOut();
        var result = new PlayerProxy(in, out).start();
        assertEquals(createSmallTrainsMap(), result);
    }

    @Test
    public void testStartBadInput() {
        var in = createIn(Map.of("height", 600, "cities",
                List.of(List.of("boston", List.of(0.4f, 0.6f)),
                        List.of("chicago", List.of(0.2f, 0.8f))), "connections",
                Map.of("boston", Map.of("chicago", Map.of("blue", 5)))));
        var out = createOut();
        assertThrows(RuntimeException.class,
                () -> new PlayerProxy(in, out).start());
    }

    @Test
    public void testPlayMoreCards() throws IOException {
        this.mapper = new ObjectMapper();
        var bos = new City("boston", new Coord(0.4f, 0.6f));
        var chi = new City("chicago", new Coord(0.2f, 0.8f));
        var mia = new City("miami", new Coord(0.2f, 0.8f));
        var bos2Chi = new DirectConnection(bos, chi, 5, ColorTrains.BLUE);
        var chi2Mia = new DirectConnection(chi, mia, 3, ColorTrains.WHITE);

        var hand = new PlayerHand(Set.of(bos2Chi),
                // TODO: shouldn't be forced to provide all 4 color keys
                Map.of(ColorTrains.GREEN, 4, ColorTrains.BLUE, 3,
                        ColorTrains.RED, 0, ColorTrains.WHITE, 0), 3,
                List.of(new Destination(bos, chi), new Destination(bos, mia)));

        var gs = new PlayerGameState(this.createSmallTrainsMap(), hand,
                new LinkedList<>(List.of(new HashSet<>(Set.of(chi2Mia)))));
        var in = createIn("more cards");
        var out = createOut();
        var result = new PlayerProxy(in, out).play(gs);
        assertEquals(createFunctionCall("play", Map.of("this",
                        Map.of("destination1", List.of("boston", "chicago"),
                                "destination2", List.of("boston", "miami"),
                                "rails", 3,
                                "cards",
                                Map.of("green", 4, "blue", 3, "red", 0,
                                        "white", 0),
                                "acquired",
                                List.of(List.of("boston", "chicago", "blue",
                                        5))),
                        "acquired",
                        List.of(List.of(List.of("chicago", "miami", "white",
                                3))))),
                mapper.readTree(out.toString()));
        assertEquals(true, result.getMove());
    }

    @Test
    public void testPlayAcquireConnection() throws IOException {
        this.mapper = new ObjectMapper();
        var bos = new City("boston", new Coord(0.4f, 0.6f));
        var chi = new City("chicago", new Coord(0.2f, 0.8f));
        var mia = new City("miami", new Coord(0.2f, 0.8f));
        var bos2Chi = new DirectConnection(bos, chi, 5, ColorTrains.BLUE);
        var chi2Mia = new DirectConnection(chi, mia, 3, ColorTrains.WHITE);

        var hand = new PlayerHand(Set.of(bos2Chi),
                // TODO: shouldn't be forced to provide all 4 color keys
                Map.of(ColorTrains.GREEN, 4, ColorTrains.BLUE, 3,
                        ColorTrains.RED, 0, ColorTrains.WHITE, 0), 3,
                List.of(new Destination(bos, chi), new Destination(bos, mia)));

        var gs = new PlayerGameState(this.createSmallTrainsMap(), hand,
                new LinkedList<>(List.of(new HashSet<>(Set.of(chi2Mia)))));
        var in = createIn(List.of("boston", "chicago", "blue", 5));
        var out = createOut();
        var result = new PlayerProxy(in, out).play(gs);
        assertEquals(createFunctionCall("play", Map.of("this",
                        Map.of("destination1", List.of("boston", "chicago"),
                                "destination2", List.of("boston", "miami"),
                                "rails", 3,
                                "cards",
                                Map.of("green", 4, "blue", 3, "red", 0,
                                        "white", 0),
                                "acquired",
                                List.of(List.of("boston", "chicago", "blue",
                                        5))),
                        "acquired",
                        List.of(List.of(List.of("chicago", "miami", "white",
                                3))))),
                mapper.readTree(out.toString()));
        assertEquals(bos2Chi, result.getMove());
    }

    @Test
    public void testPlayBadAcquireConnection() {
        var bos = new City("boston", new Coord(0.4f, 0.6f));
        var chi = new City("chicago", new Coord(0.2f, 0.8f));
        var mia = new City("miami", new Coord(0.2f, 0.8f));
        var bos2Chi = new DirectConnection(bos, chi, 5, ColorTrains.BLUE);
        var chi2Mia = new DirectConnection(chi, mia, 3, ColorTrains.WHITE);

        var hand = new PlayerHand(Set.of(bos2Chi),
                // TODO: shouldn't be forced to provide all 4 color keys
                Map.of(ColorTrains.GREEN, 4, ColorTrains.BLUE, 3,
                        ColorTrains.RED, 0, ColorTrains.WHITE, 0), 3,
                List.of(new Destination(bos, chi), new Destination(bos, mia)));

        var gs = new PlayerGameState(this.createSmallTrainsMap(), hand,
                new LinkedList<>(List.of(new HashSet<>(Set.of(chi2Mia)))));
        var in = createIn(List.of("bad city", "chicago", "blue", 5));
        var out = createOut();
        assertThrows(RuntimeException.class,
                () -> new PlayerProxy(in, out).play(gs));
    }

    @Test
    public void testPlayBadMoreCards() {
        var bos = new City("boston", new Coord(0.4f, 0.6f));
        var chi = new City("chicago", new Coord(0.2f, 0.8f));
        var mia = new City("miami", new Coord(0.2f, 0.8f));
        var bos2Chi = new DirectConnection(bos, chi, 5, ColorTrains.BLUE);
        var chi2Mia = new DirectConnection(chi, mia, 3, ColorTrains.WHITE);

        var hand = new PlayerHand(Set.of(bos2Chi),
                // TODO: shouldn't be forced to provide all 4 color keys
                Map.of(ColorTrains.GREEN, 4, ColorTrains.BLUE, 3,
                        ColorTrains.RED, 0, ColorTrains.WHITE, 0), 3,
                List.of(new Destination(bos, chi), new Destination(bos, mia)));

        var gs = new PlayerGameState(this.createSmallTrainsMap(), hand,
                new LinkedList<>(List.of(new HashSet<>(Set.of(chi2Mia)))));
        var in = createIn("less cards");
        var out = createOut();
        assertThrows(RuntimeException.class,
                () -> new PlayerProxy(in, out).play(gs));
    }

    private TrainsMap createSmallTrainsMap() {
        var bos = new City("boston", new Coord(0.4f, 0.6f));
        var chi = new City("chicago", new Coord(0.2f, 0.8f));
        var cities = Set.of(bos, chi);
        var conns = Set.of(new DirectConnection(bos, chi, 5, ColorTrains.BLUE));
        return new TrainsMap(cities, conns, 500, 600);
    }

    private InputStream createIn(Object json) {
        try {
            return new ByteArrayInputStream(
                    new ObjectMapper().writeValueAsBytes(json));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private OutputStream createOut() {
        return new ByteArrayOutputStream();
    }

    private JsonNode createFunctionCall(String command, Object... args) {
        final var node = mapper.createArrayNode();
        node.add(command);
        node.add(mapper.valueToTree(args));
        return node;
    }
}
