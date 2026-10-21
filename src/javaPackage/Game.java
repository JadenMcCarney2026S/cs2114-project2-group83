package javaPackage;

import java.util.Random;
import java.util.Scanner;

/**
 * Main game loop with multiple floors, linked-chain inventory, weapons and
 * moving enemies.
 */
public class Game
{
    private Place place;
    private final Player player;
    private final LinkedChain<Enemy> enemies;
    private final LinkedChain<Item> inventory;
    private final Random random = new Random();
    private int floor;
    private boolean running;
    private boolean won;

    public Game()
    {
        player = new Player(100, 5, 1, 1);
        enemies = new LinkedChain<Enemy>();
        inventory = new LinkedChain<Item>();
        floor = 1;
        running = true;
        won = false;
        inventory.addToEnd(new Item("Rusty sword", Item.Type.WEAPON, 0, 12));
        player.equipWeapon(inventory.get(0));
        createFloor();
    }


    private void createFloor()
    {
        int size = floor == 1 ? 50 : floor == 2 ? 75 : 100;
        place = new Place(size, size);
        enemies.clear();
        // A clear entrance and connected central route.
        for (int x = 1; x < size - 1; x++)
            place.setTile(x, 1, Place.Tile.FLOOR);
        for (int y = 1; y < size - 1; y++)
            place.setTile(size / 2, y, Place.Tile.FLOOR);
        for (int x = size / 2; x < size - 1; x++)
            place.setTile(x, size - 2, Place.Tile.FLOOR);
        player.setX(1);
        player.setY(1);
        place.setTile(1, 1, Place.Tile.PLAYER);
        place.setTile(size - 2, size - 2, Place.Tile.EXIT);
        int enemyCount = 8 + floor * 4;
        for (int i = 0; i < enemyCount; i++)
        {
            int x, y;
            do
            {
                x = 2 + random.nextInt(size - 4);
                y = 2 + random.nextInt(size - 4);
            }
            while (place.getTile(x, y) != Place.Tile.FLOOR
                || (Math.abs(x - 1) + Math.abs(y - 1) < 6));
            Enemy e = new Enemy(18 + floor * 8, 3 + floor * 2, x, y);
            enemies.addToEnd(e);
            place.setTile(x, y, Place.Tile.ENEMY);
        }
        // Several guaranteed items along the route.
        place.setTile(size / 2, 2, Place.Tile.TREASURE);
        place.setTile(size / 2, size / 2, Place.Tile.TREASURE);
        place.setTile(size - 3, size - 2, Place.Tile.TREASURE);
    }


    public void play()
    {
        Scanner input = new Scanner(System.in);
        System.out.println("=== DUNGEON CRAWLER ===");
        System.out.println("W/A/S/D move | I inventory | Q quit");
        System.out.println("Walk into enemies to attack. Reach X to descend.");
        while (running && player.isAlive() && !won)
        {
            printNearbyMap();
            System.out.println(
                "Floor " + floor + "/3 | HP " + player.getHealth()
                    + " | Damage " + player.getDamage() + " | Items "
                    + inventory.size() + " | Weapon: "
                    + (player.getEquippedWeapon() == null
                        ? "none"
                        : player.getEquippedWeapon().getName()));
            System.out.print("Command: ");
            if (!input.hasNextLine())
                break;
            String command = input.nextLine().trim().toLowerCase();
            if (command.equals("q"))
            {
                running = false;
                break;
            }
            if (command.equals("i"))
            {
                openInventory(input);
                continue;
            }
            int dx = 0, dy = 0;
            switch (command)
            {
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
                    System.out.println("Use W/A/S/D, I, or Q.");
                    continue;
            }
            takeTurn(dx, dy);
        }
        if (won)
            System.out.println("You conquered all three dungeon floors!");
        else if (!player.isAlive())
            System.out.println("You were defeated. Game over.");
        else
            System.out.println("Game ended.");
    }


    /** Prints a 21x21 viewport to keep large floors playable in a terminal. */
    private void printNearbyMap()
    {
        int radius = 10;
        for (int y = Math.max(0, player.getY() - radius); y <= Math
            .min(place.getHeight() - 1, player.getY() + radius); y++)
        {
            StringBuilder line = new StringBuilder();
            for (int x = Math.max(0, player.getX() - radius); x <= Math
                .min(place.getWidth() - 1, player.getX() + radius); x++)
                line.append(place.getTile(x, y).getSymbol());
            System.out.println(line);
        }
        System.out.println(
            "(Map view centered near player; full floor is " + place.getWidth()
                + "x" + place.getHeight() + ")");
    }


