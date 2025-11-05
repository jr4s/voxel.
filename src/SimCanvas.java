import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class SimCanvas extends JPanel implements ActionListener 
{
    static final int PIXEL_SIZE = 4;

    // Public for InputHandler
    public int GRID_COLS, GRID_ROWS;
    public Elements.Element[][] grid;

    Timer timer;
    InputHandler input;
    public int brushSize = 1;
    public Elements.Element currentElement = new Elements.Sand();

    private final Random rand = new Random();

    public SimCanvas() 
    {
        setBackground(Color.BLACK);
        setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());

        /* Hide the system cursor inside the canvas */ 
        Toolkit tool = Toolkit.getDefaultToolkit();
        Image blankCursor = tool.createImage(new byte[0]);
        Cursor invisibleCursor = tool.createCustomCursor(blankCursor, new Point(0, 0), "invisibleCursor");
        setCursor(invisibleCursor);
    }

    public void gameInit() 
    {
        resizeGrid();

        input = new InputHandler(this);

        // Update the grid depending on windows size
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

        for (int y = 0; y < newRows; y++) {
            for (int x = 0; x < newCols; x++) {
                if (grid != null && y < grid.length && x < grid[0].length) {
                    newGrid[y][x] = grid[y][x];

                } else {
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

        for (int y = 0; y < GRID_ROWS; y++) 
        {
            for (int x = 0; x < GRID_COLS; x++) 
            {
                g.setColor(grid[y][x].getColor());
                g.fillRect(x * PIXEL_SIZE, y * PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE);
            }
        }

        // cursor outline
        Point p = getMousePosition();
        if (p != null) {
            int cx = p.x / PIXEL_SIZE;
            int cy = p.y / PIXEL_SIZE;
            g.setColor(currentElement != null ? currentElement.getColor() : Color.WHITE);
            g.drawRect(
                (cx - brushSize / 2) * PIXEL_SIZE,
                (cy - brushSize / 2) * PIXEL_SIZE,
                brushSize * PIXEL_SIZE,
                brushSize * PIXEL_SIZE
            );
        }
    }

    /** Drops the currently selected element at mouse location using brushSize. */
    public void dropElement(MouseEvent e) 
    {
        if (grid == null) return;

        int cx = e.getX() / PIXEL_SIZE;
        int cy = e.getY() / PIXEL_SIZE;

        for (int dy = -brushSize / 2; dy <= brushSize / 2; dy++) 
        {
            for (int dx = -brushSize / 2; dx <= brushSize / 2; dx++)
            {
                int x = cx + dx;
                int y = cy + dy;
                
                if (x >= 0 && x < GRID_COLS && y >= 0 && y < GRID_ROWS) {
                    try {
                        if (currentElement != null) {
                            grid[y][x] = currentElement.getClass().getDeclaredConstructor().newInstance();
                        } else {
                            grid[y][x] = new Elements.Sand();
                        }
                    } catch (Exception ex) {
                        grid[y][x] = new Elements.Sand();
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
}
