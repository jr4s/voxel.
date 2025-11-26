package utils;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;

public class FontManager {

    /**
     * Load a font from the classpath resources.
     * @param path Classpath path, e.g. "/fonts/ByteBounce.ttf"
     * @param size Font size
     * @return Loaded Font object, or default SansSerif if failed
     */
    public static Font load(String path, float size) {
        try (InputStream fontStream = FontManager.class.getResourceAsStream(path)) {

            if (fontStream == null) {
                throw new RuntimeException("Font not found: " + path);
            }

            Font font = Font.createFont(Font.TRUETYPE_FONT, fontStream);
            font = font.deriveFont(Font.PLAIN, size);

            // Optional: register globally
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);

            return font;

        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            return new Font("SansSerif", Font.PLAIN, (int)size); // fallback
        }
    }
}
