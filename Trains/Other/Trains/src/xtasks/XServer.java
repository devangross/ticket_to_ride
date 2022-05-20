package xtasks;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import server.Server;
import state.ColorCard;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        List<ColorCard> cards;

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
            parser.nextToken();
            parser.readValueAsTree();
            parser.nextValue();
            parser.readValueAsTree();
            parser.nextValue();
            cards = XRef.parseColorCards(
                    objectReader.forType(ArrayList.class).readValue(parser));
        } catch (IOException e) {
            throw new IllegalArgumentException("JSON input invalid: " + e);
        }

        if (args.length < 2) {
            throw new IllegalArgumentException("Expected port number");
        }
        final int port = Integer.parseInt(args[1]);
        String host = "127.0.0.1";
        if (args.length > 2) {
            host = args[2];
        }

        new Server(host, port, cards).start();
    }
}
