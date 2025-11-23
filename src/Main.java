import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Main {
    private static boolean isFullscreen = false;
    private static final GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    private static JFrame frame;

    public static void main(String[] args) {
        frame = new JFrame("JVoxels - OpenJDK");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);

        ImageIcon ico = new ImageIcon("res/assets/icon.png");
        frame.setIconImage(ico.getImage());
        
        Renderer canvas = new Renderer();
        frame.add(canvas);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        canvas.gameInit();

        // Add key binding for F11
        canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F11"), "toggleFullscreen");
        canvas.getActionMap().put("toggleFullscreen", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleFullscreen();
            }
        });
    }

    private static void toggleFullscreen() {
        if (!isFullscreen) {
            frame.dispose(); // required before undecorated change
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
