package il.co.aman.itextimpl;

import java.io.File;
import java.io.FileOutputStream;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;

//import com.itextpdf.text.BaseColor;
//import com.itextpdf.text.Document;
//import com.itextpdf.text.PageSize;
//import com.itextpdf.text.pdf.BaseFont;
//import com.itextpdf.text.pdf.PdfContentByte;
//import com.itextpdf.text.pdf.PdfDictionary;
//import com.itextpdf.text.pdf.PdfReader;
//import com.itextpdf.text.pdf.PdfStamper;
//import com.itextpdf.text.pdf.PdfString;
//import com.itextpdf.text.pdf.PdfStructureElement;

/**
 * 
 * @author davidz
 */
public class TaggedPdf {
	
	/**
	 * The supported tag types.
	 * @author davidz
	 *
	 */
	public enum TagTypes {
		H1(PdfName.H1),
		H2(PdfName.H2),
		H3(PdfName.H3),
		H4(PdfName.H4),
		H5(PdfName.H5),
		H6(PdfName.H6),
		CAPTION(PdfName.Caption),
		TOOLTIP(new PdfName("TOOLTIP")),
		NONE(null);
		
		PdfName _type;
		
		private TagTypes(PdfName type) {
			this._type = type;
		}
		
		public PdfName getType() {
			return this._type;
		}
		
		/**
		 * Creates a {@link TagTypes} object from it's name.
		 * @param val case-sensitive.
		 * @return <code>NONE</code> if <code>val</code> is <code>null</code> or invalid.
		 */
		public static TagTypes fromString(String val) {
			if ("H1".equals(val)) {
				return TagTypes.H1;
			} else if ("H2".equals(val)) {
				return TagTypes.H2;
			} else if ("H3".equals(val)) {
				return TagTypes.H3;
			} else if ("H4".equals(val)) {
				return TagTypes.H4;
			} else if ("H5".equals(val)) {
				return TagTypes.H5;
			} else if ("H6".equals(val)) {
				return TagTypes.H6;
			} else if ("CAPTION".equals(val)) {
				return TagTypes.CAPTION;
			} else {
				return TagTypes.NONE;
			}
		}
		
		public static TagTypes header(int level) {
			return fromString("H" + Integer.toString(level));
		}
	}
	