    public void takeTurn(int dx, int dy)
    {
        if (!running || !player.isAlive() || won
            || Math.abs(dx) + Math.abs(dy) != 1)
            return;
        int nx = player.getX() + dx, ny = player.getY() + dy;
        if (!place.isInside(nx, ny) || place.getTile(nx, ny) == Place.Tile.WALL)
        {
            System.out.println("You cannot move there.");
            moveEnemies();
            return;
        }
        Place.Tile dest = place.getTile(nx, ny);
        if (dest == Place.Tile.ENEMY)
        {
            attackEnemyAt(nx, ny);
            moveEnemies();
            return;
        }
        place.setTile(player.getX(), player.getY(), Place.Tile.FLOOR);
        player.setX(nx);
        player.setY(ny);
        if (dest == Place.Tile.TREASURE)
            collectTreasure();
        if (dest == Place.Tile.EXIT)
        {
            if (floor == 3)
                won = true;
            else
            {
                floor++;
                System.out.println("You descend to floor " + floor + "!");
                createFloor();
                return;
            }
        }
        place.setTile(nx, ny, Place.Tile.PLAYER);
        moveEnemies();
    }


    private void attackEnemyAt(int x, int y)
    {
        for (int i = 0; i < enemies.size(); i++)
        {
            Enemy e = enemies.get(i);
            if (e.getX() == x && e.getY() == y && !e.isDefeated())
            {
                int dealt = player.getDamage();
                player.attack(e);
                System.out.println("You deal " + dealt + " damage.");
                if (e.isDefeated())
                {
                    enemies.remove(e);
                    place.setTile(x, y, Place.Tile.FLOOR);
                    System.out.println("Enemy defeated!");
                    if (random.nextBoolean())
                    {
                        Item drop =
                            new Item("Healing potion", Item.Type.POTION, 20, 0);
                        inventory.addToEnd(drop);
                        System.out
                            .println("The enemy dropped a healing potion.");
                    }
                }
                else
                {
                    e.attack(player);
                    System.out
                        .println("Enemy hits you for " + e.getDamage() + ".");
                }
                return;
            }
        }
    }


    private void collectTreasure()
    {
        Item item;
        int choice = random.nextInt(3);
        if (choice == 0)
            item = new Item("Iron sword", Item.Type.WEAPON, 0, 18 + floor * 2);
        else if (choice == 1)
            item = new Item("Healing potion", Item.Type.POTION, 30, 0);
        else
            item = new Item("War hammer", Item.Type.WEAPON, 0, 25 + floor * 2);
        inventory.addToEnd(item);
        System.out.println("Found: " + item + ". Press I to use/equip it.");
    }


    private void openInventory(Scanner input)
    {
        System.out.println("--- Inventory ---");
        if (inventory.isEmpty())
            System.out.println("(empty)");
        for (int i = 0; i < inventory.size(); i++)
            System.out.println(
                (i + 1) + ". " + inventory.get(i)
                    + (inventory.get(i) == player.getEquippedWeapon()
                        ? " [EQUIPPED]"
                        : ""));
        System.out.println(
            "Enter item number to equip/use, or press Enter to close:");
        String line = input.nextLine().trim();
        if (line.isEmpty())
            return;
        try
        {
            int index = Integer.parseInt(line) - 1;
            Item item = inventory.get(index);
            if (item.getType() == Item.Type.WEAPON)
            {
                player.equipWeapon(item);
                System.out.println(
                    "Equipped " + item.getName() + ". Damage is now "
                        + player.getDamage() + ".");
            }
            else if (item.getType() == Item.Type.POTION)
            {
                item.applyTo(player);
                inventory.remove(item);
                System.out
                    .println("Used potion. Health: " + player.getHealth());
            }
            else
            {
                item.applyTo(player);
            }
        }
        catch (NumberFormatException | IndexOutOfBoundsException ex)
        {
            System.out.println("Invalid inventory selection.");
        }
    }


    private void moveEnemies()
    {
        for (int i = 0; i < enemies.size(); i++)
        {
            Enemy e = enemies.get(i);
            if (e.isDefeated())
                continue;
            int dx = player.getX() - e.getX(), dy = player.getY() - e.getY();
            if (Math.abs(dx) + Math.abs(dy) == 1)
            {
                e.attack(player);
                System.out.println(
                    "An enemy attacks you for " + e.getDamage() + " damage!");
                continue;
            }
            // Pursue when close; otherwise wander randomly.
            int mx = 0, my = 0;
            if (Math.abs(dx) + Math.abs(dy) <= 8)
            {
                if (Math.abs(dx) >= Math.abs(dy))
                    mx = Integer.signum(dx);
                else
                    my = Integer.signum(dy);
            }
            else
            {
                int direction = random.nextInt(4);
                if (direction == 0)
                    my = -1;
                else if (direction == 1)
                    my = 1;
                else if (direction == 2)
                    mx = -1;
                else
                    mx = 1;
            }
            int tx = e.getX() + mx, ty = e.getY() + my;
            if (place.isInside(tx, ty)
                && place.getTile(tx, ty) == Place.Tile.FLOOR)
            {
                place.setTile(e.getX(), e.getY(), Place.Tile.FLOOR);
                e.setX(tx);
                e.setY(ty);
                place.setTile(tx, ty, Place.Tile.ENEMY);
            }
        }
    }


    public static void main(String[] args)
    {
        new Game().play();
    }
}
