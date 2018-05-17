package il.co.aman.itextimpl;

import il.co.aman.apps.Misc;
import il.co.aman.apps.XsltTransform;
import il.co.aman.formit.GenericSAX;
import il.co.aman.itextimpl.AdditionalMetadata.Metadata;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.xml.transform.stream.StreamSource;

import org.xml.sax.Attributes;

public class ArchiveDataParser {
	
	static String _sXslt;
	static XsltTransform _xslt = null;
	static Parser _parser;
	
	static {
		_sXslt = Misc.getResource(new ArchiveDataParser().getClass(), "archivedata.xsl");
		if (_sXslt != null) {
			_xslt = new XsltTransform(new StreamSource(new StringReader(_sXslt)));
		}
		_parser = new Parser();
	}
	
	public static String parseXml(String xml) {
        if (_xslt != null) {
            return _xslt.transform(new StreamSource(new StringReader(xml)));
        } else {
            return null;
        }
	}
	
	public static java.util.ArrayList<ArchivePage> getData(String xml) {
		java.util.ArrayList<ArchivePage> res = new java.util.ArrayList<ArchivePage>();
		ArchivePage page = null;
		String data = parseXml(xml);
		String[] lines = data.split("\n");
		for (String line: lines) {
			if (line.trim().length() > 0) {
				if (line.startsWith("PAGE")) {
					if (page != null) {
						res.add(page);
					}
					page = new ArchivePage(line.substring(line.indexOf(",") + 1).trim(), "");
				} else if (page != null) {
					String mdid = line.substring(0, line.indexOf(':')).trim();
					String vals = line.substring(line.indexOf(':') + 1);
					int pos = 0;
					String contentType = vals.substring(0, pos = vals.indexOf(","));
					String left = vals.substring(pos + 1, pos = vals.indexOf(",", pos + 1));
					String top = vals.substring(pos + 1, pos = vals.indexOf(",", pos + 1));
					String tagkey = vals.substring(pos + 1, pos = vals.indexOf("<", pos + 1));
					String value = vals.substring(pos + 1).trim().replace(">", "");
					ArchiveField field = new ArchiveField(mdid, contentType, CssParser.tryParseFloat(left, 0f), CssParser.tryParseFloat(top, 0f), value, tagkey, -8989);
					page.fields().add(field);
				}
			}
		}
		res.add(page);
		return res;
	}
	
	/**
	 * Parses the XML using SAX, not XSLT. Much faster.
	 * @param xml
	 * @return
	 */
	public static ArchiveResult getDataSax(String xml, CssClassParser classparser, java.util.HashMap<String, AdditionalMetadata.Metadata> metadata, String dir) {
		_parser.reset(classparser, metadata, dir);
		Parser.parse(_parser, xml);
		return _parser.result();
	}
	
	public static class ArchiveResult {
		private ArrayList<ArchivePage> _pages;
		private DocInfo _docInfo;
		private HiddenText _hiddenText;
		
		public ArrayList<ArchivePage> pages() {
			return this._pages;
		}
		
		public DocInfo docInfo() {
			return this._docInfo;
		}
		
		public HiddenText hiddenText() {
			return this._hiddenText;
		}
		
		public ArchiveResult(ArrayList<ArchivePage> pages, DocInfo docInfo, HiddenText hiddenText) {
			this._pages = pages;
			this._docInfo = docInfo;
			this._hiddenText = hiddenText;
		}
	}
	
	private static class Parser extends GenericSAX {
		private String _contentType, _left, _top, _mdid, _value, _tagkey, _dir;
		private int _ordno, _currentTag;
		private boolean _inPage = false, _sortCells = false, _inPopup = false;
		private ArrayList<ArchivePage> _pages = new ArrayList<ArchivePage>();
		private DocInfo _docInfo;
		private HiddenText _hiddenText;
		private ArchivePage _page = null;
		CssClassParser _classparser = null;
		private java.util.HashMap<String, AdditionalMetadata.Metadata> _metadata = null;

