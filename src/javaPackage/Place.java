
package javaPackage;


// -------------------------------------------------------------------------
/**
 *  The place class
 * 
 *  @author Jaden
 *  @version Oct 14, 2026
 */
public class Place {

    public enum Tile {
        WALL('■'),
        FLOOR(' '),
        PLAYER('웃'),
        ENEMY('敵'),
        TREASURE('$'),
        EXIT('X');

        private final char symbol;

        Tile(char symbol) {
            this.symbol = symbol;
        }

        public char getSymbol() {
            return symbol;
        }
    }

    private String name = "Dungeon";
    private int position;

    private final int width;
    private final int height;
    private Tile[][] map;

    public Place(int width, int height) {
        if (width < 10 || height < 10) {
            throw new IllegalArgumentException(
                "Map dimensions must be at least 10x10."
            );
        }

        this.width = width;
        this.height = height;
        this.map = new Tile[height][width];

        fillWithWalls();
    }

    private void fillWithWalls() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                map[y][x] = Tile.WALL;
            }
        }
    }

    public boolean isInside(int x, int y) {
        return x >= 0 && x < width
            && y >= 0 && y < height;
    }

    public boolean isWalkable(int x, int y) {
        return isInside(x, y)
            && map[y][x] != Tile.WALL;
    }

    public Tile getTile(int x, int y) {
        if (!isInside(x, y)) {
            throw new IndexOutOfBoundsException(
                "Coordinates outside the dungeon."
            );
        }

        return map[y][x];
    }

    public void setTile(int x, int y, Tile tile) {
        if (!isInside(x, y)) {
            throw new IndexOutOfBoundsException(
                "Coordinates outside the dungeon."
            );
        }

        if (tile == null) {
            throw new IllegalArgumentException(
                "Tile cannot be null."
            );
        }

        map[y][x] = tile;
    }

    public void printMap() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.out.print(map[y][x].getSymbol());
            }
            System.out.println();
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    
    public String getName() {
        return name;
    }

    protected void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required.");
        }
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        if (position < 0) {
            throw new IllegalArgumentException("Position cannot be negative.");
        }
        this.position = position;
    }

    public void setMap(Tile[][] map) {
        if (map == null || map.length != height) {
            throw new IllegalArgumentException("Map height does not match.");
        }
        Tile[][] copy = new Tile[height][width];
        for (int row = 0; row < height; row++) {
            if (map[row] == null || map[row].length != width) {
                throw new IllegalArgumentException("Map must be rectangular.");
            }
            for (int col = 0; col < width; col++) {
                if (map[row][col] == null) {
                    throw new IllegalArgumentException("Tile cannot be null.");
                }
                copy[row][col] = map[row][col];
            }
        }
        this.map = copy;
    }
    
}