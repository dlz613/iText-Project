package il.co.aman.itextimpl;

import il.co.aman.apps.Misc;

import org.xml.sax.Attributes;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.Style;

/**
 * Creates a table based on XML data. The table is created as HTML.<br>
 * Sample XML:<br>
 *<pre><code>&lt;?xml version="1.0" encoding="UTF-8"?>
 *&lt;_table columns="2">
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
public class XmlTable2Html extends XmlTable {
    
    String _html;
        
    public String getHtml() {
        return this._html;
    }

    public XmlTable2Html() {
        this._html = "";
        this._headerRows = 1;
        this._fontsize = 12f;
        this._fontname = "Helvetica";
        this._fontcolor = ColorConstants.BLACK;
        this._headerStyle = new Style();
        this._width = 0f;
    }
    
    public XmlTable2Html(float width) {
        this();
        this._width = width;
    }
    
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (qName.equals(TABLE) && !this.inTable) {
            String style = " style='border:1px solid black; border-collapse: collapse;";
            //style += "font-family: \"" + this._fontname + "\"; font-size:" + new Float(this._fontsize).toString() + 
            //        "; color: " + Misc.colorEncode(this._fontcolor) + ";"; commented out because of incompatibility with method type in Misc - fix!
            if (this._width > 0f) {
                style += "width:" + new Integer((int)this._width).toString() + "px";
            }
            this._html = "<table" + style + "'>";
            Integer col_width = 100 / this._columns;
            for (int i = 0; i < this._columns; i++) {
                this._html += "<col width='" + col_width.toString() + "%'/>";
            }
            
            this.rowcount = 0;
            this.inTable = true;
            this.inRow = false;
        }
        else if (qName.equals(ROW) && this.inTable && !this.inRow && this.rowcount < this._rows) {
            this.rowcount += 1;
            this._html += "<tr>";
            
            this.cellcount = 0;
            this.inRow = true;
            this.inCell = false;
        }
        else if (qName.equals(CELL) && this.inRow && !this.inCell && this.cellcount < this._columns) {
            this._html += "<td";
            String style = " style='border:1px solid black;";
//            if (this.rowcount <= this._headerRows && this._headerStyle != 0) {
//                style += style2Css(this._headerStyle);
//            }
            this._html += style + "'";
            if (attributes.getValue(COLSPAN) != null) {
                this._html += " colspan=\"" + new Integer(Integer.parseInt(attributes.getValue(COLSPAN))).toString() + "\"";
            }
//            if (attributes.getValue(ROWSPAN) != null) {
//                this._html += " rowspan=\"" + new Integer(Integer.parseInt(attributes.getValue(ROWSPAN))).toString() + "\"";
//            }
            this._html += ">";
            
            this.cellcount += 1;
            this.inCell = true;
        }
        this.text().setLength(0);
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (qName.equals(CELL) && this.inCell) {
            this._html += this.text().toString().trim() + "</td>";
            this.inCell = false;
        }
        else if (qName.equals(ROW) && this.inRow) { // checking this.inRow just in case the XMl has more rows than allowed
            this._html += "</tr>"; 
            this.inRow = false;
        }
        else if (qName.equals(TABLE)) {
            this._html += "</table>";
        }
    }
    
//    private String style2Css(Style style) {
//        String res = "";
//        if (style.) {
//            res += "font-weight: bold;";
//        }
//        if ((style & Font.ITALIC) == Font.ITALIC) {
//            res += "font-style: italic;";
//        }
//        
//        return res;
//    }

    public static void main(String[] args) {
        try {
            String path = "C:\\Users\\davidz\\Desktop\\tables.xml";
            String XML = il.co.aman.apps.Misc.readFile(path);

            XmlTable2Html xmltable2html = new XmlTable2Html(400f);
            xmltable2html.importParams();
            XmlTable2Html.parse(xmltable2html, XML);
            String before = "<html><head><title>Table doc</title></head><body><span>";
            String after = "</span></body></html>";
            
            Misc.writeTextFile("C:\\Users\\davidz\\Desktop\\table from xml.html", before + xmltable2html.getHtml() + after);
        }
        catch (Exception e) {
            System.out.println(il.co.aman.apps.Misc.message(e));
            Misc.writeTextFile("C:\\Users\\davidz\\Desktop\\debug.txt", il.co.aman.apps.Misc.message(e));
        }
    }
}
