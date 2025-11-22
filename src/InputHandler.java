import java.awt.event.*;

public class InputHandler implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {
    private final SimCanvas canvas;

    private InputHandler(SimCanvas canvas) 
    {
        this.canvas = canvas;
    }

    public static InputHandler createAndRegister(SimCanvas canvas)
    {
        InputHandler handler = new InputHandler(canvas);
        handler.registerListener();
        return handler;
    }

    private void registerListener()
    {
        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);
        canvas.addMouseWheelListener(this);

        canvas.addKeyListener(this);
        canvas.setFocusable(true);
        canvas.requestFocusInWindow();
    }

    @Override
    public void keyPressed(KeyEvent e) 
    {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_1:
                canvas.currentElement = new Elements.Sand();
                break;
            case KeyEvent.VK_2:
                canvas.currentElement = new Elements.Water();
                break;
            case KeyEvent.VK_3:
                canvas.currentElement = new Elements.Stone();
                break;
            case KeyEvent.VK_4:
                canvas.currentElement = new Elements.Dirt();
                break;
            case KeyEvent.VK_5:
                canvas.currentElement = new Elements.Empty();
                break;
            case KeyEvent.VK_C:
                if (canvas.grid != null) {
                    for (int y = 0; y < canvas.GRID_ROWS; y++) {
                        for (int x = 0; x < canvas.GRID_COLS; x++) {
                            canvas.grid[y][x] = new Elements.Empty();
                        }
                    }
                }
                canvas.repaint();
                break;
            default:
                break;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) 
    {
        canvas.mouseDown = true; 
    }

    @Override
    public void mouseReleased(MouseEvent e) 
    {
        canvas.mouseDown = false;
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        canvas.repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) 
    {
        canvas.repaint();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) 
    {
        int delta = e.getWheelRotation();
        canvas.brushSize = Math.max(1, Math.min(10, canvas.brushSize - delta));
        canvas.repaint();
    }

    // Unused overrides
    @Override public void keyReleased(KeyEvent e) { }
    @Override public void keyTyped(KeyEvent e) { }
    @Override public void mouseClicked(MouseEvent e) { }
    @Override public void mouseEntered(MouseEvent e) { }
    @Override public void mouseExited(MouseEvent e) { canvas.repaint(); }
}
