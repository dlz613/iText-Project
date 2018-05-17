package il.co.aman.formit;

// see http://www.snowtide.com/ for licensing information
import com.itextpdf.text.pdf.parser.TaggedPdfReaderTool;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;
import com.snowtide.pdf.OutputTarget;
import com.snowtide.pdf.PDFTextStream;
import il.co.aman.apps.Misc;
import il.co.aman.formit.sendit.PdfResult;
import il.co.aman.formit.sendit.PdfSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * For text search, uses iText version 2.<br>If {@link PdfSearchParams}
 * <code>.USE_ITEXTPDF</code> is <code>true</code>, uses iText version 5 as
 * well.<br>If {@link PdfSearchParams} <code>.USE_PDFTEXTSTREAM</code> is
 * <code>true</code>, uses PdfTextStream as well.<br>If {@link PdfSearchParams}
 * <code>.TRY_REVERSE</code> is <code>true</code>, searches for the text both
 * straight and reversed (useful for Hebrew, for example).
 *
 * @author davidz
 */
public class PdfMisc {

    static final ActionResult NOT_FOUND = new PdfMisc().new ActionResult("Text not found.", ResultTypes.NOT_FOUND, false);

    public enum ResultTypes {

        OK, NOT_FOUND, WRONG_NUM_PAGES, OTHER
    };

    /**
     * @param source A {@link PdfSource} object.
     * @return The number of pages in the PDF.
     */
    public static Integer numPages(PdfSource source) {
        return source.getReader().getNumberOfPages();
    }

    /**
     * Checks if the PDF has precisely the specified number of pages.
     *
     * @param source A {@link PdfSource} object.
     * @param num The number of pages long it is supposed to be.
     * @return <code>true</code> if the number of pages is correct.
     */
    public static boolean checkNumPages(PdfSource source, Integer num) {
        return numPages(source) == num;
    }

    /**
     * Checks if the PDF has at least the specified number of pages.
     *
     * @param source A {@link PdfSource} object.
     * @param num The number of pages it is supposed to have.
     * @return <code>true</code> if the number of pages is correct.
     */
    public static boolean checkMinNumPages(PdfSource source, Integer num) {
        return numPages(source) >= num;
    }

    /**
     * Checks if the text is found anywhere in the PDF.
     *
     * @param source
     * @param searchtext
     * @return An {@link ActionResult} object. <br><b>Note:</b> Requires both
     * versions 2.6 (com.lowagie.text) and 5 (com.itextpdf.text) of iText.
     */
    public static ActionResult searchText(PdfSource source, String searchtext) {
        try {
            ActionResult res;
            Integer numpages = numPages(source);
            for (int i = 1; i <= numpages; i++) {
                res = _searchText(source, searchtext, i);
                if (res.ok) {
                    return res;
                }
            }
            return NOT_FOUND;
        } catch (Exception e) {
            return new PdfMisc().new ActionResult(Misc.message(e), ResultTypes.OTHER, false);
        }
    }

