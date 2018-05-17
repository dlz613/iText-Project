package il.co.aman.formit;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import il.co.aman.formit.sendit.PdfSource;
import java.util.ArrayList;
import java.util.List;

/**
 * Returns the positions dimensions of fields in a PDF form.<br>
 * Requires iText 5.
 *
 * @author davidz
 */
public class FieldPositions {

    /**
     * Returns the positions and dimensions of all of the fields in the PDF.
     *
     * @param src
     * @return
     */
    public static ArrayList<Position> getPositions(PdfSource src) {
        return getPositions(src, null);
    }

    /**
     * Returns the positions and dimensions of all of the fields in the PDF
     * whose names begin with <code>nameStart</code>.
     *
     * @param src
     * @param nameStart
     * @return
     */
    public static ArrayList<Position> getPositions(PdfSource src, String nameStart) {
        PdfReader reader = src.getNewReader();
        AcroFields fields = reader.getAcroFields();
        ArrayList<Position> res = new ArrayList<Position>();

        for (String fieldname : fields.getFields().keySet().toArray(new String[]{})) {
            if (nameStart == null || fieldname.startsWith(nameStart)) {
                List<AcroFields.FieldPosition> positions = fields.getFieldPositions(fieldname);
                int page = positions.get(0).page;
                int type = fields.getFieldType(fieldname);
                Rectangle rect = positions.get(0).position;
                float left = rect.getLeft();
                float bTop = rect.getTop(); // the top starting from the bottom of the page
                float width = rect.getWidth();
                float height = rect.getHeight();

                Rectangle pageSize = reader.getPageSize(page);
                float pageHeight = pageSize.getTop();
                float top = pageHeight - bTop; // the "real" top.

                res.add(new Position(fieldname, type, page, left, bTop, width, height, top));
            }
        }
        return res;
    }

    public static void main(String[] args) throws java.io.IOException {
        ArrayList<Position> positions = getPositions(new PdfSource("C:\\Documents and Settings\\davidz\\Desktop\\kalauto.pdf"));
        for (Position pos : positions) {
            System.out.println(pos.toString());
            System.out.println(pos.toCmString());
        }
    }

}
