package il.co.aman.itextimpl;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Set;

import javax.xml.transform.stream.StreamSource;

import org.xml.sax.Attributes;

import com.itextpdf.kernel.pdf.PdfOutline;

import il.co.aman.apps.Misc;
import il.co.aman.apps.XsltTransform;
import il.co.aman.formit.GenericSAX;

public class IndexParser extends GenericSAX {	
	
	private static final String XSLT = "sortIndex.xsl"; // sorts the index by @Bookmark_Level (to ensure that the PdfOutline elements are created properly)

	static final protected String BOOKMARK_LEVEL = "Bookmark_Level";
	static final protected String BOOKMARK_PARENT_TAGKEY = "Bookmark_Parent_TagKey";
	static final protected String BOOKMARK_TEXT = "Bookmark_Text";
	static final protected String CELL = "Cell";
	static final protected String CELL_LINK = "Cell_Link";
	static final protected String INDEX = "Index";
	static final protected String ORDER_NO = "Order_no";
	static final protected String TAG = "Tag";
	static final protected String TAG_KEY = "Tag_Key";
	static final protected String TAG_LINK_KEY = "Tag_Link_Key";
	static final protected String TAG_TARGET_LINK_KEY = "TAG_TARGET_LINK_KEY";
	static final protected String TOOL_TIP = "Tool_Tip";
	
	private HashMap<String, TagIndex> _res = new HashMap<String, TagIndex>();
	private Bookmarks _bookmarks = new Bookmarks();
	private TagIndex _tag;
	private CellIndex _cell;
	private PdfOutline _root;
	
	public HashMap<String, TagIndex> result() {
		return this._res;
	}
	
	public Bookmarks bookmarks() {
		return this._bookmarks;
	}
	
