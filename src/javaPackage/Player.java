package javaPackage;

/**
 * A class to represent the player in the game
 * 
 * @author Hemanth Nimmagadda (nhemanth06)
 * @version 2026.10.15
 */
public class Player extends Enemy {

    public Player(int hp, int dmg, int x, int y) {
        super(hp, dmg, x, y, "Player");
    }


    public Player() {
        super(100, 10, 0, 0, "Player");
    }


    public void increaseHealth(int hp) {
        health += hp;
    }


    public void increaseDamage(int dmg) {
        damage += dmg;
    }


    public void takeDamage(int dmg) {
        health -= dmg;
    }


    public void attack(Enemy en) {
        en.takeDamage(damage);
    }


    public void heal(int heals) {
        health += heals;
    }


    public boolean isAlive() {
        return health > 0;
    }
}
