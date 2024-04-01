import java.awt.*;
import java.util.Random;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

//Tree class simulator implements runnable interface
public class TreeSimulator implements Runnable {
    private static final int TREE = 1;
    private static final int DIRT = 0;
    private static final int FIRE = 2;
    private static final int DEAD = 5;

    private double probability;
    private int[][] grid;
    private int width;
    private int height;
    private JFrame frame;
    private JPanel panel;
    private Color dirtColor = new Color(147, 80, 27);
    private Color treeColor = Color.GREEN;
    private Color fireColor = Color.RED;
    private Color deadColor = Color.DARK_GRAY;

    public TreeSimulator(int width, int height, double probability) {
        this.width = width;
        this.height = height;
        this.probability = probability;
        grid = new int[height][width];
        Random rand = new Random();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (rand.nextDouble() < probability) {
                    grid[i][j] = TREE;
                } else {
                    grid[i][j] = DIRT;
                }
            }
        }
        frame = new JFrame("Tree Simulator");
        panel = new JPanel() {
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        switch (grid[i][j]) {
                            case DIRT:
                                g.setColor(dirtColor);
                                break;
                            case TREE:
                                g.setColor(treeColor);
                                break;
                            case FIRE:
                                g.setColor(fireColor);
                                break;
                            case DEAD:
                                g.setColor(deadColor);
                                break;
                        }
                        g.fillRect(j * 4, i * 4, 4, 4);
                    }
                }
            }
        };
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width * 4, (height * 4) + 22);
        frame.add(panel);
        frame.setVisible(true);
        //object added to panel allows user to click on tree cell
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX() / 4;  // convert mouse coordinates to cell coordinates
                int y = e.getY() / 4;
                if (grid[y][x] == TREE) {
                    grid[y][x] = FIRE;
                }
            }
        });

        new Thread(this).start();
    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(75);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            updateGrid();
            panel.repaint();
        }
    }

    private void updateGrid() {
        int[][] newGrid = new int[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (grid[i][j] == FIRE) {
                    newGrid[i][j] = DEAD;
                } else if (grid[i][j] == TREE) {
                    newGrid[i][j] = TREE;
                    if (isAdjacentToFire(i, j)) {
                        newGrid[i][j] = FIRE;
                    }
                } else {
                    newGrid[i][j] = DIRT;
                }
            }
        }
        grid = newGrid;
    }

    //method checks for adjacent between tree cells
    private boolean isAdjacentToFire(int row, int col) {
        int[][] deltas = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1},           {0, 1},
                {1, -1},  {1, 0},  {1, 1}
        };
        for (int[] delta : deltas) {
            int r = row + delta[0];
            int c = col + delta[1];
            if (r >= 0 && r < height && c >= 0 && c < width && grid[r][c] == FIRE) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        new TreeSimulator(1024, 768, 0.3);
    }
}
