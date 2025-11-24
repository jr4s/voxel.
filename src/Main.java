import javax.swing.*;
import renderer.Renderer;
import java.awt.*;
import java.awt.event.*;

public class Main {
    private static boolean isFullscreen = false;
    private static final GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    private static JFrame frame;

    public static void main(String[] args) {
        frame = new JFrame("Boxel - OpenJDK");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setResizable(true);

        ImageIcon ico = new ImageIcon("res/assets/icon.png");
        frame.setIconImage(ico.getImage());
        
        Renderer canvas = new Renderer();
        // canvas.LoadMap("res/maps/map.txt"); 

        frame.add(canvas);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        canvas.gameInit(); 

        canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F11"), "toggleFullscreen");
        canvas.getActionMap().put("toggleFullscreen", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleFullscreen();
            }
        });

            AbstractAction escAction = new AbstractAction() {            
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        };
        canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
              .put(KeyStroke.getKeyStroke("ESCAPE"), "ESCAPE");
        canvas.getActionMap().put("ESCAPE", escAction);
    }

    private static void toggleFullscreen() {
        if (!isFullscreen) {
            frame.dispose();
            frame.setUndecorated(true);
            gd.setFullScreenWindow(frame);
            isFullscreen = true;
        } else {
            gd.setFullScreenWindow(null);
            frame.dispose();
            frame.setUndecorated(false);
            frame.setVisible(true);
            isFullscreen = false;
        }
    }
}
