package javaPackage;

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Random;

/** Builds and displays floors, and stores their terrain, occupants, and position. */
public class Dungeon extends Place {
    private LinkedChain<Enemy> enemies;
    private final int layerNumber;
    private int row;
    private int column;

    /** A playable floor needs a rectangular map, one player, and one exit. */
    public Dungeon(String name, int layerNumber, Tile[][] map) {
        super(mapWidth(map), map.length);
        if (layerNumber < 1) {
            throw new IllegalArgumentException("Layer number must be positive.");
        }
        setName(name);
        this.layerNumber = layerNumber;
        setPosition(layerNumber - 1);
        setMap(map);
    }

    private static int mapWidth(Tile[][] map) {
        if (map == null || map.length == 0 || map[0] == null) {
            throw new IllegalArgumentException("A map is required.");
        }
        return map[0].length;
    }

    /** Validates the complete replacement before changing the current floor. */
    @Override
    public void setMap(Tile[][] map) {
        int players = 0;
        int exits = 0;
        if (map == null || map.length != getHeight()) {
            throw new IllegalArgumentException("Map height does not match.");
        }
        for (Tile[] tiles : map) {
            if (tiles == null || tiles.length != getWidth()) {
                throw new IllegalArgumentException("Map must be rectangular.");
            }
            for (Tile tile : tiles) {
                if (tile == null) {
                    throw new IllegalArgumentException("Tile cannot be null.");
                }
                players += tile == Tile.PLAYER ? 1 : 0;
                exits += tile == Tile.EXIT ? 1 : 0;
            }
        }
        if (players != 1 || exits != 1) {
            throw new IllegalArgumentException("Map needs one player and one exit.");
        }
        super.setMap(map);
        enemies = new LinkedChain<Enemy>();
        for (int r = 0; r < getHeight(); r++) {
            for (int c = 0; c < getWidth(); c++) {
                Tile tile = getTile(c, r);
                if (tile == Tile.PLAYER) {
                    row = r;
                    column = c;
                    super.setTile(c, r, Tile.FLOOR);
                }
                else if (tile == Tile.ENEMY) {
                    enemies.addToEnd(new Enemy(20, 5, c, r));
                }
                else if (tile == Tile.TREASURE) {
                    Item item = new Item("Floor " + layerNumber + " potion", 15, 5);
                    item.setX(c);
                    item.setY(r);
                    enemies.addToEnd(item);
                }
            }
        }
    }

    /** Checks terrain; living enemies are checked separately by movePlayer. */
    public boolean canMoveTo(int row, int col) {
        return isWalkable(col, row);
    }

    /** Returns a living enemy or uncollected item, or null for an empty tile. */
    public Enemy getEnemyAt(int row, int col) {
        if (!isInside(col, row)) {
            return null;
        }
        for (Enemy enemy : enemies.toArray(new Enemy[enemies.size()])) {
            if (enemy.getX() == col && enemy.getY() == row
                && (enemy instanceof Item || !enemy.isDefeated())) {
                return enemy;
            }
        }
        return null;
    }

    public boolean isExit(int row, int col) {
        return isInside(col, row) && getTile(col, row) == Tile.EXIT;
    }

    /** Only adjacent, accessible, unblocked moves change the position. */
    public boolean movePlayer(int row, int col) {
        if (!canMoveTo(row, col)
            || Math.abs((long)row - this.row)
                + Math.abs((long)col - column) != 1) {
            return false;
        }
        Enemy occupant = getEnemyAt(row, col);
        if (occupant != null && !(occupant instanceof Item)) {
            return false;
        }
        this.row = row;
        column = col;
        return true;
    }

    public int[] getPlayerPosition() {
        return new int[] {row, column};
    }

    /** A snapshot protects the chain's membership from outside modification. */
    public LinkedChain<Enemy> getEnemies() {
        LinkedChain<Enemy> copy = new LinkedChain<Enemy>();
        for (Enemy enemy : enemies.toArray(new Enemy[enemies.size()])) {
            copy.addToEnd(enemy);
        }
        return copy;
    }

    /** Removes a collected item through the chain's public API. */
    public boolean removeItem(Item item) {
        if (item == null || !enemies.remove(item)) {
            return false;
        }
        super.setTile(item.getX(), item.getY(), Tile.FLOOR);
        return true;
    }

    public int getLayerNumber() {
        return layerNumber;
    }

