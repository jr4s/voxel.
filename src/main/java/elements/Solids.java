package elements;

import java.awt.Color;
import java.util.Random;

public class Solids {

    public static class Stone extends Core.Element {
        private static final Color[] COLORS = {
            new Color(100,100,100), new Color(120,120,120), new Color(140,140,140)
        };
        private final Color color;

        public Stone() {
            this.name = "Stone";
            this.isSolid = true;
            this.isFluid = false;
            this.isEnergy = false;
            this.color = COLORS[new Random().nextInt(COLORS.length)];
        }

        @Override
        public Color getColor() { return color; }
        @Override
        public Color[] getPalette() { return COLORS; }
    }

    public static class Wood extends Core.Element {
        private static final Color[] COLORS = {
            new Color(140,100,60), new Color(130,90,55), new Color(115,75,45)
        };
        private final Color color;
        public int burnResistance = 1;

        public Wood() {
            this.name = "Wood";
            this.isSolid = true;
            this.isFluid = false;
            this.isEnergy = false;
            this.color = COLORS[new Random().nextInt(COLORS.length)];
        }

        @Override
        public Color getColor() { return color; }
        @Override
        public Color[] getPalette() { return COLORS; }
    }
}