		public ArchiveResult result() {
			return new ArchiveResult(this._pages, this._docInfo, this._hiddenText);
		}
		
		public void reset(CssClassParser classparser, java.util.HashMap<String, AdditionalMetadata.Metadata> metadata, String dir) {
			this._pages = new ArrayList<ArchivePage>();
			this._classparser = classparser;
			this._metadata = metadata;
			this._dir = dir;
			this._docInfo = null;
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) {
			if (qName.equals("Data")) {
				String cust = attributes.getValue("Cust");
				String docno = attributes.getValue("DocNo");
				String infopass = attributes.getValue("infopass");
				String font = attributes.getValue("font");
				String infoloc = attributes.getValue("infoloc");
				if (infoloc != null) {
					String[] loc = infoloc.split(",");
					this._hiddenText = new HiddenText(cust + "," + docno + "," + infopass, font, 
							JavaItext.sizeConvert(Float.parseFloat(loc[0])), JavaItext.sizeConvert(Float.parseFloat(loc[1])), 
							JavaItext.sizeConvert(Float.parseFloat(loc[2])), JavaItext.sizeConvert(Float.parseFloat(loc[3])));
				}
				this._docInfo = new DocInfo(cust, docno, infopass/*, font, 
						JavaItext.sizeConvert(Float.parseFloat(loc[0])), JavaItext.sizeConvert(Float.parseFloat(loc[1])), 
						JavaItext.sizeConvert(Float.parseFloat(loc[2])), JavaItext.sizeConvert(Float.parseFloat(loc[3]))*/);
			} else if (qName.equals("Page")) {
				this._page = new ArchivePage(attributes.getValue("Id"), attributes.getValue("PageType"));
				this._inPage = true;
			} else if (qName.equals("Tag")) {
				this._tagkey = attributes.getValue("TagKey");
				if (this._tagkey == null) {
					this._tagkey = "";
				}
				this._currentTag = this._page.fields().size();
			} else if (qName.equals("Cell") && !this._inPopup) {
				this._mdid = attributes.getValue("Id");
				Metadata md = this._metadata.get(this._mdid);
				this._sortCells = false;
				if (md != null) {
					this._sortCells = md.cellSorting().equals("1");
				}
				this._contentType = attributes.getValue("type");
				this._left = attributes.getValue("Left");
				this._top = attributes.getValue("Top");
				this._ordno = Integer.parseInt(attributes.getValue("ord_no"));
			} else if (qName.equals("PopupPage")) {
				this._inPopup = true;
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) {
			if (qName.equals("Page")) {
				this._pages.add(this._page);
				this._inPage = false;
			} else if (qName.equals("Cell") && this._inPage && !this._inPopup) {
				this._value = this.text().toString().trim();
				this.clearChars();
				if (!this._mdid.equals("-999")) {
					ArchiveField field = new ArchiveField(this._mdid, this._contentType, 
							CssParser.tryParseFloat(this._left, 0f), CssParser.tryParseFloat(this._top, 0f), this._value, this._tagkey, this._ordno);
					this._page.fields().add(field);
				}
			} else if (qName.equals("PopupPage")) {
				this._inPopup = false;
			} else if (qName.equals("Tag") && this._sortCells) {
				ArrayList<ArchiveField> temp = new ArrayList<ArchiveField>();
				for (int i = this._currentTag; i < this._page.fields().size(); i++) {
					temp.add(this._page.fields().get(i));
				}
				
				if (this._dir.equals("ltr")) {					
					Collections.sort(temp, new Comparator<ArchiveField>() {
			
				        public int compare(ArchiveField o1, ArchiveField o2) {			
				            Float x1 = o1.top();
				            Float x2 = o2.top();
				            int tComp = x1.compareTo(x2);
			
				            if (tComp != 0) {
				               return tComp;
				            } else {
				               x1 = o1.left();
				               x2 = o2.left();
				               return x1.compareTo(x2);
				            }
				        }
			
					});
				} else {
					final CssClassParser classparser = this._classparser;
					Collections.sort(temp, new Comparator<ArchiveField>() {
						
				        public int compare(ArchiveField o1, ArchiveField o2) {			
				            Float x1 = o1.top();
				            Float x2 = o2.top();
				            int tComp = x1.compareTo(x2);
			
				            if (tComp != 0) {
				               return tComp;
				            } else {
				               x1 = o1.left() + classparser.getClasses().get("y" + o1.mdid()).width();
				               x2 = o2.left() + classparser.getClasses().get("y" + o2.mdid()).width();
				               return x2.compareTo(x1);
				            }
				        }
			
					});
				}
				this._currentTag = 0;
			}
		}
	}

	
	public static class ArchivePage {
		private String _mdid = "", _pageType;
		private java.util.ArrayList<ArchiveField> _fields;
		
