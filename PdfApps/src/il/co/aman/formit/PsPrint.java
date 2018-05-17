package il.co.aman.formit;

import il.co.aman.apps.Misc;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.ghost4j.converter.ConverterException;
import org.ghost4j.document.PDFDocument;
import org.ghost4j.converter.PSConverter;
import org.ghost4j.document.DocumentException;

/**
 * Prints Postscript (actually, any kind of PRN file) directly to a supporting
 * printer. Can also convert PDF to Postscript (using Ghostscript) for the same
 * purpose.<br>
 * Postscript conversion requires Ghostscript to be installed, and the path to
 * its <code>bin</code> folder to be on the <code>path</code> environment
 * variable.<br>
 * There are two versions of Ghostscript - 32 bit and 64 bit. The version of
 * Ghostscript installed has to be the same in that respect as the JVM.
 *
 * @author davidz
 */
public class PsPrint {

    Exception _error;
    String _printer, _pspath;
    FileOutputStream _fos;

    public Exception getError() {
        return this._error;
    }

    public String getPsPath() {
        return this._pspath;
    }

    /**
     * Sets the default printer. It must be in a UNC path format
     * (<code>\\server\printer</code>) or a device name (e.g.
     * <code>LPT1</code>).
     *
     * @param val
     */
    public void setPrinter(String val) {
        this._printer = val;
    }

    public void setPsPath(String val) {
        this._pspath = val;
    }

    /**
     * When using this constructor, either <code>setPrinter()</code> must be
     * called, or only the methods which supply the printer name may be used.
     */
    public PsPrint() {
        this._error = null;
        this._printer = null;
    }

    /**
     *
     * @param printer The default printer. It must be in a UNC path format
     * (<code>\\server\printer</code>) or a device name (e.g.
     * <code>LPT1</code>).
     */
    public PsPrint(String printer) {
        this._error = null;
        this._printer = printer;
    }

    /**
     * Prints a PDF by converting it to Postscript, then sending it directly to
     * the printer.
     *
     * @param pdf
     * @return <code>null</code> if successful
     */
    public Exception printPdf(InputStream pdf) {
        if (this._printer != null) {
            return printPdf(pdf, this._printer);
        } else {
            return new Exception("No printer set.");
        }
    }

    /**
     * Prints a PDF by converting it to Postscript, then sending it directly to
     * the printer.
     *
     * @param pdf
     * @param printer It must be in a UNC path format
     * (<code>\\server\printer</code>) or a device name (e.g.
     * <code>LPT1</code>).
     * @return <code>null</code> if successful
     */
    public Exception printPdf(InputStream pdf, String printer) {
        try {
            byte[] b = this.convert(pdf);
            if (b != null) {
                return printPrnBytes(b, printer);
            } else {
                return this._error;
            }
        } catch (IOException e) {
            return e;
        }
    }

    /**
     * Prints a PRN file directly to the default printer.
     *
     * @param prn
     * @return <code>null</code> if successful.
     * @throws IOException
     */
    public Exception printPrnFile(File prn) throws IOException {
        return copyFile(prn, new File(this._printer));
    }

    /**
     * Prints a PRN file directly to a printer.
     *
     * @param prn
     * @param printer It must be in a UNC path format
     * (<code>\\server\printer</code>) or a device name (e.g.
     * <code>LPT1</code>).
     * @return <code>null</code> if successful.
     * @throws IOException
     */
    public static Exception printPrnFile(File prn, String printer) throws IOException {
        return copyFile(prn, new File(printer));
    }

    /**
     * Prints a PRN file directly to the default printer.
     *
     * @param prn
     * @return <code>null</code> if successful.
     * @throws IOException
     */
    public Exception printPrnBytes(byte[] prn) throws IOException {
        return copyBytes(prn, new File(this._printer));
    }

    /**
     * Prints a PRN file directly to a printer.
     *
     * @param prn
     * @param printer It must be in a UNC path format
     * (<code>\\server\printer</code>) or a device name (e.g.
     * <code>LPT1</code>).
     * @return <code>null</code> if successful.
     * @throws IOException
     */
    public static Exception printPrnBytes(byte[] prn, String printer) throws IOException {
        return copyBytes(prn, new File(printer));
    }

    /**
     * Prints a PRN file directly to the default printer.
     *
     * @param prn
     * @return <code>null</code> if successful.
     * @throws IOException
     */
    public Exception printPrn(InputStream prn) throws IOException {
        return copy2File(prn, new File(this._printer));
    }

    /**
     * Prints a PRN file directly to a printer.
     *
     * @param prn
     * @param printer It must be in a UNC path format
     * (<code>\\server\printer</code>) or a device name (e.g.
     * <code>LPT1</code>).
     * @return <code>null</code> if successful.
     * @throws IOException
     */
    public static Exception printPrn(InputStream prn, String printer) throws IOException {
        return copy2File(prn, new File(printer));
    }

    /**
     * Copies the content of a byte array to a file or a network printer.
     *
     * @param source
     * @param dest
     * @return <code>null</code> if successful.
     * @throws IOException
     */
    public static Exception copyBytes(byte[] source, File dest) throws IOException {
        return copy2File(new ByteArrayInputStream(source), dest);
    }

    /**
     * Copies the content of a file to another file or a network printer.
     *
     * @param source
     * @param dest
     * @return <code>null</code> if successful.
     * @throws IOException
     */
    public static Exception copyFile(File source, File dest) throws IOException {
        return copy2File(new FileInputStream(source), dest);
    }

