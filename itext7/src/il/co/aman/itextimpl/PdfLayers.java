package il.co.aman.itextimpl;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfXObject;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import il.co.aman.apps.Misc;
import il.co.aman.formit.RemoveAreas;
import il.co.aman.formit.sendit.PdfResult;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Enables embedding a page from a PDF in another PDF. Designed to be used
 * during creation of the composite PDF.<br>
 * At the present time, does not support resizing the imported page.
 *
 * @author davidz
 */
public class PdfLayers {

    private void init(String fontdir) {
        FontFactory.registerDirectory(fontdir);
    }

    /**
     * Embeds a page from one PDF in another<br>
     * Positions the page at 0f, 0f
     *
     * @param cb
     * @param reader The imported PDF
     * @param writer The PDF being created
     * @param pageNum Which page to take from the imported PDF
     */
    public static void addLayer(PdfContentByte cb, PdfReader reader, PdfWriter writer, int pageNum) {
        addLayer(cb, reader, writer, pageNum, 0f, 0f);
    }

    /**
     * Embeds <b>part</b> of a page from one PDF in another
     *
     * @param cb
     * @param reader The imported PDF
     * @param writer The PDF being created
     * @param pageHeight The height (in points) of the page on which the
     * imported page is being added
     * @param pageNum Which page to take from the imported PDF
     * @param left X-position for the imported page (left = 0f)
     * @param top Y-position for the imported page (top = 0f)
     * @param x This and the next three parameters define what area of the
     * imported page to embed.
     * @param y
     * @param width
     * @param height
     */
    public static void addLayer(PdfContentByte cb, PdfReader reader, PdfWriter writer, float pageHeight, int pageNum,
            float left, float top, float x, float y, float width, float height) {
        boolean bClip = (x + y + width + height) > 0f;
        PdfImportedPage page = writer.getImportedPage(reader, pageNum);
        if (width <= 0f) {
            width = page.getWidth();
        }
        if (height <= 0f) {
            height = page.getHeight();
        }
        float y_end = pageHeight - top;

        cb.saveState();
        if (bClip) {
            cb.rectangle(left, y_end - height, width, height); // define the "window" into the page which will be visible.
            cb.clip();
            cb.newPath();
        }
        cb.addTemplate(page, left - x, y - top); // place the layer so that the area we want will be located in the "window" of the PdfContentByte
        cb.restoreState();
    }

    /**
     * Adds a cutout at an arbitrary location.<br>
     * This is the method which is currently used by FormIT.
     *
     * @param cb
     * @param reader
     * @param writer
     * @param pageNum
     * @param x The location where the cutout is to be added.
     * @param y The location where the cutout is to be added.
     */
    public static void addLayer(PdfContentByte cb, PdfReader reader, PdfWriter writer, int pageNum, float x, float y) {
        try {
            PdfImportedPage page = writer.getImportedPage(reader, pageNum);
            cb.addTemplate(page, x, y);
        } catch (Exception e) {
            System.out.println("PdfLayers.addLayer threw an Exception: " + Misc.message(e));
        }
    }
    
    /**
     * For iText 7
     * Adds a cutout at an arbitrary location.<br>
     * This is the method which is currently used by FormIT.
     *
     * @param pdf
     * @param canvas
     * @param source
     * @param pageNum
     * @param x
     * @param y
     */
    public static void addLayer(PdfDocument target, PdfCanvas canvas, PdfDocument source, int pageNum, float x, float y) {
    	try {
    		PdfPage page = source.getPage(pageNum).copyTo(target);
    		canvas.addXObject(new PdfFormXObject(page), x, y);
    		source.close();
    	}
    	catch (Exception e) {
    		System.out.println("PdfLayers.addLayer threw an Exception: " + Misc.message(e));
    	}
    }

