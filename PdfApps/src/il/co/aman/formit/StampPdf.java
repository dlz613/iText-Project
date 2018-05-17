package il.co.aman.formit;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import il.co.aman.formit.sendit.PdfResult;
import il.co.aman.formit.sendit.PdfSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class StampPdf {
	
	PdfReader _reader;
	ByteArrayOutputStream _bos;
	PdfStamper _stamper;
	Exception _error;
	
	public PdfResult outPdf() {
		return new PdfResult(this._bos);
	}
	
	public Exception getError() {
		return this._error;
	}
	
	public StampPdf(PdfSource src) throws DocumentException, IOException {
		this._reader = src.getNewReader();
		this._bos = new ByteArrayOutputStream();
		this._stamper = new PdfStamper(this._reader, this._bos);
	}
	
	public void close() throws DocumentException, IOException {
		this._stamper.close();
	}
	
	/**
	 * Adds an image to the PDF.
	 * @param page
	 * @param img
	 * @param x The x-coordinate of the image, in centimeters.
	 * @param y The y-coordinate of the image (with the top of the page being 0), in centimeters.
	 * @param width The width of the added image, in points. If < 0, uses the actual size of the image.
	 * @param height The height of the added image, in points. If < 0, uses the actual size of the image.
	 * @return <code>true</code> if successful. If not, the {@link Exception} will be in <code>getError()</code>.
	 */
	public boolean addImage(int page, Image img, float x, float y, float width, float height) {
		try {
			img.setAbsolutePosition(sizeConvert(x),  fixY(page, y, height <= 0? img.getHeight(): height));
			if (width > 0) {
				img.scaleAbsoluteWidth(width);
			}
			if (height > 0) {
				img.scaleAbsoluteHeight(height);
			}
			this._stamper.getOverContent(page).addImage(img);
			return true;
		}
		catch (Exception e) {
			this._error = e;
			return false;
		}	
	}
	
	/**
	 * Adds text to the PDF.
	 * @param page
	 * @param text
	 * @param x The x-coordinate of the text, in centimeters.
	 * @param y The y-coordinate of the image (with the top of the page being 0), in centimeters.
	 * @param font
	 * @param fontsize
	 * @return <code>true</code> if successful. If not, the {@link Exception} will be in <code>getError()</code>.
	 */
	public boolean addText(int page, String text, float x, float y, BaseFont font, float fontsize) {
		PdfContentByte overContent = this._stamper.getOverContent(page);
		overContent.saveState();
		overContent.beginText();
		overContent.setFontAndSize(font, fontsize);
		overContent.setTextMatrix(sizeConvert(x), fixY(page, y, fontsize));
		overContent.showText(text);
		overContent.endText();
		overContent.restoreState();	
		return true;
	}
	
	/**
	 * Adds an image to a PDF.
	 * @param src
	 * @param page
	 * @param img
	 * @param x The x-coordinate of the image, in centimeters.
	 * @param y The y-coordinate of the image (with the top of the page being 0), in centimeters.
	 * @param width The width of the added image, in points. If < 0, uses the actual size of the image.
	 * @param height The height of the added image, in points. If < 0, uses the actual size of the image.
	 * @return
	 * @throws DocumentException
	 * @throws IOException
	 */
	public static PdfResult addImage(PdfSource src, int page, Image img, float x, float y, float width, float height) throws DocumentException, IOException {
		StampPdf stamppdf = new StampPdf(src);
		stamppdf.addImage(page, img, x, y, width, height);
		stamppdf.close();
		return stamppdf.outPdf();
	}
	
	/**
	 * Adds text to the PDF.
	 * @param src
	 * @param page
	 * @param text
	 * @param x The x-coordinate of the text, in centimeters.
	 * @param y The y-coordinate of the image (with the top of the page being 0), in centimeters.
	 * @param font
	 * @param fontsize
	 * @return <code>true</code> if successful. If not, the {@link Exception} will be in <code>getError()</code>.
	 */
	public static PdfResult addText(PdfSource src, int page, String text, float x, float y, BaseFont font, float fontsize) throws DocumentException, IOException {
		StampPdf stamppdf = new StampPdf(src);
		stamppdf.addText(page, text, x, y, font, fontsize);
		stamppdf.close();
		return stamppdf.outPdf();
	}
	
	public float fixY(int page, float y, float objHeight) {
		float pageHeight = this._reader.getPageSize(page).getHeight();
		return fixY(pageHeight, y, objHeight);
	}
	
	public static float fixY(float pageHeight, float y, float objHeight) {
		return pageHeight - sizeConvert(y) - objHeight;
	}
	
	/**
	 * Converts units in <code>cm</code> to pixels.
	 * @param val
	 * @return
	 */
	public static float sizeConvert(float val) {
		return val * 28.35f; // the same as  / 2.54f * 72f
	}

	public static void main(String[] args) throws BadElementException, DocumentException, IOException {
		String PDF = "C:\\Users\\davidz\\Desktop\\kalauto2_with page numbers.pdf";
		String IMG1 = "C:\\xampp\\tomcat\\webapps\\formit\\saved_pictures\\1234_0.png";
		String IMG2 = "C:\\xampp\\tomcat\\webapps\\formit\\saved_pictures\\1234_1.png";
		String RES = "C:\\Users\\davidz\\Desktop\\kalauto with images.pdf";
		PdfSource src = new PdfSource(PDF);
		StampPdf stamppdf = new StampPdf(src);
		stamppdf.addImage(1, Image.getInstance(IMG1), 2, 5, sizeConvert(8), sizeConvert(4.5f));
		stamppdf.addImage(2, Image.getInstance(IMG2), 4, 25, sizeConvert(8), sizeConvert(4.5f)); // do NOT use the same instance of the Image more than once - its size may have been changed by the previous call!
		BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, false);
		stamppdf.addText(1, "hi there!", 11, 5, bf, 14f);
		stamppdf.close();
		stamppdf.outPdf().writeFile(RES);
		/*PdfResult outpdf = addImage(new PdfSource(PDF), 1, Image.getInstance(IMG), 2, 5, 0, 600);
		outpdf = addImage(new PdfSource(outpdf.getBytes()), 2, Image.getInstance(IMG), 4, 25, 120, 0);
		outpdf.writeFile("C:\\Users\\davidz\\Desktop\\kalauto with images_2.pdf");*/
	}

}
