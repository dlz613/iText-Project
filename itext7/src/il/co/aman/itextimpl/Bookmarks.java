package il.co.aman.itextimpl;

import java.io.IOException;
import java.util.HashMap;

import com.itextpdf.kernel.pdf.PdfOutline;

public class Bookmarks {
	
	private HashMap<String, Bookmark> _bookmarks = new HashMap<String, Bookmark>();
	
	public HashMap<String, Bookmark> bookmarks() {
		return this._bookmarks;
	}
	
	public void addBookmark(String tagkey, Bookmark bookmark) {
		if (bookmark.parent() == null && this.hasBookmark(bookmark.parentkey()) && this.getBookmark(bookmark.parentkey()).self() != null) {
			bookmark.setParent(this.getBookmark(bookmark.parentkey()).self());
		}
		this._bookmarks.put(tagkey, bookmark);
	}
	
	public boolean hasBookmark(String tagkey) {
		return this._bookmarks.containsKey(tagkey);
	}
	
	public Bookmark getBookmark(String tagkey) {
		return this._bookmarks.get(tagkey);
	}
	
	@Override
	public String toString() {
		StringBuilder res = new StringBuilder();
		for (String tagkey: this._bookmarks.keySet()) {
			res.append(this._bookmarks.get(tagkey)).append("\n");
		}
		return res.toString();
	}

	public static class Bookmark {
		
		private int _level;
		private String _title, _parentkey;
		private PdfOutline _parent, _self;
		
		public int level() {
			return this._level;
		}		
		
		public PdfOutline parent() {
			return this._parent;
		}
		
		public PdfOutline self() {
			return this._self;
		}
		
		public String title() {
			return this._title;
		}
		
		public String parentkey() {
			return this._parentkey;
		}
		
		public void setParent(PdfOutline parent) {
			this._parent = parent;
			this._self = parent.addOutline(this._title);
		}
		
		public Bookmark(int level, String title, String parentkey, PdfOutline parent) {
			this._level = level;
			this._title = title;
			this._parentkey = parentkey;
			this._parent = parent;
			if (this._parent != null) {
				this._self = parent.addOutline(this._title);
			}
		}
		
		@Override
		public String toString() {
			StringBuilder res = new StringBuilder();
			res.append("Level ").append(Integer.toString(this._level)).append(", Title: ").append(this.title())
					.append(", Parent Tag Key: ").append(this.parentkey()).append(", Parent: ").append(this._parent != null);
			return res.toString();
		}
		
	}
	
	public static void main(String[] args) throws IOException {
		Bookmarks bookmarks = new Bookmarks();
		Bookmark bookmark = new Bookmark(1, "That's all folks!", "K12", null);
		bookmarks.addBookmark("K000", bookmark);
		bookmark = new Bookmark(2, "Guombl!", "K000", null);
		bookmarks.addBookmark("1234", bookmark);
		System.out.println(bookmarks);
	}
}
