package javaPackage;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Locale;
import java.util.Random;
import javax.swing.SwingUtilities;

/**
 * Runs the desktop dungeon crawler and owns gameplay state and inventory.
 * Dungeon 1 is at the head of the LinkedChain, followed by later levels.
 */
public class Game {

    private LinkedChain<Place> places;
    private Player player;
    private LinkedChain<Item> inventory;
    private Place currentPlace;
    private int currentIndex;
    private boolean running;
    private boolean interactive;
    private boolean procedural;
    private boolean won;
    private long runSeed;
    private int maximumHealth;
    private int turns;
    private int defeatedEnemies;
    private final ArrayList<String> messages = new ArrayList<String>();
    public static final int FLOOR_COUNT = 10;

    /** Game is the entry point. Pass --console for the optional text interface. */
    public static void main(String[] args) {
        if (args != null && java.util.Arrays.asList(args).contains("--console")) {
            newRun().start();
        }
        else if (java.awt.GraphicsEnvironment.isHeadless()) {
            System.out.println("A desktop display is required. Use --console for text mode.");
        }
        else {
            SwingUtilities.invokeLater(() -> GameWindow.open(newRun()));
        }
    }

    /** Every new run has a fresh world seed. */
    public static Game newRun() {
        return newRun(new Random().nextLong());
    }

    /** A seed allows testing or replaying the same generated campaign. */
    public static Game newRun(long seed) {
        Game game = new Game(new Player(), Dungeon.generate(1, seed));
        game.procedural = true;
        game.runSeed = seed;
        return game;
    }

    /** Creates the chain of dungeon levels in the order provided. */
    public Game(Player player, Dungeon... dungeons) {
        this.places = new LinkedChain<Place>();
        this.player = player;
        this.maximumHealth = player == null ? 0 : player.getHealth();
        this.inventory = new LinkedChain<Item>();
        this.currentIndex = 0;
        this.running = false;

        if (dungeons != null) {
            for (Dungeon dungeon : dungeons) {
                if (dungeon != null) {
                    places.addToEnd(dungeon);
                }
            }
        }

        this.currentPlace = places.isEmpty() ? null : places.get(0);
        syncPlayerPosition();
    }

    /** Starts the GUI lifecycle without a blocking console-input loop. */
    public boolean startInteractive() {
        interactive = true;
        if (player == null || getCurrentDungeon() == null || !player.isAlive() || won) {
            report("Cannot start: a living player and an unfinished dungeon are required.");
            return false;
        }
        running = true;
        report("Find the stairs. Bump an enemy to attack. Every action is a turn.");
        return true;
    }

    private void report(String message) {
        messages.add(message);
        if (messages.size() > 80) {
            messages.remove(0);
        }
        if (!interactive) {
            System.out.println(message);
        }
    }

    public ArrayList<String> getMessages() {
        return new ArrayList<String>(messages);
    }

    public Player getPlayer() {
        return player;
    }

    public long getRunSeed() {
        return runSeed;
    }

    public int getMaximumHealth() {
        return maximumHealth;
    }

    public int getTurns() {
        return turns;
    }

    public int getDefeatedEnemies() {
        return defeatedEnemies;
    }

    public boolean hasWon() {
        return won;
    }

    /** Starts the optional console interface. */
    public void start() {
        if (player == null || places.isEmpty()) {
            report("Cannot start: player or dungeon missing.");
            return;
        }

        if (player.getHealth() <= 0) {
            report("Cannot start: player has no health.");
            return;
        }

        running = true;
        report("Welcome to Dungeon Crawler!");
        report("Starting in " + currentPlace.getName() + ".");
        report("Type 'help' for commands.");
        showStatus();
        collectItemAtCurrentPosition();
        showMap();

        Scanner scanner = new Scanner(System.in);
        while (isRunning()) {
            System.out.print("Command: ");

            if (!scanner.hasNextLine()) {
                running = false;
                break;
            }

            processCommand(scanner.nextLine());
        }
        // Do not close scanner: it would close System.in as well.
    }

