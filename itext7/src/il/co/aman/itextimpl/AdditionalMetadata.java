package il.co.aman.itextimpl;

import java.io.IOException;
import java.util.HashMap;

import org.xml.sax.Attributes;

import il.co.aman.apps.Misc;
import il.co.aman.formit.GenericSAX;

public class AdditionalMetadata extends GenericSAX {

	static protected final String ACCESS_MD_TYPE = "ACCESS_MD_TYPE";
	static protected final String BARCODE_CHARSET = "BARCODE_CHARSET";
	static protected final String BUTTON_ACTION = "BUTTON_ACTION";
	static protected final String CELL_BAR_WIDTH = "CELL_BAR_WIDTH";
	static protected final String CELL_BARCODE_TYPE = "CELL_BARCODE_TYPE";
	static protected final String CELL_HALIGN = "CELL_HALIGN";
	static protected final String CELL_JUSTIFY_SPACING = "CELL_JUSTIFY_SPACING";
	static protected final String CELL_SORTING = "CELL_SORTING";
	static protected final String CELL_TEXT_BACKGROUND = "CELL_TEXT_BACKGROUND";
	static protected final String CELL_TEXT_VISIBLE = "CELL_TEXT_VISIBLE";
	static protected final String COMBO_LIST = "COMBO_LIST";
	static protected final String DATE_FORMAT = "DATE_FORMAT";
	static protected final String FIELD_NAME = "FIELD_NAME";
	static protected final String FORM_URL = "FORM_URL";
	static protected final String GO_XSLT = "GO_XSLT";
	static protected final String HEADER_LEVEL = "HEADER_LEVEL";
	static protected final String LI_END_IND = "LI_END_IND";
	static protected final String LI_START_IND = "LI_START_IND";
	static protected final String LIST_END_IND = "LIST_END_IND";
	static protected final String LIST_START_IND = "LIST_START_IND";
	static protected final String METADATA = "metadata";
	static protected final String METADATUM = "metadatum";
	static protected final String OBJECT_CONTENT = "OBJECT_CONTENT";
	static protected final String OBJECT_DESCR = "OBJECT_DESCR";
	static protected final String OBJECT_ID = "OBJECT_ID";
	static protected final String PARA_END_IND = "PARA_END_IND";
	static protected final String PARA_START_IND = "PARA_START_IND";
	static protected final String QR_CHARSET = "QR_CHARSET";
	static protected final String TABLE_DESCR = "TABLE_DESCR";
	static protected final String TABLE_IND = "TABLE_IND";
	static protected final String VISIBLE = "VISIBLE";
	static protected final String WORD_INSERT_PDF = "WORD_INSERT_PDF";
	static protected final String WORD_INSERT_TYPE = "WORD_INSERT_TYPE";
	static protected final String DEFAULT_SELECTED = "DEFAULT_SELECTED";
	
	private final String ID = "id";
	
	private String _id, _cellTextBackground = "#FFFFFF", _cellHalign = "Left", _cellBarcodeType = "1", _cellTextVisible ="1", 
			_barcodeCharset = "utf-8", _qrCharset = "utf-8", _cellBarWidth = "0.8",_wordInsertType = "PDF", _wordInsertPdf = "", 
			_cellJustifySpacing = "0.0", _paraStartInd = "no", _paraEndInd = "no", _listStartInd = "no", _listEndInd = "no", 
			_liStartInd = "no", _liEndInd = "no", _accessMdType = "", _headerLevel = "0", _tableInd = "N", _tableDescr = "", _objectContent = "", 
			_objectId = "0", _objectDescr = "", _cellSorting = "0", _goXslt = "", _fieldName = "", _formUrl = "", _buttonAction = "", _dateFormat = "ddmmyyyy",
			_comboList = "", _visible = "true", _defaultSelected = "0";
	private HashMap<String, Metadata> _metadata;
	
	public HashMap<String, Metadata> metadata() {
		return this._metadata;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		if (qName.equals(METADATA)) {
			this._metadata = new HashMap<String, Metadata>();
		} else if (qName.equals(METADATUM)) {
			this._id = attributes.getValue(ID);
			this._buttonAction = "";
			this._cellSorting = "0";
			this._dateFormat = "ddmmyyyy";
			this._fieldName = "";
			this._objectDescr = "";
			this._comboList = "";
			this._visible = "true";
			this._defaultSelected = "0";
		} 
		this.text().setLength(0);
	}

