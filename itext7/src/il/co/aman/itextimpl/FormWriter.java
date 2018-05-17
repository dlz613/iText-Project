package il.co.aman.itextimpl;

//import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
//import com.itextpdf.text.DocumentException;
//import com.itextpdf.text.Element;
//import com.itextpdf.layout.element.Image;
import com.itextpdf.kernel.geom.Rectangle;
//import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
//import com.itextpdf.text.pdf.PdfAnnotation;
//import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
//import com.itextpdf.text.pdf.PdfAppearance;
//import com.itextpdf.layout.border.Border;
//import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfChoiceFormField;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfTextFormField;
//import com.itextpdf.kernel.pdf.PdfArray;
//import com.itextpdf.kernel.pdf.PdfDictionary;
//import com.itextpdf.kernel.pdf.PdfLiteral;
import com.itextpdf.kernel.pdf.PdfName;
//import com.itextpdf.kernel.pdf.PdfObject;
//import com.itextpdf.kernel.pdf.PdfPrimitiveObject;
//import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
//import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.kernel.pdf.PdfWriter;
//import com.itextpdf.text.pdf.RadioCheckField;
//import com.itextpdf.text.pdf.TextField;
//import com.itextpdf.layout.property.TextAlignment;
//import com.itextpdf.text.pdf.PdfBorderDictionary;
//import com.itextpdf.kernel.pdf.PdfNumber;

import com.itextpdf.kernel.xmp.options.Options;
import com.itextpdf.text.pdf.PdfAnnotation;



//import il.co.aman.apps.*;
import il.co.aman.itextimpl.CssParser.Style;




//import java.awt.Font;
import java.io.BufferedReader;
//import java.io.ByteArrayOutputStream;
import java.io.File;
//import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
//import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.io.IOUtils;

/**
 *
 * @author davidz Modified textbox code, May 2013, to allow saving of filled
 * values.
 */
public class FormWriter {

    PdfWriter writer;
    PdfDocument pdfdoc;
    Exception error;
    GetFonts fontBank;
    Vector<String> fieldnames;
    ////Map<String, Radiogroup> radiofields = new HashMap<String, Radiogroup>();
    Map<String, String> defvals;
    StringBuilder javascript = new StringBuilder();
    float hiddenFields = 0;
    boolean isClearButton, isResetButton, bDebug, date_already;
    static String DATE_JS_BASIC/*, DATE_JS_SPECIFIC, EMAIL_JS_SPECIFIC*/;
    public static final String FILEMARKER = "file://"; // if prepended to the javascript, indicates that it is the path to the file containing the code, not the javascript code itself
    private static final String START_SUBMIT = "this.submitForm({cURL: \"";
    private static final String END_SUBMIT = ", cSubmitAs: \"HTML\", cCharset: \"utf-8\"});";

    public void setDebug(boolean val) {
        this.bDebug = val;
    }

    public Exception getError() {
        return this.error;
    }

    public FormWriter() {
        this.error = null;
        this.fieldnames = new Vector<String>();
        this.defvals = new HashMap<String, String>();
        this.isClearButton = false;
        this.isResetButton = false;
        this.fontBank = new GetFonts(null);
        this.bDebug = false;
        this.date_already = false;
        DATE_JS_BASIC = _getResource("dateValidation.js");
        //DATE_JS_SPECIFIC = _getResource("dateValidation_2.js");
        //EMAIL_JS_SPECIFIC = _getResource("emailValidation.js");
    }

//    /**
//     * for adding a form to a pre-existing PDF
//     *
//     * @param stamper
//     * @param writer
//     */
//    public FormWriter(PdfDocument stamper, PdfWriter writer) {
//        this();
//        this.stamper = stamper;
//        this.writer = writer;
//    }

