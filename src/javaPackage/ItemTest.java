package javaPackage;

import student.TestCase;

public class ItemTest extends TestCase {

    private Item item;
    private Player play;

    public void setUp() {
        item = new Item("Health Potion", 25, 5);
        play = new Player(100,10,0,0);
    }

    public void testGetName() {
        assertEquals("Health Potion", item.getName());
    }
    
    public void testApplyTo() {
        item.applyTo(play);
        assertEquals(125,play.getHealth());
        assertEquals(15,play.getDamage());
    }

    public void testInheritedMethods() {
        assertEquals(0, item.getHealth());
        assertEquals(0, item.getDamage());
        assertTrue(item.isDefeated());
    }
}

