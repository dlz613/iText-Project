/*
 * Parses the options XML
 */
package il.co.aman.itextimpl;

import org.xml.sax.Attributes;

/**
 *
 * @author davidz
 *
 * Sample input:
 * <select class="y96" name="Para3">
 *     <option value="volvo">Volvo</option>
 *     <option value="saab" selected="selected">Saab</option>
 *     <option value="mercedes">Mercedes</option>
 *     <option value="audi">Audi</option>
 * </select>
 *
 * Sample output:
 * get_display() = "Volvo;Saab;Mercedes;Audi"  // the content of the <option> elements
 * get_submit() = "volvo;saab;mercedes;audi"   // the values of the @value attributes
 * get_selected() = 1 (if there is no element with a @selected attribute this will be -1)
 *
 * NOTE: the element and attribute names are defined in the static final String variables ELEMENT and ATT
 * 
 */
public class OptionsParse extends org.xml.sax.helpers.DefaultHandler {
    StringBuffer accumulator = new StringBuffer(); // Accumulate parsed text
    String display_values = "";
    String submit_values = "";
    String submit = "";
    int serial = -1;
    Integer selected = -1;

    static final String ELEMENT = "option";
    static final String ATT = "value";

    public String get_display() {
        return this.display_values;
    }

    public String get_submit() {
        return this.submit_values;
    }

    public Integer get_selected() {
        return this.selected;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (ELEMENT.equals(localName)) {
            this.serial++;
            accumulator.setLength(0);
            this.submit = attributes.getValue(ATT);
            if (attributes.getValue("selected") != null) { // note that there is no check for multiple elements with the @selected attribute.
                this.selected = this.serial;
            }
        }
    }
    
    @Override
    public void characters(char[] buffer, int start, int length) {
        accumulator.append(buffer, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (ELEMENT.equals(localName)) {
            this.display_values = newVal(this.display_values, accumulator.toString().trim());
            this.submit_values = newVal(this.submit_values, this.submit);
            this.submit ="";
        }
    }

    public String newVal(String str, String val) {
        if ("".equals(str)) {
            return val;
        } else {
            return str + ";" + val;
        }
    }

    public static String[] getVals(String sXML) {
        try {
            javax.xml.parsers.SAXParserFactory spf = javax.xml.parsers.SAXParserFactory.newInstance();
            spf.setValidating(false);
            javax.xml.parsers.SAXParser sp = spf.newSAXParser();
            org.xml.sax.XMLReader reader = sp.getXMLReader();
            reader.setFeature("http://xml.org/sax/features/namespaces", true); // set namespace-sensitive

            org.xml.sax.InputSource input = new org.xml.sax.InputSource(new java.io.StringReader(sXML));
            OptionsParse handler = new OptionsParse();
            reader.setContentHandler(handler);
            reader.setErrorHandler(handler);
            try {
                reader.parse(input);
            } catch (Exception e) {
                return null;
            }
            return new String[] {handler.get_display(), handler.get_submit(), handler.get_selected().toString()};
        }
        catch (Exception e) {
            return null;
        }
    }


}
