package xtasks;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import json.JsonConverter;
import map.City;
import map.ColorTrains;
import map.DirectConnection;
import map.TrainsMap;
import state.PlayerGameState;

import java.io.BufferedInputStream;
import java.io.IOException;

import java.util.ArrayList;

/**
 * Parses an input from xlegal and determines whether the given move with given
 * map for given player is a legal move. NOTE: This parser will not break on a
 * number of cards value greater than MAX_INT, but it will truncate a BigInteger
 * to an Integer.
 */
public class XLegal {
    public static void main(String[] args) {
        BufferedInputStream inputStream = new BufferedInputStream(System.in);
        JsonFactory factory = new JsonFactory();
        JsonParser parser;
        PlayerGameState pgs;
        DirectConnection toAcquire;
        try {
            parser = factory.createParser(inputStream);
            parser.setCodec(new ObjectMapper());
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectReader objectReader = objectMapper.reader();
            parser.nextToken();
            TrainsMap map = JsonConverter.jsonToMap(parser.readValueAsTree());
            parser.nextToken();
            pgs = JsonConverter.jsonToPlayerState(map, parser.readValueAsTree());
            parser.nextToken();
            toAcquire = parseAcquired(
                    objectReader.forType(ArrayList.class).readValue(parser),
                    map);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(
                    "JSON PlayerState input invalid: " + e);
        }
        System.out.println(pgs.canAcquire(toAcquire));
    }

    private static DirectConnection parseAcquired(ArrayList<Object> dest,
                                                  TrainsMap map) {
        String name1 = (String) dest.get(0);
        String name2 = (String) dest.get(1);
        ColorTrains color = ColorTrains.valueOf(((String) dest.get(2)).toUpperCase());
        Integer length = (Integer) dest.get(3);

        City c1 = new City(name1, map.getCoordGivenCityName(name1));
        City c2 = new City(name2, map.getCoordGivenCityName(name2));
        DirectConnection dc = new DirectConnection(c1, c2, length, color);
        return dc;
    }
}
