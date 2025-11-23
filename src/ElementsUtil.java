public class ElementsUtil {

    public static void swap(Elements.Element[][] grid, int y1, int x1, int y2, int x2) {
        Elements.Element temp = grid[y2][x2];
        grid[y2][x2] = grid[y1][x1];
        grid[y1][x1] = temp;
    }

    public static void move(Elements.Element[][] grid, int fromY, int fromX, int toY, int toX) {
        grid[toY][toX] = grid[fromY][fromX];
        grid[fromY][fromX] = new Elements.Empty();
    }

    private ElementsUtil() {}
}