	@Override
	public void endElement(String uri, String localName, String qName) {
		if (qName.equals(CELL_TEXT_BACKGROUND)) {
			this._cellTextBackground = this.text().toString();
		} else if (qName.equals(CELL_HALIGN)) {
			this._cellHalign = this.text().toString();
		} else if (qName.equals(CELL_BARCODE_TYPE)) {
			this._cellBarcodeType = this.text().toString();
		} else if (qName.equals(CELL_TEXT_VISIBLE)) {
			this._cellTextVisible = this.text().toString();
		} else if (qName.equals(BARCODE_CHARSET)) {
			this._barcodeCharset = this.text().toString();
		} else if (qName.equals(QR_CHARSET)) {
			this._qrCharset = this.text().toString();
		} else if (qName.equals(CELL_BAR_WIDTH)) {
			this._cellBarWidth = this.text().toString();
		} else if (qName.equals(WORD_INSERT_TYPE)) {
			this._wordInsertType = this.text().toString();
		} else if (qName.equals(WORD_INSERT_PDF)) {
			this._wordInsertPdf = this.text().toString();
		} else if (qName.equals(CELL_JUSTIFY_SPACING)) {
			this._cellJustifySpacing = this.text().toString();
		} else if (qName.equals(PARA_START_IND)) {
			this._paraStartInd = this.text().toString();
		} else if (qName.equals(PARA_END_IND)) {
			this._paraEndInd = this.text().toString();
		} else if (qName.equals(LIST_START_IND)) {
			this._listStartInd = this.text().toString();
		} else if (qName.equals(LIST_END_IND)) {
			this._listEndInd = this.text().toString();
		} else if (qName.equals(LI_START_IND)) {
			this._liStartInd = this.text().toString();
		} else if (qName.equals(LI_END_IND)) {
			this._liEndInd = this.text().toString();
		} else if (qName.equals(ACCESS_MD_TYPE)) {
			this._accessMdType = this.text().toString();
		} else if (qName.equals(HEADER_LEVEL)) {
			this._headerLevel = this.text().toString();
		} else if (qName.equals(TABLE_IND)) {
			this._tableInd = this.text().toString();
		} else if (qName.equals(TABLE_DESCR)) {
			this._tableDescr = this.text().toString();
		} else if (qName.equals(OBJECT_CONTENT)) {
			this._objectContent = this.text().toString();
		} else if (qName.equals(OBJECT_ID)) {
			this._objectId = this.text().toString();
		} else if (qName.equals(OBJECT_DESCR)) {
			this._objectDescr = this.text().toString();
		} else if (qName.equals(CELL_SORTING)) {
			this._cellSorting = this.text().toString();
		} else if (qName.equals(GO_XSLT)) {
			this._goXslt = this.text().toString();
		} else if (qName.equals(FIELD_NAME)) {
			this._fieldName = this.text().toString();
		} else if (qName.equals(FORM_URL)) {
			this._formUrl = this.text().toString();
		} else if (qName.equals(BUTTON_ACTION)) {
			this._buttonAction = this.text().toString();
		} else if (qName.equals(DATE_FORMAT)) {
			this._dateFormat = this.text().toString();
		} else if (qName.equals(COMBO_LIST)) {
			this._comboList = this.text().toString();
		} else if (qName.equals(VISIBLE)) {
			this._visible = this.text().toString();
		} else if (qName.equals(DEFAULT_SELECTED)) {
			this._defaultSelected = this.text().toString();
		} else if (qName.equals(METADATUM)) {
			Metadata metadata = new Metadata(this._id, this._cellTextBackground, this._cellHalign, this._cellBarcodeType,
					this._cellTextVisible, this._barcodeCharset, this._qrCharset, this._cellBarWidth, this._wordInsertType, this._wordInsertPdf,
					this._cellJustifySpacing, this._paraStartInd, this._paraEndInd, this._listStartInd, this._listEndInd, this._liStartInd, this._liEndInd,
					this._accessMdType, this._headerLevel, this._tableInd, this._tableDescr, this._objectContent, this._objectId, this._objectDescr, this._cellSorting,
					this._goXslt, this._fieldName, this._formUrl, this._buttonAction, this._dateFormat, this._comboList, this._visible, this._defaultSelected);
			this._metadata.put(this._id, metadata);
		}
	}
	
    public static Object[] parse(String XML) {
        AdditionalMetadata additionalMetadata = new AdditionalMetadata();
        return parse(XML, additionalMetadata);
    }
    
    private static Object[] parse(String XML, AdditionalMetadata additionalMetadata) {
        Exception res = il.co.aman.formit.GenericSAX.parseEx(additionalMetadata, XML, false);
        if (res == null) {
            return new Object[]{additionalMetadata, null};
        } else {
            return new Object[]{null, res};
        }
    }
    
    public static HashMap<String, Metadata> parseMetadata(String xml) {
    	Object[] res = parse(xml);
    	if (res[0] != null) {
    		return ((AdditionalMetadata)res[0]).metadata();
    	} else {
    		System.out.println("AdditionalMetadata error: " + (Exception)res[1]);
    		return null;
    	}
    }
    
