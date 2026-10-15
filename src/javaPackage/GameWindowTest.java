package javaPackage;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import student.TestCase;

/** Exercises the real Swing key bindings and rendering without requiring a display. */
public class GameWindowTest extends TestCase {
    private Game game;
    private GameWindow window;

    public void setUp() throws Exception {
        super.setUp();
        SwingUtilities.invokeAndWait(() -> {
            game = new Game(new Player(), new Dungeon("First", 1, DungeonTest.map()),
                new Dungeon("Second", 2, DungeonTest.map()));
            window = new GameWindow(game);
            window.setSize(1280, 820);
        });
    }

    private void press(String action) throws Exception {
        SwingUtilities.invokeAndWait(() -> window.getActionMap().get(action)
            .actionPerformed(new ActionEvent(window, ActionEvent.ACTION_PERFORMED, action)));
    }

    public void testMovementKeysAndFloorEntry() throws Exception {
        assertEquals("move-east", window.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .get(KeyStroke.getKeyStroke("RIGHT")));
        assertEquals("letter-north", window.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .get(KeyStroke.getKeyStroke("W")));
        press("move-north"); // Wall: no turn consumed.
        assertEquals(0, game.getTurns());
        press("letter-east");
        assertEquals(2, game.getPlayerColumn());
        assertEquals(1, game.getItemCount());
        press("move-east");
        assertEquals(3, game.getPlayerColumn());
        press("descend");
        assertEquals(2, game.getCurrentDungeon().getLayerNumber());
        assertEquals(1, game.getPlayerColumn());
    }

    public void testBumpCombatAndSpaceAttack() throws Exception {
        press("move-south");
        assertEquals(1, game.getPlayerRow());
        assertEquals(95, game.getPlayer().getHealth()); // One retaliation, not two.
        assertEquals(10, game.getCurrentDungeon().getEnemyAt(2, 1).getHealth());
        press("attack");
        assertNull(game.getCurrentDungeon().getEnemyAt(2, 1));
        assertEquals(1, game.getDefeatedEnemies());
        assertEquals(2, game.getTurns());
    }

    public void testPauseHelpAndRestart() throws Exception {
        press("pause");
        press("move-east");
        assertEquals(1, game.getPlayerColumn());
        press("escape");
        press("help");
        press("move-east");
        assertEquals(1, game.getPlayerColumn());
        press("help");
        press("move-east");
        assertEquals(2, game.getPlayerColumn());
        press("restart");
        assertNotSame(game, window.getGame());
        assertTrue(window.getGame().isRunning());
        assertEquals(1, window.getGame().getCurrentDungeon().getLayerNumber());
        assertEquals(0, window.getGame().getTurns());
    }

    public void testPaintingAllStatesAndMinimumSize() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            BufferedImage image = new BufferedImage(1280, 820, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            window.paint(g);
            assertTrue(image.getRGB(29, 80) != image.getRGB(0, 0));
            window.setSize(1000, 720);
            window.paint(g);
            g.dispose();
        });
        press("pause");
        paintAgain();
        press("help");
        paintAgain();
        press("help");
        press("pause");
        game.getPlayer().takeDamage(Integer.MAX_VALUE);
        paintAgain();
        press("move-east");
        assertEquals(1, game.getPlayerColumn());
    }

    private void paintAgain() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            BufferedImage image = new BufferedImage(1000, 720, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            window.paint(g);
            g.dispose();
        });
    }

    public void testRejectMissingGame() {
        try {
            new GameWindow(null);
            fail("Missing game must be rejected");
        }
        catch (IllegalArgumentException expected) {
            assertNotNull(expected);
        }
        try {
            new GameWindow(new Game(null));
            fail("Unplayable game must be rejected");
        }
        catch (IllegalArgumentException expected) {
            assertNotNull(expected);
        }
    }

    public void testVictoryOverlayAndEarlyDescend() throws Exception {
        press("enter-descend");
        assertEquals(1, game.getCurrentDungeon().getLayerNumber());
        SwingUtilities.invokeAndWait(() -> {
            game = new Game(new Player(), new Dungeon("Final", 1, DungeonTest.map()));
            window = new GameWindow(game);
            window.setSize(1000, 720);
        });
        press("move-east");
        press("attack"); // Facing east: no target, no consumed turn.
        assertEquals(1, game.getTurns());
        press("move-east");
        press("enter-descend");
        assertTrue(game.hasWon());
        assertFalse(game.isRunning());
        paintAgain();
    }
}
