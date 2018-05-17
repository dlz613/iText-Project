package il.co.aman.itextimpl;

import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.Style;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;

import il.co.aman.apps.Misc;

import java.io.IOException;
import java.util.ArrayList;

import org.xml.sax.Attributes;

/**
 * Converts an XML document (based on the schema <code>paragraph_xml.xsd</code>)
 * into {@link com.lowagie.text.Phrase} objects.
 *
 * @author davidz
 */
public class Xml2Phrases extends il.co.aman.formit.GenericSAX {

    // Element names
    private static final String BR = "br";
    private static final String DIV = "div";
    private static final String LI = "li";
    private static final String OL = "ol";
    private static final String P = "p";
    private static final String SPAN = "span";
    private static final String UL = "ul";

    // Attribute names
    private static final String BACKGROUND_COLOR = "background-color";
    private static final String COLOR = "color";
    private static final String DIR = "dir";
    private static final String DIRECTION = "direction";
    private static final String FONT_FAMILY = "font-family";
    private static final String FONT_SIZE = "font-size";
    private static final String FONT_STYLE = "font-style";
    private static final String FONT_WEIGHT = "font-weight";
    private static final String LINE_HEIGHT = "line-height";
    private static final String TEXT_ALIGN = "text-align";
    private static final String TEXT_INDENT = "text-indent"; // not yet implemented
    private static final String TEXT_DECORATION = "text-decoration";

    // Attribute values
    private static final String BOLD = "bold";
    private static final String CENTER = "center";
    private static final String ITALIC = "italic";
    private static final String JUSTIFY = "justify";
    private static final String LEFT = "left";
    private static final String LINE_THROUGH = "line-through";
    private static final String LTR = "ltr";
    private static final String RIGHT = "right";
    private static final String RTL = "rtl";
    private static final String TYPE = "type";
    private static final String UNDERLINE = "underline";

    //private ArrayList<Phrase> _phrases;
    private ArrayList<Paragraph> _phrases;
    private CssParser.Style _divStyle; // the style on the div element, which serves as the default style for the whole paragraph.
    String _backgroundColor = "white", _color = "black", _fontFamily = "Arial";
    float _fontSize = 10f, _lineHeight = 1.2f;
    Boolean _bold = false, _italic = false, _lineThrough = false, _underline = false;
    CssParser.Style.Alignment _align = CssParser.Style.Alignment.LEFT;
    private static final String NL_SAVER = "***aMaH N3WL1nE***";
    private static final String NL_SAVER_RX = NL_SAVER.replaceAll("\\*", "\\\\*");

    boolean _inList;
    private FakeList _topList;
    private GetFonts _fontbank;

    public ArrayList<Paragraph> getPhrases() {
        return this._phrases;
    }

    public CssParser.Style getDivStyle() {
        return this._divStyle;
    }

    /**
     * If not defined on the &lt;div> element, defaults to 1.2
     *
     * @return
     */
    public float getLineHeight() {
        return this._lineHeight;
    }

    public Xml2Phrases() {
        _init();
        //FontFactory.registerDirectories();
        this._fontbank = new GetFonts(null);
    }

    public Xml2Phrases(String fontDir) {
        _init();
        //FontFactory.registerDirectory(fontDir);
        this._fontbank = new GetFonts(fontDir);
    }

