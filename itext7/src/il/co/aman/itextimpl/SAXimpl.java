package il.co.aman.itextimpl;

//import com.lowagie.text.Element;
//import com.lowagie.text.pdf.BaseFont;
//import com.lowagie.text.pdf.PdfBorderDictionary;
//import com.lowagie.text.pdf.RadioCheckField;
import java.awt.Color;
import java.util.*;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.layout.property.TextAlignment;

/**
 *
 * @author davidz
 */
public class SAXimpl extends org.xml.sax.helpers.DefaultHandler {

    StringBuffer accumulator = new StringBuffer(); // Accumulate parsed text
    String SAXerror = "";
    String error = "";
    Vector<FormParams> res = new Vector<FormParams>();
    FormParams params;
    private static final Map<String, FormParams.ElementType> elementNames = _createElementsMap();
    public static final Map<String, Integer> borderstyleNames = _createBorderStylesMap();
    public static final Map<String, Integer> checkTypeNames = _createCheckTypesMap();
    public static final Map<String, TextAlignment> alignmentTypes = _createAlignmentTypesMap();
    public static final Map<String, FormParams.TextLocation> locationTypes = _createLocationTypesMap();
    private static Class paramsclass;
    private static final java.lang.reflect.Field[] paramfields = _initFields();

    private static Map<String, FormParams.ElementType> _createElementsMap() {
        Map<String, FormParams.ElementType> res = new HashMap<String, FormParams.ElementType>();
        res.put("button", FormParams.ElementType.BUTTON);
        res.put("submitbutton", FormParams.ElementType.SUBMITBUTTON);
        res.put("clearbutton", FormParams.ElementType.CLEARBUTTON);
        res.put("resetbutton", FormParams.ElementType.RESETBUTTON);
        res.put("textbox", FormParams.ElementType.TEXT);
        res.put("hiddentext", FormParams.ElementType.HIDDENTEXT);
        res.put("combo", FormParams.ElementType.COMBO);
        res.put("js", FormParams.ElementType.JAVASCRIPT);
        res.put("checkbox", FormParams.ElementType.CHECKBOX);
        res.put("datebox", FormParams.ElementType.DATEBOX);
        res.put("radiobutton", FormParams.ElementType.RADIOBUTTON);
        res.put("container", FormParams.ElementType.CONTAINER);
        return Collections.unmodifiableMap(res);
    }
    
    private static Map<String, Integer> _createBorderStylesMap() {
        Map<String, Integer> res = new HashMap<String, Integer>();
        res.put("beveled", com.itextpdf.layout.borders.Border._3D_OUTSET);
        res.put("dashed", com.itextpdf.layout.borders.Border.DASHED);
        res.put("inset", com.itextpdf.layout.borders.Border._3D_INSET);
        res.put("solid", com.itextpdf.layout.borders.Border.SOLID);
        //res.put("underline", PdfBorderDictionary.STYLE_UNDERLINE);
        return Collections.unmodifiableMap(res);
    }
    
    private static Map<String, Integer> _createCheckTypesMap() {
        Map<String, Integer> res = new HashMap<String, Integer>();
        res.put("CHECK", PdfFormField.TYPE_CHECK);
        res.put("CIRCLE", PdfFormField.TYPE_CIRCLE);
        res.put("CROSS", PdfFormField.TYPE_CROSS);
        res.put("DIAMOND", PdfFormField.TYPE_DIAMOND);
        res.put("SQUARE", PdfFormField.TYPE_SQUARE);
        res.put("STAR", PdfFormField.TYPE_STAR);
        return Collections.unmodifiableMap(res);
    }
    
    private static Map<String, TextAlignment> _createAlignmentTypesMap() {
        Map<String, TextAlignment> res = new HashMap<String, TextAlignment>(); 
        res.put("Center", TextAlignment.CENTER);
        res.put("Justified", TextAlignment.JUSTIFIED);
        res.put("Justified_all", TextAlignment.JUSTIFIED_ALL);
        res.put("Left", TextAlignment.LEFT);
        res.put("Right", TextAlignment.RIGHT);
        res.put("Undefined", null);
        return Collections.unmodifiableMap(res);
    }

