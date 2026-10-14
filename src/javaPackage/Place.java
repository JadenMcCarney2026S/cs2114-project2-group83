
package javaPackage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// -------------------------------------------------------------------------
/**
 *  The place class
 * 
 *  @author Jaden
 *  @version Oct 14, 2026
 */
public class Place {

    public enum Tile {
        WALL('#'),
        FLOOR(' '),
        PLAYER('@'),
        ENEMY('E'),
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

    private final int width;
    private final int height;
    private Tile[][] map;
    private final Random random = new Random();

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

    private void generateDungeon() {
        // Create several rectangular rooms.
        int roomCount = Math.max(3, (width * height) / 150);
        List<int[]> roomCenters = new ArrayList<>();

        for (int i = 0; i < roomCount; i++) {
            int roomWidth = random.nextInt(5) + 4;
            int roomHeight = random.nextInt(4) + 4;

            if (roomWidth >= width - 2 ||
                roomHeight >= height - 2) {
                continue;
            }

            int x = random.nextInt(width - roomWidth - 1) + 1;
            int y = random.nextInt(height - roomHeight - 1) + 1;

            carveRoom(x, y, roomWidth, roomHeight);

            int centerX = x + roomWidth / 2;
            int centerY = y + roomHeight / 2;
            roomCenters.add(new int[]{centerX, centerY});

            // Connect each room to the previous room.
            if (roomCenters.size() > 1) {
                int[] previous =
                    roomCenters.get(roomCenters.size() - 2);

                carveCorridor(
                    previous[0], previous[1],
                    centerX, centerY
                );
            }
        }

        // Ensure there's at least one walkable tile.
        if (roomCenters.isEmpty()) {
            map[height / 2][width / 2] = Tile.FLOOR;
        }

        // Place an exit in the last room.
        int[] lastCenter = roomCenters.isEmpty()
            ? new int[]{width / 2, height / 2}
            : roomCenters.get(roomCenters.size() - 1);

        map[lastCenter[1]][lastCenter[0]] = Tile.EXIT;
    }

    private void carveRoom(
        int x, int y, int roomWidth, int roomHeight
    ) {
        for (int row = y; row < y + roomHeight; row++) {
            for (int col = x; col < x + roomWidth; col++) {
                map[row][col] = Tile.FLOOR;
            }
        }
    }

    private void carveCorridor(
        int x1, int y1, int x2, int y2
    ) {
        // Horizontal, then vertical.
        for (int x = Math.min(x1, x2);
             x <= Math.max(x1, x2); x++) {
            map[y1][x] = Tile.FLOOR;
        }

        for (int y = Math.min(y1, y2);
             y <= Math.max(y1, y2); y++) {
            map[y][x2] = Tile.FLOOR;
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
    
}