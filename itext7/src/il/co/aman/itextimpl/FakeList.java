package il.co.aman.itextimpl;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.element.Text;
//import com.itextpdf.text.Phrase;
import com.itextpdf.layout.element.Paragraph;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Enables creating a List which can be embedded in a
 * {@link com.itextpdf.layout.element.Paragraph} object. Emulates the appearance of the
 * <code>HTML ul</code> or <code>ol</code> elements.<br>The class is called
 * <code>FakeList</code> because it does not use the iTtext
 * {@link com.lowagie.text.List} class.
 *
 * @author davidz
 */
public class FakeList {

    public enum PartType {
        BULLET, TEXT;
    }
    
    public enum RunDir {
    	LTR, RTL;
    }

    public static final Style DEFAULT_BULLET_FONT;

    private static final String ONE_SPACE = " ";
    private static final String TWO_SPACES = "  ";
    private static final String FOUR_SPACES = "    ";

    int _level, _ord;
    RunDir _dir;
    boolean _ordered;
    String _longest;
    ListBullets.OrderedType _orderedType = ListBullets.OrderedType.NUMBER;
    ArrayList<ListElement> _elements;
    //ArrayList<ListText> _currentLI;
    char[] _bullets;
    private final Stack<FakeList> currentLists = new Stack<FakeList>();
    private final Stack<ArrayList<ListText>> currentLIs = new Stack<ArrayList<ListText>>();
    private int _nestingLevel = 0;

    static {

        DEFAULT_BULLET_FONT = new Style();
        DEFAULT_BULLET_FONT.setFont("Arial");
        DEFAULT_BULLET_FONT.setFontColor(ColorConstants.BLACK);
    }

    /**
     * The depth of the most recently added list (0 is the level of a top-level
     * list). Refers to lists added with <code>insertList()</code>.
     *
     * @return
     */
    public int nestingLevel() {
        return this._nestingLevel;
    }

    /**
     * Sets the level of the current list. The highest level is 0.<br>
     *
     * @param val
     */
    public void setLevel(int val) {
        if (val >= 0) {
            this._level = val;
        }
    }

    /**
     * Retrieves the level of the current list. The highest level is 0.
     *
     * @return
     */
    public int getLevel() {
        return this._level;
    }

    /**
     * Sets the direction of the current list.
     *
     * @param val 
     */
    public void setDir(RunDir val) {
        this._dir = val;
    }

    public boolean isOrdered() {
        return this._ordered;
    }

    public void setOrdered(boolean ordered) {
        this._ordered = ordered;
    }

    public void setOrderedType(ListBullets.OrderedType type) {
        this._orderedType = type;
    }

    public ArrayList<ListText> getCurrentLI() {
        //return this._currentLI;
        ArrayList<ListText> res = null;
        if (!this.currentLIs.empty()) {
            res = this.currentLIs.peek();
        }
        return res;
    }

    /**
     * Replaces the default bullets. The first element of the array is the
     * bullet for the highest level, and so on. The last element of the array is
     * used if the level is deeper than the length of the array.
     *
     * @param vals
     */
    public void setBullets(char[] vals) {
        this._bullets = vals;
    }

    /**
     * Retrieves the set of bullets currently defined for unordered lists.<br>
     * <b>Note:</b> the default bullets (<code>U+2022, U+25E6,</code> and
     * <code>U+25AA</code>) are <b>not</b> supported by the 14 built-in PDF
     * fonts.
     *
     * @return
     */
    public char[] getBullets() {
        return this._bullets;
    }

    /**
     * Adds an element to the current list.
     *
     * @param val
     */
    public void addElement(String val) {
        currentList()._elements.add(new ListElement(val));
    }

    public void addElement(String val, Style font) {
        currentList()._elements.add(new ListElement(val, font));
    }

    public void addElement(ListElement el) {
        currentList()._elements.add(el);
    }

    public void addElementPart(ListText txt) {
//        if (this._currentLI != null) {
//            this._currentLI.add(txt);
//        }
        if (this.getCurrentLI() != null) {
            this.getCurrentLI().add(txt);
        }
    }

    public void closeLastElement() {
//        if (this._currentLI != null) { // if this element contained a sublist, then it has already been closed.
//            this.addElement(new FakeList.ListElement(this._currentLI));
//            this._currentLI = null;
//        }
        if (this.getCurrentLI() != null) { // if this element contained a sublist, then it has already been closed.
            this.addElement(new FakeList.ListElement(this.getCurrentLI()));
            this.currentLIs.pop();
        }
    }

