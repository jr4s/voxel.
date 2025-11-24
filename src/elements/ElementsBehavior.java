package elements;

import java.awt.Color;
import java.util.Random;

public class ElementsBehavior extends Elements 
{
    public static class Fire extends Elements.Element 
    {
        private final Random rand = new Random();

        // -- Parameters
        private int lifeTime;
        private final int driftChance;   
        private final int spreadChance;  
        private int tickCounter;

        // -- Palletes
        private static final Color[] FIRE_COLORS = {
            new Color(255, 200, 120),
            new Color(255, 160, 60),
            new Color(220, 60, 20)
        };

        // -- Settings
        public Fire() {
            this.name = "Fire";
            this.lifeTime = rand.nextInt(16) + 25; 
            this.driftChance = 3;                
            this.spreadChance = 2;                 
            this.tickCounter = 0;
        }

        // -- Randomize pixel colors
        @Override
        public Color getColor() 
        {
            Color base = FIRE_COLORS[rand.nextInt(FIRE_COLORS.length)];
            int flick = (int) (Math.sin(tickCounter * 0.2) * 18);
            return new Color(
                Math.max(0, Math.min(255, base.getRed() + flick)),
                Math.max(0, Math.min(255, base.getGreen() + flick)),
                Math.max(0, Math.min(255, base.getBlue() + flick))
            );
        }

        @Override
        public void update(int y, int x, Element[][] grid, Random rand) 
        {
            tickCounter++;

            if (tickCounter % driftChance != 0) return;

            // -- Decrease lifetime
            lifeTime--;
            if (lifeTime <= 0) {
                grid[y][x] = new Smoke();
                return;
            }

            int rows = grid.length;
            int cols = grid[0].length;

            // -- Spread to neighboring wood
            int[][] neighbors = { {-1,0}, {1,0}, {0,-1}, {0,1} };
            for (int[] n : neighbors) {
                int nx = x + n[0];
                int ny = y + n[1];
                if (nx >= 0 && nx < cols && ny >= 0 && ny < rows) {
                    if (grid[ny][nx] instanceof Elements.Wood && rand.nextInt(spreadChance) == 0) {
                        grid[ny][nx] = new Fire();
                    }
                }
            }

            // -- Rise 
            int drift = rand.nextInt(3) - 1; // -1, 0, 1
            int nx = x + drift;
            int ny = y - 1;

            // -- Move only if inside bounds and target is empty
            if (ny >= 0 && ny < rows && nx >= 0 && nx < cols) {
                if (grid[ny][nx] instanceof Elements.Empty) {
                    grid[ny][nx] = this;
                    grid[y][x] = new Elements.Empty();
                }
            }
        }
    }

    public static class Smoke extends Elements.Element 
    {
        private final Random rand = new Random();

        // -- Settings
        private int lifeTime;
        private final int driftChance;  // 1 in N ticks move up

        private static final Color[] SMOKE_COLORS = {
            new Color(180, 180, 180),
            new Color(130, 130, 130),
            new Color(90, 90, 90)
        };

        public Smoke() 
        {
            this.name = "Smoke";
            this.lifeTime = rand.nextInt(11) + 10;
            this.driftChance = 2;                   
        }

        @Override
        public Color getColor() 
        {
            if (lifeTime > 10) return SMOKE_COLORS[0];
            else if (lifeTime > 5) return SMOKE_COLORS[1];
            else return SMOKE_COLORS[2];
        }

        @Override
        public void update(int y, int x, Element[][] grid, Random rand) 
        {
            if (rand.nextInt(driftChance) != 0) return;

            lifeTime--;
            if (lifeTime <= 0) {
                grid[y][x] = new Elements.Empty();
                return;
            }

            int rows = grid.length;
            int cols = grid[0].length;

            int drift = rand.nextInt(3) - 1; // -1, 0, 1
            int nx = x + drift;
            int ny = y - 1;

            if (ny >= 0 && ny < rows && nx >= 0 && nx < cols) {
                if (grid[ny][nx] instanceof Elements.Empty) {
                    grid[ny][nx] = this;
                    grid[y][x] = new Elements.Empty();
                }
            }
        }
    }
}
