package il.co.aman.formit;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.pdfcleanup.PdfCleanUpLocation;
import com.itextpdf.text.pdf.pdfcleanup.PdfCleanUpProcessor;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <b>Completely</b> removes selected content from a PDF.<br>Requires iText from
 * at least version 5.5.4.
 *
 * @author davidz
 */
public class RemoveAreas {

    /**
     * Completely removes the content inside the {@link Rectangle}.
     *
     * @param src
     * @param page Which page of the source PDF to edit.
     * @param dest
     * @param area
     * @param color The color to apply in place of the removed content.
     * @throws DocumentException
     * @throws IOException
     */
    public static void removeArea(PdfReader src, int page, PdfStamper dest, Rectangle area, BaseColor color) throws DocumentException, IOException {
        List<PdfCleanUpLocation> cleanUpLocations = new ArrayList<PdfCleanUpLocation>();
        cleanUpLocations.add(new PdfCleanUpLocation(page, area, color));
        PdfCleanUpProcessor cleaner = new PdfCleanUpProcessor(cleanUpLocations, dest);
        cleaner.cleanUp();
        dest.close();
        src.close();
    }

    /**
     * Completely removes the content which is <b>outside</b> of the
     * {@link Rectangle}
     *
     * @param src
     * @param page Which page of the source PDF to edit.
     * @param dest
     * @param area
     * @param color The color to apply in place of the removed content.
     * @param corr Correction factor - by how many points to extend the bottom
     * of the selected area (to be used if all of the text disappears - which
     * happens sometimes if the cut PDF is part of a table, apparently)
     * @throws DocumentException
     * @throws IOException
     */
    public static void removeExceptArea(PdfReader src, int page, PdfStamper dest, Rectangle area, BaseColor color, Integer corr) throws DocumentException, IOException {
        Rectangle[] toRemove = new Rectangle[4];
        List<PdfCleanUpLocation> cleanUpLocations = new ArrayList<PdfCleanUpLocation>();
        // the "fives" were added in case the area is JUST adjacent to part of the text, so that part shouldn't be cut
        toRemove[0] = new Rectangle(0f, area.getTop() + 5 + corr /* + corr here restored to solve Mei Eden problem*/, src.getPageSize(page).getWidth(), src.getPageSize(page).getHeight()); // top
        toRemove[1] = new Rectangle(0f, area.getTop(), area.getLeft() - 5/*- corr*/, area.getBottom()); // left
        toRemove[2] = new Rectangle(area.getRight() + 5/*+ corr*/, area.getTop(), src.getPageSize(page).getWidth(), area.getBottom()); // right
        toRemove[3] = new Rectangle(0f, 0f, src.getPageSize(page).getWidth(), area.getBottom() - corr - 5); // bottom
        for (Rectangle remove : toRemove) {
            //System.out.println(String.format("%.1f, %.1f, %.1f, %.1f", remove.getLeft(), remove.getBottom(), remove.getWidth(), remove.getHeight()));
            cleanUpLocations.add(new PdfCleanUpLocation(page, remove, color));
        }
        PdfCleanUpProcessor cleaner = new PdfCleanUpProcessor(cleanUpLocations, dest);
//        try {        	
        cleaner.cleanUp();
//        }
//        catch (java.lang.RuntimeException/*com.itextpdf.text.exceptions.UnsupportedPdfException*/ e) {
//        	// This catches "The color depth 1 is not supported" Exception,
        	// resulting in a valid cut PDF with the offending picture removed - need to decide which option is preferred.
        	// For now we are going with letting the PDF be invalid, so an image will be inserted instead.
//        }
//        finally {
        dest.close();
        src.close();
//        }
    }

    public static final String SRC = "C:\\Users\\davidz\\Desktop\\PDFs\\229.pdf";
    public static final String DEST = "C:\\Users\\davidz\\Desktop\\page229_removed_content.pdf";
    public static final String DEST2 = "C:\\Users\\davidz\\Desktop\\page229_removed_content_except.pdf";

    public static void main(String[] args) throws IOException, DocumentException {
//        java.security.CodeSource code = com.itextpdf.text.pdf.parser.PdfContentStreamProcessor.class.getProtectionDomain().getCodeSource();
//        if (code != null) {
//            java.net.URL jar = code.getLocation();
//            System.out.println(jar.toString());
//        }
        java.security.CodeSource src = org.apache.commons.imaging.Imaging.class.getProtectionDomain().getCodeSource();
        // or: java.security.CodeSource src = Class.forName("org.apache.commons.codec.binary.Base64").getProtectionDomain().getCodeSource(); but then need to catch ClassNotFoundException
        if (src != null) {
            java.net.URL jar = src.getLocation();
            System.out.println(jar.toString());
        }
        System.exit(0);
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        PdfReader reader = new PdfReader(SRC);
        removeArea(reader, 1, new PdfStamper(reader, new FileOutputStream(DEST)), new Rectangle(97f, 405f, 480f, 445f), BaseColor.WHITE);
        removeExceptArea(reader, 1, new PdfStamper(new PdfReader(SRC), new FileOutputStream(DEST2)), new Rectangle(97f, 405f, 480f, 445f), BaseColor.WHITE, 0);
    }

}
