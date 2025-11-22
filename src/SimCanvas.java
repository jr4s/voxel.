import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class SimCanvas extends JPanel implements ActionListener
{
    static final int PIXEL_SIZE = 4;

    public int GRID_COLS, GRID_ROWS;
    public Elements.Element[][] grid;

    Timer timer;
    Font gameFont;

    public int brushSize = 1;
    public Elements.Element currentElement = new Elements.Sand();

    private final Random rand = new Random();
    public boolean mouseDown = false;

    public SimCanvas()
    {
        setBackground(Color.BLACK);
        setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());

        gameFont = FontManager.load("res/fonts/ByteBounce.ttf", 25);

        Toolkit tool = Toolkit.getDefaultToolkit();
        Image blankCursor = tool.createImage(new byte[0]);
        Cursor invisibleCursor = tool.createCustomCursor(blankCursor, new Point(0, 0), "invisibleCursor");
        setCursor(invisibleCursor);

        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e)
            {
                mouseDown = true;
            }

            @Override
            public void mouseReleased(MouseEvent e)
            {
                mouseDown = false;
            }
        });
    }

    public void gameInit()
    {
        resizeGrid();
        InputHandler.createAndRegister(this);

        addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentResized(ComponentEvent e)
            {
                resizeGrid();
            }
        });

        timer = new Timer(16, this);
        timer.start();
    }

    void resizeGrid()
    {
        int newCols = Math.max(1, getWidth() / PIXEL_SIZE);
        int newRows = Math.max(1, getHeight() / PIXEL_SIZE);

        Elements.Element[][] newGrid = new Elements.Element[newRows][newCols];

        for (int y = 0; y < newRows; y++)
        {
            for (int x = 0; x < newCols; x++)
            {
                if (grid != null && y < grid.length && x < grid[0].length)
                {
                    newGrid[y][x] = grid[y][x];
                }
                else
                {
                    newGrid[y][x] = new Elements.Empty();
                }
            }
        }

        grid = newGrid;
        GRID_ROWS = newRows;
        GRID_COLS = newCols;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (grid == null) return;

        if (mouseDown)
        {
            Point p = getMousePosition();
            if (p != null)
            {
                int cx = p.x / PIXEL_SIZE;
                int cy = p.y / PIXEL_SIZE;
                dropElementAt(cx, cy);
            }
        }

        updateGrid();
        repaint();
    }

    public void updateGrid()
    {
        for (int y = GRID_ROWS - 2; y >= 0; y--)
        {
            for (int x = 1; x < GRID_COLS - 1; x++)
            {
                grid[y][x].update(y, x, grid, rand);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if (grid == null) return;

        // Draw grid
        for (int y = 0; y < GRID_ROWS; y++)
        {
            for (int x = 0; x < GRID_COLS; x++)
            {
                g.setColor(grid[y][x].getColor());
                g.fillRect(x * PIXEL_SIZE, y * PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE);
            }
        }

        // Cursor + HUD
        Point p = getMousePosition();
        if (p != null)
        {
            int cx = p.x / PIXEL_SIZE;
            int cy = p.y / PIXEL_SIZE;

            g.setColor(Color.WHITE);
            int half = brushSize * PIXEL_SIZE / 2;

            g.drawLine(
                (cx * PIXEL_SIZE) - half,
                cy * PIXEL_SIZE,
                (cx * PIXEL_SIZE) + half,
                cy * PIXEL_SIZE
            );

            g.setFont(gameFont);
            g.setColor(Color.WHITE);

            String label = String.format(
                "x%d, y%d   Pxls:%d   Brush:%d",
                cx, cy,
                countDrawnPixels(),  
                brushSize
            );

            g.drawString(label, 20, 60);
        }

        // Current element label
        if (gameFont != null)
        {
            g.setFont(gameFont);
            g.setColor(Color.WHITE);
            g.drawString("Elem: " + currentElement.getClass().getSimpleName(), 20, 40);
            g.drawString("Temp: ", 180, 40);
        }
    }

    public void dropElementAt(int cx, int cy)
    {
        int half = brushSize / 2;
        int baseY = cy + 1;

        for (int dx = -half; dx <= half; dx++)
        {
            int x = cx + dx;

            int wiggle = (int)(Math.sin(System.currentTimeMillis() * 0.01 + dx * 0.5) * 1.5);
            int y = baseY + wiggle;

            if (x >= 0 && x < GRID_COLS && y >= 0 && y < GRID_ROWS)
            {
                try {
                    grid[y][x] = currentElement.getClass().getDeclaredConstructor().newInstance();
                } catch (ReflectiveOperationException ex) {
                    grid[y][x] = new Elements.Sand();
                }
            }
        }
    }

    public int countPixelsAt(int cx, int cy)
    {
        int count = 0;
        int half = brushSize / 2;

        for (int dx = -half; dx <= half; dx++)
        {
            int x = cx + dx;
            int y = cy;

            if (x >= 0 && x < GRID_COLS && y >= 0 && y < GRID_ROWS)
            {
                if (!(grid[y][x] instanceof Elements.Empty))
                {
                    count++;
                }
            }
        }

        return count;
    }

    private int countDrawnPixels()
    {
        int count = 0;
        for (int y = 0; y < GRID_ROWS; y++)
        {
            for (int x = 0; x < GRID_COLS; x++)
            {
                if (!(grid[y][x] instanceof Elements.Empty))
                {
                    count++;
                }
            }
        }
        return count;
    }
}
