package il.co.aman.itextimpl;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.TextAlignment;
import  com.itextpdf.kernel.pdf.PdfWriter;

import il.co.aman.apps.Misc;
import il.co.aman.itextimpl.FakeList.RunDir;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Parses a limited subset of CSS.<br><b>Note:</b> The only supported font-size unit is "pt".
 * @author davidz
 */
public class CssParser {

    public static final String BACKGROUND_COLOR = "background-color";
    public static final String BORDER_BOTTOM_COLOR = "border-bottom-color";
    public static final String BORDER_BOTTOM_STYLE = "border-bottom-style";
    public static final String BORDER_BOTTOM_WIDTH = "border-bottom-width";
    public static final String BORDER_LEFT_COLOR = "border-left-color";
    public static final String BORDER_LEFT_STYLE = "border-left-style";
    public static final String BORDER_LEFT_WIDTH = "border-left-width";
    public static final String BORDER_RIGHT_COLOR = "border-right-color";
    public static final String BORDER_RIGHT_STYLE = "border-right-style";
    public static final String BORDER_RIGHT_WIDTH = "border-right-width";
    public static final String BORDER_TOP_COLOR = "border-top-color";
    public static final String BORDER_TOP_STYLE = "border-top-style";
    public static final String BORDER_TOP_WIDTH = "border-top-width";
    public static final String COLOR = "color";
    public static final String DIRECTION = "direction";
    public static final String DISPLAY = "display";
    public static final String FONT_FAMILY = "font-family";
    /**
     * The only supported font-size unit is "pt".
     */
    public static final String FONT_SIZE = "font-size";
    public static final String FONT_STYLE = "font-style";
    public static final String FONT_WEIGHT = "font-weight";
    /**
     * The only supported length unit is "cm".
     */
    public static final String HEIGHT = "height";
    public static final String LINE_HEIGHT = "line-height";
    public static final String NONE = "none";
    //public static final String PADDING_BOTTOM   = "padding-bottom";
    public static final String PADDING_LEFT   = "padding-left";
    public static final String PADDING_RIGHT  = "padding-right";
    public static final String PADDING_TOP   = "padding-top";
    public static final String TEXT_ALIGN = "text-align";
    public static final String TEXT_DECORATION = "text-decoration";
    public static final String WHITE_SPACE = "white-space";
    public static final String WIDTH = "width";
    /*// the following are NOT actual CSS - they are various FormIT properties which we need to access.
    public static final String DOC_ON_LOAD_LINK = "doc-on-load-link";
    public static final String CELL_TEXT_BACKGROUND = "cell-text-background";
    public static final String CELL_HALIGN = "cell-halign";
    public static final String CELL_BARCODE_TYPE = "cell-barcode-type";
    public static final String CELL_TEXT_VISIBLE = "cell-text-visible";
    public static final String BARCODE_CHARSET = "barcode-charset";
    public static final String QR_CHARSET = "qr-charset";
    public static final String CELL_BAR_WIDTH = "cell-bar-width";
    public static final String WORD_INSERT_TYPE = "word-insert-type";
    public static final String WORD_INSERT_PDF = "word-insert-pdf"; // ???
    public static final String CELL_JUSTIFY_SPACING = "cell-justify-spacing";
    // the following six properties can only be used if the class is not shared with other elements.
    public static final String PARA_START_IND = "para-start-ind";
    public static final String PARA_END_IND = "para-end-ind";
    public static final String LIST_START_IND = "list-start-ind";
    public static final String LIST_END_IND = "list-end-ind";
    public static final String LI_START_IND = "li-start-ind";
    public static final String LI_END_IND = "li-end-ind";*/
    /**
     * @deprecated Not yet implemented
     */
    public static final String TEXT_INDENT = "text-indent";
    public static final String BOLD = "bold";
    public static final String CENTER = "center";
    public static final String ITALIC = "italic";
    public static final String JUSTIFY = "justify";
    public static final String JUSTIFY_ALL = "justify-all";
    public static final String LEFT = "left";
    public static final String LINE_THROUGH = "line-through";
    public static final String LTR = "ltr";
    public static final String NOWRAP = "nowrap";
    public static final String RIGHT = "right";
    public static final String RTL = "rtl";
    public static final String UNDERLINE = "underline";
    //public static final String Z_INDEX = "z-index";
    
