package util;

import javafx.scene.paint.Color;

/**
 * @author Sebastian Stern
 */
public class Colors {
    public final static Color BLUE = Color.rgb(115, 176, 194);

    public static Color getReadable(final Color color) {
		double r = color.getRed() > 0.5 ? 0 : 1;
		double g = color.getGreen() > 0.5 ? 0 : 1;
		double b = color.getBlue() > 0.5 ? 0 : 1;

		return Color.color(r, g, b);
	}
}
