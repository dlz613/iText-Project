package il.co.aman.itextimpl;

import com.itextpdf.kernel.colors.Color;

/**
 *
 * @author davidz
 */
public class TableDef {

    int ID;
    float[] colWidths;
    float width;
    String background;
    float[] border_widths; // order: left, bottom, right, top
    String[] border_colors, border_styles; //styles: solid, double

    public Color getBackground() {
        return Border.parseColor(this.background);
    }

    public PdfBorder getBorder_left() {
        return new PdfBorder(this.border_widths[0], this.border_colors[0], border_styles[0]);
    }

    public PdfBorder getBorder_bottom() {
        return new PdfBorder(this.border_widths[1], this.border_colors[1], border_styles[1]);
    }

    public PdfBorder getBorder_right() {
        return new PdfBorder(this.border_widths[2], this.border_colors[2], border_styles[2]);
    }

    public PdfBorder getBorder_top() {
        return new PdfBorder(this.border_widths[3], this.border_colors[3], border_styles[3]);
    }
    
    public float getWidth() {
        return this.width;
    }
    
    public TableDef(int ID, float[] colWidths, String background, float[] border_widths, String[] border_colors, String[] border_styles) {
    	this(ID, colWidths, 0, background, border_widths, border_colors, border_styles);
    }

    public TableDef(int ID, float[] colWidths, float width, String background, float[] border_widths, String[] border_colors, String[] border_styles) {
        this.ID = ID;
        this.colWidths = colWidths;
        this.width = width;
        this.background = background;
        this.border_widths = border_widths;
        this.border_colors = border_colors;
        this.border_styles = border_styles;
    }

    /**
     *
     * @param widths
     * @return
     */
    public static float[] colWidths(String widths) {
        String[] columns = widths.split(",");
        float[] cols = new float[columns.length];
        for (int i = 0; i < columns.length; i++) {
            cols[i] = Float.parseFloat(columns[i]);
        }
        return cols;
    }
    
    public static float totalWidth(float[] widths) {
        float sum = 0f;
        for (float width: widths) {
            sum += width;
        }
        return sum;        
    }

}
