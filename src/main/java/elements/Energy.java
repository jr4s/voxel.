package elements;

import java.awt.Color;
import java.util.Random;

public class Energy {

    public static class Fire extends Core.Element {
        protected final Random rand = new Random();

        protected int lifeTime;           
        protected int driftChance;       
        protected int spreadChance;      
        protected int tickCounter;

        private static final Color[] FIRE_COLORS = {
            new Color(255, 200, 120),
            new Color(255, 160, 60),
            new Color(220, 60, 20)
        };

        public Fire() {
            this.name = "Fire";
            this.isEnergy = true;
            this.isSolid = false;
            this.isFluid = false;

            this.lifeTime = rand.nextInt(16) + 25;
            this.driftChance = 3;
            this.spreadChance = 8;
            this.tickCounter = 0;
        }

        @Override
        public Color getColor() {
            Color base = FIRE_COLORS[rand.nextInt(FIRE_COLORS.length)];
            int flick = (int) (Math.sin(tickCounter * 0.2) * 18);
            return new Color(
                Math.max(0, Math.min(255, base.getRed() + flick)),
                Math.max(0, Math.min(255, base.getGreen() + flick)),
                Math.max(0, Math.min(255, base.getBlue() + flick))
            );
        }

        @Override
        public void update(int y, int x, Core.Element[][] grid, Random rand) {
            int rows = grid.length;
            int cols = grid[0].length;

            tickCounter++;
            if (tickCounter % driftChance != 0) return;

            lifeTime--;
            if (lifeTime <= 0) {
                grid[y][x] = new Smoke();
                return;
            }

            int[][] neighbors = { {-1,0}, {1,0}, {0,-1}, {0,1} };
            for (int[] n : neighbors) {
                int nx = x + n[0];
                int ny = y + n[1];
                if (nx >= 0 && nx < cols && ny >= 0 && ny < rows) {
                    Core.Element neighbor = grid[ny][nx];
                    if (neighbor instanceof Solids.Wood && rand.nextInt(spreadChance) == 0) {
                        grid[ny][nx] = new Fire();
                    }
                }
            }

            int drift = rand.nextInt(3) - 1;
            int nx = x + drift;
            int ny = y - 1;
            if (ny >= 0 && ny < rows && nx >= 0 && nx < cols) {
                if (grid[ny][nx] instanceof Core.Empty) {
                    grid[ny][nx] = this;
                    grid[y][x] = new Core.Empty();
                }
            }
        }
    }

    public static class Smoke extends Core.Element {
        private final Random rand = new Random();

        private int lifeTime;
        private final int driftChance;

        private static final Color[] SMOKE_COLORS = {
            new Color(100, 100, 100), // light/diffused smoke
            new Color(80, 80, 80),    // medium smoke
            new Color(60, 60, 60),    // dense smoke
            new Color(40, 40, 40)     // very dense smoke
        };

        public Smoke() {
            this.name = "Smoke";
            this.isEnergy = true;
            this.isSolid = false;
            this.isFluid = false;

            this.lifeTime = rand.nextInt(11) + 10;
            this.driftChance = 2;
        }

        @Override
        public Color getColor() {
            if (lifeTime > 10) 
                return SMOKE_COLORS[0];
            else if (lifeTime > 7) 
                return SMOKE_COLORS[1];
            else if (lifeTime > 4) 
                return SMOKE_COLORS[2];
            else return 
                SMOKE_COLORS[3];
        }

        @Override
        public void update(int y, int x, Core.Element[][] grid, Random rand) {
            int rows = grid.length;
            int cols = grid[0].length;

            if (rand.nextInt(driftChance) != 0) return;

            lifeTime--;
            if (lifeTime <= 0) {
                grid[y][x] = new Core.Empty();
                return;
            }

            int drift = rand.nextInt(3) - 1;
            int nx = x + drift;
            int ny = y - 1;
            if (ny >= 0 && ny < rows && nx >= 0 && nx < cols) {
                if (grid[ny][nx] instanceof Core.Empty) {
                    grid[ny][nx] = this;
                    grid[y][x] = new Core.Empty();
                }
            }
        }
    }
}
