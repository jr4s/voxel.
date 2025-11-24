package renderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Random;

import elements.Elements;
import input.InputHandler;
import utils.FontManager;
import utils.LoadMap;

public class Renderer extends JPanel implements Runnable 
{
    public int PIXEL_SIZE = 6;
    public int GRID_COLS, GRID_ROWS;
    public Elements.Element[][] grid;

    public static final int MAX_BRUSH_SIZE = 20;
    public int brushSize = 1;
    public Elements.Element currentElement = new Elements.Sand();

    private final Random rand = new Random();
    public boolean mouseDown = false;

    private Thread gameThread;
    private volatile boolean running = true;

    private int frames = 0;
    private int fps = 0;
    private long lastFpsTime = System.currentTimeMillis();

    private BufferedImage buffer;
    private WritableRaster raster;

    private int drawnPixels = 0;

    Font gameFont;

    public Renderer() 
    {
        setBackground(Color.BLACK);
        setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());

        gameFont = FontManager.load("res/fonts/ByteBounce.ttf", 25);

        Cursor invisibleCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB), new Point(), "invis");
        setCursor(invisibleCursor);

        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { mouseDown = true; }
            @Override public void mouseReleased(MouseEvent e) { mouseDown = false; }
        });
    }

    public void gameInit() 
    {
        resizeGrid();
        InputHandler.createAndRegister(this);

        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) { resizeGrid(); }
        });

        gameThread = new Thread(this);
        gameThread.start();
    }

    void resizeGrid() 
    {
        int newCols = Math.max(1, getWidth() / PIXEL_SIZE);
        int newRows = Math.max(1, getHeight() / PIXEL_SIZE);

        Elements.Element[][] newGrid = new Elements.Element[newRows][newCols];
        for (int y = 0; y < newRows; y++) {
            for (int x = 0; x < newCols; x++) {
                if (grid != null && y < grid.length && x < grid[0].length)
                    newGrid[y][x] = grid[y][x];
                else
                    newGrid[y][x] = new Elements.Empty();
            }
        }

        grid = newGrid;
        GRID_ROWS = newRows;
        GRID_COLS = newCols;

        buffer = new BufferedImage(GRID_COLS, GRID_ROWS, BufferedImage.TYPE_INT_RGB);
        raster = buffer.getRaster();

        drawnPixels = 0; // reset counter
        for (int y = 0; y < GRID_ROWS; y++) {
            for (int x = 0; x < GRID_COLS; x++) {
                if (!(grid[y][x] instanceof Elements.Empty)) drawnPixels++;
            }
        }
    }

    @Override
    public void run() 
    {
        final int TARGET_FPS = 60;
        final long OPTIMAL_TIME = 1_000_000_000 / TARGET_FPS; // nanoseconds per frame
        final double PHYSICS_HZ = 60.0;
        final double PHYSICS_DT = 1.0 / PHYSICS_HZ;

        long lastTime = System.nanoTime();
        double accumulator = 0.0;

        while (running) {
            long now = System.nanoTime();
            double deltaTime = (now - lastTime) / 1_000_000_000.0;
            lastTime = now;
            accumulator += deltaTime;

            if (mouseDown) {
                Point p = getMousePosition();
                if (p != null) dropElementAt(p.x / PIXEL_SIZE, p.y / PIXEL_SIZE);
            }

            // Fixed timestep physics
            while (accumulator >= PHYSICS_DT) {
                updateGrid();
                accumulator -= PHYSICS_DT;
            }

            repaint();

            long sleepTime = (lastTime + OPTIMAL_TIME - System.nanoTime()) / 1_000_000;
            if (sleepTime > 0) {
                try { Thread.sleep(sleepTime); } catch (InterruptedException ignored) {}
            }
        }
    }


    public void updateGrid() {
        for (int y = GRID_ROWS - 2; y >= 0; y--) {
            for (int x = 0; x < GRID_COLS; x++) {
                grid[y][x].update(y, x, grid, rand);
            }
        }

        // Update BufferedImage and drawnPixels
        drawnPixels = 0;
        for (int y = 0; y < GRID_ROWS; y++) {
            for (int x = 0; x < GRID_COLS; x++) {
                raster.setPixel(x, y, grid[y][x].getRGBArray());
                if (!(grid[y][x] instanceof Elements.Empty)) drawnPixels++;
            }
        }
    }   

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw buffer
        if (buffer != null)
            g.drawImage(buffer, 0, 0, GRID_COLS * PIXEL_SIZE, GRID_ROWS * PIXEL_SIZE, null);

        Point p = getMousePosition();
        if (p != null && gameFont != null) {
            int cx = p.x / PIXEL_SIZE;
            int cy = p.y / PIXEL_SIZE;

            // Cursor circle
            int diameter = brushSize * PIXEL_SIZE;
            g.setColor(Color.WHITE);
            g.drawRect(
                cx * PIXEL_SIZE - diameter / 2, 
                cy * PIXEL_SIZE - diameter / 2, 
                diameter, 
                diameter
            );

            // HUD text
            g.setFont(gameFont);
            g.setColor(Color.WHITE);

            int baseX = 20;
            int baseY = 30;
            int lineHeight = 20;

            g.drawString("Elem: " + currentElement.name, baseX, baseY);
            g.drawString("FPS: " + fps, baseX, baseY + lineHeight);
            g.drawString(String.format("x%d, y%d   Pxls:%d   Brush:%d",
                    cx, cy, countPixelsAt(cx, cy), brushSize), baseX, baseY + lineHeight * 2);
        }

        // Count frame for FPS
        frames++;
        long currentMillis = System.currentTimeMillis();
        if (currentMillis - lastFpsTime >= 1000) {
            fps = frames;
            frames = 0;
            lastFpsTime = currentMillis;
        }
    }

    public void dropElementAt(int cx, int cy) {
        int radius = brushSize / 2;
        for (int dy = -radius; dy <= radius; dy++) {
            for (int dx = -radius; dx <= radius; dx++) {
                int x = cx + dx;
                int y = cy + dy;
                if (x >= 0 && x < GRID_COLS && y >= 0 && y < GRID_ROWS) {
                    if (grid[y][x] instanceof Elements.Empty && !currentElement.isEmpty()) drawnPixels++;
                    grid[y][x] = currentElement.create();
                }
            }
        }
    }


public void LoadMap(String path)
    {
        Elements.Element[][] loadedGrid = LoadMap.fromFile(path);
        if(loadedGrid != null) {
            this.grid = loadedGrid;
            this.GRID_COLS = loadedGrid.length;
            this.GRID_ROWS = loadedGrid[0].length;
        }
    }

    public int countDrawnPixels() {
        return drawnPixels;
    }

    public int countPixelsAt(int cx, int cy) {
        int count = 0;
        int radius = brushSize / 2;
        for (int dy = -radius; dy <= radius; dy++) {
            for (int dx = -radius; dx <= radius; dx++) {
                if (dx*dx + dy*dy > radius*radius) continue;
                int x = cx + dx;
                int y = cy + dy;
                if (x >= 0 && x < GRID_COLS && y >= 0 && y < GRID_ROWS) {
                    if (!(grid[y][x] instanceof Elements.Empty)) count++;
                }
            }
        }
        return count;
    }
}
