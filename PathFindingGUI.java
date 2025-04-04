import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

/**
 * A standalone GUI for the Grid pathfinding class.
 * This class can be added to your project to provide an interactive
 * interface for your pathfinding algorithm.
 */
class PathfindingGUI extends JFrame {
    private Grid grid;
    private GridPanel gridPanel;
    private JButton bfsButton;
    private JButton dfsButton;
    private JButton resetButton;
    private JButton saveButton;
    private JButton setStartButton;
    private JButton setEndButton;
    private boolean settingStart = false;
    private boolean settingEnd = false;

    // Store the current end position manually
    private int currentEndX = 28;
    private int currentEndY = 28;

    /**
     * Constructor that takes an existing Grid object
     * @param grid The Grid object to visualize and interact with
     */
    public PathfindingGUI(Grid grid) {
        this.grid = grid;
        // Initialize our internal tracking of end position
        this.currentEndX = grid.getEndX();
        this.currentEndY = grid.getEndY();
        initializeUI();
    }

    /**
     * Default constructor that creates a new Grid
     */
    public PathfindingGUI() {
        this.grid = new Grid();
        // Initialize our internal tracking of end position
        this.currentEndX = grid.getEndX();
        this.currentEndY = grid.getEndY();
        initializeUI();
    }

    /**
     * Initialize the user interface components
     */
    private void initializeUI() {
        setTitle("Interactive Pathfinding");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize grid panel
        gridPanel = new GridPanel();

        // Button panel
        JPanel buttonPanel = new JPanel();
        bfsButton = new JButton("BFS Solve");
        dfsButton = new JButton("DFS Solve");
        resetButton = new JButton("Reset Path");
        saveButton = new JButton("Save Image");
        setStartButton = new JButton("Set Start");
        setEndButton = new JButton("Set End");

        buttonPanel.add(bfsButton);
        buttonPanel.add(dfsButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(setStartButton);
        buttonPanel.add(setEndButton);

        bfsButton.addActionListener(e -> {
            // Ensure the grid data is correct before solving
            grid.getGrid()[currentEndX][currentEndY] = 3;
            grid.bfs();
            gridPanel.repaint();
        });

        dfsButton.addActionListener(e -> {
            // Ensure the grid data is correct before solving
            grid.getGrid()[currentEndX][currentEndY] = 3;
            grid.dfs();
            gridPanel.repaint();
        });

        saveButton.addActionListener(e -> {
            // Ensure the grid data is correct before saving
            grid.getGrid()[currentEndX][currentEndY] = 3;
            grid.printGrid();
            JOptionPane.showMessageDialog(this, "Image saved as Path.jpg");
        });

        resetButton.addActionListener(e -> {
            resetPath();
            gridPanel.repaint();
        });

        setStartButton.addActionListener(e -> {
            settingStart = true;
            settingEnd = false;
            setStartButton.setBackground(Color.GREEN);
            setEndButton.setBackground(null);
        });

        setEndButton.addActionListener(e -> {
            settingStart = false;
            settingEnd = true;
            setEndButton.setBackground(Color.RED);
            setStartButton.setBackground(null);
        });

        // Add components to frame
        add(gridPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Reset the path in the grid
     */
    private void resetPath() {
        int[][] gridData = grid.getGrid();
        for (int i = 0; i < grid.PIXEL_LENGTH; i++) {
            for (int j = 0; j < grid.PIXEL_LENGTH; j++) {
                if (gridData[i][j] == 4) {
                    gridData[i][j] = 0; // Reset path cells to empty
                }
            }
        }
        // Make sure start and end are still marked
        gridData[grid.getStartX()][grid.getStartY()] = 2;
        gridData[currentEndX][currentEndY] = 3;
    }

    /**
     * Launch the GUI
     */
    public void display() {
        setVisible(true);
    }

    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PathfindingGUI gui = new PathfindingGUI();
            gui.display();
        });
    }

    /**
     * Panel to display the grid
     */
    private class GridPanel extends JPanel {
        private final int CELL_SIZE = 20;

        public GridPanel() {
            setPreferredSize(new Dimension(grid.PIXEL_LENGTH * CELL_SIZE, grid.PIXEL_LENGTH * CELL_SIZE));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int x = e.getX() / CELL_SIZE;
                    int y = e.getY() / CELL_SIZE;

                    if (x >= 0 && x < grid.PIXEL_LENGTH && y >= 0 && y < grid.PIXEL_LENGTH) {
                        int[][] gridData = grid.getGrid();

                        if (settingStart) {
                            // Remove previous start
                            gridData[grid.getStartX()][grid.getStartY()] = 0;

                            // Set new start
                            grid.setStartX(x);
                            grid.setStartY(y);
                            gridData[x][y] = 2;

                            settingStart = false;
                            setStartButton.setBackground(null);
                        } else if (settingEnd) {
                            // Remove previous end from grid
                            gridData[grid.getEndX()][grid.getEndY()] = 0;

                            grid.setEndX(x);
                            grid.setEndY(y);

                            currentEndX = grid.getEndX();
                            currentEndY = grid.getEndY();

                            // Mark the new end on the grid
                            gridData[x][y] = 3;

                            settingEnd = false;
                            setEndButton.setBackground(null);
                        } else {
                            // Toggle wall only if not start or end
                            if ((x != grid.getStartX() || y != grid.getStartY()) &&
                                    (x != currentEndX || y != currentEndY)) {

                                if (gridData[x][y] == 1) {
                                    gridData[x][y] = 0; // Remove wall
                                } else if (gridData[x][y] == 0 || gridData[x][y] == 4) {
                                    gridData[x][y] = 1; // Add wall
                                }
                            }
                        }
                        repaint();
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int[][] gridData = grid.getGrid();

            for (int i = 0; i < grid.PIXEL_LENGTH; i++) {
                for (int j = 0; j < grid.PIXEL_LENGTH; j++) {
                    // Set color based on cell type
                    g.setColor(grid.colors[gridData[i][j]]);
                    g.fillRect(i * CELL_SIZE, j * CELL_SIZE, CELL_SIZE, CELL_SIZE);

                    // Draw border
                    g.setColor(Color.BLACK);
                    g.drawRect(i * CELL_SIZE, j * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
        }
    }
}