    /** Renders current state without leaving old player or defeated-enemy markers. */
    public String[] getMap() {
        char[][] symbols = new char[getHeight()][getWidth()];
        for (int r = 0; r < getHeight(); r++) {
            for (int c = 0; c < getWidth(); c++) {
                Tile tile = getTile(c, r);
                symbols[r][c] = (tile == Tile.ENEMY || tile == Tile.TREASURE)
                    ? Tile.FLOOR.getSymbol() : tile.getSymbol();
            }
        }
        for (Enemy enemy : enemies.toArray(new Enemy[enemies.size()])) {
            if (enemy instanceof Item || !enemy.isDefeated()) {
                symbols[enemy.getY()][enemy.getX()] = enemy instanceof Item
                    ? Tile.TREASURE.getSymbol() : Tile.ENEMY.getSymbol();
            }
        }
        symbols[row][column] = Tile.PLAYER.getSymbol();
        String[] result = new String[getHeight()];
        for (int r = 0; r < result.length; r++) {
            result[r] = new String(symbols[r]);
        }
        return result;
    }

    @Override
    public void printMap() {
        for (String line : getMap()) {
            System.out.println(line);
        }
    }
    /** Builds a fresh ten-floor campaign; Game normally generates floors on entry. */
    public static Dungeon[] createDungeons() {
        long seed = new Random().nextLong();
        Dungeon[] floors = new Dungeon[10];
        for (int i = 0; i < floors.length; i++) {
            floors[i] = generate(i + 1, seed);
        }
        return floors;
    }

    /** Seeded generation makes runs reproducible while every floor has a new layout. */
    public static Dungeon generate(int layer, long seed) {
        if (layer < 1 || layer > 100) {
            throw new IllegalArgumentException("Generated layer must be 1 through 100.");
        }
        Random random = new Random(seed ^ (0x9E3779B97F4A7C15L * layer));
        int width = 43 + Math.min(12, layer / 2 * 2);
        int height = 29 + Math.min(6, layer / 3 * 2);
        Tile[][] tiles = new Tile[height][width];
        for (Tile[] line : tiles) {
            java.util.Arrays.fill(line, Tile.WALL);
        }
        ArrayList<int[]> rooms = new ArrayList<int[]>();
        for (int attempt = 0; attempt < 200 && rooms.size() < 8 + layer / 2; attempt++) {
            int w = 5 + random.nextInt(6);
            int h = 4 + random.nextInt(4);
            int x = 1 + random.nextInt(width - w - 2);
            int y = 1 + random.nextInt(height - h - 2);
            boolean overlap = false;
            for (int[] room : rooms) {
                if (x <= room[0] + room[2] && x + w >= room[0]
                    && y <= room[1] + room[3] && y + h >= room[1]) {
                    overlap = true;
                }
            }
            if (overlap) {
                continue;
            }
            for (int r = y; r < y + h; r++) {
                for (int c = x; c < x + w; c++) {
                    tiles[r][c] = Tile.FLOOR;
                }
            }
            int centerX = x + w / 2;
            int centerY = y + h / 2;
            if (!rooms.isEmpty()) {
                int[] previous = rooms.get(rooms.size() - 1);
                int oldX = previous[0] + previous[2] / 2;
                int oldY = previous[1] + previous[3] / 2;
                if (random.nextBoolean()) {
                    corridor(tiles, oldX, oldY, centerX, oldY);
                    corridor(tiles, centerX, oldY, centerX, centerY);
                }
                else {
                    corridor(tiles, oldX, oldY, oldX, centerY);
                    corridor(tiles, oldX, centerY, centerX, centerY);
                }
            }
            rooms.add(new int[] {x, y, w, h});
        }
        int[] first = rooms.get(0);
        int startX = first[0] + first[2] / 2;
        int startY = first[1] + first[3] / 2;
        // The farthest reachable tile makes every exit meaningful and reachable.
        int[][] distances = distances(tiles, startY, startX);
        int exitX = startX;
        int exitY = startY;
        ArrayList<int[]> candidates = new ArrayList<int[]>();
        for (int r = 1; r < height - 1; r++) {
            for (int c = 1; c < width - 1; c++) {
                if (distances[r][c] > distances[exitY][exitX]) {
                    exitY = r;
                    exitX = c;
                }
                if (distances[r][c] >= 4) {
                    candidates.add(new int[] {r, c});
                }
            }
        }
        tiles[startY][startX] = Tile.PLAYER;
        tiles[exitY][exitX] = Tile.EXIT;
        Collections.shuffle(candidates, random);
        int placed = 0;
        for (int[] cell : candidates) {
            if (tiles[cell[0]][cell[1]] == Tile.FLOOR && placed < 5 + layer) {
                tiles[cell[0]][cell[1]] = placed < 3 + layer / 4
                    ? Tile.TREASURE : Tile.ENEMY;
                placed++;
            }
        }
        Dungeon dungeon = new Dungeon("Dungeon " + layer, layer, tiles);
        int enemyNumber = 0;
        int itemNumber = 0;
        LinkedChain<Enemy> populated = new LinkedChain<Enemy>();
        for (Enemy occupant : dungeon.enemies.toArray(new Enemy[dungeon.enemies.size()])) {
            Enemy replacement;
            if (occupant instanceof Item) {
                boolean weapon = itemNumber++ % 3 == 1;
                replacement = new Item(weapon ? "Tempered blade" : "Vitality flask",
                    weapon ? 0 : 20 + layer * 2, weapon ? 3 : 0);
                replacement.setX(occupant.getX());
                replacement.setY(occupant.getY());
            }
            else {
                String[] names = {"Slime", "Sentinel", "Bat"};
                String name = layer == 10 && enemyNumber == 0 ? "Warden"
                    : names[enemyNumber % names.length];
                boolean boss = name.equals("Warden");
                replacement = new Enemy(name, boss ? 100 : 14 + layer * 3,
                    boss ? 12 : 3 + layer / 3, occupant.getX(), occupant.getY());
                enemyNumber++;
            }
            populated.addToEnd(replacement);
        }
        dungeon.enemies = populated;
        return dungeon;
    }

