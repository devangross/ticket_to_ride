package strategy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StrategyLoaderTest {
    @Test
    public void testShouldLoadBuyNowStrategyFromPath() {
        String path = "Trains/out/production/Trains/strategy.BuyNowStrategy";
        IPlayerStrategy buyNow = StrategyLoader.loadStrategyFromPath(path);
        assertEquals(BuyNowStrategy.class, buyNow.getClass());
    }

    @Test
    public void testShouldLoadHoldTenStrategyFromPath() {
        String path = "Trains/out/production/Trains/strategy.HoldTenStrategy";
        IPlayerStrategy buyNow = StrategyLoader.loadStrategyFromPath(path);
        assertEquals(HoldTenStrategy.class, buyNow.getClass());
    }

    @Test
    public void testShouldThrowWhenInvalidPath() {
        String path = "Trains/out/production/Trains/strategy.Unknown";

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            StrategyLoader.loadStrategyFromPath(path);
        });
    }

}
