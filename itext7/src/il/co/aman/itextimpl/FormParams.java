package il.co.aman.itextimpl;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
//import com.itextpdf.text.DocumentException;
//import com.itextpdf.text.Element;
//import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.kernel.pdf.action.PdfAction;
//import com.itextpdf.text.pdf.PdfBorderDictionary;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.property.TextAlignment;
//import com.itextpdf.forms.fields.RadioCheckField;




import il.co.aman.apps.ColorConvert;
import il.co.aman.itextimpl.CssParser.Style;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 *
 * @author davidz
 */
public class FormParams {

    public enum ElementType {

        BUTTON, SUBMITBUTTON, CLEARBUTTON, RESETBUTTON, TEXT, TEXTAREA, JAVASCRIPT, COMBO, HIDDENTEXT,
        CHECKBOX, DATEBOX, EMAILBOX, RADIOBUTTON, CONTAINER
    }

    public enum TextLocation {

        RIGHT, LEFT
    }

    public String name, display_options, submit_options, defval, code, href, imgup, imgdown, font, javascript;
    public String format; // for date validation: 'ddmmyyyy', 'mmddyyyy', 'yyyymmdd' or 'any'
    public String group, value, display; // for radiobuttons
    public TextLocation text_location; // for radiobuttons
    public Integer page, fontsize;
    public Float xfrom, yfrom, xtill, ytill, border;
    public ElementType type;
    //public PdfFont pdffont;
    public Color fontcolor, bordercolor, backgroundcolor;
    public Style.Alignment alignment;
    public Integer borderstyle; // PdfBorderDictionary.STYLE_*
    public Integer checktype; // RadioCheckField.TYPE_*.
    public Integer def_selection; // for combobox. zero-based index of default displayed value
    public Boolean default_selected; // for radiobuttons and checkboxes
    //public PdfFormField parent;
    public String submitFields; // the name of a JavaScript variable containing the fields to be submitted (separated by commas), for a submit button.
    // if null (which is the default), then all of the fields in the document are submitted.
    public String preCode; // code to be executed before the submission is performed, for a submit button.
    public boolean disabled; // to disable the form field
    public PdfAction action; // to attach a non-JavaScript action to the field
    // additional_action (and additional_key, of course) were added in March, 2013
    public PdfAction additional_action;
    public PdfName additional_key; // the key indicating when to activate the additional_action
    public boolean required = true; // indicates if a textfield must have a value entered - November 2015. Will be added to FormIT in the future - for the time being, always false.

    public float width() {
    	return Math.abs(this.xtill - this.xfrom);
    }
    
    public float height() {
    	return Math.abs(this.ytill - this.yfrom);
    }
    
    public FormParams() {
        //System.out.println("Creating new FormParams object.");
        this.alignment = Style.Alignment.LEFT;
        this.backgroundcolor = new DeviceRgb(255, 255, 255);
//        try {
//            this.pdffont = PdfFontFactory.createFont("Helvetica", "Cp1252", false);
////        } catch (DocumentException e) {
////            this.pdffont = null;
//        } catch (IOException e) {
//            this.pdffont = null;
//        }
        this.border = 0f;
        this.bordercolor = new DeviceRgb(0, 0, 0);
        //this.borderstyle = Border.SOLID;
        this.checktype = PdfFormField.TYPE_CHECK;
        this.font = "Helvetica";
        this.fontcolor = new DeviceRgb(0, 0, 0);
        this.fontsize = 8;
        this.page = 1;
        this.format = "";
        this.def_selection = 0;
        this.default_selected = false;
        this.submitFields = null;
        this.preCode = "";
        this.disabled = false;
        this.action = null;
    }

