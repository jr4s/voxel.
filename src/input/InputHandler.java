package input;

import java.awt.event.*;

import elements.Elements;
import elements.ElementsBehavior;
import renderer.Renderer;

public class InputHandler 
{
    private final Renderer canvas;

    private InputHandler(Renderer canvas) 
    {
        this.canvas = canvas;
        registerListener();
    }

    public static InputHandler createAndRegister(Renderer canvas) 
    {
        return new InputHandler(canvas);
    }

    private void registerListener() 
    {
        // -- Adapters
        canvas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });

        // -- Mouse events
        canvas.addMouseListener(new MouseAdapter() 
        {
            @Override
            public void mousePressed(MouseEvent e) { canvas.mouseDown = true; }
            @Override
            public void mouseReleased(MouseEvent e) { canvas.mouseDown = false; }
            @Override
            public void mouseExited(MouseEvent e) { refresh(); }
        });

        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) { refresh(); }
            @Override
            public void mouseMoved(MouseEvent e) { refresh(); }
        });

        canvas.addMouseWheelListener(e -> {
            int delta = e.getWheelRotation();
            canvas.brushSize = Math.max(1, Math.min(Renderer.MAX_BRUSH_SIZE, canvas.brushSize - delta));
            refresh();
        });

        canvas.setFocusable(true);
        canvas.requestFocusInWindow();
    }

    private void handleKeyPress(KeyEvent e) 
    {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_1 -> canvas.currentElement = new Elements.Sand();
            case KeyEvent.VK_2 -> canvas.currentElement = new Elements.Water();
            case KeyEvent.VK_3 -> canvas.currentElement = new Elements.Wood();
            case KeyEvent.VK_4 -> canvas.currentElement = new ElementsBehavior.Fire();
            case KeyEvent.VK_5 -> canvas.currentElement = new Elements.Empty();
            case KeyEvent.VK_6 -> canvas.currentElement = new Elements.Lava();
            case KeyEvent.VK_7 -> canvas.currentElement = new Elements.Stone();
            case KeyEvent.VK_DELETE -> clearGrid();
        }
    }

    private void clearGrid() 
    {
        if (canvas.grid != null) {
            for (int y = 0; y < canvas.GRID_ROWS; y++) {
                for (int x = 0; x < canvas.GRID_COLS; x++) {
                    canvas.grid[y][x] = new Elements.Empty();
                }
            }
        }
        refresh();
    }

    private void refresh() { canvas.repaint(); }
    // -- NOTES: Replaced all the overrides with adapters. It saved alot of lines.
}