    public static Style parse(GetFonts fontbank, String css, HashMap<String, PdfFont> fonts) {
        String[] elements = css.split(";");
        String[] parts;
        String fontFamily = "Arial";
        String color = "#000000";
        String backgroundColor = null; // default to transparent, NOT white!
        String direction = "ltr";
        String name, value;
        String[] border_bottom = new String[] {null, null, "0.0"}, border_left = new String[] {null, null, "0.0"}, 
        		border_right = new String[] {null, null, "0.0"}, border_top = new String[] {null, null, "0.0"};
        float padding_left = 0f, padding_right = 0f, padding_top = 0f;
        float fontSize = 12f;
        float height = 0f, width = 0f, linespace = 1f;
        boolean bold = false;
        boolean italic = false;
        boolean lineThrough = false;
        boolean underline = false;
        boolean wrap = false;
        boolean visible = true;
        Style.Alignment align = Style.Alignment.LEFT;
        ArrayList<String> parseErrors = new ArrayList<String>();
        
        for (String element : elements) {
            if (element.trim().length() > 0) {
            	if (element.endsWith(":")) { // in case of something like: text-decoration:;
            		element = element + " ";
            	}
                parts = element.split(":");
                if (parts.length > 1) {
                    name = parts[0].trim().toLowerCase();
                    value = parts[1].trim().toLowerCase();
                    if (name.equals(FONT_FAMILY)) {
                    	if (parts[1].trim().length() > 0) {
                    		fontFamily = parts[1].trim(); // Did not use <code>value</code> because we don't want to change the case.
                    	}
                    } else if (name.equals(FONT_SIZE)) {
                        fontSize = tryParseFloat(value.replace("pt", ""), fontSize);
                    } else if (name.equals(COLOR)) {
                    	if (parts[1].trim().length() > 0) {
                    		color = parts[1].trim();
                    	}
                    } else if (name.equals(DIRECTION)) {
                    	direction = value;
                    } else if (name.equals(BACKGROUND_COLOR)) {
                        backgroundColor = parts[1].trim();
                    } else if (name.equals(FONT_STYLE)) {
                        italic = value.equals(ITALIC);
                    } else if (name.equals(FONT_WEIGHT)) {
                        bold = value.equals(BOLD);
                    } else if (name.equals(HEIGHT)) {
                   		height = tryParseFloat(value.replace("cm", ""), 0f);
                    } else if (name.equals(WIDTH)) {
                     	width = tryParseFloat(value.replace("cm", ""), 0f);
                    } else if (name.equals(LINE_HEIGHT)) {
                    	linespace = tryParseFloat(value, 1f);
                    } else if (name.equals(TEXT_ALIGN)) {
                        align = Style.Alignment.fromString(value);
                    } else if (name.equals(TEXT_DECORATION)) {
                        lineThrough = value.equals(LINE_THROUGH);
                        underline = value.equals(UNDERLINE);
                    } else if (name.equals(BORDER_BOTTOM_COLOR)) {
                    	border_bottom[0] = value;
                    } else if (name.equals(BORDER_BOTTOM_STYLE)) {
                    	border_bottom[1] = value;
                    } else if (name.equals(BORDER_BOTTOM_WIDTH)) {
                    	border_bottom[2] = value.replace("cm", "");
                    } else if (name.equals(BORDER_LEFT_COLOR)) {
                    	border_left[0] = value;
                    } else if (name.equals(BORDER_LEFT_STYLE)) {
                    	border_left[1] = value;
                    } else if (name.equals(BORDER_LEFT_WIDTH)) {
                    	border_left[2] = value.replace("cm", "");
                    } else if (name.equals(BORDER_RIGHT_COLOR)) {
                    	border_right[0] = value;
                    } else if (name.equals(BORDER_RIGHT_STYLE)) {
                    	border_right[1] = value;
                    } else if (name.equals(BORDER_RIGHT_WIDTH)) {
                    	border_right[2] = value.replace("cm", "");
                    } else if (name.equals(BORDER_TOP_COLOR)) {
                    	border_top[0] = value;
                    } else if (name.equals(BORDER_TOP_STYLE)) {
                    	border_top[1] = value;
                    } else if (name.equals(BORDER_TOP_WIDTH)) {
                    	border_top[2] = value.replace("cm", "");
                    } else if (name.equals(PADDING_LEFT)) {
                    	padding_left = tryParseFloat(value.replace("cm", ""), 0f);
                    } else if (name.equals(PADDING_RIGHT)) {
                    	padding_right = tryParseFloat(value.replace("cm", ""), 0f);
                    } else if (name.equals(PADDING_TOP)) {
                    	padding_top = tryParseFloat(value.replace("cm", ""), 0f);
                    } else if (name.equals(WHITE_SPACE)) {
                    	wrap = !value.equals(NOWRAP);
                    } else if (name.equals(DISPLAY)) {
                    	visible = !value.equals(NONE);
                    } else {
                        parseErrors.add("Unrecognized element name: " + name);
                    }
                } else {
                    parseErrors.add("Missing colon in '" + element + "'");
                }
            }

        }
        Style res = new Style(fontbank, fontFamily, fontSize, height, width, linespace, color, backgroundColor, align, direction, bold, italic, lineThrough, underline,
        		border_bottom, border_left, border_right, border_top, padding_left, padding_right, padding_top, wrap, visible, fonts);
        res.setParseErrors(parseErrors);
        return res;
    }
    