    /**
     * Copies the content of an {@link InputStream} to a file or a network
     * printer.
     *
     * @param input
     * @param dest
     * @return <code>null</code> if successful.
     * @throws IOException
     */
    public static Exception copy2File(InputStream input, File dest) throws IOException {
        OutputStream output = null;
        Exception res = null;
        try {
            output = new FileOutputStream(dest);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
        } catch (IOException e) {
            res = e;
        } finally {
            if (input != null) {
                input.close();
            }
            if (output != null) {
                output.close();
            }
            return res;
        }
    }

    /**
     * Converts a PDF to Postscript, returned as a byte array.
     *
     * @param pdf
     * @return <code>null</code> if successful
     */
    public byte[] convert(InputStream pdf) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Exception ex = convert(pdf, bos);
        if (ex == null) {
            this._error = null;
            return bos.toByteArray();
        } else {
            this._error = ex;
            return null;
        }
    }

    /**
     * Converts a PDF to Postscript
     *
     * @param pdf
     * @param os
     * @return <code>null</code> if successful
     */
    public static Exception convert(InputStream pdf, OutputStream os) {
        try {
            PDFDocument document = new PDFDocument();
            document.load(pdf);
            PSConverter converter = new PSConverter();
            converter.setDevice(PSConverter.OPTION_DEVICE_PS2WRITE);

            converter.convert(document, os);
            return null;
        } catch (IOException e) {
            return e;
        } catch (ConverterException e) {
            return e;
        } catch (DocumentException e) {
            return e;
        }
    }

    /**
     * Converts a PDF to Postscript, writing it to the file specified in <code>setPsPath()</code>. 
     * @param pdf
     * @param first If <code>true</code>, this is the first PDF being written to the output file, so the file will be erased before starting.
     * @return <code>null</code> if successful, otherwise an {@link Exception}
     */
    public Exception convert(InputStream pdf, boolean first) {
        try {
            this._fos = new FileOutputStream(this._pspath, !first); // if first is false, the output file is appended to, not overwritten
            Exception res = convert(pdf, this._fos);
            this._fos.close();
            return res;
        } catch (FileNotFoundException e) {
            return e;
        } catch (IOException e) {
            return e;
        }
    }

    public static void main(String[] args) {
//        PsPrint psPrint = null;
//        String path = null;
//        if (args.length == 0) {
//            psPrint = new PsPrint("\\\\AMANAD\\HP3015");
//            path = "\\\\davidz\\c$\\Users\\davidz\\Desktop\\PDFs\\1.pdf";
//        } else if (args.length == 2) {
//            psPrint = new PsPrint(args[0].trim());
//            path = args[1].trim();
//            System.out.println(path);
//        } else if (args.length == 3) {
//            String printer = args[0].trim();
//            path = args[1].trim();
//            System.out.println("Printing PRN file: " + new File(path).getAbsolutePath());
//            try {
//                System.out.println(il.co.aman.apps.Misc.message(PsPrint.printPrnFile(new File(path), printer)));
//            } catch (IOException e) {
//                System.out.println(il.co.aman.apps.Misc.message(e));
//            }
//        } else {
//            System.out.println("Usage: java " + new PsPrint().getClass().getSimpleName() + " {<printer path> <PDF file path>} | {<printer path> <PRN file path> copy}");
//        }
//        if (psPrint != null) {
//            System.out.println("Printing " + path + " to " + psPrint._printer);
//            System.out.println(il.co.aman.apps.Misc.message(psPrint.printPdf(new ByteArrayInputStream(Misc.readBinFile(path)))));
//        }
        try {
            System.out.println(new java.util.Date());
            PsPrint psprint = new PsPrint();
            String[] pdfs = Misc.filesByExtension("C:\\Users\\davidz\\Desktop\\PDFs", "pdf", true);
            //FileOutputStream fos;
            psprint.setPsPath("C:\\Users\\davidz\\Desktop\\big.ps");
            for (int i = 0; i < pdfs.length; i++) {
                Exception res = psprint.convert(new FileInputStream(new File(pdfs[i])), i == 0);
                System.out.println(pdfs[i] + " -- " + res);
            }
//            for (String pdf : pdfs) {
//                fos = new FileOutputStream(new File("C:\\Users\\davidz\\Desktop\\big.ps"), true);
//                Exception res = convert(new FileInputStream(new File(pdf)), fos);
//                System.out.println(pdf + " -- " + res);
//                fos.close();
//            }
//            //fos.close();
            //byte[] b1 = psprint.convert(new FileInputStream(new File("\\\\davidz\\c$\\Users\\davidz\\Desktop\\IC_FOUR_1-672.pdf")));
            //if (psprint.getError() == null) {
            //System.out.println(new java.util.Date());
            //byte[] b2 = new PsPrint().convert(new FileInputStream(new File("C:\\Users\\davidz\\Desktop\\PDFs\\1.pdf")));
            //Misc.writeBinFile("C:\\Users\\davidz\\Desktop\\big.prn", b1);
            //} else {
            //System.out.println(Misc.message(psprint.getError()));
            //}
            //Misc.writeBinFile("C:\\Users\\davidz\\Desktop\\big.prn", b2, true);
        } catch (FileNotFoundException e) {
            System.out.println(Misc.message(e));
        } catch (IOException e) {
            System.out.println(Misc.message(e));
        }
    }
}
