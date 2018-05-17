package il.co.aman.itextimpl;

import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;

/**
 *
 * @author davidz
 */
public class PdfCell extends Cell {

    /**
     *
     */
    public enum CellType {HEADER, DETAIL, FOOTER}

    int rowspan, tableID, xpos, ypos;
    String text;
    CellType cellType;

    @Override
    public int getRowspan() {
        return this.rowspan;
    }

    public int getTableID() {
        return this.tableID;
    }

    public void setRowspan(int val) {
        this.rowspan = val;
    }

    public void setTableID(int val) {
        this.tableID = val;
    }
    
    public int getYpos() {
        return this.ypos;
    }
    
    public int getXpos() {
        return this.xpos;
    }
    
    public void setYpos(int val) {
        this.ypos = val;
    }
    
    public void setXpos(int val) {
        this.xpos = val;
    }

    public String getText() {
        return this.text;
    }

    public CellType getType() {
        return this.cellType;
    }

    public void setType(CellType type) {
        this.cellType = type;
    }

    public PdfCell() {
        super();
        this.tableID = -1;
        this.cellType = CellType.DETAIL;
        this.xpos = -1;
        this.ypos = -1;
    }

//    @Override
//    public void setPhrase(Paragraph phrase) {
//        super.setPhrase(phrase);
//        this.text = phrase.getContent();
//    }

    @Override
    public String toString() {
        String res = "Text: " + this.getText() + "\n";
        res += "colspan: " + Integer.toString(this.getColspan()) + "\n";
        res += "type: " + this.cellType.toString() + "\n";
        res += "Table ID: " + Integer.toString(this.tableID) + "\n";
        res += "Position: (" + Integer.toString(this.xpos) + ", " + Integer.toString(this.ypos) + ")\n";
        return res;
    }

    /**
     *
     * @param border
     * @param side 0, 1, 2, 3 (left, bottom, right, top)
     */
    public void addBorder(PdfBorder border, int side) {
        addBorder(border, side, this);
    }
    
    /**
     * 
     * @param border
     * @param side
     * @param cell 
     */
    public static void addBorder(PdfBorder border, int side, Cell cell) {
        switch(side) {
            case 0:
                cell.setBorderLeft(new SolidBorder(border.getColor(), border.getWidth()));
                break;
            case 1:
                cell.setBorderBottom(new SolidBorder(border.getColor(), border.getWidth()));
                break;
            case 2:
                cell.setBorderRight(new SolidBorder(border.getColor(), border.getWidth()));
                break;
            case 3:
                cell.setBorderTop(new SolidBorder(border.getColor(), border.getWidth()));
                break;
        }        
    }
}
