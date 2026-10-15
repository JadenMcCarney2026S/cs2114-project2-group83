package javaPackage;

public class Enemy {

    private int health;
    private int damage;
    private int x;
    private int y;

    public Enemy(int health, int damage) {
        this.health = health;
        this.damage = damage;
    }

    public int getHealth() {
        return health;
    }

    public int getDamage() {
        return damage;
    }

    public void takeDamage(int amount) {
        health = health - amount;

        if (health < 0) {
            health = 0;
        }
    }

    public void attack(Player player) {
        if (!isDefeated()) {
            player.takeDamage(damage);
        }
    }

    public boolean isDefeated() {
        return health <= 0;
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
}

