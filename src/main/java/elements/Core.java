package elements;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;

public class Core {

    public static abstract class Element {
        public String name = "Element";

        // Flags
        public boolean isSolid = false;
        public boolean isFluid = false;
        public boolean isEnergy = false;

        public abstract Color getColor();
        public void update(int y, int x, Element[][] grid, Random rand) { }

        public boolean isEmpty() { return false; }

        public Element create() {
            try {
                return this.getClass().getDeclaredConstructor().newInstance();
            } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                return new Empty();
            }
        }

        public int[] getRGBArray() {
            Color c = getColor();
            return new int[]{c.getRed(), c.getGreen(), c.getBlue()};
        }

        public Color[] getPalette() { return new Color[]{getColor()}; }

        void setColor(Color color) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    public static class Empty extends Element {
        public Empty() { this.name = "Empty"; }
        @Override
        public Color getColor() { return Color.BLACK; }
        @Override
        public boolean isEmpty() { return true; }
    }

    public static class Utility {
        public static void move(Element[][] grid, int y1, int x1, int y2, int x2) {
            Element temp = grid[y2][x2];
            grid[y2][x2] = grid[y1][x1];
            grid[y1][x1] = temp;
        }

        public static void swap(Element[][] grid, int y1, int x1, int y2, int x2) {
            Element temp = grid[y2][x2];
            grid[y2][x2] = grid[y1][x1];
            grid[y1][x1] = temp;
        }
    }
}
