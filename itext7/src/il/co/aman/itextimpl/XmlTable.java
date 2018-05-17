package il.co.aman.itextimpl;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.layout.Style;

/**
 * Creates a _table based on XML data. The table is created as both a {@link PdfTable} object, for insertion into a PDF, and in {@link String} form as HTML.<br>
 * Sample XML:<br>
 *<pre><code>&lt;?xml version="1.0" encoding="UTF-8"?>
 *&lt;_table>
 *  &lt;row>
 *      &lt;cell>Customer No.&lt;/cell>
 *      &lt;cell>Address&lt;/cell>
 *  &lt;/row>
 *  &lt;row>
 *      &lt;cell>12345&lt;/cell>
 *      &lt;cell>1600 Pennsylvania Ave., Washington DC&lt;/cell>
 *  &lt;/row>
 *  &lt;row>
 *      &lt;cell>54321&lt;/cell>
 *      &lt;cell>28 Baruch Hirsh, Bnei Braq&lt;/cell>
 *  &lt;/row>
 *&lt;/table></code></pre>
 *
 * @author davidz
 */
public class XmlTable extends il.co.aman.formit.GenericSAX {

    // element names
    static final String CELL = "cell";
    static final String ROW = "row";
    static final String TABLE = "table";
    
    // attributes
    static final String COLSPAN = "colspan";
    //static final String ROWS = "rows"; // if we want to enable defining the number of rows in the data
    //static final String ROWSPAN = "rowspan";
    
    int colspan, rowspan, rowcount, cellcount, _headerRows, _columns, _rows;
	Style _headerStyle;
    String _fontname;
    Color _fontcolor;
    float _fontsize, _width;
    boolean inTable = false;
    boolean inRow = false;
    boolean inCell = false;
    Exception error;
    
    //properties
    
    public Style getHeaderStyle() {
        return this._headerStyle;
    }
    
    public void setHeaderStyle(Style style) {
        this._headerStyle = style;
    }
    
    public int getHeaderRows() {
        return this._headerRows;
    }
    
    public void setHeaderRows(int val) {
        if (val >= 0) {
            this._headerRows = val;
        }
    }
    
    public float getFontSize() {
        return this._fontsize;
    }
    
    public final void setFontSize(float val) {
        if (val > 0f) {
            this._fontsize = val;
        }
    }
    
    public String getFontName() {
        return this._fontname;
    }
    
    public final void setFontName(String val) {
        this._fontname = val;
    }
    
    public Color getFontColor() {
        return this._fontcolor;
    }
    
    public final void setFontColor(Color val) {
        this._fontcolor = val;
    }
    
    public int getColumns() {
        return this._columns;
    }
    
    public void setColumns(int val) {
        if (val > 0) {
            this._columns = val;
        }
    }
    
    public int getRows() {
        return this._rows;
    }
    
    public void setRows(int val) {
        if (val > 0) {
            this._rows = val;
        }
    }
    public void setWidth(float val) {
        if (val > 0f) {
            this._width = val;
        }
    }
    
    public void importParams() {
        importParams(XmlTableParams.defaultParams());
    }
    
    public void importParams(XmlTableParams params) {
        this.setColumns(params.getColumns());
        this.setFontColor(params.getFontColor());
        this.setFontName(params.getFontName());
        this.setFontSize(params.getFontSize());
        this.setHeaderRows(params.getHeaderRows());
        this.setHeaderStyle(params.getHeaderStyle());
        this.setRows(params.getRows());
        this.setWidth(params.getTableWidth());
    }

}
