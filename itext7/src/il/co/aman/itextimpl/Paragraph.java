package il.co.aman.itextimpl;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.layout.Document;
//import com.itextpdf.text.Element;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
//import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Text;

import il.co.aman.apps.Misc;
import il.co.aman.apps.String2Color;
import il.co.aman.apps.XsltTransform;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.transform.stream.StreamSource;

/**
 * Enables adding a paragraph into a PDF - the paragraph will flow into the
 * assigned area. It may be composed of distinct phrases and lists (ordered or
 * unordered), and each one has its own font. Internally makes use of the
 * {@link com.itextpdf.layout.element.Paragraph} class.
 *
 * @author davidz
 */
public class Paragraph {

    com.itextpdf.layout.element.Paragraph _paragraph;
    int _dir;
    float _firstLineIndent = 0f;
    float _lineHeight = 1.2f;

    public com.itextpdf.layout.element.Paragraph getParagraph() {
        return this._paragraph;
    }

    public float getLineHeight() {
        return this._lineHeight;
    }

    public void setLineHeight(float val) {
        this._lineHeight = val;
    }

    public int getDir() {
        return this._dir;
    }

    /**
     *
     * @param dir <code>{@link PdfWriter}.RUN_DIRECTION_*</code>
     */
    public Paragraph(int dir) {
        _init(dir);
        //PdfFontFactory.registerSystemDirectories();
    }

    /**
     *
     * @param dir <code>{@link PdfWriter}.RUN_DIRECTION_*</code>
     * @param font_dir The directory containing the fonts to be used; if
     * <code>null</code> or blank, uses the default system font directory.
     */
    public Paragraph(int dir, String font_dir) {
        _init(dir);
        if (Misc.notNullOrEmpty(font_dir)) {
        	PdfFontFactory.registerDirectory(font_dir);
        } else {
        	//PdfFontFactory.registerSystemDirectories();
        }
    }

    private void _init(int dir) {
        this._paragraph = new com.itextpdf.layout.element.Paragraph();
        this._dir = dir;
    }

    /**
     * @deprecated not yet implemented
     * @param val
     */
    public void setFirstLineIndent(float val) {
        this._firstLineIndent = val;
    }

    public void addText(String text, PdfFont font) {
        addText(text, font, null);
    }

    /**
     * Adds a piece of text to the paragraph. If the text contains
     * <code>&lt;br/&gt;</code>, it is replaced by a newline character.
     *
     * @param text
     * @param font
     * @param background The background color, in String form (any of the
     * formats which are accepted by the {@link String2Color}
     * <code>fromString()</code> method. May be <code>null</code>.
     */
    public void addText(String text, PdfFont font, String background) {
        //Text phrase = new Text("");
        //phrase.setFont(font);
        Text c = new Text(text.replace("<br/>", "\n").replace("<br />", "\n"));
        if (String2Color.fromString(background) != null) {
            c.setBackgroundColor(color2Color(String2Color.fromString(background)));
        }
        c.setFont(font);
        //phrase.add(c);
        addPhrase(c);
    }
	
	public static com.itextpdf.kernel.color.Color color2Color(java.awt.Color color) {
		if (color == null) {
			return null;
		} else {
			return new com.itextpdf.kernel.color.DeviceRgb(color.getRed(), color.getGreen(), color.getBlue());
		}
	}

    public void addPhrase(Text/*com.itextpdf.layout.element.Paragraph*/ phrase) {
        this._paragraph.add(phrase);
    }

    public void addPhrase(com.itextpdf.layout.element.Paragraph phrase) {
        this._paragraph.add(phrase);
    }

    /**
     * Adds an ordered list to the paragraph. May also be used for an unordered
     * list, as long as the selected font supports the bullet characters used in
     * the list.
     *
     * @param list
     * @param font
     */
    public void addList(FakeList list, PdfFont font) {
        addText(list.getString(), font, null);
    }