    public static float tryParseFloat(String val, float def) {
    	float res = def;
    	try {
    		res = Float.parseFloat(val);
    	}
    	catch (Exception e) {
    		
    	}
    	return res;
    }
    
    private static double tryParseDouble(String val) {
    	double res = 0;
    	try {
    		res = Double.parseDouble(val);
    	}
    	catch (Exception e) {
    		
    	}
    	return res;
    }
    
    private static int tryParseInt(String val) {
    	int res = 0;
    	try {
    		res = Integer.parseInt(val);
    	}
    	catch (Exception e) {
    		
    	}
    	return res;
    }

    public static class Style {
    	
    	private BorderParams _border;
    	private PdfFont _font;
    	
    	public BorderParams getBorder() {
    		return this._border;
    	}
    	
    	public PdfFont getFont() {
    		return this._font;
    	}

        public enum Alignment {

            CENTER(CssParser.CENTER), JUSTIFY(CssParser.JUSTIFY), JUSTIFY_ALL(CssParser.JUSTIFY_ALL), LEFT(CssParser.LEFT), RIGHT(CssParser.RIGHT), NONE(null);
            
            String _name;
            
            Alignment(String val) {
                this._name = val;
            }
            
            public String getName() {
                return this._name;
            }

            /**
             * 
             * @param val  null treated as "left"
             * @return 
             */
            public static Alignment fromString(String val) {
                Alignment res = LEFT;
                if (val != null) {
                    if (val.toLowerCase().equals(CssParser.CENTER)) {
                        res = CENTER;
                    } else if (val.toLowerCase().equals(CssParser.JUSTIFY)) {
                    	res = JUSTIFY;
                    } else if (val.toLowerCase().equals(CssParser.JUSTIFY_ALL)) {
                    	res = JUSTIFY_ALL;
                    } else if (val.toLowerCase().equals(CssParser.RIGHT)) {
                        res = RIGHT;
                    }
                }
                return res;
            }
        }

        String _backgroundColor, _color, _fontFamily, _direction;
        float _fontSize, _height, _width, _linespace, _marginLeft, _marginRight, _marginTop/*, _spaceCharRatio = 2.5f*/;
        boolean _bold, _italic, _lineThrough, _underline, _wrap, _visible;
        Alignment _align = Alignment.LEFT;
        TextAlignment textAlign;
        com.itextpdf.layout.Style _fontStyle;
		RunDir runDir;
        
        ArrayList<String> _parseErrors;

