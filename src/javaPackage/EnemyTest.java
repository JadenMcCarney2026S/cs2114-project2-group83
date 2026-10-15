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

    public void testAttackAndBadDamage() {
        Player player = new Player();
        enemy.attack(player);
        assertEquals(85, player.getHealth());
        enemy.attack(null);
        enemy.takeDamage(-15);
        assertEquals(100, enemy.getHealth());
        assertTrue(enemy.isAlive());
        enemy.takeDamage(100);
        enemy.attack(player);
        assertEquals(85, player.getHealth());
        assertFalse(enemy.isAlive());
        Enemy invalid = new Enemy(-10, -5);
        assertEquals(0, invalid.getHealth());
        assertEquals(0, invalid.getDamage());
    }

    public void testCoordinates() {
        Enemy located = new Enemy(10, 2, 3, 4);
        assertEquals(3, located.getX());
        assertEquals(4, located.getY());
        located.setX(0);
        located.setY(0);
        assertEquals(0, located.getX());
        assertEquals(0, located.getY());
    }

    public void testInvalidCoordinatesPreserveState() {
        enemy.setX(3);
        enemy.setY(4);
        try {
            enemy.setX(-1);
            fail("Negative column");
        }
        catch (IllegalArgumentException expected) {
            assertEquals(3, enemy.getX());
        }
        try {
            enemy.setY(-1);
            fail("Negative row");
        }
        catch (IllegalArgumentException expected) {
            assertEquals(4, enemy.getY());
        }
        for (int[] position : new int[][] {{-1, 0}, {0, -1}}) {
            try {
                new Enemy(10, 5, position[0], position[1]);
                fail("Negative starting coordinate");
            }
            catch (IllegalArgumentException expected) {
                assertNotNull(expected);
            }
        }
    }

    public void testNamedEnemies() {
        assertEquals("Enemy", enemy.getName());
        assertEquals("Warden", new Enemy("Warden", 100, 12, 2, 3).getName());
        for (String name : new String[] {null, "", "  "}) {
            try {
                new Enemy(name, 20, 5, 1, 1);
                fail("Enemy name required");
            }
            catch (IllegalArgumentException expected) {
                assertNotNull(expected);
            }
        }
    }
}
