package javaPackage;

import java.util.Scanner;
/**
 * A class that holds all of the game logic
 * @author Hemanth Nimmagadda (nhemanth06)
 * @version 2026.10.22
 */
public class GameAlt {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        // Game Logic
        System.out.println("---Game Starting---");
        boolean exit = false;
        while (!exit) {
          //Initialization of Level 1
            LinkedChain<Enemy> level1 = new LinkedChain<Enemy>();
            Item med = new Item("Medallion", 0, 5);
            Enemy skel1 = new Enemy(20,5,0,0, "Skeleton");
            Enemy skel2 = new Enemy(20,5,0,0, "Skeleton");
            Enemy skel3 = new Enemy(20,5,0,0, "Skeleton");
            level1.add(skel3);
            level1.add(med);
            level1.add(skel2);
            level1.add(skel1);
            //Initialization of Level 2
            LinkedChain<Enemy> level2 = new LinkedChain<Enemy>();
            Item elix = new Item("Elixir", 30, 0);
            Enemy ogre1 = new Enemy(50, 10, 0, 0, "Ogre");
            Enemy wiz1 = new Enemy(40, 25, 0, 0, "Wizard");
            Enemy skel4 = new Enemy(20,5,0,0, "Skeleton");
            Enemy skel5 = new Enemy(20,5,0,0, "Skeleton");
            level2.add(wiz1);
            level2.add(elix);
            level2.add(ogre1);
            level2.add(skel5);
            level2.add(skel4);
          //Initialization of Level 3
            LinkedChain<Enemy> level3 = new LinkedChain<Enemy>();
            Item saber = new Item("Mythic Saber", 10, 30);
            Enemy guard1 = new Enemy(100,40,0,0, "Guardian");
            Enemy guard2 = new Enemy(100,40,0,0, "Guardian");
            Enemy eldguard = new Enemy(200, 50, 0, 0, "Elder Guardian");
            Enemy wiz2 = new Enemy(40, 25, 0, 0, "Wizard");
            Enemy skel6 = new Enemy(20,5,0,0, "Skeleton");
            level3.add(eldguard);
            level3.add(saber);
            level3.add(guard1);
            level3.add(guard2);
            level3.add(med);
            level3.add(wiz2);
            level3.add(skel6);
            //Level Select
            System.out.print("Which level would you like to play (1-3): ");
            int levelnum = scan.nextInt();
            LinkedChain<Enemy> level;
            if (levelnum==1) {
                level = level1;
            } else if (levelnum==2) {
               level = level2;
            } else if (levelnum==3) {
                level = level3;
            } else {
                System.out.println("---Game Closing---");
                break;
            }
            //Player Initialization
            System.out.print("Enter the player's health: ");
            int hp = scan.nextInt();
            System.out.print("Enter the player's damage: ");
            int dmg = scan.nextInt();
            Player p1 = new Player(hp, dmg, 0, 0);
            System.out.println("");
            //Level Logic
            while (!level.isEmpty()) {
                Enemy curr = level.remove();
                if (curr.getClass()==Item.class) {
                    Item it = (Item) curr;
                    System.out.println("You found a " + it.getName() + "!");
                    it.applyTo(p1);
                    System.out.println("Your health is now: " + p1.getHealth());
                    System.out.println("Your damage is now: " + p1.getDamage());
                    System.out.print("Press any key to continue: ");
                    String cont = "";
                    while (cont.equals("")) {
                        cont = scan.next();
                    }
                    System.out.println("");
                    System.out.println("Proceeding...");
                    System.out.println("");
                }
                if (curr.getClass()==Enemy.class) {
                    Enemy enemy = (Enemy) curr;
                    System.out.println("There is a " + enemy.getName() + " in your path!");
                    while (!enemy.isDefeated() && p1.isAlive()) {
                        System.out.println("Your health is now: " + p1.getHealth());
                        System.out.println("The " + enemy.getName() +"'s health is now: " + enemy.getHealth());
                        System.out.print("Press any key to continue the battle: ");
                        String cont = "";
                        while (cont.equals("")) {
                            cont = scan.next();
                        }
                        System.out.println("");
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
                    System.out.print("Press any key to continue: ");
                    String cont = "";
                    while (cont.equals("")) {
                        cont = scan.next();
                    }
                    System.out.println("");
                    System.out.println("Proceeding...");
                    System.out.println("");
                }
            }
            boolean fail = false;
            int score = 100-p1.getHealth();
            if (score<0 || !p1.isAlive()) {
                score = 0;
                fail = true;
            }
            if (fail) {
                System.out.println("Level Failed!");
            } else {
                System.out.println("Level Complete!");
            }
            System.out.println("Score: " + score);
            System.out.println("");
        }
    }
}
