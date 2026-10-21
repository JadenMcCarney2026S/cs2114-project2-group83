package javaPackage;

/**
 * An inventory item; weapons set attack damage and consumables restore health.
 */
public class Item
{
    public enum Type
    {
        WEAPON,
        POTION,
        TREASURE
    }

    private final String name;
    private final Type type;
    private final int healthBonus;
    private final int damage;

    public Item(String name, int healthBonus, int damageBonus)
    {
        this(name, Type.TREASURE, healthBonus, damageBonus);
    }


    public Item(String name, Type type, int healthBonus, int damage)
    {
        if (name == null || type == null)
            throw new IllegalArgumentException();
        this.name = name;
        this.type = type;
        this.healthBonus = healthBonus;
        this.damage = damage;
    }


    public String getName()
    {
        return name;
    }


    public Type getType()
    {
        return type;
    }


    public int getHealthBonus()
    {
        return healthBonus;
    }


    public int getDamage()
    {
        return damage;
    }


    public void applyTo(Player player)
    {
        if (player != null && healthBonus > 0)
            player.heal(healthBonus);
    }


    public String toString()
    {
        if (type == Type.WEAPON)
            return name + " (weapon, " + damage + " damage)";
        if (type == Type.POTION)
            return name + " (heals " + healthBonus + ")";
        return name + (healthBonus > 0 ? " (+" + healthBonus + " health)" : "");
    }
}
