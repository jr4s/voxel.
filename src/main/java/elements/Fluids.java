package elements;

import java.awt.Color;
import java.util.Random;

public class Fluids {

    public static class Water extends Core.Element {
        private static final Color[] COLORS = { 
            new Color(0,105,148),
            new Color(0,127,168)
        };

        private final Random rand = new Random();

        public Water() {
            this.name = "Water";
            this.isFluid = true;
            this.isSolid = false;
            this.isEnergy = false;
        }

        @Override
        public Color getColor() { return COLORS[rand.nextInt(COLORS.length)]; }

        @Override
        public Color[] getPalette() { return COLORS; }

        @Override
        public void update(int y, int x, Core.Element[][] grid, Random rand) {
            if (rand.nextInt(2) == 0) return;

            int rows = grid.length;

            // Fall down
            if (y + 1 < rows && grid[y + 1][x].isEmpty()) {
                Core.Utility.move(grid, y, x, y + 1, x);
                return;
            }

            // Move sideways
            int dir = rand.nextBoolean() ? 1 : -1;
            if (x + dir >= 0 && x + dir < grid[0].length && grid[y][x + dir].isEmpty()) {
                Core.Utility.move(grid, y, x, y, x + dir);
            }
        }
    }

    public static class Lava extends Core.Element {
        private static final Color[] COLORS = {
            new Color(255, 80, 0),
            new Color(255, 120, 0),
            new Color(255, 160, 50),
            new Color(255, 200, 80)
        };
        private int tickCounter = 0;

        public Lava() {
            this.name = "Lava";
            this.isFluid = true;
            this.isSolid = false;
            this.isEnergy = false;
        }

        @Override
        public Color getColor() {
            return COLORS[tickCounter % COLORS.length];
        }

        @Override
        public Color[] getPalette() {
            return COLORS;
        }

        @Override
        public void update(int y, int x, Core.Element[][] grid, Random rand) {
            int rows = grid.length;
            int cols = grid[0].length;

            // Slow down movement
            if (rand.nextInt(2) == 0) return;

            // Fall straight down if empty
            if (y + 1 < rows && grid[y + 1][x] instanceof Core.Empty) {
                Core.Utility.move(grid, y, x, y + 1, x);
            } else {
                // Spread sideways if blocked
                int dir = rand.nextBoolean() ? 1 : -1;
                if (x + dir >= 0 && x + dir < cols && grid[y][x + dir] instanceof Core.Empty) {
                    Core.Utility.move(grid, y, x, y, x + dir);
                }
            }

            tickCounter++;
        }
    }

    public static class Oil extends Core.Element {
        private static final Color COLOR = new Color(10, 25, 40); 

        private int tickCounter = 0;

        public Oil() {
            this.name = "Oil";
            this.isFluid = true;
            this.isSolid = false;
            this.isEnergy = false;
        }

        @Override
        public Color getColor() { 
            return COLOR; 
        }

        @Override
        public Color[] getPalette() { 
            return new Color[]{COLOR}; 
        }

        @Override
        public void update(int y, int x, Core.Element[][] grid, Random rand) {
            tickCounter++;
            if (tickCounter % 1 != 0) return; // fast tick

            int rows = grid.length;
            int cols = grid[0].length;

            int[][] neighbors = {{-1,0},{1,0},{0,-1},{0,1}};
            for (int[] n : neighbors) {
                int nx = x + n[0];
                int ny = y + n[1];
                if (nx >= 0 && nx < cols && ny >= 0 && ny < rows) {
                    if (grid[ny][nx] instanceof Energy.Fire && rand.nextInt(2) == 0) {
                        grid[y][x] = new Energy.Fire();  // replace Oil with Fire
                        // spawn Smoke on top
                        if (y - 1 >= 0 && grid[y - 1][x] instanceof Core.Empty) {
                            grid[y - 1][x] = new Energy.Smoke();
                        }
                        return;
                    }
                }
            }

            // Movement logic
            if (y + 1 < rows && grid[y + 1][x].isEmpty()) {
                Core.Utility.move(grid, y, x, y + 1, x);
                return;
            }

            int dir = rand.nextBoolean() ? 1 : -1;
            if (x + dir >= 0 && x + dir < cols && grid[y][x + dir].isEmpty()) {
                Core.Utility.move(grid, y, x, y, x + dir);
            }
        }
    }    
}
