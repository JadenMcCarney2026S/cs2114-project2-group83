package javaPackage;

public class Item extends Enemy {

    private String name;
    private int healthBonus;
    private int damageBonus;

    public Item(String name, int healthBonus, int damageBonus) {
        super(0, 0);

        this.name = name;
        this.healthBonus = healthBonus;
        this.damageBonus = damageBonus;
    }

    public void applyTo(Player player) {
        player.increaseHealth(healthBonus);
        player.increaseDamage(damageBonus);
    }

    public String getName() {
        return name;
    }
}
