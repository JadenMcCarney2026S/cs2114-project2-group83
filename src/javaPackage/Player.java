package javaPackage;
/**
 * A class to represent the player in the game
 * @author Hemanth Nimmagadda (nhemanth06)
 * @version 2026.10.15
 */
public class Player extends Enemy{

    
    public Player(int hp, int dmg, int x, int y) {
        super(hp, dmg, x, y);
    }
    
    public Player(int hp, int dmg) {
        this(hp, dmg, 0, 0);
    }

    public Player() {
        super(100,10, 0, 0);
    }
    
    public void increaseHealth(int hp) {
        addHealth(hp);
    }
   
    public void increaseDamage(int dmg) {
        addDamage(dmg);
    }
    
    public void takeDamage(int dmg) {
        super.takeDamage(dmg);
    }
    
    public void attack(Enemy en) {
        if (en != null && isAlive()) {
            en.takeDamage(getDamage());
        }
    }
    
    public void heal(int heals) {
        increaseHealth(heals);
    }
    
    public boolean isAlive() {
        return getHealth() > 0;
    }
}
