package agent;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import strategy.BuyNowStrategy;
import strategy.CheaterStrategy;
import strategy.HoldTenStrategy;
import strategy.OrderedDestSameCards;
import strategy.SuggestSmallMapStrategy;
import strategy.ThrowMoveException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ManagerTest {

    /**
     * helper to create unique player names from indices in for loop citation:
     * https://stackoverflow.com/questions/10813154/how-do-i-convert-a-number
     * -to-a-letter-in-java
     */
    public static String toAlphabetic(int i) {
        if (i < 0) {
            return "-" + toAlphabetic(-i - 1);
        }

        int quot = i / 26;
        int rem = i % 26;
        char letter = (char) ((int) 'A' + rem);
        if (quot == 0) {
            return "" + letter;
        } else {
            return toAlphabetic(quot - 1) + letter;
        }
    }

    @Test
    public void testShouldAllocateSixteenPlayersIntoTwoGroups() {
        List<IPlayer> sixteenPlayers = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            sixteenPlayers.add(new PlayerAgent("player" + toAlphabetic(i),
                    new BuyNowStrategy()));
        }
        Manager manager = new Manager(sixteenPlayers,
                RefereeAgentTest.getConstantCardListLengthN(200),
                new OrderedDestSameCards());

        List<LinkedList<IPlayer>> allocatedGroups =
                manager.allocateGameGroups(sixteenPlayers);

        assertEquals(2, allocatedGroups.size());
        assertEquals(8, allocatedGroups.get(0).size());
        assertEquals(8, allocatedGroups.get(1).size());
    }

    @Test
    public void testShouldAllocateSeventeenPlayersIntoThreeGroups() {
        List<IPlayer> seventeenPlayers = new ArrayList<>();
        for (int i = 0; i < 17; i++) {
            seventeenPlayers.add(new PlayerAgent("player" + toAlphabetic(i),
                    new BuyNowStrategy()));
        }
        Manager manager = new Manager(seventeenPlayers,
                RefereeAgentTest.getConstantCardListLengthN(200),
                new OrderedDestSameCards());

        List<LinkedList<IPlayer>> allocatedGroups =
                manager.allocateGameGroups(seventeenPlayers);

        assertEquals(3, allocatedGroups.size());
        assertEquals(8, allocatedGroups.get(0).size());
        assertEquals(7, allocatedGroups.get(1).size());
        assertEquals(2, allocatedGroups.get(2).size());
    }

    @Test
    public void testShouldAllocateSeventyNinePlayersIntoGroups() {
        List<IPlayer> seventyNine = new ArrayList<>();
        for (int i = 0; i < 79; i++) {
            seventyNine.add(new PlayerAgent("player" + toAlphabetic(i),
                    new BuyNowStrategy()));
        }
        Manager manager = new Manager(seventyNine,
                RefereeAgentTest.getConstantCardListLengthN(200),
                new OrderedDestSameCards());

        List<LinkedList<IPlayer>> allocatedGroups =
                manager.allocateGameGroups(seventyNine);

        assertEquals(10, allocatedGroups.size());
        assertEquals(8, allocatedGroups.get(0).size());
        assertEquals(8, allocatedGroups.get(1).size());
        assertEquals(8, allocatedGroups.get(2).size());
        assertEquals(7, allocatedGroups.get(9).size());
    }

    @Test
    public void testShouldAllocateTwoPlayersIntoOneGroup() {
        List<IPlayer> two = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            two.add(new PlayerAgent("player" + toAlphabetic(i),
                    new BuyNowStrategy()));
        }
        Manager manager = new Manager(two,
                RefereeAgentTest.getConstantCardListLengthN(200),
                new OrderedDestSameCards());

        List<LinkedList<IPlayer>> allocatedGroups =
                manager.allocateGameGroups(two);

        assertEquals(1, allocatedGroups.size());
        assertEquals(2, allocatedGroups.get(0).size());
    }

    @Test
    public void testShouldAllocateNineEightSevenPlayersIntoGroups() {
        List<IPlayer> alot = new ArrayList<>();
        for (int i = 0; i < 987; i++) {
            alot.add(new PlayerAgent("player" + toAlphabetic(i),
                    new BuyNowStrategy()));
        }
        Manager manager = new Manager(alot,
                RefereeAgentTest.getConstantCardListLengthN(200),
                new OrderedDestSameCards());

        List<LinkedList<IPlayer>> allocatedGroups =
                manager.allocateGameGroups(alot);

        assertEquals(124, allocatedGroups.size());
        assertEquals(8, allocatedGroups.get(0).size());
        assertEquals(8, allocatedGroups.get(1).size()); // ...
        assertEquals(3, allocatedGroups.get(123).size());
    }

    @Test
    public void testShouldPlayTournamentWith20Players() {
        List<IPlayer> players = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            if (i % 2 == 0) {
                players.add(new PlayerAgent("player" + toAlphabetic(i),
                        new BuyNowStrategy()));
            } else {
                players.add(new PlayerAgent("player" + toAlphabetic(i),
                        new HoldTenStrategy()));
            }
        }

        Manager manager = new Manager(players,
                RefereeAgentTest.getConstantCardListLengthN(250),
                new OrderedDestSameCards());

        Map<String, Set<IPlayer>> tournamentResult = manager.playTournament();

        Set<IPlayer> winners = tournamentResult.get("winners");
        Set<IPlayer> misbehavers = tournamentResult.get("misbehavers");

        assertEquals(1, winners.size());
        assertEquals("playerQ", new ArrayList<>(winners).get(0).getName());
        assertEquals(0, misbehavers.size());
    }

    @Test
    public void testShouldPlayTournamentWith100Players() {
        List<IPlayer> players = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            if (i % 7 == 0) {
                players.add(new PlayerAgent("player" + toAlphabetic(i),
                        new CheaterStrategy()));
            } else {
                players.add(new PlayerAgent("player" + toAlphabetic(i),
                        new HoldTenStrategy()));
            }
        }

        Manager manager = new Manager(players,
                RefereeAgentTest.getConstantCardListLengthN(250),
                new OrderedDestSameCards());

        Map<String, Set<IPlayer>> tournamentResult = manager.playTournament();

        Set<IPlayer> winners = tournamentResult.get("winners");
        Set<IPlayer> misbehavers = tournamentResult.get("misbehavers");

        assertEquals(1, winners.size());
        assertEquals("playerCV", new ArrayList<>(winners).get(0).getName());
        assertEquals(15, misbehavers.size());
    }

    @Test
    public void testShouldPlayTournamentWith20PlayersAndCheaters() {
        List<IPlayer> players = new ArrayList<>();
        players.add(new PlayerAgent("playerA", new BuyNowStrategy()));
        players.add(new PlayerAgent("playerB", new HoldTenStrategy()));
        players.add(new PlayerAgent("playerC", new CheaterStrategy()));
        players.add(new PlayerAgent("playerD", new BuyNowStrategy()));
        players.add(new PlayerAgent("playerE", new HoldTenStrategy()));
        players.add(new PlayerAgent("playerF", new CheaterStrategy()));
        players.add(new PlayerAgent("playerG", new BuyNowStrategy()));
        players.add(new PlayerAgent("playerH", new HoldTenStrategy()));
        players.add(new PlayerAgent("playerI", new CheaterStrategy()));
        players.add(new PlayerAgent("playerJ", new BuyNowStrategy()));
        players.add(new PlayerAgent("playerK", new BuyNowStrategy()));
        players.add(new PlayerAgent("playerL", new HoldTenStrategy()));
        players.add(new PlayerAgent("playerM", new CheaterStrategy()));
        players.add(new PlayerAgent("playerN", new BuyNowStrategy()));
        players.add(new PlayerAgent("playerO", new HoldTenStrategy()));
        players.add(new PlayerAgent("playerP", new CheaterStrategy()));
        players.add(new PlayerAgent("playerQ", new BuyNowStrategy()));
        players.add(new PlayerAgent("playerR", new HoldTenStrategy()));
        players.add(new PlayerAgent("playerS", new CheaterStrategy()));
        players.add(new PlayerAgent("playerT", new BuyNowStrategy()));

        Manager manager = new Manager(players,
                RefereeAgentTest.getConstantCardListLengthN(200),
                new OrderedDestSameCards());

        Map<String, Set<IPlayer>> tournamentResult = manager.playTournament();

        Set<IPlayer> winners = tournamentResult.get("winners");
        Set<IPlayer> misbehavers = tournamentResult.get("misbehavers");

        assertEquals(1, winners.size());
        assertEquals("playerT", new ArrayList<>(winners).get(0).getName());
        assertEquals(6, misbehavers.size());
    }

    @Test
    public void testShouldPlayTournamentWith20PlayersAndThrowers() {
        List<IPlayer> players = new ArrayList<>();
        players.add(new PlayerAgent("playerA", new BuyNowStrategy()));
        players.add(new PlayerAgent("playerB", new HoldTenStrategy()));
        players.add(new PlayerAgent("playerC", new ThrowMoveException()));
        players.add(new PlayerAgent("playerD", new BuyNowStrategy()));
        players.add(new PlayerAgent("playerE", new HoldTenStrategy()));
        players.add(new PlayerAgent("playerF", new ThrowMoveException()));
        players.add(new PlayerAgent("playerG", new BuyNowStrategy()));
        players.add(new PlayerAgent("playerH", new HoldTenStrategy()));
        players.add(new PlayerAgent("playerI", new ThrowMoveException()));
        players.add(new PlayerAgent("playerJ", new BuyNowStrategy()));
        players.add(new PlayerAgent("playerK", new BuyNowStrategy()));
        players.add(new PlayerAgent("playerL", new HoldTenStrategy()));
        players.add(new PlayerAgent("playerM", new ThrowMoveException()));
        players.add(new PlayerAgent("playerN", new BuyNowStrategy()));
        players.add(new PlayerAgent("playerO", new HoldTenStrategy()));
        players.add(new PlayerAgent("playerP", new ThrowMoveException()));
        players.add(new PlayerAgent("playerQ", new BuyNowStrategy()));
        players.add(new PlayerAgent("playerR", new HoldTenStrategy()));
        players.add(new PlayerAgent("playerS", new ThrowMoveException()));
        players.add(new PlayerAgent("playerT", new BuyNowStrategy()));

        Manager manager = new Manager(players,
                RefereeAgentTest.getConstantCardListLengthN(200),
                new OrderedDestSameCards());

        Map<String, Set<IPlayer>> tournamentResult = manager.playTournament();

        Set<IPlayer> winners = tournamentResult.get("winners");
        Set<IPlayer> misbehavers = tournamentResult.get("misbehavers");

        assertEquals(1, winners.size());
        assertEquals("playerB", new ArrayList<>(winners).get(0).getName());
        assertEquals(6, misbehavers.size());
    }

    @Test
    public void testShouldPlayTournamentWith3PlayersWhoTieForWin() {
        List<IPlayer> players = new ArrayList<>();

        players.add(new PlayerAgent("playerA", new BuyNowStrategy()));
        players.add(new PlayerAgent("playerB", new BuyNowStrategy()));
        players.add(new PlayerAgent("playerC", new BuyNowStrategy()));

        Manager manager = new Manager(players,
                RefereeAgentTest.getConstantCardListLengthN(13),
                new OrderedDestSameCards());

        Map<String, Set<IPlayer>> tournamentResult = manager.playTournament();

        Set<IPlayer> winners = tournamentResult.get("winners");
        Set<IPlayer> misbehavers = tournamentResult.get("misbehavers");

        assertEquals(3, winners.size()); // 3 winners
        assertEquals(0, misbehavers.size());
    }

    @Test
    public void testShouldPlayTournamentWith9Players() {
        List<IPlayer> players = new ArrayList<>();

        players.add(new PlayerAgent("playerA", new BuyNowStrategy()));
        players.add(new PlayerAgent("playerB", new BuyNowStrategy()));
        players.add(new PlayerAgent("playerC", new BuyNowStrategy()));
        players.add(new PlayerAgent("playerD", new BuyNowStrategy()));
        players.add(new PlayerAgent("playerE", new BuyNowStrategy()));
        players.add(new PlayerAgent("playerF", new BuyNowStrategy()));
        players.add(new PlayerAgent("playerG", new BuyNowStrategy()));
        players.add(new PlayerAgent("playerH", new BuyNowStrategy()));
        players.add(new PlayerAgent("playerI", new HoldTenStrategy()));

        Manager manager = new Manager(players,
                RefereeAgentTest.getConstantCardListLengthN(250),
                new OrderedDestSameCards());

        Map<String, Set<IPlayer>> tournamentResult = manager.playTournament();

        Set<IPlayer> winners = tournamentResult.get("winners");
        Set<IPlayer> misbehavers = tournamentResult.get("misbehavers");

        assertEquals(1, winners.size()); // 3 winners
        assertEquals(0, misbehavers.size());
    }

    @Test
    public void testShouldThrowWhenNotEnoughDestinations() {
        List<IPlayer> players = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            players.add(new PlayerAgent("player" + toAlphabetic(i),
                    new SuggestSmallMapStrategy()));
        }
        Manager manager = new Manager(players,
                RefereeAgentTest.getConstantCardListLengthN(250),
                new OrderedDestSameCards());

        Assertions.assertThrows(IllegalArgumentException.class,
                manager::playTournament);
    }
}
