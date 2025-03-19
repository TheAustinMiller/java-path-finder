public class Driver {
    public static void main(String[] args) {
        Grid grid = new Grid(2, 3, 10, 7);
        grid.bfs();
        grid.printGrid();
    }
}
