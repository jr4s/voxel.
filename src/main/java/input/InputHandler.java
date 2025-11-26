package input;

import java.awt.event.*;

import elements.Core;
import elements.Energy;
import elements.Fluids;
import elements.Solids;
import elements.Terrain;

import renderer.Cellular;

public class InputHandler 
{
    private final Cellular canvas;

    private InputHandler(Cellular canvas) 
    {
        this.canvas = canvas;
        registerListener();
    }

    public static InputHandler createAndRegister(Cellular canvas) 
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
            canvas.brushSize = Math.max(1, Math.min(Cellular.MAX_BRUSH_SIZE, canvas.brushSize - delta));
            refresh();
        });

        canvas.setFocusable(true);
        canvas.requestFocusInWindow();
    }

    private void handleKeyPress(KeyEvent e) 
    {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_1 -> canvas.currentElement = new Terrain.Sand();
            case KeyEvent.VK_2 -> canvas.currentElement = new Fluids.Water();
            case KeyEvent.VK_3 -> canvas.currentElement = new Solids.Wood();
            case KeyEvent.VK_4 -> canvas.currentElement = new Energy.Fire();
            case KeyEvent.VK_6 -> canvas.currentElement = new Fluids.Lava();
            case KeyEvent.VK_7 -> canvas.currentElement = new Solids.Stone();
            case KeyEvent.VK_8 -> canvas.currentElement = new Fluids.Oil();
            case KeyEvent.VK_C -> clearGrid();
        }
    }

    private void clearGrid() 
    {
        if (canvas.grid != null) {
            for (int y = 0; y < canvas.GRID_ROWS; y++) {
                for (int x = 0; x < canvas.GRID_COLS; x++) {
                    canvas.grid[y][x] = new Core.Empty();
                }
            }
        }
        refresh();
    }

    private void refresh() { canvas.repaint(); }
    // -- NOTES: Replaced all the overrides with adapters. It saved alot of lines.
}
