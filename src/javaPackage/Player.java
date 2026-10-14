package javaPackage;
/**
 * A class to represent the player in the game
 * @author Hemanth Nimmagadda (nhemanth06)
 * @version 2026.10.15
 */
public class Player {
    private int health;
    private int damage;
    
    public int getHealth() {
        return health;
    }
    
    public int getDamage() {
        return damage;
    }
    
    public void takeDamage(int dmg) {
        health-=dmg;
    }
    
    public void attack(Enemy en) {
        en.takeDamage(damage);
    }
    
    public void heal(int heals) {
        health+=heals;
    }
    
    public boolean isAlive() {
        return health>0;
    }
}
