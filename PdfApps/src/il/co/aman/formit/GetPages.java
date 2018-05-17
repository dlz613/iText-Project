package il.co.aman.formit;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import il.co.aman.formit.sendit.PdfSource;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Extracts pages from a PDF.
 *
 * @author davidz
 */
public class GetPages {

    Exception _ex;

    public Exception getException() {
        return this._ex;
    }

    /**
     * Extracts the specified pages from <code>inPDF</code>
     *
     * @param inPDF The input PDF.
     * @param outPDF An {@link OutputStream} object to receive the generated
     * PDF.
     * @param pages An integer array containing the pages to be copied - the
     * first page of the document is <b>1</b>. <br><b>Note:</b> If <b>some</b> of the page numbers are
     * invalid, those pages are silently skipped (if all of the page numbers are invalid, an {@link Exception} will be thrown).
     * @return <code>false</code> if unsuccessful. In this case, the
     * {@link Exception} will be found in <code>getException()</code>.
     */
    public boolean copyPages(PdfSource inPDF, OutputStream outPDF, int[] pages) {
        try {
            PdfReader reader = inPDF.getReader();
            Document doc = new Document();
            PdfWriter writer = PdfWriter.getInstance(doc, outPDF);
            doc.open();
            PdfContentByte cb = writer.getDirectContent();
            for (int pg : pages) {
                _copyPage(doc, reader, writer, cb, pg);
            }
            doc.close();
            this._ex = null;
            return true;
        } catch (DocumentException e) {
            this._ex = e;
            return false;
        }
    }

    private static void _copyPage(Document doc, PdfReader reader, PdfWriter writer, PdfContentByte cb, int pg) {
        if (pg > 0 && pg <= reader.getNumberOfPages()) {
            PdfImportedPage page = writer.getImportedPage(reader, pg);
            Rectangle rect = reader.getPageSize(pg);
            float x = rect.getWidth();
            float y = rect.getHeight();
            float angle = reader.getPageRotation(pg);
            float rad;
            if (angle == 0f) {
                doc.setPageSize(new Rectangle(x, y));
                doc.newPage();
                cb.addTemplate(page, 1, 0, 0, 1, 0, 0);
            } else if (angle == 90f) {
                doc.setPageSize(new Rectangle(y, x));
                doc.newPage();
                rad = 270 / 57.2958f;
                cb.addTemplate(page, new Float(Math.cos(rad)), new Float(Math.sin(rad)), new Float(-Math.sin(rad)), new Float(Math.cos(rad)), 0, x);
            } else if (angle == 180f) {
                doc.setPageSize(new Rectangle(x, y));
                doc.newPage();
                rad = 180 / 57.2958f;
                cb.addTemplate(page, new Float(Math.cos(rad)), new Float(Math.sin(rad)), new Float(-Math.sin(rad)), new Float(Math.cos(rad)), x, y);
            } else if (angle == 270f) {
                doc.setPageSize(new Rectangle(y, x));
                doc.newPage();
                rad = 90 / 57.2958f;
                cb.addTemplate(page, new Float(Math.cos(rad)), new Float(Math.sin(rad)), new Float(-Math.sin(rad)), new Float(Math.cos(rad)), y, 0);
            } else {
                doc.setPageSize(new Rectangle(x, y));
                doc.newPage();
                cb.addTemplate(page, 1, 0, 0, 1, 0, 0);
            }
        }
    }

    public static void main(String[] args) {
        GetPages gp = new GetPages();
        try {
            java.io.FileOutputStream fos = new java.io.FileOutputStream("C:\\Users\\davidz\\Desktop\\somepage.pdf");
            if (!gp.copyPages(new PdfSource("C:\\Users\\davidz\\Desktop\\PDFs\\MakeUseOf.com-Dropbox.pdf"), fos, new int[]{10, 60})) {
                System.out.println(gp.getException());
            }
            fos.close();
        } catch (IOException e) {
            System.out.println(e);

        }
    }
}
