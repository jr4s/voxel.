import java.awt.Color;
import java.util.Random;

public class Elements
{
    public static abstract class Element
    {
        public abstract Color getColor();

        public void update(int y, int x, Elements.Element[][] grid, Random rand) { }

        public boolean isEmpty() { return false; }
    }

    public static class Stone extends Element
    {
        public enum StoneColor
        {
            BASALT(new Color(110, 110, 110)),
            GRANITE(new Color(130, 125, 120)),
            LIMESTONE(new Color(150, 145, 135)),
            SANDSTONE(new Color(170, 165, 150)),
            MARBLE(new Color(190, 185, 170));

            public final Color value;

            StoneColor(Color value) { this.value = value; }

            public static Color random()
            {
                StoneColor[] values = StoneColor.values();
                return values[(int)(Math.random() * values.length)].value;
            }
        }

        private final Color color;

        public Stone() { this.color = StoneColor.random(); }

        @Override
        public Color getColor() { return color; }
    }

    public static class Empty extends Element
    {
        @Override
        public Color getColor() { return Color.BLACK; }

        @Override
        public boolean isEmpty() { return true; }
    }

    public static class Sand extends Element
    {
        private static final Color[] COLORS =
        {
            new Color(232, 198, 97),
            new Color(235, 200, 98),
            new Color(240, 205, 100)
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
        public void update(int y, int x, Element[][] grid, Random rand) {
            int rows = grid.length;
            int cols = grid[0].length;

            // Try straight down first
            if (y + 1 < rows) {
                Element below = grid[y + 1][x];

                // Empty space directly below → fall straight down
                if (below instanceof Empty) {
                    ElementsUtil.move(grid, y, x, y + 1, x);
                    return;
                }

                // Water directly below → slow scatter effect
                if (below instanceof Water) {
                    // Add "drag": only move sometimes
                    if (rand.nextInt(3) == 0) {
                        // 1 in 3 chance to sink directly
                        ElementsUtil.swap(grid, y, x, y + 1, x);
                        return;
                    } else {
                        // Otherwise scatter sideways
                        int dir = rand.nextBoolean() ? -1 : 1;
                        int nx = x + dir;
                        int ny = y + 1;

                        if (nx >= 0 && nx < cols && ny < rows && grid[ny][nx] instanceof Empty) {
                            ElementsUtil.move(grid, y, x, ny, nx); // drift diagonally
                            return;
                        }
                    }
                }
            }

            // Random diagonal movement (like natural sliding)
            if (rand.nextBoolean() || rand.nextInt(3) == 0) {
                int dir = rand.nextBoolean() ? -1 : 1;
                int nx = x + dir;
                int ny = y + 1;

                if (nx >= 0 && nx < cols && ny < rows) {
                    Element diag = grid[ny][nx];
                    if (diag instanceof Empty || diag instanceof Water) {
                        ElementsUtil.move(grid, y, x, ny, nx);
                    }
                }
            }
        }
    }

    public static class Dirt extends Element
    {
        private static final Color[] COLORS =
        {
            new Color(150, 100, 60),
            new Color(135, 85, 50),
            new Color(120, 70, 40)
        };

        private final Color color;

        public Dirt()
        {
            int index = (int)(Math.random() * COLORS.length);
            this.color = COLORS[index];
        }

        @Override
        public Color getColor() { return color; }

        @Override
        public void update(int y, int x, Element[][] grid, Random rand) {
            int rows = grid.length;
            int cols = grid[0].length;

            // Try straight down first
            if (y + 1 < rows) {
                Element below = grid[y + 1][x];

                // Empty space directly below → fall straight down
                if (below instanceof Empty) {
                    ElementsUtil.move(grid, y, x, y + 1, x);
                    return;
                }

                // Water directly below → slower scatter effect
                if (below instanceof Water) {
                    // Dirt is heavier, but less fluid than sand → only sometimes sink
                    if (rand.nextInt(4) == 0) { 
                        // 1 in 4 chance to sink directly
                        ElementsUtil.swap(grid, y, x, y + 1, x);
                        return;
                    } else {
                        // Otherwise try to scatter sideways
                        int dir = rand.nextBoolean() ? -1 : 1;
                        int nx = x + dir;
                        int ny = y + 1;

                        if (nx >= 0 && nx < cols && ny < rows && grid[ny][nx] instanceof Empty) {
                            ElementsUtil.move(grid, y, x, ny, nx);
                            return;
                        }
                    }
                }
            }

            // Try one random diagonal direction (less slidey than sand)
            int dir = rand.nextBoolean() ? -1 : 1;
            int nx = x + dir;
            int ny = y + 1;

            if (nx >= 0 && nx < cols && ny < rows) {
                Element diag = grid[ny][nx];
                if (diag instanceof Empty || diag instanceof Water) {
                    ElementsUtil.move(grid, y, x, ny, nx);
                }
            }
        }

    }

    public static class Water extends Element
    {
        public enum WaterColor
        {
            DEEP_OCEAN(new Color(0, 105, 148)),
            LIGHT_OCEAN(new Color(0, 127, 168));

            public final Color value;

            WaterColor(Color value) { this.value = value; }

            public static Color random()
            {
                WaterColor[] values = WaterColor.values();
                return values[(int)(Math.random() * values.length)].value;
            }
        }

        private final Color color;

        public Water() { this.color = WaterColor.random(); }

        @Override
        public Color getColor() { return color; }

        @Override
        public void update(int y, int x, Element[][] grid, Random rand)
        {
            int rows = grid.length;
            int cols = grid[0].length;

            if (y + 1 < rows && grid[y + 1][x] instanceof Empty)
            {
                ElementsUtil.move(grid, y, x, y + 1, x);
                return;
            }

            int dir = rand.nextBoolean() ? -1 : 1;

            if (rand.nextInt(4) == 0) return;

            if (y + 1 < rows && x + dir >= 0 && x + dir < cols &&
                grid[y + 1][x + dir] instanceof Empty)
            {
                ElementsUtil.move(grid, y, x, y + 1, x + dir);
                return;
            }

            if (x + dir >= 0 && x + dir < cols &&
                grid[y][x + dir] instanceof Empty)
            {
                ElementsUtil.move(grid, y, x, y, x + dir);
                return;
            }

            int opposite = -dir;
            if (x + opposite >= 0 && x + opposite < cols &&
                grid[y][x + opposite] instanceof Empty)
            {
                ElementsUtil.move(grid, y, x, y, x + opposite);
            }
        }
    }
}
