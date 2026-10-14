package javaPackage;
import student.TestCase;
public class EnemyTest extends TestCase {

    private Enemy enemy;

    public void setUp() {
        enemy = new Enemy(100, 15);
    }

    public void testGetHealth() {
        assertEquals(100, enemy.getHealth());
    }

    public void testGetDamage() {
        assertEquals(15, enemy.getDamage());
    }

    public void testTakeDamage() {
        enemy.takeDamage(20);
        assertEquals(80, enemy.getHealth());

        enemy.takeDamage(30);
        assertEquals(50, enemy.getHealth());

        enemy.takeDamage(100);
        assertEquals(0, enemy.getHealth());
    }

    public void testIsDefeated() {
        assertFalse(enemy.isDefeated());

        enemy.takeDamage(50);
        assertFalse(enemy.isDefeated());

        enemy.takeDamage(50);
        assertTrue(enemy.isDefeated());
    }
}
