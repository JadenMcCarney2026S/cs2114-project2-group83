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
}