    private static Map<String, FormParams.TextLocation> _createLocationTypesMap() {
        Map<String, FormParams.TextLocation> res = new HashMap<String, FormParams.TextLocation>();
        res.put("Right", FormParams.TextLocation.RIGHT);
        res.put("Left", FormParams.TextLocation.LEFT);
        return Collections.unmodifiableMap(res);
    }

    private static java.lang.reflect.Field[] _initFields() {
        try {
            SAXimpl.paramsclass = Class.forName("il.co.aman.itextimpl.FormParams");
            return SAXimpl.paramsclass.getFields();
        } catch (Exception e) {
            SAXimpl.paramsclass = null;
            return null;
        }
    }

    public static boolean inFields(String name, java.lang.reflect.Field[] fields) {
        boolean res = false;
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getName().equals(name)) {
                res = true;
                break;
            }
        }
        return res;
    }

    public String getSaxError() {
        return this.SAXerror;
    }

    public String getError() {
        return this.error;
    }

    public Vector<FormParams> getParams() {
        return this.res;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        accumulator.setLength(0);
        if (elementNames.containsKey(qName)) {
           this.params = new FormParams();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (elementNames.containsKey(qName)) {
            this.params.type = elementNames.get(qName);
            this.res.add(this.params);
        }
        else if (inFields(qName, paramfields)) {
            try {
                java.lang.reflect.Field thefield = SAXimpl.paramsclass.getField(qName);
                Class fieldtype = thefield.getType();

                if (fieldtype.equals(Class.forName("java.lang.String"))) {
                    thefield.set(this.params, accumulator.toString().trim());
                }
                else if (fieldtype.equals(Class.forName("java.lang.Float"))) {
                    thefield.set(this.params, Float.parseFloat(accumulator.toString().trim()));
                }
                else if (fieldtype.equals(Class.forName("java.lang.Integer"))) {
                    try {
                        Integer i = Integer.parseInt(accumulator.toString().trim());
                        thefield.set(this.params, i);
                    }
                    catch (NumberFormatException e) { // if it is not an integer, it should be a borderstyle, checktype, or alignment string value
                        String val = accumulator.toString().trim();
                        if (borderstyleNames.containsKey(val)) {
                            thefield.set(this.params, borderstyleNames.get(val));
                        }
                        else if (checkTypeNames.containsKey(val)) {
                            thefield.set(this.params, checkTypeNames.get(val));
                        }
                        else if (alignmentTypes.containsKey(val)) {
                            thefield.set(this.params, alignmentTypes.get(val));
                        }
                    }
                }
                else if (fieldtype.equals(Class.forName("com.lowagie.text.pdf.BaseFont"))) {
                    try {
                        ////thefield.set(this.params, BaseFont.createFont(accumulator.toString().trim(), "Cp1252", false));
                    }
                    catch (Exception e) { }
                }
                else if (fieldtype.equals(Class.forName("java.awt.Color"))) {
                    try {
                        String[] RGB = accumulator.toString().trim().split(",");
                        thefield.set(this.params, new Color(Integer.parseInt(RGB[0]), Integer.parseInt(RGB[1]), Integer.parseInt(RGB[2])));
                   }
                   catch (Exception e) { }
                }
                else if (fieldtype.equals(Class.forName("java.lang.Boolean"))) {
                    try {
                        String boolval = accumulator.toString().trim();
                        thefield.set(this.params, "true".equals(boolval)? true: false);
                    }
                    catch (Exception e) { }
                }
            }
            catch (Exception e) {
                this.error = e.getMessage();
            }
        }
        else if (!"form".equals(qName)) {
            this.error = "Invalid element type: " + qName + "!";
            System.out.println(this.error);
            this.params = null;
        }
    }

    @Override
    public void characters(char[] buffer, int start, int length) {
        accumulator.append(buffer, start, length);
    }

    @Override
    public void warning(SAXParseException exception) {
        SAXerror = "WARNING: line " + exception.getLineNumber() + ": " + exception.getMessage();
    }

    @Override
    public void error(SAXParseException exception) {
        SAXerror = "ERROR: line " + exception.getLineNumber() + ": " + exception.getMessage();
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        SAXerror = "FATAL: line " + exception.getLineNumber() + ": " + exception.getMessage();
        throw (exception);
    }
}