	public static String toString(HashMap<String, TagIndex> tags) {
		StringBuilder res = new StringBuilder();
		Set<String> keys = tags.keySet();
		for (String key: keys) {
			res.append(tags.get(key).toString());
		}
		return res.toString();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		if (qName.equals(TAG)) {
			this._tag = new TagIndex(attributes.getValue(TAG_KEY));
		} else if (qName.equals(CELL)) {
			int bookmarkLevel;
			int orderNo;
			try {
				bookmarkLevel = Integer.parseInt(attributes.getValue(BOOKMARK_LEVEL));								
			}
			catch (Exception e) {
				bookmarkLevel = 0;
			}
			try {
				orderNo = Integer.parseInt(attributes.getValue(ORDER_NO));
			}
			catch (Exception e) {
				orderNo = -1;
			}
			String bookmarkParentTagKey = attributes.getValue(BOOKMARK_PARENT_TAGKEY);
			String bookmarkText = attributes.getValue(BOOKMARK_TEXT);
			String cellLink = attributes.getValue(CELL_LINK);
			String tagLinkKey = attributes.getValue(TAG_LINK_KEY);
			String tagTargetLinkKey = attributes.getValue(TAG_TARGET_LINK_KEY);
			String toolTip = attributes.getValue(TOOL_TIP);
			this._cell = new CellIndex(bookmarkLevel, bookmarkParentTagKey, bookmarkText, cellLink, orderNo, this._tag.tagKey(), tagLinkKey, tagTargetLinkKey, toolTip, this._root);
			if (bookmarkLevel > 0) {
				this._bookmarks.addBookmark(this._tag.tagKey(), this._cell.bookmark());
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) {
		if (qName.equals(TAG) && !this._res.containsKey(this._tag.tagKey())) {
			this._res.put(this._tag.tagKey(), this._tag);
		} else if (qName.equals(CELL)) {
			if (this._res.containsKey(this._tag.tagKey())) {
				this._res.get(this._tag.tagKey()).cells().put(this._cell.orderNo(), this._cell);
			} else {
				this._tag.cells().put(this._cell.orderNo(), this._cell);
			}
		}
	}
	
	public static IndexData getData(String xml, PdfOutline root) {
		IndexParser parser = new IndexParser();
		parser._root = root;
		IndexParser.parse(parser, sortXml(xml));
		return new IndexData(parser.result(), parser.bookmarks());
	}
	
	private static String sortXml(String xml) {
		String xslt = Misc.getResource(new IndexParser().getClass(), XSLT);
		XsltTransform xslttransform = new XsltTransform(new StreamSource(new StringReader(xslt)));
		return xslttransform.transform(new StreamSource(new StringReader(xml)));
	}
	
	public static class TagIndex {
		
		private String _tagKey;
		private HashMap<Integer, CellIndex> _cells;
		
		public String tagKey() {
			return this._tagKey;
		}
		
		public HashMap<Integer, CellIndex> cells() {
			return this._cells;
		}
		
		@Override
		public String toString() {
			StringBuilder res = new StringBuilder();
			res.append("TagKey: ").append(this.tagKey()).append("\n");
			Set<Integer> iter = this.cells().keySet();
			for (Integer key: iter) {
				res.append(this.cells().get(key)).append("\n");
			}				
			return res.toString();
		}
		
		public TagIndex(String tagKey) {
			this._tagKey = tagKey;
			this._cells = new HashMap<Integer, CellIndex>();
		}
		
	}
	
	public static class CellIndex {
		
		private int _bookmarkLevel, _orderNo;
		private String _bookmarkParentTagKey, _bookmarkText, _cellLink, _tagKey, _tagLinkKey, _tagTargetLinkKey, _toolTip;
		private Bookmarks.Bookmark _bookmark;
		
		public Bookmarks.Bookmark bookmark() {
			return this._bookmark;
		}
		
		public int bookmarkLevel() {
			return this._bookmarkLevel;
		}
		
		public String bookmarkParentTagKey() {
			return this._bookmarkParentTagKey;
		}
		
		public String bookmarkText() {
			return this._bookmarkText;
		}
		
		public String cellLink() {
			return this._cellLink;
		}
		
		public Integer orderNo() {
			return this._orderNo;
		}
		
		public String tagKey() {
			return this._tagKey;
		}
		
		public String tagLinkKey() {
			return this._tagLinkKey;
		}
		
		public String tagTargetLinkKey() {
			return this._tagTargetLinkKey;
		}
		
		public String toolTip() {
			return this._toolTip;
		}
		
		public boolean hasBookmark() {
			return this.bookmarkLevel() > 0;
		}
		
		public boolean hasLink() {
			return Misc.notNullOrEmpty(this.cellLink());
		}
		
		@Override
		public String toString() {
			StringBuilder res = new StringBuilder();
			res.append("Bookmark level: ").append(Integer.toString(this.bookmarkLevel())).append(", Bookmark Parent Tag Key: ").append(this.bookmarkParentTagKey()).append(", Bookmark text: ").append(this.bookmarkText());
			res.append(", Cell link: " ).append(this.cellLink()).append(", Order no.: ").append(Integer.toString(this.orderNo()))
				.append(", Tag key: ").append(this._tagKey).append(", Tag Link Key: ").append(this.tagLinkKey());
			res.append(", Tag Target Link Key: ").append(this.tagTargetLinkKey()).append(", Tooltip: ").append(this.toolTip()).append("\n");			
			return res.toString();
		}
		
		public CellIndex() {
			this(0, "", "", "", -1, "", "", "", "", null);
		}
		
		public CellIndex(int bookmarkLevel, String bookmarkParentTagKey, String bookmarkText, String cellLink, int orderNo, String tagKey, String tagLinkKey, 
				String tagTargetLinkKey, String toolTip, PdfOutline root) {
			this._bookmarkLevel = bookmarkLevel;
			this._bookmarkParentTagKey = bookmarkParentTagKey;
			this._bookmarkText = bookmarkText;
			this._cellLink = cellLink;
			this._orderNo = orderNo;
			this._tagKey = tagKey;
			this._tagLinkKey = tagLinkKey;
			this._tagTargetLinkKey = tagTargetLinkKey;
			this._toolTip = toolTip;
			if (bookmarkLevel > 0) {
				this._bookmark = new Bookmarks.Bookmark(bookmarkLevel, bookmarkText, bookmarkParentTagKey, bookmarkLevel == 1? root: null);				
			}
		}
	}
	
	public static class IndexData {
		
		private HashMap<String, TagIndex> _index;
		private Bookmarks _bookmarks;
		
		/**
		 * Indexed by the tag-key.
		 * @return
		 */
		public HashMap<String, TagIndex> index() {
			return this._index;
		}
		
		public Bookmarks bookmarks() {
			return this._bookmarks;
		}
		
		public IndexData() {
			this._index = new HashMap<String, TagIndex>();
			this._bookmarks = new Bookmarks();
		}
		
		public IndexData(HashMap<String, TagIndex> index, Bookmarks bookmarks) {
			this._index = index;
			this._bookmarks = bookmarks;
		}
		
		public boolean hasIndex(String tagkey, int orderno) {
			return getIndex(tagkey, orderno) != null;
		}
		
		public CellIndex getIndex(String tagkey, int orderno) {
			TagIndex tag = this._index.get(tagkey);
			if (tag != null && tag.cells().containsKey(orderno)) {
				return tag.cells().get(orderno);
			} else {
				return null;
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		String xml = Misc.readFile("\\\\formit7\\c$\\Users\\davidz\\Desktop\\indexXML.xml");
		IndexData parsed = getData(xml, null);
		//ArrayList<IndexTag> parsed = getData(xml);
		System.out.println(toString(parsed.index()));
		//System.out.println(parsed.index().size());
		System.out.println(parsed.getIndex("K987653", 1));//.tagInstance());
		//System.out.println(parsed.bookmarks());
	}
	
	
}
