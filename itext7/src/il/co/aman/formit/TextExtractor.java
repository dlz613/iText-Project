package il.co.aman.formit;

import il.co.aman.itextimpl.JavaItext;

import java.io.IOException;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.pdf.canvas.parser.filter.TextRegionEventFilter;
import com.itextpdf.kernel.pdf.canvas.parser.listener.FilteredTextEventListener;
import com.itextpdf.kernel.pdf.canvas.parser.listener.ITextExtractionStrategy;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;

public class TextExtractor {
	
	/**
	 * Extracts text from a specified location in a PDF.<br>The location and dimensions do <b>not</b> need to be precise - as long as part of the text
	 * is within the bounds the entire piece of text will be returned.<br>
	 * All of the coordinates and dimensions are in cm. 
	 * @param pdfDoc
	 * @param page
	 * @param left
	 * @param top
	 * @param width
	 * @param height
	 * @return <code>null</code> if any error occurred.
	 */
	public static String extractText(PdfDocument pdfDoc, int page, double left, double top, double width, double height) {
		float pageHeight = pdfDoc.getPage(page).getPageSize().getHeight();
		float newTop = pageHeight - JavaItext.sizeConvert(top) - JavaItext.sizeConvert(height);
		Rectangle rect = new Rectangle(JavaItext.sizeConvert(left), newTop, JavaItext.sizeConvert(width), JavaItext.sizeConvert(height));
		TextRegionEventFilter regionFilter = new TextRegionEventFilter(rect);
		ITextExtractionStrategy strategy = new FilteredTextEventListener(new LocationTextExtractionStrategy(), regionFilter);
		try {
			String str = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(page), strategy);
			return str;
		}
		catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}
	
	/**
	 * Checks if the text at the specified location is equal to <code>compareText</code>.<br>
	 * All coordinates and dimensions are in cm.
	 * @param pdfDoc
	 * @param compareText
	 * @param page
	 * @param left
	 * @param top
	 * @param width
	 * @param height
	 * @return <code>false</code> if any error occurred.
	 */
	public static boolean checkText(PdfDocument pdfDoc, String compareText, int page, double left, double top, double width, double height) {
		return compareText.equals(extractText(pdfDoc, page, left, top, width, height));
	}
	
	public static void main(String[] args) throws IOException {
		//System.out.println(il.co.aman.apps.Misc.getClassSource("com.itextpdf.kernel.pdf.canvas.parser.filter.TextRegionEventFilter"));
		//System.exit(0);
		String path = "C:\\Users\\davidz\\Desktop\\new example.pdf";
		PdfReader reader = new PdfReader(path);
		PdfDocument pdfDoc = new PdfDocument(reader);
		System.out.println("|" + extractText(pdfDoc, 1, 0f, 0f, 1f, 1f) + "|");
	}

}