        public Style(GetFonts fontBank, String fontFamily, float fontSize, float height, float width, float linespace, String color, String backgroundColor, Alignment align, String direction, 
        		boolean bold, boolean italic, boolean lineThrough, boolean underline, String[] border_bottom, String[] border_left, String[] border_right, String[] border_top,
        		float paddingLeft, float paddingRight, float paddingTop, boolean wrap, boolean visible, HashMap<String, PdfFont> fonts) {
            this._fontFamily = fontFamily;
            this._fontSize = fontSize;
            this._height = JavaItextFullCode.sizeConvert(height);
            this._width = JavaItextFullCode.sizeConvert(width);
            this._linespace = linespace;
            this._color = color;
            this._backgroundColor = backgroundColor;
            this._align = align;
            this._direction = direction;
            this._bold = bold;
            this._italic = italic;
            this._lineThrough = lineThrough;
            this._underline = underline;
            this._parseErrors = new ArrayList<String>();
            this._border = new BorderParams(JavaItextFullCode.sizeConvert(tryParseDouble(border_left[2])), 
            				JavaItextFullCode.sizeConvert(tryParseDouble(border_top[2])), 
            				JavaItextFullCode.sizeConvert(tryParseDouble(border_right[2])), 
            				JavaItextFullCode.sizeConvert(tryParseDouble(border_bottom[2])),
            				border_left[0], border_top[0], border_right[0], border_bottom[0],
            				border_left[1], border_top[1], border_right[1], border_bottom[1]);
            this._marginLeft = sizeConvert_negative(paddingLeft);
            this._marginRight = sizeConvert_negative(paddingRight);
            this._marginTop = sizeConvert_negative(paddingTop);
            this._wrap = wrap;
            this._visible = visible;
            String CELL_FONTstyle = "";
            if (this._bold) { 
            	CELL_FONTstyle += "bold";
            }
            if (this._italic) {
            	CELL_FONTstyle += " italic";
            }
            String CELL_text_decoration = "";
            if (this._underline) { 
            	CELL_text_decoration += "underline";
            }
            if (this._lineThrough) {
            	CELL_text_decoration += " line-through";
            }
            this._fontStyle = parseStyle(CELL_FONTstyle + " " + CELL_text_decoration, this._fontFamily);
            if (!fonts.containsKey(this._fontFamily)) {
            	fonts.put(this._fontFamily, fontBank.getFont(this._fontFamily));
            }
            this._font = fontBank.getFont(this._fontFamily);
            
//            this._font = PdfFontFactory.createFont(fontBank.getFont(this._fontFamily), (float) this._fontSize * 1f, 
//            		this._fontStyle, Border.parseColor(this._color));

			if (this._align == Alignment.RIGHT) {
				if (this._direction.equals("ltr")) {
					textAlign = TextAlignment.RIGHT;
				} else {
					textAlign = TextAlignment.RIGHT; // temporary fix until RTL starts really working. ***********************************
					////textAlign = TextAlignment.LEFT; // right-align for right-to-left is actually left-align
					//textAlign = Chunk.ALIGN_RIGHT; // right-align for right-to-left is actually left-align
				}
			} else if (this._align == Alignment.CENTER) {
				textAlign = TextAlignment.CENTER;
			} else if (this._align == Alignment.JUSTIFY) {
				textAlign = TextAlignment.JUSTIFIED;
			} else if (this._align == Alignment.JUSTIFY_ALL) {
				textAlign = TextAlignment.JUSTIFIED_ALL;
			} else { // "Left"
				if (this._direction.equals("ltr")) {
					textAlign = TextAlignment.LEFT;
				} else {
					textAlign = TextAlignment.LEFT; // temporary fix until RTL starts really working. ***********************************
					////textAlign = TextAlignment.RIGHT;
					//textAlign = Chunk.ALIGN_LEFT;
				}
			}

			if (this._direction.equals("ltr")) {
				runDir = FakeList.RunDir.LTR;
			} else {
				runDir = RunDir.RTL;
				//runDir = PdfWriter.RUN_DIRECTION_LTR;
			}

        }
        
