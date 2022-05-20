package viz;

import map.ExampleMap;
import map.TrainsMap;

import javax.swing.*;

/**
 * Main class for creating map visualization of Trains
 */
public class VisualizerMain {
    final JFrame frame;
    MapGUI gui;
    MapPanel mapPanel;

    /**
     * Used to construct an instance of this visualizer by constructing a new
     * MapGUI with the given TrainsMap, then making a MapPanel which draws the
     * cities and connections, then creates a JFrame to hold the panel.
     *
     * @param trainsMap TrainsMap to display
     */
    public VisualizerMain(TrainsMap trainsMap) {
        this.gui = new MapGUI(trainsMap);
        this.mapPanel = this.gui.makeMapPanel();
        this.frame = new JFrame("Trains");
    }

    public static void main(String[] args) {
        TrainsMap tester = ExampleMap.createCaliforniaMap();
        MapGUI gui = new MapGUI(tester);
        MapPanel mapPanel = gui.makeMapPanel();
        // make frame and put panel in
        final JFrame frame = new JFrame("Trains");
        frame.setContentPane(mapPanel);
        frame.pack();
        frame.setVisible(true);
        frame.repaint();
    }

    /**
     * Function called to show this visualizer window.
     */
    public void showVisualizer() {
        this.frame.setContentPane(this.mapPanel);
        this.frame.pack();
        this.frame.setVisible(true);
        this.frame.repaint();
    }

    /**
     * Function called to close this visualizer window.
     */
    public void closeVisualizer() {
        this.frame.setVisible(false);
        this.frame.dispose();
    }
}