    /**
     * for creating the form during the creation of the PDF
     *
     * @param writer
     */
    public FormWriter(PdfDocument doc) {
        this();
        this.pdfdoc = doc;
    }

//    /**
//     * For backward compatibility - does not actually do anything.
//     *
//     * @param writer
//     */
//    /*public FormWriter(com.lowagie.text.pdf.PdfWriter writer) {
//        this();
//    }*/
//
//    public boolean close() {
//        // only to be used if the class was instantiated with a PdfStamper object
//        try {
//            this.stamper.close();
//            //this.writer.close();
//            return true;
//        } catch (Exception e) {
//            this.error = e;
//            return false;
//        }
//    }
//
//    public static boolean stampPDF(String basepdf, String outpdf, Vector fields) {
//        return stampPDF(Misc.readBinFile(basepdf), outpdf, fields);
//    }
//
//    public static boolean stampPDF(byte[] basepdf, String outpdf, Vector fields) {
//        try {
//            FileOutputStream fos = new FileOutputStream(outpdf);
//            fos.write(stampPDF(basepdf, fields));
//            fos.close();
//            return true;
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            return false;
//        }
//    }
//
//    public static byte[] stampPDF(byte[] basepdf, Vector fields) {
//        try {
//            PdfReader reader = new PdfReader(new java.io.ByteArrayInputStream(basepdf));
//            ByteArrayOutputStream outpdf = new ByteArrayOutputStream();
//            PdfWriter writer = new PdfWriter(outpdf);
//            PdfDocument stamper = new PdfDocument(reader, writer);
//            FormWriter frmwriter = new FormWriter(stamper, stamper.getWriter());
//            Iterator it = fields.iterator();
//            while (it.hasNext()) {
//                FormParams frmparams = (FormParams) it.next();
//                if (!frmwriter.addField(frmparams)) {
//                    System.out.println("************ Failure processing field: " + frmparams.name + " -- " + frmparams.type);
//                    System.out.println("                         " + frmwriter.error);
//                }
//            }
//            frmwriter.finish();
//            frmwriter.close();
//            return outpdf.toByteArray();
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//            return null;
////        } catch (DocumentException e) {
////            System.out.println(e.getMessage());
////            return null;
//        }
//    }

    /**
     * Fetches an image (or other non-textual) resource from the jar file.
     *
     * @param name The name of the resource (e.g. <code>button.png</code>.
     * @return The full path to the temporary file where the resource will be
     * found.
     */
    public static String imgFromResource(String name) {
        return il.co.aman.apps.Misc.getResourceAsFile(new FormWriter().getClass(), name).getAbsolutePath();
    }

//    public void addfields(Vector fields) {
//        try {
//            Iterator it = fields.iterator();
//            while (it.hasNext()) {
//                FormParams frmparams = (FormParams) it.next();
//                if (!this.addField(frmparams)) {
//                    System.out.println("************ Failure processing field: " + frmparams.name + " -- " + frmparams.type);
//                    System.out.println("                         " + this.error);
//                }
//            }
//        } catch (Exception e) {
//
//        }
//    }
//
//    public void addAnnotation(PdfAnnotation field, int page) {
//        if (this.stamper != null) {
//            this.stamper.addAnnotation(field, page);
//        } else {
//            this.writer.addAnnotation(field);
//        }
//    }
//
//    public PdfFormField getContainer(FormParams params) {
//        try {
//            PdfFormField container = PdfFormField.createEmpty(this.writer);
//            container.setName(params.name);
//            return container;
//        } catch (Exception e) {
//            this.error = e;
//            return null;
//        }
//    }
//
//    public boolean addContainer(PdfFormField container, int page) {
//        try {
//            this.addAnnotation(container, page);
//            return true;
//        } catch (Exception e) {
//            this.error = e;
//            return false;
//        }
//    }
//
    private boolean _addButton(FormParams params) {
        try {
        	PdfButtonFormField btnfield = null;
        	try {
        		btnfield = PdfFormField.createPushButton(this.pdfdoc, new Rectangle(params.xfrom, params.yfrom, params.width(), params.height()), params.name, "Button");
        	}
        	catch (java.lang.NullPointerException npe) {
        		System.out.println("Error creating button " + params.name);
        	}
        	//PdfContentByte cb = _getcb(params.page);
            //Image img = Image.getInstance(params.imgup);
            //PdfAppearance normal = cb.createAppearance(img.getWidth(), img.getHeight());
            //img.setAbsolutePosition(0, 0);
            //normal.addImage(img);

            //img = Image.getInstance(params.imgdown);
            //PdfAppearance down = cb.createAppearance(img.getWidth(), img.getHeight());
            //img.setAbsolutePosition(0, 0);
            //down.addImage(img);

            PdfAction formact;

            if (params.disabled) {
                formact = PdfAction.createJavaScript("");
            } else if (params.action == null) {
                formact = PdfAction.createJavaScript(params.code);
            } else {
                formact = params.action;
            }

            btnfield.setFieldName(params.name);
            btnfield.setAction(formact);
            if (params.additional_action != null) {
            	btnfield.setAdditionalAction(PdfName.X, params.additional_action);
            }
            PdfAcroForm.getAcroForm(pdfdoc, true).addField(btnfield);
            File imgFile = copyFile(params.imgup);
//            btnfield.setAppearance(PdfName.N, "a", new PdfStream(il.co.aman.apps.Misc.readBinFile(imgFile.getAbsolutePath())));
//            btnfield.setAppearance(PdfName.D, "b", new PdfStream(il.co.aman.apps.Misc.readBinFile(imgFile.getAbsolutePath())));
//            btnfield.setAppearance(PdfName.R, "c", new PdfStream(il.co.aman.apps.Misc.readBinFile(imgFile.getAbsolutePath())));
           
//            pushbutton.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, normal);
//            pushbutton.setAppearance(PdfAnnotation.APPEARANCE_DOWN, down);
//            pushbutton.setAppearance(PdfAnnotation.APPEARANCE_ROLLOVER, down);
//            pushbutton.setWidget(new Rectangle(params.xfrom, params.yfrom, params.xtill, params.ytill), PdfAnnotation.HIGHLIGHT_NONE);
//
//            this.addAnnotation(pushbutton, params.page);
//
//            if (params.parent != null) {
//                params.parent.addKid(pushbutton);
//            }
            if (imgFile != null) {
            	btnfield.setImage(imgFile.getAbsolutePath());
            }

            return true;
        } catch (Exception e) {
            this.error = e;
            return false;
        }
    }
    
