package il.co.aman.itextimpl;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import java.lang.reflect.Field;

/**
 * Converts strings to a {@link java.awt.Color} object.
 *
 * @author davidz
 */
public class String2Color {

    /**
     *
     * @param val The string representation of the color. It may be either one
     * of the names defined as static fields in the {@link java.awt.Color}
     * class, a decimal RGB string of the form "250,140,20", or a CSS-type hex
     * code (with or without the leading hash). May be <code>null</code>.
     * @return <code>null</code> if the string is unrecognized as a color or is <code>null</code>.
     */
    public static Color fromString(String val) {
        Color res;
        int[] rgb;
        if (val == null) {
            res = null;
        } else {
            try {
                if (val.contains(",")) { // it should be of the form "250,140,20"
                    String[] sRgb = val.split(",");
                    if (sRgb.length != 3) {
                        res = null;
                    } else {
                        rgb = new int[3];
                        for (int i = 0; i < 3; i++) {
                            rgb[i] = Integer.parseInt(sRgb[i].trim()); // if not an integer, will throw IllegalArgumentException
                        }
                        res = new DeviceRgb(rgb[0], rgb[1], rgb[2]);
                    }
                } else { // it should be either a field name or a CSS color code
                    res = color2Color(getColorField(val));
                    if (res == null) {
                        rgb = il.co.aman.apps.ColorConvert.hex2Dec(val.trim().replace("#", ""));
                        if (rgb != null) {
                            res = new DeviceRgb(rgb[0], rgb[1], rgb[2]);
                        } else {
                            res = null;
                        }

                    }
                }
            } catch (Exception e) {
                res = null;
            }
        }
        return res;
    }
    
    private static Color color2Color(java.awt.Color src) {
    	if (src == null) {
    		return null;
    	} else {
    		return new DeviceRgb(src.getRed(), src.getGreen(), src.getBlue());
    	}
    }

    private static java.awt.Color getColorField(String name) {
        try {
            Field field = Class.forName("java.awt.Color").getField(name.trim());
            return (java.awt.Color) field.get(null);
        } catch (Exception e) {
            return null;
        }
    }
    
    public static String toString(Color color) {
    	if (color == null) {
    		return null;
    	} else {
    		float[] vals = color.getColorValue();
    		return (int)(vals[0] * 255) + "," + (int)(vals[1] * 255) + "," + (int)(vals[2] * 255);
    	}
    }

    public static void main(String[] args) {
        System.out.println(toString(fromString(" yellow")));
        System.out.println(toString(fromString(" Ff00ff")));
        System.out.println(toString(fromString("#00ff34 ")));
        System.out.println(toString(fromString("125 , 36,45")));
        System.out.println(toString(fromString("123,4")));
        System.out.println(toString(fromString("8,there,7")));
        System.out.println(toString(fromString("orange")));
        System.out.println(toString(fromString("purple")));
    }
}