    public static FormParams getTextParams(String name, String defval, Integer fontsize, float border,
            Integer borderstyle, String font/*, String basefont*/, String fontcolor, String bordercolor, String backgroundcolor,
            Float xfrom, Float yfrom, Float xtill, Float ytill, Style.Alignment alignment) {
        FormParams res = new FormParams();
        //System.out.println("getTextParams");
        res.name = name;
        res.defval = defval;
        res.value = defval;
        res.fontsize = fontsize;
        res.border = border;
        res.borderstyle = borderstyle;
        res.font = font;
//        try {
//            res.pdffont = PdfFontFactory.createFont(basefont, "Cp1252", false);//Cp1252
//            //res.basefont = BaseFont.createFont(basefont, BaseFont.IDENTITY_H, false);
////        } catch (DocumentException e) {
////            res.pdffont = null;
//        } catch (Exception e) {
//            res.pdffont = null;
//        }
        res.setColors(fontcolor, bordercolor, backgroundcolor);
        res.xfrom = xfrom;
        res.yfrom = yfrom;
        res.xtill = xtill;
        res.ytill = ytill;
        res.alignment = alignment;
        res.type = ElementType.TEXT;

        return res;
    }

    public static FormParams getTextAreaParams(String name, String defval, Integer fontsize, float border,
            Integer borderstyle, String font/*, String basefont*/, String fontcolor, String bordercolor, String backgroundcolor,
            Float xfrom, Float yfrom, Float xtill, Float ytill, Style.Alignment alignment) {
        FormParams res = new FormParams();
        res.name = name;
        res.defval = defval;
        res.value = defval;
        res.fontsize = fontsize;
        res.border = border;
        //res.borderstyle = borderstyle;
        res.font = font;
//        try {
//            res.pdffont = PdfFontFactory.createFont(basefont, "Cp1252", false);
//        } catch (IOException e) {
//            res.pdffont = null;
//        }
        res.setColors(fontcolor, bordercolor, backgroundcolor);
        res.xfrom = xfrom;
        res.yfrom = yfrom;
        res.xtill = xtill;
        res.ytill = ytill;
        res.alignment = alignment;
        res.type = ElementType.TEXTAREA;

        return res;
    }

    public static FormParams getDateParams(String name, String defval, String format, Integer fontsize, float border,
            Integer borderstyle, String font/*, String basefont*/, String fontcolor, String bordercolor, String backgroundcolor,
            Float xfrom, Float yfrom, Float xtill, Float ytill, Style.Alignment alignment) {
        FormParams res = new FormParams();
        //System.out.println("getDateParams");
        res.name = name;
        res.defval = defval;
        res.format = format;
        res.fontsize = fontsize;
        res.border = border;
        //res.borderstyle = borderstyle;
        res.font = font;
//        try {
//            res.pdffont = PdfFontFactory.createFont(basefont, "Cp1252", false);
////        } catch (DocumentException e) {
////            res.pdffont = null;
//        } catch (IOException e) {
//            res.pdffont = null;
//        }
        res.setColors(fontcolor, bordercolor, backgroundcolor);
        res.xfrom = xfrom;
        res.yfrom = yfrom;
        res.xtill = xtill;
        res.ytill = ytill;
        res.alignment = alignment;
        res.type = ElementType.DATEBOX;

        return res;
    }

    public static FormParams getEmailParams(String name, String defval, Integer fontsize, float border, Integer borderstyle,
            String font/*, String basefont*/, String fontcolor, String bordercolor, String backgroundcolor,
            Float xfrom, Float yfrom, Float xtill, Float ytill, Style.Alignment alignment) {
        FormParams res = new FormParams();
        //System.out.println("getEmailParams");
        res.name = name;
        res.defval = defval;
        res.fontsize = fontsize;
        res.border = border;
        //res.borderstyle = borderstyle;
        res.font = font;
//        try {
//            res.pdffont = PdfFontFactory.createFont(basefont, "Cp1252", false);
////        } catch (DocumentException e) {
////            res.pdffont = null;
//        } catch (IOException e) {
//            res.pdffont = null;
//        }
        res.setColors(fontcolor, bordercolor, backgroundcolor);
        res.xfrom = xfrom;
        res.yfrom = yfrom;
        res.xtill = xtill;
        res.ytill = ytill;
        res.alignment = alignment;
        res.type = ElementType.EMAILBOX;

        return res;
    }

