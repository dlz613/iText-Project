package il.co.aman.formit;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.parser.PdfImageObject;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PRStream;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import il.co.aman.formit.sendit.PdfResult;
import il.co.aman.formit.sendit.PdfSource;

/**
 *
 * @author davidz
 */
public class CompressPdf {

    /**
     * The multiplication factor for the size of the image.
     */
    public static float FACTOR = 1f;

    /**
     * Reduces the size of the images in a PDF
     *
     * @param src the original PDF
     * @param factor The multiplication factor for the size of the image.
     * @return A {@link PdfResult} object containing the compressed PDF
     * @throws IOException
     * @throws DocumentException
     */
    public static PdfResult compress(PdfSource src, float factor) throws IOException, DocumentException {
        PdfReader reader = src.getNewReader();
        PdfObject object;
        PRStream stream;

        for (int i = 0; i < reader.getXrefSize(); i++) {
            object = reader.getPdfObject(i);
            if (object == null || !object.isStream()) {
                continue;
            }
            stream = (PRStream) object;
            PdfObject pdfsubtype = stream.get(PdfName.SUBTYPE);
            if (pdfsubtype != null && pdfsubtype.toString().equals(PdfName.IMAGE.toString())) {
                PdfImageObject image = new PdfImageObject(stream);
                BufferedImage bi = image.getBufferedImage();
                if (bi == null) {
                    continue;
                }
                int width = (int) (bi.getWidth() * factor);
                int height = (int) (bi.getHeight() * factor);
                BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                AffineTransform at = AffineTransform.getScaleInstance(factor, factor);
                Graphics2D g = img.createGraphics();
                g.drawRenderedImage(bi, at);
                ByteArrayOutputStream imgBytes = new ByteArrayOutputStream();
                ImageIO.write(img, "JPG", imgBytes);
                stream.clear();
                stream.setData(imgBytes.toByteArray(), false, PRStream.BEST_COMPRESSION);
                stream.put(PdfName.TYPE, PdfName.XOBJECT);
                stream.put(PdfName.SUBTYPE, PdfName.IMAGE);
                stream.put(PdfName.FILTER, PdfName.DCTDECODE);
                stream.put(PdfName.WIDTH, new PdfNumber(width));
                stream.put(PdfName.HEIGHT, new PdfNumber(height));
                stream.put(PdfName.BITSPERCOMPONENT, new PdfNumber(8));
                stream.put(PdfName.COLORSPACE, PdfName.DEVICERGB);
            }
        }
        // Save altered PDF
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PdfStamper stamper = new PdfStamper(reader, bos);
        stamper.close();
        reader.close();
        return new PdfResult(bos.toByteArray());
    }
    
    public static PdfResult compress(PdfSource src) throws DocumentException, IOException {
        return compress(src, FACTOR);
    }

    public static void main(String[] args) {
        String src = "C:\\Users\\davidz\\Desktop\\Bank_Scan.pdf";
        String res = "C:\\Users\\davidz\\Desktop\\compressed.pdf";
        try {
            PdfResult compressed = compress(new PdfSource(src), 0.5f);
            compressed.writeFile(res);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (DocumentException e) {
            System.out.println(e.getMessage());
        }
    }
}
