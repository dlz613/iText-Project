package il.co.aman.itextimpl;

import il.co.aman.apps.Misc;
import il.co.aman.formit.TextExtractor;
import il.co.aman.itextimpl.ArchiveDataParser.ArchivePage;

import java.io.File;
import java.io.FileOutputStream;

public class ArchiveLittleNotStatic {

	public static void main(String[] args) throws java.io.IOException {
		String pdfpath = "\\\\31.154.65.144\\FormIT\\apache-tomcat-6.0.35\\webapps\\formit\\daviddev\\new example.pdf"; // new example
		String outputtype = "file";
		String dir = "rtl";
		JavaItext javaitext = new JavaItext();
		javaitext.svgFactor = 2;
		javaitext.init("\\\\formit7\\e$\\formit\\ConvertIT\\Resources\\Fonts", true);
		String css = Misc.readFile("X:\\Desktop\\new example.css"); // new example
		CssClassParser classparser = new CssClassParser(javaitext.fontBank);
		classparser.parse(css);
		//System.out.println(classparser.getClasses().get("y5436053"));
		String mdXml = Misc.readFile("X:\\Desktop\\additional_metadata.xml", "UTF-8");
		java.util.HashMap<String, AdditionalMetadata.Metadata> metadata = AdditionalMetadata.parseMetadata(mdXml);
		String archivedata = Misc.readFile("X:\\Desktop\\new example.xml", "UTF-8"); // new example
		ArchiveDataParser.ArchiveResult archive = ArchiveDataParser.getDataSax(archivedata, classparser, metadata, dir);
		java.util.ArrayList<ArchivePage> pages = archive.pages();
		javaitext.docInfo = archive.docInfo();
		javaitext.hiddenText = archive.hiddenText();

		if (javaitext.newDoc(pdfpath, null, "no", "CM", outputtype, "", "yes", "he-IL", 
				"Document created using classes and archive data.", classparser.fonts(), dir).errorCode().equals("0")) {
			boolean succeeded = true;
			IndexParser.IndexData index = IndexParser.getData(Misc.readFile("X:\\Desktop\\indexXML.xml"), javaitext.root);

			JavaItext.ItextStatus res = javaitext.processPages("http://formit7:6005/formit/Resources/", classparser, metadata, pages, "ORIG", index, "");
	
			if (res == JavaItext.ItextStatus.ERROR) {
				for (String error: javaitext.error_msgs) {
					System.out.println(error);
				}		
			} else if (res == JavaItext.ItextStatus.FAILED) {
				System.out.println("Document production failed.");
				succeeded = false;
			}
			
			if (succeeded) {
				javaitext.endDoc(pdfpath, null, "no");
				if (outputtype.equals("stream")) {
					FileOutputStream fos = new FileOutputStream(pdfpath);
					javaitext.streamOut.writeTo(fos);
					fos.close();
				}
				java.awt.Desktop.getDesktop().open(new File(pdfpath));
				//GetDocInfo.main(null);
			}
		} else {
			System.out.println("Error creating file - aborting.");
		}
		System.exit(0);
	}	
}
