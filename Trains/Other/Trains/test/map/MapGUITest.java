package map;

import org.junit.jupiter.api.Test;
import viz.MapGUI;
import viz.MapPanel;
import viz.VisualizerMain;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MapGUITest {

    // essentially asserts that valid maps can be passed to the mapgui and no
    // exceptions are thrown
    @Test
    public void testShouldCreateValidMapGUI() {
        TrainsMap exampleMap = map.ExampleMap.createSimpleDoubleConnectionMap();
        MapGUI mapGUI = new MapGUI(exampleMap);
        assertNotNull(mapGUI);
    }

    @Test
    public void testShouldCreateValidMapGUIMaxSize() {
        TrainsMap exampleMap = map.ExampleMap.createSimpleDoubleConnectionMap();
        MapGUI mapGUI = new MapGUI(exampleMap);
        MapPanel panel = mapGUI.makeMapPanel();
        final JFrame frame = new JFrame("Trains");
        frame.setContentPane(panel);
        frame.pack();

        assertEquals(panel.getHeight(), 800);
        assertEquals(panel.getWidth(), 800);

        assertNotNull(mapGUI);
    }

    @Test
    public void testShouldGetMapPanelFromValidGUIAndCheckSize() {
        TrainsMap exampleMap = map.ExampleMap.createSimpleDoubleConnectionMap();
        MapGUI mapGUI = new MapGUI(exampleMap);
        MapPanel panel = mapGUI.makeMapPanel();
        final JFrame frame = new JFrame("Trains");
        frame.setContentPane(panel);
        frame.pack();

        assertEquals(panel.getHeight(), 800);
        assertEquals(panel.getWidth(), 800);

        assertNotNull(mapGUI);
    }

    @Test
    public void testShouldShowVisualizerSimple() throws InterruptedException {
        TrainsMap exampleMap = map.ExampleMap.createSimpleDoubleConnectionMap();
        VisualizerMain visualizer = new VisualizerMain(exampleMap);
        visualizer.showVisualizer();
//    Thread.sleep(5000);
        assertTrue(true);
    }

    @Test
    public void testShouldShowComplicatedVisualizer()
            throws InterruptedException {
        TrainsMap exampleMap =
                map.ExampleMap.createExampleMultipleConnectionMap();
        VisualizerMain visualizer = new VisualizerMain(exampleMap);
        visualizer.showVisualizer();
//    Thread.sleep(5000);
        assertTrue(true);
    }

}
