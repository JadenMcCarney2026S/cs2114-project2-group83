package javaPackage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import student.TestCase;

public class GameTest extends TestCase {
    private Game game;
    private Player player;
    private Dungeon first;
    private Dungeon second;
    private PrintStream originalOut;
    private InputStream originalIn;
    private ByteArrayOutputStream output;

    public void setUp() {
        originalOut = System.out;
        originalIn = System.in;
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
        player = new Player();
        first = new Dungeon("First", 1, DungeonTest.map());
        second = new Dungeon("Second", 2, DungeonTest.map());
        game = new Game(player, first, null, second);
    }

    public void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    private String printed() {
        return new String(output.toByteArray(), StandardCharsets.UTF_8);
    }

    private void input(String text) {
        System.setIn(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)));
    }

    public void testMainLaunchesCompleteGame() {
        input("quit\n");
        Game.main(new String[] {"--console"});
        assertTrue(printed().contains("Welcome to Dungeon Crawler!"));
        assertTrue(printed().contains("----- Dungeon 1 -----"));
        assertTrue(printed().contains("Thanks for playing"));
        assertFalse(printed().contains("Dungeon 2"));
        output.reset();
        input("");
        Game.main(new String[] {"--console"}); // EOF stops the console interface.
        assertTrue(printed().contains("----- Dungeon 1 -----"));
    }

    public void testInputNormalizationAndUnloadedMap() {
        game.processCommand(" \t ");
        game.processCommand(null);
        assertEquals(1, game.getPlayerColumn());
        game.processCommand("  MOVE \t EAST  ");
        assertEquals(2, game.getPlayerColumn());
        game.processCommand("  move   west  ");
        assertEquals(1, game.getPlayerColumn());
        new Game(null).processCommand("map");
        assertTrue(printed().contains("No dungeon loaded"));
    }

    public void testStartAndIsRunning() {
        input("status\nquit\n");
        game.start();
        assertFalse(game.isRunning());
        assertTrue(printed().contains("----- First -----"));
        assertTrue(printed().contains("Health: 100"));
        output.reset();
        input("");
        game.start(); // EOF must stop cleanly.
        assertFalse(game.isRunning());
        Game empty = new Game(null, (Dungeon[])null);
        empty.start();
        assertFalse(empty.isRunning());
        assertNull(empty.getCurrentDungeon());
        assertEquals(0, empty.getPlayerRow());
        assertEquals(0, empty.getPlayerColumn());
        new Game(player).start();
        new Game(new Player(0, 10), first).start();
        assertTrue(printed().contains("Cannot start"));
    }

    public void testProcessCommand() {
        String[] invalid = {null, "", "   ", "nonsense", "move", "move north extra",
            "status extra", "move nowhere", "attack nowhere", "quit extra"};
        for (String command : invalid) {
            game.processCommand(command);
            assertEquals(1, game.getPlayerColumn());
        }
        game.processCommand("  MoVe   EAST  ");
        assertEquals(2, game.getPlayerColumn());
        game.processCommand("help");
        game.processCommand("status");
        game.processCommand("map");
        game.processCommand("inventory");
        game.processCommand("inv");
        game.processCommand("attack south");
        assertTrue(printed().contains("Commands:"));
        assertTrue(printed().contains("Invalid"));
    }

    public void testMoveAndCollect() {
        game.movePlayer(null);
        game.movePlayer("north");
        game.movePlayer("south");
        assertEquals(1, game.getPlayerRow());
        assertEquals(1, game.getPlayerColumn());
        game.movePlayer(" EAST ");
        assertEquals(2, game.getPlayerColumn());
        assertEquals(2, player.getX());
        assertEquals(1, player.getY());
        assertEquals(1, game.getItemCount());
        assertEquals(115, player.getHealth());
        assertEquals(15, player.getDamage());
        assertNull(first.getEnemyAt(1, 2));
        game.movePlayer("west");
        game.movePlayer("east");
        assertEquals(1, game.getItemCount());
        assertEquals(115, player.getHealth());
        game.movePlayer("right");
        assertTrue(printed().contains("found the exit"));
        new Game(null, first).movePlayer("east");
        new Game(player).movePlayer("east");
    }

    public void testAttack() {
        game.attack(null);
        game.attack("east"); // Item must not be attacked.
        game.attack("north");
        assertEquals(100, player.getHealth());
        game.attack("south");
        assertEquals(10, first.getEnemyAt(2, 1).getHealth());
        assertEquals(95, player.getHealth());
        game.attack("south");
        assertNull(first.getEnemyAt(2, 1));
        game.movePlayer("south");
        assertEquals(2, game.getPlayerRow());
        new Game(null, first).attack("south");
        new Game(player).attack("south");
    }

    public void testDeathStopsLoopAndActions() {
        Player weak = new Player(1, 1);
        Game dying = new Game(weak, first);
        input("attack south\nmove east\n");
        dying.start();
        assertFalse(dying.isRunning());
        assertEquals(0, weak.getHealth());
        dying.movePlayer("east");
        dying.attack("south");
        assertEquals(1, dying.getPlayerColumn());
        assertFalse(dying.moveToNextDungeon());
        assertTrue(printed().contains("Game over"));
    }

    public void testNextLayerAndCurrentDungeon() {
        assertSame(first, game.getCurrentDungeon());
        assertFalse(game.moveToNextDungeon());
        game.nextLayer();
        assertSame(first, game.getCurrentDungeon());
        game.movePlayer("east");
        game.movePlayer("east");
        assertTrue(game.moveToNextDungeon());
        assertSame(second, game.getCurrentDungeon());
        assertEquals(1, game.getPlayerColumn());
        assertEquals(1, game.getPlayerRow());
        assertEquals(1, player.getX());
        assertTrue(printed().contains("----- Second -----"));
        game.movePlayer("east");
        game.movePlayer("east");
        assertFalse(game.moveToNextDungeon());
        assertSame(second, game.getCurrentDungeon());
        assertTrue(printed().contains("completed all"));
        assertFalse(new Game(null).moveToNextDungeon());
    }

    public void testNextCommandAndVictory() {
        input("move east\nmove east\nnext\nmove east\nmove east\nnext\n");
        game.start();
        assertFalse(game.isRunning());
        assertSame(second, game.getCurrentDungeon());
        assertTrue(printed().contains("completed all"));
        assertEquals(2, game.getItemCount());
    }

    public void testInventory() {
        Item item = new Item("Potion", 2, 0);
        game.showInventory();
        assertTrue(printed().contains("empty"));
        assertEquals(0, game.getItemCount());
        assertFalse(game.addItem(null));
        assertTrue(game.addItem(item));
        assertFalse(game.addItem(item));
        assertTrue(game.hasItem(item));
        assertFalse(game.hasItem(null));
        assertEquals(1, game.getItemCount());
        game.getInventory().clear();
        assertEquals(1, game.getInventory().size());
        game.showInventory();
        assertTrue(printed().contains("Potion"));
        assertFalse(game.removeItem(null));
        assertFalse(game.removeItem(new Item("Other", 0, 0)));
        assertTrue(game.removeItem(item));
        assertFalse(game.removeItem(item));
    }

    public void testShowStatus() {
        game.showStatus();
        assertTrue(printed().contains("Position: (1, 1)"));
        new Game(null).showStatus();
        assertTrue(printed().contains("No game loaded"));
    }

    /** Checks the specified response as well as surviving each bad command. */
    public void testInvalidCommandResponses() {
        String[][] cases = {
            {null, "Please enter a command."},
            {"  ", "Please enter a command."},
            {"wndjacndjhui", "Invalid command. Type 'help'."},
            {"move", "Invalid arguments. Type 'help'."},
            {"move east extra", "Invalid arguments. Type 'help'."},
            {"move nowhere", "Invalid direction. Use north, south, east, or west."},
            {"attack nowhere", "Invalid direction. Use north, south, east, or west."},
            {"move north", "You cannot move to that tile."},
            {"attack north", "No enemy to attack in that direction."},
            {"next", "You must reach the exit first."}
        };
        for (String[] example : cases) {
            output.reset();
            game.processCommand(example[0]);
            assertEquals(example[1] + System.lineSeparator(), printed());
            assertEquals(1, game.getPlayerRow());
            assertEquals(1, game.getPlayerColumn());
            assertEquals(100, player.getHealth());
            assertEquals(0, game.getItemCount());
        }
    }

    public void testInvalidCommandRepromptsDuringLiveGame() {
        byte[] commands = "nonsense\nmove nowhere\nstatus\nquit\n"
            .getBytes(StandardCharsets.UTF_8);
        System.setIn(new ByteArrayInputStream(commands) {
            @Override
            public synchronized int read(byte[] buffer, int offset, int length) {
                assertTrue(game.isRunning());
                return super.read(buffer, offset, length);
            }
        });
        game.start();
        assertEquals(4, printed().split("Command: ", -1).length - 1);
        assertTrue(printed().contains("Invalid command. Type 'help'."));
        assertTrue(printed().contains("Invalid direction."));
        assertTrue(printed().contains("Thanks for playing!"));
        assertFalse(game.isRunning());
        assertEquals(1, game.getPlayerColumn());
    }

    public void testMissingInventoryEntryAndEmptySnapshot() {
        assertTrue(game.getInventory().isEmpty());
        Item missing = new Item("Missing", 0, 0);
        assertFalse(game.hasItem(missing));
        assertFalse(game.removeItem(missing));
        assertEquals(0, game.getItemCount());
    }

    public void testInteractiveLifecycleMessagesAndInvalidTurns() {
        assertTrue(game.startInteractive());
        assertTrue(game.isRunning());
        assertSame(player, game.getPlayer());
        assertEquals(100, game.getMaximumHealth());
        assertFalse(game.hasWon());
        assertEquals(0, game.getDefeatedEnemies());
        game.takeTurn(null);
        game.takeTurn("nowhere");
        game.takeTurn("north");
        game.attackTurn("east"); // Item, not an enemy.
        assertEquals(0, game.getTurns());
        game.takeTurn("east");
        assertEquals(1, game.getTurns());
        assertEquals(115, game.getMaximumHealth());
        game.getMessages().clear();
        assertFalse(game.getMessages().isEmpty());
        for (int n = 0; n < 100; n++) {
            game.processCommand("invalid");
        }
        assertEquals(80, game.getMessages().size());
        Game invalid = new Game(null);
        assertFalse(invalid.startInteractive());
        assertNull(invalid.getPlayer());
        assertEquals(0, invalid.getMaximumHealth());
        invalid.takeTurn("east");
        invalid.attackTurn(null);
        assertEquals(0, invalid.getTurns());
        assertEquals(42L, Game.newRun(42L).getRunSeed());
        assertNotSame(Game.newRun().getCurrentDungeon(), Game.newRun().getCurrentDungeon());
    }

    public void testInteractiveDeath() {
        Game weak = new Game(new Player(1, 1), new Dungeon("Danger", 1, DungeonTest.map()));
        weak.startInteractive();
        weak.takeTurn("south");
        assertFalse(weak.isRunning());
        assertEquals(0, weak.getPlayer().getHealth());
        int turns = weak.getTurns();
        weak.takeTurn("east");
        assertEquals(turns, weak.getTurns());
        assertFalse(weak.startInteractive());
    }

    public void testHeadlessLaunchDoesNotOpenWindow() {
        // Eclipse normally has a display; this branch is specific to the CLI runner.
        if (!java.awt.GraphicsEnvironment.isHeadless()) {
            return;
        }
        Game.main(null);
        assertTrue(printed().contains("Use --console"));
        assertEquals(0, game.getTurns());
    }
}
