package il.co.aman.itextimpl;

import il.co.aman.apps.Misc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.itextpdf.barcodes.Barcode128;
import com.itextpdf.barcodes.Barcode1D;
import com.itextpdf.barcodes.Barcode2D;
import com.itextpdf.barcodes.BarcodeDataMatrix;
import com.itextpdf.barcodes.BarcodeEAN;
import com.itextpdf.barcodes.BarcodePDF417;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfViewerPreferences;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.ColumnDocumentRenderer;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Link;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.AreaBreakType;
import com.itextpdf.layout.property.BaseDirection;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;

public class CreatePdf {
	
    public static Paragraph createBarcodeEAN(String code, PdfDocument pdfDoc) {
        BarcodeEAN barcode = new BarcodeEAN(pdfDoc);
        barcode.setCodeType(BarcodeEAN.EAN8);
        barcode.setCode(code);
        Paragraph cell = new Paragraph().add(new Image(barcode.createFormXObject(null, null, pdfDoc)));
        cell.setPaddingTop(10);
        cell.setPaddingRight(10);
        cell.setPaddingBottom(10);
        cell.setPaddingLeft(10);
        return cell;
    }
    
    public static Paragraph createBarcode128(String code, PdfDocument pdfDoc) {
    	Barcode128 barcode = new Barcode128(pdfDoc);
    	barcode.setCode(code);
    	Paragraph cell = new Paragraph().add(new Image(barcode.createFormXObject(null, null, pdfDoc)));
    	cell.setPadding(10);
    	return cell;
    }
    
    public static Paragraph putBarcode(Barcode1D barcode, PdfDocument pdfDoc) {
    	Paragraph para = new Paragraph().add(new Image(barcode.createFormXObject(null, null, pdfDoc)));
    	return para;
    }
    
    public static Image putBarcode(Barcode2D barcode, PdfDocument pdfDoc, float mw, float mh) {
    	return new Image(barcode.createFormXObject(ColorConstants.BLACK, pdfDoc)).scale(mw, mh);
    }
    
    public static Image createBarcode417(float mw, float mh, PdfDocument pdfDoc) {
        BarcodePDF417 barcode = new BarcodePDF417();
        barcode.setCode("BarcodePDF417 barcode");
        return new Image(barcode.createFormXObject(ColorConstants.BLACK, pdfDoc)).scale(mw, mh).setMargins(10f,  10f,  10f,  10f);
    }

    public static Image createBarcodeDataMatrix(String text, String encoding, float mw, float mh, PdfDocument pdfDoc) throws UnsupportedEncodingException {
    	BarcodeDataMatrix barcode = new BarcodeDataMatrix();
     	barcode.setOptions(BarcodeDataMatrix.DM_TEXT);
   	    barcode.setEncoding(encoding);
        barcode.setCode(text);
        return new Image(barcode.createFormXObject(ColorConstants.BLACK, pdfDoc)).scale(mw, mh);
    }
    
