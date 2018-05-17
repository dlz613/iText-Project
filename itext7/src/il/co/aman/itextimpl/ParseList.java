package il.co.aman.itextimpl;

import com.itextpdf.kernel.pdf.PdfWriter;
import il.co.aman.apps.Misc;
import org.xml.sax.Attributes;

/**
 * Parses an <code>XHTML</code> ordered or unordered list.
 *
 * @author davidz
 */
public class ParseList extends il.co.aman.formit.GenericSAX {

    public enum TextStyle {

        BOLD("bold"), ITALIC("italic"), NONE("normal"), NORMAL("normal"), UNDERLINE("underline");

        String _name;

        public String getName() {
            return this._name;
        }

        public static TextStyle fromString(String val) {
            if ("bold".equals(val)) {
                return BOLD;
            } else if ("italic".equals(val)) {
                return ITALIC;
            } else if ("normal".equals(val)) {
                return NORMAL;
            } else if ("underline".equals(val)) {
                return UNDERLINE;
            } else {
                return NONE;
            }
        }

        TextStyle(String name) {
            this._name = name;
        }
    }

    // Element names
    //private final static String EM = "em";
    private final static String LI = "li";
    private final static String OL = "ol";
    private final static String SPAN = "span";
    //private final static String STRONG = "strong";
    //private final static String U = "u";
    private final static String UL = "ul";

    // Attribute names
    private final static String DIR = "dir";
    private final static String STYLE = "style";
    private final static String TYPE = "type";

    // CSS keywords
    private final static String COLOR = "color";

    private FakeList topList;
    //private final Stack<FakeList> currentLists = new Stack<FakeList>();
    private TextStyle currentStyle = TextStyle.NORMAL;

    private static ListBullets.OrderedType getOrderedType(String code) {
        if ("1".equals(code)) {
            return ListBullets.OrderedType.NUMBER;
        } else if ("A".equals(code)) {
            return ListBullets.OrderedType.ALPHA_CAPS;
        } else if ("a".equals(code)) {
            return ListBullets.OrderedType.ALPHA;
        } else if ("I".equals(code)) {
            return ListBullets.OrderedType.ROMAN_CAPS;
        } else if ("i".equals(code)) {
            return ListBullets.OrderedType.ROMAN;
        } else {
            return null;
        }
    }

    public String getList() {
        return this.topList.getString();
    }

    public FakeList getListObject() {
        return this.topList;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        boolean ordered;
        FakeList.RunDir dir = FakeList.RunDir.LTR;
        ListBullets.OrderedType orderedType = null;
        if (qName.equals(OL) || qName.equals(UL)) {
            if (this.text().toString().length() > 0) {
                addElement(this.text().toString());
            }
            ordered = qName.equals(OL);
            if (ordered) {
                orderedType = getOrderedType(attributes.getValue(TYPE));
                String sDir = attributes.getValue(DIR);
                if ("rtl".equals(sDir)) {
                    dir = FakeList.RunDir.RTL;
                }
            }
            if (this.topList == null) {
                this.topList = new FakeList(ordered, dir);
                if (orderedType != null && ordered) {
                    this.topList.setOrderedType(orderedType);
                }
            } else {
                this.topList.insertList();
                this.topList.setOrdered(ordered, orderedType);
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (qName.equals(OL) || qName.equals(UL)) {
            if (this.topList.nestingLevel() > 0) {
                this.topList.closeLastList();
            }
        } else if (qName.equals(LI)) {
            if (this.text().toString().length() > 0) {
                addElement(this.text().toString());
            }
        }
    }

    private void addElement(String val) {
        if (this.topList != null) {
            this.topList.addElement(val, FakeList.DEFAULT_BULLET_FONT);//new FakeList.ListFont(null, this.currentStyle, null, null));
            this.text().setLength(0);
        }
    }

    /**
     * Parses the XML, returning a {@link FakeList} object.
     *
     * @param XML
     * @return An array of {@link Object} - the first element is the
     * {@link FakeList} object, if parsed properly, otherwise <code>null</code>.
     * The second entry is the {@link Exception} if an error occurred, otherwise
     * <code>null</code>.
     */
    public static Object[] parse(String XML) {
        ParseList parselist = new ParseList();
        Exception res = il.co.aman.formit.GenericSAX.parseEx(parselist, XML, false);
        if (res == null) {
            return new Object[]{parselist.getListObject(), null};
        } else {
            return new Object[]{null, res};
        }
    }

    private static String quoteLines(String val) {
        StringBuilder res = new StringBuilder();
        String[] lines = Misc.string2Lines(val);
        for (String line : lines) {
            res.append("'").append(line).append("'\n");
        }
        return res.toString();
    }

    public static void main(String[] args) {
        String XML = "<ol dir='ltr' type='i'><li>what's</li><li>up<ul><li>guombl<ul><li>shniblitz</li><li>potato</li></ul></li></ul></li><li>doc?</li></ol>";
//        XML = "<ol type='i'>\n"
//                + "    <li>First\n"
//                + "  <ol><li>something</li>\n"
//                + "  <li>else</li>\n"
//                + "  <li>also<ul><li>third level<ul><li>fourth level</li></ul></li></ul></li></ol></li>\n"
//                + "    <li>Second</li>\n"
//                + "    <li>Fourth</li>\n"
//                + "</ol>";
        ParseList parselist = new ParseList();
        String res = ParseList.parse(parselist, XML, false);
        System.out.println(parselist.getList().toString());
        if (res.equals("OK")) {
            //System.out.println(quoteLines(parselist.getList()));
        } else {
            System.out.println(res);
        }
    }

}
