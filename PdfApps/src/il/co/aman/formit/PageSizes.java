package il.co.aman.formit;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.Rectangle;
import il.co.aman.formit.sendit.PdfSource;
import java.util.ArrayList;
import java.lang.StringBuilder;

/**
 * Returns the size of each page in a PDF.
 * @author davidz
 *
 */
public class PageSizes {
	
	public static ArrayList<PageSize> getSizes(PdfSource src) {
		ArrayList<PageSize> res = new ArrayList<PageSize>();
		
		PdfReader reader = src.getNewReader();
		for (int i = 1; i <= reader.getNumberOfPages(); i++) {
			Rectangle rect = reader.getPageSize(i);
			res.add(new PageSize(i, rect.getWidth(), rect.getHeight()));
		}
		return res;
	}
	
	public static String toCsv(ArrayList<PageSize> sizes) {
		StringBuilder res = new StringBuilder();
		
		for (PageSize size: sizes) {
			res.append(Integer.toString(size.pageNum())).append(",")
				.append(Float.toString(size.width())).append(",")
				.append(Float.toString(size.height())).append("\n");
		}
		return res.toString();
	}
	
	public static String toXml(ArrayList<PageSize> sizes) {
		StringBuilder res = new StringBuilder();
		res.append("<pages>");
		
		for (PageSize size: sizes) {
			res.append("<page num='").append(Integer.toString(size.pageNum()))
				.append("' width='").append(Float.toString(size.width()))
				.append("' height='").append(Float.toString(size.height())).append("'/>");
		}
		res.append("</pages>");
		return res.toString();
	}
	
	public static String toString(ArrayList<PageSize> sizes) {//{p1-19.70-25.80}
		StringBuilder res = new StringBuilder();
		
		for (PageSize size: sizes) {
			res.append("{p").append(Integer.toString(size.pageNum())).append("-")
				.append(Float.toString(size.width())).append("-")
				.append(Float.toString(size.height())).append("}");
		}
		return res.toString();
	}
	
	public static class PageSize {
		int _pageNum;
		float _width, _height;
		
		public int pageNum() {
			return this._pageNum;
		}
		
		/**
		 * 
		 * @return the width in cm
		 */
		public float width() {
			return this._width;
		}
		
		/**
		 * 
		 * @return the height in cm
		 */
		public float height() {
			return this._height;
		}
				
		/**
		 * 
		 * @param pageNum
		 * @param width the width in points (1/72 of an inch)
		 * @param height the height in points (1/72 of an inch)
		 */
		public PageSize(int pageNum, float width, float height) {
			this._pageNum = pageNum;
			this._width = width / 28.35f;
			this._height = height / 28.35f;
		}
	}

	public static void main(String[] args) throws java.io.IOException {
		PdfSource src = new PdfSource("C:\\Users\\davidz\\Desktop\\kalauto2_with page numbers.pdf");
		ArrayList<PageSize> sizes = getSizes(src);
		System.out.println(toCsv(sizes));
		System.out.println(toXml(sizes));
		System.out.println(toString(sizes));
	}
}
