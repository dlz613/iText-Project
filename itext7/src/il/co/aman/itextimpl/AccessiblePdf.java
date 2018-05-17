package il.co.aman.itextimpl;

import java.io.File;
import java.io.IOException;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfViewerPreferences;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;

public class AccessiblePdf {
	
	static final String DEST = "C:\\Users\\davidz\\Desktop\\access7.pdf";
	
	 public static void main(String args[]) throws IOException {
	        File file = new File(DEST);
	        file.getParentFile().mkdirs();
	        new AccessiblePdf().createPdf(DEST);
	    }
	
	public void createPdf(String dest) throws IOException {
        PdfDocument pdf = new PdfDocument(new PdfWriter(dest, new WriterProperties().addXmpMetadata()));
        Document document = new Document(pdf);

        //Setting some required parameters
        pdf.setTagged();
        pdf.getCatalog().setLang(new PdfString("en-US"));
        pdf.getCatalog().setViewerPreferences(
                new PdfViewerPreferences().setDisplayDocTitle(true));
        PdfDocumentInfo info = pdf.getDocumentInfo();
        info.setTitle("iText7 PDF/UA example");
        
        //Fonts need to be embedded
        PdfFont font = PdfFontFactory.createFont("C:\\Users\\davidz\\Desktop\\Fonts\\arial.ttf", PdfEncodings.WINANSI, true);
        Paragraph p = new Paragraph();
        p.setFont(font);
        Text t = new Text("The quick brown ");
        t.setAction(PdfAction.createURI("http://www.aman.co.il/"));
        p.add(t);
        Image foxImage = new Image(ImageDataFactory.create("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSaoawZu0FWbRxb3VNE7ruuow7k1_1fw_mkhLPExTSayfBHok1PkQ"));
        //PDF/UA: Set alt text
        foxImage.getAccessibilityProperties().setAlternateDescription("Fox");
//        PdfLinkAnnotation annotation = new PdfLinkAnnotation(new Rectangle(0, 0))
//        	.setAction(PdfAction.createURI("http://itextpdf.com/"));
        foxImage.setAction(PdfAction.createURI("http://itextpdf.com/"));
        //Link link = new Link("here", annotation);
        p.add(foxImage);
        p.add(" jumps over the lazy ");
        Image dogImage = new Image(ImageDataFactory.create("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQy6FZOdEV-94YXrdTTQjvyE92Ci2DZqeeVMeeEEdpO8Ze9X3n40fd5rg"));
        //PDF/UA: Set alt text
        dogImage.getAccessibilityProperties().setAlternateDescription("Dog");
        p.add(dogImage);
 
        document.add(p);
        document.close();
	}

}
