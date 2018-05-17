package il.co.aman.formit;

import il.co.aman.apps.Misc;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import org.apache.commons.io.IOUtils;

/**
 * Converts a PDF file to Postscript using Ghostscript, accessed by means of
 * temporary files and the command line.<br>
 * Hopefully will run more efficiently than using Ghost4j.
 *
 * @author davidz
 */
public class PsConvert {

    String _gsFolder, _gsExec;
    Exception _error;

    public Exception getError() {
        return this._error;
    }

    /**
     * Uses the default parameters from {@link PsConvertParams}.
     */
    public PsConvert() {
        this._gsFolder = PsConvertParams.GS_FOLDER;
        this._gsExec = PsConvertParams.GS_EXEC;
    }

    /**
     *
     * @param gsFolder The folder where the Ghostscript binary is installed.
     * <b>Must</b> have the final path separator.
     * @param gsExec The name of the Ghostscript executable.
     */
    public PsConvert(String gsFolder, String gsExec) {
        this._gsFolder = gsFolder;
        this._gsExec = gsExec;
    }

    /**
     *
     * @param pdf
     * @return <code>null</code> if successful, an {@link Exception} otherwise.
     */
    public byte[] convert(InputStream pdf) {
        try {
            File pdffile = File.createTempFile("PSconvert", ".pdf");
            pdffile.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(pdffile, false);
            fos.write(IOUtils.toByteArray(pdf));
            fos.close();

            File result = File.createTempFile("fromPDF", ".ps");
            result.deleteOnExit();
            String command = this._gsFolder + this._gsExec + " -dNOPAUSE -dBATCH -sDEVICE=ps2write -sOutputFile=" + result.getCanonicalPath() + " " + pdffile.getCanonicalPath();
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            // we have to consume the output, otherwise the process hangs
            StringBuilder output = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }
            p.waitFor();

            return Misc.readBinFile(result.getCanonicalPath());
        } catch (IOException e) {
            this._error = e;
            return null;
        } catch (InterruptedException e) {
            this._error = e;
            return null;
        }
    }
    
    public void convert2Images(InputStream pdf) {
    	try {
    		File pdffile = File.createTempFile("PSconvert",  ".pdf");
    		pdffile.deleteOnExit();
    		FileOutputStream fos = new FileOutputStream(pdffile, false);
    		fos.write(IOUtils.toByteArray(pdf));
    		fos.close();
    		
    		String command = this._gsFolder + this._gsExec + " -sDEVICE=pngalpha -o C:\\Users\\davidz\\Desktop\\gsimages\\file-%03d.png -r144 " + pdffile.getCanonicalPath();
    		Process p = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            // we have to consume the output, otherwise the process hangs
            StringBuilder output = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }
            p.waitFor();
            System.out.println(output.toString());
    	}
    	catch (Exception e) {
    		System.out.println(e);
    	}
    }

    public static void main(String[] args) throws Exception {
        PsConvert psconvert = new PsConvert();
    	psconvert.convert2Images(new FileInputStream(new File("C:\\Users\\davidz\\Desktop\\444bc800-1a6f-4479-a5d8-d90a6b203312.pdf")));
    	System.exit(0);
        try {
            System.out.println(new java.util.Date());
            byte[] ps = psconvert.convert(new FileInputStream(new File("C:\\Users\\davidz\\Desktop\\IC_FOUR_1-672.pdf")));
            System.out.println(new java.util.Date());
            Misc.writeBinFile("C:\\Users\\davidz\\Desktop\\big.ps", ps);
        } catch (Exception e) {
            System.out.println(Misc.message(e));
        }
    }

}