		public String mdid() {
			return this._mdid;
		}
		
		public String pageType() {
			return this._pageType;
		}
		
		public java.util.ArrayList<ArchiveField> fields() {
			return this._fields;
		}
		
		public ArchivePage(String mdid, String pageType) {
			this._mdid = mdid.replace("|", "");
			this._pageType = pageType;
			this._fields = new java.util.ArrayList<ArchiveField>();
		}
		
		@Override
		public String toString() {
			StringBuilder res = new StringBuilder();
			res.append("Page,").append(this._mdid).append("\n");
			for (ArchiveField field: this._fields) {
				res.append(field.toString());
			}
			return res.toString();
		}
	}
	
	public static class ArchiveField {
		private String _mdid, _contentType, _value, _tagkey;
		private float _left, _top;	
		private int _ordno;
		
		public String mdid() {
			return this._mdid;
		}
		
		public String contentType() {
			return this._contentType;
		}
		
		public String value() {
			return this._value;
		}
		
		public float left() {
			return this._left;
		}
		
		public float top() {
			return this._top;
		}
		
		public String tagkey() {
			return this._tagkey;
		}
		
		public int ordno() {
			return this._ordno;
		}
		
		public boolean hasTagKey() {
			return this._tagkey.length() > 0;
		}
		
		@Override
		public String toString() {
			StringBuilder res = new StringBuilder();
			res.append(this._mdid).append(":").append(this._contentType).append(",").append(Float.toString(this._left))
				.append(",").append(Float.toString(this._top)).append(",").append(this._tagkey).append(",").append(Integer.toString(this._ordno)).append("<").append(this._value).append(">\n");
			return res.toString();
		}
		
		public ArchiveField(String mdid, String contentType, float left, float top, String value, String tagkey, int ordno) {
			this._mdid = mdid;
			this._contentType = contentType;
			this._left = left;
			this._top = top;
			this._value = value;
			this._tagkey = tagkey;
			this._ordno = ordno;
		}
	}
	
	public static void main(String[] args) throws java.io.IOException {
		String xml = Misc.readFile("\\\\formit7\\C$\\Users\\davidz\\Desktop\\littlearchive.xml");
		//System.out.println(xml);
		//System.out.println(parseXml(xml));
		//System.out.println();
		Long startTime = System.nanoTime();
		java.util.ArrayList<ArchivePage> data = getDataSax(xml, null, null, "ltr").pages();
		Long diff = System.nanoTime() - startTime;
		System.out.println(java.util.concurrent.TimeUnit.NANOSECONDS.toMillis(diff));
		for (ArchivePage datum: data) {
			System.out.println(datum);
		}
		startTime = System.nanoTime();
		data = getDataSax(xml, null, null, "ltr").pages();
		diff = System.nanoTime() - startTime;
		System.out.println(java.util.concurrent.TimeUnit.NANOSECONDS.toMillis(diff));
		for (ArchivePage datum: data) {
			//System.out.println(datum);
		}
		startTime = System.nanoTime();
		data = getDataSax(xml, null, null, "ltr").pages();
		diff = System.nanoTime() - startTime;
		System.out.println(java.util.concurrent.TimeUnit.NANOSECONDS.toMillis(diff));
	}

}
