package il.co.aman.itextimpl;

import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.layout.splitting.ISplitCharacters;

/**
 *
 * @author davidz
 */
public class SplitOnAll implements ISplitCharacters {

	@Override
	public boolean isSplitCharacter(GlyphLine text, int glyphPos) {
		int c = (int)text.get(glyphPos).getUnicode();
		return ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || (c >= '\u05d0' && c <= '\u05ea')); // aleph to tav
	}
}
