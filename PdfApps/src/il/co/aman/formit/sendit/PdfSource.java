package il.co.aman.formit.sendit;

import com.itextpdf.text.DocumentException;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import il.co.aman.apps.Misc;
import il.co.aman.formit.PdfSearchParams;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A class used for supplying a PDF (including password-protected) as a source.
 * The PDF can be supplied in any of the following formats:
 * <p>
 * - A
 * {@link PdfReader} object.
 * <p>
 * - An {@link InputStream}.
 * <p>
 * - A filepath.
 * <p>
 * - A {@link Byte} array.
 * <p>
 * Now updated to support the <code>com.itextpdf.text.pdf.PdfReader</code> class
 * as well (accessed by
 * <code>getNewReader()</code>)
 * .<p>
 * <b>Note:</b> That being the case, it is essential to have the new iText jar
 * on the classpath.<br><b>Note:</b>
 * <code>itextpdf</code> is only used if {@link PdfSearchParams}
 * <code>.USE_ITEXTPDF</code> is <code>true</code>.
 *
 * @author davidz
 */
public class PdfSource {

    PdfReader _reader;
    com.itextpdf.text.pdf.PdfReader _newreader;
    byte[] _bytes = null;
    public Exception error = null;

    public PdfReader getReader() {
        return this._reader;
    }

    public final com.itextpdf.text.pdf.PdfReader getNewReader() {
        if (PdfSearchParams.USE_ITEXTPDF) {
            return this._newreader;
        } else {
            return null;
        }
    }

    /**
     *
     * @return The PDF as a <code>byte</code> array.
     */
    public byte[] getBytes() {
        return this._bytes;
    }

    /**
     *
     * @param reader A {@link PdfReader} object.
     */
    //@SuppressWarnings("LeakingThisInConstructor")
    public PdfSource(PdfReader reader) {
        PdfReader copy = new PdfReader(reader);
        //this._reader = reader;
        this._bytes = _copyPdf(reader); // "Leaking this in Constructor" problem was here (when using copyPdf(this) instead of _copyPdf(reader))
        this._reader = copy;
        if (PdfSearchParams.USE_ITEXTPDF) {
            try {
                this._newreader = new com.itextpdf.text.pdf.PdfReader(this._bytes);
            } catch (IOException e) {
                this._newreader = null;
            }
        }
    }

    /**
     *
     * @param reader A {@link com.itextpdf.text.pdf.PdfReader} object.
     */
    public PdfSource(com.itextpdf.text.pdf.PdfReader reader) {
        if (PdfSearchParams.USE_ITEXTPDF) {
            this._newreader = new com.itextpdf.text.pdf.PdfReader(reader);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                com.itextpdf.text.pdf.PdfStamper stamper = new com.itextpdf.text.pdf.PdfStamper(reader, bos);
                stamper.close();
                this._bytes = bos.toByteArray();
                this._reader = new PdfReader(this._bytes);
            } catch (IOException e) {
                this._reader = null;
            } catch (DocumentException e) {
                this._bytes = null;
            }
        }
    }

    /**
     *
     * @param pdf
     * @throws java.io.IOException
     */
    public PdfSource(InputStream pdf) throws IOException {
        this(new PdfReader(pdf));
    }

    /**
     *
     * @param pdf
     * @param password
     * @throws java.io.IOException
     */
    public PdfSource(InputStream pdf, byte[] password) throws IOException {
        this(new PdfReader(pdf, password));
    }

    /**
     *
     * @param pdf The path to the PDF file.
     * @throws java.io.IOException
     */
    public PdfSource(String pdf) throws IOException {
        this(new PdfReader(pdf));
    }

    /**
     *
     * @param pdf The path to the PDF file.
     * @param password
     * @throws java.io.IOException
     */
    public PdfSource(String pdf, byte[] password) throws IOException {
        this(new PdfReader(pdf, password));
    }

    /**
     *
     * @param pdf
     * @throws java.io.IOException
     */
    public PdfSource(byte[] pdf) throws IOException {
        this(new PdfReader(pdf));
    }

    /**
     *
     * @param pdf
     * @param password
     * @throws java.io.IOException
     */
    public PdfSource(byte[] pdf, byte[] password) throws IOException {
        this(new PdfReader(pdf, password));
    }

    /**
     * Copies a {@link PdfSource} to a <code>byte</code> array.
     *
     * @param source
     * @return <code>null</code> if an error occurred.
     */
    public static byte[] copyPdf(PdfSource source) {
        if (source.getReader() != null) {
            return _copyPdf(source.getReader());
        } else {
            return null;
        }
    }

    /**
     * This is needed in order to avoid a "Leaking this in Constructor" problem
     *
     * @param reader
     * @return <code>null</code> in case of failure
     */
    private static byte[] _copyPdf(PdfReader reader) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            PdfStamper stamper = new PdfStamper(reader, bos);
            stamper.close();
            return bos.toByteArray();
        } catch (com.lowagie.text.DocumentException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    public static void main(String[] args) {
//        try {
//            String path = "C:\\Users\\davidz\\Desktop\\old process.pdf";
//            PdfSource src;
//            src = new PdfSource(new PdfReader(new FileInputStream(new File(path))));
//            Misc.writeBinFile("C:\\Users\\davidz\\Desktop\\tmppdfs\\lowagiereader.pdf", src.getBytes());
//            src = new PdfSource(new com.itextpdf.text.pdf.PdfReader(new FileInputStream(new File(path))));
//            Misc.writeBinFile("C:\\Users\\davidz\\Desktop\\tmppdfs\\itextpdfreader.pdf", src.getBytes());
//            src = new PdfSource(new FileInputStream(new File(path)));
//            Misc.writeBinFile("C:\\Users\\davidz\\Desktop\\tmppdfs\\inputstream.pdf", src.getBytes());
//            src = new PdfSource(path);
//            Misc.writeBinFile("C:\\Users\\davidz\\Desktop\\tmppdfs\\filepath.pdf", src.getBytes());
//            src = new PdfSource(Misc.readBinFile(path));
//            Misc.writeBinFile("C:\\Users\\davidz\\Desktop\\tmppdfs\\bytes.pdf", src.getBytes());
//        } catch (IOException e) {
//            System.out.println(Misc.message(e));
//        }
        try {
            String path = "C:\\Users\\davidz\\Desktop\\StartStop.pdf";//StartStop
            PdfReader reader = new PdfReader(path, "".getBytes());
            PdfSource src = new PdfSource(new PdfReader(path, "".getBytes()));
        } catch (IOException e) {
            System.out.println(Misc.message(e));
        }
    }
}
