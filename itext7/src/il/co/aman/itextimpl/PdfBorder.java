package il.co.aman.itextimpl;

import com.itextpdf.kernel.colors.Color;

import il.co.aman.apps.Misc;

/**
 *
 * @author davidz
 */
public class PdfBorder {

    public enum BorderStyles {NONE, SOLID, DOUBLE}

    private final float width;
    private final Color color;
    private final BorderStyles style;

    public float getWidth() {
        return width;
    }

    public Color getColor() {
        return color;
    }

    public BorderStyles getStyle() {
        return style;
    }

    /**
     *
     * @param width
     * @param color e.g. "128,255,54"
     * @param style solid, double, or <code>null</code>
     */
    public PdfBorder(float width, String color, String style) {
        this.width = width;
        this.color = Border.parseColor(color);
        this.style = parseBorderStyle(style);
    }

    static BorderStyles parseBorderStyle(String styleName) {
        if (styleName == null) {
            return BorderStyles.NONE;
        } else if (styleName.equals("solid")) {
            return BorderStyles.SOLID;
        } else if (styleName.equals("double")) {
            return BorderStyles.DOUBLE;
        } else {
            return BorderStyles.NONE;
        }
    }

    /**
     *
     * @return An object representing no border.
     */
    public static PdfBorder none() {
        return new PdfBorder(0f, null, null);
    }

}