    /**
     * Only to be used for a top-level list.
     *
     * @return
     */
    public String getString() {
        return this.getString(false);
    }

    /**
     * Returns the current list as a {@link String}.
     *
     * @param isSubList If <code>true</code>, this list is embedded in another
     * list (and therefore will <b>not</b> have a prepended newline).
     * @return
     */
    public String getString(boolean isSubList) {
        this._longest = longest();
        StringBuilder strVal = new StringBuilder();
        this._ord = 1;
        if (!isSubList) {
            strVal.append("\n");
        }
        for (ListElement el : this._elements) {
            if (this.isRtl()) {
                strVal.append("\n").append(el.toString()).append(this._bullet()).append(this._indent());
            } else {
                strVal.append(this._indent()).append(this._bullet()).append(el.toString()).append("\n");
            }
            if (el.hasSubList()) {
                FakeList sublist = el.getSubList();
                sublist.setLevel(this._level + 1);
                sublist.setBullets(this._bullets);
                sublist.setDir(this._dir);
                strVal.append(sublist.getString(true));
            }
        }
        return strVal.toString();
    }

    /**
     * Returns the list as an {@link ArrayList} of {@link TextPart} objects. The
     * purpose of this is to enable applying a different font to the bullets
     * than to the text itself. This is necessary if the text of the list is to
     * be displayed in one of the built-in PDF fonts, which do not support the
     * standard bullets. This is obviously only necessary for unordered
     * lists.<br><br>
     * This will also be necessary for supporting varied fonts within a single
     * list element - even for ordered lists.
     *
     * @param parts
     * @param isSubList
     * @return
     */
    public ArrayList<TextPart> getParts(ArrayList<TextPart> parts, boolean isSubList) {
        this._longest = longest();
        this._ord = 1;
        String current = isSubList ? "" : "\n";
        for (ListElement el : this._elements) {
            current += this._indent();
            parts.add(new TextPart(PartType.TEXT, new ListText(current)));
            current = "";
            parts.add(new TextPart(PartType.BULLET, new ListText(this._bullet())));
            for (int i = 0; i < el._val.size(); i++) {
                String nl = i == el._val.size() - 1 ? "\n" : "";
                parts.add(new TextPart(PartType.TEXT, new ListText(el._val.get(i).getText() + nl, el._val.get(i).getListFont(), el._val.get(i).getBackground())));
            }
            if (el.hasSubList()) {
                FakeList sublist = el.getSubList();
                sublist.setLevel(this._level + 1);
                sublist.setBullets(this._bullets);
                sublist.getParts(parts, true);
            }
        }
        return parts;
    }

    /**
     * Creates a new {@link FakeList} and places it on the internal stack. Calls
     * to <code>currentList()</code> will return this list until
     * <code>closeLastList()</code> is called. The new list initially shares the
     * same ordering and direction attributes as the parent list.
     */
    public void insertList() {
        FakeList newList = new FakeList(this._ordered, this._dir);
        if (this._orderedType != null && this._ordered) {
            newList.setOrderedType(this._orderedType);
        }
        pushList(newList);
    }

    public void insertListElement() {
        //this._currentLI = new ArrayList<ListText>();
        this.currentLIs.push(new ArrayList<ListText>());
    }

    public void setOrdered(boolean ordered, ListBullets.OrderedType orderedType) {
        currentList().setOrdered(ordered);
        currentList().setOrderedType(orderedType);
    }

    /**
     * Closes the current sublist and inserts it into its parent list.
     */
    public void closeLastList() {
        if (!this.currentLists.empty()) {
            FakeList last = this.currentLists.pop(); // do NOT combine this statement with the next one
            currentList().insertList(last);
            this._nestingLevel--;
        }
    }

    /**
     *
     * @param ordered If true, the list is ordered (e.g. 1, 2, 3 instead of
     * bullets).
     * @param dir 
     */
    public FakeList(boolean ordered, RunDir dir) {
        _init(ordered, dir);
    }

    private void _init(boolean ordered, RunDir dir) {
        this._ordered = ordered;
        this._dir = dir;
        this._elements = new ArrayList<ListElement>();
        this._level = 0;
        this._ord = 1;
        this._bullets = ListBullets.DEFAULT_BULLETS;
    }

