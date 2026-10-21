package javaPackage;

/** A moving dungeon enemy. */
public class Enemy
{
    private int health, damage, x, y;

    public Enemy(int health, int damage, int x, int y)
    {
        this.health = Math.max(0, health);
        this.damage = Math.max(0, damage);
        this.x = x;
        this.y = y;
    }


    public int getHealth()
    {
        return health;
    }


    public int getDamage()
    {
        return damage;
    }


    public void takeDamage(int amount)
    {
        if (amount > 0)
            health = Math.max(0, health - amount);
    }


    public void attack(Player player)
    {
        if (player != null && !isDefeated())
            player.takeDamage(damage);
    }


    public boolean isDefeated()
    {
        return health <= 0;
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
}
