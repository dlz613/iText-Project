package il.co.aman.itextimpl;

/**
 * @(#)GetFonts.java
 *
 *
 * @author David Zalkin, Aman Computers Group
 * @version 1.00 2009/6/3
 */
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;

import java.io.IOException;

public class GetFonts {

    String[] fonts14 = {"Courier", "Courier-Bold", "Courier-BoldOblique", "Courier-Oblique",
        "Helvetica", "Helvetica-Bold", "Helvetica-BoldOblique", "Helvetica-Oblique", "Times-Roman",
        "Times-Bold", "Times-BoldItalic", "Times-Italic", "Symbol", "ZapfDingbats"};

    public GetFonts(String otherDirs) {
    	//System.out.println(PdfFontFactory.registerSystemDirectories());
        if (otherDirs != null) {
        	PdfFontFactory.registerDirectory(otherDirs);
        }
    }
    
    public PdfFont getFont(String fontName) {
    	return getFont(fontName, false);
    }

    public PdfFont getFont(String fontName, boolean embed) {
    	PdfFont res;
        try {
            if (_checkArray(fonts14, fontName)) {
            	res = PdfFontFactory.createFont(fontName, "Cp1255", false); // the 14 fonts cannot be embedded (encoding was Cp1252)
            } else {
            	res = PdfFontFactory.createRegisteredFont(fontName, PdfEncodings.IDENTITY_H, embed);
            }
            return res;
        } catch (IOException e) {
            return null;
        }
    }

    /*public Font getFont(String fontName) {
        return (new Font(getBaseFont(fontName)));
    }*/

    private boolean _checkArray(String[] arr, String val) {
        for (Integer i = 0; i < arr.length; i++) {
            if (arr[i].equals(val)) {
                return (true);
            }
        }
        return (false);
    }
}