    // **** Static methods ****
    /**
     * Creates a {@link Phrase} consisting of all the elements of the list
     * contained in <code>parts</code>, applying the appropriate {@link Font} to
     * those parts which contain bullets, if necessary.
     *
     * @param parts
     * @param style The font for the text.
     * @param defaultBulletFont An appropriate {@link Font} which supports the bullet
     * characters. If the general font supports the bullets, this can be
     * <code>null</code>.
     * @return
     */
    public static Paragraph parts2Phrase(ArrayList<TextPart> parts, /*Style*/PdfFont style, /*Style*/PdfFont defaultBulletFont) {
    	Paragraph res = new Paragraph();
    	res.setFont(style);
        //res.addStyle(style);
        Text chunk;
        for (TextPart part : parts) {
            chunk = new Text(part.getListText().getText());
            if (part.getListText().getListFont() != null) {
                chunk.addStyle(part.getListText().getListFont());
            }
            if (part.getListText().getBackground() != null) {
                chunk.setBackgroundColor(part.getListText().getBackground());
            }
            if (part.getType() == FakeList.PartType.BULLET && defaultBulletFont != null) {
                //chunk.addStyle(defaultBulletFont); // ensure that the bullet is visible (the built-in PDF fonts don't support the standard Unicode bullets)
                chunk.setFont(defaultBulletFont); // ensure that the bullet is visible (the built-in PDF fonts don't support the standard Unicode bullets)
            }
            res.add(chunk);
        }
        return res;
    }

    /**
     * Static method to create a list from an array of values.
     *
     * @param ordered If true, the list is ordered (e.g. 1, 2, 3 instead of
     * bullets).
     * @param dir 
     * @param level The level of the list - 0 is the highest level.
     * @param vals
     * @return
     */
    public static String createList(boolean ordered, RunDir dir, int level, String[] vals) {
        return createList(ordered, dir, level, ListBullets.OrderedType.NUMBER, vals);
    }

    /**
     * Static method to create a list from an array of values.
     *
     * @param ordered If true, the list is ordered (e.g. 1, 2, 3 instead of
     * bullets).
     * @param dir 
     * @param level The level of the list - 0 is the highest level.
     * @param type The numeration format of the list, if it is ordered.
     * @param vals
     * @return
     */
    public static String createList(boolean ordered, RunDir dir, int level, ListBullets.OrderedType type, String[] vals) {
        FakeList fl = createList(ordered, dir, vals);
        fl.setLevel(level);
        fl.setOrderedType(type);
        return fl.getString(false);
    }

    /**
     * Static method to create a list from an array of values.
     *
     * @param ordered If true, the list is ordered (e.g. 1, 2, 3 instead of
     * bullets).
     * @param dir 
     * @param vals
     * @return
     */
    public static FakeList createList(boolean ordered, RunDir dir, String[] vals) {
        FakeList fl = new FakeList(ordered, dir);
        for (String val : vals) {
            fl.addElement(val);
        }
        return fl;
    }

    /**
     * Uses the {@link ParseList} class to generate a {@link FakeList} object
     * from an <code>XHTML</code> unordered or ordered list.
     *
     * @param sXml
     * @return The generated list. If an error occurred, returns
     * <code>null</code>.
     */
    public static FakeList fromXml(String sXml) {
        Object[] res = ParseList.parse(sXml);
        return (FakeList) res[0];
    }

    // **** Private methods ****
    private String _bullet() {
        return ListBullets.getBullet(this._ordered, this._orderedType, this._ord++, this._level, this._bullets, this.isRtl());
    }

    private String previewBullet() {
        String res = this._bullet();
        if (this._ordered) {
            this._ord--;
        }
        return res;
    }

    private String _indent() {
        if (this._level == 0) {
            return _indent0();
        } else {
            String res = _indent0() + FOUR_SPACES;
            for (int i = 1; i < this._level; i++) {
                res += FOUR_SPACES;
            }
            return res;
        }
    }

    private String _indent0() {
        if (this._ordered) {
            if (this._orderedType == ListBullets.OrderedType.ROMAN || this._orderedType == ListBullets.OrderedType.ROMAN_CAPS) {
                String current;
                if (this.isRtl()) {
                    current = this.previewBullet().replace(ListBullets.SPACE_PERIOD, "");
                } else {
                    current = this.previewBullet().replace(ListBullets.PERIOD_SPACE, "");
                }
                return new String(new char[this._longest.length() - current.length()]).replace("\u0000", ONE_SPACE);
            } else {
                if (this.previewBullet().length() <= 3) {
                    return ONE_SPACE;
                } else {
                    return "";
                }
            }
        } else {
            return TWO_SPACES;
        }
    }

