package il.co.aman.formit;

import com.itextpdf.text.pdf.AcroFields;

/**
 * Represents the location and size of an AcroField
 * @author davidz
 */
public class Position {
    
    private final String _name;
    private final int _page, _type;
    private final float _left, _top, _width, _height, _realTop; // _realTop starts from the top of the page
    
    public String name() {
        return this._name;
    }
    
    /**
     * The field type.
     * @return 
     */
    public int type() {
        return this._type;
    }
    
    /**
     * The field type as a String - see the <code>type2String()</code> method for the possible values.
     * @return 
     */
    public String typeStr() {
        return type2String(this._type);
    }
    
    public int page() {
        return this._page;
    }
    
    public float left() {
        return this._left;
    }
    
    /**
     * The top of the field in PDF terms - starting from the <b>bottom</b> of the page.
     * @return 
     */
    public float top() {
        return this._top;
    }
    
    public float width() {
        return this._width;
    }
    
    public float height() {
        return this._height;
    }
    
    /**
     * The top of the field starting from the top of the page.
     * @return 
     */
    public float realTop() {
        return this._realTop;
    }
    
    /**
     * Converts points (1/72 inch) to centimeters - all of the dimensions returned by this class are in points.
     * @param points
     * @return 
     */
    public static float points2cm(float points) {
        return (points / 72) * 2.54f; 
    }
    
    /**
     * Converts the field type to a string value, according to the following rules:<br>
     * <code>{@link AcroFields}.FIELD_TYPE_CHECKBOX</code> - "checkbox"<br>
     * <code>{@link AcroFields}.FIELD_TYPE_COMBO</code> - "combo"<br>
     * <code>{@link AcroFields}.FIELD_TYPE_LIST</code> - "list"<br>
     * <code>{@link AcroFields}.FIELD_TYPE_NONE</code> - "none"<br>
     * <code>{@link AcroFields}.FIELD_TYPE_PUSHBUTTON</code> - "button"<br>
     * <code>{@link AcroFields}.FIELD_TYPE_RADIOBUTTON</code> - "radio"<br>
     * <code>{@link AcroFields}.FIELD_TYPE_SIGNATURE</code> - "sign"<br>
     * <code>{@link AcroFields}.FIELD_TYPE_TEXT</code> - "text"<br>
     * Any other value returns "none".
     * @param type
     * @return 
     */
    public static String type2String(int type) {
        switch (type) {
            case AcroFields.FIELD_TYPE_CHECKBOX: return "checkbox";
            case AcroFields.FIELD_TYPE_COMBO: return "combo";
            case AcroFields.FIELD_TYPE_LIST: return "list";
            case AcroFields.FIELD_TYPE_NONE: return "none";
            case AcroFields.FIELD_TYPE_PUSHBUTTON: return "button";
            case AcroFields.FIELD_TYPE_RADIOBUTTON: return "radio";
            case AcroFields.FIELD_TYPE_SIGNATURE: return "sign";
            case AcroFields.FIELD_TYPE_TEXT: return "text";
            default: return "none";
        }
    }
    
    @Override
    public String toString() {
        return this._name + "::" + type2String(this._type) + "::" + this._page + "::" + this._left + "::" + this._realTop + "::" + this._width + "::" + this._height;
    }
    
    public String toCmString() {
        return this._name + "::" + type2String(this._type) + "::" + this._page + "::" + points2cm(this._left) + "::" 
                + points2cm(this._realTop) + "::" + points2cm(this._width) + "::" + points2cm(this._height);
    }
    
    public Position(String name, int type, int page, float left, float top, float width, float height, float realTop) {
        this._name = name;
        this._type = type;
        this._page = page;
        this._left = left;
        this._top = top;
        this._width = width;
        this._height = height;
        this._realTop = realTop;
    }
    
    public static void main(String[] args) {
        System.out.println(points2cm(72));
        System.out.println(points2cm(38));
    }
    
}