    public static FormParams getHiddenParams(String name, String defval,
            Float xfrom, Float yfrom, Float xtill, Float ytill) {
        FormParams res = new FormParams();
        res.name = name;
        res.defval = defval;
        res.type = ElementType.HIDDENTEXT;
        res.xfrom = xfrom;
        res.yfrom = yfrom;
        res.xtill = xtill;
        res.ytill = ytill;

        return res;
    }
    
    public static FormParams getHiddenParams(String name, String defval) {
        return getHiddenParams(name, defval, 0f, 0f, 5f, 5f);
    }

    public static FormParams getBtnParams(String name, String code, String imgup, String imgdown,
            Float xfrom, Float yfrom, Float xtill, Float ytill) {
        FormParams res = new FormParams();
        System.out.println("getBtnParams");
        res.name = name;
        res.code = code;
        res.imgup = imgup;
        res.imgdown = imgdown;
        res.xfrom = xfrom;
        res.yfrom = yfrom;
        res.xtill = xtill;
        res.ytill = ytill;
        res.type = ElementType.BUTTON;

        return res;
    }

    public static FormParams getSubmitParams(String name, String href, String imgup, String imgdown,
            Float xfrom, Float yfrom, Float xtill, Float ytill) {
        FormParams res = new FormParams();
        System.out.println("getSubmitParams");
        res.name = name;
        res.href = href;
        res.imgup = imgup;
        res.imgdown = imgdown;
        res.xfrom = xfrom;
        res.yfrom = yfrom;
        res.xtill = xtill;
        res.ytill = ytill;
        res.type = ElementType.SUBMITBUTTON;
        res.action = null;

        return res;
    }

    public static FormParams getClearParams(String name, String imgup, String imgdown,
            Float xfrom, Float yfrom, Float xtill, Float ytill) {
        FormParams res = new FormParams();
        System.out.println("getClearParams");
        res.name = name;
        res.imgup = imgup;
        res.imgdown = imgdown;
        res.xfrom = xfrom;
        res.yfrom = yfrom;
        res.xtill = xtill;
        res.ytill = ytill;
        res.type = ElementType.CLEARBUTTON;

        return res;
    }

    public static FormParams getResetParams(String name, String imgup, String imgdown,
            Float xfrom, Float yfrom, Float xtill, Float ytill) {
        FormParams res = new FormParams();
        System.out.println("getResetParams");
        res.name = name;
        res.imgup = imgup;
        res.imgdown = imgdown;
        res.xfrom = xfrom;
        res.yfrom = yfrom;
        res.xtill = xtill;
        res.ytill = ytill;
        res.type = ElementType.RESETBUTTON;

        return res;
    }

    public static FormParams getComboParams(String name, String display_options, String submit_options, float border, String bordercolor, Integer borderstyle,
            String backgroundcolor, Float xfrom, Float yfrom, Float xtill, Float ytill, Integer def_selected) {
        FormParams res = new FormParams();
        System.out.println("getComboParams");
        res.name = name;
        res.display_options = display_options;
        res.submit_options = submit_options;
        res.border = border;
        res.setColors(null, bordercolor, backgroundcolor);
        //res.borderstyle = borderstyle;
        res.xfrom = xfrom;
        res.yfrom = yfrom;
        res.xtill = xtill;
        res.ytill = ytill;
        res.def_selection = def_selected;
        res.type = ElementType.COMBO;

        return res;
    }

    public static FormParams getCheckParams(String name, String value, Integer checktype, float border, String bordercolor, String backgroundcolor,
            Integer borderstyle, Float xfrom, Float yfrom, Float xtill, Float ytill, Boolean default_selected) {
        FormParams res = new FormParams();
        System.out.println("getCheckParams");
        res.name = name;
        res.value = value;
        res.checktype = checktype;
        res.border = border;
        res.setColors(null, bordercolor, backgroundcolor);
        //res.borderstyle = borderstyle;
        res.xfrom = xfrom;
        res.yfrom = yfrom;
        res.xtill = xtill;
        res.ytill = ytill;
        res.default_selected = default_selected;
        res.type = ElementType.CHECKBOX;

        return res;
    }