    /**
     * Adds an unordered list to the paragraph.<br><b>Note:</b>
     *
     * @param list
     * @param textFont The default text font for the list.
     * @param bulletFont A font which supports the bullet characters (the
     * standard PDF fonts, for example, do not support the default bullets). If
     * <code>textFont</code> <b>does</b> support the bullets, this can be
     * <code>null</code>.
     */
    public void addList(FakeList list, PdfFont textFont, PdfFont bulletFont) {
        ArrayList<FakeList.TextPart> parts = new ArrayList<FakeList.TextPart>();
        list.getParts(parts, false);
        com.itextpdf.layout.element.Paragraph phrase = FakeList.parts2Phrase(parts, textFont, bulletFont);
        addPhrase(phrase);
    }

    /**
     * Adds the paragraph to the document without any borders.
     *
     * @param cb The {@link PdfContentByte} of the {@link PdfWriter} whence the
     * paragraph is to be added.
     * @param height The maximum height (in points) the paragraph is allowed to
     * take up - if it takes more, it is reduced in size until it fits.
     * @param spacing Line spacing in proportion to the font-size. 1 = minimum
     * spacing.
     * @param indent Indentation - if non-zero, the indentation is applied for
     * each new line.
     * @param right_indent
     * @param alignment <code>{@link Element}.ALIGN_*</code>
     * @param llx The coordinates whence the paragraph will be inserted into the
     * document.
     * @param lly
     * @param urx
     * @param ury
     * @return <code>true</code> if successful
     * @throws DocumentException
     */
//    public boolean addToDocument(PdfContentByte cb, int height, float spacing, float indent, float right_indent, int alignment,
//            float llx, float lly, float urx, float ury) throws DocumentException {
//        return addToDocument(cb, height, spacing, indent, right_indent, alignment, llx, lly, urx, ury,
//                Border.none(), Border.none(), Border.none(), Border.none());
//    }

    /**
     * Adds the paragraph to the document.
     *
     * @param cb The {@link PdfContentByte} of the {@link PdfWriter} whence the
     * paragraph is to be added.
     * @param height The maximum height (in points) the paragraph is allowed to
     * take up - if it takes more, it is reduced in size until it fits.
     * @param spacing Line spacing in proportion to the font-size. 1 = minimum
     * spacing.
     * @param indent Indentation - if non-zero, the indentation is applied for
     * each new line.
     * @param right_indent
     * @param alignment <code>{@link Element}.ALIGN_*</code>
     * @param llx The coordinates whence the paragraph will be inserted into the
     * document.
     * @param lly
     * @param urx
     * @param ury
     * @param borderLeft The left border (if none, use
     * {@link Border}<code>.none()</code>)
     * @param borderTop The top border (if none, use
     * {@link Border}<code>.none()</code>)
     * @param borderRight The right border (if none, use
     * {@link Border}<code>.none()</code>)
     * @param borderBottom The bottom border (if none, use
     * {@link Border}<code>.none()</code>)
     * @return <code>true</code> if successful
     * @throws DocumentException
     */
    public boolean addToDocument(Document doc, int pageNum, int height, float spacing, float indent, float right_indent, int alignment,
            float llx, float lly, float urx, float ury, Border borderLeft, Border borderTop, Border borderRight, Border borderBottom) {
    	this.getParagraph().setFixedPosition(llx/* + cssStyle.marginLeft()*/ /*- cssStyle.marginRight()*/, ury/* - cssStyle.fontSize() -*//* marginTop - vert * ix*/, 
				urx - llx/* - widthCorr*/);
    	this.getParagraph().setPageNumber(pageNum);
		doc.add(this.getParagraph());						

        boolean res = cutParagraph(ct, height, this.getParagraph());
        Border.setBorders(cb, llx, lly - height/*ury*/, urx, lly, borderLeft, borderTop, borderRight, borderBottom);
        return res;
    }

