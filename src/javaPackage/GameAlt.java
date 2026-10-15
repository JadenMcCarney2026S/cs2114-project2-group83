package javaPackage;

import java.util.Scanner;

public class GameAlt {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter the player's health: ");
        int hp = scan.nextInt();
        System.out.print("Enter the player's damage: ");
        int dmg = scan.nextInt();
        Player p1 = new Player(hp, dmg, 0, 0);
        LinkedChain<Enemy> level1 = new LinkedChain<Enemy>();
        Item med = new Item("Medallion", 10, 5);
        Enemy en = new Enemy(20,5,0,0);
        level1.add(en);
        level1.add(med);
        level1.add(p1);
        System.out.println("---Game Starting---");
        while (!level1.isEmpty()) {
            Enemy curr = level1.remove();
            if (curr.getClass()==Item.class) {
                Item it = (Item) curr;
                System.out.println("You found a " + it.getName() + "!");
                it.applyTo(p1);
                System.out.println("New health: " + p1.getHealth());
                System.out.println("New damage: " + p1.getDamage());
                System.out.println("Proceeding...");
            }
            if (curr.getClass()==Enemy.class) {
                Enemy enemy = (Enemy) curr;
                System.out.println("There is an enemy in your path!");
                while (!enemy.isDefeated() && p1.isAlive()) {
                    p1.attack(enemy);
                    enemy.attack(p1);
                }
                if (!p1.isAlive()) {
                    System.out.println("You were defeated in battle! :(");
                    break;
                } else {
                    System.out.println("The enemy has been vanquished.");
                    System.out.println("Your health is now: " + p1.getHealth());
                }
                System.out.println("Proceeding...");
            }
        }
        int score = 100-p1.getHealth();
        if (score<0 || !p1.isAlive()) {
            score = 0;
        }
        System.out.println("Final Score: " + score);
    }
}
