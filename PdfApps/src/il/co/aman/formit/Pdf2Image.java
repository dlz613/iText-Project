package il.co.aman.formit;

import il.co.aman.apps.Misc;
import il.co.aman.formit.sendit.PdfSource;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree; // in PdfBox 2.0.6.
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

/**
 *
 * @author davidz
 */
public class Pdf2Image {

    public enum ImageTypes {

    	/**
    	 * <code>TIFF</code> requires that <code>jai_imageio.jar</code> be on the classpath.
    	 */
        BMP(".bmp"), GIF(".gif"), JPEG(".jpg"), PNG(".png") ,TIFF(".tif");

        String _extension;

        ImageTypes(String ext) {
            this._extension = ext;
        }
        
        /**
         * If <code>type</code> is null or unrecognized, defaults to <code>PNG</code>.
         * @param type Not case-sensitive.
         * @return
         */
        public static ImageTypes fromString(String type) {
        	if (type == null) {
        		type = "";
        	}
        	type = type.trim().toLowerCase();
        	if (type.equals("bmp")) {
        		return ImageTypes.BMP;
        	} else if (type.equals("gif")) {
        		return ImageTypes.GIF;
        	} else if (type.equals("jpg") || type.equals("jpeg")) {
        		return ImageTypes.JPEG;
        	} else if (type.equals("png")) {
        		return ImageTypes.PNG;
        	} else if (type.equals("tif") || type.equals("tiff")) {
        		return ImageTypes.TIFF;
        	} else {
        		return ImageTypes.PNG;
        	}
        }

        /**
         *
         * @return The appropriate file extension, <b>including</b> the leading
         * period.
         */
        public String extension() {
            return this._extension;
        }
    }

    Exception _error;
    int _errorPage;
    PDDocument _document;
    PDFRenderer _pdfRenderer;

    public Exception getError() {
        return this._error;
    }

    /**
     *
     * @return The page (one-based) on which the error occurred.
     */
    public int getErrorPage() {
        return this._errorPage;
    }
    
    public PDDocument document() {
    	return this._document;
    }
    
    public Pdf2Image() {
    	
    }
    
    public Pdf2Image(InputStream inPDF) throws InvalidPasswordException, IOException {
    	this._document = PDDocument.load(inPDF);
    	this._pdfRenderer = new PDFRenderer(this._document);
    }

    /**
     * Converts each page of the PDF to a {@link BufferedImage} object.
     * @deprecated
     * @param inPDF
     * @param resolution The desired resolution of the images. For printing one
     * would usually use 300 (dpi).
     * @return If no error occurred, <code>getError()</code> will be
     * <code>null</code>.
     */
    public ArrayList<BufferedImage> convertv18(InputStream inPDF, int resolution) {
        int page = 0;
        ArrayList<BufferedImage> outImages = new ArrayList<BufferedImage>();
        try {
            PDDocument doc = PDDocument.load(inPDF);//(inPDF, null);
            //List<PDPage> pdPages = (List<PDPage>) doc.getDocumentCatalog().getPages();
            PDPageTree pdPages = doc.getDocumentCatalog().getPages();
            for (PDPage pdPage : pdPages) {
                ++page;
                //BufferedImage bim = pdPage.(BufferedImage.TYPE_INT_RGB, resolution);
                //outImages.add(bim);
            }
            this._error = null;
            return outImages;
        } catch (IOException e) {
            this._error = e;
            this._errorPage = page;
            return outImages;
        }
    }
    
    public byte[] convertSingle(int page, ImageTypes type, int resolution) throws IOException {
    	resolution = 96;
    	BufferedImage bim = this._pdfRenderer.renderImageWithDPI(page, resolution, ImageType.RGB);
    	return writeImage(bim, type, resolution);
    }
    
    /**
     * Uses PdfBox v. 2 (for proper handling of ZapfDingbats fonts)
     * @param inPDF
     * @param resolution Currrently hard-coded to 96.
     * @return
     * @throws InvalidPasswordException
     * @throws IOException
     */
    public ArrayList<BufferedImage> convert(InputStream inPDF, int resolution) throws InvalidPasswordException, IOException {
    	resolution = 96;
    	ArrayList<BufferedImage> outImages = new ArrayList<BufferedImage>();
    	PDDocument document = PDDocument.load(inPDF);
    	PDFRenderer pdfRenderer = new PDFRenderer(document);
    	for (int page = 0; page < document.getNumberOfPages(); ++page)
    	{ 
    	    BufferedImage bim = pdfRenderer.renderImageWithDPI(page, resolution, ImageType.RGB);
    	    outImages.add(bim);
    	}
    	document.close();
    	return outImages;
    }
    
    public ArrayList<byte[]> convert(InputStream inPDF, ImageTypes type, int resolution) throws InvalidPasswordException, IOException {
    	ArrayList<BufferedImage> images = convert(inPDF, resolution);
    	ArrayList<byte[]> res = new ArrayList<byte[]>();
    	for (BufferedImage img: images) {
    		res.add(writeImage(img, type, resolution));
    	}
    	return res;
    }