    /**
     * Produces a small PDF containing just the appropriate cutout of the PDF -
     * thereby enabling pasting it onto many PDFs (of the same page size)
     * without repeating all of the work each time.
     *
     * @param pdfIn
     * @param pageNum
     * @param pageWidth The width of the target PDF page
     * @param pageHeight The height of the target PDF page
     * @param left
     * @param top
     * @param x This and the next three parameters define what area of the
     * imported page to embed.
     * @param y
     * @param width
     * @param height
     * @return A PDF the same size as the ultimate target, with the cutout
     * placed in the same place it will be in the target.
     * @throws DocumentException
     * @throws java.io.IOException
     */
    public static PdfResult cutLayer(byte[] pdfIn, int pageNum, float pageWidth, float pageHeight,
            float left, float top, float x, float y, float width, float height) throws DocumentException, IOException {
        PdfReader reader = _removeText(pdfIn, pageNum, x, y, width, height);
        //PdfReader reader = new PdfReader(reader);
        Document doc = new Document(new Rectangle(pageWidth, pageHeight));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(doc, bos);
        writer.setPdfVersion(PdfWriter.VERSION_1_5);
        doc.open();
        PdfContentByte cb = writer.getDirectContent();
        addLayer(cb, reader, writer, pageHeight, pageNum, left, top, x, y, width, height);
        doc.close();
        writer.close();
        return new PdfResult(bos.toByteArray());
    }

    /**
     * Produces a small PDF the size of (and containing just) the cutout, which
     * can then be added to the target PDF in any location.<br>
     * This is the method which is currently used by FormIT.
     *
     * @param pdfIn
     * @param pageNum
     * @param x The location of the cutout in the source PDF
     * @param y The location of the cutout in the source PDF
     * @param width The size of the cutout
     * @param height The size of the cutout
     * @return
     * @throws DocumentException
     * @throws java.io.IOException
     */
    public static PdfResult cutLayer(byte[] pdfIn, int pageNum, float x, float y, float width, float height)
            throws DocumentException, IOException {
        PdfReader reader = _removeText(pdfIn, pageNum, x, y, width, height);
        Document doc = new Document(new Rectangle(width, height));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(doc, bos);
        writer.setPdfVersion(PdfWriter.VERSION_1_5);
        doc.open();
        PdfContentByte cb = writer.getDirectContent();
        addLayer(cb, reader, writer, pageNum, -x, height - reader.getPageSize(pageNum).getHeight() + y);
        doc.close();
        writer.close();

        return new PdfResult(bos.toByteArray());
    }

    //*** Private methods
    private Exception createLayeredPdf(String path, String layerpath, boolean layerfirst) {
        Exception res = null;
        try {
            Document doc = new Document(PageSize.A3);
            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(new File(path)));
            writer.setPdfVersion(PdfWriter.VERSION_1_5);
            doc.open();
            PdfContentByte cb = writer.getDirectContent();
            PdfReader reader = new PdfReader(layerpath);
            if (layerfirst) {
                addLayer(cb, reader, writer, 1, 10f, PageSize.A3.getHeight() - 425f - 10f); // 595f, 842f (A4)  60f, 50f, 378f, 249f    49f, 60f, 420f, 425f
            }

            /*Little.addImage(cb, Image.getInstance("C:\\Users\\davidz\\Desktop\\images\\bigsignature.png"), 50, 20, 200, 500, "http://www.google.com/");
            //Little.addText(cb, "This is some text", 50, 750, 200, 50, "Courier New", 12, BaseColor.BLACK);
            Little.addText(doc, cb, "David Zalkin", 240, 638, 300, 50, "Courier New", 12, BaseColor.BLUE);
            Little.addText(doc, cb, "123-4567", 240, 608, 300, 50, "Courier New", 12, BaseColor.BLUE);
            Little.addText(doc, cb, "52", 240, 580, 300, 50, "Courier New", 12, BaseColor.BLUE);
            Little.addText(doc, cb, "$1,234,500", 240, 555, 300, 50, "Courier New", 12, BaseColor.BLUE);*/
//            com.lowagie.text.Image img = com.lowagie.text.Image.getInstance("C:\\Users\\davidz\\Desktop\\jet.jpg");
//            Little.addImage(cb, img, 300, 400, 300, 200, "http://www.google.com/");
            //Little.addImage(doc, img, 300f, 400f, 300f, 200f, "http://www.aman.co.ii/");

            if (!layerfirst) {
                addLayer(cb, reader, writer, 1, 10f, PageSize.A3.getHeight() - 425f - 10f);
            }
            doc.close();
            writer.close();
        } catch (FileNotFoundException e) {
            res = e;
        } catch (DocumentException e) {
            res = e;
        } catch (IOException e) {
            res = e;
        }

