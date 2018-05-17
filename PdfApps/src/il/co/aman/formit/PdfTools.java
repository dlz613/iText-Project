package il.co.aman.formit;

import com.pdftools.printer.Printer;
import il.co.aman.apps.Misc;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.io.FileUtils;

/**
 * Requires the PRNA430x64\bin folder to be on the PATH environment variable
 *
 * @author davidz
 */
public class PdfTools {

    /**
     * The default number of pages to be converted at a time. Can be changed by
     * using the appropriate constructor.
     */
    public static final int PORTION = 25; // empirically, this gave the best results for big PDFs (1000+ pages)
    int _portion;
    Printer _printer;
    String _printername;

    Exception _error;

    public Exception getError() {
        return this._error;
    }

    private PdfTools() {
        Printer.setLicenseKey(PdfToolsParams.LICENSE_KEY);
    }

    /**
     * Uses the default value of <code>PORTION</code>
     *
     * @param printername
     */
    public PdfTools(String printername) {
        this();
        this._portion = PORTION;
        this._printer = new Printer(printername);
        this._printername = printername;
    }

    /**
     *
     * @param printername
     * @param portion
     */
    public PdfTools(String printername, int portion) {
        this(printername);
        this._portion = portion;
    }

    /**
     * Creates a PRN file for the specified printer.
     *
     * @param filepath The PDF file
     * @param password The password, if any, of the PDF. If there is none, enter
     * an empty String ("")
     * @param outfilepath The path for the PRN file
     * @return <code>null</code> if successful, otherwise an {@link Exception}
     */
    public Exception printToFile(String filepath, String password, String outfilepath) {
        try {
            if (this._printer.open(filepath, password)) {
                ArrayList<String> tempfiles = new ArrayList<String>();
                int pages = this._printer.getPageCount();
                int currentPage = 1;

                while (currentPage <= pages) {
                    System.out.println(currentPage);
                    File temp = File.createTempFile("pdftools", ".prn");
                    temp.deleteOnExit();
                    tempfiles.add(temp.getCanonicalPath());
                    this._printer.setOutput(temp.getCanonicalPath());
                    int lastPage;
                    if (currentPage + this._portion - 1 <= pages) {
                        lastPage = currentPage + this._portion - 1;
                    } else {
                        lastPage = pages;
                    }
                    this._printer.printFile(filepath, this._printername, password, currentPage, lastPage);
                    currentPage += this._portion;
                }

                File output = new File(outfilepath);
                File input = new File(tempfiles.get(0));
                FileUtils.writeByteArrayToFile(output, FileUtils.readFileToByteArray(input));
                for (int i = 1; i < tempfiles.size(); i++) {
                    input = new File(tempfiles.get(i));
                    FileUtils.writeByteArrayToFile(output, FileUtils.readFileToByteArray(input), true); // append
                }
                return null;

            } else {
                return new Exception("Unable to open PDF document: " + filepath);
            }
        } catch (IOException e) {
            return e;
        }
    }

    /**
     * Converts a PDF file to the appropriate PRN file for the specified
     * printer.
     *
     * @param filepath
     * @param password
     * @return <code>null</code> in case of an error; the {@link Exception} can
     * be retrieved with the <code>getError()</code> method.
     */
    public byte[] convert(String filepath, String password) {
        try {
            File temp = File.createTempFile("pdftools", ".prn");
            temp.deleteOnExit();
            Exception res = printToFile(filepath, password, temp.getCanonicalPath());
            if (res == null) {
                return Misc.readBinFile(temp.getCanonicalPath());
            } else {
                this._error = res;
                return null;
            }
        } catch (IOException e) {
            this._error = e;
            return null;
        }
    }

    public static void main(String[] args) {
        if (args.length >= 3) {
            String printer = args[0].trim();
            String pdfpath = args[1].trim();
            String prnpath = args[2].trim();
            PdfTools pdftools = new PdfTools(printer);
            System.out.println(new java.util.Date());
            Exception res = pdftools.printToFile(pdfpath, "", prnpath);
            System.out.println(new java.util.Date());
            if (res != null) {
                System.out.println(Misc.message(res));
            }
        } else {
            PdfTools pdftools = new PdfTools("\\\\AMANAD\\HP3015", 25);
            System.out.println(new java.util.Date());
        //byte[] b = pdftools.convert("C:\\Users\\davidz\\Desktop\\mail.pdf", "");
            //Misc.writeBinFile("C:\\Users\\davidz\\Desktop\\mail2.prn", b);
            Exception res = pdftools.printToFile("C:\\Users\\davidz\\Desktop\\IC_FOUR_1-672.pdf", "", "C:\\Users\\davidz\\Desktop\\pdftools.prn");
        //Exception res = pdftools.printToFile("C:\\Users\\davidz\\Desktop\\PDFs\\actfax_manual_en.pdf", "", "C:\\Users\\davidz\\Desktop\\pdftools.prn");
            //Exception res = pdftools.printToFile("C:\\Users\\davidz\\Desktop\\mail.pdf", "", "C:\\Users\\davidz\\Desktop\\mail.prn");
            System.out.println(new java.util.Date());
            if (res == null) {
                //Misc.writeBinFile("C:\\Users\\davidz\\Desktop\\pdftools.prn", b);

            } else {
                System.out.println(Misc.message(res));
            }
        }
        //printToFile("C:\\Users\\davidz\\Desktop\\mail.pdf", "", "\\\\AMANAD\\HP3015", "C:\\Users\\davidz\\Desktop\\pdftools.prn");
    }

}
