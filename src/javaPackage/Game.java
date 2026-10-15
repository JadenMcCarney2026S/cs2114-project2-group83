
package javaPackage;

import java.util.Scanner;

/**
 * Main game loop for the dungeon crawler.
 */
public class Game {

    private final Place place;
    private final Player player;

    // The same generic LinkedChain implementation stores both types.
    private final LinkedChain<Enemy> enemies;
    private final LinkedChain<Item> inventory;

    private boolean running;
    private boolean won;

    public Game() {
        place = new Place(12, 12);
        player = new Player(100, 10, 1, 1);
        enemies = new LinkedChain<Enemy>();
        inventory = new LinkedChain<Item>();

        running = true;
        won = false;

        createDungeon();
    }

    /**
     * Sets up a small dungeon for the playable prototype.
     */
    private void createDungeon() {
        Place.Tile[][] map = new Place.Tile[12][12];

        // Start with walls.
        for (int y = 0; y < 12; y++) {
            for (int x = 0; x < 12; x++) {
                map[y][x] = Place.Tile.WALL;
            }
        }

        // Carve a connected, walkable path.
        for (int x = 1; x <= 10; x++) {
            map[1][x] = Place.Tile.FLOOR;
            map[2][x] = Place.Tile.FLOOR;
            map[3][x] = Place.Tile.FLOOR;
        }

        for (int y = 2; y <= 10; y++) {
            map[y][3] = Place.Tile.FLOOR;
            map[y][4] = Place.Tile.FLOOR;
            map[y][8] = Place.Tile.FLOOR;
            map[y][9] = Place.Tile.FLOOR;
        }

        for (int x = 3; x <= 9; x++) {
            map[8][x] = Place.Tile.FLOOR;
            map[9][x] = Place.Tile.FLOOR;
            map[10][x] = Place.Tile.FLOOR;
        }

        map[player.getY()][player.getX()] = Place.Tile.PLAYER;
        map[2][5] = Place.Tile.TREASURE;
        map[8][8] = Place.Tile.TREASURE;
        map[3][9] = Place.Tile.ENEMY;
        map[9][5] = Place.Tile.ENEMY;
        map[10][9] = Place.Tile.EXIT;

        place.setMap(map);

        enemies.addToEnd(new Enemy(25, 8, 9, 3));
        enemies.addToEnd(new Enemy(35, 12, 5, 9));
    }

    /**
     * Runs until the player wins, loses, or quits.
     */
    public void play() {
        Scanner input = new Scanner(System.in);

        System.out.println("=== DUNGEON CRAWLER ===");
        System.out.println("W/A/S/D: Move");
        System.out.println("Q: Quit");
        System.out.println("Move into an enemy to attack it.");

        while (running && player.isAlive() && !won) {
            place.printMap();

            System.out.println(
                "Health: " + player.getHealth()
                + " | Damage: " + player.getDamage()
                + " | Items: " + inventory.size()
            );

            System.out.print("Your move: ");

            if (!input.hasNextLine()) {
                running = false;
                break;
            }

            String command = input.nextLine().trim()
                .toLowerCase();

            if (command.equals("q")) {
                running = false;
                break;
            }

            int dx = 0;
            int dy = 0;

            switch (command) {
                case "w":
                    dy = -1;
                    break;
                case "s":
                    dy = 1;
                    break;
                case "a":
                    dx = -1;
                    break;
                case "d":
                    dx = 1;
                    break;
                default:
                    System.out.println(
                        "Invalid command. Use W, A, S, D, or Q."
                    );
                    continue;
            }

            takeTurn(dx, dy);
        }

        if (won) {
            System.out.println("You escaped the dungeon. You win!");
        }
        else if (!player.isAlive()) {
            System.out.println("You were defeated. Game over.");
        }
        else {
            System.out.println("Game ended.");
        }

        input.close();
    }

    /**
     * Processes one movement and its associated interactions.
     */
    public void takeTurn(int dx, int dy) {
        if (!running || !player.isAlive() || won) {
            return;
        }

        // Reject anything other than one cardinal step.
        if (Math.abs(dx) + Math.abs(dy) != 1) {
            System.out.println("Invalid movement.");
            return;
        }

        int nextX = player.getX() + dx;
        int nextY = player.getY() + dy;

        if (!place.isInside(nextX, nextY)
            || place.getTile(nextX, nextY) == Place.Tile.WALL) {
            System.out.println("You cannot move there.");
            return;
        }

        Place.Tile destination = place.getTile(nextX, nextY);

        if (destination == Place.Tile.ENEMY) {
            attackEnemyAt(nextX, nextY);
            return;
        }

        // Clear the player's old tile.
        place.setTile(
            player.getX(), player.getY(), Place.Tile.FLOOR
        );

        player.setX(nextX);
        player.setY(nextY);

        if (destination == Place.Tile.TREASURE) {
            collectTreasure();
        }
        else if (destination == Place.Tile.EXIT) {
            won = true;
        }

        place.setTile(nextX, nextY, Place.Tile.PLAYER);
    }

    /**
     * Finds an enemy at the target coordinates and fights it.
     */
    private void attackEnemyAt(int x, int y) {
        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);

            if (enemy.getX() == x && enemy.getY() == y
                && !enemy.isDefeated()) {

                player.attack(enemy);

                System.out.println(
                    "You attack the enemy for "
                    + player.getDamage() + " damage."
                );

                if (enemy.isDefeated()) {
                    enemies.remove(enemy);
                    place.setTile(x, y, Place.Tile.FLOOR);
                    System.out.println("Enemy defeated!");
                }
                else {
                    enemy.attack(player);
                    System.out.println(
                        "Enemy attacks you for "
                        + enemy.getDamage() + " damage."
                    );
                }

                return;
            }
        }

        // Keep the map and enemy storage consistent if no enemy
        // is found at the displayed enemy location.
        place.setTile(x, y, Place.Tile.FLOOR);
    }

    /**
     * Creates and applies a treasure item.
     */
    private void collectTreasure() {
        Item treasure = new Item("Dungeon treasure", 20, 5);

        treasure.applyTo(player);
        inventory.addToEnd(treasure);

        System.out.println(
            "Treasure collected! +20 health and +5 damage."
        );
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.play();
    }
}
