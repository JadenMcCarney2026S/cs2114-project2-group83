package javaPackage;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import student.TestCase;

public class DungeonTest extends TestCase {
    private Dungeon dungeon;
    private PrintStream originalOut;
    private ByteArrayOutputStream output;

    /** A short playable route with an optional enemy to its south. */
    static Place.Tile[][] map() {
        Place.Tile[][] map = new Place.Tile[10][10];
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                map[r][c] = Place.Tile.WALL;
            }
        }
        map[1][1] = Place.Tile.PLAYER;
        map[1][2] = Place.Tile.TREASURE;
        map[1][3] = Place.Tile.EXIT;
        map[2][1] = Place.Tile.ENEMY;
        return map;
    }

    public void tearDown() {
        System.setOut(originalOut);
    }

    public void setUp() {
        originalOut = System.out;
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
        dungeon = new Dungeon("Test floor", 1, map());
    }

    public void testConstructorAndMetadata() {
        assertEquals("Test floor", dungeon.getName());
        assertEquals(1, dungeon.getLayerNumber());
        assertEquals(0, dungeon.getPosition());
        assertEquals(10, dungeon.getWidth());
        assertEquals(10, dungeon.getHeight());
        assertInvalid(null, 1, map());
        assertInvalid("x", 0, map());
        assertInvalid("x", 1, null);
        assertInvalid("x", 1, new Place.Tile[0][]);
        assertInvalid("x", 1, new Place.Tile[5][5]);
    }

    private void assertInvalid(String name, int layer, Place.Tile[][] map) {
        try {
            new Dungeon(name, layer, map);
            fail("Expected invalid dungeon to be rejected");
        }
        catch (IllegalArgumentException expected) {
            assertNotNull(expected);
        }
    }

    public void testCanMoveTo() {
        assertTrue(dungeon.canMoveTo(1, 2));
        assertFalse(dungeon.canMoveTo(0, 1));
        assertFalse(dungeon.canMoveTo(-1, 1));
        assertFalse(dungeon.canMoveTo(10, 1));
    }

    public void testGetEnemyAt() {
        assertNotNull(dungeon.getEnemyAt(2, 1));
        assertTrue(dungeon.getEnemyAt(1, 2) instanceof Item);
        assertNull(dungeon.getEnemyAt(1, 3));
        assertNull(dungeon.getEnemyAt(-1, 0));
        dungeon.getEnemyAt(2, 1).takeDamage(100);
        assertNull(dungeon.getEnemyAt(2, 1));
    }

    public void testIsExit() {
        assertTrue(dungeon.isExit(1, 3));
        assertFalse(dungeon.isExit(1, 1));
        assertFalse(dungeon.isExit(Integer.MAX_VALUE, -1));
    }

    public void testMovePlayerAndPosition() {
        assertFalse(dungeon.movePlayer(2, 1)); // Living enemy.
        assertFalse(dungeon.movePlayer(1, 3)); // Nonadjacent.
        assertFalse(dungeon.movePlayer(0, 1)); // Wall.
        assertFalse(dungeon.movePlayer(-1, Integer.MIN_VALUE));
        assertEquals(1, dungeon.getPlayerPosition()[1]);
        assertTrue(dungeon.movePlayer(1, 2));
        assertEquals(2, dungeon.getPlayerPosition()[1]);
        int[] position = dungeon.getPlayerPosition();
        position[1] = -100;
        assertEquals(2, dungeon.getPlayerPosition()[1]);
    }

    public void testGetEnemiesAndRemoveItem() {
        LinkedChain<Enemy> snapshot = dungeon.getEnemies();
        assertEquals(2, snapshot.size());
        assertTrue(snapshot.contains(dungeon.getEnemyAt(1, 2)));
        snapshot.clear();
        assertEquals(2, dungeon.getEnemies().size());
        Item item = (Item)dungeon.getEnemyAt(1, 2);
        assertFalse(dungeon.removeItem(null));
        assertFalse(dungeon.removeItem(new Item("missing", 0, 0)));
        assertTrue(dungeon.removeItem(item));
        assertFalse(dungeon.removeItem(item));
        assertNull(dungeon.getEnemyAt(1, 2));
        assertEquals(1, dungeon.getEnemies().size());
        dungeon.getEnemies().clear();
        assertEquals(1, dungeon.getEnemies().size());
    }

    public void testGetMapAndPrintMap() {
        assertEquals(Place.Tile.PLAYER.getSymbol(), dungeon.getMap()[1].charAt(1));
        assertEquals(Place.Tile.ENEMY.getSymbol(), dungeon.getMap()[2].charAt(1));
        String[] map = dungeon.getMap();
        map[1] = null;
        assertNotNull(dungeon.getMap()[1]);
        dungeon.getEnemyAt(2, 1).takeDamage(100);
        dungeon.movePlayer(2, 1);
        assertEquals(' ', dungeon.getMap()[1].charAt(1));
        assertEquals(Place.Tile.PLAYER.getSymbol(), dungeon.getMap()[2].charAt(1));
        dungeon.printMap();
    }

    public void testSetMap() {
        Place.Tile[][] map = map();
        dungeon.setMap(map);
        map[1][2] = Place.Tile.WALL;
        assertTrue(dungeon.canMoveTo(1, 2));
        Place.Tile[][][] invalid = {null, new Place.Tile[10][], map(), map(), map()};
        invalid[2][1][1] = Place.Tile.FLOOR;
        invalid[3][1][3] = Place.Tile.FLOOR;
        invalid[4][1][2] = null;
        for (Place.Tile[][] bad : invalid) {
            try {
                dungeon.setMap(bad);
                fail("Expected invalid map to be rejected");
            }
            catch (IllegalArgumentException expected) {
                assertEquals(2, dungeon.getEnemies().size());
                assertEquals(1, dungeon.getPlayerPosition()[1]);
            }
        }
    }
    public void testCreateDungeons() {
        Dungeon[] floors = Dungeon.createDungeons();
        assertEquals(10, floors.length);
        assertEquals(43, floors[0].getWidth());
        assertEquals(45, floors[1].getWidth());
        assertEquals(2, floors[1].getLayerNumber());
        assertNotSame(floors[0], Dungeon.createDungeons()[0]);
        assertTrue(floors[0].canMoveTo(floors[0].getPlayerPosition()[0],
            floors[0].getPlayerPosition()[1]));
        assertTrue(floors[1].canMoveTo(floors[1].getPlayerPosition()[0],
            floors[1].getPlayerPosition()[1]));
    }

    public void testShowMap() {
        Dungeon.createDungeons()[0].showMap();
        assertTrue(output.toString().contains("----- Dungeon 1 -----"));
        assertTrue(output.toString().contains("웃 = player"));
        assertFalse(output.toString().contains("Dungeon 2"));
    }

    /** Walks every occupant and exit across a generated campaign without terrain edits. */
    public void testGeneratedCampaignIsPlayable() {
        Dungeon[] floors = new Dungeon[10];
        for (int i = 0; i < floors.length; i++) {
            floors[i] = Dungeon.generate(i + 1, 2114L);
        }
        // Isolate map connectivity from combat balancing in this route test.
        Player player = new Player(10000, 100);
        Game game = new Game(player, floors);
        int expectedItems = 0;
        for (Dungeon dungeon : floors) {
            for (Enemy occupant : dungeon.getEnemies().toArray(new Enemy[dungeon.getEnemies().size()])) {
                if (occupant instanceof Item) {
                    expectedItems++;
                }
            }
            assertSame(dungeon, game.getCurrentDungeon());
            // Every generated occupant must be reachable.
            for (Enemy enemy : dungeon.getEnemies().toArray(
                new Enemy[dungeon.getEnemies().size()])) {
                walkTo(game, enemy.getY(), enemy.getX());
            }
            int exitRow = -1;
            int exitCol = -1;
            for (int r = 0; r < dungeon.getHeight(); r++) {
                for (int c = 0; c < dungeon.getWidth(); c++) {
                    if (dungeon.isExit(r, c)) {
                        exitRow = r;
                        exitCol = c;
                    }
                }
            }
            walkTo(game, exitRow, exitCol);
            assertTrue(player.isAlive());
            if (dungeon != floors[floors.length - 1]) {
                assertTrue(game.moveToNextDungeon());
            }
            else {
                assertFalse(game.moveToNextDungeon());
            }
        }
        assertEquals(expectedItems, game.getItemCount());
        assertTrue(output.toString().contains("Entering Dungeon 2"));
        assertTrue(output.toString().contains("completed all"));
    }

    private void walkTo(Game game, int targetRow, int targetCol) {
        Dungeon dungeon = game.getCurrentDungeon();
        int width = dungeon.getWidth();
        int start = game.getPlayerRow() * width + game.getPlayerColumn();
        int target = targetRow * width + targetCol;
        int[] previous = new int[width * dungeon.getHeight()];
        java.util.Arrays.fill(previous, -1);
        ArrayDeque<Integer> queue = new ArrayDeque<Integer>();
        previous[start] = start;
        queue.add(start);
        int[][] offsets = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        while (!queue.isEmpty() && previous[target] == -1) {
            int cell = queue.remove();
            for (int[] offset : offsets) {
                int r = cell / width + offset[0];
                int c = cell % width + offset[1];
                if (dungeon.canMoveTo(r, c) && previous[r * width + c] == -1) {
                    previous[r * width + c] = cell;
                    queue.add(r * width + c);
                }
            }
        }
        assertTrue("Target must be reachable: " + targetRow + "," + targetCol,
            previous[target] != -1);
        ArrayList<Integer> route = new ArrayList<Integer>();
        for (int cell = target; cell != start; cell = previous[cell]) {
            route.add(cell);
        }
        Collections.reverse(route);
        for (int cell : route) {
            int r = cell / width;
            int c = cell % width;
            String direction = r < game.getPlayerRow() ? "north"
                : r > game.getPlayerRow() ? "south"
                : c < game.getPlayerColumn() ? "west" : "east";
            Enemy occupant = dungeon.getEnemyAt(r, c);
            int attacks = 0;
            while (occupant != null && !(occupant instanceof Item)
                && !occupant.isDefeated()) {
                assertTrue("Combat must finish", ++attacks < 10);
                game.processCommand("attack " + direction);
            }
            game.processCommand("move " + direction);
            assertEquals(r, game.getPlayerRow());
            assertEquals(c, game.getPlayerColumn());
        }
    }

    public void testEmptyOccupantsAndRendering() {
        Place.Tile[][] empty = map();
        empty[1][2] = Place.Tile.FLOOR;
        empty[2][1] = Place.Tile.FLOOR;
        dungeon.setMap(empty);
        assertTrue(dungeon.getEnemies().isEmpty());
        assertNull(dungeon.getEnemyAt(2, 1));
        assertFalse(dungeon.removeItem(new Item("Missing", 0, 0)));
        assertTrue(dungeon.movePlayer(2, 1));
        assertEquals(2, dungeon.getPlayerPosition()[0]);
        assertEquals(1, dungeon.getPlayerPosition()[1]);
        assertEquals(' ', dungeon.getMap()[1].charAt(2));
        output.reset();
        dungeon.printMap();
        assertTrue(output.toString().contains(dungeon.getMap()[2]));
        output.reset();
        dungeon.showMap();
        assertTrue(output.toString().contains("----- Test floor -----"));
        assertEquals(1, dungeon.getLayerNumber());
    }

    /** Many seeds and all floor depths must produce connected, nonoverlapping content. */
    public void testGeneratedMapsAcrossSeeds() {
        for (long seed = 0; seed < 30; seed++) {
            for (int layer = 1; layer <= Game.FLOOR_COUNT; layer++) {
                Dungeon floor = Dungeon.generate(layer, seed);
                int[] start = floor.getPlayerPosition();
                boolean[][] reached = new boolean[floor.getHeight()][floor.getWidth()];
                ArrayDeque<int[]> queue = new ArrayDeque<int[]>();
                queue.add(start);
                reached[start[0]][start[1]] = true;
                int[][] offsets = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
                while (!queue.isEmpty()) {
                    int[] cell = queue.remove();
                    for (int[] offset : offsets) {
                        int r = cell[0] + offset[0];
                        int c = cell[1] + offset[1];
                        if (floor.canMoveTo(r, c) && !reached[r][c]) {
                            reached[r][c] = true;
                            queue.add(new int[] {r, c});
                        }
                    }
                }
                int exits = 0;
                for (int r = 0; r < floor.getHeight(); r++) {
                    for (int c = 0; c < floor.getWidth(); c++) {
                        if (floor.canMoveTo(r, c)) {
                            assertTrue("Every room must connect", reached[r][c]);
                        }
                        if (floor.isExit(r, c)) {
                            exits++;
                        }
                        if (r == 0 || c == 0 || r == floor.getHeight() - 1
                            || c == floor.getWidth() - 1) {
                            assertFalse(floor.canMoveTo(r, c));
                        }
                    }
                }
                assertEquals(1, exits);
                assertNull(floor.getEnemyAt(start[0], start[1]));
                boolean[][] occupied = new boolean[floor.getHeight()][floor.getWidth()];
                int items = 0;
                int enemies = 0;
                for (Enemy actor : floor.getEnemies().toArray(new Enemy[floor.getEnemies().size()])) {
                    assertFalse(occupied[actor.getY()][actor.getX()]);
                    occupied[actor.getY()][actor.getX()] = true;
                    assertTrue(reached[actor.getY()][actor.getX()]);
                    assertFalse(floor.isExit(actor.getY(), actor.getX()));
                    if (actor instanceof Item) { items++; }
                    else { enemies++; }
                }
                assertTrue(items >= 3);
                assertTrue(enemies >= 3);
            }
        }
    }

    public void testGenerationSeedsAndBadLayers() {
        String same = java.util.Arrays.toString(Dungeon.generate(1, 42).getMap());
        assertEquals(same, java.util.Arrays.toString(Dungeon.generate(1, 42).getMap()));
        assertFalse(same.equals(java.util.Arrays.toString(Dungeon.generate(1, 43).getMap())));
        assertFalse(same.equals(java.util.Arrays.toString(Dungeon.generate(2, 42).getMap())));
        for (int layer : new int[] {0, -1, 101}) {
            try {
                Dungeon.generate(layer, 42);
                fail("Bad layer");
            }
            catch (IllegalArgumentException expected) {
                assertNotNull(expected);
            }
        }
    }

    public void testLazyCampaignGenerationAndVictory() {
        Game campaign = Game.newRun(2114L);
        campaign.getPlayer().increaseHealth(10000);
        campaign.getPlayer().increaseDamage(100);
        campaign.startInteractive();
        for (int layer = 1; layer <= Game.FLOOR_COUNT; layer++) {
            Dungeon floor = campaign.getCurrentDungeon();
            assertEquals(layer, floor.getLayerNumber());
            assertEquals(java.util.Arrays.toString(Dungeon.generate(layer, 2114L).getMap()),
                java.util.Arrays.toString(floor.getMap()));
            for (Enemy actor : floor.getEnemies().toArray(new Enemy[floor.getEnemies().size()])) {
                walkTo(campaign, actor.getY(), actor.getX());
            }
            for (int r = 0; r < floor.getHeight(); r++) {
                for (int c = 0; c < floor.getWidth(); c++) {
                    if (floor.isExit(r, c)) {
                        walkTo(campaign, r, c);
                    }
                }
            }
            assertEquals(layer < Game.FLOOR_COUNT, campaign.moveToNextDungeon());
        }
        assertTrue(campaign.hasWon());
        assertFalse(campaign.isRunning());
        assertTrue(campaign.getDefeatedEnemies() > 0);
    }

    public void testEnemyTurnsRespectWallsItemsAndNullPlayer() {
        dungeon.advanceEnemies(null, null);
        Player player = new Player();
        Enemy enemy = dungeon.getEnemyAt(2, 1);
        dungeon.advanceEnemies(player, enemy); // Already retaliated this turn.
        assertEquals(100, player.getHealth());
        dungeon.advanceEnemies(player, null);
        assertEquals(95, player.getHealth());
        enemy.takeDamage(100);
        dungeon.advanceEnemies(player, null);
        assertEquals(95, player.getHealth());
        assertNotNull(dungeon.getEnemyAt(1, 2)); // Items never move or attack.
        player.takeDamage(1000);
        dungeon.advanceEnemies(player, null);
        assertEquals(0, player.getHealth());
    }

    public void testPursuitMovesOnceAndDoesNotOccupyItems() {
        Place.Tile[][] path = map();
        path[2][1] = Place.Tile.WALL;
        path[1][2] = Place.Tile.FLOOR;
        path[1][3] = Place.Tile.ENEMY;
        path[1][4] = Place.Tile.EXIT;
        Dungeon chase = new Dungeon("Chase", 1, path);
        Enemy hunter = chase.getEnemyAt(1, 3);
        Player player = new Player();
        chase.advanceEnemies(player, null);
        assertSame(hunter, chase.getEnemyAt(1, 2));
        assertEquals(100, player.getHealth());
        chase.advanceEnemies(player, null);
        assertEquals(95, player.getHealth());
        path[1][2] = Place.Tile.TREASURE;
        chase.setMap(path);
        chase.advanceEnemies(player, null);
        assertNotNull(chase.getEnemyAt(1, 3));
        assertTrue(chase.getEnemyAt(1, 2) instanceof Item);
    }

    public void testWardenMustBeDefeatedToWin() {
        Dungeon finalFloor = Dungeon.generate(10, 2114L);
        Game finalGame = new Game(new Player(10000, 100), finalFloor);
        finalGame.startInteractive();
        Enemy warden = null;
        for (Enemy enemy : finalFloor.getEnemies().toArray(new Enemy[finalFloor.getEnemies().size()])) {
            if (enemy.getName().equals("Warden")) { warden = enemy; }
        }
        assertNotNull(warden);
        for (int r = 0; r < finalFloor.getHeight(); r++) {
            for (int c = 0; c < finalFloor.getWidth(); c++) {
                if (finalFloor.isExit(r, c)) { walkTo(finalGame, r, c); }
            }
        }
        assertTrue(warden.isAlive());
        assertFalse(finalGame.moveToNextDungeon());
        assertFalse(finalGame.hasWon());
        assertTrue(finalGame.isRunning());
        assertTrue(finalGame.getMessages().get(finalGame.getMessages().size() - 1)
            .contains("Defeat the Warden"));
        warden.takeDamage(1000);
        assertFalse(finalGame.moveToNextDungeon());
        assertTrue(finalGame.hasWon());
        assertFalse(finalGame.isRunning());
        assertFalse(finalGame.startInteractive());
    }
}
