import java.awt.Color;
import java.util.Random;

public class Elements 
{
    public static abstract class Element 
    {
        public abstract Color getColor();

        // Called each tick to simulate element behavior
        public void update(int y, int x, Elements.Element[][] grid, Random rand) { }

        public boolean isEmpty() { return false; }
    }

    /* Empty */
    public static class Empty extends Element 
    {
        @Override
        public Color getColor() { return Color.BLACK; } // Background
        @Override
        public boolean isEmpty() { return true; }       // Stay empty
    }

    /* Sand */
    public static class Sand extends Element 
    {
        @Override
        public Color getColor() { return new Color(253, 253, 150); }

        @Override
        public void update(int y, int x, Element[][] grid, Random rand) 
        {
            int rows = grid.length;
            int cols = grid[0].length;

            // fall straight down
            if (y + 1 < rows && (grid[y + 1][x] instanceof Empty || grid[y + 1][x] instanceof Water)) 
            {
                move(grid, y, x, y + 1, x);
                return;
            }

            int dir = rand.nextBoolean() ? -1 : 1;

            /* if and else if slides diagonaly */
            if (y + 1 < rows && x + dir >= 0 && x + dir < cols &&

                (grid[y + 1][x + dir] instanceof Empty || grid[y + 1][x + dir] instanceof Water) &&
                grid[y][x + dir] instanceof Empty) {
                move(grid, y, x, y + 1, x + dir);

            } else if (y + 1 < rows && x - dir >= 0 && x - dir < cols &&
                       (grid[y + 1][x - dir] instanceof Empty || grid[y + 1][x - dir] instanceof Water) &&
                       grid[y][x - dir] instanceof Empty) {
                move(grid, y, x, y + 1, x - dir);
            }
        }
    }

    /* Water */
    public static class Water extends Element 
    {
        @Override
        public Color getColor() { return new Color(128, 200, 230); }

        @Override
        public void update(int y, int x, Element[][] grid, Random rand) 
        {
            int rows = grid.length;
            int cols = grid[0].length;

            // fall straight down
            if (y + 1 < rows && grid[y + 1][x] instanceof Empty) 
            {
                move(grid, y, x, y + 1, x);
                return;
            }

            int dir = rand.nextBoolean() ? -1 : 1;

            // diagonal down
            if (y + 1 < rows && x + dir >= 0 && x + dir < cols &&
                grid[y + 1][x + dir] instanceof Empty) 
            {
                move(grid, y, x, y + 1, x + dir);
                return;
            }

            // sideways flow
            if (x + dir >= 0 && x + dir < cols && grid[y][x + dir] instanceof Empty) {
                move(grid, y, x, y, x + dir);

            } else if (x - dir >= 0 && x - dir < cols && grid[y][x - dir] instanceof Empty) {
                move(grid, y, x, y, x - dir);
            }
        }
    }

    /* Stone */ 
    public static class Stone extends Element 
    {
        @Override
        public Color getColor() { return new Color(120, 120, 120); }
    }

    static void move(Element[][] grid, int fromY, int fromX, int toY, int toX) {
        grid[toY][toX] = grid[fromY][fromX];
        grid[fromY][fromX] = new Elements.Empty();
    }
}
