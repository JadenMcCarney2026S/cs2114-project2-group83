package javaPackage;

import student.TestCase;

public class ItemTest extends TestCase {

    private Item item;

    public void setUp() {
        item = new Item("Health Potion", 25, 5);
    }

    public void testGetName() {
        assertEquals("Health Potion", item.getName());
    }

    public void testInheritedMethods() {
        assertEquals(0, item.getHealth());
        assertEquals(0, item.getDamage());
        assertTrue(item.isDefeated());
    }

    public void testApplyToAndBonuses() {
        Player player = new Player();
        item.applyTo(player);
        assertEquals(125, player.getHealth());
        assertEquals(15, player.getDamage());
        item.applyTo(null);
        assertEquals(25, item.getHealthBonus());
        assertEquals(5, item.getDmgBonus());
        assertTrue(item.getDescription().contains("Health Potion"));
        Item zero = new Item("Nothing", 0, 0);
        zero.applyTo(player);
        assertEquals(125, player.getHealth());
        assertEquals(0, zero.getHealthBonus());
        assertEquals(0, zero.getDmgBonus());
    }

    public void testInvalidItems() {
        for (String name : new String[] {null, "", "  "}) {
            try {
                new Item(name, 0, 0);
                fail("Name required");
            }
            catch (IllegalArgumentException expected) {
                assertNotNull(expected);
            }
        }
        try {
            new Item("Bad", -1, 0);
            fail("Negative health bonus");
        }
        catch (IllegalArgumentException expected) {
            assertNotNull(expected);
        }
        try {
            new Item("Bad", 0, -1);
            fail("Negative damage bonus");
        }
        catch (IllegalArgumentException expected) {
            assertNotNull(expected);
        }
    }
}

