package strategy;

import agent.IPlayer;
import agent.PlayerAgent;
import agent.RefereeAgent;
import agent.RefereeAgentTest;
import map.ExampleMap;
import map.TrainsMap;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ThrowStrategyTest {

    @Test
    public void testShouldThrowAndBeCaughtByReferee() {
        TrainsMap map = ExampleMap.createBostonMap();
        PlayerAgent thrower =
                new PlayerAgent("thrower", new ThrowDestException());
        PlayerAgent buyNowPlayer =
                new PlayerAgent("buyNow", new BuyNowStrategy());
        LinkedList<IPlayer> players = new LinkedList<>();
        players.addLast(thrower);
        players.addLast(buyNowPlayer);

        IAdminStrategy rulebook = new OrderedDestSameCards();
        RefereeAgent referee = new RefereeAgent(map, players,
                RefereeAgentTest.getConstantCardListLengthN(100), rulebook, 20);
        referee.playGame();
        assertFalse(thrower.isGameWinner());
        assertTrue(buyNowPlayer.isGameWinner());
    }

    @Test
    public void testShouldThrowOnMoveAndBeCaughtByReferee() {
        TrainsMap map = ExampleMap.createBostonMap();
        PlayerAgent thrower =
                new PlayerAgent("thrower", new ThrowMoveException());
        PlayerAgent buyNowPlayer =
                new PlayerAgent("buyNow", new BuyNowStrategy());
        LinkedList<IPlayer> players = new LinkedList<>();
        players.addLast(thrower);
        players.addLast(buyNowPlayer);

        IAdminStrategy rulebook = new OrderedDestSameCards();
        RefereeAgent referee = new RefereeAgent(map, players,
                RefereeAgentTest.getConstantCardListLengthN(100), rulebook, 20);
        referee.playGame();
        assertFalse(thrower.isGameWinner());
        assertTrue(buyNowPlayer.isGameWinner());
    }
}