    public static class Metadata {
    	private String _id, _cellTextBackground, _cellHalign, _cellBarcodeType, _cellTextVisible, _barcodeCharset, _qrCharset, _cellBarWidth,
			_wordInsertType, _cellJustifySpacing, _paraStartInd, _paraEndInd, _listStartInd, _listEndInd, _liStartInd, _liEndInd,
			_accessMdType, _headerLevel, _tableInd, _tableDescr, _objectContent, _objectId, _objectDescr, _cellSorting, _goXslt, _fieldName,
			_formUrl, _buttonAction, _dateFormat, _comboList, _visible, _defaultSelected;
    	private byte[] _wordInsertPdf;
    	
    	public String id() {
    		return this._id;
    	}
    	
    	public String cellTextBackground() {
    		return this._cellTextBackground;
    	}
    	
    	public String cellHalign() {
    		return this._cellHalign;
    	}
    	
    	public String cellBarcodeType() {
    		return this._cellBarcodeType;
    	}
    	
    	public String cellTextVisible() {
    		return this._cellTextVisible;
    	}
    	
    	public String barcodeCharset() {
    		return this._barcodeCharset;
    	}
    	
    	public String qrCharset() {
    		return this._qrCharset;
    	}
    	
    	public String cellBarWidth() {
    		return this._cellBarWidth;
    	}
    	
    	public String wordInsertType() {
    		return this._wordInsertType;
    	}
    	
    	public byte[] wordInsertPdf() {
    		return this._wordInsertPdf;
    	}
    	
    	public String cellJustifySpacing() {
    		return this._cellJustifySpacing;
    	}
    	
    	public String paraStartInd() {
    		return this._paraStartInd;
    	}
    	
    	public String paraEndInd() {
    		return this._paraEndInd;
    	}
    	
    	public String listStartInd() {
    		return this._listStartInd;
    	}
    	
    	public String listEndInd() {
    		return this._listEndInd;
    	}
    	
    	public String liStartInd() {
    		return this._liStartInd;
    	}
    	
    	public String liEndInd() {
    		return this._liEndInd;
    	}
    	
    	public String accessMdType() {
    		return this._accessMdType;
    	}
    	
    	public String headerLevel() {
    		return this._headerLevel;
    	}
    	
    	public String tableInd() {
    		return this._tableInd;
    	}
    	
    	public String tableDescr() {
    		return this._tableDescr;
    	}
    	
    	public String objectContent() {
    		return this._objectContent;
    	}
    	
    	public String objectId() {
    		return this._objectId;
    	}
    	
    	public String objectDescr() {
    		return this._objectDescr;
    	}
    	
    	public String cellSorting() {
    		return this._cellSorting;
    	}
    	
    	public String goXslt() {
    		return this._goXslt;
    	}
    	
    	public String fieldName() {
    		return this._fieldName;
    	}
    	
    	public String formUrl() {
    		return this._formUrl;
    	}
    	
    	public String buttonAction() {
    		return this._buttonAction;
    	}
    	
    	public String dateFormat() {
    		return this._dateFormat;
    	}
    	
    	public String comboList() {
    		return this._comboList;
    	}
    	
    	public String visible() {
    		return this._visible;
    	}
    	
    	public String defaultSelected() {
    		return this._defaultSelected;
    	}
    	
    	public Metadata(String id, String cellTextBackground, String cellHalign, String cellBarcodeType, 
    			String cellTextVisible, String barcodeCharset, String qrCharset, String cellBarWidth, String wordInsertType, String wordInsertPdfStr,
    			String cellJustifySpacing, String paraStartInd, String paraEndInd, String listStartInd, String listEndInd,  String liStartInd, String liEndInd,
    			String accessMdType, String headerLevel, String tableInd, String tableDescr, String objectContent, String objectId, String objectDescr, String cellSorting,
    			String goXslt, String fieldName, String formUrl, String buttonAction, String dateFormat, String comboList, String visible, String defaultSelected) {
    		this._id = id;
    		this._cellTextBackground = cellTextBackground;
    		this._cellHalign = cellHalign;
    		this._cellBarcodeType = cellBarcodeType;
    		this._cellTextVisible = cellTextVisible;
    		this._barcodeCharset = barcodeCharset;
    		this._qrCharset = qrCharset;
    		this._cellBarWidth = cellBarWidth;
    		this._wordInsertType = wordInsertType;
    		this._wordInsertPdf = il.co.aman.apache.commons.codec.binary.Base64.decodeBase64(wordInsertPdfStr);
    		this._cellJustifySpacing = cellJustifySpacing;
    		this._paraStartInd = paraStartInd;
    		this._paraEndInd = paraEndInd;
    		this._listStartInd = listStartInd;
    		this._listEndInd = listEndInd;
    		this._liStartInd = liStartInd;
    		this._liEndInd = liEndInd;
    		this._accessMdType = accessMdType;
    		this._headerLevel = headerLevel;
    		this._tableInd = tableInd;
    		this._tableDescr = tableDescr;
    		this._objectContent = objectContent;
    		this._objectId = objectId;
    		this._objectDescr = objectDescr;
    		this._cellSorting = cellSorting;
    		this._goXslt = goXslt;
    		this._fieldName = fieldName;
    		this._formUrl = formUrl;
    		this._buttonAction = buttonAction;
    		this._dateFormat = dateFormat;
    		this._comboList = comboList;
    		this._visible = visible;
    		this._defaultSelected = defaultSelected;
    	}
    	
