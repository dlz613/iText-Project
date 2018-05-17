package il.co.aman.itextimpl;

public class FormContentParams {

	public String CONTENT_TYPE, visible, CELL_form_name, CELL_CONTENT, CELL_form_border_style, CELL_FONT, CELL_color, CELL_form_border_color, CELL_backgroundColor,
		CELL_HAlign, CELL_button_action, IMG_PATH, JAVA_SCRIPT, CELL_list_value, CELL_form_default_selected, CELL_form_date_format, CELL_form_radiobutton_name;
	public double CELL_FONTSIZE, CELL_form_border_width, left, top, cellWidth, cellHeight;
	
	/**
	 * Defaults:<br>
	 * <pre>this.visible = "true";
this.CELL_FONT = "Arial";
this.CELL_color = "0,0,0";
this.CELL_backgroundColor = "255,255,255";
this.CELL_HAlign = "Left";
this.CELL_FONTSIZE = 12.0;</pre>
	 */
	public FormContentParams() {
		this.visible = "true";
		this.CELL_FONT = "Arial";
		this.CELL_color = "0,0,0";
		this.CELL_backgroundColor = "255,255,255";
		this.CELL_HAlign = "Left";
		this.CELL_FONTSIZE = 12.0;
	}
}
