package xtasks;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import json.JsonConverter;
import map.City;
import map.TrainsMap;

import java.io.BufferedInputStream;
import java.io.IOException;

/**
 * Main class for XMap deliverable - reads in two cities and map in JSON form
 * and returns if the given cities are connected in the map.
 */
public class XMap {
    public static void main(String[] args) {
        BufferedInputStream inputStream = new BufferedInputStream(System.in);
        JsonFactory factory = new JsonFactory();
        JsonParser parser;
        City city0;
        City city1;
        TrainsMap map;
        try {
            parser = factory.createParser(inputStream);
            parser.setCodec(new ObjectMapper());
            parser.nextToken();
            String city0Name = parser.readValueAs(String.class);
            parser.nextToken();
            String city1Name = parser.readValueAs(String.class);
            parser.nextToken();
            map = JsonConverter.jsonToMap(parser.readValueAsTree());
            city0 = JsonConverter.findCityByName(map.getCities(), city0Name);
            city1 = JsonConverter.findCityByName(map.getCities(), city1Name);
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "JSON input invalid, must be 2 strings for city and a " +
                            "JSON representation of a TrainsMap");
        }

        System.out.println(map.areCitiesConnected(city0, city1));
    }
}