    	@Override
    	public String toString() {
    		StringBuilder res = new StringBuilder();
    		res.append(startEl(METADATUM, new String[] {"id"}, new String[] {this.id()}));
    		res.append(el(CELL_TEXT_BACKGROUND, this.cellTextBackground()));
    		res.append(el(CELL_HALIGN, this.cellHalign()));
    		res.append(el(CELL_BARCODE_TYPE, this.cellBarcodeType()));
    		res.append(el(CELL_TEXT_VISIBLE, this.cellTextVisible()));
    		res.append(el(BARCODE_CHARSET, this.barcodeCharset()));
    		res.append(el(QR_CHARSET, this.qrCharset()));
    		res.append(el(CELL_BAR_WIDTH, this.cellBarWidth()));
    		res.append(el(WORD_INSERT_TYPE, this.wordInsertType()));
    		res.append(el(WORD_INSERT_PDF, new String(il.co.aman.apache.commons.codec.binary.Base64.encodeBase64(this._wordInsertPdf))));
    		res.append(el(CELL_JUSTIFY_SPACING, this.cellJustifySpacing()));
    		res.append(el(PARA_START_IND, this.paraStartInd()));
    		res.append(el(PARA_END_IND, this.paraEndInd()));
    		res.append(el(LIST_START_IND, this.listStartInd()));
    		res.append(el(LIST_END_IND, this.listEndInd()));
    		res.append(el(LI_START_IND, this.liStartInd()));
    		res.append(el(LI_END_IND, this.liEndInd()));
    		res.append(el(ACCESS_MD_TYPE, this.accessMdType()));
    		res.append(el(HEADER_LEVEL, this.headerLevel()));
    		res.append(el(TABLE_IND, this.tableInd()));
    		res.append(el(TABLE_DESCR, this.tableDescr()));
    		res.append(el(OBJECT_CONTENT, this.objectContent()));
    		res.append(el(OBJECT_ID, this.objectId()));
    		res.append(el(OBJECT_DESCR, this.objectDescr()));
    		res.append(el(CELL_SORTING, this.cellSorting()));
    		res.append(el(GO_XSLT, this.goXslt()));
    		res.append(el(FIELD_NAME, this.fieldName()));
    		res.append(el(FORM_URL, this.formUrl()));
    		res.append(el(COMBO_LIST, this.comboList()));
    		res.append(el(VISIBLE, this.visible()));
    		res.append(el(DEFAULT_SELECTED, this.defaultSelected()));
            res.append(endEl(METADATUM));
            
    		return res.toString();
    	}
    	
    	private String el(String name, String val) {
    		return startEl(name) + val + endEl(name);
    	}
    	
    	private String startEl(String name, String[] att_names, String[] att_vals) {
    		String res = "<" + name;
    		for (int i = 0; i < att_names.length; i++) {
    			res += " " + att_names[i] + "=\"";
    			if (i < att_vals.length) {
    				res += att_vals[i];
    			}
    			res += "\"";
    		}
    		return res + ">";
    	}
    	
    	private String startEl(String name) {
    		return "<" + name + ">";
    	}
    	
    	private String endEl(String name) {
    		return "</" + name + ">";
    	}
    }
	
    public static void main(String[] args) {
        String path = "\\\\formit7\\c$\\Users\\davidz\\Desktop\\md.xml";
         try {
            HashMap<String, Metadata> metadata = parseMetadata(Misc.readFile(path, "UTF-8"));
            //System.out.println(metadata.get("0").toString());
            //Misc.writeBinFile(metadata.get("1649200085").wordInsertPdf(), "C:\\Users\\davidz\\Desktop\\t.pdf");
            //System.out.println(metadata.get("1649200085").cellTextBackground());
            System.out.println(metadata.get("1649200081"));
        } catch (IOException e) {
            System.out.println(Misc.message(e));
        }
    }

}
