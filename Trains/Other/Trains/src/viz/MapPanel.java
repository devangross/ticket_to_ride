package viz;

import map.City;
import map.ColorTrains;
import map.Coord;
import map.DirectConnection;
import map.TrainsMap;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a Panel containing a gameboard as a TrainsMap Calls paintComponent
 * on construction which calls helpers to draw map.
 */
public class MapPanel extends JPanel {
    private final TrainsMap trainsMap;
    private final int CURVE_MULTIPLIER = 5;
    private final int X_OFFSET = 5;
    private final int Y_OFFSET = 5;
    private final int OFFSET = 5;

    public MapPanel(TrainsMap trainsMap) {
        this.trainsMap = trainsMap;
    }

    /**
     * Function to find the length of a segment with distance formula
     */
    private static double getLengthPixels(Point2D p1, Point2D p2) {
        double x = p1.getX() - p2.getX();
        double y = p1.getY() - p2.getY();
        return Math.sqrt(x * x + y * y);
    }

    /**
     * Function looks through given direct connections and creates a Mapping of
     * 2 cities to a list of their direct connections. Purpose is to essentially
     * count the number of direct connections per two cities for purposes of
     * drawing multi connections.
     *
     * @param directConnections set of unique DirectConnection's
     *
     * @return HashMap mapping two cities to the direct connections between
     * them.
     **/
    public static HashMap<HashSet<City>, ArrayList<DirectConnection>> getNumMultiConnectionsByCities(
            Set<DirectConnection> directConnections) {
        HashMap<HashSet<City>, ArrayList<DirectConnection>>
                connectionsBetweenTwoCities = new HashMap<>();
        for (DirectConnection dc : directConnections) {
            HashSet<City> cityKey = new HashSet<>();
            cityKey.add(dc.getCity0());
            cityKey.add(dc.getCity1());

            // if we have an entry, add to list - else create and add current
            // directConnection
            if (connectionsBetweenTwoCities.containsKey(cityKey)) {
                ArrayList<DirectConnection> dcs =
                        connectionsBetweenTwoCities.get(cityKey);
                dcs.add(dc);
            } else {
                connectionsBetweenTwoCities.put(cityKey,
                        new ArrayList<>(Arrays.asList(dc)));
            }
        }
        return connectionsBetweenTwoCities;
    }

    /**
     * Method to paint TrainsMap object on MapPanel
     *
     * @param g canvas
     */
    @Override
    public void paintComponent(final Graphics g) {
        int mapWidth = trainsMap.getWidth();
        int mapHeight = trainsMap.getHeight();

        g.setColor(getBackground());
        g.fillRect(0, 0, mapWidth, mapHeight);
        g.setColor(getForeground());
        Graphics2D graphics = (Graphics2D) g;

        drawDirectConnections(graphics);
        drawCities(graphics, mapWidth);
        drawCityNames(graphics);
    }

    /**
     * Helper method to draw the city points on MapPanel
     *
     * @param graphics object to be drawn on
     */
    private void drawCities(Graphics2D graphics, int mapWidth) {
        for (final City c : trainsMap.getCities()) {
            graphics.setColor(Color.white);
            int[] cityPixels = relativeCoordValToPixels(c.getLocation());
            int sizePixel = Math.max(mapWidth / 100, 1);
            int x_val = enforceOffsetOnCityOval(this.trainsMap.getWidth(),
                    cityPixels[0] - sizePixel / 2);
            int y_val = enforceOffsetOnCityOval(this.trainsMap.getHeight(),
                    cityPixels[1] - sizePixel / 2);
            graphics.drawOval(x_val, y_val, sizePixel, sizePixel);
            graphics.fillOval(x_val, y_val, sizePixel, sizePixel);
        }
    }

    /**
     * Helper method to draw the cities names on MapPanel
     *
     * @param graphics object to be drawn on
     */
    private void drawCityNames(Graphics2D graphics) {
        for (final City c : trainsMap.getCities()) {
            graphics.setColor(Color.white);
            // draw the point for the city
            int[] cityPixels = relativeCoordValToPixels(c.getLocation());
            //graphics.fillOval(Math.max(cityPixels[0] - 4, 0), Math.max
            // (cityPixels[1] - 4, 0), 8, 8);
            int stringWidth =
                    graphics.getFontMetrics().stringWidth(c.getName());
            int stringHeight = graphics.getFontMetrics().getHeight();
            // draw the city name string
            graphics.drawString(c.getName(),
                    offsetCityLabelsX(cityPixels[0], stringWidth),
                    offsetCityLabelsY(cityPixels[1], stringHeight));
        }
    }

