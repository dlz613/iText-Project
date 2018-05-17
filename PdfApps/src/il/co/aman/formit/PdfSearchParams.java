package il.co.aman.formit;

/**
 * Installation parameters (static final variables).<br>
 * To change, recompile this class and place the new version in the JAR file or the classpath.
 * @author davidz
 */
public class PdfSearchParams {
    /**
     * Whether to use iText version 5 (not freeware) for text search.
     */
    public static final boolean USE_ITEXTPDF = true;
    /**
     * Whether to use PDFTextStream for text search.
     */
    public static final boolean USE_PDFTEXTSTREAM = true;
    /**
     * Whether to try reversing the search text (may be necessary for Hebrew, for example).<br><b>Warning:</b> Obviously, this may lead to false-positives (if the search text is not found,
     * but its reverse <b>is</b> found). Use with care!
     */
    public static final boolean TRY_REVERSE = true;
}
