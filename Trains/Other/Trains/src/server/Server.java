package server;

import agent.IPlayer;
import agent.Manager;
import agent.PlayerProxy;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import state.ColorCard;
import strategy.OrderedDestSameCards;
import xtasks.XManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Represents a server that accepts clients, supports a waiting room, and calls
 * implementations whose jobs are to listen for timer expirations (to indicate a
 * tournament may start) or new client connections (to register a player for a
 * tournament).
 */
public class Server {

    private static final int NAME_TIMEOUT_MS = 3_000;
    private static final int WAITING_PERIOD_MS = 20_000;
    private static final JsonFactory JSON_FACTORY =
            new JsonFactory(new ObjectMapper())
                    .configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false)
                    .configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);

    protected final ExecutorService executor = Executors.newCachedThreadPool();

    private final ServerSocket socket;
    private final Object lock = new Object();
    private final AtomicReference<TimerTask> timer = new AtomicReference<>();
    private final AtomicBoolean running = new AtomicBoolean(false);

    private final List<IPlayer> players = new ArrayList<>();
    private final List<ColorCard> cards;

    private volatile boolean inSecondRound = false;
    private volatile boolean isTournamentRunning = false;

    public Server(String host, int port, List<ColorCard> cards) {
        this.cards = cards;

        try {
            this.socket =
                    new ServerSocket(port, 64, InetAddress.getByName(host));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Starts a new waiting room and blocks until {@link #cancelAll()} has been
     * called by an implementation.
     */
    public void start() {
        this.running.set(true);
        final Future<?> registrationTask = this.executor.submit(() -> {
            while (true) {
                try {
                    final Socket clientSocket = this.socket.accept();
                    this.executor.submit(new RegistrationListener(clientSocket));
                } catch (IOException e) {
                    if (this.running.get()) {
                        throw new RuntimeException(e);
                    }
                    return;
                }
            }
        });
        this.startTimer();
        try {
            registrationTask.get();
            if (!this.executor.awaitTermination(5, TimeUnit.SECONDS)) {
                System.err.println("Executor didn't shut down properly.");
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } finally {
            this.cancelAll();
        }
    }

    /**
     * Gets whether a tournament is currently running (which is likely happening
     * in another thread).
     */
    private boolean isNotTournamentRunning() {
        return !this.isTournamentRunning;
    }

    /**
     * Cancels all executors, closes the server socket, and cancels the waiting
     * room. Should be called to allow {@link #start()} to terminate.
     */
    protected void cancelAll() {
        this.executor.shutdown();
        PlayerProxy.executor.shutdown();
        this.running.set(false);
        this.timer.get().cancel();
        try {
            this.socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Starts the waiting room timer.
     */
    private void startTimer() {
        final TimerTask task = new TimerTask() {
            @Override
            public void run() {
                synchronized (Server.this.lock) {
                    if (Server.this.isNotTournamentRunning() &&
                        Server.this.onTick()) {
                        Server.this.startTimer();
                    }
                }
            }
        };
        new Timer(true).schedule(task, WAITING_PERIOD_MS);
        this.timer.set(task);
    }

    /**
     * Called at the end of every timer expiration.
     *
     * @return `true` to start another timer, `false` to stop
     */
    private boolean onTick() {
        if (this.inSecondRound && !this.isReady()) {
            System.out.println("[[],[]]");
            this.cancelAll();
            return false;
        }

        if (this.isReady()) {
            this.startTournament();
            return false;
        }

        if (this.inSecondRound) {
            return false;
        }
        return this.inSecondRound = true;
    }

    /**
     * Called when a new player registers.
     */
    private void onNewPlayer(IPlayer player) {
        this.players.add(player);
        if (this.players.size() == 50) {
            this.startTournament();
        }
    }

    private void startTournament() {
        this.isTournamentRunning = true;
        this.executor.submit(() -> {
            Manager manager = new Manager(this.players, this.cards, new OrderedDestSameCards());
            XManager.runTournamentAndDisplayResults(manager);
            this.cancelAll();
        });
    }

    /**
     * Returns whether the server has enough players for a tournament.
     */
    private boolean isReady() {
        return this.players.size() >= (this.inSecondRound ? 2 : 5);
    }

    private boolean isDuplicatePlayerName(String name) {
        return this.players.stream().anyMatch(p -> p.getName().equals(name));
    }

    private static final char[] playerNameSuffixes;

    static {
        playerNameSuffixes = new char[26 * 2];
        for (int i = 0; i < 26; i++) {
            playerNameSuffixes[i] = (char) ('A' + i);
        }
        for (int i = 0; i < 26; i++) {
            playerNameSuffixes[i + 26] = (char) ('a' + i);
        }
    }

    private int nextPlayerNameSuffixIdx = 0;

    private String addPlayerNameSuffix(String name) {
        return name + (playerNameSuffixes[nextPlayerNameSuffixIdx++ % playerNameSuffixes.length]);
    }

    /**
     * Listens for registration from connected client sockets.
     */
    private final class RegistrationListener implements Runnable {

        private final Socket clientSocket;
        private final AtomicBoolean didSendName = new AtomicBoolean(false);

        private RegistrationListener(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            // Ensure that they send their name within the timeout period.
            new Timer(true).schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!RegistrationListener.this.didSendName.get()) {
                        try {
                            RegistrationListener.this.clientSocket.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }, NAME_TIMEOUT_MS);
            try {
                final JsonParser in = Server.JSON_FACTORY.createParser(
                        this.clientSocket.getInputStream());
                final JsonGenerator out = Server.JSON_FACTORY.createGenerator(
                        this.clientSocket.getOutputStream());
                in.nextValue();
                String name = in.getValueAsString();
                this.didSendName.set(true);
                // locks access to Server fields, blocking other threads until completion
                synchronized (Server.this.lock) {
                    if (Server.this.running.get() &&
                        Server.this.isNotTournamentRunning()) {
                        while (Server.this.isDuplicatePlayerName(name)) {
                            name = addPlayerNameSuffix(name);
                        }
                        final PlayerProxy player = new PlayerProxy(in, out, name);
                        Server.this.onNewPlayer(player);
                    } else {
                        this.clientSocket.close();
                    }
                }
            } catch (JsonParseException | IllegalArgumentException e) {
                try {
                    this.clientSocket.close();
                } catch (IOException ignored) {
                    //
                }
            } catch (IOException e) {
                try {
                    this.clientSocket.close();
                } catch (IOException ignored) {
                    //
                }
                throw new RuntimeException(e);
            }
        }
    }
}
