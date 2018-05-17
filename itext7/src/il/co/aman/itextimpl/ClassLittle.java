package il.co.aman.itextimpl;

import il.co.aman.apps.Misc;

public class ClassLittle {
	
	public static void main(String[] args) throws java.io.IOException {
		String pdfpath = "C:\\Users\\davidz\\Desktop\\classkalauto.pdf";
		JavaItextFullCode.init("C:\\Users\\davidz\\Desktop\\Fonts", "");
		String css = Misc.readFile("C:\\Users\\davidz\\Desktop\\classlittle.css");
		CssClassParser classparser = new CssClassParser(JavaItextFullCode.fontBank);
		classparser.parse(css);
		//System.out.println(classparser._classes.get("y1"));
		ItextResult res = JavaItextFullCode.newDoc(pdfpath, null, "no", 
				"CM", 29.7, 21.0, "file", "", "yes", "he-il", "Document created using classes.");
		res = JavaItextFullCode.newPage(null, 29.7, 21.0);
		res = JavaItextFullCode.textContent(classparser._classes, "y1", "text", -1, 0, "The 12 quick brown foxes jump over", new TableParams(), 
				0, (double)JavaItextFullCode.sizeConvert(1.0), getTop(842, 5.0, 100), "200,220,240", 1, null, "link2goodbye", null, null, "", "Hello", 0, "no", "no", "no", 0, null, "N", null);
		res = JavaItextFullCode.textContent(classparser._classes, "y1", "text", -1, 0, " the lazy dog.", new TableParams(), 
				0, (double)JavaItextFullCode.sizeConvert(8.0), getTop(842, 5.0, 100), "200,220,240", 1, null, null, null, null, "", "Hello", 0, "no", "no", "no", 0, null, "N", null);
		
		JavaItextFullCode.endDoc(pdfpath, null, "no");
		java.awt.Desktop.getDesktop().open(new java.io.File(pdfpath));
	}

	public static double getTop(int pageHeight, double top, int cellHeight) {
		return pageHeight - JavaItextFullCode.sizeConvert(top) - cellHeight;
	}

}
