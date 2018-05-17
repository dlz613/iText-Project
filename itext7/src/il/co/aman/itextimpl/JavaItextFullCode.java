package il.co.aman.itextimpl;

// Start of 'Import Package' snippet.
import java.awt.Color;
import java.awt.Graphics2D;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
//import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.property.AreaBreakType;
import com.itextpdf.layout.property.BaseDirection;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.splitting.ISplitCharacters;
import com.itextpdf.barcodes.Barcode1D;
import com.itextpdf.barcodes.Barcode2D;
import com.itextpdf.barcodes.Barcode128;
import com.itextpdf.barcodes.Barcode39;
import com.itextpdf.barcodes.BarcodeCodabar;
import com.itextpdf.barcodes.BarcodeDataMatrix;
import com.itextpdf.barcodes.BarcodeEAN;
import com.itextpdf.barcodes.BarcodeInter25;
import com.itextpdf.barcodes.BarcodePostnet;
import com.itextpdf.barcodes.BarcodeQRCode;
import com.itextpdf.layout.ColumnDocumentRenderer;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;
//import com.itextpdf.text.pdf.PdfAppearance;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfOutline;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfViewerPreferences;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
//import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.kernel.pdf.PdfWriter;
//import com.itextpdf.text.pdf.RadioCheckField;
import com.itextpdf.barcodes.qrcode.EncodeHintType;

import il.co.aman.apps.Misc;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.svg.SVGDocument;

import il.co.aman.itextimpl.Border;
import il.co.aman.itextimpl.FormParams;
import il.co.aman.itextimpl.FormWriter;
import il.co.aman.itextimpl.SAXimpl;
import il.co.aman.itextimpl.OptionsParse;
import il.co.aman.itextimpl.Xml2Phrases;
import il.co.aman.itextimpl.FakeList;
import il.co.aman.itextimpl.XmlTableParams;
import il.co.aman.itextimpl.XmlTable2Pdf;
import il.co.aman.itextimpl.PdfCell;
import il.co.aman.itextimpl.GetFonts;
import il.co.aman.itextimpl.PdfBorder;
import il.co.aman.itextimpl.SplitOnAll;
import il.co.aman.itextimpl.TableDef;
import il.co.aman.itextimpl.ItextResult;
//import il.co.aman.itextimpl.TaggedPdf;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

// End of 'Import Package' snippet.
public class JavaItextFullCode {

	// Start of 'Helper code' snippet.
	static Document doc = null;
	static PdfDocument pdf = null;
	static PdfWriter writer;
	static ByteArrayOutputStream streamOut = null;
	static float pageHeight;
	static float divisor = 2.54f;
	static String filepath;
	static Integer pageNum;
	static GetFonts fontBank = null;
	static Map<String, Image> images = new HashMap<String, Image>();
	static boolean discard = false; // if the document can't be created, discard
									// all of its records
	static String output_type;
	static PdfOutline[] parents = new PdfOutline[] { null, null, null, null,
			null, null, null };
	static String bookmark_ID_base = "_____&&FIT__BK";
	static int bookmark_num = 0;
	static FormWriter frmwriter;
	static String url = "";
	static final String EMPHASIS_START = "{{B\\";
	static final String EMPHASIS_STOP = "\\B}}";
	static final String EMPHASIS_START_ONLY_BOLD = "{{C\\";
	static final String EMPHASIS_STOP_ONLY_BOLD = "\\C}}";
	static boolean bHasAdvancedJS; // the JavaScript to handle advanced submit
									// has already been added to the PDF
	static Exception ex;

	//static PdfPTable table;
	static int currentTable = -1;
	static int numCols, cellNum;
	static float tableX, tableY;
	static Integer runs = 0;
	static boolean bAfterHeader;
	static Color background; // the background of the whole table.
	static TableDef tabledef;
	static boolean inParagraph;
	static int listLevel;
	static il.co.aman.itextimpl.Paragraph para;
	static il.co.aman.itextimpl.FakeList list;
	static PdfFont listFont;
	//static PdfStructureElement eTop;

	public static float sizeConvert(Double size) {
		return sizeConvert(new Float(size));
	}

	public static float sizeConvert(Float size) {
		if (size == null) {
			return 0f;
		} else if (size > 0) {
			float res = size / divisor * 72f;
			if (res < 0.75f) { // The equivalent in points (at 96DPI) of 1 pixel
				return 0.75f;
			} else {
				return res;
			}
		} else {
			return 0f;
		}
	}

	public static float sizeConvert_negative(Double size) {
		if (size == null) {
			return 0f;
		} else {
			double res = size / divisor * 72f;
			if (Math.abs(res) < 0.75f && res > 0) { // The equivalent in points
													// (at 96DPI) of 1 pixel
				return 0.75f;
			} else if (Math.abs(res) < 0.75f && res < 0) {
				return -0.75f;
			} else {
				return (float) res;
			}
		}
	}

    public static com.itextpdf.layout.Style parseStyle(String style, String font_name) {
    	com.itextpdf.layout.Style res = new com.itextpdf.layout.Style();
        style = style.toLowerCase();
        if ((style.contains("bold")) && !font_name.toUpperCase().contains("BOLD")) { // assuming that all "real" bold fonts have the word bold in their name
        	res.setBold();
            //res = res | Font.BOLD;
        }
        if (style.contains("italic")) {
        	res.setItalic();
            //res = res | Font.ITALIC;
        }
        if (style.contains("underline")) {
        	res.setUnderline();
            //res = res | Font.UNDERLINE;
        }
        if (style.contains("line-through")) {
        	res.setLineThrough();
            //res = res | Font.STRIKETHRU;
        }
        return res;
    }

	public static double verticalLeading(double marginTop, double marginBottom,
			double height, double size) {
		if (marginTop != 0) {
			return marginTop + (size * 0.7);
		} else if (marginBottom != 0) {
			return height - marginBottom;
		} else {
			// return (height + size) * 0.5;
			return (size * 1.0);
		}
	}

	public static Image getImage(String src) {
		if (images.containsKey(src)) {
			return images.get(src);
		} else {
			try {
				Image img = new Image(ImageDataFactory.create(src));
				images.put(src, img);
				return img;
			} catch (IOException e) {
				return null;
			}
		}
	}

	public static boolean pathExists(String path) {
		// if path represents a directory, checks that it exists.
		// if path represents a file, checks that its parent directory exists.
		File file = new File(path);
		String dir = new File(path).isDirectory() ? path : new File(file.getAbsolutePath()).getParent();
		return new File(dir).exists();
	}

