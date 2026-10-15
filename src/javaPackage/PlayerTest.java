package javaPackage;

import student.TestCase;

public class PlayerTest extends TestCase {
    private Player play1;
    private Player play2;
    private Enemy enem;
    
    public void setUp() {
        play1 = new Player();
        play2 = new Player(150, 25);
        enem = new Enemy(20,5);
    }
    
    public void testGetHealth() {
        assertEquals(100, play1.getHealth());
        assertEquals(150, play2.getHealth());
    }
    
    public void testGetDamage() {
        assertEquals(10, play1.getDamage());
        assertEquals(25, play2.getDamage());
    }
    
    public void testIncreaseHealth() {
        play1.increaseHealth(10);
        assertEquals(110, play1.getHealth());
    }
    
    public void testIncreaseDamage() {
        play1.increaseDamage(5);
        assertEquals(15, play1.getDamage());
    }
    
    public void testTakeDamage() {
        play1.takeDamage(8);
        assertEquals(92,play1.getHealth());
    }
    
    public void testAttack() {
        play1.attack(enem);
        assertEquals(10, enem.getHealth());
    }
    
    public void testHeal() {
        play1.heal(30);
        assertEquals(130, play1.getHealth());
    }
    
    public void testIsAlive() {
        assertTrue(play1.isAlive());
        play2.takeDamage(160);
        assertFalse(play2.isAlive());
    }

    public void testBadInputsAndSharedStats() {
        play1.increaseHealth(-20);
        play1.increaseDamage(-20);
        play1.takeDamage(-20);
        play1.heal(-20);
        play1.attack((Enemy)null);
        assertEquals(100, play1.getHealth());
        assertEquals(10, play1.getDamage());
        play1.increaseDamage(5);
        play1.attack(enem);
        assertEquals(5, enem.getHealth());
        Player positioned = new Player(80, 7, 3, 4);
        assertEquals(3, positioned.getX());
        assertEquals(4, positioned.getY());
        assertEquals(80, positioned.getHealth());
        play1.takeDamage(200);
        play1.attack(enem);
        assertEquals(5, enem.getHealth());
        assertEquals(0, play1.getHealth());
        assertFalse(play1.isAlive());
        Player invalid = new Player(-1, -1);
        assertEquals(0, invalid.getHealth());
        assertEquals(0, invalid.getDamage());
    }

    public void testPositionedPlayerRejectsBadCoordinates() {
        try {
            new Player(100, 10, -1, 0);
            fail("Negative starting coordinate");
        }
        catch (IllegalArgumentException expected) {
            assertNotNull(expected);
        }
    }
}
