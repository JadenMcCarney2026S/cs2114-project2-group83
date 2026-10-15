package javaPackage;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

/** Desktop presentation and key bindings. All game rules stay in Game/Dungeon. */
public class GameWindow extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final Color BACKGROUND = new Color(13, 17, 23);
    private static final Color SURFACE = new Color(21, 27, 35);
    private static final Color LINE = new Color(44, 54, 65);
    private static final Color TEXT = new Color(226, 231, 229);
    private static final Color MUTED = new Color(139, 156, 163);
    private static final Color GOLD = new Color(228, 185, 105);
    private static final Color MINT = new Color(123, 211, 185);
    private static final Color RED = new Color(228, 113, 108);
    private static final String[] REGIONS = {
        "Mossbound halls", "The sunken archive", "Ember corridors", "The Warden's keep"
    };
    private Game game;
    private Dungeon exploredFloor;
    private boolean[][] explored;
    private boolean paused;
    private boolean help;
    private String facing = "south";

    /** Creates a playable panel. A missing or unloaded game is rejected. */
    public GameWindow(Game game) {
        if (game == null || !game.startInteractive()) {
            throw new IllegalArgumentException("A playable game is required.");
        }
        this.game = game;
        setPreferredSize(new Dimension(1280, 820));
        setBackground(BACKGROUND);
        setFocusable(true);
        String[] directions = {"north", "south", "west", "east"};
        String[] arrows = {"UP", "DOWN", "LEFT", "RIGHT"};
        String[] letters = {"W", "S", "A", "D"};
        for (int i = 0; i < directions.length; i++) {
            final String direction = directions[i];
            bind("move-" + direction, arrows[i], () -> move(direction));
            bind("letter-" + direction, letters[i], () -> move(direction));
        }
        bind("attack", "SPACE", () -> {
            if (canAct()) {
                game.attackTurn(facing);
                updateExploration();
                repaint();
            }
        });
        bind("descend", "E", this::descend);
        bind("enter-descend", "ENTER", this::descend);
        bind("restart", "R", this::restart);
        bind("pause", "P", this::togglePause);
        bind("escape", "ESCAPE", this::togglePause);
        bind("help", "H", () -> {
            help = !help;
            repaint();
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                if (event.getX() >= getWidth() - 292 && event.getX() <= getWidth() - 28
                    && event.getY() >= getHeight() - 83
                    && event.getY() <= getHeight() - 41) {
                    restart();
                }
                requestFocusInWindow();
            }
        });
        updateExploration();
    }

    /** Kept package-private: Game.main owns application launch. */
    static void open(Game game) {
        JFrame frame = new JFrame("The Hollow — Dungeon Crawler");
        GameWindow panel = new GameWindow(game);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(panel);
        frame.pack();
        frame.setMinimumSize(new Dimension(1000, 720));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        panel.requestFocusInWindow();
    }

    Game getGame() {
        return game;
    }

    private void bind(String action, String key, Runnable command) {
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key), action);
        getActionMap().put(action, new AbstractAction() {
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent event) {
                command.run();
            }
        });
    }

    private boolean canAct() {
        return !paused && !help && game.isRunning();
    }

    private void move(String direction) {
        if (canAct()) {
            facing = direction;
            game.takeTurn(direction);
            updateExploration();
            repaint();
        }
    }

    private void descend() {
        if (canAct()) {
            game.nextLayer();
            updateExploration();
            repaint();
        }
    }

    private void restart() {
        game = Game.newRun();
        game.startInteractive();
        paused = false;
        help = false;
        facing = "south";
        updateExploration();
        repaint();
    }

    private void togglePause() {
        if (help) {
            help = false;
        }
        else if (game.isRunning()) {
            paused = !paused;
        }
        repaint();
    }

    private void updateExploration() {
        Dungeon dungeon = game.getCurrentDungeon();
        if (dungeon != exploredFloor) {
            exploredFloor = dungeon;
            explored = new boolean[dungeon.getHeight()][dungeon.getWidth()];
        }
        for (int r = 0; r < explored.length; r++) {
            for (int c = 0; c < explored[r].length; c++) {
                int dy = r - game.getPlayerRow();
                int dx = c - game.getPlayerColumn();
                if (dx * dx + dy * dy <= 64) {
                    explored[r][c] = true;
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D)graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int sidebarX = getWidth() - 292;
        int boardWidth = sidebarX - 52;
        label(g, "T H E   H O L L O W", 28, 40, 24, TEXT, true);
        label(g, "A descent into the unknown", 29, 62, 12, MUTED, false);
        label(g, "TURN " + String.format("%03d", game.getTurns()),
            sidebarX + 164, 40, 12, GOLD, true);
        g.setColor(LINE);
        g.drawLine(28, 80, getWidth() - 28, 80);
        Dungeon floor = game.getCurrentDungeon();
        int layer = floor.getLayerNumber();
        label(g, REGIONS[Math.min(3, (layer - 1) / 3)].toUpperCase(), 40, 116, 11, MINT, true);
        label(g, "Explore. Gather strength. Find the stairs.", 40, 137, 12, MUTED, false);
        drawBoard(g, 28, 158, boardWidth, getHeight() - 256);
        drawSidebar(g, sidebarX, layer);
        label(g, "W A S D / ARROWS   Move & bump attack     SPACE   Strike", 40,
            getHeight() - 64, 12, TEXT, false);
        label(g, "E / ENTER   Descend     P / ESC   Pause     H   Help     R   New run", 40,
            getHeight() - 40, 11, MUTED, false);
        if (help || paused || !game.isRunning()) {
            drawOverlay(g, 28, 158, boardWidth, getHeight() - 256);
        }
        g.dispose();
    }

    private void drawBoard(Graphics2D g, int x, int y, int width, int height) {
        g.setColor(SURFACE);
        g.fillRoundRect(x, y, width, height, 12, 12);
        g.setColor(LINE);
        g.drawRoundRect(x, y, width, height, 12, 12);
        Dungeon floor = game.getCurrentDungeon();
        int tile = Math.max(4, Math.min((width - 28) / floor.getWidth(),
            (height - 28) / floor.getHeight()));
        int left = x + (width - floor.getWidth() * tile) / 2;
        int top = y + (height - floor.getHeight() * tile) / 2;
        for (int r = 0; r < floor.getHeight(); r++) {
            for (int c = 0; c < floor.getWidth(); c++) {
                int px = left + c * tile;
                int py = top + r * tile;
                if (!explored[r][c]) {
                    g.setColor(new Color(17, 22, 29));
                    g.fillRect(px, py, tile, tile);
                    continue;
                }
                boolean wall = floor.getTile(c, r) == Place.Tile.WALL;
                g.setColor(wall ? new Color(47, 59, 69)
                    : (r + c) % 2 == 0 ? new Color(31, 41, 47) : new Color(33, 43, 49));
                g.fillRect(px, py, tile - 1, tile - 1);
                if (wall) {
                    g.setColor(new Color(65, 79, 87));
                    g.fillRect(px, py, tile - 1, Math.max(1, tile / 7));
                    g.setColor(new Color(36, 46, 55));
                    g.drawLine(px + tile / 2, py + tile / 2, px + tile - 2, py + tile / 2);
                }
                else if ((r * 13 + c * 7) % 11 == 0) {
                    g.setColor(new Color(58, 69, 64));
                    g.fillRect(px + tile / 3, py + tile / 2, 2, 2);
                }
                if (floor.isExit(r, c)) {
                    drawStairs(g, px, py, tile);
                }
            }
        }
        for (Enemy occupant : floor.getEnemies().toArray(new Enemy[floor.getEnemies().size()])) {
            int r = occupant.getY();
            int c = occupant.getX();
            int distance = Math.abs(r - game.getPlayerRow()) + Math.abs(c - game.getPlayerColumn());
            if (explored[r][c] && distance <= 11
                && (occupant instanceof Item || occupant.isAlive())) {
                drawOccupant(g, occupant, left + c * tile, top + r * tile, tile);
            }
        }
        int px = left + game.getPlayerColumn() * tile;
        int py = top + game.getPlayerRow() * tile;
        drawPlayer(g, px, py, tile);
        if (floor.isExit(game.getPlayerRow(), game.getPlayerColumn())) {
            label(g, "STAIRS FOUND · PRESS E TO DESCEND", x + 18, y + height - 12, 11, GOLD, true);
        }
    }

    private void drawPlayer(Graphics2D g, int x, int y, int size) {
        int unit = Math.max(1, size / 8);
        int cx = x + size / 2;
        g.setColor(new Color(51, 88, 90));
        g.fillOval(x - 2, y - 2, size + 3, size + 3);
        g.setColor(MINT);
        g.fillRect(cx - 2 * unit, y + unit, 4 * unit, 5 * unit);
        g.setColor(TEXT);
        g.fillRect(cx - 2 * unit, y + unit, 4 * unit, 2 * unit);
        g.setColor(BACKGROUND);
        g.fillRect(cx - unit, y + 2 * unit, 3 * unit, unit);
        g.setColor(new Color(71, 130, 132));
        g.fillRect(cx - 2 * unit, y + 6 * unit, unit, unit);
        g.fillRect(cx + unit, y + 6 * unit, unit, unit);
        g.setColor(GOLD);
        g.fillRect(cx + 3 * unit, y + 2 * unit, unit, 4 * unit);
    }

    private void drawStairs(Graphics2D g, int x, int y, int size) {
        g.setColor(new Color(76, 64, 42));
        g.fillRect(x, y, size - 1, size - 1);
        g.setColor(GOLD);
        for (int step = 0; step < 4; step++) {
            int inset = 2 + step * Math.max(1, size / 8);
            g.drawLine(x + inset, y + size - 3 - step * size / 5,
                x + size - 3, y + size - 3 - step * size / 5);
        }
    }

    private void drawOccupant(Graphics2D g, Enemy enemy, int x, int y, int size) {
        int unit = Math.max(1, size / 8);
        int cx = x + size / 2;
        if (enemy instanceof Item) {
            Item item = (Item)enemy;
            g.setColor(item.getDmgBonus() > 0 ? GOLD : RED);
            if (item.getDmgBonus() > 0) {
                g.fillRect(cx, y + unit, unit, 5 * unit);
                g.fillRect(cx - unit, y + 5 * unit, 3 * unit, unit);
            }
            else {
                g.fillRoundRect(cx - 2 * unit, y + 3 * unit, 4 * unit, 4 * unit, 3, 3);
                g.setColor(TEXT);
                g.fillRect(cx - unit, y + unit, 2 * unit, 2 * unit);
            }
            return;
        }
        boolean boss = enemy.getName().equals("Warden");
        Color color = boss ? RED : enemy.getName().equals("Slime") ? MINT
            : enemy.getName().equals("Bat") ? GOLD : new Color(160, 150, 207);
        g.setColor(color);
        if (enemy.getName().equals("Bat")) {
            g.fillPolygon(new int[] {x + unit, cx, x + size - unit, cx},
                new int[] {y + unit, y + 4 * unit, y + unit, y + 7 * unit}, 4);
        }
        else {
            g.fillRoundRect(x + unit, y + 2 * unit, size - 2 * unit,
                size - 3 * unit, boss ? 2 : 6, boss ? 2 : 6);
        }
        g.setColor(BACKGROUND);
        g.fillRect(cx - 2 * unit, y + 4 * unit, unit, unit);
        g.fillRect(cx + unit, y + 4 * unit, unit, unit);
        if (boss) {
            g.setColor(GOLD);
            g.fillRect(x + unit, y, unit, 3 * unit);
            g.fillRect(x + size - 2 * unit, y, unit, 3 * unit);
        }
    }

    private void drawSidebar(Graphics2D g, int x, int layer) {
        label(g, "EXPEDITION", x, 116, 11, MUTED, true);
        label(g, "Floor " + String.format("%02d", layer), x, 154, 28, TEXT, true);
        label(g, "/ " + Game.FLOOR_COUNT, x + 116, 154, 16, MUTED, false);
        for (int i = 0; i < Game.FLOOR_COUNT; i++) {
            g.setColor(i < layer ? GOLD : LINE);
            g.fillRoundRect(x + i * 26, 169, 21, 3, 2, 2);
        }
        Player player = game.getPlayer();
        label(g, "VITALITY", x, 207, 10, MUTED, true);
        label(g, player.getHealth() + " / " + game.getMaximumHealth(), x + 170, 207, 12, TEXT, true);
        g.setColor(LINE);
        g.fillRoundRect(x, 220, 264, 8, 5, 5);
        g.setColor(player.getHealth() < 30 ? RED : MINT);
        g.fillRoundRect(x, 220, (int)(264L * player.getHealth()
            / Math.max(1, game.getMaximumHealth())), 8, 5, 5);
        label(g, "ATTACK", x, 258, 10, MUTED, true);
        label(g, Integer.toString(player.getDamage()), x + 90, 258, 16, GOLD, true);
        label(g, "DEFEATED", x + 145, 258, 10, MUTED, true);
        label(g, Integer.toString(game.getDefeatedEnemies()), x + 237, 258, 16, TEXT, true);
        rule(g, x, 280);
        label(g, "SATCHEL", x, 308, 11, MUTED, true);
        label(g, game.getItemCount() + " relics collected", x, 331, 12, TEXT, false);
        ArrayList<Item> inventory = game.getInventory();
        if (inventory.isEmpty()) {
            wrapped(g, "Find red flasks for vitality and golden blades for attack.", x, 357, 245, MUTED, 2);
        }
        else {
            for (int i = Math.max(0, inventory.size() - 3); i < inventory.size(); i++) {
                Item item = inventory.get(i);
                int y = 358 + (i - Math.max(0, inventory.size() - 3)) * 23;
                label(g, item.getName(), x, y, 12, TEXT, false);
                label(g, item.getDmgBonus() > 0 ? "+" + item.getDmgBonus() + " ATK"
                    : "+" + item.getHealthBonus() + " HP", x + 199, y, 10, GOLD, true);
            }
        }
        rule(g, x, 429);
        label(g, "JOURNAL", x, 457, 11, MUTED, true);
        ArrayList<String> messages = game.getMessages();
        int availableRows = Math.max(2, (getHeight() - 590) / 44);
        int lineY = 482;
        for (int i = Math.max(0, messages.size() - availableRows); i < messages.size(); i++) {
            wrapped(g, messages.get(i), x, lineY, 252,
                i == messages.size() - 1 ? TEXT : MUTED, 2);
            lineY += 44;
        }
        g.setColor(GOLD);
        g.fillRoundRect(x, getHeight() - 83, 264, 42, 7, 7);
        label(g, "NEW EXPEDITION                       R", x + 17, getHeight() - 57, 11, BACKGROUND, true);
        label(g, "Run " + Long.toUnsignedString(game.getRunSeed(), 36).toUpperCase(),
            x, getHeight() - 18, 10, MUTED, false);
    }

    private void drawOverlay(Graphics2D g, int x, int y, int width, int height) {
        g.setColor(new Color(8, 12, 18, 215));
        g.fillRoundRect(x, y, width, height, 12, 12);
        int cx = x + width / 2;
        int top = y + height / 2 - 94;
        String title = help ? "YOUR EXPEDITION" : paused ? "TAKE A BREATH"
            : game.hasWon() ? "THE HOLLOW IS SILENT" : "LOST TO THE DEPTHS";
        centered(g, title, cx, top, 22, GOLD);
        String[] lines = help ? new String[] {
            "WASD or arrow keys: move one tile.",
            "Walk into an enemy to attack. Space attacks where you face.",
            "Enemies respond after each successful move or attack.",
            "Flasks and blades apply their bonuses when collected.",
            "Stand on gold stairs and press E to descend.",
            "Survive ten floors and defeat the Warden. H closes this guide."
        } : paused ? new String[] {"The dungeon waits for you.", "Press P or Escape to continue."}
            : new String[] {game.hasWon() ? "You defeated the Warden and escaped all ten floors."
                : "Floor " + game.getCurrentDungeon().getLayerNumber() + " reached. Every descent is a new chance.",
                game.getTurns() + " turns  /  " + game.getDefeatedEnemies() + " enemies defeated  /  "
                    + game.getItemCount() + " relics", "Press R for a newly generated expedition."};
        for (int i = 0; i < lines.length; i++) {
            centered(g, lines[i], cx, top + 40 + i * 25, 12, i == 0 ? TEXT : MUTED);
        }
    }

    private void rule(Graphics2D g, int x, int y) {
        g.setColor(LINE);
        g.setStroke(new BasicStroke(1));
        g.drawLine(x, y, x + 264, y);
    }

    private void label(Graphics2D g, String text, int x, int y, int size, Color color, boolean bold) {
        g.setFont(new Font(Font.SANS_SERIF, bold ? Font.BOLD : Font.PLAIN, size));
        g.setColor(color);
        g.drawString(text, x, y);
    }

    private void centered(Graphics2D g, String text, int x, int y, int size, Color color) {
        g.setFont(new Font(Font.SANS_SERIF, size > 18 ? Font.BOLD : Font.PLAIN, size));
        g.setColor(color);
        g.drawString(text, x - g.getFontMetrics().stringWidth(text) / 2, y);
    }

    private void wrapped(Graphics2D g, String text, int x, int y, int width, Color color, int maxLines) {
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        g.setColor(color);
        String line = "";
        int lines = 0;
        for (String word : text.split(" ")) {
            String next = line.isEmpty() ? word : line + " " + word;
            if (!line.isEmpty() && g.getFontMetrics().stringWidth(next) > width) {
                g.drawString(line, x, y + lines++ * 17);
                if (lines >= maxLines) {
                    return;
                }
                line = word;
            }
            else {
                line = next;
            }
        }
        if (!line.isEmpty() && lines < maxLines) {
            g.drawString(line, x, y + lines * 17);
        }
    }
}