        return res;
    }

    private static PdfReader _removeText(byte[] pdfIn, int pageNum, float x, float y, float width, float height) throws DocumentException, IOException, RuntimeException {
        int step = 10;
        int maxCorr = 2000; // maximum correction factor to attempt - prevents infinite loop in case of PDF without any text
        ByteArrayOutputStream bos = null;
        Integer corr = 0;
        PdfReader reader = new PdfReader(pdfIn);
        if (x > 5f || y > 5f || Math.abs(width - reader.getPageSize(pageNum).getWidth()) > 5f || Math.abs(height - reader.getPageSize(pageNum).getHeight()) > 5f) {
	        Rectangle area = new Rectangle(x, reader.getPageSize(pageNum).getHeight() - y - height,
	                width + x, reader.getPageSize(pageNum).getHeight() - y);
	        do {
	            corr += step;
	            reader = new PdfReader(pdfIn);
	            bos = _removeText(reader, pageNum, area, BaseColor.WHITE, corr);
	        } while (!_checkForText(new PdfReader(bos.toByteArray()), pageNum) && corr < maxCorr);
	        if (corr > 0 && corr < maxCorr) {
	            System.out.println("PdfLayers.removeText - Applied correction factor: " + corr.toString());
	        }
	        return new PdfReader(bos.toByteArray());
        } else {
        	return reader;
        }
    }

    private static ByteArrayOutputStream _removeText(PdfReader reader, Integer pageNum, Rectangle area, BaseColor color, Integer corr)
            throws DocumentException, IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PdfStamper stamper = new PdfStamper(reader, bos);
        RemoveAreas.removeExceptArea(reader, pageNum, stamper, area, color, corr);
        return bos;
    }

    private static boolean _checkForText(PdfReader reader, int pagenum) throws IOException {
        String res = PdfTextExtractor.getTextFromPage(reader, pagenum);
        return res.trim().length() > 0;
    }

    public static void main(String[] args) {
        //final String basepdf = "C:\\Users\\davidz\\Desktop\\base background.pdf";
        PdfLayers pdflayers = new PdfLayers();
        pdflayers.init("C:\\Users\\davidz\\Desktop\\fonts");
        try {
            //PdfResult cutPdf = cutLayer(Misc.readBinFile("C:\\Users\\davidz\\Desktop\\foo bank form background.pdf"), 1, 49f, 60f, 420f, 425f)"C:\\Users\\davidz\\Documents\\problems\\Mei Eden\\Word Insert\\Akt_install.pdf";Documents\\problems\\Mei Eden\\Word Insert\\Akt_install.pdf
            //PdfResult cutPdf = cutLayer(new PdfReader("C:\\Users\\davidz\\Desktop\\Apache.pdf"), 1, 49f, 160f, 420f, 325f);רגו�?ציה ר�?וונטית �?שנת הדוח ש�? 2015 סופי - פור�?יט.pdf
            //byte[] pdf = Misc.readBinFile("C:\\Users\\davidz\\Documents\\problems\\Mei Eden\\Word Insert\\Akt_install.pdf");
            byte[] pdf = Misc.readBinFile("\\\\formit7\\c$\\TEMP\\Cellcom\\f1db7d91-f75b-42ac-b9ef-9ed0b854d159.pdf");//PDFs\\229
            pdf = Misc.readBinFile("C:\\Users\\davidz\\Desktop\\A4.pdf");
            PdfResult cutPdf = cutLayer(pdf, 1, 0f, 0f, PageSize.A4.getWidth(), PageSize.A4.getHeight());
                    //*PageSize.A4.getWidth(), PageSize.A4.getHeight(), 0f, 200f, */ 0f/*49f*/, 0.25f/*60f 70f*/, 578.55f, 213.25f/*425f*/);
            ///*PageSize.A4.getWidth(), PageSize.A4.getHeight(), 0f, 200f, */ 0f/*49f*/, 0f/*60f 70f*/, 578.55f, 513.25f/*425f*/);
            //PdfResult cutPdf = cutLayer(Misc.readBinFile("C:\\Users\\davidz\\Desktop\\PDFs\\Vol 7 Slifkin.pdf"), 1, /*PageSize.A4.getWidth(), PageSize.A4.getHeight(), 0f, 200f, */0f/*49f*/, 300f/*60f 70f*/, PageSize.A4.getWidth()/*420f*/, 337f/*425f*/);
            System.out.println(cutPdf.writeFile("C:\\Users\\davidz\\Desktop\\cutPdf7.pdf"));
            java.awt.Desktop.getDesktop().open(new File("C:\\Users\\davidz\\Desktop\\cutPdf7.pdf"));
        } catch (DocumentException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        } catch (RuntimeException e) {
        	System.out.println(e);
        }
    }

}