        /*public Style(GetFonts fontBank, String fontFamily, float fontSize, float height, float width, String color, String backgroundColor, Alignment align, String direction, 
        		boolean bold, boolean italic, boolean lineThrough, boolean underline, String[] border_bottom, String[] border_left, String[] border_right, String[] border_top,
        		float paddingLeft, float paddingRight, float paddingTop, boolean wrap, String DOC_ONLOAD_LINK, String CELL_textbackground,
        		String CELL_BARCODE_TYPE, boolean CELL_text_visible, String BARCODE_CHARSET, String QR_CHARSET, double CELL_BAR_WIDTH, 
        		String CELL_HAlign, String WORD_INSERT_TYPE, byte[] WORD_INSERT_PDF, String Para_start_ind, String Para_end_ind, String List_start_ind, String List_end_ind,
        		String LI_start_ind, String LI_end_ind, float CELL_justify_spacing) {
        	this(fontBank, fontFamily, fontSize, height, width, color, backgroundColor, align, direction, bold, italic, lineThrough, underline, 
        			border_bottom, border_left, border_right, border_top, paddingLeft, paddingRight, paddingTop, wrap);
        	this._DOC_ONLOAD_LINK = DOC_ONLOAD_LINK;
        	this._CELL_textbackground = CELL_textbackground;
        	this._CELL_BARCODE_TYPE = CELL_BARCODE_TYPE;
        	this._CELL_text_visible = CELL_text_visible;
        	this._BARCODE_CHARSET = BARCODE_CHARSET;
        	this._QR_CHARSET = QR_CHARSET;
        	this._CELL_BAR_WIDTH = CELL_BAR_WIDTH;
        	this._CELL_HAlign = CELL_HAlign;
        	this._WORD_INSERT_TYPE = WORD_INSERT_TYPE;
        	this._WORD_INSERT_PDF = WORD_INSERT_PDF;
        	this._Para_start_ind = Para_start_ind;
        	this._Para_end_ind = Para_end_ind;
        	this._List_start_ind = List_start_ind;
        	this._List_end_ind = List_end_ind;
        	this._LI_start_ind = LI_start_ind;
        	this._LI_end_ind = LI_end_ind;
        	this._CELL_justify_spacing = CELL_justify_spacing;
        }*/
        
        public static String parseTextAlign(TextAlignment textalign) {
        	if (textalign == TextAlignment.LEFT){
        		return "left";
        	} else if (textalign == TextAlignment.RIGHT) {
        		return "right";
        	} else if (textalign == TextAlignment.CENTER) {
        		return "center";
        	} else if (textalign == TextAlignment.JUSTIFIED) {
        		return "justify";
        	} else {
        		return "justify-all";
        	}
        }
        
        public static com.itextpdf.layout.Style parseStyle(String style, String font_name) {
        	com.itextpdf.layout.Style res = new com.itextpdf.layout.Style();
            style = style.toLowerCase();
            if ((style.contains("bold")) && !font_name.toUpperCase().contains("BOLD")) { // assuming that all "real" bold fonts have the word bold in their name
            	res.setBold();
                //res = res | Font.BOLD;
            }
            if (style.contains("italic")) {
            	res.setItalic();
                //res = res | Font.ITALIC;
            }
            if (style.contains("underline")) {
            	res.setUnderline();
                //res = res | Font.UNDERLINE;
            }
            if (style.contains("line-through")) {
            	res.setLineThrough();
                //res = res | Font.STRIKETHRU;
            }
            return res;
        }
       
        public static float sizeConvert_negative(Float size) {
            if (size == null) {
                return 0f;
            } else {
                float res = size / 2.54f * 72f;
                if (res > 0 && res < 0.75f) { // The equivalent in points (at 96DPI) of 1 pixel
                    return 0.75f;
                } else if (res < 0 && Math.abs(res) < 0.75f) {
                    return -0.75f;
                } else {
                    return (float) res;
                }
            }
        }
        
        public Alignment align() {
    	    return this._align;
        }
        
        public TextAlignment textalign() {
        	return this.textAlign;
        }
        
        public RunDir rundir() {
        	return this.runDir;
        }

