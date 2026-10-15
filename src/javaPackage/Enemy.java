package javaPackage;

public class Enemy {

    private final String name;
    private int health;
    private int damage;
    private int x;
    private int y;

    public Enemy(int health, int damage, int x, int y) {
        this("Enemy", health, damage, x, y);
    }

    public Enemy(String name, int health, int damage, int x, int y) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Enemy name is required.");
        }
        this.name = name;
        this.health = Math.max(0, health);
        this.damage = Math.max(0, damage);
        setX(x);
        setY(y);
    }

    public Enemy(int health, int damage) {
        this(health, damage, 0, 0);
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public int getDamage() {
        return damage;
    }

    public void takeDamage(int amount) {
        if (amount <= 0) {
            return;
        }
        health = health - amount;

        if (health < 0) {
            health = 0;
        }
    }

    public void attack(Player player) {
        if (player != null && !isDefeated()) {
            player.takeDamage(damage);
        }
    }

    public boolean isDefeated() {
        return health <= 0;
    }
    
    /** Positive bonuses change the same stats returned by the getters. */
    protected void addHealth(int amount) {
        if (amount > 0) {
            health = (int)Math.min(Integer.MAX_VALUE, (long)health + amount);
        }
    }

    protected void addDamage(int amount) {
        if (amount > 0) {
            damage = (int)Math.min(Integer.MAX_VALUE, (long)damage + amount);
        }
    }

    public boolean isAlive() {
        return !isDefeated();
    }

    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public void setX(int x) {
        if (x < 0) {
            throw new IllegalArgumentException("Column cannot be negative.");
        }
        this.x = x;
    }
    
    public void setY(int y) {
        if (y < 0) {
            throw new IllegalArgumentException("Row cannot be negative.");
        }
        this.y = y;
    }
}

