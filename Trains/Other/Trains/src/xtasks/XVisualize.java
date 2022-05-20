package xtasks;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import json.JsonConverter;
import map.TrainsMap;
import viz.VisualizerMain;

import javax.swing.*;
import java.io.BufferedInputStream;
import java.io.IOException;

public class XVisualize {

    public static void main(String[] args) {
        BufferedInputStream inputStream = new BufferedInputStream(System.in);
        JsonFactory factory = new JsonFactory();
        JsonParser parser;
        TrainsMap map;
        try {
            parser = factory.createParser(inputStream);
            parser.setCodec(new ObjectMapper());
            parser.nextToken();
            map = JsonConverter.jsonToMap(parser.readValueAsTree());
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "JSON input invalid, must be 2 strings for city and a " +
                    "JSON representation" +
                    "of a Trains.com Map");
        }
        VisualizerMain vis = new VisualizerMain(map);

        new Timer(10_000, (e) -> vis.closeVisualizer()).start();
        vis.showVisualizer();
    }
}
