import javax.swing.*;

public class Main 
{
    public static void main(String[] args) 
    {
        JFrame frame = new JFrame("OpenJDK");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);

        SimCanvas canvas = new SimCanvas();
        frame.add(canvas);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        canvas.gameInit();
    }
}
