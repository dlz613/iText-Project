package il.co.aman.formit;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfImage;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfStream;
import com.itextpdf.text.pdf.parser.PdfImageObject;

import il.co.aman.apps.Misc;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Converts images in a PDF to gray scale.<br>NOTE: <b>Not</b>
 * guaranteed to make the result PDF smaller than the source.
 *
 * @author davidz
 */
public class Pix2Gray {

    /**
     * Converts all of the images in the PDF to gray scale.
     *
     * @param src
     * @return
     * @throws DocumentException
     * @throws IOException
     */
    public static byte[] manipulatePdf(byte[] src) throws DocumentException, IOException {
        return manipulatePdf(src, 0);
    }

    /**
     * Converts all of the images on the specified page of the PDF to gray
     * scale.
     *
     * @param src
     * @param pagenum 1-based page number. If zero or less, converts all of the
     * pages.
     * @return
     * @throws DocumentException
     * @throws IOException
     */
    public static byte[] manipulatePdf(byte[] src, int pagenum) throws DocumentException, IOException {
        PdfReader reader = new PdfReader(src);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfStamper stamper = new PdfStamper(reader, baos);
        int start, end;
        if (pagenum < 1) {
            start = 1;
            end = reader.getNumberOfPages();
        } else {
            start = pagenum;
            end = pagenum;
        }
        for (int i = start; i <= end; i++) {
            PdfDictionary page = reader.getPageN(i);
            manipulatePage(page);
        }
        stamper.close();
        reader.close();
        return baos.toByteArray();
    }

    private static void manipulatePage(PdfDictionary page) throws DocumentException, IOException {
        PdfDictionary resources = page.getAsDict(PdfName.RESOURCES);
        PdfDictionary xobjects = resources.getAsDict(PdfName.XOBJECT);
        for (PdfName imgRef : xobjects.getKeys()) {
            PRStream stream = (PRStream) xobjects.getAsStream(imgRef);
            new PdfImageObject(stream, null);
        	PdfImage image = new PdfImage(makePng(new PdfImageObject(stream)), "", null);
        	replaceStream(stream, image);
        }
    }

    public static Image makeBlackAndWhitePng(PdfImageObject image) throws IOException, DocumentException {
        BufferedImage bi = image.getBufferedImage();
        BufferedImage newBi = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_USHORT_GRAY);
        newBi.getGraphics().drawImage(bi, 0, 0, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(newBi, "png", baos);
        return Image.getInstance(baos.toByteArray());
    }

    public static Image makePng(PdfImageObject image) throws IOException, DocumentException {
        BufferedImage bi = image.getBufferedImage();
        BufferedImage newBi = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_RGB);
        newBi.getGraphics().drawImage(bi, 0, 0, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(newBi, "png", baos);
        return Image.getInstance(baos.toByteArray());
    }

    public static void replaceStream(PRStream orig, PdfStream stream) throws IOException {
        orig.clear();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        stream.writeContent(baos);
        orig.setData(baos.toByteArray(), false);
        for (PdfName name : stream.getKeys()) {
            orig.put(name, stream.get(name));
        }
    }

    public static void main(String[] args) {
        //String src = "C:\\Users\\davidz\\Desktop\\PDFs\\MakeUseOf.com-Dropbox.pdf";
        String src = "C:\\Users\\davidz\\Desktop\\output.pdf";
        String dest = "C:\\Users\\davidz\\Desktop\\Dropbox-BW.pdf";
        try {
            byte[] dest_pdf = manipulatePdf(il.co.aman.apps.Misc.readBinFile(src));
            il.co.aman.apps.Misc.writeBinFile(dest, dest_pdf);
        } catch (DocumentException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
