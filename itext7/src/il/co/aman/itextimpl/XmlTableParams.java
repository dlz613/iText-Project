package il.co.aman.itextimpl;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.Style;

/**
 *
 * @author davidz
 */
public class XmlTableParams {
    
    float _tablewidth, _fontsize;
    String _fontname;
    Color _fontcolor;
    int _headerrows, _columns, _rows;
    Style _headerstyle;
    
    public float getTableWidth () {
        return this._tablewidth;
    }
    
    public float getFontSize() {
        return this._fontsize;
    }
    
    public String getFontName() {
        return this._fontname;
    }
    
    public Color getFontColor() {
        return this._fontcolor;
    }
    
    public int getHeaderRows() {
        return this._headerrows;
    }
    
    public Style getHeaderStyle() {
        return this._headerstyle;
    }
    
    public int getColumns() {
        return this._columns;
    }
    
    public int getRows() {
        return this._rows;
    }
    
    public static XmlTableParams defaultParams() {
        return new XmlTableParams(500f, /*Color.BLACK,*/Misc.colorDecode("2,192,197"), 16f, "NarkisTamMF-Light", 1, new Style().setBold().setItalic(), 3, 4);
    }
    
    public XmlTableParams(float tablewidth, Color fontcolor, float fontsize, String fontname, int headerrows, Style headerstyle, int columns, int rows) {
        this._tablewidth = tablewidth;
        this._fontcolor = fontcolor;
        this._fontsize = fontsize;
        this._fontname = fontname;
        this._headerrows = headerrows;
        this._headerstyle = headerstyle;
        this._columns = columns;
        this._rows = rows;        
    }
    
    public static Style createStyle(boolean bold, boolean italic) {
        Style res = new Style();
        if (bold) {
        	res.setBold();
            //res = res ^ Font.BOLD;
        }
        if (italic) {
        	res.setItalic();
            //res = res ^ Font.ITALIC;
        }
        return res;
    }
        
    public static void main(String[] args) {
        XmlTable2Pdf.main(null);
        XmlTable2Html.main(null);
    }

}