    /**
     * Determines if a PDF is valid.<br><b>NOTE:</b> doesn't work if the PDF is encrypted.
     *
     * @param path
     * @return
     */
    public static boolean isValid(String path) {
        try {
            new PdfReader(path); // we don't need to assign it to a variable - it is just to see if an Exception will be thrown.
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Validates all of the PDFs in the specified folder<br><b>NOTE:</b> doesn't work on encrypted PDFs.
     *
     * @param folder
     * @return An array of {@link String} arrays, each subarray consisting of
     * the path of the PDF in the first element, and either 'OK' or 'invalid' in the second element
     */
    public static String[][] validateFolder(String folder) {
        String[] files = Misc.filesByExtension(folder, "pdf", true);
        String[][] res = new String[files.length][];
        for (int i = 0; i < files.length; i++) {
            res[i] = new String[]{files[i], (isValid(files[i]) ? "OK" : "invalid")};
        }
        return res;
    }

    /**
     * This can find text which is not retrievable by either version of iText.
     *
     * @param source
     * @param page The 1-based page number.
     * @return <code>null</code> if an {@link IOException} occurred (which
     * should never happen).
     */
    private static String _getTextNew(PdfSource source, int page) {
        try {
            PDFTextStream pdfts = new PDFTextStream(new ByteArrayInputStream(source.getBytes()), "a pdf");
            StringBuilder text = new StringBuilder(1024);
            pdfts.getPage(page - 1).pipe(new OutputTarget(text));
            pdfts.close();
            return text.toString();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Checks if <code>searchtext</code> is found on the specified page.<br>
     * Uses iText version 2.<br> If {@link PdfSearchParams}.USE_ITEXTPDF is
     * <code>true</code>, uses iText version 5 as well.<br> if
     * {@link PdfSearchParams}.USE_PDFTEXTSTREAM is <code>true</code>, uses
     * PdfTextStream as well.
     *
     * @param source
     * @param searchtext
     * @param page The 1-based number of the page to be searched.
     */
    private static ActionResult _searchText(PdfSource source, String searchtext, int page) {
        String reversed = new StringBuilder(searchtext).reverse().toString(); // for Hebrew, for example, sometimes the text has to be reversed in order to be found
        PdfMisc.ActionResult found = new PdfMisc().new ActionResult("Text found.", ResultTypes.OK, true, page);
        try {
            byte[] content = source.getReader().getPageContent(page);
            String str = new String(content);
            if (str.contains(searchtext) || (PdfSearchParams.TRY_REVERSE && str.contains(reversed))) {
            	return found;
            }
            if (PdfSearchParams.USE_ITEXTPDF) {
                str = com.itextpdf.text.pdf.parser.PdfTextExtractor.getTextFromPage(source.getNewReader(), page, new com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy());
                if (str.contains(searchtext) || (PdfSearchParams.TRY_REVERSE && str.contains(reversed))) {
                	
                    return found;
                }
            }
            if (PdfSearchParams.USE_PDFTEXTSTREAM) {
                str = _getTextNew(source, page);
                if (str.contains(searchtext) || (PdfSearchParams.TRY_REVERSE && str.contains(reversed))) {
                    return found;
                }
            }
            return NOT_FOUND;
        } catch (IOException e) {
            return new PdfMisc().new ActionResult(Misc.message(e), ResultTypes.OTHER, false);
        }
    }

    /**
     * Checks if the text is found anywhere in the PDF, starting on page
     * <code>start</code>.
     *
     * @param source
     * @param searchtext
     * @param start
     * @return An {@link ActionResult} object. <br><b>Note:</b> Requires both
     * versions 2.6 (com.lowagie.text) and 5 (com.itextpdf.text) of iText, and
     * PDFTextStream.
     */
    public static ActionResult searchText(PdfSource source, String searchtext, int start) {
        try {
            ActionResult res;
            Integer numpages = numPages(source);
            for (int i = start; i <= numpages; i++) {
                res = _searchText(source, searchtext, i);
                if (res.ok) {
                    return res;
                }
            }
            return NOT_FOUND;
        } catch (Exception e) {
            return new PdfMisc().new ActionResult(Misc.message(e), ResultTypes.OTHER, false);
        }
    }

    /**
     * Determines which pages of the PDF (starting at a specified page) contain
     * <code>searchtext</code>. The pages containing the text must be sequential
     * with no gaps.
     *
     * @param source
     * @param searchtext
     * @param start The page to start the search.
     * @return An {@link ArrayList} object containing the numbers of the pages
     * which contain the text. If an error occurred, returns <code>null</code>.
     */
    public static ArrayList<Integer> getTextPages(PdfSource source, String searchtext, Integer start) {
        ArrayList<Integer> res = new ArrayList<Integer>();
        boolean started = false;
        try {
            Integer numpages = numPages(source);
            for (int i = start; i <= numpages; i++) {
                if (_searchText(source, searchtext, i).ok) {
                    res.add(i);
                    started = true;
                } else if (started) {
                    return res;
                }
            }
            return res;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns the page numbers on which each respective text appears.
     *
     * @param source
     * @param texts
     */
    public static Integer[][] getPagesByIds(PdfSource source, String[] texts) {
        ArrayList<Integer> pages;
        Integer[][] res = new Integer[texts.length][];
        for (int i = 0; i < texts.length; i++) {
            pages = getTextPages(source, texts[i], 1);
            res[i] = pages.toArray(new Integer[]{});
        }
        return res;
    }

    /**
     * Copies the specified pages from the source PDF.
     *
     * @param source
     * @param pages array of 1-based page numbers
     * @return A {@link PdfResult} object; if there was an error,
     * <code>getException()</code> will not be <code>null<code>.
     */
    public static PdfResult copyPages(PdfSource source, Integer[] pages) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Exception ex;
        try {
            Document doc = new Document();
            PdfWriter writer = PdfWriter.getInstance(doc, bos);
            doc.open();
            PdfContentByte cb = writer.getDirectContent();
            for (int page : pages) {
                ex = _copyPage(doc, source.getReader(), writer, cb, page);
                if (ex != null) {
                    return new PdfResult(bos, ex);
                }
            }
            doc.close();
            return new PdfResult(bos);
        } catch (DocumentException e) {
            return new PdfResult(bos, e);
        }
    }

    /**
     * Copies the PDF, removing the specified pages.
     *
     * @param source
     * @param pages array of 1-based page numbers to <b>exclude</b> from the
     * result
     * @return A {@link PdfResult} object; if there was an error,
     * <code>getException()</code> will not be <code>null<code>.
     */
    public static PdfResult removePages(PdfSource source, int[] pages) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Exception ex;
        try {
            Document doc = new Document();
            PdfWriter writer = PdfWriter.getInstance(doc, bos);
            doc.open();
            PdfContentByte cb = writer.getDirectContent();
            for (int page = 1; page <= numPages(source); page++) {
                if (!Misc.contains(pages, page)) {
                    ex = _copyPage(doc, source.getReader(), writer, cb, page);
                    if (ex != null) {
                        return new PdfResult(bos, ex);
                    }
                }
            }
            doc.close();
            return new PdfResult(bos);
        } catch (DocumentException e) {
            return new PdfResult(bos, e);
        }
    }

    /**
     * Copies the PDF, reordering the pages, and/or removing or duplicating some
     * of them.
     *
     * @param source
     * @param newpages The old pages in the order they are to appear in the new
     * PDF.
     * @return A {@link PdfResult} object; if there was an error,
     * <code>getException()</code> will not be <code>null<code>.
     */
    public static PdfResult reorderPages(PdfSource source, int[] newpages) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Exception ex;
        try {
            Document doc = new Document();
            PdfWriter writer = PdfWriter.getInstance(doc, bos);
            doc.open();
            PdfContentByte cb = writer.getDirectContent();
            for (int page : newpages) {
                ex = _copyPage(doc, source.getReader(), writer, cb, page);
                if (ex != null) {
                    return new PdfResult(bos, ex);
                }
            }
            doc.close();
            return new PdfResult(bos);
        } catch (DocumentException e) {
            return new PdfResult(bos, e);
        }
    }

    /**
     *
     * @param doc
     * @param reader
     * @param writer
     * @param cb
     * @param pagenum
     * @return <code>null</code> if successful.
     */
    private static Exception _copyPage(Document doc, PdfReader reader, PdfWriter writer, PdfContentByte cb, int pagenum) {
        try {
            PdfImportedPage page = writer.getImportedPage(reader, pagenum);
            Rectangle rect = reader.getPageSize(pagenum);
            float x = rect.getWidth();
            float y = rect.getHeight();
            double rad;
            int angle = reader.getPageRotation(pagenum);
            switch (angle) {
                case 0:
                    doc.setPageSize(new Rectangle(x, y));
                    doc.newPage();
                    cb.addTemplate(page, 1, 0, 0, 1, 0, 0);
                    break;
                case 90:
                    doc.setPageSize(new Rectangle(y, x));
                    doc.newPage();
                    rad = 270 / 57.2958;
                    cb.addTemplate(page, new Float(Math.cos(rad)), new Float(Math.sin(rad)), new Float(-Math.sin(rad)), new Float(Math.cos(rad)), 0, x);
                    break;
                case 180:
                    doc.setPageSize(new Rectangle(x, y));
                    doc.newPage();
                    rad = 180 / 57.2958;
                    cb.addTemplate(page, new Float(Math.cos(rad)), new Float(Math.sin(rad)), new Float(-Math.sin(rad)), new Float(Math.cos(rad)), x, y);
                    break;
                case 270:
                    doc.setPageSize(new Rectangle(y, x));
                    doc.newPage();
                    rad = 90 / 57.2958;
                    cb.addTemplate(page, new Float(Math.cos(rad)), new Float(Math.sin(rad)), new Float(-Math.sin(rad)), new Float(Math.cos(rad)), y, 0);
                    break;
                default:
                    doc.setPageSize(new Rectangle(x, y));
                    doc.newPage();
                    cb.addTemplate(page, 1, 0, 0, 1, 0, 0);
                    break;
            }
            return null;
        } catch (Exception e) {
            return e;
        }
    }

    /**
     * Checks if the text is found on the specified page of the PDF.
     *
     * @param source
     * @param page
     * @param searchtext
     * @return An {@link ActionResult} object. <br><b>Note:</b> Requires both
     * versions 2.6 (com.lowagie.text) and 5 (com.itextpdf.text) of iText.
     */
    public static ActionResult searchText(PdfSource source, Integer page, String searchtext) {
        try {
            if (!checkMinNumPages(source, page)) {
                return new PdfMisc().new ActionResult("Page " + page.toString() + " missing.", ResultTypes.WRONG_NUM_PAGES, false);
            } else {
                return _searchText(source, searchtext, page);
            }
        } catch (Exception e) {
            return new PdfMisc().new ActionResult(Misc.message(e), ResultTypes.OTHER, false);
        }
    }

    /**
     * Checks that the text is found on all of the specified pages of the PDF.
     *
     * @param source
     * @param start 1-based page on which to start the search, inclusively.
     * @param end Page on which to end the search, inclusively.
     * @param searchtext
     * @return An {@link ActionResult} object. <br><b>Note:</b> Requires both
     * versions 2.6 (com.lowagie.text) and 5 (com.itextpdf.text) of iText.
     */
    public static ActionResult searchText(PdfSource source, Integer start, Integer end, String searchtext) {
        try {
            if (!checkMinNumPages(source, end)) {
                return new PdfMisc().new ActionResult("Incorrect number of pages in PDF.", PdfMisc.ResultTypes.WRONG_NUM_PAGES, false);
            } else {
                for (int i = start; i <= end; i++) {
                    if (!_searchText(source, searchtext, i).ok) {
                        return new PdfMisc().new ActionResult("Tag not found on page: " + i, PdfMisc.ResultTypes.NOT_FOUND, false);
                    }
                }
                return new PdfMisc().new ActionResult("Tag found on all pages", PdfMisc.ResultTypes.OK, true);
            }
        } catch (Exception e) {
            return new PdfMisc().new ActionResult(Misc.message(e), PdfMisc.ResultTypes.OTHER, false);
        }
    }

    /**
     * Copies a PDF file, writing the result to a file.
     *
     * @param source A {@link PdfSource} object.
     * @param newPDF The path for the copied PDF.
     * @return "OK" or an error message.
     */
    public static String copyPdf(PdfSource source, String newPDF) {
        return copyPdf(source).writeFile(newPDF);
    }

    /**
     * Copies a PDF file.<br>NOTE: If the file (or the binary content) of the
     * PDF is available, it is more efficient to simply copy it as is, using one
     * of the overrides of the method).
     *
     * @param source A {@link il.co.aman.formit.sendit.PdfSource} object.
     * @return A {@link il.co.aman.formit.sendit.PdfResult} object containing
     * the copied PDF.
     */
    public static PdfResult copyPdf(PdfSource source) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            PdfStamper stamper = new PdfStamper(source.getReader(), bos);
            stamper.close();
            return new PdfResult(bos);
        } catch (DocumentException e) {
            return new PdfResult(bos, e);
        } catch (IOException e) {
            return new PdfResult(bos, e);
        }
    }

    /**
     * Copies a PDF file. Recommended if the file is available.
     *
     * @param src The source file path.
     * @param result The result file path.
     */
    public static void copyPdf(String src, String result) {
        new PdfResult(Misc.readBinFile(src)).writeFile(result);
    }

    /**
     * Copies a PDF file. Recommended if the binary content is available.
     *
     * @param src
     * @param result The result file path
     */
    public static void copyPdf(byte[] src, String result) {
        new PdfResult(src).writeFile(result);
    }

    /**
     * Returns the filename and number of pages of each PDF file in a directory.
     *
     * @param directory
     * @return A two-dimensional array of <code>String</code>, the first entry
     * of each subarray containing the filename, and the second containing the
     * number of pages therein.
     */
    public static String[][] countPages(String directory) {
        PdfConcat.ConcatSource src = new PdfConcat.ConcatSource(directory, false);
        if (src.error == null) {
            String[][] res = new String[src.paths.length][2];
            for (int i = 0; i < src.paths.length; i++) {
                res[i][0] = src.paths[i];
                try {
                    com.lowagie.text.pdf.PdfReader reader = new PdfSource(src.inPDFs[i]).getReader();
                    res[i][1] = new Integer(reader.getNumberOfPages()).toString();
                } catch (IOException e) {
                    res[i][0] = Misc.message(e) + " for " + res[i][0];
                    res[i][1] = "-1";
                }
            }
            return res;
        } else {
            System.out.println(src.error + "\n" + src.error_id);
            return null;
        }
    }

    public static byte[] base64_binary(String base64) {
        return org.apache.commons.codec.binary.Base64.decodeBase64(base64);
    }

    public static boolean base64_2file(String base64, String path) {
        return Misc.writeBinFile(path, base64_binary(base64)) == null;
    }

    /**
     * Creates an XML representation of the structure of a tagged PDF.
     *
     * @param reader A {@link com.itextpdf.text.pdf.PdfReader} object.
     * @param os An {@link OutputStream} for the generated XML.
     * @return An {@link Exception}, or <code>null</code> if the process
     * succeeded.
     */
    public static Exception tagged2Xml(com.itextpdf.text.pdf.PdfReader reader, OutputStream os) {
        try {
            TaggedPdfReaderTool taggedReader = new TaggedPdfReaderTool();
            taggedReader.convertToXml(reader, os);
            return null;
        } catch (IOException e) {
            return e;
        }
    }

    public class ActionResult {

        /**
         * A readable message describing the result.
         */
        public final String msg;
        /**
         * The <code>ResultTypes</code> enum should be declared in the outer
         * class.
         */
        public final ResultTypes type;
        /**
         * Indicates whether the result is the desired one, or not.
         */
        public final boolean ok;
        /**
         * Which page the text was found on; or -1, if the text was not found.
         */
        public int page;

        public ActionResult(String msg, ResultTypes type, boolean ok) {
            this.msg = msg;
            this.type = type;
            this.ok = ok;
            this.page = -1;
        }

        public ActionResult(String msg, ResultTypes type, boolean ok, int page) {
            this(msg, type, ok);
            this.page = page;
        }
    }

    public static void main(String[] args) throws IOException {
        /*String[][] valid = validateFolder("C:\\Users\\davidz\\Desktop\\PDFs");
        for (String[] val : valid) {
            System.out.println(val[0] + " --- " + val[1]);
        }*/
//        try {
//            PdfSource src = new PdfSource("\\\\chaims\\c$\\Documents and Settings\\davidz\\Desktop\\old process.pdf");
//            ArrayList<Integer> pages = getTextPages(src, str, 1);
//            System.out.println(pages.get(0));
//
//            String pdf = "C:\\Documents and Settings\\davidz\\My Documents\\downloads\\Cellcom Statement for 02.06.11 - 01.07.11.pdf";
//
//            String[][] sizes = countPages("C:\\Documents and Settings\\davidz\\Desktop\\PDFs");
//            if (sizes != null) {
//                for (String[] size : sizes) {
//                    //System.out.println(sizes[i][0] + ": " + sizes[i][1]);
//                }
//            }
//        } catch (IOException e) {
//            System.out.println(Misc.message(e));
//        }
        System.out.println(searchText(new PdfSource("\\\\formit7\\e$\\formit\\Curr_env\\Environments\\PR_Example_AccFull\\fit_WorkFiles\\fit_OutputFiles\\PDF_072417_1626\\arc_741852963_123456789.pdf"), "27/02/2012").msg);
    }
}