    private static File copyFile(String path) {
    	try {
    		URL imgURL = new URL(path);
    		InputStream is = imgURL.openStream();
            File temp = File.createTempFile("temp", ".tmp");
            temp.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(temp);
            byte[] content = IOUtils.toByteArray(is);
            is.close();
            fos.write(content);
            fos.close();
            return temp;
    	}
    	catch (IOException e) {
    		return null;
    	}    	
    }

    private boolean _addSubmitButton(FormParams params) {
    	String middle_submit = params.href + "\"";
        params.code = params.preCode + START_SUBMIT + middle_submit + END_SUBMIT;
        try {
        	return _addButton(params);
        } catch (Exception e) {
        	this.error = e;
        	return false;
        }
    }

    private boolean _addResetButton(FormParams params) {
        if (!this.isResetButton) { // only one reset button is allowed
            try {
                //params.code = RESET_FUNCTION + "();";
                params.action = PdfAction.createResetForm(new Object[] {}, 1);
                if (_addButton(params)) {
                    this.isResetButton = true;
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                this.error = e;
                return false;
            }
        } else {
            return false;
        }
    }

//    private boolean _clearButtonCode() {
//        if (!this.fieldnames.isEmpty()) {
//            String code = "function " + CLEAR_FUNCTION + "() {\n";
//            for (String fieldname : this.fieldnames) {
//                code += "this.getField(\"" + fieldname + "\").value = \"\";\n";
//            }
//            code += "}";
//
//            try {
//                FormParams clearbuttonJS = new FormParams();
//                //System.out.println("clearButtonJS");
//                clearbuttonJS.javascript = code;
//                clearbuttonJS.type = FormParams.ElementType.JAVASCRIPT;
//                return this._addJavaScript(clearbuttonJS);
//            } catch (Exception e) {
//                return false;
//            }
//        } else {
//            return true;
//        }
//    }
//
//    private boolean _resetButtonCode() {
//        if (!this.defvals.isEmpty()) {
//            String code = "function " + RESET_FUNCTION + "() {\n";
//            for (Map.Entry pairs : this.defvals.entrySet()) {
//                code += "this.getField(\"" + pairs.getKey() + "\").value = \"" + pairs.getValue() + "\";\n";
//            }
//            code += "}";
//
//            try {
//                FormParams resetbuttonJS = new FormParams();
//                //System.out.println("resetbuttonJS");
//                resetbuttonJS.javascript = code;
//                resetbuttonJS.type = FormParams.ElementType.JAVASCRIPT;
//                return this._addJavaScript(resetbuttonJS);
//            } catch (Exception e) {
//                return false;
//            }
//        } else {
//            return true;
//        }
//    }
    
    private static int alignConvert(Style.Alignment align) {
        int res = PdfFormField.ALIGN_LEFT;
        switch (align) {
        	case LEFT:
        		res = PdfFormField.ALIGN_LEFT;
        		break;
        	case RIGHT:
        		res = PdfFormField.ALIGN_RIGHT;
        		break;
        	case CENTER:
        		res = PdfFormField.ALIGN_CENTER;
        		break;
        	case JUSTIFY:
        	case JUSTIFY_ALL:
        	case NONE:
        }
        return res;
    }

    private boolean _addTextBox(FormParams params) {
    	PdfTextFormField textfield = null;
    	try {
    		textfield = PdfFormField.createText(this.pdfdoc, new Rectangle(params.xfrom, params.yfrom, params.width(), params.height()), params.name);
    	}
    	catch (java.lang.NullPointerException npe) {
    		System.out.println("Error creating textbox " + params.name);
    		return false;
    	}
    	return _configTextBox(params, textfield);
    }
    
    private boolean _configTextBox(FormParams params, PdfTextFormField textfield) {
    	try {
	    	//textfield.makeIndirect(pdfdoc);
//	        if (params.pdffont == null) {
            try {
                textfield.setFontAndSize(fontBank.getFont(params.font), params.fontsize);
            } catch (Exception e) {
            	System.out.println("Exception thrown in _configTextBox() - using default font of Helvetica.");
                textfield.setFontAndSize(fontBank.getFont("Helvetica"), params.fontsize);
            }
//	        } else {
//	            textfield.setFontAndSize(params.pdffont, params.fontsize);
//	            System.out.println("++++++++++++++++++ Using pdffont");
//	        }
	        textfield.setColor(params.fontcolor);
	        if (params.backgroundcolor.equals(new DeviceRgb(255, 255, 255))) { // the border causes the background color to disappear after entering text into the textbox
		        textfield.setBorderColor(params.bordercolor);
		        textfield.setBorderWidth(params.border);
	        }
	        textfield.setBackgroundColor(params.backgroundcolor);
	        textfield.setValue(JavaItext.reverseString(params.defval));
	        textfield.setDefaultValue(new PdfString(JavaItext.reverseString(params.defval)));
	        //textfield.setVisibility(PdfFormField.VISIBLE);
	        //PdfDictionary border = new PdfDictionary();
	        //border.put(PdfName.BorderStyle, new PdfNumber(Border.DOUBLE));
	        //border.put(PdfName.BorderThickness, new PdfNumber(params.border));
	        //textfield.setBorderStyle(border);
	        //PdfArray options = new PdfArray();
	        textfield.setJustification(alignConvert(params.alignment));
	        if (params.type == FormParams.ElementType.HIDDENTEXT) {
	            textfield.setVisibility(PdfTextFormField.HIDDEN);
	        }
	        if (params.type == FormParams.ElementType.EMAILBOX) {
	        	String js = "";
	        	if (!this.date_already) {
	        		js = DATE_JS_BASIC;
	        		this.date_already = true;
	        	}
	        	js += "it3xt1Mpl___checkEmail()";
		        textfield.setAdditionalAction(PdfName.V, PdfAction.createJavaScript(js));
	        }
	        if (params.type == FormParams.ElementType.DATEBOX) {
	        	String js = "";
	        	if (!this.date_already) {
	        		js = DATE_JS_BASIC;
	        		this.date_already = true;
	        	}
	        	js += "it3xt1Mpl___checkDate(\"" + params.format + "\")";
	        	textfield.setAdditionalAction(PdfName.V, PdfAction.createJavaScript(js));
	        }
	
	        PdfAcroForm.getAcroForm(pdfdoc, true).addField(textfield);
	
	        if (params.type != FormParams.ElementType.HIDDENTEXT) {
	            this.fieldnames.add(params.name);
	            this.defvals.put(params.name, params.defval);
	        }
	
	        return true;
	    } catch (Exception e) {
	        this.error = e;
	        System.out.println(e);
	        return false;
	    }   	
    }

    private boolean _addTextArea(FormParams params) {
        //try {
    	PdfTextFormField textareafield = null;
    	try {
    		Rectangle rect = new Rectangle(params.xfrom, params.yfrom, params.width(), params.height());
    		textareafield = PdfFormField.createMultilineText(this.pdfdoc, rect, params.name, "");
    	}
    	catch (java.lang.NullPointerException npe) {
    		System.out.println("Error creating textarea " + params.name);
    	}
    	return _configTextBox(params, textareafield);
    	//textfield.makeIndirect(pdfdoc);
        /*if (params.pdffont == null) {
            try {
                textareafield.setFont(fontBank.getFont(params.font));
            } catch (Exception e) {
                textareafield.setFont(fontBank.getFont("Helvetica"));
            }
        } else {
            textareafield.setFont(params.pdffont);
        }
        textareafield.setFontSize(params.fontsize);
        textareafield.setColor(params.fontcolor);
        textareafield.setBackgroundColor(params.backgroundcolor);
        //textfield.setBorderColor(params.bordercolor);
        //textfield.setDefaultValue(new PdfLiteral(params.defval));
        textareafield.setValue(params.defval);
        //textfield.setBorderWidth(params.border);
        //textfield.setBorderStyle(params.borderstyle); // doesn't work yet.
        textareafield.setJustification(alignConvert(params.alignment));
        if (params.type == FormParams.ElementType.HIDDENTEXT) {
            textareafield.setVisibility(PdfTextFormField.HIDDEN);
        }

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfdoc, true);
        form.addField(textareafield);

        //this._setDefaultValue(params.name); // this was removed to cause Adobe Reader to display the changed value after the form is saved and reopened
        if (params.type != FormParams.ElementType.HIDDENTEXT) {
            this.fieldnames.add(params.name);
            this.defvals.put(params.name, params.defval);
        }

        return true;
        } catch (Exception e) {
            this.error = e;
            System.out.println(e);
            return false;
        }*/
    }

    private boolean _addInvisibleTextBox(FormParams params) {
        // adds a hidden text box (for sending invariable parameters)
        FormParams fixedParams = new FormParams();
        fixedParams.name = params.name;
        fixedParams.defval = params.defval;
        fixedParams.xfrom = params.xfrom;
        fixedParams.yfrom = params.yfrom;
        fixedParams.xtill = params.xtill;
        fixedParams.ytill = params.ytill;
        fixedParams.font = "Helvetica";
        fixedParams.fontsize = 10;
        fixedParams.fontcolor = ColorConstants.BLACK;
        fixedParams.border = 0f;
        fixedParams.bordercolor = ColorConstants.WHITE;
        fixedParams.backgroundcolor = ColorConstants.WHITE;
        fixedParams.page = 1;
        fixedParams.alignment = Style.Alignment.LEFT;
        fixedParams.type = FormParams.ElementType.HIDDENTEXT;

        return _addTextBox(fixedParams);
    }

//    private boolean _addJavaScript(FormParams params) {
//        try {
//        	String JS = params.javascript;
//            if (params.javascript.startsWith(FILEMARKER)) {
//                File fileJS = new File(params.javascript.substring(FILEMARKER.length()));
//                FileInputStream readJS = new FileInputStream(fileJS);
//                byte[] JSb = new byte[(int) fileJS.length()];
//                readJS.read(JSb);
//                readJS.close();
//                JS = new String(JSb);
//                //this.javascript.append(JS + "\n");
//                //this.writer.addJavaScript(JS);
//            }
//            //this.writer.addJavaScript(JS);
//            return true;
//        } catch (IOException e) {
//            this.error = e;
//            return false;
//        }
//    }

//    /**
//     * @deprecated Use <code>addField</code><br>
//     * retained for backwards-compatability<br>NOTE: Informatica takes the
//     * deprecated warning as an error, and causes the compilation to fail.
//     * @param params
//     * @return
//     */
//    public boolean addJavaScript(FormParams params) {
//        try {
//            if (params.javascript.startsWith(FILEMARKER)) {
//                File fileJS = new File(params.javascript.substring(FILEMARKER.length()));
//                FileInputStream readJS = new FileInputStream(fileJS);
//                byte[] JSb = new byte[(int) fileJS.length()];
//                readJS.read(JSb);
//                readJS.close();
//                String JS = new String(JSb);
//                //this.javascript.append(JS + "\n");
//                this.writer.addJavaScript(JS);
//            } else {
//                //this.javascript.append(params.javascript + "\n");
//                this.writer.addJavaScript(params.javascript);
//            }
//
//            return true;
//        } catch (IOException e) {
//            this.error = e;
//            return false;
//        }
//    }
//
//    private boolean _setDefaultValue(String name) {
//        // adds javascript to set the default values of fields
//        // PROBLEM: this causes the default value to be restored when opening the PDF in Adobe Reader (even if a different value was previously saved).
//        try {
//            FormParams javascriptparams = new FormParams();
//            System.out.println("_setDefaultValue.javascriptparams");
//            javascriptparams.javascript = "this.getField(\"" + name + "\").value = this.getField(\"" + name + "\").defaultValue;";
//            javascriptparams.type = FormParams.ElementType.JAVASCRIPT;
//            return this._addJavaScript(javascriptparams);
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    private boolean _setDefaultRadioValue(String group, int order) {
//        // adds javascript to set the default value of a radiobutton group
//        // PROBLEM: this causes the default value to be restored when opening the PDF in Adobe Reader (even if a different value was previously saved).
//        try {
//            FormParams javascriptparams = new FormParams();
//            System.out.println("_setDefaultRadioValue.javascriptparams");
//            javascriptparams.javascript = "this.getField(\"" + group + "\").checkThisBox(" + String.valueOf(order) + ", true);";
//            javascriptparams.type = FormParams.ElementType.JAVASCRIPT;
//            return this._addJavaScript(javascriptparams);
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
    private boolean _addComboBox(FormParams params) {
        try {
            String[] submits;
            if ("".equals(params.submit_options)) {
                submits = params.display_options.split(";");
            } else {
                submits = params.submit_options.split(";");
            }
            String[] displays = params.display_options.split(";");

            int len = Math.min(displays.length, submits.length);

            String[][] options = new String[len][len];
            for (int i = 0; i < len; i++) {
                options[i][0] = submits[i];
                options[i][1] = displays[i];
            }
            PdfChoiceFormField choicefield = PdfFormField.createComboBox(
            		this.pdfdoc, new Rectangle(params.xfrom, params.yfrom, params.width(), params.height()),
                params.name, params.def_selection > -1? submits[params.def_selection]: "", options);
	        if (params.backgroundcolor.equals(new DeviceRgb(255, 255, 255))) { // the border causes the background color to disappear after entering text into the textbox
	        	choicefield.setBorderColor(params.bordercolor);
	        	choicefield.setBorderWidth(params.border);
	        }
	        choicefield.setBackgroundColor(params.backgroundcolor);
	        if (params.def_selection > -1) {
	        	choicefield.setValue(submits[params.def_selection], displays[params.def_selection]);
	        }

	        PdfAcroForm.getAcroForm(pdfdoc, true).addField(choicefield);

            return true;
        } catch (Exception e) {
            this.error = e;
            return false;
        }
    }

    private boolean _addCheckBox(FormParams params) {
        try {
        	PdfButtonFormField checkfield = PdfFormField.createCheckBox(this.pdfdoc, new Rectangle(params.xfrom, params.yfrom, params.width(), params.height()),
                params.name, params.default_selected ? "Yes": "Off", params.checktype);
            //checkfield.setBackgroundColor(params.backgroundcolor);
            PdfCanvas canvas = new PdfCanvas(this.pdfdoc.getLastPage());
            canvas.rectangle(params.xfrom, params.yfrom, params.width(), params.height());
            canvas.stroke();
            if (params.disabled) {
                checkfield.setReadOnly(true);
            }

	        PdfAcroForm.getAcroForm(pdfdoc, true).addField(checkfield);

            return true;
        } catch (Exception e) {
            this.error = e;
            return false;
        }
    }

    private boolean _addDateBox(FormParams params) {
        try {
            if ("".equals(params.defval)) { // if there isn't display text, show an example of the date format
                if ("ddmmyyyy".equals(params.format)) {
                    params.defval = "18/10/2009";
                } else if ("mmddyyyy".equals(params.format)) {
                    params.defval = "10/18/2009";
                } else if ("yyyymmdd".equals(params.format)) {
                    params.defval = "2009/10/18";
                } else {
                    params.defval = "18/10/2009";
                }
            }
            return _addTextBox(params);
        } catch (Exception e) {
            this.error = e;
            return false;
        }
    }

    private boolean _addEmailBox(FormParams params) {
        try {
            return _addTextBox(params);
        } catch (Exception e) {
            this.error = e;
            return false;
        }
    }

//    private Radiogroup _addRadioField(String name) {
//        try {
//            if (this.radiofields.containsKey(name)) { // if the group already exists, return it
//                this.radiofields.get(name).increment();
//                return this.radiofields.get(name);
//            } else { // create a new group
//                PdfFormField radiofield = PdfFormField.createRadioButton(this.writer, true);
//                radiofield.setFieldName(name);
//                Radiogroup group = new Radiogroup(radiofield);
//                this.radiofields.put(name, group);
//
//                return group;
//            }
//        } catch (Exception e) {
//            this.error = e;
//            return null;
//        }
//    }
//
//    private boolean _addRadioButton(FormParams params) {
//        final Float BUTTONSIZE = 10f;
//        try {
//            Radiogroup radiogroup = _addRadioField(params.group);
//            if (this.bDebug) {
//                System.out.println("***Formwriter: radiofield added.");
//            }
//            Float xstart = 0f;
//            Float xend = 0f;
//            Float textstart = 0f;
//            switch (params.text_location) {
//                case RIGHT:
//                    xstart = params.xfrom;
//                    xend = xstart + BUTTONSIZE;
//                    textstart = params.xfrom + BUTTONSIZE * 1.5f;
//                    break;
//                case LEFT:
//                    xend = params.xtill;
//                    xstart = xend - BUTTONSIZE;
//                    textstart = params.xfrom;
//                    break;
//            }
//            switch (params.alignment) {
//                case RIGHT:
//                    textstart = params.text_location == FormParams.TextLocation.RIGHT ? params.xtill : params.xtill - BUTTONSIZE * 1.5f;
//                    break;
//                case LEFT:
//                    break;
//                case CENTER:
//                    textstart += Math.abs(params.xtill - textstart) / 2;
//                    break;
//            }
//            Rectangle rect = new Rectangle(xstart, params.yfrom, xend, params.yfrom + BUTTONSIZE);
//            _addRadio(this.writer, rect, radiogroup.getGroup(), radiogroup.getIndex(), params.group,
//                    params.name, params.value, params.default_selected, params.bordercolor, params.disabled);
//            if (this.bDebug) {
//                System.out.println("***FormWriter: Radiobutton " + params.name + " added to document.");
//            }
//
//            PdfContentByte cb = _getcb(params.page);
//            cb.beginText();
//            cb.setFontAndSize(params.basefont, params.fontsize);
//            cb.showTextAligned(params.alignment, params.display, textstart, params.yfrom, 0);
//            cb.endText();
//            return true;
//        } catch (Exception e) {
//            this.error = e;
//            return false;
//        }
//    }
//
//    private void _addRadio(PdfWriter writer, Rectangle rect, PdfFormField radio, int index,
//            String group, String name, String value, boolean on, Color bordercolor, boolean disabled) throws IOException {
//        RadioCheckField check = new RadioCheckField(writer, rect, null, name);
//        check.setCheckType(RadioCheckField.TYPE_CIRCLE);
//        check.setBorderColor(bordercolor);
//        check.setOnValue(value);
//        check.setChecked(on);
//        if (on) {
//            radio.setDefaultValue(new PdfString(value));
//            //setDefaultRadioValue(group, index);
//        }
//        if (disabled) {
//            check.setOptions(RadioCheckField.READ_ONLY);
//        }
//        radio.addKid(check.getRadioField());
//    }
//
//    private PdfContentByte _getcb(Integer page) {
//        PdfContentByte cb;
//        if (this.stamper != null) {
//            cb = this.stamper.getOverContent(page);
//        } else {
//            cb = this.writer.getDirectContent();
//        }
//        return cb;
//    }
//
//    private void _addRadioFields() {
//        for (Map.Entry pairs : this.radiofields.entrySet()) {
//            Radiogroup group = (Radiogroup) pairs.getValue();
//            this.addAnnotation(group.getGroup(), 1);
//        }
//    }
//
    public boolean addField(FormParams params) {
        boolean res = false;
        switch (params.type) {
            case BUTTON:
                res = _addButton(params);
                break;
            case SUBMITBUTTON:
                res = _addSubmitButton(params);
                break;
            case CLEARBUTTON:
                //res = _addClearButton(params);
                break;
            case RESETBUTTON:
                res = _addResetButton(params);
                break;
            case TEXT:
                res = _addTextBox(params);
                break;
            case TEXTAREA:
            	res = _addTextArea(params);
            case JAVASCRIPT:
                //res = _addJavaScript(params);
                break;
            case COMBO:
                res = _addComboBox(params);
                break;
            case HIDDENTEXT:
                res = _addInvisibleTextBox(params);
                break;
            case CHECKBOX:
                res = _addCheckBox(params);
                break;
            case DATEBOX:
                res = _addDateBox(params);
                break;
            case EMAILBOX:
                res = _addEmailBox(params);
                break;
            case RADIOBUTTON:
                //res = _addRadioButton(params);
                break;
        }
        return res;
    }

    private String _getResource(String name) {
        InputStream is = getClass().getResourceAsStream(name);
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        String line;
        StringBuilder sb = new StringBuilder();
        try {
            while ((line = br.readLine()) != null) {
                sb.append(line).append(System.getProperty("line.separator"));
            }
        } catch (IOException e) {
            return null;
        }
        return sb.toString();
    }

//    private class Radiogroup {
//
//        private PdfFormField group;
//        private int index;
//
//        private PdfFormField getGroup() {
//            return this.group;
//        }
//
//        private int getIndex() {
//            return this.index;
//        }
//
//        private void increment() {
//            this.index++;
//        }
//
//        public Radiogroup(PdfFormField group) {
//            this.group = group;
//            this.index = 0;
//        }
//    }
//
//    public static void main(String[] args) {
////        ClassLoader cl = ClassLoader.getSystemClassLoader();
////
////        java.net.URL[] urls = ((java.net.URLClassLoader) cl).getURLs();
////
////        for (java.net.URL url : urls) {
////            System.out.println(url.getFile());
////        }
//        //System.exit(0);
//        //System.out.println(Misc.getClassLocation("javax.xml.parsers.DocumentBuilderFactory"));
//        //System.exit(0);
//        try {
//            Vector<FormParams> fields = new Vector<FormParams>();
//            fields.add(FormParams.getBtnParams("btn", "dispFields();",
//                    Misc.getResourceAsFile(new FormParams().getClass(), "button.png").getAbsolutePath(),
//                    Misc.getResourceAsFile(new FormParams().getClass(), "button_down.png").getAbsolutePath(),
//                    300f, 400f, 330f, 370f));
//            fields.add(FormParams.getJSparams(Misc.getResource(new FormParams().getClass(), "debug.js")));
//            stampPDF("C:\\Users\\davidz\\Desktop\\a.pdf", "C:\\Users\\davidz\\Desktop\\aa.pdf", fields);
//        } catch (Exception e) {
//            System.out.println(Misc.message(e));
//        }
//    }
}
