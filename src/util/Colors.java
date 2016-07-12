package util;

import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import json.DataFormatException;
import json.parser.PosBuffer;

/**
 * @author Sebastian Stern
 */
public class Colors {
    public final static Color BLUE = Color.rgb(115, 176, 194);

	public final static Color PETROL = Color.rgb(3, 127, 127);

	public final static Color GREEN = Color.rgb(102, 255, 0);

	public final static Color RED = Color.rgb(255, 31, 31);

	public final static Color TEAM1 = Color.rgb(203, 147, 0);

	public final static Color Team2 = Color.rgb(121, 105, 98);

    public static Color getReadable(final Color color) {
		double r = color.getRed() > 0.5 ? 0 : 1;
		double g = color.getGreen() > 0.5 ? 0 : 1;
		double b = color.getBlue() > 0.5 ? 0 : 1;

		return Color.color(r, g, b);
	}

	public static Color getRandom() {
		return Color.color(Math.random(), Math.random(), Math.random());
	}

	public static Color team(boolean first) {
		return first ? TEAM1 : Team2;
	}

    public static java.awt.Color getReadable(final java.awt.Color color) {
		int r = color.getRed() > 0x7F ? 0 : 0xFF;
		int g = color.getGreen() > 0x7F ? 0 : 0xFF;
		int b = color.getBlue() > 0x7F ? 0 : 0xFF;

		return new java.awt.Color(r, g, b);
	}
    public static java.awt.Color toAwt(Color c){
    	return new java.awt.Color((float)c.getRed(), (float)c.getGreen(), (float)c.getBlue(), (float)c.getOpacity());
    }
    public static Color toFx(java.awt.Color c){
    	float factor=1.0f/0xFF;
    	return new Color(c.getRed()*factor, c.getGreen()*factor, c.getBlue()*factor, c.getAlpha()*factor);
    }
    public static String toString(Color c){
    	return toString(toAwt(c));
    }
    public static String toString(java.awt.Color c){
    	return c.getRed()+"/"+c.getGreen()+"/"+c.getBlue();
    }
	public static Color parseColor(String s, PosBuffer pos) {
		int firstSlash=s.indexOf('/');
		int lastSlash=s.lastIndexOf('/');
		if(firstSlash!=lastSlash && firstSlash>=0 && lastSlash>=0){
			try{
				int r = Integer.parseInt(s.substring(0, firstSlash));
				int g = Integer.parseInt(s.substring(firstSlash+1, lastSlash));
				int b = Integer.parseInt(s.substring(lastSlash+1));
				if(r<0 || g<0 || b<0 || r>0xFF || g>0xFF || b>0xFF)
					throw new DataFormatException("invalid color string: Number out of range", pos);
				double factor=1.0/0xFF;
				Color color = new Color(r*factor, g*factor, b*factor, 1);
				return color;
			}catch(NumberFormatException x){
				throw new DataFormatException("invalid color string", pos);
			}

		}
		if(s.length()==6 && s.matches("[0-9a-fA-F]*")){
			int r = Integer.parseInt(s.substring(0, 2), 16);
			int g = Integer.parseInt(s.substring(2, 4), 16);
			int b = Integer.parseInt(s.substring(4, 6), 16);
			double factor=1.0/0xFF;
			Color color = new Color(r*factor, g*factor, b*factor, 1);
			return color;

		}
		throw new DataFormatException("invalid color string", pos);
	}
}
