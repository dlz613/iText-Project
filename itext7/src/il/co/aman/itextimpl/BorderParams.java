package il.co.aman.itextimpl;

public class BorderParams {
	
	Border _borderLeft, _borderTop, _borderRight, _borderBottom;
	
	public Border borderLeft() {
		return this._borderLeft;
	}
	
	public Border borderTop() {
		return this._borderTop;
	}
	
	public Border borderRight() {
		return this._borderRight;
	}
	
	public Border borderBottom() {
		return this._borderBottom;
	}
	
	public BorderParams(double CELL_borderLeftWidth, double CELL_borderTopWidth, double CELL_borderRightWidth, double CELL_borderBottomWidth, 
			    String CELL_borderLeftColor, String CELL_borderTopColor, String CELL_borderRightColor, String CELL_borderBottomColor, 
			    String CELL_borderLeftStyle, String CELL_borderTopStyle, String CELL_borderRightStyle, String CELL_borderBottomStyle) {
		this._borderLeft = new Border((float)CELL_borderLeftWidth, CELL_borderLeftColor, CELL_borderLeftStyle);
		this._borderTop = new Border((float)CELL_borderTopWidth, CELL_borderTopColor, CELL_borderTopStyle);
		this._borderRight = new Border((float)CELL_borderRightWidth, CELL_borderRightColor, CELL_borderRightStyle);
		this._borderBottom = new Border((float)CELL_borderBottomWidth, CELL_borderBottomColor, CELL_borderBottomStyle);
	}
	
	public static BorderParams none() {
		return new BorderParams(0, 0, 0, 0, null, null, null, null, null, null, null, null);
	}

}