    /** Normalizes text before Game validates the command and its arguments. */
    private String[] parseCommand(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new String[0];
        }
        return input.trim().toLowerCase(Locale.ROOT).split("\\s+");
    }

    /** Displays the active floor, handling an unloaded game gracefully. */
    private void showMap() {
        if (interactive) {
            return;
        }
        Dungeon dungeon = getCurrentDungeon();
        if (dungeon == null) {
            report("No dungeon loaded.");
            return;
        }
        dungeon.showMap();
    }

    /** Interprets a command without terminating on invalid input. */
    public void processCommand(String command) {
        String[] parts = parseCommand(command);
        if (parts.length == 0) {
            report("Please enter a command.");
            return;
        }

        String action = parts[0];
        String argument = parts.length > 1 ? parts[1] : "";
        boolean needsDirection = action.equals("move") || action.equals("attack");
        if (parts.length != (needsDirection ? 2 : 1)) {
            report("Invalid arguments. Type 'help'.");
            return;
        }

        switch (action) {
            case "help":
                report("Commands:");
                report("  move north/south/east/west");
                report("  attack north/south/east/west");
                report("  status    - Show player information");
                report("  map       - Show the current floor");
                report("  inventory - View collected items (or 'inv')");
                report("  next      - Go to the next dungeon at the exit");
                report("  quit      - Exit the game");
                break;

            case "move":
                movePlayer(argument);
                break;

            case "attack":
                attack(argument);
                break;

            case "map":
                showMap();
                break;

            case "status":
                showStatus();
                break;

            case "inventory":
            case "inv":
                showInventory();
                break;

            case "next":
                nextLayer();
                break;

            case "quit":
                running = false;
                report("Thanks for playing!");
                break;

            default:
                report("Invalid command. Type 'help'.");
        }
    }

    /** Moves the player one tile, if the dungeon allows it. */
    public void movePlayer(String direction) {
        Dungeon dungeon = getCurrentDungeon();
        if (player == null || !player.isAlive() || dungeon == null) {
            report("No active dungeon or player.");
            return;
        }

        int[] target = getTargetPosition(direction);
        if (target == null) {
            report("Invalid direction. Use north, south, east, or west.");
            return;
        }

        int newRow = target[0];
        int newColumn = target[1];

        if (!dungeon.canMoveTo(newRow, newColumn)) {
            report("You cannot move to that tile.");
            return;
        }

        Enemy occupant = dungeon.getEnemyAt(newRow, newColumn);
        if (occupant != null && !(occupant instanceof Item)
            && !occupant.isDefeated()) {
            report("An enemy blocks that tile. Attack it first.");
            return;
        }

        if (!dungeon.movePlayer(newRow, newColumn)) {
            report("You cannot move to that tile.");
            return;
        }
        syncPlayerPosition();
        report("Moved " + direction.trim().toLowerCase(Locale.ROOT) + ".");

        collectItemAtCurrentPosition();
        showMap();

        if (dungeon.isExit(getPlayerRow(), getPlayerColumn())) {
            report(interactive ? "Stairs found! Press E or Enter to descend."
                : "You found the exit! Type 'next'.");
        }
    }

    /** Attacks an enemy on the tile adjacent to the player. */
    public void attack(String direction) {
        Dungeon dungeon = getCurrentDungeon();
        if (player == null || !player.isAlive() || dungeon == null) {
            report("No active dungeon or player.");
            return;
        }

        int[] target = getTargetPosition(direction);
        if (target == null) {
            report("Invalid direction. Use north, south, east, or west.");
            return;
        }

        Enemy enemy = dungeon.getEnemyAt(target[0], target[1]);
        if (enemy == null || enemy instanceof Item || enemy.isDefeated()) {
            report("No enemy to attack in that direction.");
            return;
        }

        player.attack(enemy);
        report("Enemy health: " + enemy.getHealth());

        if (enemy.isDefeated()) {
            defeatedEnemies++;
            report("Enemy defeated!");
        }
        else {
            enemy.attack(player);
            report("Player health: " + player.getHealth());
        }

        if (player.getHealth() <= 0) {
            running = false;
            report("Game over!");
        }
    }

    /** One directional turn: move, collect loot, or attack the blocking enemy. */
    public void takeTurn(String direction) {
        takeTurn(direction, false);
    }

    /** Attacks in the facing direction without moving. */
    public void attackTurn(String direction) {
        takeTurn(direction, true);
    }

    private void takeTurn(String direction, boolean attackOnly) {
        if (!isRunning() || won) {
            return;
        }
        int[] target = getTargetPosition(direction);
        if (target == null) {
            report("Choose north, south, east, or west.");
            return;
        }
        Dungeon dungeon = getCurrentDungeon();
        Enemy enemy = dungeon.getEnemyAt(target[0], target[1]);
        boolean combat = enemy != null && !(enemy instanceof Item);
        if (attackOnly && !combat) {
            report("No enemy in that direction.");
            return;
        }
        if (!combat && !dungeon.canMoveTo(target[0], target[1])) {
            report("A wall blocks your way.");
            return;
        }
        if (combat) {
            attack(direction);
        }
        else {
            movePlayer(direction);
        }
        turns++;
        int healthBefore = player.getHealth();
        dungeon.advanceEnemies(player, combat ? enemy : null);
        if (player.getHealth() < healthBefore) {
            report("Nearby enemies dealt " + (healthBefore - player.getHealth()) + " damage.");
        }
        if (!player.isAlive()) {
            running = false;
            report("Your journey ends here. Start a new run to try again.");
        }
    }

    /** Calculates an adjacent tile without changing the player position. */
    private int[] getTargetPosition(String direction) {
        if (direction == null) {
            return null;
        }

        int row = getPlayerRow();
        int column = getPlayerColumn();

        switch (direction.trim().toLowerCase(Locale.ROOT)) {
            case "north":
            case "up":
                row--;
                break;
            case "south":
            case "down":
                row++;
                break;
            case "east":
            case "right":
                column++;
                break;
            case "west":
            case "left":
                column--;
                break;
            default:
                return null;
        }
        return new int[] {row, column};
    }

    /** Shows the player's stats and current position. */
    public void showStatus() {
        if (player == null || currentPlace == null) {
            report("No game loaded.");
            return;
        }

        report("----- Player Status -----");
        report("Health: " + player.getHealth());
        report("Damage: " + player.getDamage());
        report("Location: " + currentPlace.getName());
        report("Position: (" + getPlayerRow() + ", " + getPlayerColumn() + ")");
    }

    /** Adds an item to the Game's inventory, rejecting nulls and duplicates. */
    public boolean addItem(Item item) {
        if (item == null || inventory.contains(item)) {
            return false;
        }
        return inventory.addToEnd(item);
    }

    /** Removes an inventory entry, returning false for null or missing items. */
    public boolean removeItem(Item item) {
        return item != null && inventory.remove(item);
    }

    public int getItemCount() {
        return inventory.size();
    }

    public boolean hasItem(Item item) {
        return item != null && inventory.contains(item);
    }

    /** Returns a copy so callers cannot change the inventory directly. */
    public ArrayList<Item> getInventory() {
        ArrayList<Item> copy = new ArrayList<Item>();
        for (Item item : inventory.toArray(new Item[inventory.size()])) {
            copy.add(item);
        }
        return copy;
    }

    /** Prints all items the player has collected. */
    public void showInventory() {
        if (inventory.isEmpty()) {
            report("Inventory is empty.");
            return;
        }

        report("----- Inventory -----");
        Item[] items = inventory.toArray(new Item[inventory.size()]);
        for (int i = 0; i < items.length; i++) {
            report((i + 1) + ". " + items[i].getName());
        }
    }

    /** Collects an item located on the player's current tile. */
    private void collectItemAtCurrentPosition() {
        Dungeon dungeon = getCurrentDungeon();
        if (dungeon == null) {
            return;
        }

        Enemy occupant = dungeon.getEnemyAt(getPlayerRow(), getPlayerColumn());
        if (occupant instanceof Item) {
            Item item = (Item)occupant;
            if (addItem(item)) {
                maximumHealth = (int)Math.min(Integer.MAX_VALUE,
                    (long)maximumHealth + item.getHealthBonus());
                item.applyTo(player);
                dungeon.removeItem(item);
                report("Collected: " + item.getDescription());
            }
        }
    }

    /** Advances to the next dungeon only after reaching the exit. */
    public void nextLayer() {
        moveToNextDungeon();
    }

    /** Returns true only when the player enters another floor. */
    public boolean moveToNextDungeon() {
        Dungeon dungeon = getCurrentDungeon();
        if (player == null || !player.isAlive() || dungeon == null) {
            report("No dungeon available.");
            return false;
        }

        if (!dungeon.isExit(getPlayerRow(), getPlayerColumn())) {
            report("You must reach the exit first.");
            return false;
        }

        int limit = procedural ? FLOOR_COUNT : places.size();
        if (currentIndex + 1 >= limit) {
            for (Enemy enemy : dungeon.getEnemies().toArray(
                new Enemy[dungeon.getEnemies().size()])) {
                if (enemy.getName().equals("Warden") && enemy.isAlive()) {
                    report("Defeat the Warden before leaving the final floor.");
                    return false;
                }
            }
            report("You completed all the dungeons!");
            won = true;
            running = false;
            return false;
        }
        if (procedural && currentIndex + 1 == places.size()) {
            places.addToEnd(Dungeon.generate(currentIndex + 2, runSeed));
        }

        currentIndex++;
        currentPlace = places.get(currentIndex);

        syncPlayerPosition();

        if (procedural) {
            int recovery = Math.max(0, Math.min(18, maximumHealth - player.getHealth()));
            player.heal(recovery);
            report("A moment of shelter. Recovered " + recovery + " health.");
        }
        report("Entering " + currentPlace.getName() + "!");
        collectItemAtCurrentPosition();
        showMap();
        return true;
    }

    /** Returns the dungeon currently being explored. */
    public Dungeon getCurrentDungeon() {
        return currentPlace instanceof Dungeon
            ? (Dungeon)currentPlace : null;
    }

    /** Keeps inherited Player coordinates consistent with the active floor. */
    private void syncPlayerPosition() {
        if (player != null && getCurrentDungeon() != null) {
            player.setX(getPlayerColumn());
            player.setY(getPlayerRow());
        }
    }

    /** Returns zero when no floor is loaded. */
    public int getPlayerRow() {
        return getCurrentDungeon() == null ? 0
            : getCurrentDungeon().getPlayerPosition()[0];
    }

    public int getPlayerColumn() {
        return getCurrentDungeon() == null ? 0
            : getCurrentDungeon().getPlayerPosition()[1];
    }

    public boolean isRunning() {
        return running && player != null && player.getHealth() > 0;
    }
}
