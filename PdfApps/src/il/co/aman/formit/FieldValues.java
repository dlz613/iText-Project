package il.co.aman.formit;

import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import il.co.aman.formit.sendit.PdfSource;
import java.util.Iterator;
import java.util.Set;

/**
 * Enables fetching of field values from a PDF.<br> Fetches either the default
 * value or the updated value, if any.
 *
 * @author davidz
 */
public class FieldValues {

    // ***** declarations
    //
    Integer errorCode = 0;
    AcroFields fields;
    Set<String> fieldNames;

    // ***** constructors
    //
    public FieldValues(PdfSource source) {
        this.fields = source.getReader().getAcroFields();
        this.fieldNames = this.fields.getFields().keySet();
    }

    // ***** public instance methods
    //
    /**
     * Retrieves a field value from the PDF.
     *
     * @param name
     * @return The field value. If there was an error in retrieving the      
     * value, <code>getErrorCode()</code>      
     * and <code>getErrorMessage()</code> will reflect the error
     * encountered.
     */
    public String getValue(String name) {
        if (this.fieldNames.contains(name)) {
            this.errorCode = 0;
            return _getValue(this.fields, name);
        } else {
            this.errorCode = 1;
            return null;
        }
    }

    /**
     * Retrieves several field values from the PDF.
     *
     * @param names
     */
    public StringResults getValues(String[] names) {
        StringResults res = new StringResults(names.length);
        for (String name : names) {
            String val = this.getValue(name);
            if (this.errorCode != 0) {
                res.addResult(name, this.errorCode, this.getErrorMessage());
                this.errorCode = 0;
            } else {
                res.addResult(val, this.errorCode, this.getErrorMessage());
            }
        }
        this.errorCode = res.isAllValid() ? 0 : 2;
        return res;
    }

    /**
     * Retrieves several field values from the PDF.
     *
     * @param names A comma-separated list of the field names.
     */
    public StringResults getValues(String names) {
        return this.getValues(names.split(","));
    }

    public Integer getErrorCode() {
        return this.errorCode;
    }

    public String getErrorMessage() {
        switch (this.errorCode) {
            case 0:
                return "No error";
            case 1:
                return "Specified field does not exist";
            case 2:
                return "getValues() returned one or more failures";
            default:
                return "Unknown error code: " + this.errorCode.toString();
        }
    }

    /**
     * Gets the names of all of the Form Fields in the PDF.
     */
    public String[] getNames() {
        return _getNames(this.fields);
    }

    // ***** public static methods
    //
    /**
     * Gets the names of all of the Form Fields in a PDF.
     *
     * @param source
     */
    public static String[] getNames(PdfSource source) {
        return _getNames(source.getReader().getAcroFields());
    }

    /**
     * Gets the names of all of the Form Fields in a PDF and their values.
     *
     * @param source
     * @return An array of {@link String} arrays, the first element of each
     * containing the name of the field and the second the value.
     */
    public static String[][] getNamesAndValues(PdfSource source) {
        AcroFields fields = source.getReader().getAcroFields();
        Set<String> fieldNames = fields.getFields().keySet();
        String[][] res = new String[fieldNames.size()][2];
        Iterator it = fieldNames.iterator();
        int i = 0;
        while (it.hasNext()) {
            res[i][0] = (String) it.next();
            res[i][1] = _getValue(fields, res[i][0]);
            i++;
        }
        return res;
    }

    // ***** private methods
    //
    private static String[] _getNames(AcroFields fields) {
        Set<String> names = fields.getFields().keySet();
        return names.toArray(new String[]{});
    }

    private static String _getValue(AcroFields fields, String name) {
        String s = fields.getField(name);
        if (s == null || s.isEmpty()) { // try and get the default value
            PdfObject obj = ((PdfDictionary) fields.getFieldItem(name).getMerged(0)).get(PdfName.DV);
            if (obj != null) {
                s = obj.toString();
            }
        }
        return s;
    }

    public static void main(String[] args) {
        try {
//            FieldValues fieldvalues = new FieldValues(new PdfSource("C:\\Documents and Settings\\davidz\\Desktop\\kalauto.pdf"));
//            System.out.println(fieldvalues.getValue("radiogroup"));
//            StringResults res = fieldvalues.getValues("col3_E6,col2_E9,col1_E3,col3_E3,col1_E6,col1_E9,col2_E6,col3_E9,col2_E3");
//            for (String val : res.getValuesOrErrors()) {
//                //System.out.println(val);
//            }
            String[][] vals = FieldValues.getNamesAndValues(new PdfSource("C:\\Documents and Settings\\davidz\\Desktop\\kalauto.pdf"));
            for (String[] pair : vals) {
                System.out.println(pair[0] + " -- " + pair[1]);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
