package il.co.aman.itextimpl;

public class HiddenText {
	
	private String /*_cust, _docno, _infopass, */_text, _fontFamily;
	private float _left, _top, _width, _height;
	
//	public String cust() {
//		return this._cust;
//	}
//	
//	public String docno() {
//		return this._docno;
//	}
//	
//	public String infopass() {
//		return this._infopass;
//	}
	
	public String text() {
		return this._text;
	}
	
	public String fontFamily() {
		return this._fontFamily;
	}
	
	public float left() {
		return this._left;
	}
	
	public float top() {
		return this._top;
	}
	
	public float width() {
		return this._width;
	}
	
	public float height() {
		return this._height;
	}
	
//	public String getText() {
//		return this._cust + "," + this._docno + "," + this._infopass;
//	}

	public HiddenText(/*String cust, String docno, String infopass, */String text, String fontFamily, float left, float top, float width, float height) {
//		this._cust = cust;
//		this._docno = docno;
//		this._infopass = infopass;
		this._text = text;
		this._fontFamily = fontFamily;
		this._left = left;
		this._top = top;
		this._width = width;
		this._height = height;
	}

}