	public static PdfStructElem initStructure(PdfDocument pdf) {
		/*PdfStructTreeRoot root =  */pdf.getStructTreeRoot();
		//root.mapRole(new PdfName("Everything"), new PdfName("Sect"));
		//return new PdfStructElem(pdf, new PdfName("Everything"));
		return null;
	}

//    public static Exception markedContent() {
//        try {
//            /*Document document = new Document();
//            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("C:\\Users\\davidz\\Desktop\\marked_content1_doc.pdf"));
//            writer.setTagged();*/ // ****
//            //writer.setUserProperties(true); // **** for some reason, this was causing Adobe to crash when opening the PDF.
//            /*document.setPageSize(PageSize.A4);
//            document.addTitle("guombl schniblitz");
//            document.addLanguage("en-us");
//            document.open();
//            writer.setPdfVersion(PdfWriter.VERSION_1_7);*/
//            /*PdfStructureTreeRoot root = writer.getStructureTreeRoot();
//            root.mapRole(new PdfName("Everything"), new PdfName("Sect"));*/
//            /*PdfStructureElement eTop = initStructure(writer); /*new PdfStructureElement(root, new PdfName("Everything"));*/
//            /*PdfStructureElement e1 = new PdfStructureElement(eTop, PdfName.H1);
//            PdfStructureElement e2 = new PdfStructureElement(eTop, PdfName.P);
//            PdfStructureElement e3 = new PdfStructureElement(eTop, PdfName.CAPTION);*/
//            /*PdfContentByte cb = writer.getDirectContent();*/
//            //BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, false);
//            /*cb.setLeading(16);*/
//            //cb.setFontAndSize(bf, 12);
//            //String/*[]*/ text1 = /*new String[] {*/"Four score and seven years ago."/*}*/;
//            //String/*[]*/ text2 = /*new String[] {*/"Parlez-vous franÃ§ais?"/*}*/;
//            //cb.beginMarkedContentSequence(e1);
//            //cb.beginText();
//            //cb.setTextMatrix(50, 804);
//            //for (int k = 0; k < text1.length; ++k) {
//                //cb.showText(text1/*[k]*/);
//            //Little.addText(document, cb, text1, 50, 804, 200, 30, "Arial", 12, BaseColor.BLACK, 1, 1, false, 0);
//            //Little.addText(document, cb, text1, 50, 402, 200, 30, "Arial", 12, BaseColor.BLACK, 1, 1, false, 0);
//                //cb.showText(text1);
//            //}
//            //cb.endText();
//            //cb.endMarkedContentSequence();
////    		BorderParams borderParams = new BorderParams(2.5, 0.1, 1.9, 0.1, "255,0,0", "0,0,0", "0,0,200", "0,0,0", 
////    				"solid", "solid", "solid", "solid");
//    		BorderParams borderParams = new BorderParams(1.5, 1.5, 1.5, 1.5, "0,0,0", "0,0,0", "0,0,0", "0,0,0", 
//    				"dotted", "dotted", "dotted", "dotted");
//    		JavaItextFullCode.init("C:\\Users\\davidz\\Desktop\\Fonts", "");
//    		JavaItextFullCode.doc = null;
//    		ItextResult res = JavaItextFullCode.newDoc("C:\\Users\\davidz\\Desktop\\marked_content1_doc.pdf", null, "yes", "cm", 29.7, 21.0, "file", null, "yes", 
//    				"he-IL", "guombl schniblitz");
//    		JavaItextFullCode.writer.setViewerPreferences(PdfWriter.PageModeUseOC | PdfWriter.FitWindow);
//    		/*JavaItextFullCode.divisor = 2.54f;
//    		JavaItextFullCode.cb = cb;
//    		JavaItextFullCode.eTop = eTop;
//    		JavaItextFullCode.pageHeight = 842;
//    		//JavaItextFullCode.pageHeight = 421;
//    		JavaItextFullCode.doc = document;
//    		JavaItextFullCode.writer = writer;*/
//            res = JavaItextFullCode.textContent("text", -1, "Arial", 12.0, "regular", "", 0, /*"Hello, how are you all?"*/"?×©×œ×•×� ×¢×œ×™×›×�, ×ž×” × ×©×ž×¢", "255,0,0", "200,220,240", 
//    				new TableParams(), borderParams, "Left", "ltr", 0, 
//    				250/*(double) JavaItextFullCode.sizeConvert(1.0)*/, NewLittle.getTop(842, 1.05, 30), 
//    				150, 30, "no", "200,220,240", 1, null, null, null, null, "", "Hello", 
//    				0.0, 0.0, 0.0, 0.0, 0, "no", "no", "no", -1, "H", "N", "grape juice");
////            ItextResult res = JavaItextFullCode.textContent("text", -1, "Arial", 12.0, "regular", "", 0, "Hello", "255,0,0", "200,220,240", 
////    				new TableParams(), borderParams, "Left", "ltr", 0, 
////    				125/*(double) JavaItextFullCode.sizeConvert(1.0)*/, NewLittle.getTop(421, 5.05, 30), 
////    				50, 30, "no", "200,220,240", 1, null, null, null, null, "", "Hello", 
////    				0.0, 0.0, 0.0, 0.0, 0, "no", "no", "no", -1, "H", "N", "grape juice");
////            res = JavaItextFullCode.textContent("text", -1, "Arial", 12.0, "regular", "", 0, "A big picture", "255,0,0", "200,220,240", 
////    				new TableParams(), borderParams, "Left", "ltr", 0, 
////    				250/*(double) JavaItextFullCode.sizeConvert(1.0)*/, NewLittle.getTop(842, 7.05, 30), 
////    				50, 30, "no", "200,220,240", 1, null, null, null, "http://www.google.com", "", "A big picture", 
////    				0.0, 0.0, 0.0, 0.0, 0, "no", "no", "no", 0, "caption", "N");
//            System.out.println(res._errorMsg);
//            // table
//            res = JavaItextFullCode.textContent("text", -1, "Arial", 12.0, "bold", "underline", 0, "Amount", "0,0,0", "255,255,255", 
//            		new TableParams(), BorderParams.none(),	"Left", "ltr", 0, 
//            		20, NewLittle.getTop(842, 3.0, 30), 
//            		100, 30, "no", "255,255,255", 1, null, null, null, null, "", "Start of table", 
//            		0.0, 0.0, 0.0, 0.0, 0, "no", "no", "no", -1, "H", "N", "Bill amount");
//            res = JavaItextFullCode.textContent("text", -1, "Arial", 12.0, "bold", "underline", 0, "VAT", "0,0,0", "255,255,255", 
//            		new TableParams(), BorderParams.none(),	"Left", "ltr", 0, 
//            		120, NewLittle.getTop(842, 3.0, 30), 
//            		100, 30, "no", "255,255,255", 0, null, null, null, null, "", null, 
//            		0.0, 0.0, 0.0, 0.0, 0, "no", "no", "no", -1, "H", "N", "Value added tax");
//            res = JavaItextFullCode.textContent("text", -1, "Arial", 12.0, "regular", "", 0, "13.75", "0,0,0", "255,255,255", 
//            		new TableParams(), BorderParams.none(),	"Left", "ltr", 0, 
//            		20, NewLittle.getTop(842, 4.0, 30), 
//            		100, 30, "no", "255,255,255", 0, null, null, null, null, "", null, 
//            		0.0, 0.0, 0.0, 0.0, 0, "no", "no", "no", -1, "H", "N", "");
//            res = JavaItextFullCode.textContent("text", -1, "Arial", 12.0, "regular", "", 0, "2.20", "0,0,0", "255,255,255", 
//            		new TableParams(), BorderParams.none(),	"Left", "ltr", 0, 
//            		120, NewLittle.getTop(842, 4.0, 30), 
//            		100, 30, "no", "255,255,255", 0, null, null, null, null, "", null, 
//            		0.0, 0.0, 0.0, 0.0, 0, "no", "no", "no", -1, "H", "N", "");
//            
//            res = JavaItextFullCode.imageContent("", "C:\\Users\\davidz\\Desktop\\images\\", "batik.jpg", 
//        			1.0, 20.0, 3, 3, null,
//        			null, BorderParams.none(), "this is a picture");
//            
//            res = JavaItextFullCode.imageContent("", "C:\\Users\\davidz\\Desktop\\images\\", "logoHEB.jpg", 
//        			2.0, 21.5, 5, 5, "http://www.microsoft.com/",
//        			null, BorderParams.none(), "this is also a picture");
////            res = JavaItextFullCode.imageContent("", "C:\\Users\\davidz\\Desktop\\images\\", "batik.jpg", 
////        			1.0, 5.0, 3, 3, null,
////        			null, BorderParams.none(), "this is a picture");
////            
////            res = JavaItextFullCode.imageContent("", "C:\\Users\\davidz\\Desktop\\images\\", "logoHEB.jpg", 
////        			2.0, 5.75, 5, 5, "http://www.google.com/",
////        			null, BorderParams.none(), "this is also a picture");
//            
//            //writer.flush();
//            System.out.println(res.errorCode());
//            //double CELL_justify_spacing, double left, double top, double cellWidth, double cellHeight
//            //Little.addText(cb, "Hello", 250, 640, 50, 30, "Arial", 12, BaseColor.RED, 1, 1, false, 0);
//            /*Little.addText(document, cb, "There", 300, 670, 50, 30, "Arial", 12, BaseColor.RED, 1, 1, false, 0);*/
//            //Little.addText(document, cb, "There", 150, 335, 50, 30, "Arial", 12, BaseColor.RED, 1, 1, false, 0);
//            //PdfDictionary dict = new PdfDictionary();
//            //dict.put(PdfName.LANG, new PdfString("en-us"));
//            //cb.beginText();
//            //cb.setTextMatrix(50, 700);
//            //for (int k = 0; k < text2.length; ++k) {
//                //cb.beginMarkedContentSequence(PdfName.P, dict, true);
//                /*cb.beginMarkedContentSequence(e2);
//                //cb.newlineShowText(text2[k]);
//                //cb.showText(text2[k]);
//                Little.addText(cb, text2, 50, 670, 200, 30, "Arial", 12, BaseColor.BLACK, 1, 1, false, 0);
//                cb.endMarkedContentSequence();*/
//            //}
//            //cb.endText();
//            /*cb.beginMarkedContentSequence(e3);
//            cb.beginText();
//            cb.setTextMatrix(50, 400);
//            cb.showText("It was the ");
//            PdfDictionary dic = new PdfDictionary();
//            dic.put(new PdfName("ActualText"), new PdfString("best"));
//            cb.beginMarkedContentSequence(new PdfName("Span"), dic, true);
//            cb.showText("worst");
//            cb.endMarkedContentSequence();
//            cb.showText(" of times.");
//            cb.endText();
//            cb.endMarkedContentSequence();*/
//            
//            //writer.flush();
//            JavaItextFullCode.endDoc("C:\\Users\\davidz\\Desktop\\marked_content1_doc.pdf", null, "no");
//            /*document.close();
//            writer.close();*/
//            return null;
//        }
//        catch (Exception e) {
//            System.out.println(il.co.aman.apps.Misc.message(e));
//            return e;
//        }        
//    }
//    
//    public static Exception fakeTable() {
//        try {
//            Document doc = new Document();
//            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(new File("C:\\Users\\davidz\\Desktop\\not_tagged.pdf")));
//            doc.open();
//            PdfContentByte cb = writer.getDirectContent();
//            
//            Little.addText(doc, cb, "The quick brown fox", 20, 750, 200, 20, "Arial", 12f);
//            Little.addText(doc, cb, "Then he tripped and stumbled,", 20, 690, 300, 20, "Arial", 12f);
//            Little.addText(doc, cb, "jumped over the lazy brown dog", 200, 750, 300, 20, "Arial", 12f);
//            Little.addText(doc, cb, "and never tried that trick again.", 200, 690, 200, 20, "Arial", 12f);
//            
//            Little.addText(doc, cb, "This is on the side", 450, 750, 100, 20, "Arial", 12f);
//
//            doc.close();
//            writer.close();
//            
//            return null;
//        }
//        catch (Exception e) {
//            System.out.println(il.co.aman.apps.Misc.message(e));
//            return e;
//        }
//    }
//    
//    public static Exception table() {
//        try {
//            Document document = new Document();
//            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("C:\\Users\\davidz\\Desktop\\tagged.pdf"));
//            writer.setTagged();
//            document.open();
//            PdfStructureTreeRoot root = writer.getStructureTreeRoot();
//            root.mapRole(new PdfName("Everything"), new PdfName("Sect"));
//            PdfStructureElement eTop = new PdfStructureElement(root, new PdfName("Everything"));
//            PdfStructureElement table = new PdfStructureElement(eTop, new PdfName("Table"));
//            PdfStructureElement tr1 = new PdfStructureElement(table, new PdfName("TR"));
//            PdfStructureElement tr2 = new PdfStructureElement(table, new PdfName("TR"));
//            PdfStructureElement p = new PdfStructureElement(eTop, new PdfName("P"));
//            PdfStructureElement h1 = new PdfStructureElement(eTop, new PdfName("H1"));
//            PdfStructureElement h2 = new PdfStructureElement(eTop, new PdfName("H2"));
//            PdfContentByte cb = writer.getDirectContent();
//            BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, false);
//            cb.setFontAndSize(bf, 16);
//            
//            _addMarkedContent(cb, new PdfStructureElement(tr1, new PdfName("TD")), 20, 750, "The quick brown fox");
//            _addMarkedContent(cb, new PdfStructureElement(tr1, new PdfName("TD")), 200, 750, "jumped over the lazy brown dog.");
//            _addMarkedContent(cb, new PdfStructureElement(tr2, new PdfName("TD")), 20, 690, "Then he tripped and stumbled,");
//            _addMarkedContent(cb, new PdfStructureElement(tr2, new PdfName("TD")), 250, 690, "and never tried that trick again.");
//            
//            cb.setFontAndSize(bf, 12);
//            _addMarkedContent(cb, p, 450, 750, "This is on the side.");
//            
//            _addMarkedContent(cb, h1, 20, 500, "First Heading");
//            _addMarkedContent(cb, h2, 20, 450, "Second Heading");
//            
//            document.close();
//            writer.close();
//            
//            PdfReader reader = new PdfReader("C:\\Users\\davidz\\Desktop\\tagged.pdf");
//            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream("C:\\Users\\davidz\\Desktop\\tagged_withlang.pdf"));
//            PdfDictionary cat = reader.getCatalog();
//            cat.put(new PdfName("Lang"), new PdfName("en"));
//            
//            stamper.close();
//            reader.close();
//            
//            return null;
//        }
//        catch (Exception e) {
//            System.out.println(il.co.aman.apps.Misc.message(e));
//            return e;
//        }
//    }
//    
//    private static void _addMarkedContent(PdfContentByte cb, PdfStructureElement el, int x, int y, String text) {
//        cb.beginMarkedContentSequence(el);
//        cb.beginText();
//        cb.setTextMatrix(x, y);
//        cb.newlineShowText(text);
//        cb.endText();
//        cb.endMarkedContentSequence();
//    }
//
//    public static void main(String[] args) {
//    	System.out.println(TagTypes.fromString("H1"));
//    	System.out.println(TagTypes.fromString("h1"));
//    	System.out.println(TagTypes.fromString(null));
//    	System.out.println(TagTypes.header(3));
//    	//System.exit(0);
//        markedContent();
//        //fakeTable();
//        //table();
//    }
}