    /**
     * Ensures the given coordinate is in the range - offset and returns
     * adjusted coordinate in range [offset, parameter-offset]
     *
     * @param canvasParamMax the maximum value for height or width of the
     *                       canvas
     *
     * @return adjusted coordinate
     */
    private int enforceOffsetOnCityOval(int canvasParamMax, int location) {
        if (location > (canvasParamMax - OFFSET)) {
            return canvasParamMax - OFFSET;
        }
        return location;
    }

    /**
     * Ensures city labels is always printed on screen. Takes an x-coord pixel
     * location and width  and shifts the pixel location according to its
     * distance from the edge of our trainsMap window. Also adds offset for
     * normal labels generally, so they aren't rendered on city points
     *
     * @param pixelsX   original location
     * @param nameWidth width of the city name as a rendered string
     *
     * @return int - representing the resulting x-coord pixel location.
     */
    private int offsetCityLabelsX(int pixelsX, int nameWidth) {
        int widthPixelsMap = this.trainsMap.getWidth();
        int endString = pixelsX + nameWidth;

        if (widthPixelsMap < endString + X_OFFSET) {
            int shifted_x = pixelsX - (endString - widthPixelsMap);
            return shifted_x - X_OFFSET;
        }
        return pixelsX + X_OFFSET;
    }

    /**
     * Ensures city labels is always printed on screen. Takes an y-coord pixel
     * location and city name and shifts the pixel location according to its
     * distance from the edge of our trainsMap window.
     *
     * @param pixelsY      original location
     * @param stringHeight name of the city to be drawn
     *
     * @return int - representing the resulting y-coord pixel location.
     */
    private int offsetCityLabelsY(int pixelsY, int stringHeight) {
        int heightPixelsMap = this.trainsMap.getHeight();
        int topString = pixelsY - stringHeight;

        if (pixelsY > heightPixelsMap - Y_OFFSET) {
            return heightPixelsMap - Y_OFFSET;
        } else if (topString < Y_OFFSET) {
            return pixelsY + (Y_OFFSET - topString);
        }
        return pixelsY - Y_OFFSET;
    }

