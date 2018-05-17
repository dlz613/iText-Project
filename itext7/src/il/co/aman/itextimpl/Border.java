package il.co.aman.itextimpl;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants.LineJoinStyle;
import com.itextpdf.kernel.colors.DeviceRgb;

/**
 * Used for adding borders to a PDF.
 *
 * @author davidz
 */
public class Border {

    public enum BorderStyles {

        NONE, SOLID, DASHED, DOTTED, DOUBLE
    }
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

    public static Border none() {
        return new Border(0f, null, null);
    }

    public Border(float width, String color, String style) {
        this.width = width;
        this.color = parseColor(color);
        this.style = parseBorderStyle(style);
    }
    
    @Override
    public String toString() {
    	return Float.toString(this.width) + ", " + colorString(this.color) + ", " + this.style;
    }
    
    public static String colorString(Color color) {
    	if (color == null) {
    		return "null";
    	}
    	float[] rgb = color.getColorValue();
    	return "(" + (int)(rgb[0] * 255) + "," + (int)(rgb[1] * 255) + "," + (int)(rgb[2] * 255) + ")";
    }

    /**
     * 
     * @param RGB if <code>null</code> or "transparent", returns <code>null</code>. Otherwise must be either a decimal comma-separated triplet, or 
     * a hexadecimal CSS-style color, with the leading "#" optional. 
     * @return <code>null</code> if the parameter is invalid.
     */
    public static com.itextpdf.kernel.colors.Color parseColor(String RGB) {
        if (RGB == null || RGB.endsWith("transparent")) { // endsWith in place of equals, because sometimes there is #transparent
            return null;
        } 
        if (RGB.startsWith("#")) {
        	RGB = RGB.substring(1);
        }
        if (RGB.contains(",")) {
            String[] colors = RGB.split(",");
            return (new com.itextpdf.kernel.colors.DeviceRgb(Integer.parseInt(colors[0]), Integer.parseInt(colors[1]), Integer.parseInt(colors[2])));
        } else if (RGB.length() == 6) {
            return new com.itextpdf.kernel.colors.DeviceRgb(Integer.parseInt(RGB.substring(0, 2), 16), Integer.parseInt(RGB.substring(2, 4), 16), Integer.parseInt(RGB.substring(4, 6), 16));
        } else {
            return null;
        }
    }

    static BorderStyles parseBorderStyle(String styleName) {
        if (styleName == null) {
            return BorderStyles.NONE;
        } else if (styleName.equals("solid")) {
            return BorderStyles.SOLID;
        } else if (styleName.equals("dashed")) {
            return BorderStyles.DASHED;
        } else if (styleName.equals("dotted")) {
            return BorderStyles.DOTTED;
        } else if (styleName.equals("double")) {
            return BorderStyles.DOUBLE;
        } else {
            return BorderStyles.NONE;
        }
    }

    public static void doBorder(PdfPage page, Border border, float x1, float y1, float x2, float y2) {
        float dashedSize = border.width * 5;

        if (border.style != BorderStyles.NONE && border.width > 0 && border.color != null) {
        	PdfCanvas canvas = new PdfCanvas(page);
        	canvas.saveState();
            canvas.setColor(border.color, false);
            canvas.setLineWidth(border.width);
            canvas.setLineCapStyle(PdfCanvasConstants.LineCapStyle.BUTT);
            if (border.style == BorderStyles.DASHED) {
                canvas.setLineDash(dashedSize, 0);
            } else if (border.style == BorderStyles.DOTTED) {
                canvas.setLineDash(border.width / 2, border.width * 2, 0);
                canvas.setLineCapStyle(PdfCanvasConstants.LineCapStyle.ROUND);
            } else if (border.style == BorderStyles.DOUBLE) {
                canvas.setLineWidth(border.width / 3);
            }
            //canvas.setLineJoinStyle(PdfCanvasConstants.LineJoinStyle.ROUND);
            canvas.moveTo(x1, y1);
            canvas.lineTo(x2, y2);
            canvas.stroke();
            canvas.restoreState();
        }
    }

    public static void setBorders(PdfPage page, float x, float y, float x_end, float y_end,
            Border borderLeft, Border borderTop, Border borderRight, Border borderBottom) {

        doBorder(page, borderLeft, x, y, x, y_end);
        if (borderLeft.style == BorderStyles.DOUBLE) {
            doBorder(page, borderLeft, x + borderLeft.width, y + borderBottom.width, x + borderLeft.width, y_end - borderTop.width);
        }

        doBorder(page, borderTop, x, y_end, x_end, y_end);
        if (borderTop.style == BorderStyles.DOUBLE) {
            doBorder(page, borderTop, x + borderLeft.width, y_end - borderTop.width, x_end - borderRight.width, y_end - borderTop.width);
        }

        doBorder(page, borderRight, x_end, y, x_end, y_end);
        if (borderRight.style == BorderStyles.DOUBLE) {
            doBorder(page, borderRight, x_end - borderRight.width, y + borderBottom.width, x_end - borderRight.width, y_end - borderTop.width);
        }

        doBorder(page, borderBottom, x, y, x_end, y);
        if (borderBottom.style == BorderStyles.DOUBLE) {
            doBorder(page, borderBottom, x + borderLeft.width, y + borderBottom.width, x_end - borderRight.width, y + borderBottom.width);
        }
    }
    
    public static void main(String[] args) {
    	//System.out.println(parseColor("a032aa").getRed());
    	Border border = new Border(0.2f, "#ff0033", "solid");
    	System.out.println(border);
    	
    	Color color = parseColor("#ff23d5");
    	System.out.println(colorString(color));
    }
}
