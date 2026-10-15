
package javaPackage;

/**
 * Represents the player in the dungeon crawler.
 */
public class Player {
    private int health;
    private int damage;
    private int x;
    private int y;

    public Player(int hp, int dmg, int x, int y) {
        if (hp <= 0 || dmg < 0) {
            throw new IllegalArgumentException(
                "Health must be positive and damage cannot be negative."
            );
        }
        this.health = hp;
        this.damage = dmg;
        this.x = x;
        this.y = y;
    }

    public Player() {
        this(100, 10, 1, 1);
    }

    public int getHealth() {
        return health;
    }

    public int getDamage() {
        return damage;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void increaseHealth(int amount) {
        if (amount > 0) {
            health += amount;
        }
    }

    public void increaseDamage(int amount) {
        if (amount > 0) {
            damage += amount;
        }
    }

    public void takeDamage(int amount) {
        if (amount > 0) {
            health = Math.max(0, health - amount);
        }
    }

    public void attack(Enemy enemy) {
        if (enemy != null && !enemy.isDefeated()) {
            enemy.takeDamage(damage);
        }
    }

    public void heal(int amount) {
        increaseHealth(amount);
    }

    public boolean isAlive() {
        return health > 0;
    }
}
