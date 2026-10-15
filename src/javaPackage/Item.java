package javaPackage;

public class Item extends Enemy {

    private String name;
    private int healthBonus;
    private int damageBonus;

    public Item(String name, int healthBonus, int damageBonus) {
        super(0, 0, 0, 0);

        if (name == null || name.trim().isEmpty()
            || healthBonus < 0 || damageBonus < 0) {
            throw new IllegalArgumentException("Item needs a name and nonnegative bonuses.");
        }
        this.name = name;
        this.healthBonus = healthBonus;
        this.damageBonus = damageBonus;
    }

    public void applyTo(Player player) {
        if (player == null) {
            return;
        }
        player.increaseHealth(healthBonus);
        player.increaseDamage(damageBonus);
    }

    public int getHealthBonus() {
        return healthBonus;
    }

    public int getDmgBonus() {
        return damageBonus;
    }

    public String getDescription() {
        return name + " (health +" + healthBonus + ", damage +" + damageBonus + ")";
    }

    public String getName() {
        return name;
    }
}