    public static void createPdf(String text, GetFonts fontbank, String dest) throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
        Document doc = new Document(pdfDoc);
        doc.setBaseDirection(BaseDirection.RIGHT_TO_LEFT);
        PdfFont f = fontbank.getFont("Arial", true);
        Paragraph p = new Paragraph(fixBidiText(text));
        Style hebrew = new Style().setTextAlignment(TextAlignment.LEFT).setBaseDirection(BaseDirection.RIGHT_TO_LEFT).setFontSize(20).setFont(f);
        p.addStyle(hebrew);
        doc.add(p);
        pdfDoc.close();
        doc.close();
    }
    
    private static String[] splitByLang(String val) {
    	ArrayList<String> res = new ArrayList<String>();
    	StringBuilder substr = new StringBuilder();
    	boolean isHebrew = false;
    	boolean inString = false;
    	for (int i = 0; i < val.length(); i++) {
    		char c = val.charAt(i);
    		if (isHebrew(c)) {
        		if (inString && !isHebrew) {
        			res.add(substr.toString());
        			substr = new StringBuilder();
        		}
          		isHebrew = true;
    		} else if (isHebrew && c != ' ' && c != ',' && c != ';' && c != ':' && c != '.' && c != '\'') {
    			if (inString) {
    				res.add(substr.toString());
    				substr = new StringBuilder();
    			}
    			isHebrew = false;
    		}
    		substr.append(c);
    		inString = true;
    	}
    	res.add(substr.toString());
    	
    	return res.toArray(new String[] {});
    }
    
    private static boolean isHebrew(char c) {
    	return c >= '\u05d0' && c <= '\u05ea';
    }
    
    public static String fixBidiText(String val) {
    	String[] substrs = splitByLang(val);
    	StringBuilder str = new StringBuilder();
    	for (String substr: substrs) {
    		if (substr.trim().length() > 0 && isHebrew(substr.trim().charAt(0))) {
    			str.append(reverseText(substr));
    		} else {
    			str.append(substr);
    		}
    	}
    	return str.toString();
    }
    
    private static String reverseText(String val) {
    	StringBuilder res = new StringBuilder();
    	for (int i = val.length() - 1; i >= 0; i--) {
    		res.append(val.charAt(i));
    	}
    	return res.toString();
    }

	public static void main(String[] args) throws IOException {
		String test = "רח' שדי חמד 34/16, מודיעין עילית";
		test = " הנתונים הכספיים המוצגים בדוח מעוגלים לשקל הקרוב.";
		System.out.println(fixBidiText(test));
		System.exit(0);
		GetFonts fontbank = new GetFonts("C:\\Users\\davidz\\Desktop\\Fonts");
		createPdf(test, fontbank, "C:\\Users\\davidz\\Desktop\\verysimple.pdf");
		System.exit(0);
		OutputStream fos = null;
		try {
			fos = new FileOutputStream("C:\\Users\\davidz\\Desktop\\itext7.pdf");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		PdfWriter writer = new PdfWriter(fos);
		PdfDocument pdf = new PdfDocument(writer);
		pdf.setTagged();
		pdf.getCatalog().setLang(new PdfString("en-US"));
		pdf.getCatalog().setViewerPreferences(new PdfViewerPreferences().setDisplayDocTitle(true));
		PdfDocumentInfo info = pdf.getDocumentInfo();
		info.setTitle("iText7 PDF/UA example");
		pdf.getXmpMetadata();
		Document document = new Document(pdf);
		
		//PdfFont font = fontbank.getFont("Arial Black", true);
		//Paragraph p = new Paragraph("Hello World!");
		Style normal = new Style();
		PdfFont font = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
		normal.setFont(font).setFontSize(14);
		Style code = new Style();
		PdfFont monospace = PdfFontFactory.createFont(FontConstants.COURIER);
		code.setFont(monospace).setFontColor(ColorConstants.RED).setBackgroundColor(ColorConstants.LIGHT_GRAY);
		Paragraph p = new Paragraph();
		p.add(new Text("The Strange Case of ").addStyle(normal));
		p.add(new Text("Dr. Jekyll").addStyle(code));
		p.add(new Text(" and ").addStyle(normal));
		p.add(new Text("Mr. Hyde").addStyle(code));
		p.add(new Text(".").addStyle(normal));
		document.add(p);
		String hebrewText = "שלום העולם!";
		p = new Paragraph(hebrewText);
		//System.out.println(hebrewText.charAt(0));
		//PdfFont hf = fontbank.getFont("Arial", true)).setFontColor(Color.RED);
		//p.setFont(fontbank.getFont("Arial", true)).setFontColor(Color.RED);
		Style hebrew = new Style()
			.setHorizontalAlignment(HorizontalAlignment.RIGHT)
			//.setTextAlignment(TextAlignment.RIGHT)
			.setBaseDirection(BaseDirection.RIGHT_TO_LEFT)
			.setFontSize(14).setFontColor(ColorConstants.RED).
			setFont(fontbank.getFont("Guttman Rashi", true));
		p.addStyle(hebrew);
		p.setFixedPosition(145, 300, 75);
		document.add(p);
		String borderStyle = "double";
		Border border = new Border(5f, "50,255,0", borderStyle);
		Border.setBorders(pdf.getPage(1), 20f, 400f, 400f, 820f,
				border, new Border(5f, "255,0,0", borderStyle), new Border(5f, "0,0,255", borderStyle), new Border(5f, "0,0,0", borderStyle));
		//document.add(createBarcodeEAN("12345678", pdf));
		//document.add(createBarcode128("hello there", pdf));
		//document.add(createBarcode417(1f, 1f, pdf));
		document.add(new Paragraph(""));
		document.add(createBarcodeDataMatrix("Вставай, проклятьем заклеймённый", "Cp1251", 6f, 6f, pdf));
		
		//Rectangle[] areas = new Rectangle[]{new Rectangle(50, 750, 200, 50), new Rectangle(300, 750, 200, 50)};
		
		//document.setRenderer(new ColumnDocumentRenderer(document, areas));
		pdf.addNewPage();
		//document.add(new AreaBreak(AreaBreakType.LAST_PAGE));
//		for (Rectangle rect : areas) {
//		    new PdfCanvas(pdf.getPage(2)).setLineWidth(0.5f).setStrokeColor(Color.RED).rectangle(rect).stroke();
//		    new PdfCanvas(pdf.getPage(2)).saveState().setColor(Color.ORANGE, true).rectangle(rect).fill().restoreState();
//		}
		/*addColumn(document, false, null);
		addColumn(document, true, Color.YELLOW);*/
		
		addTextAbsolute(document, 50, 350, 200, 50, new Paragraph("hi there guys!"));
		new PdfCanvas(pdf.getPage(2)).setLineWidth(0.5f).setStrokeColor(ColorConstants.RED).rectangle(new Rectangle(50, 350, 200, 50)).stroke();
		Image img = new Image(ImageDataFactory.create("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSaoawZu0FWbRxb3VNE7ruuow7k1_1fw_mkhLPExTSayfBHok1PkQ"));
		img.getAccessibilityProperties().setAlternateDescription("this is a fox");
		Rectangle linkLocation = new Rectangle(200,350, 250, 400);
		PdfLinkAnnotation annotation = new PdfLinkAnnotation(linkLocation).setAction(PdfAction.createURI("http://itextpdf.com/"));
		pdf.getLastPage().addAnnotation(annotation);
		addImageAbsolute(document, 200, 350, 50, 50, img);
		addImageAbsolute(document, 50, 550, 50, 50, createBarcodeDataMatrix("שלום עליכם", "Cp1255", 6f, 6f, pdf));
		pdf.addNewPage();
		document.add(new AreaBreak(AreaBreakType.LAST_PAGE));
		document.add(new Paragraph("sfsfsfsdf sdfsfsd"));

		document.close();
	}
	
	public static void addTextAbsolute(Document doc, int x, int y, int width, int height, Paragraph p) {
		Rectangle[] area = new Rectangle[] {new Rectangle(x, y, width, height)};
		doc.setRenderer(new ColumnDocumentRenderer(doc, area));
		doc.add(new AreaBreak(AreaBreakType.LAST_PAGE));
		p.setMargin(0);
		p.setMultipliedLeading(1);
		doc.add(p);
	}
	
	public static void addImageAbsolute(Document doc, int x, int y, int width, int height, Image i) {
		Rectangle[] area = new Rectangle[] {new Rectangle(x, y, width, height)};
		doc.setRenderer(new ColumnDocumentRenderer(doc, area));
		doc.add(new AreaBreak(AreaBreakType.LAST_PAGE));
		Paragraph p = new Paragraph();
		p.add(i);
		p.setMargin(0);
		p.setMultipliedLeading(1);
		doc.add(p);
	}

	public static void addColumn(Document doc, boolean useAscender, Color textbackground) {
		Text t = new Text("This text is added at the top of the column.");
		if (textbackground != null) {
			t.setBackgroundColor(textbackground);
		}
	    Paragraph p = new Paragraph(t);
	    if (useAscender) {
	        p.setMargin(0);
	        p.setMultipliedLeading(1);
	    }
	    doc.add(p);
	}

}