    /**
     * Helper method to draw DirectConnections on MapPanel
     * TODO maybe make direct connections drawing sorted by length increasing
     * outwards
     *
     * @param graphics object to be drawn on
     */
    private void drawDirectConnections(Graphics2D graphics) {
        HashMap<HashSet<City>, ArrayList<DirectConnection>>
                multiDirectConnections = getNumMultiConnectionsByCities(
                trainsMap.getDirectConnections());

        Set<HashSet<City>> keys = multiDirectConnections.keySet();
        Set<DirectConnection> drawnConnection = new HashSet<>();
        for (HashSet<City> key : keys) {
            // for each connection between these two cities: key
            // (ArrayList<City> length 2)
            int count = 2;
            boolean multi = true;
            // ensure to curve the first line if there are more
            if (count == 2 && multiDirectConnections.get(key).size() == 1) {
                count =
                        0; // indicates a single connection bewteen these two
                // cities for our bezier control and dash calc
                multi = false;
            }
            for (DirectConnection current : multiDirectConnections.get(key)) {
                if (drawnConnection.contains(current)) {
                    continue;
                }
                City c0 = current.getCity0();
                City c1 = current.getCity1();
                int[] c0PixelLocation =
                        relativeCoordValToPixels(c0.getLocation());
                int[] c1PixelLocation =
                        relativeCoordValToPixels(c1.getLocation());

                Point2D.Double P1 = new Point2D.Double(c0PixelLocation[0],
                        c0PixelLocation[1]); // Start Point
                Point2D.Double P2 = new Point2D.Double(c1PixelLocation[0],
                        c1PixelLocation[1]); // End Point

                int side;
                if (count % 2 == 0) {
                    side = 1;
                } else {
                    side = -1;
                }
                Point2D.Double control =
                        handleBezierControlPoint(count, side, P1, P2);

                QuadCurve2D quadCurve2D =
                        new QuadCurve2D.Double(P1.x, P1.y, control.x, control.y,
                                P2.x, P2.y);
                graphics.setColor(ColorTrains.getAWTColor(current.getColor()));
                float[] dashArray =
                        getDashArray(current.getLength(), P1, P2, control,
                                multi);
                Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_BEVEL, 0, dashArray, 0);
                graphics.setColor(ColorTrains.getAWTColor(current.getColor()));
                graphics.setStroke(dashed);
                graphics.draw(quadCurve2D);
                drawnConnection.add(current);
                count++;
            }
        }
    }

    /**
     * Determines the correct Bezier control point to create concentric curved
     * paths between points
     *
     * @param count the number of levels of concentric (one on each side of the
     *              straight line per level)
     * @param side  which side compared to the center line (-1 or 1)
     * @param P1    first point in the line
     * @param P2    second point in the line
     *
     * @return Bezier control point as a Point2D.Double
     */
    private Point2D.Double handleBezierControlPoint(int count, int side,
                                                    Point2D.Double P1,
                                                    Point2D.Double P2) {
        Point2D.Double control;
        double midPointX = (P1.x + P2.x) / 2;
        double midPointY = (P1.y + P2.y) / 2;
        double length = getLengthPixels(P1, P2);
        if (count == 0) { //straight line
            return new Point2D.Double(midPointX, midPointY);
        } else if (P1.x == P2.x) { //vertical line
            control = new Point2D.Double(
                    (midPointX - (CURVE_MULTIPLIER * 2 * count * side)),
                    midPointY / 2);
        } else if (P1.y == P2.y) {// horizontal line
            control = new Point2D.Double(midPointX,
                    (midPointY - (CURVE_MULTIPLIER * 2 * count * side)));
        } else { // any other line
            double slope = (P2.y - P1.y) / (P2.x - P1.x);
            double invSlope = -(1 / slope);
            double lengthMult = 1;
            double slope_multiplier =
                    CURVE_MULTIPLIER * count * side * lengthMult;
            // brute handling of very low/high magintude slopes
            if (invSlope < 1 && invSlope > -1) {
                invSlope = 5 * invSlope;
            }
            if (invSlope > 5 || invSlope < -5) {
                invSlope = invSlope / 5;
            }
            if (invSlope > 0) {
                control = new Point2D.Double(
                        midPointX + (slope_multiplier * invSlope),
                        (midPointY + (slope_multiplier * invSlope)));
            } else {
                control = new Point2D.Double(
                        midPointX + (slope_multiplier * invSlope),
                        (midPointY - (slope_multiplier * invSlope)));
            }
        }
        return control;
    }

    /**
     * Function to get a dash array representing the direct connection by
     * creating a float array with length number of connections and with a blank
     * dash size of 8.
     */
    private float[] getDashArray(int numConnections, Point2D p1, Point2D p2,
                                 Point2D control, boolean multi) {
        double lengthPixels = getLengthPixels(p1, p2);

        if (multi) {
            double cont_net =
                    getLengthPixels(p1, control) + getLengthPixels(p2, control);
            lengthPixels = (lengthPixels + cont_net) / 2;
        }
        if (numConnections == 3) {
            return new float[] {
                    (float) lengthPixels / 3f - (float) lengthPixels / 30f,
                    (float) lengthPixels / 20f};
        } else if (numConnections == 4) {
            return new float[] {
                    (float) lengthPixels / 4f - (float) lengthPixels / 25f,
                    (float) lengthPixels / 20f};
        } else if (numConnections == 5) {
            return new float[] {
                    (float) lengthPixels / 5f - (float) lengthPixels / 25f,
                    (float) lengthPixels / 20f};
        } else {
            throw new IllegalArgumentException(
                    "Invalid number of connections, must " +
                    "be between 3 and 5.");
        }
    }

    /**
     * Converts given relative Coord (float x, float y) and converts it to
     * equivalent pixel value as int array based on TrainsMap width and height
     *
     * @param cityCoordinate relative coordinates of the city
     *
     * @return int pixel value
     */
    private int[] relativeCoordValToPixels(Coord cityCoordinate) {
        float x = cityCoordinate.getX();
        float y = cityCoordinate.getY();
        // return values
        int x_ret = Math.round(x * trainsMap.getWidth());
        int y_ret = Math.round(y * trainsMap.getHeight());
        return new int[] {x_ret, y_ret};
    }
}
