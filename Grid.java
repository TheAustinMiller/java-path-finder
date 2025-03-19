import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class Grid {
    int[][] grid = new int[29][29];
    private static final int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
    final int PICTURE_LENGTH = 1450; // 1450x1450 image
    final int PIXEL_LENGTH = 29;
    final int BUFFER = 49;
    BufferedImage image;
    Graphics2D canvas;
    int startX, startY, endX, endY;
    final Color[] colors = {Color.white, Color.gray, Color.green, Color.red, Color.blue};
    // 0 - walkablePath (white)
    // 1 - wall (gray)
    // 2 - start (green)
    // 3 - end (red)
    // 4 - path (blue)


    public Grid() {
        this(0, 0, 28, 28);
    }

    public Grid(int startX, int startY, int endX, int endY) {
        grid[6][6] = 1;
        grid[6][7] = 1;
        grid[6][8] = 1;
        grid[7][5] = 1;
        grid[8][5] = 1;
        grid[9][5] = 1;
        grid[10][5] = 1;
        grid[11][5] = 1;
        grid[12][5] = 1;
        grid[13][5] = 1;
        grid[6][9] = 1;
        grid[6][10] = 1;
        grid[6][11] = 1;
        grid[6][12] = 1;
        this.startX = startX;
        this.startY = startY;
        grid[startX][startY] = 2;
        this.endX = endX;
        this.endY = endY;
        grid[endX][endY] = 3;
        image = new BufferedImage(PICTURE_LENGTH, PICTURE_LENGTH, BufferedImage.TYPE_INT_RGB);
        canvas = image.createGraphics();
        canvas.setColor(Color.white);
        canvas.fillRect(0, 0, PICTURE_LENGTH, PICTURE_LENGTH);
    }

    public void printGrid() {
        int xPos = 0, yPos = 0;
        for (int i = 0; i < PIXEL_LENGTH; i++) {
            for (int j = 0; j < PIXEL_LENGTH; j++) {
                canvas.setColor(colors[grid[i][j]]);
                canvas.fillRect(xPos, yPos, 50, 50);
                xPos += 50;
            }
            xPos = 0;
            yPos += 50;
        }

        canvas.setColor(Color.black);
        // Draw vertical lines
        for (int i = 0; i <= PIXEL_LENGTH; i++) {
            canvas.fillRect(i * 50, 0, 3, PICTURE_LENGTH);
        }

        // Draw horizontal lines
        for (int i = 0; i <= PIXEL_LENGTH; i++) {
            canvas.fillRect(0, i * 50, PICTURE_LENGTH, 3);
        }

        try {
            File f = new File("Path.jpg");
            ImageIO.write(image, "jpg", f);
        } catch (IOException e) {
            System.out.println("Failure");
        }
    }

    public void displayGrid() {
        for (int[] row: grid) {
            for (int value: row) {
                System.out.print(value + " ");
            }
            System.out.println();
        }
    }

    public void bfs() {
        int rows = PIXEL_LENGTH;
        int cols = PIXEL_LENGTH;
        boolean[][] visited = new boolean[rows][cols];
        int[][] parentX = new int[rows][cols];
        int[][] parentY = new int[rows][cols];
        Queue<int[]> queue = new LinkedList<>();

        queue.add(new int[]{startX, startY});
        visited[startX][startY] = true;

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int x = current[0];
            int y = current[1];

            if (x == endX && y == endY) {
                // Reconstruct the path
                reconstructPath(parentX, parentY);
                return;
            }

            for (int[] dir : directions) {
                int newX = x + dir[0];
                int newY = y + dir[1];

                if (newX >= 0 && newX < rows && newY >= 0 && newY < cols &&
                        grid[newX][newY] != 1 && !visited[newX][newY]) {
                    queue.add(new int[]{newX, newY});
                    visited[newX][newY] = true;
                    parentX[newX][newY] = x;
                    parentY[newX][newY] = y;
                }
            }
        }
        System.out.println("No path found.");
    }

    private void reconstructPath(int[][] parentX, int[][] parentY) {
        int x = endX;
        int y = endY;
        while (x != startX || y != startY) {
            grid[x][y] = 4; // Mark path as blue
            int prevX = parentX[x][y];
            int prevY = parentY[x][y];
            x = prevX;
            y = prevY;
        }
        grid[startX][startY] = 2; //Ensure start is still green
        grid[endX][endY] = 3; // Ensure end is still red
    }

    public int getStartX() { return startX; }
    public int getStartY() { return startY; }
    public int getEndX() { return endX; }
    public int getEndY() { return endY; }
    public void setStartX(int startX) { this.startX = startX; }
    public void setStartY(int startY) { this.startY = startY; }
    public void setEndX(int EndX) { this.endX = endX; }
    public void setEndY(int EndY) { this.endY = endY; }
    public int[][] getGrid() { return grid; }
}
