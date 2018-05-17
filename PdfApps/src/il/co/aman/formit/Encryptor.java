package il.co.aman.formit;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;
import il.co.aman.formit.sendit.PdfResult;
import il.co.aman.formit.sendit.PdfSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Facilitates password-encryption of a PDF, with the option of including file
 * attachments.
 *
 * @author davidz
 */
public class Encryptor {

    Exception _error = null;

    /**
     *
     * @return The {@link Exception}, if any, encountered by the 
     * methods.
     */
    public Exception getError() {
        return this._error;
    }

    /**
     * Encrypts the PDF (without adding attachments) using the default
     * parameters: Allow Fill-in, Allow printing, Allow copying.
     *
     * @param inPDF A {@link  PdfSource} object referencing the PDF to be
     * encrypted.
     * @param openPass The password for opening the encrypted PDF.
     * @param ownerPass The password for other access to the encrypted PDF.
     * @return The encrypted PDF as a {@link Byte} array, or <code>null</code>
     * if there was an error.
     */
    public byte[] encrypt(PdfSource inPDF, String openPass, String ownerPass) {
        return this.encrypt(inPDF, openPass, ownerPass, PdfWriter.ALLOW_FILL_IN | PdfWriter.ALLOW_PRINTING | PdfWriter.ALLOW_COPY);
    }

    /**
     * Encrypts the PDF without adding attachments.
     *
     * @param inPDF A {@link  PdfSource} object referencing the PDF to be
     * encrypted.
     * @param openPass The password for opening the encrypted PDF.
     * @param ownerPass The password for other access to the encrypted PDF.
     * @param params PDF parameters
     * (e.g. <code>PdfWriter.ALLOW_FILL_IN | PdfWriter.ALLOW_PRINTING | PdfWriter.ALLOW_COPY</code>).
     * @return The encrypted PDF as a {@link Byte} array, or <code>null</code>
     * if there was an error.
     */
    public byte[] encrypt(PdfSource inPDF, String openPass, String ownerPass, Integer params) {
        try {
            PdfReader reader = inPDF.getReader();
            ByteArrayOutputStream outPDF = new ByteArrayOutputStream();
            PdfStamper stamper = new PdfStamper(reader, outPDF);
            stamper.setEncryption(openPass.getBytes(), ownerPass.getBytes(), params, PdfWriter.ENCRYPTION_AES_128);
            stamper.close();

            return outPDF.toByteArray();
        } catch (DocumentException e) {
            this._error = e;
            return null;
        } catch (IOException e) {
            this._error = e;
            return null;
        }
    }

    /**
     * Adds the attachments to the PDF and encrypts it.
     *
     * @param inPDF A {@link  PdfSource} object referencing the PDF to be
     * encrypted.
     * @param openPass The password for opening the encrypted PDF.
     * @param ownerPass The password for other access to the encrypted PDF.
     * @param params PDF parameters
     * (e.g. <code>PdfWriter.ALLOW_FILL_IN | PdfWriter.ALLOW_PRINTING | PdfWriter.ALLOW_COPY</code>).
     * @param attachments The array of file attachments to be added before
     * encryption.
     * @param names The array of the names to be given to the attachments.
     * @param descriptions The array of descriptions of the file attachments.
     * @return The encrypted PDF as a {@link Byte} array, or <code>null</code>
     * if there was an error.
     */
    public byte[] encrypt(PdfSource inPDF, String openPass, String ownerPass, Integer params, String[] attachments, String[] names, String[] descriptions) {
        try {
            if (attachments.length != names.length || attachments.length != descriptions.length) {
                this._error = new Exception("Attachment definition array sizes incompatible!");
                return null;
            } else {
                PdfReader reader = inPDF.getReader();
                ByteArrayOutputStream withAttachments = new ByteArrayOutputStream();

                PdfStamper stamper = new PdfStamper(reader, withAttachments);

                for (int i = 0; i < attachments.length; i++) {
                    stamper.addFileAttachment(descriptions[i], null, attachments[i], names[i]);
                }

                stamper.close();

                return encrypt(new PdfSource(withAttachments.toByteArray()), openPass, ownerPass, params);
            }
        } catch (DocumentException e) {
            this._error = e;
            return null;
        } catch (IOException e) {
            this._error = e;
            return null;
        }
    }

    /**
     * Adds the attachments to the PDF and encrypts it using the default
     * parameters: Allow Fill-in, Allow printing, Allow copying.
     *
     * @param inPDF A {@link  PdfSource} object referencing the PDF to be
     * encrypted.
     * @param openPass The password for opening the encrypted PDF.
     * @param ownerPass The password for other access to the encrypted PDF.
     * @param attachments The array of file attachments to be added before
     * encryption.
     * @param names The array of the names to be given to the attachments.
     * @param descriptions The array of descriptions of the file attachments.
     * @return The encrypted PDF as a {@link Byte} array, or <code>null</code>
     * if there was an error.
     */
    public byte[] encrypt(PdfSource inPDF, String openPass, String ownerPass, String[] attachments, String[] names, String[] descriptions) {
        return this.encrypt(inPDF, openPass, ownerPass, PdfWriter.ALLOW_FILL_IN | PdfWriter.ALLOW_PRINTING | PdfWriter.ALLOW_COPY, attachments, names, descriptions);
    }

    public static void main(String[] args) {
        try {
            Encryptor enc = new Encryptor();
//            byte[] pdf = enc.encrypt(new PdfSource("C:\\Documents and Settings\\davidz\\Desktop\\PDFs\\all.pdf"), "open", "owner",
//                    new String[]{"C:\\Documents and Settings\\davidz\\Desktop\\test.txt"}, new String[]{"guombl.txt"}, new String[]{"an interesting text file"});
            byte[] pdf = enc.encrypt(new PdfSource("C:\\Documents and Settings\\davidz\\Desktop\\Bank_Statement.pdf"), "0550", "0550");
            if (enc.getError() == null) {
                PdfResult res = new PdfResult(pdf);
                res.writeFile("C:\\Documents and Settings\\davidz\\Desktop\\Bank_Statement_encrypted.pdf");
                System.out.println("OK");
            } else {
                System.out.println(il.co.aman.apps.Misc.message(enc.getError()));
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