    /**
     * Checks if the {@link com.itextpdf.text.Paragraph} will fit into a
     * specified amount of space. If not, it is cut down until it will fit.<br>
     * Once it fits, it is inserted into the document.
     *
     * @param ct The {@link ColumnText} object which will hold the paragraph.
     * @param heightAvailable In points
     * @param content
     * @return
     * @throws DocumentException
     */
    private static boolean cutParagraph(float heightAvailable, com.itextpdf.text.Paragraph content) throws DocumentException {
        ct.addText(content);
        //System.out.println("cutParagraph: " + Float.toString(paragraphSize(ct, content)));
        if (paragraphSize(ct, content) <= heightAvailable) {
            //System.out.println(content.getFirstLineIndent());
            //System.out.println(paragraphSize(ct, content));
            ct.go();
            return true;
        } else {
            ArrayList<Chunk> chunks = getChunks(content);
            for (int i = chunks.size() - 1; i >= 0; i--) { // remove Chunks until it fits
                com.itextpdf.text.Paragraph par = shorterParagraph(chunks, i);
                float y = ct.getYLine();
                ct.go(true); // remove the current content from the ColumnText
                replaceText(ct, par, y); // and put it right back (this IS necessary)
                float size = paragraphSize(ct, par);
                if (heightAvailable > size) { // put back as much of the last excluded Chunk as possible
                    Chunk lastChunk = (Chunk) chunks.get(i);
                    String text = lastChunk.getContent();
                    String shorter = text;
                    while (shorter.length() > 0 && shorter.contains(" ")) { // remove words from the end of the String until the Paragraph fits
                        shorter = shorter.substring(0, shorter.lastIndexOf(" "));
                        Chunk newchunk = new Chunk(shorter, lastChunk.getFont());
                        chunks.set(i, newchunk);
                        par = shorterParagraph(chunks, i + 1);
                        ct.go(true);
                        replaceText(ct, par, y);
                        size = paragraphSize(ct, par);
                        if (heightAvailable >= size) {
                            ct.go();
                            return true;
                        }
                    }
                    if (!shorter.contains(" ")) {
                        par = shorterParagraph(chunks, i);
                        ct.go(true);
                        replaceText(ct, par, y);
                        ct.go();
                        return true;
                    } else {
                        return false;
                    }
                } else if (heightAvailable == size) {
                    //System.out.println(paragraphSize(ct, content));
                    ct.go();
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Returns a {@link com.lowagie.text.Paragraph} consisting of the first
     * <code>num</code> {@link Chunk} objects in <code>chunks</code>.
     *
     * @param chunks
     * @param num
     * @return
     */
    public static com.itextpdf.layout.element.Paragraph shorterParagraph(ArrayList chunks, int num) {
    	com.itextpdf.layout.element.Paragraph res = new com.itextpdf.layout.element.Paragraph();
        for (int i = 0; i < num; i++) {
            res.add((Text) chunks.get(i));
        }
        return res;
    }

    /**
     * Calculates the height of <code>content</code> in <code>ct</code>.
     *
     * @param ct
     * @param content
     * @return
     * @throws DocumentException
     */
//    public static float paragraphSize(ColumnText ct, com.itextpdf.layout.element.Paragraph content) throws /*DocumentException*/ {
//        float y = ct.getYLine();
//        ct.go(true);
//        float res = y - replaceText(ct, content, y);
//        return res;
//    }

    /**
     *
     * @param content
     * @param height
     * @param width
     * @param spacing Line spacing in proportion to the font-size. 1 = minimum
     * spacing.
     * @param indent
     * @param right_indent
     * @return
     * @throws DocumentException
     */
//    public static float paragraphSize(com.itextpdf.layout.element.Paragraph content, float height, float width, float spacing, float indent, float right_indent) throws DocumentException {
////        ColumnText ct = new ColumnText(new PdfContentByte(PdfWriter.getInstance(new Document(), new java.io.ByteArrayOutputStream())));
////        ct.setSimpleColumn(0f, 0f, width, height);
////        ct.setLeading(0, spacing);
////        ct.setIndent(indent);
////        ct.setRightIndent(right_indent);
////        ct.addText(content);
//        return paragraphSize(ct, content);
//    }

    /**
     * Calculates the height the given HTML will take up. Uses the
     * {@link Xml2Phrases} class to convert the HTML to a {@link Paragraph}.
     *
     * @param html
     * @param font_dir The folder containing the fonts to be used.
     * @param height
     * @param width
     * @param indent
     * @param right_indent
     * @return 0 if an error occurred in parsing the HTML, -1 if an error
     * occurred in the XSLT transformation.
     * @throws DocumentException
     */
    public static float htmlParagraphSize(String html, String font_dir, float height, float width, float indent, float right_indent) /*throws DocumentException*/ {
//        Paragraph para = new Paragraph(PdfWriter.RUN_DIRECTION_LTR, font_dir);
//        String sXml = _html2Xml(para, html);
//        if (sXml != null) {
//            System.out.println(sXml);
//            Object res = _xml2para(para, sXml);
//            if (res != null) {
//                System.out.println(para._paragraph.size());
//                float size = paragraphSize(para.getParagraph(), height, width, para.getLineHeight(), indent, right_indent);
//                return size;
//            } else {
//                return 0;
//            }
//        } else {
//            return -1;
//        }
    	return 1f;
    }

    /**
     * Calculates the height the given XML will take up. Uses the
     * {@link Xml2Phrases} class to convert the XML to a {@link Paragraph}.
     *
     * @param xml
     * @param font_dir
     * @param width
     * @param indent
     * @param right_indent
     * @return 0 if an error occurred in parsing the HTML, -1 if
     * <code>xml</code> is <code>null</code>.
     * @throws DocumentException
     */
    public static float paragraphSize(String xml, String font_dir, float width, float indent, float right_indent) /*throws DocumentException*/ {
//        Paragraph para = new Paragraph(PdfWriter.RUN_DIRECTION_LTR, font_dir);
//        if (xml != null) {
//            Object res = _xml2para(para, xml);
//            if (res != null) {
//                //System.out.println("*** Generated XML:");
//                //System.out.println(para2xml(para));
//                float size = paragraphSize(para.getParagraph(), 20000f, width, para.getLineHeight(), indent, right_indent);
//                return size;
//            } else {
//                return 0;
//            }
//        } else {
//            return -1;
//        }
    	return 1f;
    }

    /**
     * Calculates the height the given XML will take up. Uses the
     * {@link Xml2Phrases} class to convert the XML to a {@link Paragraph}. Uses
     * the default font directory.
     *
     * @param xml
     * @param width
     * @param indent
     * @param right_indent
     * @return 0 if an error occurred in parsing the HTML, -1 if
     * <code>xml</code> is <code>null</code>.
     * @throws DocumentException
     */
    public static float paragraphSize(String xml, float width, float indent, float right_indent) /*throws DocumentException*/ {
        return paragraphSize(xml, null, width, indent, right_indent);
    }

    @Override
    public String toString() {
//        StringBuilder res = new StringBuilder();
//        Integer i = 0;
//        for (Object c : this._paragraph.getChunks()) {
//            res.append((i++).toString()).append(": ").append(((Chunk) c).getContent()).append("\n");
//        }
//        return res.toString();
    	return null;
    }

    /**
     * Calculates the height the given HTML will take up. Uses the
     * {@link Xml2Phrases} class to convert the HTML to a {@link Paragraph}.
     * Uses the default system font directory.
     *
     * @param html
     * @param height
     * @param width
     * @param indent
     * @param right_indent
     * @return 0 if an error occurred in parsing the HTML, -1 if an error
     * occurred in the XSLT transformation.
     * @throws DocumentException
     */
    public static float htmlParagraphSize(String html, float height, float width, float indent, float right_indent) /*throws DocumentException*/ {
//        return htmlParagraphSize(html, null, height, width, indent, right_indent);
    	return 1f;
    }

    /**
     * Calculates the height the given HTML will take up. Uses the
     * {@link Xml2Phrases} class to convert the HTML to a {@link Paragraph}.
     * Uses the default system font directory.
     *
     * @param html
     * @param width
     * @param indent
     * @param right_indent
     * @return 0 if an error occurred in parsing the HTML, -1 if an error
     * occurred in the XSLT transformation.
     * @throws DocumentException
     */
    public static float htmlParagraphSize(String html, float width, float indent, float right_indent) /*throws DocumentException*/ {
        return htmlParagraphSize(html, 20000f, width, indent, right_indent);
    }

    // Private methods
    private static ArrayList<Text> getChunks(com.itextpdf.layout.element.Paragraph content) {
        ArrayList<Text> res = new ArrayList<Text>();
//        for (Object c : content.getChunks()) {
//            res.add((Chunk) c);
//        }
        return res;
    }

    private static float replaceText(/*ColumnText ct, */com.itextpdf.layout.element.Paragraph par, float y) {
//        float res = ct.getYLine();
//        ct.addText(par);
//        ct.setYLine(y);
//        return res;
    	return 1f;
    }

    private static boolean makePdf(String path, Paragraph para, int width, int height) {
//        try {
//            Document doc = new Document(new Rectangle(0, 0, 739, 1134));
//            PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(new File(path)));
//            doc.open();
//            PdfContentByte cb = writer.getDirectContent();
//            if (para.addToDocument(cb, height, para.getLineHeight(), 0f, 0f, Element.ALIGN_LEFT, 20, 1000, 20 + width, 1000 - height)) {
//                doc.close();
//                writer.close();
//                return true;
//            } else {
//                System.out.println("Failed.");
//                return false;
//            }
//        } catch (DocumentException e) {
//            System.out.println(e.getMessage());
//            return false;
//        } catch (FileNotFoundException e) {
//            System.out.println(e.getMessage());
//            return false;
//        }
    	return false;
    }
    
    /**
     * Reverse-engineers the {@link Paragraph} to XML.
     * @param para
     * @return 
     */
    public static String para2xml(Paragraph para) {
//        il.co.aman.apps.ColorConvert converter = new il.co.aman.apps.ColorConvert();
//        StringBuilder res = new StringBuilder();
//        Integer id = 0;
//        res.append("<div Id='").append((++id).toString()).append("' line-height='")
//                .append(Integer.toString(Math.round(para.getLineHeight() * 100))).append("%'>");
//        for (Chunk c: para.getParagraph().getChunks()) {
//            res.append("<span Id='").append((++id).toString()).append("' font-size='")
//                    .append(Float.toString(c.getFont().getSize())).append("pt' font-family='")
//                    .append(c.getFont().getFamilyname()).append("' color='")
//                    .append(converter.convert2Css(color2Rgb(c.getFont().getColor()))).append("'>");
//            res.append(_content(c.getContent())/*.trim().length() == 0? "<br/>": c.getContent()*/);
//            res.append("</span>");
//        }
//        res.append("</div>");
//        return res.toString();
    	return null;
    }
    
    private static String _content(String content) {
        return content.replaceAll("\n", "<br/>");
    }
    
    private static int[] color2Rgb(Color color) {
        int[] res =  new int[3];
        if (color != null) {
        	float[] rgb = color.getColorValue();
        	res[0] = (int)(rgb[0] * 255);
        	res[1] = (int)(rgb[1] * 255);
        	res[2] = (int)(rgb[2] * 255);
        }
        return res;
    }
    
    private static Paragraph _xml2para(Paragraph para, String xml) {
//        Object[] getPhrases = Xml2Phrases.parse(xml);
//        if (getPhrases[1] == null) {
//            Xml2Phrases xml2phrases = (Xml2Phrases) getPhrases[0];
//            para.setLineHeight(xml2phrases.getLineHeight());
//            //System.out.println(xml2phrases.getPhrases().size());
//            for (Phrase phrase : xml2phrases.getPhrases()) {
//                //System.out.println("'" + phrase.getContent() + "'");
//                para.addPhrase(phrase);
//            }
//            //System.out.println("\"" + para.toString() + "\"");
//            return para;
//        } else {
//        	System.out.println((Exception)getPhrases[1]);
//            return null;
//        }
    	return null;
    }

    private static String _html2Xml(String html) {
        return _html2Xml(new Paragraph(0), html);
    }

    private static String _html2Xml(Paragraph para, String html) {
        String sXslt = Misc.getResource(para.getClass(), "collapseSpans.xsl");
        if (sXslt != null) {
            XsltTransform xslt = new XsltTransform(new StreamSource(new StringReader(sXslt)));
            return xslt.transform(new StreamSource(new StringReader(html)));
        } else {
            return null;
        }
    }

    public static void main(String[] args) /*throws IOException, DocumentException*/ {
//        Paragraph par1 = new Paragraph(PdfWriter.RUN_DIRECTION_LTR);
//        //Paragraph._xml2para(par1, Misc.readFile("C:\\Users\\davidz\\Desktop\\XML\\collapse.xml"));
//        //System.out.println(para2xml(par1));
////        System.exit(0);
//        FontFactory.registerDirectories();
//        Document doc = new Document(new Rectangle(0, 0, 739, 1134));
//        PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(new File("C:\\Users\\davidz\\Desktop\\paragraph.pdf")));
//        BaseFont bf = FontFactory.getFont("Courier New", BaseFont.IDENTITY_H, BaseFont.EMBEDDED).getBaseFont();
//        doc.open();
//        doc.newPage();
//        PdfContentByte cb = writer.getDirectContent();
//        Paragraph par = _xml2para(par1, Misc.getResource(par1.getClass(), "sampleParagraph.xml"));//Misc.readFile("C:\\Users\\davidz\\Desktop\\paragraph.xml"));
//        doc.add(par.getParagraph());
//        doc.close();
//        writer.close();
//        //String xml = Misc.readFile("C:\\Users\\davidz\\Dropbox\\work\\Eclipse_workspace\\iTextImpl\\src\\il\\co\\aman\\itextimpl\\sampleParagraph.xml");
//        String xml = Misc.getResource(par1.getClass(), "sampleParagraph.xml");
//        System.out.println(paragraphSize(xml, "C:\\Users\\davidz\\Desktop\\Fonts", 200f, 0, 0));
//        System.exit(0);
////        html = Misc.readFile("S:\\FormIT\\HeightDetection\\HeightDetection\\testHtmlFiles\\bigfont.html");
////        for (int i = 1000; i <= 12000; i += 1000) {
////            System.out.print(i);
////            System.out.print(" - ");
////            System.out.println(paragraphSize(html, i, 600f, 1.2f, 0f, 0f));
////        }
////        String html = "<div style=\"font-size:18pt; width: 1208px\"><p>hello there, everyone</p><p>hello there, everyone</p><p>hello there, everyone</p><p>hello there, everyone</p><p>hello there, everyone</p><p>hello there, everyone</p><p>hello there, everyone</p><p>hello there, everyone</p><p>hello there, everyone</p><p>hello there, everyone</p><p>hello there, everyone</p><p>hello there, everyone</p><p>hello there, everyone</p></div>";
////        if (args.length > 0) {
////            html = Misc.readFile(args[0]);
////        }
////        float width = 600f;
////        if (args.length > 1) {
////            width = Float.parseFloat(args[1]);
////        }
////        System.out.println(htmlParagraphSize(html, width, 0f, 0f));
//        //System.exit(0);
//        //Paragraph _para = new Paragraph(PdfWriter.RUN_DIRECTION_LTR);
//        //_para = _xml2para(_para, _html2Xml(_para, html));
//        //makePdf("C:\\Users\\davidz\\Desktop\\html.pdf", _para, 600, 600);
//        //System.out.println(htmlParagraphSize(html, 600f, 0f, 0f));
//        //System.out.println(paragraphSize(_html2Xml(html), 600f, 0f, 0f));
//        //System.exit(0);
//        try {
//            new File("C:\\Users\\davidz\\Desktop\\nestedlist.pdf").delete();
//            //FontFactory.registerDirectory("C:\\Users\\davidz\\Desktop\\fonts");
//            doc = new Document(new Rectangle(0, 0, 739, 1134));
//            writer = PdfWriter.getInstance(doc, new FileOutputStream(new File("C:\\Users\\davidz\\Desktop\\nestedlist.pdf")));
//            bf = FontFactory.getFont("Courier New", BaseFont.IDENTITY_H, BaseFont.EMBEDDED).getBaseFont();
//            doc.open();
//            cb = writer.getDirectContent();
////            try {
////                //Image img = Image.getInstance("http://examples.itextpdf.com/resources/img/bruno.jpg");
////                //Little.addImage(doc, img, 500, 1000 - height, 200, height, "");
////            } catch (IOException e) {
////                System.out.println(il.co.aman.apps.Misc.message(e));
////            } catch (BadElementException e) {
////                System.out.println(il.co.aman.apps.Misc.message(e));
////            }
//            float spacing = 1f; //20f
//            if (args.length > 2) {
//                spacing = Float.parseFloat(args[2]);
//            }
//            float width = 680f;
//            if (args.length > 1) {
//                width = Float.parseFloat(args[1]);
//            }
//            int add = 0;
//            if (args.length > 0) {
//                add = Integer.parseInt(args[0]);
//            }
//            String text = "ה�?�?ש�?ה �?ישרה ביו�? �?' ה - 21/6/15  �?ת �?ורנה הוז�?�? בכור כ�?נכ\"�?ית הר�?שונה ש�?ונתה ב�?�?ש�?ה הנוכחית, ";
//            String text2 = "ה�?נכ\"�?ית הר�?שונה ב�?שרד הפני�? �?יו�? הק�?תו. ";
//            String text3 = "�?ורנה הוז�?�? בכור בע�?ת ניסו�? עשיר ב�?גזר הציבורי, ובי�? היתר �?י�?�?ה שורה ש�? תפקידי�? �?ש�?עותיי�? : כ�?נכ\"�?ית �?שרד התשתיות ה�?�?ו�?יות";
//            /*String text = "But, in a larger sense, SPACING: " + Float.toString(spacing) + " HEIGHT: " + Integer.toString(height);
//             String text2 = " we can                      not dedicate, we can not consecrate, we can not hallow this ground. ";
//             String text3 = "The brave men, living and dead, who struggled here, have consecrated\n it, far above our poor power to add or detract. The world will little note, nor long remember what we say here, but it can never forget what they did here. It is for us the living, rather, to be dedicated here to the unfinished work which they who fought here have thus far so nobly advanced. It is rather for us to be here dedicated to the great task remaining before us—that from these honored dead we take increased devotion to that cause for which they gave the last full measure of devotion—that we here highly resolve that these dead shall not have died in vain—that this nation, under God, shall have a new birth of freedom—and that government of the people, by the people, for the people, shall not perish from the earth.";
//             /
//            Paragraph para = new Paragraph(PdfWriter.RUN_DIRECTION_LTR);
//            if (1 == 1) {
//                Paragraph._xml2para(para, Misc.readFile("C:\\Users\\davidz\\Desktop\\XML\\generated.xml"));//C:\\Users\\davidz\\Desktop\\XML\\nestedlist.xml
//                //Paragraph._xml2para(para, Paragraph._html2Xml(para, Misc.readFile("C:\\Users\\davidz\\Desktop\\XML\\nataly.xml")));
//            } else {
//                para.addText(text, new Font(bf, 12, Font.NORMAL, BaseColor.RED), "yellow");
//                para.addText(text2, new Font(bf, 12, Font.BOLD, BaseColor.RED));
//                para.addText(text3, new Font(bf, 12, Font.NORMAL, BaseColor.BLUE));
//                //FakeList list = FakeList.fromXml("<ul dir='ltr'><li>what's</li><li>up<ul><li>guombl<ul><li>shniblitz</li><li>potato</li></ul></li></ul></li><li>doc?</li></ul>");
//                Font bulletFont = new Font(bf);
//                bulletFont.setColor(BaseColor.BLUE);
//                bulletFont.setStyle(Font.NORMAL);
//                //if (list != null) {
//                //para.addList(list, new Font(Font.COURIER, 16, Font.NORMAL, Color.RED), bulletFont);
//                //}
//                FakeList complex = new FakeList(false, PdfWriter.RUN_DIRECTION_LTR);
//                //complex.setOrderedType(OrderedType.ROMAN);
//                complex.setBullets(new char[]{'#', '@', '*'});
//                complex.addElement("First");
//                complex.addElement("Second");
//                complex.insertList();
//                complex.insertListElement();
//                complex.addElementPart(new FakeList.ListText("-First", BaseColor.YELLOW));
//                complex.addElementPart(new FakeList.ListText(" and second"));
//                complex.closeLastElement();
//                complex.addElement("-Second");
//                complex.addElement("-Third");
//                complex.closeLastList();
//                complex.addElement("Fourth");
//                para.addList(complex, new Font(Font.FontFamily.COURIER, 16, Font.NORMAL, BaseColor.RED), bulletFont);
////        FakeList child = new FakeList(true, PdfWriter.RUN_DIRECTION_LTR);
////        child.addElement("-First");
////        child.addElement("-Second");
////        child.insertList(FakeList.createList(true, PdfWriter.RUN_DIRECTION_LTR, new String[]{"*First", "*Second", "*Third"}));
////        child.addElement("-Third");
////        complex.insertList(child);
//
//                String l = "<ol type='a' dir='ltr'><li>one</li><li>two <strong>four</strong> six eight</li><li>three</li></ol>";
//                if (args.length > 0) {
//                    //l = args[0];
//                }
//                //list = FakeList.fromXml(l);
//                //System.out.println(list == null);
//                //if (list != null) {
//                //para.addList(list, new Font(Font.COURIER, 24, Font.NORMAL, Color.RED), bulletFont);
//                //}
//            }
//            Font bulletFont = new Font(bf);
//            bulletFont.setColor(BaseColor.GREEN);
//            bulletFont.setStyle(Font.NORMAL);
//            FakeList list = new FakeList(false, PdfWriter.RUN_DIRECTION_LTR);
//            FakeList.ListText txt = new FakeList.ListText("red ", Xml2Phrases.getFont("Arial", 12F, "red", false, true, false, false));//("Arial", ParseList.TextStyle.ITALIC, 12f, Color.RED));
//            ArrayList<FakeList.ListText> el = new ArrayList<FakeList.ListText>();
//            el.add(txt);
//            txt = new FakeList.ListText("blue", Xml2Phrases.getFont("Arial", 12F, "blue", false, false, false, false));//new FakeList.ListFont("Arial", ParseList.TextStyle.NORMAL, 12f, Color.BLUE));
//            el.add(txt);
//            list.addElement(new FakeList.ListElement(el));
//            list.addElement("hello there");
//            list.addElement("everyone!");
//            //para.addList(list, new FakeList.ListFont("Broadway", ParseList.TextStyle.NORMAL, 18f, Color.BLUE).getFont(), FakeList.DEFAULT_BULLET_FONT);//new Font(Font.COURIER, 24, Font.NORMAL, Color.GREEN), bulletFont);
////            }C:\\Users\\davidz\\Dropbox\\work\\devJava\\iTextImpl\\src\\il\\co\\aman\\itextimpl\\sampleParagraph.xml
//            float parheight = paragraphSize(Misc.readFile("C:\\Users\\davidz\\Desktop\\XML\\generated.xml"), 
//                                "\\formit6\\d$\\formit\\ConvertIT\\Resources\\Fonts", width, 0, 0);
//            System.out.println(parheight);
//            parheight = paragraphSize(para.getParagraph(), 20000, width, para.getLineHeight(), 0, 0);
//            System.out.println(parheight);
//            if (para.addToDocument(cb, (int) parheight + add, para.getLineHeight(), 0f, 0f, Element.ALIGN_LEFT, 20, 1000, width + 20, 1000 - parheight - add,
//                    new Border(2, "ff0000", "solid"), new Border(2, "00ff00", "solid"), new Border(2, "0000ff", "solid"), new Border(2, "0,0,0", "solid"))) { //20, 1000, 750, 200 // if height is more than the space allowed by the coordinates, then the last part of the text will jump to the top of the paragraph.
//                //if (cutParagraph(ct, height, para.getParagraph())) {
//                doc.close();
//                writer.close();
//            } else {
//                System.out.println("Failed.");
//            }
//            
//            
//            //System.out.println(paragraphSize(Misc.readFile("C:\\Users\\davidz\\Desktop\\XML\\test.xml"), 800, 730, spacing, 0, 0));*/
//        } catch (DocumentException e) {
//            System.out.println(il.co.aman.apps.Misc.message(e));
//        } catch (FileNotFoundException e) {
//            System.out.println(il.co.aman.apps.Misc.message(e));
//        }

    }
}
