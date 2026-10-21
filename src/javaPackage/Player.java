package javaPackage;

/** Represents the player and their currently equipped weapon. */
public class Player
{
    private int health, damage, x, y;
    private Item equippedWeapon;

    public Player(int hp, int dmg, int x, int y)
    {
        if (hp <= 0 || dmg < 0)
            throw new IllegalArgumentException(
                "Health must be positive and damage cannot be negative.");
        health = hp;
        damage = dmg;
        this.x = x;
        this.y = y;
    }


    public Player()
    {
        this(100, 10, 1, 1);
    }


    public int getHealth()
    {
        return health;
    }


    /**
     * Returns weapon damage when a weapon is equipped, otherwise base damage.
     */
    public int getDamage()
    {
        return equippedWeapon == null ? damage : equippedWeapon.getDamage();
    }


    public int getBaseDamage()
    {
        return damage;
    }


    public Item getEquippedWeapon()
    {
        return equippedWeapon;
    }


    public void equipWeapon(Item weapon)
    {
        if (weapon == null || weapon.getType() != Item.Type.WEAPON)
            throw new IllegalArgumentException(
                "An equipped item must be a weapon.");
        equippedWeapon = weapon;
    }


    public int getX()
    {
        return x;
    }


    public int getY()
    {
        return y;
    }


    public void setX(int x)
    {
        this.x = x;
    }


    public void setY(int y)
    {
        this.y = y;
    }


    public void increaseHealth(int amount)
    {
        if (amount > 0)
            health += amount;
    }


    public void increaseDamage(int amount)
    {
        if (amount > 0)
            damage += amount;
    }


    public void takeDamage(int amount)
    {
        if (amount > 0)
            health = Math.max(0, health - amount);
    }


    public void attack(Enemy enemy)
    {
        if (enemy != null && !enemy.isDefeated())
            enemy.takeDamage(getDamage());
    }


    public void heal(int amount)
    {
        increaseHealth(amount);
    }


    public boolean isAlive()
    {
        return health > 0;
    }
}
