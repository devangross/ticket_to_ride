package map;

import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ColorTrainsTest {

    @Test
    public void testShouldGetAWTBlueColor() {
        ColorTrains blue = ColorTrains.BLUE;
        assertEquals(Color.blue, ColorTrains.getAWTColor(blue));
    }

    @Test
    public void testShouldGetAWTRedColor() {
        ColorTrains red = ColorTrains.RED;
        assertEquals(Color.red, ColorTrains.getAWTColor(red));
    }

    @Test
    public void testShouldGetAWTGreenColor() {
        ColorTrains green = ColorTrains.GREEN;
        assertEquals(Color.green, ColorTrains.getAWTColor(green));
    }

    @Test
    public void testShouldGetAWTWhiteColor() {
        ColorTrains white = ColorTrains.WHITE;
        assertEquals(Color.white, ColorTrains.getAWTColor(white));
    }
}
