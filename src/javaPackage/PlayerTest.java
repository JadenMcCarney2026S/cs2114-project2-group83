package javaPackage;

import student.TestCase;

/**
 * A test class for Player
 * 
 * @author Hemanth Nimmagadda (nhemanth06)
 * @version 2026.10.15
 */
public class PlayerTest extends TestCase {
    private Player play1;
    private Player play2;
    private Enemy enem;

    public void setUp() {
        play1 = new Player();
        play2 = new Player(150, 25, 0, 0);
        enem = new Enemy(20, 5, 0, 0, "Grunt");
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
        assertEquals(92, play1.getHealth());
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
}