    public static FormParams getRadioParams(String name, String group, String value, String display, TextLocation location,
            Integer fontsize/*, String basefont*/, String fontcolor, float border, String bordercolor, String backgroundcolor,
            Integer borderstyle, Float xfrom, Float yfrom, Float xtill, Float ytill, Style.Alignment alignment, Boolean default_selected) {
        FormParams res = new FormParams();
        System.out.println("getRadioParams");
        res.name = name;
        res.group = group;
        res.value = value;
        res.display = display;
        res.text_location = location;
        res.fontsize = fontsize;
        res.setColors(fontcolor, bordercolor, backgroundcolor);
//        try {
//            res.pdffont = PdfFontFactory.createFont(basefont, "Cp1252", false);
////        } catch (DocumentException e) {
////            res.pdffont = null;
//        } catch (IOException e) {
//            res.pdffont = null;
//        }
        res.border = border;
        //res.borderstyle = borderstyle;
        res.xfrom = xfrom;
        res.yfrom = yfrom;
        res.xtill = xtill;
        res.ytill = ytill;
        res.alignment = alignment;
        res.default_selected = default_selected;
        res.type = ElementType.RADIOBUTTON;

        return res;
    }

    public static FormParams getContainerParams(String name) {
        FormParams res = new FormParams();
        System.out.println("getContainerParams");
        res.name = name;
        res.type = ElementType.CONTAINER;

        return res;
    }

    public static FormParams getJSparams(String code) {
        FormParams res = new FormParams();
        System.out.println("getJSparams");
        res.javascript = code;
        res.type = ElementType.JAVASCRIPT;

        return res;
    }

    public static FormParams getJSparamsFromFile(String filepath) {
        FormParams res = new FormParams();
        System.out.println("getJSparamsFromFile");
 //       res.javascript = FormWriter.FILEMARKER + filepath;
        res.type = ElementType.JAVASCRIPT;

        return res;
    }

    private void setColors(String fontcolor, String bordercolor, String backgroundcolor) {
        if (fontcolor != null) {
            this.fontcolor = colorDecode(fontcolor, this.fontcolor);
        }
        if (bordercolor != null) {
            this.bordercolor = colorDecode(bordercolor, this.bordercolor);
        }
        if (backgroundcolor != null) {
            this.backgroundcolor = colorDecode(backgroundcolor, this.backgroundcolor);
        }
    }
    
	/**
	 * Converts a decimal RGB color code (e.g. 255,128,37) to a
	 * {@link Color} object.
	 *
	 * @param code
	 * @param def The default {@link Color}, to be used in case of an error or if <code>code</code> is <code>null</code>
	 * @return The {@link Color} object. If <code>code</code> is not
	 *         valid, returns <code>null</code>.
	 */
	public static Color colorDecode(String code, Color def) {
		if (code == null) {
			return def;
		} else if (code.contains(",")){
			try {
				String[] RGB = code.split(",");
				return new DeviceRgb(Integer.parseInt(RGB[0]), Integer.parseInt(RGB[1]), Integer.parseInt(RGB[2]));
			} catch (NumberFormatException e) {
				return def;
			}
		} else {
			try {
				int[] rgb = ColorConvert.hex2Dec(code);
				return new DeviceRgb(rgb[0], rgb[1], rgb[2]);
			}
			catch (Exception e) {
				return def;
			}
		}
	}

    @Override
    public FormParams clone() {
        // NOTE: this only works because all of the variables are Fields (public)
        FormParams res = new FormParams();
        Class<? extends FormParams> paramsclass = this.getClass();
        java.lang.reflect.Field[] fields = paramsclass.getFields();
        try {
            for (Field field : fields) {
                field.set(res, field.get(this));
            }
            return res;
        } catch (IllegalAccessException e) {
            return null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public String stringRep() {
        try {
            Class<? extends FormParams> paramsclass = this.getClass();
            java.lang.reflect.Field[] fields = paramsclass.getFields();
            String res = "";
            for (Field field : fields) {
                res += field.getName() + ": " + field.get(this) + "\n";
            }
            return res;
        } catch (IllegalAccessException e) {
            return "";
        } catch (IllegalArgumentException e) {
            return "";
        } catch (SecurityException e) {
            return "";
        }
    }
}
