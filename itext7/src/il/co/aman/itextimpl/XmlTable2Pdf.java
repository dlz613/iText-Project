package il.co.aman.itextimpl;

/*import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;*/
import il.co.aman.apps.Misc;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import org.xml.sax.Attributes;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;

/**
 * Creates a _table based on XML data. The table is created as a {@link PdfTable} object, for insertion into a PDF.<br>
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
public class XmlTable2Pdf extends XmlTable {
    
//    ArrayList<PdfTable.Cell> cells;
//    PdfTable.Cell[][] rows;
//    PdfTable.Cell currentCell;
//    PdfTable _table;
//    PdfFont font, headerFont;
//    
//    final static String[] FONTS14 = {"Courier", "Courier-Bold", "Courier-BoldOblique", "Courier-Oblique",
//        "Helvetica", "Helvetica-Bold", "Helvetica-BoldOblique", "Helvetica-Oblique", "Times-Roman",
//        "Times-Bold", "Times-BoldItalic", "Times-Italic", "Symbol", "ZapfDingbats"};
//    
//    // properties
//    
//    public PdfTable getPdfTable() {
//        return this._table;
//    }    
//    
//    public XmlTable2Pdf(String fontdir) {
//        if (fontdir != null) {
//            PdfFontFactory.registerDirectory(fontdir);
//        }
//        this._table = null;
//        this._headerRows = 1;
//        this._fontname = "Helvetica";
//        this._fontsize = 12f;
//        this._fontcolor = Color.BLACK;
//        this._width = 400f;
//        this._columns = 2;
//        this._rows = 2;
//    }
//    
//    public XmlTable2Pdf(String fontdir, float width) {
//        this(fontdir);
//        this._width = width;
//    }
//    
//    @Override
//    public void startElement(String uri, String localName, String qName, Attributes attributes) {
//        if (qName.equals(TABLE) && !this.inTable) {
//            this._table = new PdfTable(this._columns);
//            //if (this._width > 0f) {
//            this._table.setTotalWidth(this._width);
//            this._table.setLockedWidth(true);
//            //}
//            this.inTable = true;
//            this.rowcount = 0;
//            this.rows = new PdfTable.Cell[this._rows][this._columns];
//        }
//        else if (qName.equals(ROW) && this.inTable && !this.inRow && (this.rowcount < this._rows)) {
//            this.cells = new ArrayList<PdfTable.Cell>();
//            
//            this.inRow = true;
//            this.rowcount += 1;
//            this.cellcount = 0;
//        }
//        else if (qName.equals(CELL) && this.inRow && !this.inCell && this.cellcount < this._columns) {
//            this.currentCell = this._table.new Cell();
//            this.inCell = true;
//            this.cellcount += 1;
//            if (attributes.getValue(COLSPAN) != null) {
//                this.currentCell.setColspan(Integer.parseInt(attributes.getValue(COLSPAN)));
//            }
//        }
//        this.text().setLength(0);
//    }
//
//    @Override
//    public void endElement(String uri, String localName, String qName) {
//        //if (qName.equals(CELL) && this.cellcount < this._columns) {
//        if (qName.equals(CELL) && this.inCell && (this._table.missingCells(this.cells) >= this.currentCell.getColspan())) {
//            Paragraph par;
//            if (this.rowcount <= this._headerRows) {
//                par = new Paragraph(this.text().toString().trim(), this.headerFont);
//            } else {
//                par = new Paragraph(this.text().toString().trim(), this.font);
//            }
//            this.currentCell.setPhrase(par);
//            this.cells.add(this.currentCell); 
//            //this.cellcount += 1; 
//            this.currentCell = null;
//            this.inCell = false;
//        }
//        else if (qName.equals(ROW) && this.inRow) {
//            int diff = this._table.missingCells(this.cells);
//            if (diff > 0) {
//                for (int i = 0; i < diff; i++) {
//                    this.currentCell = this._table.new Cell();
//                    this.currentCell.setPhrase(new Paragraph(" "));
//                    this.cells.add(this.currentCell);
//                }
//            }
//            //this._table.addRow(this.cells);
//            this.rows[this.rowcount - 1] = this.cells.toArray(new PdfTable.Cell[] {});
//            //this.cells = null;
//            this.inRow = false;
//        }
//        else if (qName.equals(TABLE)) {
//            //Misc.writeTextFile("C:\\Users\\davidz\\Desktop\\debug.txt", rows2string());
//            this._table.addRows(this.rows);
//            this.inTable = false;
//        }
//    }
//    
//    private void setFonts() {
//        BaseFont basefont = getFont(this._fontname);
//        this.font = new Font(basefont, this._fontsize);
//        this.headerFont = new Font(basefont, this._fontsize);  
//        
//        this.font.setColor(this._fontcolor);
//        this.font.setSize(this._fontsize);
//        
//        this.headerFont.setColor(this._fontcolor);
//        this.headerFont.setSize(this._fontsize);
//        this.headerFont.setStyle(this._headerStyle);
//    }
//    
//    private BaseFont getFont(String fontName) {
//        try {
//            if (checkArray(FONTS14, fontName)) { // these fonts can't use IDENTITY_H
//                return BaseFont.createFont(fontName, "Cp1252", false);
//            } else {
//                return (FontFactory.getFont(fontName, BaseFont.IDENTITY_H, BaseFont.EMBEDDED).getBaseFont());
//            }
//        } catch (Exception e) {
//            return (null);
//        }
//    }
//
//    private boolean checkArray(String[] arr, String val) {
//        for (Integer i = 0; i < arr.length; i++) {
//            if (arr[i].equals(val)) {
//                return (true);
//            }
//        }
//        return (false);
//    }
//    
//    private String rows2string() {
//        String res = "";
//        for (PdfTable.Cell[] row: rows) {
//            for (PdfTable.Cell row1 : row) {
//                res += row1.getVal() + ",";
//            }
//            res += "\r\n";
//        }
//        return res;
//    }
//    
//    public static String parse(XmlTable2Pdf handler, String XML, boolean namespaceSensitive) {
//        handler.setFonts();
//        return il.co.aman.formit.GenericSAX.parse(handler, XML, namespaceSensitive);
//    }

    public static void main(String[] args) {
        try {
            String path = "C:\\Users\\davidz\\Desktop\\tables.xml";
            String XML = il.co.aman.apps.Misc.readFile(path);

//            XmlTable2Pdf xmltable2pdf = new XmlTable2Pdf("\\\\formit6\\d$\\Formit\\ConvertIT\\Resources\\Fonts");
//            xmltable2pdf.importParams();
//            String parseRes = XmlTable2Pdf.parse(xmltable2pdf, XML, false);
//            if (parseRes.equals("OK")) {
//                Document doc = new Document(PageSize.A4);
//                PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(new File("C:\\Users\\davidz\\Desktop\\table_from XML.pdf")));
//                doc.open();
//                PdfContentByte cb = writer.getDirectContent();
//
//                doc.add(new Paragraph("This is before the table."));
//                //doc.add(xmltable2pdf._table);
//                xmltable2pdf._table.addTableAbs(cb, 5f, 600f); // this doesn't seem to work if the width is zero, EVEN if lockedWidth is false.
//                doc.close();
//                writer.close();
//                System.out.println("Result: " + Misc.message(xmltable2pdf.error));
//                Misc.writeTextFile("C:\\Users\\davidz\\Desktop\\debug.txt", "Result: " + Misc.message(xmltable2pdf.error));
//            } else { // will get here if the XML is invalid
//                System.out.println("Parsing failed: " + parseRes);
//                Misc.writeTextFile("C:\\Users\\davidz\\Desktop\\debug.txt", "Parsing failed: " + parseRes);
//            }
        }
        catch (Exception e) {
            System.out.println("Catch: " + Misc.message(e));
            Misc.writeTextFile("C:\\Users\\davidz\\Desktop\\debug.txt", "Catch: " + Misc.message(e));
        }
        
    }
}
