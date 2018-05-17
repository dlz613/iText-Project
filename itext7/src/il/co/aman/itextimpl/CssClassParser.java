package il.co.aman.itextimpl;

import il.co.aman.apps.Misc;

import java.util.HashMap;

import com.itextpdf.kernel.font.PdfFont;

public class CssClassParser {

	HashMap<String, CssParser.Style> _classes;
	HashMap<String, PdfFont> _fonts;
	GetFonts _fontbank;

	public CssClassParser(GetFonts fontbank) {
		this._classes = new HashMap<String, CssParser.Style>();
		this._fonts = new HashMap<String, PdfFont>();
		this._fontbank = fontbank;
	}

	public void parse(String css) {
		String[] classes = CommentStripper.strip(css).split("}");
		for (String _class : classes) {
			String[] vals = _class.split("\\{");
			if (vals.length > 1) {
				CssParser.Style _style = CssParser.parse(this._fontbank, vals[1], this._fonts);
				for (String name : vals[0].split(",")) {
					this._classes.put(name.trim().replace(".",  ""), _style);
				}
			}
		}
	}
	
	public HashMap<String, CssParser.Style> getClasses() { 
		return this._classes;
	}
	
	public HashMap<String, PdfFont> fonts() {
		return this._fonts;
	}

	public static void main(String[] args) throws java.io.IOException {
		GetFonts fontBank = new GetFonts("C:\\Users\\davidz\\Desktop\\fonts");
		String css = Misc.readFile("X:\\Desktop\\yes.css");
		CssClassParser classparser = new CssClassParser(fontBank);
		classparser.parse(css);
		System.out.println(classparser.getClasses().get("y20000002"));
		//System.out.println(classparser.getClasses().get("y1649200086"));
		//System.out.println(classparser.getClasses().get("y1649200274"));
	}
}
