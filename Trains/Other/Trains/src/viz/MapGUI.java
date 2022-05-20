package viz;

import map.TrainsMap;

import java.awt.*;

public class MapGUI {

    private final TrainsMap trainsMap;

    public MapGUI(TrainsMap map) {
        this.trainsMap = map;
    }

    /**
     * Creates panel for return to main, with correct panel features (opaque,
     * background color, size)
     */
    public MapPanel makeMapPanel() {
        MapPanel mapPan =
                new MapPanel(this.trainsMap); // calls paintComponent implicitly
        mapPan.setOpaque(true);
        mapPan.setBackground(Color.darkGray);
        mapPan.setPreferredSize(
                new Dimension(trainsMap.getWidth(), trainsMap.getHeight()));
        return mapPan;
    }

}
