package il.co.aman.formit;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfImage;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfStream;
import com.itextpdf.text.pdf.parser.PdfImageObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CopyPdf {
	
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
            //PdfImage image = new PdfImage(makeBlackAndWhitePng(new PdfImageObject(stream)), "", null);
            replaceStream(stream, stream);
        }
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
    	String src = "\\\\formit7\\c$\\TEMP\\Altshuler\\15-3GEMEL.PDF";
    	String dest = "C:\\Users\\davidz\\Desktop\\15-3GEMEL.pdf";
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
