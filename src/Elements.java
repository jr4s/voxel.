import java.awt.Color;
import java.util.Random;

public class Elements 
{

    /* Stone */ 
    public static class Stone extends Element {
        public enum StoneColor {
            BASALT(new Color(110, 110, 110)),
            GRANITE(new Color(130, 125, 120)),
            LIMESTONE(new Color(150, 145, 135)),
            SANDSTONE(new Color(170, 165, 150)),
            MARBLE(new Color(190, 185, 170));

            public final Color value;

            StoneColor(Color value) {
                this.value = value;
            }

            public static Color random() {
                StoneColor[] values = StoneColor.values();
                return values[(int)(Math.random() * values.length)].value;
            }
        }

    private final Color color;

    public Stone() {
        this.color = StoneColor.random();
    }

    @Override
    public Color getColor() {
        return color;
    }
}


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

    /* Sand properties and behaviour */
    public static class Sand extends Element 
    {

    private static final Color[] COLORS = {
        new Color(220, 180, 60),   
        new Color(230, 190, 80),   
        new Color(240, 205, 100),  
        new Color(245, 220, 130),  
        new Color(250, 235, 160)   
    };


    private final Color color;

    public Sand() 
    {
        int index = (int)(Math.random() * COLORS.length);
        this.color = COLORS[index];
    }

    @Override
    public Color getColor() 
    {
        return color;
    }

        @Override
        public void update(int y, int x, Element[][] grid, Random rand) 
        {
            int rows = grid.length;
            int cols = grid[0].length;

            if (y + 1 < rows && (grid[y + 1][x] instanceof Empty || grid[y + 1][x] instanceof Water)) 
            {
                move(grid, y, x, y + 1, x);
                return;
            }

            int dir = rand.nextBoolean() ? -1 : 1;

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

    /* Water properties and behaviours */
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
        static void move(Element[][] grid, int fromY, int fromX, int toY, int toX) {
        grid[toY][toX] = grid[fromY][fromX];
        grid[fromY][fromX] = new Elements.Empty();
    }
}



