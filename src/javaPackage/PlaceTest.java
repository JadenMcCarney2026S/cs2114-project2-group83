
package javaPackage;

import student.TestCase;

/**
 * Tests for the Place dungeon map class.
 *
 * @author
 * @version 2026-10-14
 */
public class PlaceTest extends TestCase {

    private Place dungeon;

    /**
     * Sets up a new dungeon before each test.
     */
    public void setUp() {
        dungeon = new Place(20, 20);
    }

    /**
     * Tests that the map is created with the correct dimensions.
     */
    public void testConstructor() {
        assertEquals(20, dungeon.getWidth());
        assertEquals(20, dungeon.getHeight());
    }

    /**
     * Tests that small maps are rejected.
     */
    public void testInvalidDimensions() {
        try {
            new Place(5, 5);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }

    /**
     * Tests whether coordinates are inside the map.
     */
    public void testIsInside() {
        assertTrue(dungeon.isInside(0, 0));
        assertTrue(dungeon.isInside(19, 19));
        assertFalse(dungeon.isInside(-1, 0));
        assertFalse(dungeon.isInside(0, -1));
        assertFalse(dungeon.isInside(20, 0));
        assertFalse(dungeon.isInside(0, 20));
    }

    /**
     * Tests tile access and map boundaries.
     */
    public void testGetTile() {
        assertNotNull(dungeon.getTile(0, 0));
        assertNotNull(dungeon.getTile(10, 10));

        try {
            dungeon.getTile(-1, 0);
            fail("Expected IndexOutOfBoundsException");
        }
        catch (IndexOutOfBoundsException e) {
            assertNotNull(e);
        }

        try {
            dungeon.getTile(20, 20);
            fail("Expected IndexOutOfBoundsException");
        }
        catch (IndexOutOfBoundsException e) {
            assertNotNull(e);
        }
    }

    /**
     * Tests setting and retrieving tiles.
     */
    public void testSetTile() {
        dungeon.setTile(5, 5, Place.Tile.PLAYER);
        assertEquals(
            Place.Tile.PLAYER,
            dungeon.getTile(5, 5)
        );

        dungeon.setTile(5, 5, Place.Tile.TREASURE);
        assertEquals(
            Place.Tile.TREASURE,
            dungeon.getTile(5, 5)
        );
    }

    /**
     * Tests that null tiles are rejected.
     */
    public void testSetNullTile() {
        try {
            dungeon.setTile(5, 5, null);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }

    /**
     * Tests that setting a tile outside the map fails.
     */
    public void testSetTileOutsideMap() {
        try {
            dungeon.setTile(-1, 0, Place.Tile.FLOOR);
            fail("Expected IndexOutOfBoundsException");
        }
        catch (IndexOutOfBoundsException e) {
            assertNotNull(e);
        }
    }

    /**
     * Tests walkability of tiles.
     */
    public void testIsWalkable() {
        // A wall is not walkable.
        dungeon.setTile(1, 1, Place.Tile.WALL);
        assertFalse(dungeon.isWalkable(1, 1));

        // A floor is walkable.
        dungeon.setTile(2, 2, Place.Tile.FLOOR);
        assertTrue(dungeon.isWalkable(2, 2));

        // Other non-wall tiles are walkable.
        dungeon.setTile(3, 3, Place.Tile.TREASURE);
        assertTrue(dungeon.isWalkable(3, 3));

        dungeon.setTile(4, 4, Place.Tile.EXIT);
        assertTrue(dungeon.isWalkable(4, 4));

        // Outside the map is not walkable.
        assertFalse(dungeon.isWalkable(-1, 0));
        assertFalse(dungeon.isWalkable(20, 20));
    }

    /**
     * Tests the tile symbols.
     */
    public void testTileSymbols() {
        assertEquals('■', Place.Tile.WALL.getSymbol());
        assertEquals(' ', Place.Tile.FLOOR.getSymbol());
        assertEquals('웃', Place.Tile.PLAYER.getSymbol());
        assertEquals('敵', Place.Tile.ENEMY.getSymbol());
        assertEquals('$', Place.Tile.TREASURE.getSymbol());
        assertEquals('X', Place.Tile.EXIT.getSymbol());
    }

    /**
     * Tests that printing the map does not throw an exception.
     */
    public void testPrintMap() {
        dungeon.setTile(1, 1, Place.Tile.PLAYER);
        dungeon.printMap();
        assertEquals(20, dungeon.getHeight());
    }

    public void testNameAndPosition() {
        assertEquals("Dungeon", dungeon.getName());
        assertEquals(0, dungeon.getPosition());
        dungeon.setPosition(2);
        assertEquals(2, dungeon.getPosition());
        try {
            dungeon.setPosition(-1);
            fail("Negative position");
        }
        catch (IllegalArgumentException expected) {
            assertEquals(2, dungeon.getPosition());
        }
    }

    public void testSetMap() {
        Place.Tile[][] map = new Place.Tile[20][20];
        for (Place.Tile[] row : map) {
            java.util.Arrays.fill(row, Place.Tile.FLOOR);
        }
        dungeon.setMap(map);
        map[0][0] = Place.Tile.WALL;
        assertEquals(Place.Tile.FLOOR, dungeon.getTile(0, 0));
        Place.Tile[][][] invalid = {null, new Place.Tile[10][10],
            new Place.Tile[20][], new Place.Tile[20][20]};
        for (Place.Tile[][] bad : invalid) {
            try {
                dungeon.setMap(bad);
                fail("Invalid map");
            }
            catch (IllegalArgumentException expected) {
                assertEquals(Place.Tile.FLOOR, dungeon.getTile(0, 0));
            }
        }
    }
}
