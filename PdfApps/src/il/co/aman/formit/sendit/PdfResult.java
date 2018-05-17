package il.co.aman.formit.sendit;

import il.co.aman.apps.Misc;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * A generic class for storing a PDF in various formats.<br> The content of the
 * PDF can be accessed in various ways:<p> As a
 * <code>byte array</code>, as an {@link OutputStream}, or written to a file.
 *
 * @author davidz
 */
public class PdfResult {

    byte[] pdfbytes;
    OutputStream stream;
    Exception error, ext_exception;

    /**
     *
     * @return The PDF as a <code>byte</code> array.
     */
    public byte[] getBytes() {
        return this.pdfbytes;
    }

    /**
     *
     * @return The PDF as an {@link OutputStream} object.
     */
    public OutputStream getStream() {
        return this.stream;
    }

    /**
     * An {@link Exception} encountered in the Constructor.
     */
    public Exception getError() {
        return this.error;
    }
    
    /**
     * Used to return an {@link Exception} encountered in generating the PDF. If not specified in the Constructor, it is <code>null</code>.
     */
    public Exception getException() {
        return this.ext_exception;
    }

    /**
     *
     * @param pdf
     */
    public PdfResult(OutputStream pdf) {
        this.stream = pdf;
        this.pdfbytes = ((ByteArrayOutputStream) pdf).toByteArray();
        this.ext_exception = null;
    }

    /**
     * 
     * @param pdf 
     */
    public PdfResult(byte[] pdf) {
        this.ext_exception = null;
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            os.write(pdf);
            this.stream = os;
            this.pdfbytes = pdf;
        } catch (Exception e) {
            this.stream = null;
            this.pdfbytes = null;
            this.error = e;
        }
    }
    
    /**
     * 
     * @param pdf
     * @param ex An {@link Exception} encountered in generating the PDF
     */
    public PdfResult(OutputStream pdf, Exception ex) {
        this(pdf);
        this.ext_exception = ex;
    }
    
    /**
     * 
     * @param pdf
     * @param ex An {@link Exception} encountered in generating the PDF
     */
    public PdfResult(byte[] pdf, Exception ex) {
        this(pdf);
        this.ext_exception = ex;
    }

    /**
     * Writes the PDF to a file.
     *
     * @param filepath
     * @return "OK" or an error message.
     */
    public String writeFile(String filepath) {
        return this.writeFile(new File(filepath));
    }

    /**
     * Writes the PDF to a file.
     *
     * @param file A {@link File} object.
     * @return "OK" or an error message.
     */
    public String writeFile(File file) {
        Exception e = writeFileEx(file);
        return e == null? "OK": Misc.message(e);
    }
    
    /**
     * Writes the PDF to a file.
     * @param file
     * @return An {@link Exception}, or <code>null</code> if the method succeeded.
     */
    public Exception writeFileEx(File file) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(this.pdfbytes);
            fos.close();
            
            return null;
        } catch (Exception e) {
            return e;
        }
    }
}