    private void _init() {
        this._phrases = new ArrayList<Paragraph>();
        this._divStyle = null;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        boolean ordered;
        FakeList.RunDir dir = FakeList.RunDir.LTR;
        ListBullets.OrderedType orderedType = null;
        if (qName.equals(DIV)) {
            parseStyles(attributes, true);
        } else if (this._divStyle == null) {
            this._divStyle = new CssParser.Style(this._fontbank, _fontFamily, _fontSize, 0f, 0f, _color, 
                    _backgroundColor, _align, "ltr", _bold, _italic, _lineThrough, _underline, 
                    new String[] {"0", null, null}, new String[] {"0", null, null}, new String[] {"0", null, null}, new String[] {"0", null, null},
                    0f, 0f, 0f, false);
            resetStyles();
        }
        if (qName.equals(P)) {
            this.text().append(NL_SAVER); // tentative!!!!!!!!
        } else if (qName.equals(SPAN)) {
            parseStyles(attributes, false);
            this.text().setLength(0); // we don't want to take notice of any text (e.g. newlines) which are not inside a span
        }
        if (qName.equals(OL) || qName.equals(UL)) {
            this._inList = true;
            if (this._topList != null) {
                this._topList.closeLastElement(); // this is a mistake - the list element CAN continue after the sublist is finished,
            }
            ordered = qName.equals(OL);
            if (ordered) {
                orderedType = ListBullets.OrderedType.getOrderedType(attributes.getValue(TYPE));
                String sDir = attributes.getValue(DIR);
                if ("rtl".equals(sDir)) {
                    dir =FakeList.RunDir.RTL;
                }
            }
            if (this._topList == null) {
                this._topList = new FakeList(ordered, dir);
                if (orderedType != null && ordered) {
                    this._topList.setOrderedType(orderedType);
                }
            } else {
                this._topList.insertList();
                this._topList.setOrdered(ordered, orderedType);
            }
        } else if (qName.equals(LI)) {
            this._topList.insertListElement();
        } else if (qName.equals(BR)) {
            this.text().append(NL_SAVER);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (qName.equals(DIV)) {

        } else if (qName.equals(P)) {
            this.text().append(NL_SAVER); // tentative!!!!!!!!
        } else if (qName.equals(SPAN)) {
            if (!this._inList) {
                //Phrase phrase = new Phrase();
                //Text phrase = new Text("");
                // NOTE: the font MUST be set before the text is added.
                //phrase.setFont(getFont());
                Paragraph c = new Paragraph(getText());
                try {
					c.addStyle(new Style().setFont(getFont()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                c.setBackgroundColor(String2Color.fromString(this._backgroundColor));
                //phrase.add(c);
                this._phrases.add(c);
                this.text().setLength(0);
                resetStyles();
            } else {
                try {
					this._topList.addElementPart(new FakeList.ListText(getText(), new Style().setFont(getFont()), 
					        String2Color.fromString(this._backgroundColor)));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                resetStyles();
                this.text().setLength(0);
            }
        } else if (qName.equals(OL) || qName.equals(UL)) {
            if (this._topList.nestingLevel() > 0) {
                this._topList.closeLastList();
            } else /*
             if (this._topList.nestingLevel() <= 0)*/ {
                ArrayList<FakeList.TextPart> parts = new ArrayList<FakeList.TextPart>();
                parts = this._topList.getParts(parts, false);
                try {
					this._phrases.add(FakeList.parts2Phrase(parts, new Style().setFont(getFont()), FakeList.DEFAULT_BULLET_FONT));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                this._inList = false;
                this._topList = null;
            }
        } else if (qName.equals(LI)) {
            _topList.closeLastElement();
        }
    }

    private String getText() {
        // first collapse adjacent whitespace
        String beforeCollapse = this.text().toString();
        String afterCollapse = Misc.collapseWhitespace(beforeCollapse).replaceAll(NL_SAVER_RX + " ", NL_SAVER_RX).replaceAll(NL_SAVER_RX, "\n");
        return afterCollapse;
    }

    private PdfFont getFont() throws IOException {
        // Cascades _divStyle and the current style definitions
        String fontFamily, color;
        float fontSize;
        boolean bold, italic, lineThrough, underline;
        fontFamily = this._fontFamily != null ? this._fontFamily : this._divStyle.fontFamily();
        color = this._color != null ? this._color : this._divStyle.color();
        fontSize = this._fontSize > 0f ? this._fontSize : this._divStyle.fontSize();
        bold = this._bold != null ? this._bold : this._divStyle.isBold();
        italic = this._italic != null ? this._italic : this._divStyle.isItalic();
        lineThrough = this._lineThrough != null ? this._lineThrough : this._divStyle.isLineThrough();
        underline = this._underline != null ? this._underline : this._divStyle.isUnderline();

        return getFont(fontFamily, fontSize, color, bold, italic, lineThrough, underline);
    }

    private void parseStyles(Attributes attributes, boolean isDiv) {
        this._align = CssParser.Style.Alignment.fromString(attValueLowerCase(attributes, TEXT_ALIGN));
        if (attributes.getValue(BACKGROUND_COLOR) != null) {
            this._backgroundColor = attributes.getValue(BACKGROUND_COLOR);
        }
        this._bold = BOLD.equals(attValueLowerCase(attributes, FONT_WEIGHT));
        if (attributes.getValue(COLOR) != null) {
            this._color = attributes.getValue(COLOR);
        }
        this._fontFamily = attributes.getValue(FONT_FAMILY);
        String fontsize = attValueLowerCase(attributes, FONT_SIZE);
        if (fontsize != null) {
            this._fontSize = Float.parseFloat(fontsize.replace("pt", ""));
        }
        this._italic = ITALIC.equals(attValueLowerCase(attributes, FONT_STYLE));
        this._lineThrough = LINE_THROUGH.equals(attValueLowerCase(attributes, TEXT_DECORATION));
        this._underline = UNDERLINE.equals(attValueLowerCase(attributes, TEXT_DECORATION));
        String lineHeight = attValueLowerCase(attributes, LINE_HEIGHT);
        if (isDiv) {
            if (lineHeight != null) {
                if (lineHeight.endsWith("%")) {
                    this._lineHeight = Float.valueOf(lineHeight.replace("%", "")) / 100;
                } else {
                    this._lineHeight = Float.valueOf(lineHeight);
                }
            }
        }
    }

    private String attValueLowerCase(Attributes attributes, String name) {
        String res = attributes.getValue(name);
        if (res != null) {
            res = res.toLowerCase();
        }
        return res;
    }

    private void resetStyles() {
        this._align = CssParser.Style.Alignment.NONE;
        this._backgroundColor = "white";
        this._color = "black";
        this._bold = null;
        this._italic = null;
        this._fontFamily = "Arial";
        this._fontSize = 0f;
        this._lineThrough = null;
        this._underline = null;
    }
    
    public static PdfFont getFont(String family, float size, String color, boolean bold, boolean italic, boolean lineThrough, boolean underline) throws IOException {
        if (!Misc.notNullOrEmpty(family)) {
            family = "Arial";
        }
        family = family.replace("'", "").replace("\"", "");
        //BaseFont bf = FontFactory.getFont(family, BaseFont.IDENTITY_H, BaseFont.EMBEDDED).getBaseFont();
        Style style = new Style();
        if (bold) {
        	style.setBold();
        }
        if (italic) {
        	style.setItalic();
        }
        if (lineThrough) {
        	style.setLineThrough();
        }
        if (underline) {
        	style.setUnderline();
        }
        style.setFontColor(String2Color.fromString(color));
        //PdfFont font = new PdfFont(bf, size, style, String2Color.fromString(color));
        PdfFont font = PdfFontFactory.createFont(family, PdfEncodings.IDENTITY_H);
        style.setFont(font);
        
        return font;
    }

    /**
     * Uses the default system font directories.
     *
     * @param XML
     * @return An array of {@link Object}, where the first element is the
     * {@link Xml2Phrases} object and the second is an {@link Exception}. If the
     * parsing succeeded, the {@link Exception} will be <code>null</code>, and
     * vice versa if the parsing failed.
     */
    public static Object[] parse(String XML) {
        Xml2Phrases xml2phrases = new Xml2Phrases();
        return parse(XML, xml2phrases);
    }

    /**
     *
     * @param XML
     * @param font_dir If <code>null</code> or empty, uses the default font
     * directories.
     * @return An array of {@link Object}, where the first element is the
     * {@link Xml2Phrases} object and the second is an {@link Exception}. If the
     * parsing succeeded, the {@link Exception} will be <code>null</code>, and
     * vice versa if the parsing failed.
     */
    public static Object[] parse(String XML, String font_dir) {
        if (Misc.notNullOrEmpty(font_dir)) {
            Xml2Phrases xml2phrases = new Xml2Phrases(font_dir);
            return parse(XML, xml2phrases);
        } else {
            return parse(XML);
        }
    }

    private static Object[] parse(String XML, Xml2Phrases xml2phrases) {
        Exception res = il.co.aman.formit.GenericSAX.parseEx(xml2phrases, XML, false);
        if (res == null) {
            return new Object[]{xml2phrases, null};
        } else {
            return new Object[]{null, res};
        }
    }

    public static void main(String[] args) {
        String path = "C:\\Users\\davidz\\Desktop\\XML\\collapse.xml";
        try {
            String xml = Misc.readFile(path);
            Object[] res = parse(xml);
            Xml2Phrases xml2phrases = (Xml2Phrases) res[0];
            System.out.println(xml2phrases.getDivStyle());
            System.out.println(xml2phrases.getPhrases().size());

        } catch (IOException e) {
            System.out.println(Misc.message(e));
        }
    }

}
