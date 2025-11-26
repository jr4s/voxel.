package elements;

import java.awt.Color;
import java.util.Random;

public class Terrain {

    public static class Dirt extends Core.Element {
        private static final Color[] COLORS = {
            new Color(150,100,60), new Color(135,85,50), new Color(120,70,40)
        };
        private final Color color;
        private final Random rand = new Random();

        public Dirt() {
            this.name = "Dirt";
            this.isSolid = true;
            this.isFluid = false;
            this.isEnergy = false;
            this.color = COLORS[rand.nextInt(COLORS.length)];
        }

        @Override
        public Color getColor() { return color; }
        @Override
        public Color[] getPalette() { return COLORS; }

        @Override
        public void update(int y, int x, Core.Element[][] grid, Random rand) {
            if (rand.nextInt(2) == 0) return;

            int rows = grid.length;
            int cols = grid[0].length;

            // Fall straight down
            if (y + 1 < rows) {
                Core.Element below = grid[y + 1][x];
                if (below instanceof Core.Empty) {
                    Core.Utility.move(grid, y, x, y + 1, x);
                    return;
                }
                if (below instanceof Fluids.Water && rand.nextInt(4) == 0) {
                    Core.Utility.swap(grid, y, x, y + 1, x);
                    return;
                }

                // Diagonal slide
                int dir = rand.nextBoolean() ? -1 : 1;
                int nx = x + dir;
                int ny = y + 1;
                if (nx >= 0 && nx < cols && ny < rows && 
                    (grid[ny][nx].isEmpty() || grid[ny][nx] instanceof Fluids.Water)) {
                    Core.Utility.move(grid, y, x, ny, nx);
                }
            }
        }
    }

    public static class Sand extends Core.Element {
        private static final Color[] COLORS = {
            new Color(232,198,97), new Color(235,200,98), new Color(240,205,100)
        };
        private final Color color;
        private final Random rand = new Random();

        public Sand() {
            this.name = "Sand";
            this.isSolid = true;
            this.isFluid = false;
            this.isEnergy = false;
            this.color = COLORS[rand.nextInt(COLORS.length)];
        }

        @Override
        public Color getColor() { return color; }
        @Override
        public Color[] getPalette() { return COLORS; }

        @Override
        public void update(int y, int x, Core.Element[][] grid, Random rand) {
            if (rand.nextInt(2) == 0) return;

            int rows = grid.length;
            int cols = grid[0].length;

            // Fall straight down
            if (y + 1 < rows) {
                Core.Element below = grid[y + 1][x];
                if (below instanceof Core.Empty) {
                    Core.Utility.move(grid, y, x, y + 1, x);
                    return;
                }
                if (below instanceof Fluids.Water) {
                    if (rand.nextInt(3) == 0) {
                        Core.Utility.swap(grid, y, x, y + 1, x);
                        return;
                    } else {
                        int dir = rand.nextBoolean() ? -1 : 1;
                        int nx = x + dir;
                        int ny = y + 1;
                        if (nx >= 0 && nx < cols && ny < rows && grid[ny][nx] instanceof Core.Empty) {
                            Core.Utility.move(grid, y, x, ny, nx);
                            return;
                        }
                    }
                }
            }

            // Diagonal slide left/right
            if (rand.nextBoolean() || rand.nextInt(3) == 0) {
                int dir = rand.nextBoolean() ? -1 : 1;
                int nx = x + dir;
                int ny = y + 1;
                if (nx >= 0 && nx < cols && ny < rows) {
                    Core.Element diag = grid[ny][nx];
                    if (diag instanceof Core.Empty || diag instanceof Fluids.Water) {
                        Core.Utility.move(grid, y, x, ny, nx);
                    }
                }
            }
        }
    }
}
