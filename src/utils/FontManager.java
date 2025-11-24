package utils;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class FontManager {
    public static Font load(String path, float size) {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, new File(path));
            return font.deriveFont(Font.PLAIN, size);
        } catch (FontFormatException | IOException e) {
            return new Font("SansSerif", Font.PLAIN, (int)size);
        }
    }
}