    private String longest() {
        String res = "";
        if (this._ordered && (this._orderedType == ListBullets.OrderedType.ROMAN || this._orderedType == ListBullets.OrderedType.ROMAN_CAPS)) {
            for (int i = 1; i < this._elements.size() + 1; i++) {
                String next = Roman.int2roman(i);
                if (next.length() > res.length()) {
                    res = next;
                }
            }
        }
        return res;
    }

    /**
     * Add a sublist to the last element added to the list. The level,
     * direction, and bullets of the sublist are set automatically in accordance
     * with the parent list.
     *
     * @param childList
     */
    private void insertList(FakeList childList) {
        currentList()._elements.get(this._elements.size() - 1)._subList = childList;
    }

    private static String parts2String(ArrayList<TextPart> parts) {
        StringBuilder res = new StringBuilder();
        for (TextPart part : parts) {
            res.append(part.getListText().getText());
        }
        return res.toString();
    }

    private boolean isRtl() {
        return this._dir == RunDir.RTL;
    }

    private FakeList currentList() {
        FakeList res;
        if (this.currentLists.empty()) {
            res = this;
        } else {
            res = this.currentLists.peek();
        }
        return res;
    }

    private void pushList(FakeList list) {
        this.currentLists.push(list);
        this._nestingLevel++;
    }

    // **** Classes ****
    /**
     * Represents one list element (<code>li</code>).
     */
    public static class ListElement {

        ArrayList<ListText> _val;
        FakeList _subList;

        ArrayList<ListText> getVal() {
            return this._val;
        }

        FakeList getSubList() {
            return this._subList;
        }

        ListElement(ArrayList<ListText> val) {
            this._val = val;
            this._subList = null;
        }

        ListElement(String val) {
            this._val = new ArrayList<ListText>();
            this._val.add(new ListText(val));
            this._subList = null;
        }

        ListElement(String val, Style font) {
            this._val = new ArrayList<ListText>();
            this._val.add(new ListText(val, font));
            this._subList = null;
        }

        void addList(FakeList list) {
            this._subList = list;
        }

        boolean hasSubList() {
            return this._subList != null;
        }

        @Override
        public String toString() {
            StringBuilder res = new StringBuilder();
            for (ListText t : this._val) {
                res.append(t.getText());
            }
            return res.toString();
        }
    }

    /**
     * Represents a piece of text (which may be <b>part</b> of a list element)
     * and it's associated font.
     */
    public static class ListText {

        String _val;
        Style _font;
        Color _background;

        public String getText() {
            return this._val;
        }

        public Style getListFont() {
            return this._font;
        }

        public Color getBackground() {
            return this._background;
        }

        public ListText(String val) {
            this._val = val;
            this._font = null;
            this._background = null;
        }

        public ListText(String val, Style font) {
            this._val = val;
            this._font = font;
            this._background = null;
        }

        public ListText(String val, Color background) {
            this._val = val;
            this._font = null;
            this._background = background;
        }

        public ListText(String val, Style pdfFont, Color color) {
            this._val = val;
            this._font = pdfFont;
            this._background = color;
        }
    }

    /**
     * Represents a part of a list element - either a piece of text or a bullet
     * (for an unordered list). The distinction is important in case the text
     * font doesn't support the characters used for the bullets.
     */
    public class TextPart {

        PartType _type;
        ListText _listText;

        public PartType getType() {
            return this._type;
        }

        public ListText getListText() {
            return this._listText;
        }

        public TextPart(PartType type, ListText value) {
            this._type = type;
            this._listText = value;
        }
    }

    public static void main(String[] args) {
        FakeList complex = new FakeList(false, RunDir.LTR);
        complex.setBullets(new char[]{'#', '@', '*'});
        complex.addElement("First");
        complex.addElement("Second");
        complex.insertList();
        complex.insertListElement();
        complex.addElementPart(new ListText("-First"));
        complex.addElementPart(new ListText(" and second"));
        complex.closeLastElement();
        complex.addElement("-Second");
        complex.insertList(FakeList.createList(true, RunDir.LTR, new String[]{"*First", "*Second", "*Third"}));
        complex.addElement("-Third");
        complex.closeLastList();
        complex.addElement("Fourth");
        System.out.println(complex.getString());
    }
}
