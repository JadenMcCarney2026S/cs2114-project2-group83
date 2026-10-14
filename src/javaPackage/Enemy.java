package javaPackage;

public class Enemy {

    private int health;
    private int damage;

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
}

