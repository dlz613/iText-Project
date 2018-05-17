package il.co.aman.formit;

import com.itextpdf.text.io.FileChannelRandomAccessSource;
import com.itextpdf.text.pdf.codec.TiffImage;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;
import com.itextpdf.text.Document;
import il.co.aman.apps.Misc;
import il.co.aman.formit.sendit.PdfResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

/**
 * Converts a TIFF image (including multipage) to a PDF.
 * @author davidz
 *
 */
public class Tiff2Pdf {
	
	private Exception _error;
	
	public Exception getError() {
		return this._error;
	}

	/**
	 * 
	 * @param img The full path of the TIFF image file.
	 * @param pdf The full path of the target PDF file.
	 * @return <code>null</code> in case of success.
	 */
	public static Exception convert(String img, String pdf) {
		try {
			RandomAccessFile aFile = new RandomAccessFile(img, "r");
			FileChannel inChannel = aFile.getChannel();
			FileChannelRandomAccessSource fcra = new FileChannelRandomAccessSource(inChannel);
			Document document = new Document();
			PdfWriter.getInstance(document, new FileOutputStream(pdf));
			document.open();
			RandomAccessFileOrArray rafa = new RandomAccessFileOrArray(fcra);
			int pages = TiffImage.getNumberOfPages(rafa);
			Image image;
			for (int i = 1; i <= pages; i++) {
				image = TiffImage.getTiffImage(rafa, i);
				Rectangle pageSize = new Rectangle(image.getWidth(), image.getHeight());
				document.setPageSize(pageSize);
				document.newPage();
				document.add(image);
			}
			document.close();
			aFile.close();
			return null;
		} catch (Exception e) {
			return e;
		}
	}

	/**
	 * 
	 * @param img
	 * @return In case of an error, the {@link Exception} with be in <code>getError()</code>.
	 */
	public PdfResult convert(byte[] img) {
		try {
			File imgFile = File.createTempFile("tiff2pdfimg", ".tif");
			String imgPath = imgFile.getAbsolutePath();
			Misc.writeBinFile(imgPath, img);
			File pdfFile = File.createTempFile("tiff2pdfpdf", ".pdf");
			String pdfPath = pdfFile.getAbsolutePath();
			this._error = convert(imgPath, pdfPath);
			if (this._error == null) {
				this._error = null;
				return new PdfResult(Misc.readBinFile(pdfPath));
			} else {
				return null;
			}
		} catch (Exception e) {
			this._error = e;
			return null;
		}
	}

	public static void main(String args[]) {
		String img = "C:\\Users\\davidz\\Desktop\\pdf2image\\Converted\\kalauto2_with page numbers_1.tif";
		String pdf = "C:\\Users\\davidz\\Desktop\\pdf2image\\kalauto2_with page numbers_2.pdf";
		Exception e = convert(img, pdf);
		if (e != null) {
			System.out.println(e);
		}
		Tiff2Pdf tiff2pdf = new Tiff2Pdf();
		PdfResult res = tiff2pdf.convert(Misc.readBinFile(img));
		if (res != null) {
			res.writeFile("C:\\Users\\davidz\\Desktop\\pdf2image\\kalauto2_with page numbers_2.pdfresult.pdf");
		} else {
			System.out.println(tiff2pdf.getError());
		}
	}
}
