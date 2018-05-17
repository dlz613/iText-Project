package il.co.aman.itextimpl;

import java.io.IOException;

import com.itextpdf.kernel.pdf.PdfDocument;
//import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfReader;

//import il.co.aman.formit.TextExtractor;

public class GetDocInfo {
	
//	public static DocIdentity getInfo(PdfDocument pdfDoc, int page, double left, double top, double width, double height) {
//		String text = TextExtractor.extractText(pdfDoc, page, left, top, width, height);
//		if (text != null && text.length() > 0) {
//			String[] texts = text.split(",");
//			if (texts.length == 3) {
//				return new DocIdentity(texts[0], texts[1], texts[2]);
//			} else {
//				return new DocIdentity("Wrong number of info elements in '" + text + "'.");
//			}
//		} else {
//			return new DocIdentity("Null or empty value returned by TextExtractor.");
//		}
//	}
//	
	public static DocIdentity getInfo(PdfDocument pdfDoc) {
		String text = pdfDoc.getDocumentInfo().getKeywords();
		if (text != null && text.length() > 0) {
			String[] texts = text.split(",");
			if (texts.length == 3) {
				return new DocIdentity(texts[0], texts[1], texts[2]);
			} else {
				return new DocIdentity("Wrong number of info elements in '" + text + "'.");
			}
		} else {
			return new DocIdentity("Null or empty value returned by getKeywords().");
		}
	}
	
	public static class DocIdentity {
		
		String _cust, _docno, _infopass;
		boolean _success;
		String _error;
		
		public String cust() {
			return this._cust;
		}
		
		public String docno() {
			return this._docno;
		}
		
		public String infopass() {
			return this._infopass;
		}
		
		public boolean success() {
			return this._success;
		}
		
		public String error() {
			return this._error;
		}
		
		public DocIdentity(String cust, String docno, String infopass) {
			this._cust = cust;
			this._docno = docno;
			this._infopass = infopass;
			this._success = true;
			this._error = "OK";
		}
		
		/**
		 * Indicates failure of fetching the document information
		 */
		public DocIdentity(String error) {
			this._success = false;
			this._error = error;
		}
	}
	
	public static void main(String[] args) throws IOException {
		//System.out.println(il.co.aman.apps.Misc.getClassSource("com.itextpdf.kernel.pdf.canvas.parser.filter.TextRegionEventFilter"));
		//System.exit(0);
		String path = "C:\\Users\\davidz\\Desktop\\new example.pdf";
		PdfReader reader = new PdfReader(path);
		PdfDocument pdfDoc = new PdfDocument(reader);
		DocIdentity info = getInfo(pdfDoc/*, 1, 0f, 0f, 9f, 0.5f*/);
		if (info.success()) {
			System.out.println(info.cust());
			System.out.println(info.docno());
			System.out.println(info.infopass());
		} else {
			System.out.println(info.error());
		}
	}

}
