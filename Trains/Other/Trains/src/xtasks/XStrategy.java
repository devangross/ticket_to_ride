package xtasks;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import json.JsonConverter;
import map.DirectConnection;
import map.TrainsMap;
import state.PlayerGameState;
import strategy.HoldTenStrategy;
import strategy.Move;

import java.io.BufferedInputStream;
import java.io.IOException;

public class XStrategy {

    public static void main(String[] args) {
        BufferedInputStream inputStream = new BufferedInputStream(System.in);
        JsonFactory factory = new JsonFactory();
        JsonParser parser;
        PlayerGameState pgs;
        try {
            parser = factory.createParser(inputStream);
            parser.setCodec(new ObjectMapper());
            parser.nextToken();
            TrainsMap map = JsonConverter.jsonToMap(parser.readValueAsTree());
            parser.nextToken();
            pgs = JsonConverter.jsonToPlayerState(map, parser.readValueAsTree());
        } catch (IOException e) {
            throw new IllegalArgumentException("JSON input invalid: " + e);
        }

        HoldTenStrategy hts = new HoldTenStrategy();
        Move<?> retMove = hts.makeMove(pgs);
        if (retMove.getMove() instanceof Boolean) {
            System.out.println("\"more cards\"");
        } else {
            DirectConnection dc = (DirectConnection) retMove.getMove();
            System.out.println(dc.toJSON());
        }
    }
}
