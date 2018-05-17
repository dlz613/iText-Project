package il.co.aman.itextimpl;

import il.co.aman.apps.Misc;
import il.co.aman.itextimpl.ArchiveDataParser.ArchiveField;
import il.co.aman.itextimpl.ArchiveDataParser.ArchivePage;

public class ArchiveLittle {

	public static void main(String[] args) throws java.io.IOException {
		String pdfpath = "C:\\Users\\davidz\\Desktop\\sample.pdf";
		JavaItextFullCode.init("C:\\Users\\davidz\\Desktop\\Fonts", "");
		String css = Misc.readFile("C:\\Users\\davidz\\Desktop\\metadata.css");
		CssClassParser classparser = new CssClassParser(JavaItextFullCode.fontBank);
		classparser.parse(css);
		String mdXml = Misc.readFile("C:\\Users\\davidz\\Desktop\\additional_metadata.xml");
		java.util.HashMap<String, AdditionalMetadata.Metadata> metadata = AdditionalMetadata.parseMetadata(mdXml);
		String archivedata = Misc.readFile("C:\\Users\\davidz\\Desktop\\littlearchive.xml");
		java.util.ArrayList<ArchivePage> pages = ArchiveDataParser.getData(archivedata);
		
		ItextResult res = JavaItextFullCode.newDoc(pdfpath, null, "no", 
				"CM", 29.7, 21.0, "file", "", "yes", "he-IL", "Document created using classes and archive data.");
		
		for (int i = 0; i < pages.size(); i++) {
			ArchivePage page = pages.get(i);
			CssParser.Style pageStyle = classparser.getClasses().get("y" + page.mdid());
			res = JavaItextFullCode.newPage(null, (double)sizeReConvert(pageStyle.height()), (double)sizeReConvert(pageStyle.width()));
			
			for (ArchiveField field: page.fields()) {
				if (field.contentType().equals("text")) {
					res = JavaItextFullCode.textContent(classparser._classes, "y" + field.mdid(), field.contentType(), -1, 0, field.value(), new TableParams(), 
							0, (double)JavaItextFullCode.sizeConvert(field.left()), 
							ClassLittle.getTop((int)pageStyle.height(), field.top(), (int)(classparser.getClasses().get("y" + field.mdid()).height())), 
							null, 1, null, null, null, null, "", "Hello", 0, "no", "no", "no", 0, null, "N", null);
				} else if(field.contentType().equals("image")) {
					CssParser.Style imageStyle = classparser.getClasses().get("y" + field.mdid());
					res = JavaItextFullCode.imageContent("image", "C:\\Users\\davidz\\Desktop\\", field.value(), field.left(), field.top(), imageStyle.height(), 
							imageStyle.width(), null, null, imageStyle.getBorder(), "hi there!");
//					res = JavaItextFullCode.imageContent("image", "C:\\Users\\davidz\\Desktop\\images\\", "fox.jpg", 3, 0, 3, 
//							3, null, null, BorderParams.none(), "Fox");
//					res = JavaItextFullCode.imageContent("image", "C:\\Users\\davidz\\Desktop\\images\\", "dog.jpg", 10, 0, 3, 
//							3, null, null, BorderParams.none(), "Dog");
				} else if (field.contentType().equals("barcode")) {
					CssParser.Style barcodeStyle = classparser.getClasses().get("y" + field.mdid());
					AdditionalMetadata.Metadata md = metadata.get(field.mdid());
					if (md != null) {
						res = JavaItextFullCode.barcodeContent((double)field.left(), (double)field.top(), barcodeStyle,  
									md, field.value(), "", "Arial"/*barcodeStyle.fontFamily()*/);
					}
				}
			}
		}
		
		JavaItextFullCode.endDoc(pdfpath, null, "no");
		java.awt.Desktop.getDesktop().open(new java.io.File(pdfpath));
	}
	
	public static float sizeReConvert(Float size) {
		if (size == null) {
			return 0f;
		} else if (size > 0) {
			return size * 2.54f / 72f;
		} else {
			return 0f;
		}
	}


}