        public String backgroundColor() {
            return this._backgroundColor;
        }
        
        public String color() {
            return this._color;
        }

        public String fontFamily() {
            return this._fontFamily;
        }

        public float fontSize() {
            return this._fontSize;
        }
        
        public float height() {
        	return this._height;
        }
        
        public float width() {
        	return this._width;
        }
        
        public float linespace() {
        	return this._linespace;
        }

        public boolean isPlain() {
            return !this._bold && !this._italic && !this._lineThrough && !this._underline;
        }

        public boolean isBold() {
            return this._bold;
        }

        public boolean isItalic() {
            return this._italic;
        }
        
        public  com.itextpdf.layout.Style fontStyle() {
        	return this._fontStyle;
        }

        public boolean isLineThrough() {
            return this._lineThrough;
        }

        public boolean isUnderline() {
            return this._underline;
        }
        
        public boolean wrap() {
        	return this._wrap;
        }
        
        public boolean visible() {
        	return this._visible;
        }
        
        public float marginLeft() {
        	return this._marginLeft;
        }
        
        public float marginRight() {
        	return this._marginRight;
        }
        
        public float marginTop() {
        	return this._marginTop;
        }
        
        public String direction() {
        	return this._direction;
        }
        
        public ArrayList<String> getParseErrors() {
            return this._parseErrors;
        }
       
        public void setParseErrors(ArrayList<String> errors) {
            this._parseErrors = errors;
        }
        
        public void addParseError(String error) {
            this._parseErrors.add(error);
        }

        @Override
        public String toString() {
            StringBuilder res = new StringBuilder();
            if (Misc.notNullOrEmpty(this._fontFamily)) {
                res.append("font-family:").append(this._fontFamily).append("; ");
            }
            res.append("font-size:").append(Float.toString(this._fontSize)).append("pt; ");
            res.append("height:").append(Float.toString(this._height)).append("; ");
            res.append("width:").append(Float.toString(this._width)).append("; ");
            res.append("line-height:").append(Float.toString(this._linespace)).append("; ");
            if (Misc.notNullOrEmpty(this._color)) {
                res.append("color:").append(this._color).append("; ");
            }
            if (Misc.notNullOrEmpty(this._backgroundColor)) {
                res.append("background-color:").append(this._backgroundColor).append("; ");
            }
            res.append("text-align:").append(parseTextAlign(this.textAlign)).append("; ");
            if (this.isBold()) {
                res.append("font-weight:bold; ");
            }
            if (this.isItalic()) {
                res.append("font-style:italic; ");
            }
            if (this.isLineThrough()) {
                res.append("text-decoration:line-through; ");
            }
            if (this.isUnderline()) {
                res.append("text-decoration:underline; ");
            }
            res.append("border-bottom:").append(this._border.borderBottom()).append("; ");
            res.append("border-left:").append(this._border.borderLeft()).append("; ");
            res.append("border-right:").append(this._border.borderRight()).append("; ");
            res.append("border-top:").append(this._border.borderTop()).append("; ");
            //res.append("padding-bottom:").append(this._paddingBottom).append("cm; ");
            res.append("padding-left:").append(this._marginLeft).append("; ");
            res.append("padding-right:").append(this._marginRight).append("; ");
            res.append("padding-top:").append(this._marginTop).append("; ");
            res.append("white-space:").append(this._wrap? "normal; ": "nowrap; ");
            res.append("display:").append(this._visible? "block; ": "none; ");
            res.append("direction:").append((this.runDir == RunDir.LTR? "ltr": "rtl"));
            return res.toString();
        }

    }

    public static void main(String[] args) {
    	HashMap<String, PdfFont> fonts = new HashMap<String, PdfFont>();
        Style style = parse(new GetFonts("C:\\Users\\davidz\\Desktop\\fonts"), "text-align left;fruit:apple;color : green;TEXT-ALIGN: center  ; \nbackground-color: yellow; font-family: Arial;font-siZE:24 ; font-weight:   bold ;text-decoration:underline;", fonts);
        System.out.println(style.toString());
        for (String error: style.getParseErrors()) {
            System.out.println(error);
        }
    }

}