	public static void doBarCode(Barcode1D barcode,
			String code, float size, float x, float y, boolean visible,
			boolean setText) {
		try {
			barcode.setStartStopText(setText);
			barcode.setCode(code);
			if (!visible) {
				barcode.setFont(null);
			}
			barcode.setBarHeight(size);
			Image img = new Image(barcode.createFormXObject(null, null, pdf));
			addImage((int)x, (int)y, (int)size, (int)size, img, null, "Barcode");
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static boolean writeMsg(Document doc, float height, String text) {
		Rectangle[] areas = new Rectangle[] {new Rectangle(50, 750, 500, 50)};
		doc.setRenderer(new ColumnDocumentRenderer(doc, areas));
		Paragraph p = new Paragraph(text);
		p.setMargin(0);
		p.setMultipliedLeading(1);
		doc.add(p);
		return true;
	}
	
	public static ByteArrayOutputStream createPDF(String msg) throws java.io.IOException {
		ByteArrayOutputStream _streamOut = new ByteArrayOutputStream();
		PdfWriter writer = new PdfWriter(_streamOut);
		PdfDocument pdf = new PdfDocument(writer);
		Document document = new Document(pdf);
		writeMsg(document, 20f, msg);
		document.close();
		return _streamOut;
	}

	public static boolean createPDF(String path, String msg) throws java.io.FileNotFoundException, java.io.IOException {
		if (pathExists(path)) {
			FileOutputStream fos = new FileOutputStream(path);
			PdfWriter writer = new PdfWriter(fos);
			PdfDocument pdf = new PdfDocument(writer);
			Document document = new Document(pdf);
			writeMsg(doc, 20f, msg);
			document.close();
			return true;
		}
		return false;
	}

	public static ItextResult PDFmsg(String output_type, String path, String msg) {
		String errorMsg = "";
		byte[] streamOut = null;
		boolean generateRow = false;
		try {
			if (output_type.equals("file")) {
				createPDF(path, msg);
			} else {
				streamOut = createPDF(msg).toByteArray();
			}
			errorMsg = msg;
			generateRow = true;
			// generateRow();
		} catch (IOException e) {
		}
		return new ItextResult("", errorMsg, "", streamOut, generateRow);
	}

	public static void addOutlineElement(String xref, String label, int level) {
//		if (level >= 1 && level < parents.length && parents[level - 1] != null) {
//			parents[level] = new PdfOutline(parents[level - 1],
//					PdfAction.gotoLocalPage(xref, false), label);
//		}
	}

	public static String bookmark_ID() {
		return bookmark_ID_base + String.valueOf(bookmark_num++);
	}

	public static void addOnLoadLink(PdfWriter writer, String target) {
//		String js = "function onLoad() {this.gotoNamedDest(\"" + target
//				+ "\");} onLoad();";
//		writer.addJavaScript(js);
	}

	public static ArrayList<String> breakText(String text, String breakstart,
			String breakend) {
		// Splits the string each time a substring is surrounded by the Strings
		// breakstart and breakend.
		// Returns breakstart with the substring.
		// example: "Hello there. {{B\How are\B}} you doing today?" returns:
		// "Hello there. ", "{{B\How are", " you doing today?"
		boolean inside = false;
		ArrayList<String> res = new ArrayList<String>();
		StringBuilder piece = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			if (text.startsWith(breakstart, i)) {
				inside = true;
				res.add(new String(piece));
				piece = new StringBuilder();
				piece.append(breakstart);
				i = i + breakstart.length() - 1;
			} else if (text.startsWith(breakend, i) && inside) {
				inside = false;
				res.add(new String(piece));
				piece = new StringBuilder();
				i = i + breakend.length() - 1;
			} else {
				piece.append(text.charAt(i));
			}
		}
		if (inside) { // the markup is not balanced, so ignore the starting
						// symbol
			res.add(new String(piece).substring(breakstart.length()));
		} else {
			res.add(new String(piece));
		}

		return res;
	}

	public static String fixPath(String first, String second) { // prevents
																// things like
																// http://localhost/resources/example\42.jpg
		if (first.indexOf('/') > -1) {
			return first + second.replace('\\', '/');
		} else {
			return first + second;
		}
	}

	public static FormParams.TextLocation convertLocation(String location) {
		if (location.equals("Right")) {
			return FormParams.TextLocation.RIGHT;
		} else if (location.equals("Left")) {
			return FormParams.TextLocation.LEFT;
		} else {
			return FormParams.TextLocation.LEFT;
		}
	}

	public static void newTable(String widths, String sBackground, float X,
			float Y, int ID, float[] border_widths, String[] border_colors,
			String[] border_styles) {
//		table = new PdfPTable(colWidths(widths));
//		background = Border.parseColor(sBackground);
//		numCols = table.getNumberOfColumns();
//		cellNum = 1;
//		tableX = X;
//		tableY = Y;
//		tabledef = new TableDef(ID, colWidths(widths), sBackground,
//				border_widths, border_colors, border_styles);
//		System.out.println("New table opened. TableID = "
//				+ Integer.toString(ID));
	}

	private static void errorClosing(Document doc, String path, Exception ex,
			String output_type) {
		System.out.println("ERROR in closing this document: " + path);
		System.out.println(ex);
		System.out.println(ex.getMessage());
		PDFmsg(output_type, path,
				"ERROR in closing this document.\nPossible cause: the document has no pages.\n"
						+ ex.getMessage());
		doc = null;
	}

//	private static Exception endTable(PdfContentByte cb, PdfBorder border_bottom) {
//		try {
//			PdfPRow lastRow = table.getRow(table.getRows().size() - 1);
//			for (PdfPCell cell : lastRow.getCells()) {
//				addBorder(border_bottom, 1, cell);
//			}
//			table.setTotalWidth(doc.right() - doc.left());
//			table.writeSelectedRows(0, -1, tableX, tableY, cb);
//			currentTable = -1;
//			System.out.println("Table closed.");
//			return null;
//		} catch (Exception e) {
//			return e;
//		}
//	}

	public static float[] colWidths(String widths) {
		String[] columns = widths.split(",");
		float[] cols = new float[columns.length];
		for (int i = 0; i < columns.length; i++) {
			cols[i] = Float.parseFloat(columns[i]);
		}
		return cols;
	}

//	public static Exception addCell(PdfContentByte cb, PdfCell cell,
//			BaseColor cell_background, int tableID, TableParams tableParams)/* String table_type, String widths, String background, float X,
//			float Y, float[] border_widths, String[] border_colors, String[] border_styles*/{
//		try {
//			if (tableID != currentTable) {
//				// if tabledef == -1, the previous table is ending
//				// if currentTable == -1, a new table is opening
//				// if both are != -1, the previous table is ending, and a new
//				// table is opening
//				if (currentTable > -1) {
//					System.out.println("currentTable > -1");
//					Exception e = endTable(cb, tabledef.getBorder_bottom());
//					if (e != null) {
//						System.out.println(e);
//					}
//				}
//				currentTable = tableID;
//				if (tableID > -1) {// System.out.println("tableID > -1 - opening new table.");
//					newTable(tableParams.TABLE_COL_WIDTHS,
//							tableParams.TABLE_BACKGROUND_COLOR,
//							tableParams.fLeft, tableParams.fTop, tableID,
//							tableParams.borderWidths(),
//							tableParams.borderColors(),
//							tableParams.borderStyles());
//				}
//			} else if (bAfterHeader
//					&& tableParams.CELL_table_row_type.equals("header")) {
//				System.out
//						.println("bAfterHeader && table_type.equals(\"header\")");
//				Exception e = endTable(cb, tabledef.getBorder_bottom());
//				if (e != null) {
//					System.out.println(e);
//				}
//				newTable(tableParams.TABLE_COL_WIDTHS,
//						tableParams.TABLE_BACKGROUND_COLOR, tableParams.fLeft,
//						tableParams.fTop, tableID, tableParams.borderWidths(),
//						tableParams.borderColors(), tableParams.borderStyles());
//			}
//			if (currentTable > -1) {
//				// System.out.println("second currentTable > -1");
//				if (background != null) {
//					cell.setBackgroundColor(tabledef.getBackground());
//				}
//				if (cell_background != null) {
//					cell.setBackgroundColor(cell_background);
//				}
//				if (cellNum % numCols == 1) {
//					addBorder(tabledef.getBorder_left(), 0, cell);
//				}
//				if ((cellNum % cell.getColspan() - 1) % numCols == 0) {
//					addBorder(tabledef.getBorder_right(), 2, cell);
//				}
//				if (cellNum <= numCols) {
//					addBorder(tabledef.getBorder_top(), 3, cell);
//				}
//				cellNum += cell.getColspan();
//				table.addCell(cell);
//				// System.out.println("Cell added to table - " +
//				// cell.getText());
//				bAfterHeader = !tableParams.CELL_table_row_type
//						.equals("header");
//			}
//			return null;
//
//		} catch (Exception e) {
//			return e;
//		}
//
//	}

//	public static void addBorder(PdfBorder border, int side, PdfPCell cell) {
//		if (cell != null) { // if a cell has a colspan (say 3), then the
//							// following two cells will be null
//			switch (side) {
//			case 0:
//				cell.setBorderColorLeft(border.getColor());
//				cell.setBorderWidthLeft(border.getWidth());
//				break;
//			case 1:
//				cell.setBorderColorBottom(border.getColor());
//				cell.setBorderWidthBottom(border.getWidth());
//				break;
//			case 2:
//				cell.setBorderColorRight(border.getColor());
//				cell.setBorderWidthRight(border.getWidth());
//				break;
//			case 3:
//				cell.setBorderColorTop(border.getColor());
//				cell.setBorderWidthTop(border.getWidth());
//				break;
//			}
//		}
//	}

	public static Image createDataMatrix(String text, String charset) {
    	BarcodeDataMatrix barcode = new BarcodeDataMatrix();
     	barcode.setOptions(BarcodeDataMatrix.DM_TEXT);
   	    barcode.setEncoding(charset);
        barcode.setCode(text);
        return new Image(barcode.createFormXObject(com.itextpdf.kernel.colors.ColorConstants.BLACK, pdf))/*.scale(mw, mh)*/;

//		try {
//			BarcodeDatamatrix datamatrix = new BarcodeDatamatrix();
//			datamatrix.setOptions(BarcodeDatamatrix.DM_TEXT);
//			byte[] textBytes = text.getBytes(charset);
//			datamatrix.generate(textBytes, 0, textBytes.length);
//			// make sure that the barcode is square
//			if (datamatrix.getHeight() > datamatrix.getWidth()) {
//				datamatrix.setWidth(datamatrix.getHeight());
//				datamatrix.generate(textBytes, 0, textBytes.length);
//			} else if (datamatrix.getHeight() < datamatrix.getWidth()) {
//				datamatrix.setHeight(datamatrix.getWidth());
//				datamatrix.generate(textBytes, 0, textBytes.length);
//			}
//			// we need to create a java.awt.Image object and then convert it to
//			// com.lowagie.text.Image for use with the older classes (for the
//			// time being)
//			java.awt.Image image = datamatrix.createAwtImage(
//					java.awt.Color.BLACK, java.awt.Color.WHITE);
//			return Image.getInstance(image, null);
//		} catch (Exception e) {
//			System.out.println(e);
//			return null;
//		}
	}
	
    public static Image createDataMatrix(String text, float mw, float mh, String charset) {
    	BarcodeDataMatrix barcode = new BarcodeDataMatrix();
    	barcode.setEncoding(charset);
        barcode.setCode(text);
        return new Image(barcode.createFormXObject(com.itextpdf.kernel.colors.ColorConstants.BLACK, pdf)).scale(mw, mh);
    }

//	public static Image createQR(String text, String charset) {
//		try {
//			java.awt.Image img = createQRAwt(text, charset);
//			if (img != null) {
//				return Image.getInstance(img, null);
//			} else {
//				return null;
//			}
//		} catch (com.itextpdf.text.BadElementException e) {
//			System.out.println(e);
//			return null;
//		} catch (IOException e) {
//			System.out.println(e);
//			return null;
//		}
//	}

//	public static java.awt.Image createQRAwt(String text, String charset) {
//		java.util.Map<EncodeHintType, Object> params = new java.util.HashMap<EncodeHintType, Object>();
//		params.put(EncodeHintType.CHARACTER_SET, charset);
//		BarcodeQRCode qrcode = new BarcodeQRCode(text.trim(), 1, 1, params);
//		return qrcode.createAwtImage(Color.black, Color.white);
//	}

	public static void addImage(int CELL_LEFT, int CELL_TOP, int CELL_width, int CELL_height, Image img,
			String CELL_URL, String altText) {
		if (Misc.notNullOrEmpty(altText)) {
			img.getAccessibilityProperties().setAlternateDescription(altText);
		}
		if (Misc.notNullOrEmpty(CELL_URL)) {
			Rectangle linkLocation = new Rectangle(CELL_LEFT, CELL_TOP, CELL_width, CELL_height);
			PdfLinkAnnotation annotation = new PdfLinkAnnotation(linkLocation).setAction(PdfAction.createURI(CELL_URL));
			pdf.getPage(pageNum).addAnnotation(annotation);
		}
		img.setFixedPosition(pageNum, CELL_LEFT, CELL_TOP);
		doc.add(img);
	}

	// Methods to replace (most of) the code in the 'Input Row' area

	public static void init(String FONTS_DIR, String CELL_form_URL) {
		// initialize the font factory, if necessary
		if (fontBank == null) {
			fontBank = new GetFonts(FONTS_DIR);
			System.out.println("Fonts from " + FONTS_DIR + " registered.");
		}

		if (CELL_form_URL != null) {
			url = CELL_form_URL;
		}
	}

	public static ItextResult newDoc(String path, TableDef tabledef,
			String optimize_ind, String DOC_UNIT, Double DOC_PAGE_HEIGHT,
			Double DOC_PAGE_WIDTH, String OUTPUT_TYPE, String DOC_ON_LOAD_LINK,
			String TAGGED_PDF_IND, String language, String title) {
		if (language == null) {
			language = "en-US";
		}
		if (title == null) {
			title = "FormIT Document";
		}
		String _RESULT = "OK", _ERROR_CODE = "0", _ERROR_MSG = "";
		byte[] _STREAM_OUT = null;
		boolean _generateRow = false;

		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>New Document. Output = "	+ path);
		images = new HashMap<String, Image>();
		try {
			filepath = path;

			if (doc != null) { // close previous document
				boolean bClosed = false;
				try {
					//frmwriter.finish();
					if (currentTable > -1) {
						System.out
								.println("Closing last table from previous document");
//						Exception e = endTable(cb, tabledef.getBorder_bottom());
//						if (e != null) {
//							System.out.println(e);
//						}
					}
					doc.close();
					boolean tagged = pdf.isTagged();
					writer.close();
					_RESULT = "Document closed";
					if ("yes".equals(optimize_ind)) {
						if (streamOut != null) {
							path = "stream";
							_STREAM_OUT = optimizePDF(streamOut.toByteArray(),
									tagged);
							streamOut.close();
							streamOut = null;
						} else {
							il.co.aman.apps.Misc.writeBinFile(
									filepath,
									optimizePDF(Misc.readBinFile(filepath),
											tagged));
						}
					} else if (streamOut != null) {
						path = "stream";
						_STREAM_OUT = streamOut.toByteArray();
						streamOut.close();
						streamOut = null;
					}
					bClosed = true;
					pause(3000);
					System.gc();
				} catch (Exception e) {
					ex = e;
				}
				
				if (!bClosed) {
					errorClosing(doc, path + " - NEW_DOC.equals(\"start\")", ex, output_type);
				}
				doc = null;
			}
			discard = false;

			output_type = OUTPUT_TYPE;

			if (DOC_UNIT.toUpperCase().equals("CM")) {
				divisor = 2.54f;
			} else {
				divisor = 25.4f;
			}

			pageHeight = sizeConvert(DOC_PAGE_HEIGHT);

			System.gc();

			pageNum = 0;
			if (output_type.equals("file")) {
				if (pathExists(path)) {
					try {
						streamOut = null;
						writer = new PdfWriter(new FileOutputStream(path));
					} catch (java.io.FileNotFoundException e) {
						System.out.println("******* Output file: " + path + " cannot be opened.");
						discard = true;
						_ERROR_CODE = "2";
						_ERROR_MSG = "File cannot be opened: " + path;
						_generateRow = true;
					}
				} else {
					discard = true;
					_ERROR_CODE = "2";
					_ERROR_MSG = "Invalid file path: " + path;
					_generateRow = true;
				}
			} else {
				streamOut = new ByteArrayOutputStream();
				writer = new PdfWriter(streamOut);
			}
			pdf = new PdfDocument(writer);
			doc = new Document(pdf);
			//System.out.println("PdfDocument object instantiated.");

			//writer.setPdfVersion(PdfWriter);
			if (TAGGED_PDF_IND.equals("yes")) {
				pdf.setTagged(); // ** Added November 2016, to support tagged
									// PDF. This must be set BEFORE opening the
									// Document

				// writer.setUserProperties(true); // **** for some reason, this
				// was causing Adobe to crash when opening the PDF.
			}
			pdf.getCatalog().setLang(new PdfString(language));
			pdf.getCatalog().setViewerPreferences(new PdfViewerPreferences().setDisplayDocTitle(true));
			PdfDocumentInfo info = pdf.getDocumentInfo();
			info.setTitle(title);
			pdf.getXmpMetadata();
			////doc.open();

			if (TAGGED_PDF_IND.equals("yes")) {
				//eTop = TaggedPdf.initStructure(writer); // This must be done
														// AFTER opening the
														// Document
			} else {
				//eTop = null; // just to make sure....
			}
			//frmwriter = new FormWriter(writer);
			//frmwriter.setDebug(true);
			bHasAdvancedJS = false;

			if (DOC_ON_LOAD_LINK != null && !DOC_ON_LOAD_LINK.equals("")) { // add JavaScript for onload jump to internal destination
				addOnLoadLink(writer, DOC_ON_LOAD_LINK);
			}

			////cb = writer.getDirectContent();

			////parents[0] = cb.getRootOutline();

			_RESULT = "New document.";
			// System.out.println("************************** Document opened: "
			// + path);
		} catch (Exception e) {
			_ERROR_CODE = "2";
			_ERROR_MSG = e.getMessage();
		}
		return new ItextResult(_ERROR_CODE, _ERROR_MSG, _RESULT, _STREAM_OUT,
				_generateRow);
	}

	public static ItextResult endDoc(String path, TableDef tabledef,
			String optimize_ind) {
		String _RESULT = "OK", _ERROR_CODE = "0", _ERROR_MSG = "";
		byte[] _STREAM_OUT = null;
		boolean _generateRow = false;

		System.out.println("Run number: " + (++runs).toString());

		boolean bClosed = false;
		try {
//			frmwriter.finish();
//			if (currentTable > -1) {
//				System.out.println("NEW_DOC.equals(\"end\")");
//				Exception e = endTable(cb, tabledef.getBorder_bottom());
//				if (e != null) {
//					System.out.println(e);
//				}
//			}
			if (doc != null) {
				doc.close();
			}
			boolean tagged = pdf.isTagged();
			writer.close();
			path = filepath;
			_RESULT = "Document closed.";
			if ("yes".equals(optimize_ind)) {
				if (streamOut != null) {
					path = "stream";
					_STREAM_OUT = optimizePDF(streamOut.toByteArray(), tagged);
					streamOut.close();
					streamOut = null;
				} else {
					il.co.aman.apps.Misc.writeBinFile(filepath,
							optimizePDF(Misc.readBinFile(filepath), tagged));
				}
			} else if (streamOut != null) {
				path = "stream";
				_STREAM_OUT = streamOut.toByteArray();
				streamOut.close();
				streamOut = null;
			}
			_ERROR_CODE = "0";
			_ERROR_MSG = "Document created successfully.";
			if (doc != null) {
				_generateRow = true;
			}
			bClosed = true;
			doc = null; // to prevent an empty PDF after a good one from being
						// reported as being created
		} catch (Exception e) {
			ex = e;
		}
		if (!bClosed) { // means the try failed
			errorClosing(doc, path + " - NEW_DOC.equals(\"end\")", ex,
					output_type);
		}
		return new ItextResult(_ERROR_CODE, _ERROR_MSG, _RESULT, _STREAM_OUT,
				_generateRow);
	}

	private static byte[] optimizePDF(byte[] pdf, boolean tagged)
			throws Exception {
//		byte[] res;
//		com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(
//				pdf);
//		res = il.co.aman.formit.PdfConcat.normalizeFile(reader, tagged);
//		reader.close();
//		return res;
		return pdf;
	}

	public static ItextResult newPage(TableDef tabledef,
			Double DOC_PAGE_HEIGHT, Double DOC_PAGE_WIDTH) {
		String _RESULT;
//		if (currentTable > -1) {
//			System.out.println("NEW_PAGE.equals(\"yes\")");
//			Exception e = endTable(cb, tabledef.getBorder_bottom());
//			if (e != null) {
//				System.out.println(e);
//			}
//		}
		currentTable = -1;
//		doc.setPageSize(new Rectangle(sizeConvert(DOC_PAGE_WIDTH),
//				sizeConvert(DOC_PAGE_HEIGHT)));
		float width = sizeConvert(DOC_PAGE_WIDTH);
		float height = sizeConvert(DOC_PAGE_HEIGHT);
		PageSize pagesize = new PageSize(width, height);
		pdf.addNewPage(pagesize);
		pageNum++;
		_RESULT = "New page.";
		System.out.println("****************************** Page " + pageNum.toString() + " opened.");
		return new ItextResult("", "", _RESULT, null, false);
	}

	public static ItextResult handleParagraphs(String Para_start_ind,
			String CELL_dir, double linespace, String List_start_ind,
			String CELL_FONTstyle, String CELL_text_decoration,
			String CELL_FONT, double CELL_FONTSIZE, String CELL_color,
			String LI_start_ind) {
//		if (!inParagraph) {
//			inParagraph = Para_start_ind.equals("yes");
//			if (inParagraph) {
//				int dir = PdfWriter.RUN_DIRECTION_LTR;
//				if ("rtl".equals(CELL_dir)) {
//					dir = PdfWriter.RUN_DIRECTION_RTL;
//				}
//				para = new il.co.aman.itextimpl.Paragraph(dir);
//				System.out.println("New paragraph initialized");
//				listLevel = -1;
//				para.setLineHeight((float) linespace);
//			}
//		}
//
//		if (inParagraph && List_start_ind.equals("yes")) {
//			int style = parseStyle(CELL_FONTstyle + " " + CELL_text_decoration,
//					CELL_FONT);
//			boolean bold = (style & Font.BOLD) == Font.BOLD;
//			boolean italic = (style & Font.ITALIC) == Font.ITALIC;
//			boolean lineThrough = (style & Font.STRIKETHRU) == Font.STRIKETHRU;
//			boolean underline = (style & Font.UNDERLINE) == Font.UNDERLINE;
//			listFont = Xml2Phrases.getFont(CELL_FONT, (float) CELL_FONTSIZE,
//					CELL_color, bold, italic, lineThrough, underline);
//			listLevel++;
//			if (listLevel == 0) {
//				list = new FakeList(false, para.getDir());
//			} else {
//				list.insertList();
//			}
//		}
//
//		if (inParagraph && LI_start_ind.equals("yes")) {
//			list.insertListElement();
//		}
		return null;
	}

	public static ItextResult textContent(HashMap<String, CssParser.Style> styles, String stylename,
			String CONTENT_TYPE, int CELL_tabledef_ID, int CELL_colspan,
			String CELL_CONTENT, TableParams tableParams,
			double CELL_justify_spacing, double left, double top,
			String CELL_textbackground, int CELL_bookmark_level,
			String CELL_tag_target_link_key, String CELL_tag_link_key,
			String CELL_external_path, String CELL_cell_link,
			String CELL_tool_tip, String CELL_bookmark_text, double linespace,
			String LI_end_ind, String List_end_ind, String Para_end_ind,
			int CELL_header_level, String ACCESS_MD_TYPE, String TABLE_IND,
			String CELL_alt_text) {
		String _RESULT = "", _ERROR_CODE = "", _ERROR_MSG = "";
		//System.out.println(CELL_CONTENT);
//		TaggedPdf.TagTypes _tagType = TaggedPdf.TagTypes.NONE;
//
//		if ("H".equals(ACCESS_MD_TYPE)) {
//			if (TABLE_IND.equals("Y")) {
//				_tagType = TaggedPdf.TagTypes.CAPTION;
//			} else if (CELL_header_level > 0) {
//				_tagType = TaggedPdf.TagTypes.header(CELL_header_level);
//			} else {
//				_tagType = TaggedPdf.TagTypes.TOOLTIP;
//			}
//		} else if ("I".equals(ACCESS_MD_TYPE)) {
//			// this is handled in imageContent()
//		} else if ("N".equals(ACCESS_MD_TYPE)) {
//
//		}

		if (CONTENT_TYPE.equals("signpad")) {
			System.out.println("********* signpad ********");
		}

		CssParser.Style cssStyle = styles.get(stylename);
		if (cssStyle == null) {
			_RESULT = "Failed";
			_ERROR_CODE = "200";
			_ERROR_MSG = "Style ' " + stylename + "' not found!";
			return new ItextResult(_ERROR_CODE, _ERROR_MSG, _RESULT, null, false);
		} else {
			if (cssStyle.align() == CssParser.Style.Alignment.JUSTIFY || cssStyle.align() == CssParser.Style.Alignment.JUSTIFY_ALL) {
				new PdfCanvas(pdf.getPage(pageNum)).setCharacterSpacing((float)CELL_justify_spacing);
				//writer.setSpaceCharRatio((float) CELL_justify_spacing);
			}
			if (CELL_tabledef_ID > -1) {
//				PdfCell cell = new PdfCell();
//				cell.setColspan(CELL_colspan);
//				Chunk chunk = new Chunk(CELL_CONTENT, cssStyle.getFont());
//				cell.setPhrase(new Paragraph(chunk));
//				cell.setFixedHeight(cssStyle.height());
//
//				Exception e = addCell(cb, cell,
//						Border.parseColor(cssStyle.backgroundColor()),
//						CELL_tabledef_ID, tableParams);
//				if (e != null) {
//					System.out.println(e);
//				}
			} else {
				if (inParagraph) {
//					if (listLevel > -1) {
//						list.addElementPart(new FakeList.ListText(CELL_CONTENT, cssStyle.getFont()));
//					} else {
//						para.addText(CELL_CONTENT.trim(), cssStyle.getFont(),
//								cssStyle.backgroundColor());
//					}
				} else {
					try {
						float x_end = (float) left + cssStyle.width();
						float y_end = (float) top + cssStyle.height();
						/*Rectangle frame = new Rectangle((float) left,
								(float) top, (float) x_end, (float) y_end);
						doc.setRenderer(new ColumnDocumentRenderer(doc, new Rectangle[] {frame}));
						doc.add(new AreaBreak(AreaBreakType.LAST_PAGE));*/
						// set the cell background color and/or text background
						// color
						com.itextpdf.kernel.colors.Color backgroundColor = Border.parseColor(cssStyle.backgroundColor());
						if (backgroundColor != null) {
							new PdfCanvas(pdf.getPage(pageNum)).saveState().setFillColor(backgroundColor).rectangle(new Rectangle((float)left, (float)top, cssStyle.width(), cssStyle.height())).fill().restoreState();
//							frame.setBackgroundColor(Border
//									.parseColor(cssStyle._backgroundColor));
						}

						////cb.rectangle(frame);

						Border.setBorders(pdf.getPage(pageNum), (float) left, (float) top,
								(float) x_end, (float) y_end, cssStyle.getBorder().borderLeft(), cssStyle.getBorder().borderTop(), 
								cssStyle.getBorder().borderRight(), cssStyle.getBorder().borderBottom());

						Paragraph phrase = new Paragraph();
						ArrayList<String> chunks;
						if (CELL_CONTENT.contains(EMPHASIS_START)) {
							chunks = breakText(CELL_CONTENT, EMPHASIS_START, EMPHASIS_STOP);
							for (int i = 0; i < chunks.size(); i++) {
								String txt = chunks.get(i);
								//Style newstyle = cssStyle.fontStyle();
								Text ch = new Text(CreatePdf.fixBidiText(txt.startsWith(EMPHASIS_START)? txt.substring(EMPHASIS_START.length()): txt));
								if (txt.startsWith(EMPHASIS_START)) {
									ch.setBold().setUnderline();
									//newstyle.setBold().setUnderline();
									//txt = txt.substring(EMPHASIS_START.length());
								}
								//newstyle.setFontColor(Border.parseColor(cssStyle.color()));
								//newstyle.setFont(cssStyle.getFont());
								//newstyle.setFontSize(cssStyle.fontSize());
								//ch.addStyle(newstyle);
								ch.setFontColor(Border.parseColor(cssStyle.color())).setFont(cssStyle.getFont()).setFontSize(cssStyle.fontSize());
								phrase.add(ch);
							}
						} else {
							chunks = breakText(CELL_CONTENT, EMPHASIS_START_ONLY_BOLD, EMPHASIS_STOP_ONLY_BOLD);
							for (int i = 0; i < chunks.size(); i++) {
								String txt = chunks.get(i);
								//Style newstyle = cssStyle.fontStyle();
								Text ch = new Text(CreatePdf.fixBidiText(txt.startsWith(EMPHASIS_START_ONLY_BOLD)? txt.substring(EMPHASIS_START_ONLY_BOLD.length()): txt));
								if (txt.startsWith(EMPHASIS_START_ONLY_BOLD)) {
									//newstyle.setBold();
									//txt = txt.substring(EMPHASIS_START_ONLY_BOLD.length());
									ch.setBold();
								}
								//newstyle.setFontColor(Border.parseColor(cssStyle.color()));
								//newstyle.setFont(cssStyle.getFont());
								//newstyle.setFontSize(cssStyle.fontSize());
								//ch.addStyle(newstyle);
								ch.setFontColor(Border.parseColor(cssStyle.color())).setFont(cssStyle.getFont()).setFontSize(cssStyle.fontSize());
								phrase.add(ch);
							}
						}

						//Links
						//Text c = (Text) phrase.getChunks().get(0);
						if (!cssStyle.wrap()) {
							phrase.setSplitCharacters(new SplitOnAll());
						}
						if (CELL_textbackground != null) {
							phrase.setBackgroundColor(Border.parseColor(CELL_textbackground));
						}

						if (CELL_bookmark_level > 0) {
							String ID = CELL_tag_target_link_key == null ? bookmark_ID()
									: CELL_tag_target_link_key;
							phrase.setDestination(ID);
							addOutlineElement(ID, CELL_bookmark_text,
									CELL_bookmark_level);
						}

						if (CELL_tag_target_link_key != null) { // the target of a
																// link
							phrase.setDestination(CELL_tag_target_link_key);
						} else if (CELL_external_path != null
								&& CELL_tag_link_key != null) { // link to an
																// external PDF
																// document
							System.out.println("********************* |"
									+ CELL_tag_link_key + "|");
							////phrase.setRemoteGoto(CELL_external_path, CELL_tag_link_key);
						} else if (CELL_tag_link_key != null) { // an internal link
////							phrase.setAction(PdfAction.gotoLocalPage(CELL_tag_link_key,
////									false));
						} else if (CELL_cell_link != null) { // a link to a URL
																// (webpage or PDF
																// on the web)
							////phrase.setAnchor(CELL_cell_link);
							//c.setAccessibleAttribute(PdfName.ALT, new PdfString("google"));
						}

						if (CELL_tool_tip != null) {

						}

						float marginTop = cssStyle.marginTop();

						if (cssStyle.wrap()) {
							// marginTop = marginTop + 0.75f; // one pixel @ 96 DPI
							marginTop = cssStyle.fontSize()	* new Float(linespace);
							if (marginTop < 0.75f) {
								marginTop = 0.75f;
							}

						}

//						if (_tagType != TaggedPdf.TagTypes.NONE
//								&& _tagType != TaggedPdf.TagTypes.TOOLTIP
//								&& eTop != null) {
//							PdfStructureElement e1 = new PdfStructureElement(eTop,
//									_tagType.getType());
//							cb.beginMarkedContentSequence(e1);
//						}
						/*Rectangle[] area = new Rectangle[] {new Rectangle((float)left, (float)top, x_end, y_end)};
						doc.setRenderer(new ColumnDocumentRenderer(doc, area));
						doc.add(new AreaBreak(AreaBreakType.LAST_PAGE));
						phrase.setMargin(0);
						phrase.setMultipliedLeading(1);*/
						phrase.setTextAlignment(cssStyle.textalign());
						phrase.setFixedPosition((float)left, (float)top, (float)(x_end - left));
						doc.add(phrase);

//						ct.setSimpleColumn(
//								new com.itextpdf.text.Paragraph(phrase),
//								//phrase,
//								(float) left
//										+ sizeConvert_negative(CELL_marginLeft),
//								(float) top,
//								x_end - sizeConvert_negative(CELL_marginRight),
//								y_end,
//								(float) verticalLeading(marginTop,
//										sizeConvert_negative(CELL_marginBottom),
//										cellHeight, CELL_FONTSIZE * 1), textAlign);
//						ct.setRunDirection(runDir);
//						ct.go();
//						if (_tagType != TaggedPdf.TagTypes.NONE	&& _tagType != TaggedPdf.TagTypes.TOOLTIP && eTop != null) {
//							//cb.endMarkedContentSequence();
//						}
//						if (_tagType == TaggedPdf.TagTypes.TOOLTIP) {
//							PdfAnnotation annotation = PdfAnnotation
//									.createSquareCircle(
//											writer,
//											new Rectangle((float) left + cssStyle.marginLeft(), (float) top + 20, x_end - cssStyle.marginRight(), y_end), "", true);
//							annotation.put(PdfName.T, new PdfString(CELL_alt_text));
//							annotation.put(PdfName.CONTENTS, new PdfString(" "));
//							writer.addAnnotation(annotation);
//						}

						_RESULT = "Text added.";
					} catch (Exception e) {
						_ERROR_CODE = "2";
						_ERROR_MSG = Misc.message(e);
						System.out.println(_ERROR_MSG);
						System.out.println(CELL_CONTENT);
					}
				}

			}
		}
		return new ItextResult(_ERROR_CODE, _ERROR_MSG, _RESULT, null, false);
		//return null;
	}

//	public static ItextResult textContent(TextContentParams params) {
//		return textContent(params.CONTENT_TYPE, params.CELL_tabledef_ID,
//				params.CELL_FONT, params.CELL_FONTSIZE, params.CELL_FONTstyle,
//				params.CELL_text_decoration, params.CELL_colspan,
//				params.CELL_CONTENT, params.CELL_color,
//				params.CELL_backgroundColor, params.tableParams,
//				params.borderParams, params.CELL_HAlign, params.CELL_dir,
//				params.CELL_justify_spacing, params.left, params.top,
//				params.cellWidth, params.cellHeight, params.CELL_wrap,
//				params.CELL_textbackground, params.CELL_bookmark_level,
//				params.CELL_tag_target_link_key, params.CELL_tag_link_key,
//				params.CELL_external_path, params.CELL_cell_link,
//				params.CELL_tool_tip, params.CELL_bookmark_text,
//				params.CELL_marginTop, params.CELL_marginLeft,
//				params.CELL_marginRight, params.CELL_marginBottom,
//				params.linespace, params.LI_end_ind, params.List_end_ind,
//				params.Para_end_ind, params.CELL_header_level,
//				params.ACCESS_MD_TYPE, params.TABLE_IND, params.CELL_alt_text);
//	}

	public static ItextResult textContent(String CONTENT_TYPE,
			int CELL_tabledef_ID, String CELL_FONT, double CELL_FONTSIZE,
			String CELL_FONTstyle, String CELL_text_decoration,
			int CELL_colspan, String CELL_CONTENT, String CELL_color, /*double CELL_height, */
			String CELL_backgroundColor, TableParams tableParams,
			BorderParams borderParams, String CELL_HAlign, String CELL_dir,
			double CELL_justify_spacing, double left, double top,
			double cellWidth, double cellHeight, String CELL_wrap,
			String CELL_textbackground, int CELL_bookmark_level,
			String CELL_tag_target_link_key, String CELL_tag_link_key,
			String CELL_external_path, String CELL_cell_link,
			String CELL_tool_tip, String CELL_bookmark_text,
			double CELL_marginTop, double CELL_marginLeft,
			double CELL_marginRight, double CELL_marginBottom,
			double linespace, String LI_end_ind, String List_end_ind,
			String Para_end_ind, int CELL_header_level, String ACCESS_MD_TYPE,
			String TABLE_IND, String CELL_alt_text) {

//		String _RESULT = "", _ERROR_CODE = "", _ERROR_MSG = "";
//		//TaggedPdf.TagTypes _tagType = TaggedPdf.TagTypes.NONE;
//
////		if ("H".equals(ACCESS_MD_TYPE)) {
////			if (TABLE_IND.equals("Y")) {
////				_tagType = TaggedPdf.TagTypes.CAPTION;
////			} else if (CELL_header_level > 0) {
////				_tagType = /*TaggedPdf.TagTypes.header(CELL_header_level)*/TaggedPdf.TagTypes.TOOLTIP;
////			} else {
////				_tagType = TaggedPdf.TagTypes.TOOLTIP;
////			}
////		} else if ("I".equals(ACCESS_MD_TYPE)) {
////			// this is handled in imageContent()
////		} else if ("N".equals(ACCESS_MD_TYPE)) {
////
////		}
//
//		if (CONTENT_TYPE.equals("signpad")) {
//			System.out.println("********* signpad ********");
//		}
//		/*if (CELL_tabledef_ID > -1) {
//			PdfCell cell = new PdfCell();
//			cell.setColspan(CELL_colspan);
//			Chunk chunk = new Chunk(CELL_CONTENT, new Font(
//					fontBank.getFont(CELL_FONT),
//					(float) CELL_FONTSIZE * 1f, parseStyle(CELL_FONTstyle + " "
//							+ CELL_text_decoration, CELL_FONT),
//					Border.parseColor(CELL_color)));
//			cell.setPhrase(new Paragraph(chunk));
//			cell.setFixedHeight((float) cellHeight);
//
//			Exception e = addCell(cb, cell,
//					Border.parseColor(CELL_backgroundColor), CELL_tabledef_ID,
//					tableParams);
//			if (e != null) {
//				System.out.println(e);
//			}
//		} else {
//			if (inParagraph) {
//				int style = parseStyle(CELL_FONTstyle + " "
//						+ CELL_text_decoration, CELL_FONT);
//				boolean bold = (style & Font.BOLD) == Font.BOLD;
//				boolean italic = (style & Font.ITALIC) == Font.ITALIC;
//				boolean lineThrough = (style & Font.STRIKETHRU) == Font.STRIKETHRU;
//				boolean underline = (style & Font.UNDERLINE) == Font.UNDERLINE;
//				if (listLevel > -1) {
//					list.addElementPart(new FakeList.ListText(CELL_CONTENT,
//							Xml2Phrases.getFont(CELL_FONT,
//									(float) CELL_FONTSIZE, CELL_color, bold,
//									italic, lineThrough, underline), Border
//									.parseColor(CELL_backgroundColor)));
//				} else {
//					para.addText(CELL_CONTENT, Xml2Phrases.getFont(CELL_FONT,
//							(float) CELL_FONTSIZE, CELL_color, bold, italic,
//							lineThrough, underline), CELL_backgroundColor);
//				}
//			} else*/ {
//				try {
//					TextAlignment textAlign;
//					BaseDirection runDir;
//					Rectangle[] area = new Rectangle[] {new Rectangle((int)left, (int)top, (int)cellWidth, (int)cellHeight)};
//					doc.setRenderer(new ColumnDocumentRenderer(doc, area));
//					doc.add(new AreaBreak(AreaBreakType.LAST_PAGE));
//					if (CELL_HAlign.equals("Right")) {
//						if (CELL_dir.equals("ltr")) {
//							textAlign = TextAlignment.RIGHT;
//						} else {
//							textAlign = TextAlignment.LEFT; // right-align for
//															// right-to-left is
//															// actually
//															// left-align
//						}
//					} else if (CELL_HAlign.equals("Center")) {
//						textAlign = TextAlignment.CENTER;
//					} else if (CELL_HAlign.equals("Justify")) {
//						//writer.setSpaceCharRatio((float) CELL_justify_spacing);
//						new PdfCanvas(pdf.getPage(pageNum)).setCharacterSpacing((float)CELL_justify_spacing);
//						textAlign = TextAlignment.JUSTIFIED;
//					} else if (CELL_HAlign.equals("JustifyAll")) {
//						//writer.setSpaceCharRatio((float) CELL_justify_spacing);
//						new PdfCanvas(pdf.getPage(pageNum)).setCharacterSpacing((float)CELL_justify_spacing);
//						textAlign = TextAlignment.JUSTIFIED_ALL;
//					} else { // "Left"
//						if (CELL_dir.equals("ltr")) {
//							textAlign = TextAlignment.LEFT;
//						} else {
//							textAlign = TextAlignment.RIGHT;
//						}
//					}
//
//					if (CELL_dir.equals("ltr")) {
//						runDir = BaseDirection.LEFT_TO_RIGHT;
//					} else {
//						runDir = BaseDirection.RIGHT_TO_LEFT;
//					}
//
//					float x_end = (float) left + (float) cellWidth;
//					float y_end = (float) top + (float) cellHeight;
//					Rectangle frame = new Rectangle((float) left, (float) top,
//							(float) x_end, (float) y_end);
//
//					// set the cell background color and/or text background
//					// color
//					if (CELL_backgroundColor != null) {
//						new PdfCanvas(pdf.getPage(pageNum)).saveState().setFillColor(Border.parseColor(CELL_backgroundColor)).rectangle(new Rectangle((float)left, (float)top, (float)cellWidth, (float)cellHeight)).fill().restoreState();
////						frame.setBackgroundColor(Border
////								.parseColor(CELL_backgroundColor));
//					}
//
//					//cb.rectangle(frame);
//
//					Border.setBorders(pdf.getPage(pageNum), (float) left, (float) top,
//							(float) x_end, (float) y_end,
//							borderParams.borderLeft(),
//							borderParams.borderTop(),
//							borderParams.borderRight(),
//							borderParams.borderBottom());
//
//					Paragraph phrase = new Paragraph();
//					ArrayList<String> chunks;
//					if (CELL_CONTENT.contains(EMPHASIS_START)) {
//						chunks = breakText(CELL_CONTENT, EMPHASIS_START, EMPHASIS_STOP);
//						for (int i = 0; i < chunks.size(); i++) {
//							String txt = chunks.get(i);
//							Style newstyle = parseStyle(CELL_FONTstyle + " " + CELL_text_decoration, CELL_FONT);
//							if (txt.startsWith(EMPHASIS_START)) {
//								newstyle.setBold().setUnderline();
//								txt = txt.substring(EMPHASIS_START.length());
//							}
//							Text ch = new Text(txt);
//							newstyle.setFontColor(Border.parseColor(CELL_color));
//							newstyle.setFont(fontBank.getFont(CELL_FONT));
//							ch.setFontSize((float) CELL_FONTSIZE * 1f);
//							phrase.add(ch);
//						}
//					} else {
//						chunks = breakText(CELL_CONTENT,
//								EMPHASIS_START_ONLY_BOLD,
//								EMPHASIS_STOP_ONLY_BOLD);
//						for (int i = 0; i < chunks.size(); i++) {
//							String txt = chunks.get(i);
//							Style newstyle = parseStyle(CELL_FONTstyle + " " + CELL_text_decoration, CELL_FONT);
//							if (txt.startsWith(EMPHASIS_START_ONLY_BOLD)) {
//								newstyle.setBold();
//								txt = txt.substring(EMPHASIS_START_ONLY_BOLD
//										.length());
//							}
//							Text ch = new Text(txt);
//							newstyle.setFontColor(Border.parseColor(CELL_color));
//							newstyle.setFont(fontBank.getFont(CELL_FONT));
//							ch.setFontSize((float) CELL_FONTSIZE * 1f);
//							phrase.add(ch);
//						}
//					}
//
//					// Links
//					//Text c = (Text) phrase.getChunks().get(0);
//					if (!CELL_wrap.equals("true")) {
//						phrase.setSplitCharacters(new SplitOnAll());
//					}
//					if (CELL_textbackground != null) {
//						phrase.setBackgroundColor(Border.parseColor(CELL_textbackground));
//					}
//
//					if (CELL_bookmark_level > 0) {
//						String ID = CELL_tag_target_link_key == null ? bookmark_ID()
//								: CELL_tag_target_link_key;
//						phrase.setDestination(ID);
//						addOutlineElement(ID, CELL_bookmark_text,
//								CELL_bookmark_level);
//					}
//
//					if (CELL_tag_target_link_key != null) { // the target of a
//															// link
//						phrase.setDestination(CELL_tag_target_link_key);
//					} else if (CELL_external_path != null
//							&& CELL_tag_link_key != null) { // link to an
//															// external PDF
//															// document
//						System.out.println("********************* |"
//								+ CELL_tag_link_key + "|");
//						////phrase.setRemoteGoto(CELL_external_path, CELL_tag_link_key);
//					} else if (CELL_tag_link_key != null) { // an internal link
//////						phrase.setAction(PdfAction.gotoLocalPage(CELL_tag_link_key,
//////								false));
//					} else if (CELL_cell_link != null) { // a link to a URL
//															// (webpage or PDF
//															// on the web)
//						////phrase.setAnchor(CELL_cell_link);
//						//c.setAccessibleAttribute(PdfName.ALT, new PdfString("google"));
//					}
//
//					if (CELL_tool_tip != null) {
//
//					}
//
//					float marginTop = sizeConvert_negative(CELL_marginTop);
//
//					if (CELL_wrap.equals("true")) {
//						// marginTop = marginTop + 0.75f; // one pixel @ 96 DPI
//						marginTop = new Float(CELL_FONTSIZE)
//								* new Float(linespace);
//						if (marginTop < 0.75f) {
//							marginTop = 0.75f;
//						}
//
//					}
//
////					if (_tagType != TaggedPdf.TagTypes.NONE
////							&& _tagType != TaggedPdf.TagTypes.TOOLTIP
////							&& eTop != null) {
////						PdfStructureElement e1 = new PdfStructureElement(eTop,
////								_tagType.getType());
////						cb.beginMarkedContentSequence(e1);
////					}
//					/*area = new Rectangle[] {new Rectangle((float)left, (float)top, x_end, y_end)};
//					doc.setRenderer(new ColumnDocumentRenderer(doc, area));
//					doc.add(new AreaBreak(AreaBreakType.LAST_PAGE));
//					phrase.setMargin(0);
//					phrase.setMultipliedLeading(1);*/
//					phrase.setFixedPosition((float)left, (float)top, (float)(x_end - left));
//					doc.add(phrase);
//
////					ct.setSimpleColumn(
////							new com.itextpdf.text.Paragraph(phrase),
////							//phrase,
////							(float) left
////									+ sizeConvert_negative(CELL_marginLeft),
////							(float) top,
////							x_end - sizeConvert_negative(CELL_marginRight),
////							y_end,
////							(float) verticalLeading(marginTop,
////									sizeConvert_negative(CELL_marginBottom),
////									cellHeight, CELL_FONTSIZE * 1), textAlign);
////					ct.setRunDirection(runDir);
////					ct.go();
////					if (_tagType != TaggedPdf.TagTypes.NONE
////							&& _tagType != TaggedPdf.TagTypes.TOOLTIP
////							&& eTop != null) {
////						cb.endMarkedContentSequence();
////					}
////					if (_tagType == TaggedPdf.TagTypes.TOOLTIP) {
////						PdfAnnotation annotation = PdfAnnotation
////								.createSquareCircle(
////										writer,
////										new Rectangle(
////												(float) left
////														+ sizeConvert_negative(CELL_marginLeft),
////												(float) top + 20,
////												x_end
////														- sizeConvert_negative(CELL_marginRight),
////												y_end), "", true);
////						annotation.put(PdfName.T, new PdfString(CELL_alt_text));
////						annotation.put(PdfName.CONTENTS, new PdfString(" "));
////						/*
////						 * PdfAppearance ap =
////						 * cb.createAppearance((float)cellWidth,
////						 * (float)cellHeight);
////						 * annotation.setAppearance(PdfAnnotation
////						 * .APPEARANCE_NORMAL, ap);
////						 */
////						writer.addAnnotation(annotation);
////						/*
////						 * Annotation desc = new Annotation("", CELL_alt_text);
////						 * //desc.setDimensions((float)x, (float)y, (float) x +
////						 * (float) width, (float) y + (float) height);
////						 * desc.setDimensions((float) left +
////						 * sizeConvert_negative(CELL_marginLeft), (float) top,
////						 * x_end - sizeConvert_negative(CELL_marginRight),
////						 * y_end); doc.add(desc);
////						 */
////					}
////
//					_RESULT = "Text added.";
//				} catch (Exception e) {
//					_ERROR_CODE = "2";
//					_ERROR_MSG = e.getMessage();
//				}
//			
//		}
//
//		if (inParagraph && LI_end_ind.equals("yes")) {
//			list.closeLastElement();
//		}
//
//		if (inParagraph && List_end_ind.equals("yes")) {
//			listLevel--;
//			list.closeLastList();
//			para.addList(list, listFont, null);
//		}
//
//		if (Para_end_ind.equals("yes")) {
//			try {
//				Float spacing = 1.2f;
//				if (linespace > 0) {
//					spacing = new Float(linespace);
//				}
//				TextAlignment align;
//				if ("right".equals(CELL_HAlign)) {
//					align = "rtl".equals(CELL_dir) ? TextAlignment.LEFT
//							: TextAlignment.RIGHT;
//				} else if ("center".equals(CELL_HAlign)) {
//					align = TextAlignment.CENTER;
//				} else {
//					align = "rtl".equals(CELL_dir) ? TextAlignment.RIGHT
//							: TextAlignment.LEFT;
//				}
//				System.out.println("*** adding paragraph: align = "
//						+ align.toString());
//				System.out.println("*** adding paragraph, lineheight = "
//						+ new Float(para.getLineHeight()).toString());
//////				para.addToDocument(cb, (int) Math.round(cellHeight), spacing,
//////						0f, 0f, align, (float) left
//////								+ sizeConvert_negative(CELL_marginLeft),
//////						(float) top + (float) cellHeight, (float) left
//////								+ (float) cellWidth
////								- sizeConvert_negative(CELL_marginRight),
////						(float) top, borderParams.borderLeft(),
////						borderParams.borderTop(), borderParams.borderRight(),
////						borderParams.borderBottom());
//			} catch (Exception e) {
//				System.out.println("Exception closing paragraph: "
//						+ il.co.aman.apps.Misc.message(e));
//			}
//			inParagraph = false;
//		}
//
//		return new ItextResult(_ERROR_CODE, _ERROR_MSG, _RESULT, null, false);
		return null;
	}

	public static ItextResult imageContent(String WORD_INSERT_TYPE,
			String IMG_PATH, String CELL_CONTENT, double CELL_LEFT,
			double CELL_TOP, double CELL_height, double CELL_width,
			String CELL_URL, byte[] WORD_INSERT_PDF, BorderParams borderParams,
			String CELL_alt_text) {
		double top = (double) pageHeight - (double) sizeConvert(CELL_TOP)
				- sizeConvert(CELL_height);
		double left = (double) sizeConvert(CELL_LEFT);
		String _ERROR_CODE = "", _ERROR_MSG = "";
		if (!"PDF".equals(WORD_INSERT_TYPE)) {
			try {
				Image img = getImage(fixPath(IMG_PATH, CELL_CONTENT));
				if (img != null) {
					if (pdf.isTagged()) {
						addImage((int)CELL_LEFT, (int)CELL_TOP, (int)CELL_height,
								(int)CELL_width, img, CELL_URL, CELL_alt_text);
					} else {
						addImage((int)CELL_LEFT, (int)CELL_TOP, (int)CELL_height,
								(int)CELL_width, img, CELL_URL, null);
					}
				}
			} catch (Exception e) {
				_ERROR_CODE = "2";
				_ERROR_MSG = e.getMessage();
			}
		} /*else { // A Word Insert object which is to be embedded as a PDF page,
					// not an image (to save space and improve the quality of
					// the insert)
			try {
				float x = sizeConvert(CELL_LEFT);
				float y = pageHeight - sizeConvert(CELL_TOP)
						- sizeConvert(CELL_height);

				il.co.aman.itextimpl.PdfLayers.addLayer(cb,
						new com.itextpdf.text.pdf.PdfReader(WORD_INSERT_PDF),
						writer, 1, x, y);
			} catch (Exception e) {
				_ERROR_CODE = "2";
				_ERROR_MSG = e.getMessage();
			}
		}*/
		Border.setBorders(pdf.getPage(pageNum), (float) left, (float) top, (float) left
				+ (float) sizeConvert(CELL_width), (float) top
				+ (float) sizeConvert(CELL_height), borderParams.borderLeft(),
				borderParams.borderTop(), borderParams.borderRight(),
				borderParams.borderBottom());
		return new ItextResult(_ERROR_CODE, _ERROR_MSG, "", null, false);
	}

//	public static ItextResult SVGcontent(String SVG, double CELL_LEFT,
//			double CELL_TOP) {
//		String _ERROR_CODE = "", _ERROR_MSG = "";
//		try {
//			SVGrep SVGdoc = new SVGrep(SVG, false);
//			if (SVGdoc.success) {
//				// 0.75f added by David Zalkin, February 2013, to fix problem of
//				// inconsistency of size of charts between PDF and HTML. Not yet
//				// tested.
//				float x = sizeConvert(CELL_LEFT);// / 0.75f;
//				// float diff = sizeConvert(CELL_height) - SVGdoc.dims[1];
//				// float y = pageHeight - sizeConvert(CELL_TOP) -
//				// sizeConvert(CELL_height) + diff;
//				// float y = pageHeight - sizeConvert(CELL_TOP) -
//				// sizeConvert(CELL_height);
//				float y = pageHeight - sizeConvert(CELL_TOP) - SVGdoc.dims[1]
//						* 0.75f; // why is this * 0.75f still here?????
//				SVGrep.addSVG(SVGdoc, cb, x, y);
//			}
//		} catch (Exception e) {
//			_ERROR_CODE = "2";
//			_ERROR_MSG = e.getMessage();
//			System.out.println("**** SVG error: " + e.getMessage());
//		}
//		return new ItextResult(_ERROR_CODE, _ERROR_MSG, "", null, false);
//	}
	
	public static ItextResult barcodeContent(double left, double top,
			CssParser.Style barcodeStyle/*, double CELL_LEFT,
			double CELL_TOP*/, AdditionalMetadata.Metadata md, String CELL_CONTENT,
			String CELL_URL, String CELL_FONT) {
		String CELL_BARCODE_TYPE = md.cellBarcodeType();
		String BARCODE_CHARSET = md.barcodeCharset();
		String QR_CHARSET = md.qrCharset();
		double CELL_BAR_WIDTH = Double.parseDouble(md.cellBarWidth());
		String CELL_text_visible = md.cellTextVisible();
		return barcodeContent(left, top, (double)barcodeStyle.width(), (double)barcodeStyle.height(), 
				barcodeStyle.getBorder(), (double)sizeReConvert(barcodeStyle.height()), barcodeStyle.fontSize()/*, CELL_LEFT, CELL_TOP*/,
				CELL_BARCODE_TYPE, CELL_CONTENT, BARCODE_CHARSET, (double)barcodeStyle.width(), CELL_URL, QR_CHARSET, CELL_BAR_WIDTH, CELL_FONT,
				CELL_text_visible);
	}
	
	public static float sizeReConvert(Float size) {
		if (size == null) {
			return 0f;
		} else if (size > 0) {
			return size * 2.54f / 72f;
		} else {
			return 0f;
		}
	}

	public static ItextResult barcodeContent(double left, double top,
			double cellWidth, double cellHeight, BorderParams borderParams,
			double CELL_height, double CELL_FONTSIZE/*, double CELL_LEFT,
			double CELL_TOP*/, String CELL_BARCODE_TYPE, String CELL_CONTENT,
			String BARCODE_CHARSET, double CELL_width, String CELL_URL,
			String QR_CHARSET, double CELL_BAR_WIDTH, String CELL_FONT,
			String CELL_text_visible) {
		String _ERROR_CODE = "", _ERROR_MSG = "";
		try {
			Border.setBorders(pdf.getPage(pageNum), (float) left, (float) top, (float) left
					+ (float) cellWidth, (float) top + (float) cellHeight,
					borderParams.borderLeft(), borderParams.borderTop(),
					borderParams.borderRight(), borderParams.borderBottom());

			float size = sizeConvert(CELL_height) - (float) CELL_FONTSIZE
					* 1.2f;
			float x = sizeConvert(left);
			float y = pageHeight - sizeConvert(top)
					- sizeConvert(CELL_height);
			int barcodetype = Integer.parseInt(CELL_BARCODE_TYPE.trim());
			Barcode1D barcode1d = null;
			switch (barcodetype) {
			case 1:
				barcode1d = new BarcodeCodabar(pdf);
				break;
			case 2:
				barcode1d = new Barcode128(pdf);
				break;
			case 3:
				barcode1d = new Barcode39(pdf);
				break;
			case 4:
				barcode1d = new BarcodePostnet(pdf);
				break;
			case 5:
				int square = (int) Math.min(CELL_height, CELL_width);
				Image img = createDataMatrix(CELL_CONTENT, square, square, BARCODE_CHARSET);
				addImage((int)left, (int)top, square, square, img, 
						CELL_URL, "Barcode");
				break;
			case 6:
				barcode1d = new BarcodeInter25(pdf);
				break;
			case 7:
//				img = createQR(CELL_CONTENT, QR_CHARSET);
//				square = Math.min(CELL_height, CELL_width);
//				addImage(img, CELL_LEFT, CELL_TOP, square, square,
//						CELL_URL, null);
				break;
			default:
				System.out.println("Invalid barcode type: " + CELL_BARCODE_TYPE);
				break;
			}
			if (barcode1d != null) {
				barcode1d.setX(new Float(CELL_BAR_WIDTH));
				barcode1d.setFont(fontBank.getFont(CELL_FONT));
				doBarCode(barcode1d, CELL_CONTENT, size, x, y,
						CELL_text_visible.equals("1"), barcodetype == 1);
			}
		} catch (Exception e) {
			_ERROR_CODE = "2";
			_ERROR_MSG = e.getMessage();
		}
		return new ItextResult(_ERROR_CODE, _ERROR_MSG, "", null, false);

	}

	public static ItextResult popupContent() {
		String _ERROR_CODE = "", _ERROR_MSG = "";
		try {
			// System.out.println("****POPUP: " + CELL_CONTENT);
		} catch (Exception e) {
			_ERROR_CODE = "2";
			_ERROR_MSG = e.getMessage();
		}
		return new ItextResult(_ERROR_CODE, _ERROR_MSG, "", null, false);
	}

	public static ItextResult formContent(FormContentParams params) {
		return formContent(params.CONTENT_TYPE, params.visible,
				params.CELL_form_name, params.CELL_CONTENT,
				params.CELL_FONTSIZE, params.CELL_form_border_width,
				params.CELL_form_border_style, params.CELL_FONT,
				params.CELL_color, params.CELL_form_border_color,
				params.CELL_backgroundColor, params.left, params.top,
				params.cellWidth, params.cellHeight, params.CELL_HAlign,
				params.CELL_button_action, params.IMG_PATH, params.JAVA_SCRIPT,
				params.CELL_list_value, params.CELL_form_default_selected,
				params.CELL_form_date_format, params.CELL_form_radiobutton_name);
	}

	public static ItextResult formContent(String CONTENT_TYPE, String visible,
			String CELL_form_name, String CELL_CONTENT, double CELL_FONTSIZE,
			double CELL_form_border_width, String CELL_form_border_style,
			String CELL_FONT, String CELL_color, String CELL_form_border_color,
			String CELL_backgroundColor, double left, double top,
			double cellWidth, double cellHeight, String CELL_HAlign,
			String CELL_button_action, String IMG_PATH, String JAVA_SCRIPT,
			String CELL_list_value, String CELL_form_default_selected,
			String CELL_form_date_format, String CELL_form_radiobutton_name) {
		if ((CONTENT_TYPE.equals("textbox") || CONTENT_TYPE.equals("textarea"))
				&& "true".equals(visible)) {
			int numlines = CONTENT_TYPE.equals("textbox") ? 1 : 2; // any value
																	// more than
																	// 1 means
																	// multiline
			/*if (!frmwriter.addField(FormParams.getTextParams(CELL_form_name,
					CELL_CONTENT, numlines, (int) CELL_FONTSIZE,
					sizeConvert(CELL_form_border_width),
					SAXimpl.borderstyleNames.get(CELL_form_border_style),
					CELL_FONT, CELL_FONT, CELL_color, CELL_form_border_color,
					CELL_backgroundColor, (float) left, (float) top,
					(float) left + (float) cellWidth, (float) top
							+ (float) cellHeight,
					SAXimpl.alignmentTypes.get(CELL_HAlign)))) {
			}*/
		} else if (CONTENT_TYPE.equals("textbox")
				|| CONTENT_TYPE.equals("textarea")) {
			System.out.println("Adding hidden field. Result:");
			System.out.println(CELL_form_name);
			System.out.println(CELL_CONTENT);
//			frmwriter.addField(FormParams.getHiddenParams(CELL_form_name,
//					CELL_CONTENT, (float) left, (float) top, (float) left
//							+ (float) cellWidth, (float) top
//							+ (float) cellHeight));
		} else if (CONTENT_TYPE.equals("button")) {
			boolean res = true;
			if ("Submit".equals(CELL_button_action)) {
//				res = frmwriter.addField(FormParams.getSubmitParams(
//						CELL_form_name, url, fixPath(IMG_PATH, CELL_CONTENT),
//						fixPath(IMG_PATH, CELL_CONTENT), (float) left,
//						(float) top, (float) left + (float) cellWidth,
//						(float) top + (float) cellHeight));
			} else if ("Reset".equals(CELL_button_action)) {// System.out.println("------------------"
															// +
															// fixPath(IMG_PATH,
															// CELL_CONTENT));
//				res = frmwriter.addField(FormParams.getResetParams(
//						CELL_form_name, fixPath(IMG_PATH, CELL_CONTENT),
//						fixPath(IMG_PATH, CELL_CONTENT), (float) left,
//						(float) top, (float) left + (float) cellWidth,
//						(float) top + (float) cellHeight));
			} else if ("Clear".equals(CELL_button_action)) {
//				res = frmwriter.addField(FormParams.getClearParams(
//						CELL_form_name, fixPath(IMG_PATH, CELL_CONTENT),
//						fixPath(IMG_PATH, CELL_CONTENT), (float) left,
//						(float) top, (float) left + (float) cellWidth,
//						(float) top + (float) cellHeight));
			} else if ("AdvancedSubmit".equals(CELL_button_action)) {
				FormParams advSubBtn = FormParams.getSubmitParams(
						CELL_form_name, url, fixPath(IMG_PATH, CELL_CONTENT),
						fixPath(IMG_PATH, CELL_CONTENT), (float) left,
						(float) top, (float) left + (float) cellWidth,
						(float) top + (float) cellHeight);
				advSubBtn.submitFields = "submitFields";
				advSubBtn.preCode = "this.getField('xml_data').value = getXML(); popInternalParams(); this.getField('button_name').value = '"
						+ CELL_form_name
						+ "'; this.getField('ws_name').value = this.getField('WS2').value; ";
				//res = frmwriter.addField(advSubBtn);

				if (!bHasAdvancedJS) {
//					boolean res1 = frmwriter.addField(FormParams
//							.getHiddenParams("WS", "form_request"));
//					boolean res2 = frmwriter.addField(FormParams
//							.getHiddenParams("xml_data", ""));
//					boolean res3 = frmwriter.addField(FormParams
//							.getHiddenParams("doc_id", ""));
//					boolean res4 = frmwriter.addField(FormParams
//							.getHiddenParams("letter_id", ""));
//					boolean res5 = frmwriter.addField(FormParams
//							.getHiddenParams("session_id", ""));
//					boolean res6 = frmwriter.addField(FormParams
//							.getHiddenParams("env_name", ""));
//					boolean res7 = frmwriter.addField(FormParams
//							.getHiddenParams("button_name", ""));
//					boolean res8 = frmwriter.addField(FormParams
//							.getHiddenParams("ws_name", ""));
//					FormParams advSubJS = new FormParams();
//					advSubJS.javascript = JAVA_SCRIPT;
//					advSubJS.type = FormParams.ElementType.JAVASCRIPT;
//					boolean JSres = frmwriter.addField(advSubJS);
//					bHasAdvancedJS = JSres;
//					res = res && JSres && res1 && res2 && res3 && res4 && res5
//							&& res6 && res7 && res8;
				}
			}
			if (!res) {
//				System.out
//						.println(CELL_form_name + ": " + frmwriter.getError());
			}
		} else if (CONTENT_TYPE.equals("combobox")) {
			String[] vals = OptionsParse.getVals(CELL_list_value);
//			if (!frmwriter.addField(FormParams.getComboParams(CELL_form_name,
//					vals[0], vals[1], sizeConvert(CELL_form_border_width),
//					CELL_form_border_color,
//					SAXimpl.borderstyleNames.get(CELL_form_border_style),
//					CELL_backgroundColor, (float) left, (float) top,
//					(float) left + (float) cellWidth, (float) top
//							+ (float) cellHeight, Integer.parseInt(vals[2])))) {
//				System.out
//						.println(CELL_form_name + ": " + frmwriter.getError());
//			}
		} else if (CONTENT_TYPE.equals("checkbox")) {// System.out.println("CHECKBOX: "
														// + CELL_form_name);
//			if (!frmwriter.addField(FormParams.getCheckParams(CELL_form_name,
//					PdfFormField.TYPE_CHECK,
//					sizeConvert(CELL_form_border_width),
//					CELL_form_border_color, CELL_backgroundColor,
//					SAXimpl.borderstyleNames.get(CELL_form_border_style),
//					(float) left, (float) top,
//					(float) left + (float) cellWidth, (float) top
//							+ (float) cellHeight,
//					"YES".equals(CELL_form_default_selected)))) {
//				System.out
//						.println(CELL_form_name + ": " + frmwriter.getError());
//			}
		} else if (CONTENT_TYPE.equals("datebox")) {
//			frmwriter.addField(FormParams.getDateParams(CELL_form_name,
//					CELL_CONTENT, CELL_form_date_format == null ? "ddmmyyyy"
//							: CELL_form_date_format, (int) CELL_FONTSIZE,
//					sizeConvert(CELL_form_border_width),
//					SAXimpl.borderstyleNames.get(CELL_form_border_style),
//					CELL_FONT, CELL_FONT, CELL_color, CELL_form_border_color,
//					CELL_backgroundColor, (float) left, (float) top,
//					(float) left + (float) cellWidth, (float) top
//							+ (float) cellHeight, SAXimpl.alignmentTypes
//							.get(CELL_HAlign)));
		} else if (CONTENT_TYPE.equals("emailbox")) {
//			frmwriter.addField(FormParams.getEmailParams(CELL_form_name,
//					CELL_CONTENT, (int) CELL_FONTSIZE,
//					sizeConvert(CELL_form_border_width),
//					SAXimpl.borderstyleNames.get(CELL_form_border_style),
//					CELL_FONT, CELL_FONT, CELL_color, CELL_form_border_color,
//					CELL_backgroundColor, (float) left, (float) top,
//					(float) left + (float) cellWidth, (float) top
//							+ (float) cellHeight,
//					SAXimpl.alignmentTypes.get(CELL_HAlign)));
		} else if (CONTENT_TYPE.equals("radiobox")) {
//			if (!frmwriter.addField(FormParams.getRadioParams(CELL_form_name,
//					CELL_form_radiobutton_name, CELL_CONTENT, "",
//					FormParams.TextLocation.RIGHT, 1, "Helvetica", "0,0,0",
//					sizeConvert(CELL_form_border_width),
//					CELL_form_border_color, CELL_backgroundColor,
//					SAXimpl.borderstyleNames.get(CELL_form_border_style),
//					(float) left, (float) top,
//					(float) left + (float) cellWidth, (float) top
//							+ (float) cellHeight,
//					SAXimpl.alignmentTypes.get(CELL_HAlign),
//					"YES".equals(CELL_form_default_selected)))) {
//				System.out
//						.println(CELL_form_name + ": " + frmwriter.getError());
//			}
		} else if (CONTENT_TYPE.equals("fileupload")) {

		}
		return null;
	}

	public static ItextResult tableContent(int CELL_HEADER_BOLD,
			int CELL_HEADER_ITALIC, double cellWidth, String CELL_color,
			double CELL_FONTSIZE, String CELL_FONT, int CELL_HEADER_ROWS,
			int CELL_TABLE_COLUMNS, int CELL_TABLE_ROWS, String FONTS_DIR,
			String CELL_CONTENT, double left, double top) {
//		System.out.println("==================== TABLE ====================");
//		int headerstyle = Font.BOLD * CELL_HEADER_BOLD ^ Font.ITALIC
//				* CELL_HEADER_ITALIC;
//		XmlTableParams params = new XmlTableParams((float) cellWidth,
//				Border.parseColor(CELL_color), (float) CELL_FONTSIZE,
//				CELL_FONT, CELL_HEADER_ROWS, headerstyle, CELL_TABLE_COLUMNS,
//				CELL_TABLE_ROWS);
//		XmlTable2Pdf xmltable2pdf = new XmlTable2Pdf(FONTS_DIR);
//		xmltable2pdf.importParams(params);
//		System.out.println(CELL_CONTENT);
//		String parseRes = XmlTable2Pdf.parse(xmltable2pdf, CELL_CONTENT, false);
//		if (parseRes.equals("OK")) {
//			System.out.println("Left = " + new Double(left).toString());
//			System.out.println("Top = " + new Double(top).toString());
//			xmltable2pdf.getPdfTable().addTableAbs(cb, (float) left,
//					(float) top); // this doesn't seem to work if the width is
//									// zero, EVEN if lockedWidth is false.
//		} else { // will get here if the XML is invalid
//			System.out.println("Parsing failed: " + parseRes);
//		}
		return null;
	}

	public static boolean pause(long ms) {
		// pauses for ms milliseconds. returns true if successful.
		try {
			Thread.sleep(ms);
			return true;
		} catch (InterruptedException e) {
			return false;
		}
	}

	// End of 'Helper code' snippet.
	private void generateRow() {
	}

	private StringBuffer strBuf = new StringBuffer();
	private byte[] STREAM_OUT;
	private String path;
	private String RESULT;
	private String ERROR_CODE;
	private String ERROR_MSG;
	private String Reserve1;
	private String Reserve2;
	private String Reserve3;
	private String Message_id;

	/**
	 * This function is called by PowerCenter engine when data is available for
	 * the input group.
	 *
	 * @param grp
	 *            Input group on which the data is available.
	 * @param inputBuf
	 *            Input Data buffer corresponding to the group.
	 */
	@SuppressWarnings({ "unused", "null" })
	public void execute() {

		Object defaultObj;
		String FONTS_DIR;
		String NEW_DOC;
		String DOC_UNIT;
		double DOC_PAGE_WIDTH;
		double DOC_PAGE_HEIGHT;
		String DOC_ON_LOAD_LINK;
		String DOC_LANG;
		String DOC_TITLE;
		String NEW_PAGE;
		String IMG_PATH;
		String JAVA_SCRIPT;
		String OUTPUT_TYPE;
		double CELL_width;
		double CELL_height;
		String CONTENT_TYPE;
		String CELL_CONTENT;
		String CELL_URL;
		String CELL_FONT;
		double CELL_FONTSIZE;
		String CELL_color;
		String CELL_backgroundColor;
		String CELL_textbackground;
		String CELL_FONTstyle;
		String CELL_text_decoration;
		double CELL_LEFT;
		double CELL_TOP;
		double CELL_marginLeft;
		double CELL_marginRight;
		double CELL_marginTop;
		double CELL_marginBottom;
		String CELL_HAlign;
		String CELL_dir;
		double CELL_borderLeftWidth;
		String CELL_borderLeftStyle;
		String CELL_borderLeftColor;
		double CELL_borderTopWidth;
		String CELL_borderTopStyle;
		String CELL_borderTopColor;
		double CELL_borderRightWidth;
		String CELL_borderRightStyle;
		String CELL_borderRightColor;
		double CELL_borderBottomWidth;
		String CELL_borderBottomStyle;
		String CELL_borderBottomColor;
		String CELL_wrap;
		String SVG;
		int CELL_bookmark_level;
		String CELL_bookmark_text;
		String CELL_tag_target_link_key;
		String CELL_tag_link_key;
		String CELL_cell_link;
		String CELL_tool_tip;
		String CELL_external_path;
		String CELL_form_name;
		String CELL_form_URL;
		String CELL_button_action;
		double CELL_form_border_width;
		String CELL_form_border_style;
		String CELL_form_border_color;
		String visible;
		String CELL_list_value;
		String CELL_form_date_format;
		String CELL_BARCODE_TYPE;
		String CELL_form_radiobutton_name;
		String CELL_form_default_selected;
		String CELL_text_visible;
		int CELL_colspan;
		String CELL_table_row_type;
		int CELL_tabledef_ID;
		int TABLE_WIDTH;
		String TABLE_COL_WIDTHS;
		String TABLE_BACKGROUND_COLOR;
		double TABLE_BORDER_LEFT_WIDTH;
		double TABLE_BORDER_BOTTOM_WIDTH;
		double TABLE_BORDER_RIGHT_WIDTH;
		double TABLE_BORDER_TOP_WIDTH;
		String TABLE_BORDER_LEFT_COLOR;
		String TABLE_BORDER_BOTTOM_COLOR;
		String TABLE_BORDER_RIGHT_COLOR;
		String TABLE_BORDER_TOP_COLOR;
		String TABLE_BORDER_LEFT_STYLE;
		String TABLE_BORDER_BOTTOM_STYLE;
		String TABLE_BORDER_RIGHT_STYLE;
		String TABLE_BORDER_TOP_STYLE;
		String Para_start_ind;
		String List_start_ind;
		String LI_start_ind;
		String Para_end_ind, List_end_ind, LI_end_ind;
		double CELL_justify_spacing;
		double linespace;
		String WORD_INSERT_TYPE;
		int CELL_HEADER_BOLD, CELL_HEADER_ITALIC, CELL_HEADER_ROWS, CELL_TABLE_COLUMNS, CELL_TABLE_ROWS, CELL_BAR_WIDTH;
		String BARCODE_CHARSET;
		String QR_CHARSET;
		byte[] WORD_INSERT_PDF;
		String optimize_ind;
		// String tag_type;
		String TAGGED_PDF_IND;
		int CELL_header_level;
		String ACCESS_MD_TYPE;
		String TABLE_IND;
		String CELL_alt_text;

		for (int jtx_row_counter = 1; jtx_row_counter <= 55; jtx_row_counter++) {
			try {

				STREAM_OUT = null;
				RESULT = null;
				ERROR_CODE = null;
				ERROR_MSG = null;

				FONTS_DIR = null;
				NEW_DOC = null;
				DOC_UNIT = null;
				DOC_PAGE_WIDTH = 0;
				DOC_PAGE_HEIGHT = 0;
				DOC_ON_LOAD_LINK = null;
				DOC_LANG = null;
				DOC_TITLE = null;
				NEW_PAGE = null;
				IMG_PATH = null;
				JAVA_SCRIPT = null;
				OUTPUT_TYPE = null;
				CELL_width = 0;
				CELL_height = 0;
				CONTENT_TYPE = null;
				CELL_CONTENT = null;
				CELL_URL = null;
				CELL_FONT = null;
				CELL_FONTSIZE = 0;
				CELL_color = null;
				CELL_backgroundColor = null;
				CELL_textbackground = null;
				CELL_FONTstyle = null;
				CELL_text_decoration = null;
				CELL_LEFT = 0;
				CELL_TOP = 0;
				CELL_marginLeft = 0;
				CELL_marginRight = 0;
				CELL_marginTop = 0;
				CELL_marginBottom = 0;
				CELL_HAlign = null;
				CELL_dir = null;
				CELL_borderLeftWidth = 0;
				CELL_borderLeftStyle = null;
				CELL_borderLeftColor = null;
				CELL_borderTopWidth = 0;
				CELL_borderTopStyle = null;
				CELL_borderTopColor = null;
				CELL_borderRightWidth = 0;
				CELL_borderRightStyle = null;
				CELL_borderRightColor = null;
				CELL_borderBottomWidth = 0;
				CELL_borderBottomStyle = null;
				CELL_borderBottomColor = null;
				CELL_wrap = null;
				SVG = null;
				CELL_bookmark_level = 0;
				CELL_bookmark_text = null;
				CELL_tag_target_link_key = null;
				CELL_tag_link_key = null;
				CELL_cell_link = null;
				CELL_tool_tip = null;
				CELL_external_path = null;
				CELL_form_name = null;
				CELL_form_URL = null;
				CELL_button_action = null;
				CELL_form_border_width = 0;
				CELL_form_border_style = null;
				CELL_form_border_color = null;
				visible = null;
				CELL_list_value = null;
				CELL_form_date_format = null;
				CELL_BARCODE_TYPE = null;
				CELL_form_radiobutton_name = null;
				CELL_form_default_selected = null;
				CELL_text_visible = null;
				CELL_colspan = 0;
				CELL_table_row_type = null;
				CELL_tabledef_ID = 0;
				TABLE_WIDTH = 0;
				TABLE_COL_WIDTHS = null;
				TABLE_BACKGROUND_COLOR = null;
				TABLE_BORDER_LEFT_WIDTH = 0;
				TABLE_BORDER_BOTTOM_WIDTH = 0;
				TABLE_BORDER_RIGHT_WIDTH = 0;
				TABLE_BORDER_TOP_WIDTH = 0;
				TABLE_BORDER_LEFT_COLOR = null;
				TABLE_BORDER_BOTTOM_COLOR = null;
				TABLE_BORDER_RIGHT_COLOR = null;
				TABLE_BORDER_TOP_COLOR = null;
				TABLE_BORDER_LEFT_STYLE = null;
				TABLE_BORDER_BOTTOM_STYLE = null;
				TABLE_BORDER_RIGHT_STYLE = null;
				TABLE_BORDER_TOP_STYLE = null;
				path = null;
				Reserve1 = null;
				Reserve2 = null;
				Reserve3 = null;
				Message_id = null;
				Para_start_ind = null;
				List_start_ind = null;
				LI_start_ind = null;
				Para_end_ind = null;
				List_end_ind = null;
				LI_end_ind = null;
				CELL_justify_spacing = 0.0;
				WORD_INSERT_TYPE = null;
				linespace = 0;
				CELL_HEADER_BOLD = 0;
				CELL_HEADER_ITALIC = 0;
				CELL_HEADER_ROWS = 0;
				CELL_TABLE_COLUMNS = 0;
				CELL_TABLE_ROWS = 0;
				CELL_BAR_WIDTH = 0;
				BARCODE_CHARSET = null;
				QR_CHARSET = null;
				WORD_INSERT_PDF = null;
				optimize_ind = null;
				// tag_type = null;
				TAGGED_PDF_IND = "no";
				CELL_header_level = 0;
				ACCESS_MD_TYPE = "";
				TABLE_IND = "N";
				CELL_alt_text = null;

				ex = null;

				// Start of 'On Input Row' code snippet.

				ex = new Exception("What's up, doc? Main body of code.");
				if (!discard | NEW_DOC.equals("start")) {
					init(FONTS_DIR, CELL_form_URL);
					if (NEW_DOC != null) {

						if (NEW_DOC.equals("start")) {
							ItextResult result = newDoc(path, tabledef,
									optimize_ind, DOC_UNIT, DOC_PAGE_HEIGHT,
									DOC_PAGE_WIDTH, OUTPUT_TYPE,
									DOC_ON_LOAD_LINK, TAGGED_PDF_IND, DOC_LANG,
									DOC_TITLE);
							if (result.generateRow()) {
								ERROR_CODE = result.errorCode();
								ERROR_MSG = result.errorMsg();
								RESULT = result.result();
								generateRow();
							}
							if (result.streamOut() != null) {
								STREAM_OUT = result.streamOut();
							}

						} else if (NEW_DOC.equals("end")) {
							ItextResult result = endDoc(path, tabledef,
									optimize_ind);
							if (result.generateRow()) {
								ERROR_CODE = result.errorCode();
								ERROR_MSG = result.errorMsg();
								RESULT = result.result();
								STREAM_OUT = result.streamOut();
								generateRow();
							}
						}

						if (NEW_PAGE.equals("yes") && doc != null) {
							ItextResult result = newPage(tabledef, DOC_PAGE_HEIGHT, DOC_PAGE_WIDTH);
							RESULT = result.result();
						}

						// Here we process the cell content
						if (CELL_CONTENT != null && CELL_width > 0
								&& CELL_height > 0 && doc != null) {

							handleParagraphs(Para_start_ind, CELL_dir,
									linespace, List_start_ind, CELL_FONTstyle,
									CELL_text_decoration, CELL_FONT,
									CELL_FONTSIZE, CELL_color, LI_start_ind);

							double cellWidth = (double) sizeConvert(CELL_width);
							double cellHeight = (double) sizeConvert(CELL_height);
							double top = (double) pageHeight - (double) sizeConvert(CELL_TOP) - cellHeight;
							double left = (double) sizeConvert(CELL_LEFT);
							float fTop = pageHeight - sizeConvert(CELL_TOP) - sizeConvert(CELL_height);
							float fLeft = sizeConvert(CELL_LEFT);

							if (CONTENT_TYPE.equals("text")
									| CONTENT_TYPE.equals("userdef")
									| CONTENT_TYPE.equals("signpad")
									| CONTENT_TYPE.equals("paragraph")) {
								textContent(CONTENT_TYPE, CELL_tabledef_ID,
										CELL_FONT, CELL_FONTSIZE,
										CELL_FONTstyle, CELL_text_decoration,
										CELL_colspan, CELL_CONTENT, CELL_color,
										CELL_backgroundColor, new TableParams(
												CELL_table_row_type,
												TABLE_COL_WIDTHS,
												TABLE_BACKGROUND_COLOR, fLeft,
												fTop, TABLE_BORDER_LEFT_WIDTH,
												TABLE_BORDER_BOTTOM_WIDTH,
												TABLE_BORDER_RIGHT_WIDTH,
												TABLE_BORDER_TOP_WIDTH,
												TABLE_BORDER_LEFT_COLOR,
												TABLE_BORDER_BOTTOM_COLOR,
												TABLE_BORDER_RIGHT_COLOR,
												TABLE_BORDER_TOP_COLOR,
												TABLE_BORDER_LEFT_STYLE,
												TABLE_BORDER_BOTTOM_STYLE,
												TABLE_BORDER_RIGHT_STYLE,
												TABLE_BORDER_TOP_STYLE),
										new BorderParams(CELL_borderLeftWidth,
												CELL_borderTopWidth,
												CELL_borderRightWidth,
												CELL_borderBottomWidth,
												CELL_borderLeftColor,
												CELL_borderTopColor,
												CELL_borderRightColor,
												CELL_borderBottomColor,
												CELL_borderLeftStyle,
												CELL_borderTopStyle,
												CELL_borderRightStyle,
												CELL_borderBottomStyle),
										CELL_HAlign, CELL_dir,
										CELL_justify_spacing, left, top,
										cellWidth, cellHeight, CELL_wrap,
										CELL_textbackground,
										CELL_bookmark_level,
										CELL_tag_target_link_key,
										CELL_tag_link_key, CELL_external_path,
										CELL_cell_link, CELL_tool_tip,
										CELL_bookmark_text, CELL_marginTop,
										CELL_marginLeft, CELL_marginRight,
										CELL_marginBottom, linespace,
										LI_end_ind, List_end_ind, Para_end_ind,
										CELL_header_level, ACCESS_MD_TYPE,
										TABLE_IND, CELL_alt_text);

							} else if (CONTENT_TYPE.equals("image")) {
								imageContent(WORD_INSERT_TYPE, IMG_PATH,
										CELL_CONTENT, CELL_LEFT, CELL_TOP,
										CELL_height, CELL_width, CELL_URL,
										WORD_INSERT_PDF, /*
														 * left, top, cellWidth,
														 * cellHeight,
														 */
										new BorderParams(CELL_borderLeftWidth,
												CELL_borderTopWidth,
												CELL_borderRightWidth,
												CELL_borderBottomWidth,
												CELL_borderLeftColor,
												CELL_borderTopColor,
												CELL_borderRightColor,
												CELL_borderBottomColor,
												CELL_borderLeftStyle,
												CELL_borderTopStyle,
												CELL_borderRightStyle,
												CELL_borderBottomStyle),
										CELL_alt_text);

							} else if (CONTENT_TYPE.equals("SVG")) {
								//SVGcontent(SVG, CELL_LEFT, CELL_TOP);

							} else if (CONTENT_TYPE.equals("barcode")) {
								barcodeContent(left, top, cellWidth,
										cellHeight, new BorderParams(
												CELL_borderLeftWidth,
												CELL_borderTopWidth,
												CELL_borderRightWidth,
												CELL_borderBottomWidth,
												CELL_borderLeftColor,
												CELL_borderTopColor,
												CELL_borderRightColor,
												CELL_borderBottomColor,
												CELL_borderLeftStyle,
												CELL_borderTopStyle,
												CELL_borderRightStyle,
												CELL_borderBottomStyle),
										CELL_height, CELL_FONTSIZE/*, CELL_LEFT,
										CELL_TOP*/, CELL_BARCODE_TYPE,
										CELL_CONTENT, BARCODE_CHARSET,
										CELL_width, CELL_URL, QR_CHARSET,
										CELL_BAR_WIDTH, "Arial",
										CELL_text_visible);

							} else if (CONTENT_TYPE.equals("popup")) {
								popupContent();

							} else if (CONTENT_TYPE.equals("textbox")
									|| CONTENT_TYPE.equals("textarea")
									|| CONTENT_TYPE.equals("button")
									|| CONTENT_TYPE.equals("combobox")
									|| CONTENT_TYPE.equals("checkbox")
									|| CONTENT_TYPE.equals("datebox")
									|| CONTENT_TYPE.equals("emailbox")
									|| CONTENT_TYPE.equals("radiobox")
									|| CONTENT_TYPE.equals("fileupload")) {
								formContent(CONTENT_TYPE, visible,
										CELL_form_name, CELL_CONTENT,
										CELL_FONTSIZE, CELL_form_border_width,
										CELL_form_border_style, CELL_FONT,
										CELL_color, CELL_form_border_color,
										CELL_backgroundColor, left, top,
										cellWidth, cellHeight, CELL_HAlign,
										CELL_button_action, IMG_PATH,
										JAVA_SCRIPT, CELL_list_value,
										CELL_form_default_selected,
										CELL_form_date_format,
										CELL_form_radiobutton_name);

							} else if (CONTENT_TYPE.equals("table")) {
								tableContent(CELL_HEADER_BOLD,
										CELL_HEADER_ITALIC, cellWidth,
										CELL_color, CELL_FONTSIZE, CELL_FONT,
										CELL_HEADER_ROWS, CELL_TABLE_COLUMNS,
										CELL_TABLE_ROWS, FONTS_DIR,
										CELL_CONTENT, left, top);

							} else {
								System.out
										.println("ERROR: invalid CONTENT_TYPE value -- '"
												+ CONTENT_TYPE + "'");
								ERROR_CODE = "1";
								ERROR_MSG = "ERROR: invalid CONTENT_TYPE value -- '"
										+ CONTENT_TYPE + "'";
								generateRow();
							}
						}

					} else { // null record
						System.out.println("&&&&&&&&&&&&&&&&&&&& null record!");
						ERROR_CODE = "17";
						ERROR_MSG = "NULL RECORD.";
						generateRow();
					}
				}

				// End of 'On Input Row' code snippet.
				// Start of 'On End of Data' code snippet.
				Exception ex2 = new Exception("What's up, doc?");
				if (doc != null) {
					boolean bClosed = false;
					try {
						////frmwriter.finish();
						if (currentTable > -1) {
							System.out.println("End of data");
//							Exception e = endTable(cb,
//									tabledef.getBorder_bottom());
//							if (e != null) {
//								System.out.println(e);
//							}
						}
						doc.close();
						writer.close();
						path = filepath;
						RESULT = "Document closed.";
						ERROR_MSG = "Last document closed.";
						if (streamOut != null) {
							path = "stream";
							STREAM_OUT = streamOut.toByteArray();
						}
						bClosed = true;
					} catch (Exception e) {
						ex2 = e;
						System.out.println("ERROR on last row: "
								+ e.getMessage());
					}
					if (!bClosed) {
						errorClosing(doc, path, ex2, output_type);
					}
					generateRow();
				}
				discard = false;

				// End of 'On End of Data' code snippet.
			} catch (Exception e) {
			}
		}
	}

	public static void main(String[] args) {
		java.security.CodeSource src = org.apache.batik.dom.svg.SAXSVGDocumentFactory.class
				.getProtectionDomain().getCodeSource();
		// or: java.security.CodeSource src =
		// Class.forName("org.apache.commons.codec.binary.Base64").getProtectionDomain().getCodeSource();
		// but then need to catch ClassNotFoundException
		if (src != null) {
			java.net.URL jar = src.getLocation();
			System.out.println(jar.toString());
		}
	}
}
