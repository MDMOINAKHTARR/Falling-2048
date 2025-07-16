import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class Falling2048 extends JFrame implements KeyListener, ActionListener {
    private static final int GRID_WIDTH = 6;
    private static final int GRID_HEIGHT = 7;
    private static final int CELL_SIZE = 60;
    private static final int GAME_SPEED = 600; // milliseconds
    private static final int MARGIN = 8;
    
    private int[][] grid;
    private int fallingNumber;
    private int fallingColumn;
    private int fallingRow;
    private boolean gameOver;
    private int score;
    private int level;
    private Random random;
    private Timer gameTimer;
    private boolean isNumberFalling;
    private JLabel scoreLabel;
    private JLabel levelLabel;
    private JLabel nextLabel;
    private int nextNumber;
    
    // Enhanced color scheme for different numbers
    private Color[] numberColors = {
        new Color(238, 228, 218), // 2 - light beige
        new Color(237, 224, 200), // 4 - tan
        new Color(242, 177, 121), // 8 - orange
        new Color(245, 149, 99),  // 16 - dark orange
        new Color(246, 124, 95),  // 32 - red-orange
        new Color(246, 94, 59),   // 64 - red
        new Color(237, 207, 114), // 128 - yellow
        new Color(237, 204, 97),  // 256 - gold
        new Color(237, 200, 80),  // 512 - bright gold
        new Color(237, 197, 63),  // 1024 - yellow-gold
        new Color(237, 194, 46),  // 2048 - golden yellow
        new Color(255, 100, 100), // 4096 - light red
        new Color(255, 50, 50),   // 8192 - red
        new Color(200, 50, 255),  // 16384 - purple
        new Color(100, 50, 255),  // 32768 - blue-purple
        new Color(50, 100, 255),  // 65536 - blue
    };
    
    public Falling2048() {
        initializeGame();
        setupWindow();
        startGame();
    }
    
    private void initializeGame() {
        grid = new int[GRID_HEIGHT][GRID_WIDTH];
        random = new Random();
        score = 0;
        level = 1;
        gameOver = false;
        isNumberFalling = false;
        
        // Initialize grid with zeros
        for (int i = 0; i < GRID_HEIGHT; i++) {
            for (int j = 0; j < GRID_WIDTH; j++) {
                grid[i][j] = 0;
            }
        }
        
        generateNextNumber();
        spawnNewNumber();
    }
    
    private void setupWindow() {
        setTitle("Falling 2048 Enhanced");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Create main panel with modern layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(40, 44, 52));
        
        // Create info panel
        JPanel infoPanel = createInfoPanel();
        
        // Create game panel
        JPanel gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGame(g);
            }
        };
        
        gamePanel.setPreferredSize(new Dimension(
            GRID_WIDTH * CELL_SIZE + 2 * MARGIN, 
            GRID_HEIGHT * CELL_SIZE + 2 * MARGIN
        ));
        gamePanel.setBackground(new Color(40, 44, 52));
        gamePanel.setFocusable(true);
        gamePanel.addKeyListener(this);
        
        // Create controls panel
        JPanel controlsPanel = createControlsPanel();
        
        mainPanel.add(infoPanel, BorderLayout.NORTH);
        mainPanel.add(gamePanel, BorderLayout.CENTER);
        mainPanel.add(controlsPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
        
        // Setup game timer
        gameTimer = new Timer(GAME_SPEED, this);
        
        // Request focus for key events
        gamePanel.requestFocusInWindow();
    }
    
    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        infoPanel.setBackground(new Color(40, 44, 52));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Score panel
        JPanel scorePanel = createInfoCard("SCORE", "0");
        scoreLabel = (JLabel) scorePanel.getComponent(1);
        
        // Level panel
        JPanel levelPanel = createInfoCard("LEVEL", "1");
        levelLabel = (JLabel) levelPanel.getComponent(1);
        
        // Next number panel
        JPanel nextPanel = createInfoCard("NEXT", "2");
        nextLabel = (JLabel) nextPanel.getComponent(1);
        
        infoPanel.add(scorePanel);
        infoPanel.add(levelPanel);
        infoPanel.add(nextPanel);
        
        return infoPanel;
    }
    
    private JPanel createInfoCard(String title, String value) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(60, 64, 72));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 104, 112), 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setForeground(new Color(200, 200, 200));
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createControlsPanel() {
        JPanel controlsPanel = new JPanel(new FlowLayout());
        controlsPanel.setBackground(new Color(40, 44, 52));
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel controlsLabel = new JLabel("Controls: ← → Move | ↓ Drop | R Restart");
        controlsLabel.setForeground(new Color(180, 180, 180));
        controlsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        controlsPanel.add(controlsLabel);
        
        return controlsPanel;
    }
    
    private void generateNextNumber() {
        // Generate next number with probability: 60% for 2, 30% for 4, 10% for 8
        int rand = random.nextInt(10);
        if (rand < 6) {
            nextNumber = 2;
        } else if (rand < 9) {
            nextNumber = 4;
        } else {
            nextNumber = 8;
        }
    }
    
    private void spawnNewNumber() {
        if (gameOver) return;
        
        fallingNumber = nextNumber;
        generateNextNumber();
        updateNextLabel();
        
        fallingColumn = GRID_WIDTH / 2;
        fallingRow = 0;
        isNumberFalling = true;
        
        // Check if game is over (top row is blocked)
        if (grid[0][fallingColumn] != 0) {
            gameOver = true;
            gameTimer.stop();
            showGameOverDialog();
        }
    }
    
    private void updateNextLabel() {
        if (nextLabel != null) {
            nextLabel.setText(String.valueOf(nextNumber));
        }
    }
    
    private void showGameOverDialog() {
        String message = String.format("Game Over!\n\nFinal Score: %d\nLevel Reached: %d\n\nPress R to restart", score, level);
        JOptionPane.showMessageDialog(this, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void dropNumber() {
        if (!isNumberFalling || gameOver) return;
        
        // Check if number can continue falling
        if (fallingRow + 1 < GRID_HEIGHT && grid[fallingRow + 1][fallingColumn] == 0) {
            fallingRow++;
        } else {
            // Number has landed, process it
            landNumber();
        }
    }
    
    private void landNumber() {
        if (gameOver) return;
        
        // Place the number first
        grid[fallingRow][fallingColumn] = fallingNumber;
        
        // Check for merges and apply them
        performMerges();
        
        // Apply gravity
        applyGravity();
        
        // Update level based on score
        updateLevel();
        
        isNumberFalling = false;
        updateLabels();
        
        // Spawn next number after a short delay
        Timer delayTimer = new Timer(300, e -> {
            spawnNewNumber();
            ((Timer) e.getSource()).stop();
        });
        delayTimer.setRepeats(false);
        delayTimer.start();
    }
    
    private void performMerges() {
        boolean foundMerge = true;
        
        while (foundMerge) {
            foundMerge = false;
            
            // Check horizontal merges
            for (int row = 0; row < GRID_HEIGHT; row++) {
                for (int col = 0; col < GRID_WIDTH - 1; col++) {
                    if (grid[row][col] != 0 && grid[row][col] == grid[row][col + 1]) {
                        // Merge horizontally
                        grid[row][col] = grid[row][col] * 2;
                        grid[row][col + 1] = 0;
                        score += grid[row][col];
                        foundMerge = true;
                    }
                }
            }
            
            // Check vertical merges
            for (int row = 0; row < GRID_HEIGHT - 1; row++) {
                for (int col = 0; col < GRID_WIDTH; col++) {
                    if (grid[row][col] != 0 && grid[row][col] == grid[row + 1][col]) {
                        // Merge vertically
                        grid[row + 1][col] = grid[row][col] * 2;
                        grid[row][col] = 0;
                        score += grid[row + 1][col];
                        foundMerge = true;
                    }
                }
            }
            
            // Apply gravity after each merge round
            if (foundMerge) {
                applyGravity();
            }
        }
    }
    
    private void applyGravity() {
        // Make numbers fall down due to gravity
        for (int col = 0; col < GRID_WIDTH; col++) {
            // Collect all non-zero numbers in this column
            List<Integer> numbers = new ArrayList<>();
            
            for (int row = GRID_HEIGHT - 1; row >= 0; row--) {
                if (grid[row][col] != 0) {
                    numbers.add(grid[row][col]);
                }
            }
            
            // Clear column
            for (int row = 0; row < GRID_HEIGHT; row++) {
                grid[row][col] = 0;
            }
            
            // Place numbers at bottom
            for (int i = 0; i < numbers.size(); i++) {
                grid[GRID_HEIGHT - 1 - i][col] = numbers.get(i);
            }
        }
    }
    
    private void updateLevel() {
        int newLevel = Math.max(1, score / 1000 + 1);
        if (newLevel > level) {
            level = newLevel;
            // Increase game speed slightly
            gameTimer.setDelay(Math.max(200, GAME_SPEED - (level - 1) * 50));
        }
    }
    
    private void updateLabels() {
        if (scoreLabel != null) {
            scoreLabel.setText(String.valueOf(score));
        }
        if (levelLabel != null) {
            levelLabel.setText(String.valueOf(level));
        }
    }
    
    private void moveFallingNumber(int direction) {
        if (!isNumberFalling || gameOver) return;
        
        int newColumn = fallingColumn + direction;
        
        // Check bounds and if target position is empty
        if (newColumn >= 0 && newColumn < GRID_WIDTH && 
            grid[fallingRow][newColumn] == 0) {
            fallingColumn = newColumn;
        }
    }
    
    private void dropFast() {
        if (!isNumberFalling || gameOver) return;
        
        // Drop the number as far as possible
        while (fallingRow + 1 < GRID_HEIGHT && 
               grid[fallingRow + 1][fallingColumn] == 0) {
            fallingRow++;
        }
        
        // Land the number immediately
        landNumber();
    }
    
    private void drawGame(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw grid background
        g2d.setColor(new Color(60, 64, 72));
        g2d.fillRoundRect(5, 5, 
            GRID_WIDTH * CELL_SIZE + 2 * MARGIN - 10, 
            GRID_HEIGHT * CELL_SIZE + 2 * MARGIN - 10, 15, 15);
        
        // Draw grid cells
        for (int row = 0; row < GRID_HEIGHT; row++) {
            for (int col = 0; col < GRID_WIDTH; col++) {
                int x = col * CELL_SIZE + MARGIN;
                int y = row * CELL_SIZE + MARGIN;
                
                // Draw cell background
                g2d.setColor(new Color(80, 84, 92));
                g2d.fillRoundRect(x + 2, y + 2, CELL_SIZE - 4, CELL_SIZE - 4, 8, 8);
                
                // Draw number if present
                if (grid[row][col] != 0) {
                    drawNumber(g2d, x, y, grid[row][col], false);
                }
            }
        }
        
        // Draw falling number with glow effect
        if (isNumberFalling && !gameOver) {
            int x = fallingColumn * CELL_SIZE + MARGIN;
            int y = fallingRow * CELL_SIZE + MARGIN;
            
            // Draw glow effect
            g2d.setColor(new Color(255, 255, 0, 80));
            g2d.fillRoundRect(x - 2, y - 2, CELL_SIZE + 4, CELL_SIZE + 4, 12, 12);
            
            // Draw falling number
            drawNumber(g2d, x, y, fallingNumber, true);
        }
        
        // Draw game over overlay
        if (gameOver) {
            g2d.setColor(new Color(0, 0, 0, 180));
            g2d.fillRect(0, 0, getWidth(), getHeight());
            
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 32));
            String gameOverText = "GAME OVER";
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(gameOverText)) / 2;
            int y = getHeight() / 2 - 20;
            g2d.drawString(gameOverText, x, y);
            
            g2d.setFont(new Font("Arial", Font.PLAIN, 16));
            String restartText = "Press R to restart";
            fm = g2d.getFontMetrics();
            x = (getWidth() - fm.stringWidth(restartText)) / 2;
            y = getHeight() / 2 + 20;
            g2d.drawString(restartText, x, y);
        }
    }
    
    private void drawNumber(Graphics2D g2d, int x, int y, int number, boolean isFalling) {
        // Get color index based on number
        int colorIndex = (int) (Math.log(number) / Math.log(2)) - 1;
        colorIndex = Math.max(0, Math.min(colorIndex, numberColors.length - 1));
        
        // Draw number background with shadow
        if (!isFalling) {
            g2d.setColor(new Color(0, 0, 0, 50));
            g2d.fillRoundRect(x + 3, y + 3, CELL_SIZE - 4, CELL_SIZE - 4, 8, 8);
        }
        
        g2d.setColor(numberColors[colorIndex]);
        g2d.fillRoundRect(x + 2, y + 2, CELL_SIZE - 4, CELL_SIZE - 4, 8, 8);
        
        // Draw subtle border
        g2d.setColor(new Color(255, 255, 255, 30));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRoundRect(x + 2, y + 2, CELL_SIZE - 4, CELL_SIZE - 4, 8, 8);
        
        // Draw number text
        g2d.setColor(number <= 4 ? new Color(80, 80, 80) : Color.WHITE);
        
        // Adjust font size based on number length
        int fontSize = 24;
        if (number >= 1000) fontSize = 20;
        if (number >= 10000) fontSize = 18;
        if (number >= 100000) fontSize = 16;
        
        g2d.setFont(new Font("Arial", Font.BOLD, fontSize));
        
        String text = String.valueOf(number);
        FontMetrics fm = g2d.getFontMetrics();
        int textX = x + (CELL_SIZE - fm.stringWidth(text)) / 2;
        int textY = y + (CELL_SIZE + fm.getAscent()) / 2 - 2;
        
        g2d.drawString(text, textX, textY);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            dropNumber();
            repaint();
        }
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver && e.getKeyCode() == KeyEvent.VK_R) {
            // Restart game
            initializeGame();
            gameTimer.start();
            repaint();
            return;
        }
        
        if (gameOver) return;
        
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                moveFallingNumber(-1);
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                moveFallingNumber(1);
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                dropFast();
                break;
            case KeyEvent.VK_R:
                // Restart game
                initializeGame();
                gameTimer.start();
                break;
        }
        repaint();
    }
    
    @Override
    public void keyTyped(KeyEvent e) {}
    
    @Override
    public void keyReleased(KeyEvent e) {}
    
    private void startGame() {
        gameTimer.start();
        setVisible(true);
        // Ensure the game panel has focus for key events
        SwingUtilities.invokeLater(() -> {
            Component gamePanel = ((JPanel) getContentPane().getComponent(0)).getComponent(1);
            gamePanel.requestFocusInWindow();
        });
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Falling2048();
        });
    }
}