    /**
     * Writes each page of the PDF to a distinct image file.
     *
     * @param inPDF
     * @param type The image type.
     * @param basePath The path of the image files, <b>including</b> the base of
     * the filename.<br>
     * <code>"_&lt;page num>"</code> and the appropriate
     * extension are appended to the path.
     * @param resolution The desired resolution of the images. For printing one
     * would usually use 300 (dpi).
     * @return The number of pages processed. If an error occurred, returns -1
     * (and then the error will be returned in <code>getError()</code>).
     * @throws IOException 
     * @throws InvalidPasswordException 
     */
    public int convert(InputStream inPDF, ImageTypes type, String basePath, int resolution) throws InvalidPasswordException, IOException {
        ArrayList<BufferedImage> outImages = convert(inPDF, resolution);
        if (this._error == null) {
            return _write(outImages, type, basePath, resolution);
        } else {
            return -1;
        }
    }
    
    public ArrayList<byte[]> convert(InputStream inPDF, String password, ImageTypes type, int resolution) {
    		if (password == null) {
    			password = "";
    		}
    		PdfSource src;
			try {
				src = new PdfSource(inPDF, password.getBytes());
			} catch (IOException e) {
				this._error = e;
				return null;
			}
    		try {
				return convert(new ByteArrayInputStream(src.getBytes()), type, resolution);
			} catch (InvalidPasswordException e) {
				this._error = e;
				return null;
			} catch (IOException e) {
				this._error = e;
				return null;
			}
    	//}
    }

    /**
     * Writes each page of the PDF to a distinct image file.
     *
     * @param inPDF
     * @param password The password of the PDF, if any. May be <code>null</code>
     * @param type The image type.
     * @param basePath The path for the output image files, <b>including</b> the base of
     * the filename.<br>
     * <code>"_&lt;page num>"</code> and the appropriate
     * extension are appended to the path.
     * @param resolution The desired resolution of the images. For printing one
     * would usually use 300 (dpi).
     * @return The number of pages processed. If an error occurred, returns -1
     * (and then the error will be returned in <code>getError()</code>).
     */
    public int convert(InputStream inPDF, String password, ImageTypes type, String basePath, int resolution) {
        try {
            if (password == null) {
                password = "";
            }
            PdfSource src = new PdfSource(inPDF, password.getBytes());
            return convert(new ByteArrayInputStream(src.getBytes()), type, basePath, resolution);
        } catch (Exception e) {
            this._error = e;
            return -1;
        }
    }

    private int _write(ArrayList<BufferedImage> images, ImageTypes type, String basePath, int resolution) {
        int pages = images.size();
        int page = 0;
        try {
            for (int i = 1; i <= pages; i++) {
                page = i;
                writeImage(images.get(i - 1), type, basePath, i, resolution);
                /*FileOutputStream fos = new FileOutputStream(basePath + "_" + Integer.toString(i) + type.extension());
                ImageIOUtil.writeImage(images.get(i - 1), type.toString(), fos, resolution);
                fos.close();*/
            }
            this._error = null;
            return pages;
        } catch (IOException e) {
            this._error = e;
            this._errorPage = page;  
            return -1;
        }
    }
    
    public static void writeImage(BufferedImage image, ImageTypes type, String basePath, int serial, int resolution) throws IOException {
   		FileOutputStream fos = new FileOutputStream(basePath + "_" + Integer.toString(serial) + type.extension());
    	ImageIOUtil.writeImage(image, type.toString(), fos, resolution);
    }
    
    public static byte[] writeImage(BufferedImage image, ImageTypes type, int resolution) throws IOException {
    	ByteArrayOutputStream bos = new ByteArrayOutputStream();
    	ImageIOUtil.writeImage(image, type.toString(), bos, resolution);
    	return bos.toByteArray();
    }

    public static void main(String[] args) throws ClassNotFoundException {
        java.security.CodeSource codesrc = org.apache.pdfbox.tools.imageio.ImageIOUtil.class.getProtectionDomain().getCodeSource(); //but then need to catch ClassNotFoundException
        if (codesrc != null) {
            java.net.URL jar = codesrc.getLocation();
            System.out.println(jar.toString());
        }
        //System.exit(0);
        try {
            String pdfFilename = "C:\\Users\\davidz\\Desktop\\PDFs\\634369_1_22038324840001_report.pdf";//"C:\\Users\\davidz\\Downloads\\46_2.pdf";//"\\\\formit7\\c$\\Informatica\\pc96\\tomcat\\webapps\\formit\\Graphical_Signature\\In\\06032017\\314141c4-4bed-4487-989d-8ba956a02ae7.pdf";
            FileInputStream fis = new FileInputStream(pdfFilename);
            Pdf2Image pdf2image = new Pdf2Image(fis);
            //pdf2image.convert(fis, "", ImageTypes.PNG, "C:\\Users\\davidz\\Desktop\\pdf2image\\634369_1_22038324840001_report", 96);
            /*ArrayList<byte[]> imgs = pdf2image.convert(fis, ImageTypes.PNG, 96);
            for (int i = 0; i < imgs.size(); i++) {
            	Misc.writeBinFile(imgs.get(i), "C:\\Users\\davidz\\Desktop\\PNGs\\" + Integer.toString(i) + ".png");
            }*/
            for (int i = 0; i < pdf2image.document().getNumberOfPages(); i++) {
            	Misc.writeBinFile(pdf2image.convertSingle(i, ImageTypes.PNG, 96), "C:\\Users\\davidz\\Desktop\\PNGs\\" + Integer.toString(i) + ".png");
            }
            System.out.println(pdf2image.getError());
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
