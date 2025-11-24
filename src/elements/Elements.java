package elements;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;


public class Elements 
{
    // Base Element 
    public static abstract class Element 
    {
        public String name = "Element";

        public abstract Color getColor();

        public void update(int y, int x, Element[][] grid, Random rand) { }

        public boolean isEmpty() { return false; }

        public Element create() {
            try {
                return this.getClass().getDeclaredConstructor().newInstance();
            } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                return new Sand(); 
            }
        }

        public int[] getRGBArray() 
        {
            Color c = getColor();
            return new int[]{c.getRed(), c.getGreen(), c.getBlue()};
        }
    }

    // Stone 
    public static class Stone extends Element 
    {
        private static final Color[] DEFAULT_COLORS = {
            new Color(100, 100, 100),  // Dark gray
            new Color(120, 120, 120),  // Medium gray
            new Color(140, 140, 140)   // Light gray
        };
        
        private final Color color;

        public Stone() {
            this.name = "Stone";
            int index = (int)(Math.random() * DEFAULT_COLORS.length);
            this.color = DEFAULT_COLORS[index];
        }

        @Override
        public Color getColor() { return color; }
    }

    public static class Wood extends Element 
    {
        private static final Color[] DEFAULT_COLORS = {
            new Color(102, 51, 0),
            new Color(139, 69, 19),
            new Color(160, 82, 45)
        };
        
        private final Color color;
        public int burnResistance;

        public Wood() 
        {
            this.name = "Wood";
            this.burnResistance = 3;
            int index = (int)(Math.random() * DEFAULT_COLORS.length);
            this.color = DEFAULT_COLORS[index];
        }

        @Override
        public Color getColor() { return color; }
    }

    // Empty 
    public static class Empty extends Element 
    {
        public Empty() { this.name = "Empty"; }

        @Override
        public Color getColor() { return Color.BLACK; }

        @Override
        public boolean isEmpty() { return true; }
    }

    // Sand 
    public static class Sand extends Element 
    {
        private static final Color[] COLORS = {
            new Color(232, 198, 97),
            new Color(235, 200, 98),
            new Color(240, 205, 100)  
        };

        private final Color color;
        private final Random rand = new Random();

        public Sand() {
            this.name = "Sand";
            int index = rand.nextInt(COLORS.length);
            this.color = COLORS[index];
        }

        @Override
        public Color getColor() { return color; }

        @Override
        public void update(int y, int x, Element[][] grid, Random rand) 
        {
            if (rand.nextInt(2) == 0) return; 

            int rows = grid.length;
            int cols = grid[0].length;

            // For example, Sand
            if (y + 1 < rows) {
                Element below = grid[y + 1][x];
                if (below instanceof Empty) {
                    ElementsUtil.move(grid, y, x, y + 1, x);
                    return;
                }
                if (below instanceof Water) {
                    if (rand.nextInt(3) == 0) {
                        ElementsUtil.swap(grid, y, x, y + 1, x);
                        return;
                    } else {
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

            // Random diagonal slide
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

    // Dirt 
    public static class Dirt extends Element 
    {
        private static final Color[] COLORS = {
            new Color(150, 100, 60), new Color(135, 85, 50), new Color(120, 70, 40)
        };

        private final Color color;
        private final Random rand = new Random();

        public Dirt() 
        {
            this.name = "Dirt";
            int index = rand.nextInt(COLORS.length);
            this.color = COLORS[index];
        }

        @Override
        public Color getColor() { return color; }

        @Override
        public void update(int y, int x, Element[][] grid, Random rand) 
        {
            if (rand.nextInt(2) == 0) return; 

            int rows = grid.length;
            int cols = grid[0].length;

            if (y + 1 < rows) {
                Element below = grid[y + 1][x];
                if (below instanceof Empty) {
                    ElementsUtil.move(grid, y, x, y + 1, x);
                    return;
                }
                if (below instanceof Water && rand.nextInt(4) == 0) {
                    ElementsUtil.swap(grid, y, x, y + 1, x);
                    return;
                }
                int dir = rand.nextBoolean() ? -1 : 1;
                int nx = x + dir;
                int ny = y + 1;
                if (nx >= 0 && nx < cols && ny < rows && (grid[ny][nx] instanceof Empty || grid[ny][nx] instanceof Water)) {
                    ElementsUtil.move(grid, y, x, ny, nx);
                }
            }
        }
    }

    // Water 
    public static class Water extends Element 
    {
        private static final Color[] COLORS = {
            new Color(0, 105, 148),
            new Color(0, 127, 168)
        };

        private final Random rand = new Random();

        public Water() {
            this.name = "Water";
        }

        @Override
        public Color getColor() {
            int index = rand.nextInt(COLORS.length);
            return COLORS[index];
        }

        @Override
        public void update(int y, int x, Element[][] grid, Random rand) {
            // Slow down movement
            if (rand.nextInt(2) == 0) return;

            int rows = grid.length;
            int cols = grid[0].length;

            // Fall straight down
            if (y + 1 < rows && grid[y + 1][x] instanceof Empty) {
                ElementsUtil.move(grid, y, x, y + 1, x);
                return;
            }

            int dir = rand.nextBoolean() ? -1 : 1;

            // Random skip for sideways movement
            if (rand.nextInt(4) == 0) return;

            // Move diagonally down-left/right
            if (y + 1 < rows && x + dir >= 0 && x + dir < cols && grid[y + 1][x + dir] instanceof Empty) {
                ElementsUtil.move(grid, y, x, y + 1, x + dir);
                return;
            }

            // Move sideways if blocked
            if (x + dir >= 0 && x + dir < cols && grid[y][x + dir] instanceof Empty) {
                ElementsUtil.move(grid, y, x, y, x + dir);
                return;
            }

            // Try opposite direction
            int opposite = -dir;
            if (x + opposite >= 0 && x + opposite < cols && grid[y][x + opposite] instanceof Empty) {
                ElementsUtil.move(grid, y, x, y, x + opposite);
            }
        }
    }

    public static class Lava extends Elements.Element 
    {
        private final Color[] COLORS = {
            new Color(255, 80, 0),   new Color(255, 120, 0),
            new Color(255, 160, 50),new Color(255, 200, 80)
        };

        private int currentColorIndex = 0;
        private int tickCounter = 0;

        public Lava() {
            this.name = "Lava";
        }

        @Override
        public void update(int y, int x, Element[][] grid, Random rand) {
            // Slow down lava falling
            if (rand.nextInt(2) == 0) return;

            int rows = grid.length;
            int cols = grid[0].length;

            // Move down if empty
            if (y + 1 < rows && grid[y + 1][x] instanceof Elements.Empty) {
                grid[y + 1][x] = this;
                grid[y][x] = new Elements.Empty();
            } else {
                // Spread sideways if blocked
                int dir = rand.nextBoolean() ? 1 : -1;
                if (x + dir >= 0 && x + dir < cols && grid[y][x + dir] instanceof Elements.Empty) {
                    grid[y][x + dir] = this;
                    grid[y][x] = new Elements.Empty();
                }
            }

            // Update color every few ticks
            tickCounter++;
            if (tickCounter % 15 == 0) { 
                currentColorIndex = (currentColorIndex + 1) % COLORS.length;
            }
        }

        @Override
        public Color getColor() {
            return COLORS[currentColorIndex];
        }
    }
}