    private static void corridor(Tile[][] map, int x1, int y1, int x2, int y2) {
        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
            map[y1][x] = Tile.FLOOR;
        }
        for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
            map[y][x2] = Tile.FLOOR;
        }
    }

    private static int[][] distances(Tile[][] map, int row, int col) {
        int[][] result = new int[map.length][map[0].length];
        for (int[] line : result) {
            java.util.Arrays.fill(line, -1);
        }
        ArrayDeque<int[]> queue = new ArrayDeque<int[]>();
        queue.add(new int[] {row, col});
        result[row][col] = 0;
        int[][] offsets = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        while (!queue.isEmpty()) {
            int[] cell = queue.remove();
            for (int[] offset : offsets) {
                int r = cell[0] + offset[0];
                int c = cell[1] + offset[1];
                if (r >= 0 && r < map.length && c >= 0 && c < map[0].length
                    && map[r][c] != Tile.WALL && result[r][c] == -1) {
                    result[r][c] = result[cell[0]][cell[1]] + 1;
                    queue.add(new int[] {r, c});
                }
            }
        }
        return result;
    }

    /** Enemies pursue nearby players through corridors, one tile per valid turn. */
    public void advanceEnemies(Player player, Enemy alreadyFought) {
        if (player == null || !player.isAlive()) {
            return;
        }
        Tile[][] terrain = new Tile[getHeight()][getWidth()];
        for (int r = 0; r < getHeight(); r++) {
            for (int c = 0; c < getWidth(); c++) {
                terrain[r][c] = getTile(c, r);
            }
        }
        int[][] distance = distances(terrain, row, column);
        int[][] offsets = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (Enemy enemy : enemies.toArray(new Enemy[enemies.size()])) {
            if (!player.isAlive()) {
                break;
            }
            if (enemy instanceof Item || enemy.isDefeated() || enemy == alreadyFought) {
                continue;
            }
            int d = distance[enemy.getY()][enemy.getX()];
            if (d == 1) {
                enemy.attack(player);
            }
            else if (d > 1 && d <= 8) {
                for (int[] offset : offsets) {
                    int r = enemy.getY() + offset[0];
                    int c = enemy.getX() + offset[1];
                    if (isInside(c, r) && distance[r][c] == d - 1
                        && !isExit(r, c) && getEnemyAt(r, c) == null) {
                        enemy.setY(r);
                        enemy.setX(c);
                        break;
                    }
                }
            }
        }
    }

    /** Displays this floor's title, current map, and legend. */
    public void showMap() {
        System.out.println("----- " + getName() + " -----");
        printMap();
        System.out.println("웃 = player, ■ = wall, 敵 = enemy, $ = item, X = exit");
    }
}
