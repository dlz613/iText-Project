package il.co.aman.formit.sendit;

import il.co.aman.formit.PdfMisc;

/**
 *
 * @author davidz
 */
public class CheckPdf {

    // default tag and replacement text
    
    public final static String REP = "EMAILHERE";
    public final static String TAG = "#ebill#embeddedEmail#" + REP + "#";

    /**
     * Checks if the email identification text is found in each page of the PDF.
     * @param pdf
     * @param email
     * @return A {@link il.co.aman.formit.PdfMisc.ActionResult} object.
     */
    public static PdfMisc.ActionResult check(PdfSource pdf, String email) {
        return check(pdf, email, REP, TAG);
    }

    /**
     * Checks if the email identification text is found on each page, from 1 to lastpage, of the PDF.
     * @param pdf
     * @param lastpage
     * @param email
     * @return A {@link il.co.aman.formit.PdfMisc.ActionResult} object.
     */
    public static PdfMisc.ActionResult check(PdfSource pdf, Integer lastpage, String email) {
        return check(pdf, lastpage, email, REP, TAG);
    }

    /**
     * Checks if the specified text (tag, with rep replaced by variable) is found in each page of the PDF.
     * @param pdf
     * @param variable
     * @param rep
     * @param tag
     * @return A {@link il.co.aman.formit.PdfMisc.ActionResult} object.
     */
    public static PdfMisc.ActionResult check(PdfSource pdf, String variable, String rep, String tag) {
        return check(pdf, PdfMisc.numPages(pdf), variable, rep, tag);
    }
    
    /**
     * Checks if the specified text (tag, with rep replaced by variable) is found on each page, from 1 to lastpage, of the PDF.
     * @param pdf
     * @param lastpage
     * @param variable
     * @param rep
     * @param tag
     * @return An {@link il.co.aman.formit.PdfMisc.ActionResult} object.
     */
    public static PdfMisc.ActionResult check(PdfSource pdf, Integer lastpage, String variable, String rep, String tag) {
        return PdfMisc.searchText(pdf, 1, lastpage, tag.replace(rep, variable));
    }
    
    public static void main(String[] args) throws java.io.IOException {
        PdfSource src = new PdfSource("C:\\Documents and Settings\\davidz\\My Documents\\downloads\\Cellcom Statement for 02.06.11 - 01.07.11.pdf");
        PdfMisc.ActionResult res = check(src, "dlzalkin@gmail.com");
        if (res.ok) {
            System.out.println(res.msg);
        } else {
            System.out.println("Problem:");
            System.out.println(res.msg);
        }
        System.out.println(PdfMisc.numPages(src));

    }
}
