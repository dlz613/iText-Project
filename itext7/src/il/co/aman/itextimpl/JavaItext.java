package il.co.aman.itextimpl;

// Start of 'Import Package' snippet.
import java.awt.Color;
import java.awt.Graphics2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.text.Bidi;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfTextMarkupAnnotation;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.element.AbstractElement;
import com.itextpdf.layout.element.IElement;
//import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Link;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfTextFormField;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.property.AreaBreakType;
import com.itextpdf.layout.property.BaseDirection;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import com.itextpdf.layout.renderer.ParagraphRenderer;
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
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfBoolean;
//import com.itextpdf.text.pdf.PdfAppearance;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfOutline;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfViewerPreferences;
import com.itextpdf.kernel.pdf.PdfViewerPreferences.PdfViewerPreferencesConstants;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
//import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.kernel.pdf.PdfWriter;
//import com.itextpdf.text.pdf.RadioCheckField;
import com.itextpdf.barcodes.qrcode.EncodeHintType;

import il.co.aman.apps.FontMeasurements;
import il.co.aman.apps.Misc;
import il.co.aman.apps.XsltTransform;

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
import il.co.aman.itextimpl.CssParser.Style;
import il.co.aman.itextimpl.FormParams;
import il.co.aman.itextimpl.FormWriter;
import il.co.aman.itextimpl.IndexParser.CellIndex;
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

import il.co.aman.itextimpl.ArchiveDataParser.ArchiveField;
import il.co.aman.itextimpl.ArchiveDataParser.ArchivePage;
import il.co.aman.itextimpl.IndexParser.TagIndex;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;

import javax.xml.transform.stream.StreamSource;

// End of 'Import Package' snippet.
public class JavaItext {
	
	// Start of 'Helper code' snippet.
	public Document doc = null;
	PdfDocument pdf = null;
	PdfWriter writer;
	public ByteArrayOutputStream streamOut = null;
	float pageHeight;
	static float divisor = 2.54f;
	String filepath;
	Integer pageNum;
	public GetFonts fontBank = null;
	Map<String, Image> images = new HashMap<String, Image>();
	boolean discard = false; // if the document can't be created, discard
								// all of its records
	String output_type;
	PdfOutline[] parents = new PdfOutline[] { null, null, null, null, null,
			null, null };
	int bookmark_num = 0;
	FormWriter frmwriter;
	static final String bookmark_ID_base = "_____&&FIT__BK";
	public static final String EMPHASIS_START = "{{B\\";
	public static final String EMPHASIS_STOP = "\\B}}";
	public static final String UNDERLINE_START = "{{U\\";
	public static final String UNDERLINE_STOP = "\\U}}";
	public static final String EMPHASIS_START_ONLY_BOLD = "{{C\\";
	public static final String EMPHASIS_STOP_ONLY_BOLD = "\\C}}";
	boolean bHasAdvancedJS; // the JavaScript to handle advanced submit has already been added to the PDF
	Exception ex;
	boolean _debug, _bookmarks, _links, _urls;
	HashMap<String, PdfFont> fonts;
	String readDir;
	public PdfOutline root;
	public DocInfo docInfo = null;
	public HiddenText hiddenText = null;

	public ArrayList<String> error_msgs = new ArrayList<String>();

	// static PdfPTable table;
	int currentTable = -1;
	int numCols, cellNum;
	float tableX, tableY;
	Integer runs = 0;
	boolean bAfterHeader;
	Color background; // the background of the whole table.
	TableDef tabledef;
	boolean inParagraph;
	int listLevel;
	il.co.aman.itextimpl.Paragraph para;
	il.co.aman.itextimpl.FakeList list;
	PdfFont listFont;
	public int svgFactor = 2; // for correcting size of SVGs (to improve quality)

	//PdfStructElem eTop;

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

	public static com.itextpdf.layout.Style parseStyle(String style,
			String font_name) {
		com.itextpdf.layout.Style res = new com.itextpdf.layout.Style();
		style = style.toLowerCase();
		if ((style.contains("bold"))
				&& !font_name.toUpperCase().contains("BOLD")) { // assuming that
																// all "real"
																// bold fonts
																// have the word
																// bold in their
																// name
			res.setBold();
		}
		if (style.contains("italic")) {
			res.setItalic();
		}
		if (style.contains("underline")) {
			res.setUnderline();
		}
		if (style.contains("line-through")) {
			res.setLineThrough();
		}
		return res;
	}

	public static double verticalLeading(double marginTop, double height, double size) {
		if (marginTop != 0) {
			return marginTop - (size * 0.7);
		} else {
			// return (height + size) * 0.5;
			return size;
		}
	}

	public Image getImage(String src) {
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
		String dir = new File(path).isDirectory() ? path : new File(
				file.getAbsolutePath()).getParent();
		return new File(dir).exists();
	}

	public void doBarCode(Barcode1D barcode, String code, float size,
			float width, float height, float x, float y, boolean visible,
			boolean setText) {
		try {
			barcode.setStartStopText(setText);
			barcode.setCode(code);
			//System.out.println(code.length());
			if (!visible) {
				barcode.setFont(null);
			}
			barcode.setBarHeight(size);
			Image img = new Image(barcode.createFormXObject(pdf));
			addImage((int) x, (int) y, (int) width, (int) height, img, null, "Barcode");
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static boolean writeMsg(Document doc, float height, String text) {
		Rectangle[] areas = new Rectangle[] { new Rectangle(50, 750, 500, 50) };
		doc.setRenderer(new ColumnDocumentRenderer(doc, areas));
		Paragraph p = new Paragraph(text);
		p.setMargin(0);
		p.setMultipliedLeading(1);
		doc.add(p);
		return true;
	}

	public static ByteArrayOutputStream createPDF(String msg)
			throws java.io.IOException {
		ByteArrayOutputStream _streamOut = new ByteArrayOutputStream();
		PdfWriter writer = new PdfWriter(_streamOut);
		PdfDocument pdf = new PdfDocument(writer);
		Document document = new Document(pdf);
		writeMsg(document, 20f, msg);
		document.close();
		return _streamOut;
	}

	public boolean createPDF(String path, String msg)
			throws java.io.FileNotFoundException, java.io.IOException {
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

	public ItextResult PDFmsg(String output_type, String path, String msg) {
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
		// if (level >= 1 && level < parents.length && parents[level - 1] !=
		// null) {
		// parents[level] = new PdfOutline(parents[level - 1],
		// PdfAction.gotoLocalPage(xref, false), label);
		// }
	}

	public String bookmark_ID() {
		return bookmark_ID_base + String.valueOf(bookmark_num++);
	}

	public static void addOnLoadLink(PdfWriter writer, String target) {
		// String js = "function onLoad() {this.gotoNamedDest(\"" + target
		// + "\");} onLoad();";
		// writer.addJavaScript(js);
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
				addIfNotEmpty(res, new String(piece));
				piece = new StringBuilder();
				piece.append(breakstart);
				i = i + breakstart.length() - 1;
			} else if (text.startsWith(breakend, i) && inside) {
				inside = false;
				addIfNotEmpty(res, new String(piece));
				piece = new StringBuilder();
				i = i + breakend.length() - 1;
			} else {
				piece.append(text.charAt(i));
			}
		}
		if (inside) { // the markup is not balanced, so ignore the starting symbol
			addIfNotEmpty(res, new String(piece).substring(breakstart.length()));
		} else {
			addIfNotEmpty(res, new String(piece));
		}

		return res;
	}
	
	private static void addIfNotEmpty(ArrayList<String> res, String val) {
		if (val.length() > 0) {
			res.add(val);
		}
	}

	public static String fixPath(String first, String second) { // prevents things like http://localhost/resources/example\42.jpg
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
		// table = new PdfPTable(colWidths(widths));
		// background = Border.parseColor(sBackground);
		// numCols = table.getNumberOfColumns();
		// cellNum = 1;
		// tableX = X;
		// tableY = Y;
		// tabledef = new TableDef(ID, colWidths(widths), sBackground,
		// border_widths, border_colors, border_styles);
		// System.out.println("New table opened. TableID = "
		// + Integer.toString(ID));
	}

	public void errorClosing(Document doc, String path, Exception ex,
			String output_type) {
		System.out.println("ERROR in closing this document: " + path);
		System.out.println(ex);
		System.out.println(ex.getMessage());
		PDFmsg(output_type, path,
				"ERROR in closing this document.\nPossible cause: the document has no pages.\n"
						+ ex.getMessage());
		doc = null;
	}

	// private static Exception endTable(PdfContentByte cb, PdfBorder
	// border_bottom) {
	// try {
	// PdfPRow lastRow = table.getRow(table.getRows().size() - 1);
	// for (PdfPCell cell : lastRow.getCells()) {
	// addBorder(border_bottom, 1, cell);
	// }
	// table.setTotalWidth(doc.right() - doc.left());
	// table.writeSelectedRows(0, -1, tableX, tableY, cb);
	// currentTable = -1;
	// System.out.println("Table closed.");
	// return null;
	// } catch (Exception e) {
	// return e;
	// }
	// }

	public static float[] colWidths(String widths) {
		String[] columns = widths.split(",");
		float[] cols = new float[columns.length];
		for (int i = 0; i < columns.length; i++) {
			cols[i] = Float.parseFloat(columns[i]);
		}
		return cols;
	}

	// public static Exception addCell(PdfContentByte cb, PdfCell cell,
	// BaseColor cell_background, int tableID, TableParams tableParams)/* String
	// table_type, String widths, String background, float X,
	// float Y, float[] border_widths, String[] border_colors, String[]
	// border_styles*/{
	// try {
	// if (tableID != currentTable) {
	// // if tabledef == -1, the previous table is ending
	// // if currentTable == -1, a new table is opening
	// // if both are != -1, the previous table is ending, and a new
	// // table is opening
	// if (currentTable > -1) {
	// System.out.println("currentTable > -1");
	// Exception e = endTable(cb, tabledef.getBorder_bottom());
	// if (e != null) {
	// System.out.println(e);
	// }
	// }
	// currentTable = tableID;
	// if (tableID > -1) {//
	// System.out.println("tableID > -1 - opening new table.");
	// newTable(tableParams.TABLE_COL_WIDTHS,
	// tableParams.TABLE_BACKGROUND_COLOR,
	// tableParams.fLeft, tableParams.fTop, tableID,
	// tableParams.borderWidths(),
	// tableParams.borderColors(),
	// tableParams.borderStyles());
	// }
	// } else if (bAfterHeader
	// && tableParams.CELL_table_row_type.equals("header")) {
	// System.out
	// .println("bAfterHeader && table_type.equals(\"header\")");
	// Exception e = endTable(cb, tabledef.getBorder_bottom());
	// if (e != null) {
	// System.out.println(e);
	// }
	// newTable(tableParams.TABLE_COL_WIDTHS,
	// tableParams.TABLE_BACKGROUND_COLOR, tableParams.fLeft,
	// tableParams.fTop, tableID, tableParams.borderWidths(),
	// tableParams.borderColors(), tableParams.borderStyles());
	// }
	// if (currentTable > -1) {
	// // System.out.println("second currentTable > -1");
	// if (background != null) {
	// cell.setBackgroundColor(tabledef.getBackground());
	// }
	// if (cell_background != null) {
	// cell.setBackgroundColor(cell_background);
	// }
	// if (cellNum % numCols == 1) {
	// addBorder(tabledef.getBorder_left(), 0, cell);
	// }
	// if ((cellNum % cell.getColspan() - 1) % numCols == 0) {
	// addBorder(tabledef.getBorder_right(), 2, cell);
	// }
	// if (cellNum <= numCols) {
	// addBorder(tabledef.getBorder_top(), 3, cell);
	// }
	// cellNum += cell.getColspan();
	// table.addCell(cell);
	// // System.out.println("Cell added to table - " +
	// // cell.getText());
	// bAfterHeader = !tableParams.CELL_table_row_type
	// .equals("header");
	// }
	// return null;
	//
	// } catch (Exception e) {
	// return e;
	// }
	//
	// }

	// public static void addBorder(PdfBorder border, int side, PdfPCell cell) {
	// if (cell != null) { // if a cell has a colspan (say 3), then the
	// // following two cells will be null
	// switch (side) {
	// case 0:
	// cell.setBorderColorLeft(border.getColor());
	// cell.setBorderWidthLeft(border.getWidth());
	// break;
	// case 1:
	// cell.setBorderColorBottom(border.getColor());
	// cell.setBorderWidthBottom(border.getWidth());
	// break;
	// case 2:
	// cell.setBorderColorRight(border.getColor());
	// cell.setBorderWidthRight(border.getWidth());
	// break;
	// case 3:
	// cell.setBorderColorTop(border.getColor());
	// cell.setBorderWidthTop(border.getWidth());
	// break;
	// }
	// }
	// }

	public Image createDataMatrix(String text, String charset) {
		BarcodeDataMatrix barcode = new BarcodeDataMatrix();
		barcode.setOptions(BarcodeDataMatrix.DM_TEXT);
		barcode.setEncoding(charset);
		barcode.setCode(text);
		return new Image(barcode.createFormXObject(
				com.itextpdf.kernel.colors.ColorConstants.BLACK, pdf))/* .scale(mw, mh) */;
	}

	public Image createDataMatrix(String text, float mw, float mh,
			String charset) {
		BarcodeDataMatrix barcode = new BarcodeDataMatrix();
		barcode.setEncoding(charset);
		barcode.setCode(text);
		return new Image(barcode.createFormXObject(com.itextpdf.kernel.colors.ColorConstants.BLACK, pdf)).scale(mw, mh);
	}

//	public Image createQR(String text, String charset) {
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

	public Image createQR(String text, String charset) {
		java.util.Map<EncodeHintType, Object> params = new
		java.util.HashMap<EncodeHintType, Object>();
		params.put(EncodeHintType.CHARACTER_SET, charset);
		BarcodeQRCode qrcode = new BarcodeQRCode(text.trim());
		return new Image(qrcode.createFormXObject(com.itextpdf.kernel.colors.ColorConstants.BLACK, this.pdf));
		//return qrcode.createAwtImage(Color.black, Color.white);
	}

	public void addImage(int CELL_LEFT, int CELL_TOP, int CELL_width,
			int CELL_height, Image img, String CELL_URL, String altText) throws UnsupportedEncodingException {
		if (Misc.notNullOrEmpty(altText)) {
			img.getAccessibilityProperties().setAlternateDescription(altText);
		}
		if (Misc.notNullOrEmpty(CELL_URL)) {
			Rectangle linkLocation = new Rectangle(CELL_LEFT, CELL_TOP,
					CELL_width, CELL_height);
			PdfLinkAnnotation annotation = new PdfLinkAnnotation(linkLocation)
					.setAction(PdfAction.createURI(CELL_URL));
			pdf.getPage(pageNum).addAnnotation(annotation);
		}
		img.scaleAbsolute(CELL_width, CELL_height);
		img.setFixedPosition(pageNum, CELL_LEFT, CELL_TOP);
		doc.add(img);
	}

	// Methods to replace (most of) the code in the 'Input Row' area

	public void init(String FONTS_DIR, boolean debug) {
		// initialize the font factory, if necessary
		if (fontBank == null) {
			fontBank = new GetFonts(FONTS_DIR);
			System.out.println("Fonts from " + FONTS_DIR + " registered.");
		}
		
		this._debug = debug;
		this._bookmarks = true;
		this._links = true;
		this._urls = true;
	}
	
	public void setState(boolean bookmarks, boolean links, boolean urls) {
		this._bookmarks = bookmarks;
		this._links = links;
		this._urls = urls;
	}

	public ItextResult newDoc(String path, TableDef tabledef,
			String optimize_ind, String DOC_UNIT, String OUTPUT_TYPE, String DOC_ON_LOAD_LINK,
			String TAGGED_PDF_IND, String language, String title, HashMap<String, PdfFont> fonts, String readDir) {
		//JavaItext.main(null);
		this.fonts = fonts;
		this.readDir = readDir;
		if (language == null) {
			language = "en-US";
		}
		if (title == null) {
			title = "FormIT Document";
		}
		String _RESULT = "OK", _ERROR_CODE = "0", _ERROR_MSG = "";
		byte[] _STREAM_OUT = null;
		boolean _generateRow = false;

		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>New Document. Output = " + path);
		images = new HashMap<String, Image>();
		try {
			filepath = path;

			if (doc != null) { // close previous document
				boolean bClosed = false;
				try {
					// frmwriter.finish();
					if (currentTable > -1) {
						System.out.println("Closing last table from previous document");
						// Exception e = endTable(cb,
						// tabledef.getBorder_bottom());
						// if (e != null) {
						// System.out.println(e);
						// }
					}
					doc.close();
					boolean tagged = pdf.isTagged();
					writer.close();
					_RESULT = "Document closed";
					if ("yes".equals(optimize_ind)) {
						if (streamOut != null) {
							path = "stream";
							_STREAM_OUT = optimizePDF(streamOut.toByteArray(), tagged);
							streamOut.close();
							streamOut = null;
						} else {
							il.co.aman.apps.Misc.writeBinFile(filepath,	optimizePDF(Misc.readBinFile(filepath), tagged));
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
						_ERROR_CODE = "1";
						_ERROR_MSG = "File cannot be opened: " + path;
						_generateRow = true;
					}
				} else {
					System.out.println("****** Output file: " + path + ": path is invalid.");
					discard = true;
					_ERROR_CODE = "2";
					_ERROR_MSG = "Invalid file path: " + path;
					_generateRow = true;
				}
			} else {
				streamOut = new ByteArrayOutputStream();
				writer = new PdfWriter(streamOut);
			}
			if (!discard) {
				pdf = new PdfDocument(writer);
				//PdfAcroForm.getAcroForm(this.pdf, true).put(PdfName.NeedAppearances, new PdfBoolean(true)); // prevents Hebrew textfield default values from disappearing until clicked
				doc = new Document(pdf);
				//System.out.println("PdfDocument object instantiated.");
	
				// writer.setPdfVersion(PdfWriter);
	
				if ("yes".equals(TAGGED_PDF_IND)) {
					pdf.setTagged(); // ** Added November 2016, to support tagged PDF. This must be set BEFORE opening the Document
	
					// writer.setUserProperties(true); // **** for some reason, this was causing Adobe to crash when opening the PDF.
				}
				pdf.getCatalog().setLang(new PdfString(language));
				pdf.getCatalog().setViewerPreferences(new PdfViewerPreferences().setDirection(readDir.equals("rtl")? PdfViewerPreferencesConstants.RIGHT_TO_LEFT: PdfViewerPreferencesConstants.LEFT_TO_RIGHT));
				pdf.getCatalog().setViewerPreferences(new PdfViewerPreferences().setDisplayDocTitle(true));
				PdfDocumentInfo info = pdf.getDocumentInfo();
				info.setTitle(title);
				pdf.getXmpMetadata();
				// //doc.open();
	
//				if ("yes".equals(TAGGED_PDF_IND)) {
//					eTop = TaggedPdf.initStructure(pdf); // This must be done AFTER opening the Document
//				} else {
//					eTop = null; // just to make sure....
//				}
				// frmwriter = new FormWriter(writer);
				// frmwriter.setDebug(true);
				bHasAdvancedJS = false;
	
				if (DOC_ON_LOAD_LINK != null && !DOC_ON_LOAD_LINK.equals("")) { // add JavaScript for onload jump to internal destination
					addOnLoadLink(writer, DOC_ON_LOAD_LINK);
				}
	
				this.root = pdf.getOutlines(false);
				this.frmwriter = new FormWriter(pdf);
	
				_RESULT = "New document.";
				// System.out.println("************************** Document opened: "
				// + path);
			}
		} catch (Exception e) {
			_ERROR_CODE = "3";
			_ERROR_MSG = e.getMessage();
			System.out.println(e.toString() + " in newDoc()");
		}
		return new ItextResult(_ERROR_CODE, _ERROR_MSG, _RESULT, _STREAM_OUT, _generateRow);
	}

	public ItextResult endDoc(String path, TableDef tabledef, String optimize_ind) {
		String _RESULT = "OK", _ERROR_CODE = "0", _ERROR_MSG = "";
		byte[] _STREAM_OUT = null;
		boolean _generateRow = false;

		System.out.println("Run number: " + (++runs).toString());

		boolean bClosed = false;
		try {
			// frmwriter.finish();
			// if (currentTable > -1) {
			// System.out.println("NEW_DOC.equals(\"end\")");
			// Exception e = endTable(cb, tabledef.getBorder_bottom());
			// if (e != null) {
			// System.out.println(e);
			// }
			// }
			//PdfAcroForm.getAcroForm(this.pdf, true).put(PdfName.NeedAppearances, new PdfBoolean(true)); // prevents Hebrew textfield default values form disappearing until clicked
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
					streamOut = new ByteArrayOutputStream(_STREAM_OUT.length);
					streamOut.write(_STREAM_OUT, 0, _STREAM_OUT.length);
					streamOut.close();
					//streamOut = null;
				} else {
					byte[] pdfbytes = Misc.readBinFile(filepath);
					byte[] opt = optimizePDF(pdfbytes, tagged);
					System.out.println("optimizing...");
					System.out.println(opt == null);
					il.co.aman.apps.Misc.writeBinFile(filepath,	opt);
				}
			} else if (streamOut != null) {
				path = "stream";
				//_STREAM_OUT = streamOut.toByteArray();
				streamOut.close();
				//streamOut = null;
			}
			_ERROR_CODE = "0";
			_ERROR_MSG = "Document created successfully.";
			if (doc != null) {
				_generateRow = true;
			}
			bClosed = true;
			doc = null; // to prevent an empty PDF after a good one from being reported as being created
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

	public static byte[] optimizePDF(byte[] pdf, boolean tagged) throws Exception {
		byte[] res;
		com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(pdf);
		System.out.println(reader);
		res = il.co.aman.formit.PdfConcat.normalizeFile(reader, tagged);
		reader.close();
		System.out.println("finishing optimization");
		return res;
	}

	public ItextResult newPage(TableDef tabledef, Double DOC_PAGE_HEIGHT, Double DOC_PAGE_WIDTH) {
		String _RESULT;
		// if (currentTable > -1) {
		// System.out.println("NEW_PAGE.equals(\"yes\")");
		// Exception e = endTable(cb, tabledef.getBorder_bottom());
		// if (e != null) {
		// System.out.println(e);
		// }
		// }
		try {
			currentTable = -1;
			float width = sizeConvert(DOC_PAGE_WIDTH);
			float height = sizeConvert(DOC_PAGE_HEIGHT);
			pageHeight = sizeConvert(DOC_PAGE_HEIGHT);
			PageSize pagesize = new PageSize(width, height);
			pdf.addNewPage(pagesize);
			pageNum++;
			_RESULT = "New page.";
			System.out.println("****************************** Page " + pageNum.toString() + " opened.");
		}
		catch (Exception e) {
			System.out.println(e);
			System.out.println(" in method newPage()");
			_RESULT = "Failed to add new page.";
		}
		return new ItextResult("", "", _RESULT, null, false);
	}

	public static ItextResult handleParagraphs(String Para_start_ind,
			String CELL_dir, double linespace, String List_start_ind,
			String CELL_FONTstyle, String CELL_text_decoration,
			String CELL_FONT, double CELL_FONTSIZE, String CELL_color,
			String LI_start_ind) {
		// if (!inParagraph) {
		// inParagraph = Para_start_ind.equals("yes");
		// if (inParagraph) {
		// int dir = PdfWriter.RUN_DIRECTION_LTR;
		// if ("rtl".equals(CELL_dir)) {
		// dir = PdfWriter.RUN_DIRECTION_RTL;
		// }
		// para = new il.co.aman.itextimpl.Paragraph(dir);
		// System.out.println("New paragraph initialized");
		// listLevel = -1;
		// para.setLineHeight((float) linespace);
		// }
		// }
		//
		// if (inParagraph && List_start_ind.equals("yes")) {
		// int style = parseStyle(CELL_FONTstyle + " " + CELL_text_decoration,
		// CELL_FONT);
		// boolean bold = (style & Font.BOLD) == Font.BOLD;
		// boolean italic = (style & Font.ITALIC) == Font.ITALIC;
		// boolean lineThrough = (style & Font.STRIKETHRU) == Font.STRIKETHRU;
		// boolean underline = (style & Font.UNDERLINE) == Font.UNDERLINE;
		// listFont = Xml2Phrases.getFont(CELL_FONT, (float) CELL_FONTSIZE,
		// CELL_color, bold, italic, lineThrough, underline);
		// listLevel++;
		// if (listLevel == 0) {
		// list = new FakeList(false, para.getDir());
		// } else {
		// list.insertList();
		// }
		// }
		//
		// if (inParagraph && LI_start_ind.equals("yes")) {
		// list.insertListElement();
		// }
		return null;
	}
	
	public boolean addSimpleText(String CELL_CONTENT, String fontFamily, float left, float top, float width, float height) {
		float fontsize = 1;
		float x_end = left + width;
		float y_end = top + height;

		ArrayList<Paragraph> phrases = new ArrayList<Paragraph>();
		ArrayList<String> texts = new ArrayList<String>();
		texts.add(reverseString(CELL_CONTENT, null, false));
		for (String text: texts) {
			Text ch = new Text(text);
			ch.setFontColor(Border.parseColor("#ffffff"));
			ch.setFont(this.fonts.get(fontFamily));
			ch.setFontSize(fontsize);
			phrases.add(new Paragraph().add(ch));									
		}
		for (Paragraph p: phrases) {
			p.setTextAlignment(TextAlignment.LEFT);						
		}
		float topLoc = y_end - fontsize;

		for (Paragraph p: phrases) { // there really should be only one.
			p.setVerticalAlignment(VerticalAlignment.MIDDLE);
			p.setFixedPosition((float) left, topLoc, (float) (x_end - left));
			p.setPageNumber(this.pageNum);
			doc.add(p);								
		}
		return true;
	}

	public ItextResult textContent(CssParser.Style cssStyle,
			String CONTENT_TYPE, int CELL_tabledef_ID, int CELL_colspan,
			String CELL_CONTENT, TableParams tableParams,
			double CELL_justify_spacing, double left, double top,
			String CELL_textbackground,	String CELL_external_path, double linespace,
			String LI_end_ind, String List_end_ind, String Para_end_ind,
			int CELL_header_level, String ACCESS_MD_TYPE, String TABLE_IND,
			String CELL_alt_text, String tagKey, int ordno, IndexParser.IndexData index) {
		
		 String _RESULT = "", _ERROR_CODE = "", _ERROR_MSG = "";
		 CellIndex cellindex = index.getIndex(tagKey, ordno);
		 String CELL_cell_link = null;
		 String CELL_tag_link_key = null;
		 String CELL_tag_target_link_key = null;
		 String CELL_tool_tip = null;
		 if (cellindex != null) {
			 CELL_cell_link = cellindex.cellLink();
			 CELL_tag_link_key = cellindex.tagLinkKey();
			 CELL_tag_target_link_key = cellindex.tagTargetLinkKey();
			 CELL_tool_tip = cellindex.toolTip();
		 }
		 TaggedPdf.TagTypes _tagType = TaggedPdf.TagTypes.NONE;
		
		 if ("H".equals(ACCESS_MD_TYPE)) {
			if (TABLE_IND.equals("Y")) {
				_tagType = TaggedPdf.TagTypes.CAPTION;
			} else if (CELL_header_level > 0) {
				_tagType = TaggedPdf.TagTypes.header(CELL_header_level);
			} else {
				_tagType = TaggedPdf.TagTypes.TOOLTIP;
			}
		} else if ("I".equals(ACCESS_MD_TYPE)) {
			 // this is handled in imageContent()
		} else if ("N".equals(ACCESS_MD_TYPE)) {
		
		}

		if (CONTENT_TYPE.equals("signpad")) {
			System.out.println("********* signpad ********");
		}

		if (cssStyle == null) {
			System.out.println("******* CSS not found for text " + CELL_CONTENT);
			_RESULT = "Failed";
			_ERROR_CODE = "200";
			_ERROR_MSG = "Style not found!";
			return new ItextResult(_ERROR_CODE, _ERROR_MSG, _RESULT, null,
					false);
		} else {
			if (cssStyle.align() == CssParser.Style.Alignment.JUSTIFY
					|| cssStyle.align() == CssParser.Style.Alignment.JUSTIFY_ALL) {
				new PdfCanvas(pdf.getPage(pageNum)).setCharacterSpacing((float) CELL_justify_spacing);
				// writer.setSpaceCharRatio((float) CELL_justify_spacing);
			}
			if (CELL_tabledef_ID > -1) {
				// PdfCell cell = new PdfCell();
				// cell.setColspan(CELL_colspan);
				// Chunk chunk = new Chunk(CELL_CONTENT, cssStyle.getFont());
				// cell.setPhrase(new Paragraph(chunk));
				// cell.setFixedHeight(cssStyle.height());
				//
				// Exception e = addCell(cb, cell,
				// Border.parseColor(cssStyle.backgroundColor()),
				// CELL_tabledef_ID, tableParams);
				// if (e != null) {
				// System.out.println(e);
				// }
			} else {
				if (inParagraph) {
					 if (listLevel > -1) {
						 list.addElementPart(new FakeList.ListText(CELL_CONTENT/*, cssStyle.getFont()*/));
					 } else {
						 para.addText(CELL_CONTENT.trim(), cssStyle.getFont(), cssStyle.backgroundColor());
					 }
				} else {
					try {
						float x_end = (float) left + cssStyle.width();
						float y_end = (float) top + cssStyle.height();
						// set the cell background color and/or text background
						// color
						com.itextpdf.kernel.colors.Color backgroundColor = Border.parseColor(cssStyle.backgroundColor());
						if (backgroundColor != null) {
							new PdfCanvas(pdf.getPage(pageNum))
									.saveState()
									.setFillColor(backgroundColor)
									.rectangle(
											new Rectangle((float) left,
													(float) top, cssStyle.width(), cssStyle.height())).fill().restoreState();
						}

						float marginTop = cssStyle.marginTop();
						if (cssStyle.wrap()) {
							//marginTop = -marginTop;
						}

						double corr = cssStyle.wrap()? 0.15: 0.09;
						corr = 0.15;
//						if (cssStyle.wrap()) {
//							marginTop = cssStyle.fontSize()	* new Float(linespace - 1);
//							if (marginTop < 0.75f) {
//								marginTop = sizeConvert(corr);/*0.75f; // one pixel @ 96 DPI*/
//							}
//						}
						
						if (marginTop == 0f) {
							marginTop = sizeConvert(corr); // correction factor for iText 7, to conform to previous appearance
						}

						Border.setBorders(pdf.getPage(pageNum), (float) left,
								(float) top, (float) x_end, (float) y_end,
								cssStyle.getBorder().borderLeft(), cssStyle.getBorder().borderTop(),
								cssStyle.getBorder().borderRight(), cssStyle.getBorder().borderBottom());

						Paragraph phrase = new Paragraph();
						ArrayList<Paragraph> phrases = new ArrayList<Paragraph>();
						ArrayList<String> chunks, broken = new ArrayList<String>();
						int start, end;
						int iter = this.readDir.equals("ltr")? 1: -1;
						if (CELL_CONTENT.contains(EMPHASIS_START)) {
							chunks = breakText(CELL_CONTENT, EMPHASIS_START, EMPHASIS_STOP);
							start = iter == 1? 0: chunks.size() - 1;
							end = iter == 1? chunks.size(): -1;
							for (int i = start; i != end; i+= iter) {
								String txt = chunks.get(i);
								if (CELL_cell_link != null) { // a link to a URL (webpage or PDF on the web)
									Link link = addUrlLink(CELL_cell_link, (reverseString(txt.startsWith(EMPHASIS_START) ? txt.substring(EMPHASIS_START.length()): txt)));
									if (txt.startsWith(EMPHASIS_START)) {
										link.setBold().setUnderline();
									}
									setStyles(link, cssStyle);
									phrase.add(link);	
								} else {
									Text ch = new Text(reverseString(txt.startsWith(EMPHASIS_START) ? txt.substring(EMPHASIS_START.length()): txt));
									if (txt.startsWith(EMPHASIS_START)) {
										ch.setBold().setUnderline();
									}
									setStyles(ch, cssStyle);
									phrase.add(ch);
								}
							}
							phrases.add(phrase);
						} else if (CELL_CONTENT.contains(UNDERLINE_START)) {
							chunks = breakText(CELL_CONTENT, UNDERLINE_START, UNDERLINE_STOP);
							start = iter == 1? 0: chunks.size() - 1;
							end = iter == 1? chunks.size(): -1;
							for (int i = start; i != end; i+= iter) {
								String txt = chunks.get(i);
								if (CELL_cell_link != null) { // a link to a URL (webpage or PDF on the web)
									Link link = addUrlLink(CELL_cell_link, (reverseString(txt.startsWith(UNDERLINE_START) ? txt.substring(UNDERLINE_START.length()): txt)));
									if (txt.startsWith(UNDERLINE_START) || cssStyle.isUnderline()) {
										link.setUnderline();
									}
									setStyles(link, cssStyle);
									phrase.add(link);	
								} else {
									Text ch = new Text(reverseString(txt.startsWith(UNDERLINE_START) ? txt.substring(UNDERLINE_START.length()): txt));
									if (txt.startsWith(UNDERLINE_START) || cssStyle.isUnderline()) {
										ch.setUnderline();
									}
									setStyles(ch, cssStyle);
									phrase.add(ch);
								}
							}
							phrases.add(phrase);
						} else if (CELL_CONTENT.contains(EMPHASIS_START_ONLY_BOLD)) {
							chunks = breakText(CELL_CONTENT, EMPHASIS_START_ONLY_BOLD, EMPHASIS_STOP_ONLY_BOLD);
							start = iter == 1? 0: chunks.size() - 1;
							end = iter == 1? chunks.size(): -1;
							for (int i = start; i != end; i+= iter) {
								//for (int i = 0; i < chunks.size(); i++) {
								String txt = chunks.get(i);
								if (CELL_cell_link != null) { // a link to a URL (webpage or PDF on the web)
									Link link = addUrlLink(CELL_cell_link, reverseString(txt.startsWith(EMPHASIS_START_ONLY_BOLD) ? txt.substring(EMPHASIS_START_ONLY_BOLD.length()): txt));
									if (txt.startsWith(EMPHASIS_START_ONLY_BOLD) || cssStyle.isBold()) {
										link.setBold();
									}
									setStyles(link, cssStyle);
									phrase.add(link);	
								} else {
									Text ch = new Text(reverseString(txt.startsWith(EMPHASIS_START_ONLY_BOLD) ? txt.substring(EMPHASIS_START_ONLY_BOLD.length()): txt));
									if (txt.startsWith(EMPHASIS_START_ONLY_BOLD) || cssStyle.isBold()) {
										ch.setBold();
									}
									setStyles(ch, cssStyle);
									phrase.add(ch);
								}
							}
							phrases.add(phrase);
						} else {
							//String txt = CELL_CONTENT;
							ArrayList<String> texts = new ArrayList<String>();
							if (cssStyle.wrap()) {
								broken = multiline(cssStyle.fontFamily(), cssStyle.isBold(), (int)cssStyle.fontSize(), 
										sizeReConvert(cssStyle.width() - cssStyle.marginLeft() - cssStyle.marginRight()), CELL_CONTENT);
								for (int i = 0; i < broken.size(); i++) {
									String[] prevs = new String[i];
									if (i > 0) {
										for (int j = 0; j < i; j++) {
											prevs[j] = broken.get(j);
										}
									}
									texts.add(reverseString(broken.get(i), prevs, cssStyle.direction().equals("rtl")));
								}
							} else {
								texts.add(reverseString(CELL_CONTENT, null, cssStyle.direction().equals("rtl")));
								//txt = reverseString(txt);
							}
							if (CELL_cell_link != null) { // a link to a URL (webpage or PDF on the web)
								for (String text: texts) {
									Link link = addUrlLink(CELL_cell_link, text);
									setStyles(link, cssStyle);
									phrases.add(new Paragraph().add(link));								
								}
							} else {
								for (String text: texts) {
									Text ch = new Text(text);
									setStyles(ch, cssStyle);
									phrases.add(new Paragraph().add(ch));									
								}
							}
						}

						// Links

						if (!cssStyle.wrap()) {
							for (Paragraph p: phrases) {
								p.setSplitCharacters(new SplitOnAll());
							}
						}
						if (CELL_textbackground != null) {
							for (Paragraph p: phrases) {
								p.setBackgroundColor(Border.parseColor(CELL_textbackground));
							}
						}

						if (tagKey.length() > 0 && cellindex != null) {
							if (cellindex.bookmarkLevel() > 0 && this._bookmarks) {
								if (index.bookmarks().hasBookmark(tagKey)) {
									System.out.println("adding bookmark: " + index.bookmarks().getBookmark(tagKey).title());
									PdfOutline bkmrk = index.bookmarks().getBookmark(tagKey).self();
									bkmrk.addAction(PdfAction.createGoTo(PdfExplicitDestination.createFitH(pdf.getLastPage(), (float) y_end)));
									//pdf.getLastPage().getPageSize().getTop())));
								}
							}
						}
						if (this._links && CELL_tag_target_link_key != null) {
							System.out.println("Adding link target: " + CELL_CONTENT);
							for (Paragraph p: phrases) {
								p.setAction(PdfAction.createGoTo(CELL_tag_target_link_key));
							}
						}
						if (this._links && CELL_tag_link_key != null && CELL_tag_target_link_key == null) {
							System.out.println("Adding link destination: " + CELL_CONTENT);
							for (Paragraph p: phrases) {
								p.setDestination(CELL_tag_link_key.replace("#", ""));
							}
						}
						if (CELL_tool_tip != null) {
							System.out.println("Adding tooltip: " + CELL_CONTENT);
							PdfAnnotation ann = PdfTextMarkupAnnotation.createHighLight(
									new Rectangle((float)left, (float)top, (float)(x_end - left), (float)(y_end - top)),
									new float[]{(float)left, (float)top, (float)(x_end - left), (float)(y_end - top)})
									.setColor(com.itextpdf.kernel.colors.ColorConstants.YELLOW).setTitle(new PdfString("Hello!"))
									.setContents(new PdfString(CELL_tool_tip + "****")).setTitle(new PdfString("iText"))/*.setOpen(true) - 7.1.1*/
									.setRectangle(new PdfArray(new float[]{(float)left, (float)top, (float)(x_end - left), (float)(y_end - top)}));
							pdf.getLastPage().addAnnotation(ann);
						}

//						float marginTop = cssStyle.marginTop();
//						if (cssStyle.wrap()) {
//							marginTop = -marginTop;
//						}
//
//						double corr = cssStyle.wrap()? 0.15: 0.09;
//						corr = 0.15;
////						if (cssStyle.wrap()) {
////							marginTop = cssStyle.fontSize()	* new Float(linespace - 1);
////							if (marginTop < 0.75f) {
////								marginTop = sizeConvert(corr);/*0.75f; // one pixel @ 96 DPI*/
////							}
////						}
//						
//						if (marginTop == 0f) {
//							marginTop = sizeConvert(corr); // correction factor for iText 7, to conform to previous appearance
//						}
						
						if (_tagType != TaggedPdf.TagTypes.NONE && _tagType != TaggedPdf.TagTypes.TOOLTIP && pdf.isTagged()/*&& eTop != null*/) {
							for (Paragraph p: phrases) {
								//p.setRole(_tagType.getType()); - 7.1.1
							}
					    }
						for (Paragraph p: phrases) {
							p.setTextAlignment(cssStyle.textalign());						
						}
						float topLoc = y_end - cssStyle.fontSize() - marginTop;
						if (cssStyle.wrap()) {
							topLoc = (float)top;
						}
						//phrase.setFixedPosition((float) left + cssStyle.marginLeft() - cssStyle.marginRight(), (float) y_end - cssStyle.marginTop(), (float) (x_end - left));
						//phrase.setFixedPosition((float) left + cssStyle.marginLeft() - cssStyle.marginRight(), y_end - (float) verticalLeading(marginTop, cssStyle.height(), cssStyle.fontSize())/* - cssStyle.marginTop()*/, (float) (x_end - left));
						/*final Rectangle rect = new Rectangle((float)left, (float)top, (float)(x_end - left), (float)(y_end - top));
					    phrase.setNextRenderer(new ParagraphRenderer(phrase) {
					        @Override
					        public List<Rectangle> initElementAreas(LayoutArea area) {
					            List<Rectangle> list = new ArrayList<Rectangle>();
					            list.add(rect);
					            return list;
					        }
					    });
					    doc.add(phrase);*/
						if (cssStyle.wrap()) {
							for (int ix = 0; ix < phrases.size(); ix++) {
	 							int j = 0;
							    ////PdfCanvas canvas = new PdfCanvas(pdf.getLastPage());
							    j = 1;
							    float vert = cssStyle.fontSize() * 1f * new Float(linespace);
//							    Rectangle rect = new Rectangle((float)left + cssStyle.marginLeft() - cssStyle.marginRight(), 
//							    		(float)top + marginTop - vert * ix, 
//							    		(float)(x_end - left), (float)(y_end - top));
							    j = 2;
							    ////new Canvas(canvas, pdf, rect).add(phrases.get(ix));
							    Paragraph p = phrases.get(ix);
								//p.setVerticalAlignment(VerticalAlignment.MIDDLE);
							    float widthCorr = 0f;
							    if (cssStyle.textalign() == TextAlignment.JUSTIFIED_ALL) {
							    	widthCorr = cssStyle.marginLeft() + cssStyle.marginRight();
							    }
								p.setFixedPosition((float) left + cssStyle.marginLeft() /*- cssStyle.marginRight()*/, (float)y_end - cssStyle.fontSize() - marginTop - vert * ix, 
										(float) (x_end - left - widthCorr));
								p.setPageNumber(this.pageNum);
								doc.add(p);						
							    j = 3;
							    //canvas.rectangle(rect);
							    //canvas.stroke();			
							}
						} else {
							for (Paragraph p: phrases) { // there really should be only one.
								p.setVerticalAlignment(VerticalAlignment.MIDDLE);
								p.setFixedPosition((float) left + cssStyle.marginLeft() - cssStyle.marginRight(), topLoc, (float) (x_end - left));
								p.setPageNumber(this.pageNum);
								doc.add(p);								
							}
						}
						_RESULT = "Text added.";
					} catch (Exception e) {
						_ERROR_CODE = "2";
						_ERROR_MSG = Misc.message(e);
						System.out.println(e.toString() + " in textContent():");
						System.out.println(CELL_CONTENT);
						System.out.println(cssStyle.fontFamily());
						System.out.println(CELL_tag_target_link_key);
						System.out.println(CELL_tag_link_key);
					}
				}

			}
		}
		return new ItextResult(_ERROR_CODE, _ERROR_MSG, _RESULT, null, false);
	}
	
	private static Link addUrlLink(String url, String txt) {
		PdfLinkAnnotation annotation = new PdfLinkAnnotation(new Rectangle(0, 0));
		annotation.setAction(PdfAction.createURI(url));
		annotation.setBorder(new PdfArray(new float[] {0, 0, 0})); // the last zero makes that there is no border (width = 0f)
		Link link = new Link(txt, annotation);
		return link;
	}
	
	@SuppressWarnings("rawtypes")
	private void setStyles(AbstractElement el, CssParser.Style cssStyle) {
		if (cssStyle.isBold()) {
			el.setBold();
		}
		if (cssStyle.isItalic()) {
			el.setItalic();
		}
		if (cssStyle.isUnderline()) {
			el.setUnderline();
		}
		if (cssStyle.isLineThrough()) {
			el.setLineThrough();
		}
		el.setFontColor(Border.parseColor(cssStyle.color()));
		el.setFont(this.fonts.get(cssStyle.fontFamily()));
		el.setFontSize(cssStyle.fontSize());
	}

	public static ItextResult textContent(String CONTENT_TYPE,
			int CELL_tabledef_ID, String CELL_FONT, double CELL_FONTSIZE,
			String CELL_FONTstyle, String CELL_text_decoration,
			int CELL_colspan, String CELL_CONTENT, String CELL_color, /* double CELL_height, */
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

		// String _RESULT = "", _ERROR_CODE = "", _ERROR_MSG = "";
		// //TaggedPdf.TagTypes _tagType = TaggedPdf.TagTypes.NONE;
		//
		// // if ("H".equals(ACCESS_MD_TYPE)) {
		// // if (TABLE_IND.equals("Y")) {
		// // _tagType = TaggedPdf.TagTypes.CAPTION;
		// // } else if (CELL_header_level > 0) {
		// // _tagType =
		// /*TaggedPdf.TagTypes.header(CELL_header_level)*/TaggedPdf.TagTypes.TOOLTIP;
		// // } else {
		// // _tagType = TaggedPdf.TagTypes.TOOLTIP;
		// // }
		// // } else if ("I".equals(ACCESS_MD_TYPE)) {
		// // // this is handled in imageContent()
		// // } else if ("N".equals(ACCESS_MD_TYPE)) {
		// //
		// // }
		//
		// if (CONTENT_TYPE.equals("signpad")) {
		// System.out.println("********* signpad ********");
		// }
		// /*if (CELL_tabledef_ID > -1) {
		// PdfCell cell = new PdfCell();
		// cell.setColspan(CELL_colspan);
		// Chunk chunk = new Chunk(CELL_CONTENT, new Font(
		// fontBank.getFont(CELL_FONT),
		// (float) CELL_FONTSIZE * 1f, parseStyle(CELL_FONTstyle + " "
		// + CELL_text_decoration, CELL_FONT),
		// Border.parseColor(CELL_color)));
		// cell.setPhrase(new Paragraph(chunk));
		// cell.setFixedHeight((float) cellHeight);
		//
		// Exception e = addCell(cb, cell,
		// Border.parseColor(CELL_backgroundColor), CELL_tabledef_ID,
		// tableParams);
		// if (e != null) {
		// System.out.println(e);
		// }
		// } else {
		// if (inParagraph) {
		// int style = parseStyle(CELL_FONTstyle + " "
		// + CELL_text_decoration, CELL_FONT);
		// boolean bold = (style & Font.BOLD) == Font.BOLD;
		// boolean italic = (style & Font.ITALIC) == Font.ITALIC;
		// boolean lineThrough = (style & Font.STRIKETHRU) == Font.STRIKETHRU;
		// boolean underline = (style & Font.UNDERLINE) == Font.UNDERLINE;
		// if (listLevel > -1) {
		// list.addElementPart(new FakeList.ListText(CELL_CONTENT,
		// Xml2Phrases.getFont(CELL_FONT,
		// (float) CELL_FONTSIZE, CELL_color, bold,
		// italic, lineThrough, underline), Border
		// .parseColor(CELL_backgroundColor)));
		// } else {
		// para.addText(CELL_CONTENT, Xml2Phrases.getFont(CELL_FONT,
		// (float) CELL_FONTSIZE, CELL_color, bold, italic,
		// lineThrough, underline), CELL_backgroundColor);
		// }
		// } else*/ {
		// try {
		// TextAlignment textAlign;
		// BaseDirection runDir;
		// Rectangle[] area = new Rectangle[] {new Rectangle((int)left,
		// (int)top, (int)cellWidth, (int)cellHeight)};
		// doc.setRenderer(new ColumnDocumentRenderer(doc, area));
		// doc.add(new AreaBreak(AreaBreakType.LAST_PAGE));
		// if (CELL_HAlign.equals("Right")) {
		// if (CELL_dir.equals("ltr")) {
		// textAlign = TextAlignment.RIGHT;
		// } else {
		// textAlign = TextAlignment.LEFT; // right-align for
		// // right-to-left is
		// // actually
		// // left-align
		// }
		// } else if (CELL_HAlign.equals("Center")) {
		// textAlign = TextAlignment.CENTER;
		// } else if (CELL_HAlign.equals("Justify")) {
		// //writer.setSpaceCharRatio((float) CELL_justify_spacing);
		// new
		// PdfCanvas(pdf.getPage(pageNum)).setCharacterSpacing((float)CELL_justify_spacing);
		// textAlign = TextAlignment.JUSTIFIED;
		// } else if (CELL_HAlign.equals("JustifyAll")) {
		// //writer.setSpaceCharRatio((float) CELL_justify_spacing);
		// new
		// PdfCanvas(pdf.getPage(pageNum)).setCharacterSpacing((float)CELL_justify_spacing);
		// textAlign = TextAlignment.JUSTIFIED_ALL;
		// } else { // "Left"
		// if (CELL_dir.equals("ltr")) {
		// textAlign = TextAlignment.LEFT;
		// } else {
		// textAlign = TextAlignment.RIGHT;
		// }
		// }
		//
		// if (CELL_dir.equals("ltr")) {
		// runDir = BaseDirection.LEFT_TO_RIGHT;
		// } else {
		// runDir = BaseDirection.RIGHT_TO_LEFT;
		// }
		//
		// float x_end = (float) left + (float) cellWidth;
		// float y_end = (float) top + (float) cellHeight;
		// Rectangle frame = new Rectangle((float) left, (float) top,
		// (float) x_end, (float) y_end);
		//
		// // set the cell background color and/or text background
		// // color
		// if (CELL_backgroundColor != null) {
		// new
		// PdfCanvas(pdf.getPage(pageNum)).saveState().setFillColor(Border.parseColor(CELL_backgroundColor)).rectangle(new
		// Rectangle((float)left, (float)top, (float)cellWidth,
		// (float)cellHeight)).fill().restoreState();
		// // frame.setBackgroundColor(Border
		// // .parseColor(CELL_backgroundColor));
		// }
		//
		// //cb.rectangle(frame);
		//
		// Border.setBorders(pdf.getPage(pageNum), (float) left, (float) top,
		// (float) x_end, (float) y_end,
		// borderParams.borderLeft(),
		// borderParams.borderTop(),
		// borderParams.borderRight(),
		// borderParams.borderBottom());
		//
		// Paragraph phrase = new Paragraph();
		// ArrayList<String> chunks;
		// if (CELL_CONTENT.contains(EMPHASIS_START)) {
		// chunks = breakText(CELL_CONTENT, EMPHASIS_START, EMPHASIS_STOP);
		// for (int i = 0; i < chunks.size(); i++) {
		// String txt = chunks.get(i);
		// Style newstyle = parseStyle(CELL_FONTstyle + " " +
		// CELL_text_decoration, CELL_FONT);
		// if (txt.startsWith(EMPHASIS_START)) {
		// newstyle.setBold().setUnderline();
		// txt = txt.substring(EMPHASIS_START.length());
		// }
		// Text ch = new Text(txt);
		// newstyle.setFontColor(Border.parseColor(CELL_color));
		// newstyle.setFont(fontBank.getFont(CELL_FONT));
		// ch.setFontSize((float) CELL_FONTSIZE * 1f);
		// phrase.add(ch);
		// }
		// } else {
		// chunks = breakText(CELL_CONTENT,
		// EMPHASIS_START_ONLY_BOLD,
		// EMPHASIS_STOP_ONLY_BOLD);
		// for (int i = 0; i < chunks.size(); i++) {
		// String txt = chunks.get(i);
		// Style newstyle = parseStyle(CELL_FONTstyle + " " +
		// CELL_text_decoration, CELL_FONT);
		// if (txt.startsWith(EMPHASIS_START_ONLY_BOLD)) {
		// newstyle.setBold();
		// txt = txt.substring(EMPHASIS_START_ONLY_BOLD
		// .length());
		// }
		// Text ch = new Text(txt);
		// newstyle.setFontColor(Border.parseColor(CELL_color));
		// newstyle.setFont(fontBank.getFont(CELL_FONT));
		// ch.setFontSize((float) CELL_FONTSIZE * 1f);
		// phrase.add(ch);
		// }
		// }
		//
		// // Links
		// //Text c = (Text) phrase.getChunks().get(0);
		// if (!CELL_wrap.equals("true")) {
		// phrase.setSplitCharacters(new SplitOnAll());
		// }
		// if (CELL_textbackground != null) {
		// phrase.setBackgroundColor(Border.parseColor(CELL_textbackground));
		// }
		//
		// if (CELL_bookmark_level > 0) {
		// String ID = CELL_tag_target_link_key == null ? bookmark_ID()
		// : CELL_tag_target_link_key;
		// phrase.setDestination(ID);
		// addOutlineElement(ID, CELL_bookmark_text,
		// CELL_bookmark_level);
		// }
		//
		// if (CELL_tag_target_link_key != null) { // the target of a
		// // link
		// phrase.setDestination(CELL_tag_target_link_key);
		// } else if (CELL_external_path != null
		// && CELL_tag_link_key != null) { // link to an
		// // external PDF
		// // document
		// System.out.println("********************* |"
		// + CELL_tag_link_key + "|");
		// ////phrase.setRemoteGoto(CELL_external_path, CELL_tag_link_key);
		// } else if (CELL_tag_link_key != null) { // an internal link
		// //// phrase.setAction(PdfAction.gotoLocalPage(CELL_tag_link_key,
		// //// false));
		// } else if (CELL_cell_link != null) { // a link to a URL
		// // (webpage or PDF
		// // on the web)
		// ////phrase.setAnchor(CELL_cell_link);
		// //c.setAccessibleAttribute(PdfName.ALT, new PdfString("google"));
		// }
		//
		// if (CELL_tool_tip != null) {
		//
		// }
		//
		// float marginTop = sizeConvert_negative(CELL_marginTop);
		//
		// if (CELL_wrap.equals("true")) {
		// // marginTop = marginTop + 0.75f; // one pixel @ 96 DPI
		// marginTop = new Float(CELL_FONTSIZE)
		// * new Float(linespace);
		// if (marginTop < 0.75f) {
		// marginTop = 0.75f;
		// }
		//
		// }
		//
		// // if (_tagType != TaggedPdf.TagTypes.NONE
		// // && _tagType != TaggedPdf.TagTypes.TOOLTIP
		// // && eTop != null) {
		// // PdfStructureElement e1 = new PdfStructureElement(eTop,
		// // _tagType.getType());
		// // cb.beginMarkedContentSequence(e1);
		// // }
		// /*area = new Rectangle[] {new Rectangle((float)left, (float)top,
		// x_end, y_end)};
		// doc.setRenderer(new ColumnDocumentRenderer(doc, area));
		// doc.add(new AreaBreak(AreaBreakType.LAST_PAGE));
		// phrase.setMargin(0);
		// phrase.setMultipliedLeading(1);*/
		// phrase.setFixedPosition((float)left, (float)top, (float)(x_end -
		// left));
		// doc.add(phrase);
		//
		// // ct.setSimpleColumn(
		// // new com.itextpdf.text.Paragraph(phrase),
		// // //phrase,
		// // (float) left
		// // + sizeConvert_negative(CELL_marginLeft),
		// // (float) top,
		// // x_end - sizeConvert_negative(CELL_marginRight),
		// // y_end,
		// // (float) verticalLeading(marginTop,
		// // sizeConvert_negative(CELL_marginBottom),
		// // cellHeight, CELL_FONTSIZE * 1), textAlign);
		// // ct.setRunDirection(runDir);
		// // ct.go();
		// // if (_tagType != TaggedPdf.TagTypes.NONE
		// // && _tagType != TaggedPdf.TagTypes.TOOLTIP
		// // && eTop != null) {
		// // cb.endMarkedContentSequence();
		// // }
		// // if (_tagType == TaggedPdf.TagTypes.TOOLTIP) {
		// // PdfAnnotation annotation = PdfAnnotation
		// // .createSquareCircle(
		// // writer,
		// // new Rectangle(
		// // (float) left
		// // + sizeConvert_negative(CELL_marginLeft),
		// // (float) top + 20,
		// // x_end
		// // - sizeConvert_negative(CELL_marginRight),
		// // y_end), "", true);
		// // annotation.put(PdfName.T, new PdfString(CELL_alt_text));
		// // annotation.put(PdfName.CONTENTS, new PdfString(" "));
		// // /*
		// // * PdfAppearance ap =
		// // * cb.createAppearance((float)cellWidth,
		// // * (float)cellHeight);
		// // * annotation.setAppearance(PdfAnnotation
		// // * .APPEARANCE_NORMAL, ap);
		// // */
		// // writer.addAnnotation(annotation);
		// // /*
		// // * Annotation desc = new Annotation("", CELL_alt_text);
		// // * //desc.setDimensions((float)x, (float)y, (float) x +
		// // * (float) width, (float) y + (float) height);
		// // * desc.setDimensions((float) left +
		// // * sizeConvert_negative(CELL_marginLeft), (float) top,
		// // * x_end - sizeConvert_negative(CELL_marginRight),
		// // * y_end); doc.add(desc);
		// // */
		// // }
		// //
		// _RESULT = "Text added.";
		// } catch (Exception e) {
		// _ERROR_CODE = "2";
		// _ERROR_MSG = e.getMessage();
		// }
		//
		// }
		//
		// if (inParagraph && LI_end_ind.equals("yes")) {
		// list.closeLastElement();
		// }
		//
		// if (inParagraph && List_end_ind.equals("yes")) {
		// listLevel--;
		// list.closeLastList();
		// para.addList(list, listFont, null);
		// }
		//
		// if (Para_end_ind.equals("yes")) {
		// try {
		// Float spacing = 1.2f;
		// if (linespace > 0) {
		// spacing = new Float(linespace);
		// }
		// TextAlignment align;
		// if ("right".equals(CELL_HAlign)) {
		// align = "rtl".equals(CELL_dir) ? TextAlignment.LEFT
		// : TextAlignment.RIGHT;
		// } else if ("center".equals(CELL_HAlign)) {
		// align = TextAlignment.CENTER;
		// } else {
		// align = "rtl".equals(CELL_dir) ? TextAlignment.RIGHT
		// : TextAlignment.LEFT;
		// }
		// System.out.println("*** adding paragraph: align = "
		// + align.toString());
		// System.out.println("*** adding paragraph, lineheight = "
		// + new Float(para.getLineHeight()).toString());
		// //// para.addToDocument(cb, (int) Math.round(cellHeight), spacing,
		// //// 0f, 0f, align, (float) left
		// //// + sizeConvert_negative(CELL_marginLeft),
		// //// (float) top + (float) cellHeight, (float) left
		// //// + (float) cellWidth
		// // - sizeConvert_negative(CELL_marginRight),
		// // (float) top, borderParams.borderLeft(),
		// // borderParams.borderTop(), borderParams.borderRight(),
		// // borderParams.borderBottom());
		// } catch (Exception e) {
		// System.out.println("Exception closing paragraph: "
		// + il.co.aman.apps.Misc.message(e));
		// }
		// inParagraph = false;
		// }
		//
		// return new ItextResult(_ERROR_CODE, _ERROR_MSG, _RESULT, null,
		// false);
		return null;
	}
	
	public ItextResult imageContent(String WORD_INSERT_TYPE, String IMG_PATH,
			String CELL_CONTENT, double CELL_LEFT, double CELL_TOP,
			double CELL_height, double CELL_width,
			byte[] WORD_INSERT_PDF, BorderParams borderParams,
			String CELL_alt_text, String tagKey, int ordno, IndexParser.IndexData index) {
		double top = (double) pageHeight - (double) sizeConvert(CELL_TOP) - CELL_height;
		double left = (double) sizeConvert(CELL_LEFT);
		String _ERROR_CODE = "", _ERROR_MSG = "";
		CellIndex cellindex = index.getIndex(tagKey, ordno);
		String CELL_tag_link_key = null;
		String CELL_tag_target_link_key = null;
		String CELL_tool_tip = null;
		String CELL_URL = null;
		if (cellindex != null) {
			CELL_URL = cellindex.cellLink();
			CELL_tag_link_key = cellindex.tagLinkKey();
			CELL_tag_target_link_key = cellindex.tagTargetLinkKey();
			CELL_tool_tip = cellindex.toolTip();
		}
		if (!"PDF".equals(WORD_INSERT_TYPE)) {
			try {
				Image img = getImage(fixPath(IMG_PATH, CELL_CONTENT));
				if (img != null) {
					if (!pdf.isTagged()) {
						CELL_alt_text = null;
					}
					addImage((int) left, (int) top, (int) CELL_width,
							(int) CELL_height, img, CELL_URL, CELL_alt_text);
				}
			} catch (Exception e) {
				_ERROR_CODE = "2";
				_ERROR_MSG = e.getMessage() + " - "	+ fixPath(IMG_PATH, CELL_CONTENT);
				System.out.println("Exception " + e.toString() + "in method imageContent(), regular image section [" + fixPath(IMG_PATH, CELL_CONTENT) + "].");
			}
		} else {
			// A Word Insert object which is to be embedded as a PDF page, not an image (to save space and improve the quality of the insert) 
			try { 
				float x = sizeConvert(CELL_LEFT); float y = (float) (pageHeight - sizeConvert(CELL_TOP) - /*sizeConvert(*/CELL_height/*)*/);		  
				PdfLayers.addLayer(pdf, new PdfCanvas(pdf.getPage(pageNum)), new PdfDocument(new PdfReader(new ByteArrayInputStream(WORD_INSERT_PDF))), 1, x, y); 
			} catch (Exception e) { 
				_ERROR_CODE = "2"; 
				_ERROR_MSG = e.getMessage();
				System.out.println("Exception " + e.toString() + "in method imageContent(), Word Insert section.");
			} 
		}
		Border.setBorders(pdf.getPage(pageNum), (float) left, (float) top,
				(float) left + (float) CELL_width, (float) top
						+ (float) CELL_height, borderParams.borderLeft(),
				borderParams.borderTop(), borderParams.borderRight(),
				borderParams.borderBottom());
		return new ItextResult(_ERROR_CODE, _ERROR_MSG, "", null, false);
	}

	public ItextResult SVGcontent(String chartXml, String sXslt, double CELL_LEFT, double CELL_TOP, String altText, float fontsize) {
		String _ERROR_CODE = "", _ERROR_MSG = "";
		try {
			String svgdims = Misc.getResource(this.getClass(), "SVGdims.xsl");
			XsltTransform xslt = new XsltTransform(new StreamSource(new StringReader(svgdims)));
			sXslt = sXslt.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
			String[] dims = xslt.transform(new StreamSource(new StringReader(sXslt))).split(","); // temporary fix for iText7 image quality problems
			xslt = new XsltTransform(new StreamSource(new StringReader(sXslt)));
			if (dims.length == 7) { // new barchart XSLT
				xslt.addParam("full_width", new Integer(Integer.parseInt(dims[0]) * this.svgFactor).toString());
				xslt.addParam("full_height", new Integer(Integer.parseInt(dims[1]) * this.svgFactor + 5).toString()); // *************************************** + 5
				xslt.addParam("fontsize", new Integer(Integer.parseInt(dims[2]) * this.svgFactor).toString());
				xslt.addParam("grid_depth", new Integer(Integer.parseInt(dims[3]) * this.svgFactor).toString());
				xslt.addParam("rule_width", new Integer(Integer.parseInt(dims[4]) * this.svgFactor).toString());
				xslt.addParam("padding", new Integer(Integer.parseInt(dims[5]) * this.svgFactor).toString());
				xslt.addParam("y_labels_width", new Integer(Integer.parseInt(dims[6]) * this.svgFactor).toString());
			} else if (dims.length == 11) { // barchart
				xslt.addParam("size", new Integer(Integer.parseInt(dims[0]) * this.svgFactor).toString());
				xslt.addParam("full_height", new Float(Float.parseFloat(dims[1]) * this.svgFactor + 5).toString()); // *************************************** + 5
				xslt.addParam("fontsize", new Integer(Integer.parseInt(dims[2]) * this.svgFactor).toString());
				xslt.addParam("rule_width", new Integer(Integer.parseInt(dims[3]) * this.svgFactor).toString());
				xslt.addParam("border", new Float(Float.parseFloat(dims[4]) * this.svgFactor).toString());
				xslt.addParam("width_corr", new Integer(Integer.parseInt(dims[5]) * this.svgFactor).toString());
				xslt.addParam("sample_corr", new Integer(Integer.parseInt(dims[6]) * this.svgFactor).toString());
				xslt.addParam("keystart_corr", new Integer(Integer.parseInt(dims[7]) * this.svgFactor).toString());
				xslt.addParam("rule_corr", new Integer(Integer.parseInt(dims[8]) * this.svgFactor).toString());
				xslt.addParam("minus_corr", new Float(Float.parseFloat(dims[9]) * this.svgFactor * 0.6).toString());
				xslt.addParam("plus_corr", new Float(Float.parseFloat(dims[10]) * this.svgFactor * 0.6).toString());
				xslt.addParam("size_corr", this.svgFactor <= 1? "0": new Integer(this.svgFactor * 10).toString());
				xslt.addParam("vert_corr", this.svgFactor <= 1? "5": new Float(this.svgFactor * -5/*fontsize / 2*/).toString());
				//xslt.addParam("height_corr", new Integer(Integer.parseInt(dims[9]) * this.svgFactor).toString());
				//xslt.addParam("height_corr_2", new Integer(Integer.parseInt(dims[10]) * this.svgFactor).toString());
			} else if (dims.length == 2) { // piechart
				xslt.addParam("whole_size", new Integer(Integer.parseInt(dims[0]) * this.svgFactor + 10).toString()); // *************************************** + 10
				xslt.addParam("fontsize", new Integer(Integer.parseInt(dims[1]) * this.svgFactor).toString());			
			}
			String SVG = xslt.transform(new StreamSource(new StringReader(chartXml)));
			SVGrep SVGdoc = new SVGrep(SVG, false);
			if (SVGdoc.error != null) {
				System.out.println("Error creating SVGdoc object: " + SVGdoc.error);
			}
			if (SVGdoc.success) {
				// 0.75f added by David Zalkin, February 2013, to fix problem of
				// inconsistency of size of charts between PDF and HTML. Not yet
				// tested.
				float x = sizeConvert(CELL_LEFT);// / 0.75f;
				// float diff = sizeConvert(CELL_height) - SVGdoc.dims[1];
				// float y = pageHeight - sizeConvert(CELL_TOP) -
				// sizeConvert(CELL_height) + diff;
				// float y = pageHeight - sizeConvert(CELL_TOP) -
				// sizeConvert(CELL_height);
				float y = pageHeight - sizeConvert(CELL_TOP) - SVGdoc.dims[1] * (0.75f / this.svgFactor); // why is this * 0.75f still here?????
				SVGrep.addSVG(SVGdoc, (int)x, (int)y, altText, this);
			}
		} catch (Exception e) {
			_ERROR_CODE = "2";
			_ERROR_MSG = e.getMessage();
			System.out.println("**** SVG error: " + e.getMessage());
		}
		return new ItextResult(_ERROR_CODE, _ERROR_MSG, "", null, false);
	}

	public ItextResult barcodeContent(double left, double top,
			CssParser.Style barcodeStyle, AdditionalMetadata.Metadata md,
			String CELL_CONTENT, String CELL_URL) {
		return barcodeContent(left, top, (double) barcodeStyle.width(),
				(double) barcodeStyle.height(), barcodeStyle.getBorder(),
				(double) sizeReConvert(barcodeStyle.height()),
				barcodeStyle.fontSize()/* , CELL_LEFT, CELL_TOP */,
				md.cellBarcodeType(), CELL_CONTENT, md.barcodeCharset(),
				(double) barcodeStyle.width(), CELL_URL, md.qrCharset(),
				Double.parseDouble(md.cellBarWidth()),
				barcodeStyle.fontFamily(), md.cellTextVisible());
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

	public ItextResult barcodeContent(double left, double top,
			double cellWidth, double cellHeight, BorderParams borderParams,
			double CELL_height, double CELL_FONTSIZE, String CELL_BARCODE_TYPE,
			String CELL_CONTENT, String BARCODE_CHARSET, double CELL_width,
			String CELL_URL, String QR_CHARSET, double CELL_BAR_WIDTH,
			String CELL_FONT, String CELL_text_visible) {
		String _ERROR_CODE = "", _ERROR_MSG = "";
		try {
			Border.setBorders(pdf.getPage(pageNum), (float) left, (float) top,
					(float) left + (float) cellWidth, (float) top
							+ (float) cellHeight, borderParams.borderLeft(),
					borderParams.borderTop(), borderParams.borderRight(),
					borderParams.borderBottom());
			float size = (float) (sizeConvert(CELL_height) - (float) CELL_FONTSIZE * 1.2f);
			float x = sizeConvert(left);
			float y = pageHeight - sizeConvert(top) - sizeConvert(CELL_height);
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
				int square = (int) Math.min(sizeConvert(CELL_height), sizeConvert(CELL_width));
				Image img = createDataMatrix(CELL_CONTENT, square, square,
						BARCODE_CHARSET);				
				addImage((int) x, (int) y, square, square, img, CELL_URL, "Datamatrix barcode");
				break;
			case 6:
				barcode1d = new BarcodeInter25(pdf);
				break;
			case 7:
				square = (int)Math.min(sizeConvert(CELL_height), sizeConvert(CELL_width));
				img = createQR(CELL_CONTENT, QR_CHARSET);
				addImage((int)x, (int)y, square, square, img, CELL_URL, "QR barcode");
				break;
			default:
				System.out.println("Invalid barcode type: " + CELL_BARCODE_TYPE);
				break;
			}
			if (barcode1d != null) {
				barcode1d.setX(new Float(CELL_BAR_WIDTH));
				barcode1d.setFont(fontBank.getFont("Arial"));
				doBarCode(barcode1d, CELL_CONTENT, size, (float) CELL_width,
						(float) sizeConvert(CELL_height), x, y,
						CELL_text_visible.equals("1"), barcodetype == 1);
			}
		} catch (Exception e) {
			_ERROR_CODE = "2";
			_ERROR_MSG = e.getMessage();
			System.out.println(e.toString() + " in barcodeContent()");
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
			System.out.println(_ERROR_MSG + " in popupContent()");
		}
		return new ItextResult(_ERROR_CODE, _ERROR_MSG, "", null, false);
	}

	public ItextResult formContent(FormContentParams params) {
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

	public ItextResult formContent(String CONTENT_TYPE, String visible,
			String CELL_form_name, String CELL_CONTENT, double CELL_FONTSIZE,
			double CELL_form_border_width, String CELL_form_border_style,
			String CELL_FONT, String CELL_color, String CELL_form_border_color,
			String CELL_backgroundColor, double left, double top,
			double cellWidth, double cellHeight, String CELL_HAlign,
			String CELL_button_action, String IMG_PATH, String JAVA_SCRIPT,
			String CELL_list_value, String CELL_form_default_selected,
			String CELL_form_date_format, String CELL_form_radiobutton_name) {
		if ((CONTENT_TYPE.equals("textbox") || CONTENT_TYPE.equals("textarea")) && "true".equals(visible)) {
			int numlines = CONTENT_TYPE.equals("textbox") ? 1 : 2; // any value more than 1 means multiline
			/*
			 * if (!frmwriter.addField(FormParams.getTextParams(CELL_form_name,
			 * CELL_CONTENT, numlines, (int) CELL_FONTSIZE,
			 * sizeConvert(CELL_form_border_width),
			 * SAXimpl.borderstyleNames.get(CELL_form_border_style), CELL_FONT,
			 * CELL_FONT, CELL_color, CELL_form_border_color,
			 * CELL_backgroundColor, (float) left, (float) top, (float) left +
			 * (float) cellWidth, (float) top + (float) cellHeight,
			 * SAXimpl.alignmentTypes.get(CELL_HAlign)))) { }
			 */
		} else if (CONTENT_TYPE.equals("textbox") || CONTENT_TYPE.equals("textarea")) {
			System.out.println("Adding hidden field. Result:");
			System.out.println(CELL_form_name);
			System.out.println(CELL_CONTENT);
			// frmwriter.addField(FormParams.getHiddenParams(CELL_form_name,
			// CELL_CONTENT, (float) left, (float) top, (float) left
			// + (float) cellWidth, (float) top
			// + (float) cellHeight));
		} else if (CONTENT_TYPE.equals("button")) {
			boolean res = true;
			if ("Submit".equals(CELL_button_action)) {
				// res = frmwriter.addField(FormParams.getSubmitParams(
				// CELL_form_name, url, fixPath(IMG_PATH, CELL_CONTENT),
				// fixPath(IMG_PATH, CELL_CONTENT), (float) left,
				// (float) top, (float) left + (float) cellWidth,
				// (float) top + (float) cellHeight));
			} else if ("Reset".equals(CELL_button_action)) {// System.out.println("------------------"
															// +
															// fixPath(IMG_PATH,
															// CELL_CONTENT));
															// res =
															// frmwriter.addField(FormParams.getResetParams(
				// CELL_form_name, fixPath(IMG_PATH, CELL_CONTENT),
				// fixPath(IMG_PATH, CELL_CONTENT), (float) left,
				// (float) top, (float) left + (float) cellWidth,
				// (float) top + (float) cellHeight));
			} else if ("Clear".equals(CELL_button_action)) {
				// res = frmwriter.addField(FormParams.getClearParams(
				// CELL_form_name, fixPath(IMG_PATH, CELL_CONTENT),
				// fixPath(IMG_PATH, CELL_CONTENT), (float) left,
				// (float) top, (float) left + (float) cellWidth,
				// (float) top + (float) cellHeight));
			} else if ("AdvancedSubmit".equals(CELL_button_action)) {
				FormParams advSubBtn = FormParams.getSubmitParams(
						CELL_form_name, "", fixPath(IMG_PATH, CELL_CONTENT),
						fixPath(IMG_PATH, CELL_CONTENT), (float) left,
						(float) top, (float) left + (float) cellWidth,
						(float) top + (float) cellHeight);
				advSubBtn.submitFields = "submitFields";
				advSubBtn.preCode = "this.getField('xml_data').value = getXML(); popInternalParams(); this.getField('button_name').value = '"
						+ CELL_form_name
						+ "'; this.getField('ws_name').value = this.getField('WS2').value; ";
				// res = frmwriter.addField(advSubBtn);

				if (!bHasAdvancedJS) {
					// boolean res1 = frmwriter.addField(FormParams
					// .getHiddenParams("WS", "form_request"));
					// boolean res2 = frmwriter.addField(FormParams
					// .getHiddenParams("xml_data", ""));
					// boolean res3 = frmwriter.addField(FormParams
					// .getHiddenParams("doc_id", ""));
					// boolean res4 = frmwriter.addField(FormParams
					// .getHiddenParams("letter_id", ""));
					// boolean res5 = frmwriter.addField(FormParams
					// .getHiddenParams("session_id", ""));
					// boolean res6 = frmwriter.addField(FormParams
					// .getHiddenParams("env_name", ""));
					// boolean res7 = frmwriter.addField(FormParams
					// .getHiddenParams("button_name", ""));
					// boolean res8 = frmwriter.addField(FormParams
					// .getHiddenParams("ws_name", ""));
					// FormParams advSubJS = new FormParams();
					// advSubJS.javascript = JAVA_SCRIPT;
					// advSubJS.type = FormParams.ElementType.JAVASCRIPT;
					// boolean JSres = frmwriter.addField(advSubJS);
					// bHasAdvancedJS = JSres;
					// res = res && JSres && res1 && res2 && res3 && res4 &&
					// res5
					// && res6 && res7 && res8;
				}
			}
			if (!res) {
				// System.out
				// .println(CELL_form_name + ": " + frmwriter.getError());
			}
		} else if (CONTENT_TYPE.equals("combobox")) {
			String[] vals = OptionsParse.getVals(CELL_list_value);
			// if (!frmwriter.addField(FormParams.getComboParams(CELL_form_name,
			// vals[0], vals[1], sizeConvert(CELL_form_border_width),
			// CELL_form_border_color,
			// SAXimpl.borderstyleNames.get(CELL_form_border_style),
			// CELL_backgroundColor, (float) left, (float) top,
			// (float) left + (float) cellWidth, (float) top
			// + (float) cellHeight, Integer.parseInt(vals[2])))) {
			// System.out
			// .println(CELL_form_name + ": " + frmwriter.getError());
			// }
		} else if (CONTENT_TYPE.equals("checkbox")) {// System.out.println("CHECKBOX: "
														// + CELL_form_name);
														// if
														// (!frmwriter.addField(FormParams.getCheckParams(CELL_form_name,
			// PdfFormField.TYPE_CHECK,
			// sizeConvert(CELL_form_border_width),
			// CELL_form_border_color, CELL_backgroundColor,
			// SAXimpl.borderstyleNames.get(CELL_form_border_style),
			// (float) left, (float) top,
			// (float) left + (float) cellWidth, (float) top
			// + (float) cellHeight,
			// "YES".equals(CELL_form_default_selected)))) {
			// System.out
			// .println(CELL_form_name + ": " + frmwriter.getError());
			// }
		} else if (CONTENT_TYPE.equals("datebox")) {
			// frmwriter.addField(FormParams.getDateParams(CELL_form_name,
			// CELL_CONTENT, CELL_form_date_format == null ? "ddmmyyyy"
			// : CELL_form_date_format, (int) CELL_FONTSIZE,
			// sizeConvert(CELL_form_border_width),
			// SAXimpl.borderstyleNames.get(CELL_form_border_style),
			// CELL_FONT, CELL_FONT, CELL_color, CELL_form_border_color,
			// CELL_backgroundColor, (float) left, (float) top,
			// (float) left + (float) cellWidth, (float) top
			// + (float) cellHeight, SAXimpl.alignmentTypes
			// .get(CELL_HAlign)));
		} else if (CONTENT_TYPE.equals("emailbox")) {
			// frmwriter.addField(FormParams.getEmailParams(CELL_form_name,
			// CELL_CONTENT, (int) CELL_FONTSIZE,
			// sizeConvert(CELL_form_border_width),
			// SAXimpl.borderstyleNames.get(CELL_form_border_style),
			// CELL_FONT, CELL_FONT, CELL_color, CELL_form_border_color,
			// CELL_backgroundColor, (float) left, (float) top,
			// (float) left + (float) cellWidth, (float) top
			// + (float) cellHeight,
			// SAXimpl.alignmentTypes.get(CELL_HAlign)));
		} else if (CONTENT_TYPE.equals("radiobox")) {
			// if (!frmwriter.addField(FormParams.getRadioParams(CELL_form_name,
			// CELL_form_radiobutton_name, CELL_CONTENT, "",
			// FormParams.TextLocation.RIGHT, 1, "Helvetica", "0,0,0",
			// sizeConvert(CELL_form_border_width),
			// CELL_form_border_color, CELL_backgroundColor,
			// SAXimpl.borderstyleNames.get(CELL_form_border_style),
			// (float) left, (float) top,
			// (float) left + (float) cellWidth, (float) top
			// + (float) cellHeight,
			// SAXimpl.alignmentTypes.get(CELL_HAlign),
			// "YES".equals(CELL_form_default_selected)))) {
			// System.out
			// .println(CELL_form_name + ": " + frmwriter.getError());
			// }
		} else if (CONTENT_TYPE.equals("fileupload")) {

		}
		return null;
	}

	public static ItextResult tableContent(int CELL_HEADER_BOLD,
			int CELL_HEADER_ITALIC, double cellWidth, String CELL_color,
			double CELL_FONTSIZE, String CELL_FONT, int CELL_HEADER_ROWS,
			int CELL_TABLE_COLUMNS, int CELL_TABLE_ROWS, String FONTS_DIR,
			String CELL_CONTENT, double left, double top) {
		// System.out.println("==================== TABLE ====================");
		// int headerstyle = Font.BOLD * CELL_HEADER_BOLD ^ Font.ITALIC
		// * CELL_HEADER_ITALIC;
		// XmlTableParams params = new XmlTableParams((float) cellWidth,
		// Border.parseColor(CELL_color), (float) CELL_FONTSIZE,
		// CELL_FONT, CELL_HEADER_ROWS, headerstyle, CELL_TABLE_COLUMNS,
		// CELL_TABLE_ROWS);
		// XmlTable2Pdf xmltable2pdf = new XmlTable2Pdf(FONTS_DIR);
		// xmltable2pdf.importParams(params);
		// System.out.println(CELL_CONTENT);
		// String parseRes = XmlTable2Pdf.parse(xmltable2pdf, CELL_CONTENT,
		// false);
		// if (parseRes.equals("OK")) {
		// System.out.println("Left = " + new Double(left).toString());
		// System.out.println("Top = " + new Double(top).toString());
		// xmltable2pdf.getPdfTable().addTableAbs(cb, (float) left,
		// (float) top); // this doesn't seem to work if the width is
		// // zero, EVEN if lockedWidth is false.
		// } else { // will get here if the XML is invalid
		// System.out.println("Parsing failed: " + parseRes);
		// }
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

	public ItextStatus processPages(String IMG_DIR, CssClassParser classparser,
			java.util.HashMap<String, AdditionalMetadata.Metadata> metadata,
			java.util.ArrayList<ArchivePage> pages, String userdef, IndexParser.IndexData index,
			String pageTypes) {
		ItextResult itextres;
		ItextStatus res = ItextStatus.OK;

		if (pageTypes == null) {
			pageTypes = "";
		}
		boolean visible = true;

		for (int i = 0; i < pages.size() && res != ItextStatus.FAILED; i++) {
			try {
				ArchivePage page = pages.get(i);
				CssParser.Style pageStyle = classparser.getClasses().get("y" + page.mdid());
				boolean processThisPage = pageTypes.length() == 0 || pageTypes.contains(page.pageType());
				if (pageStyle != null && processThisPage) {
					itextres = this.newPage(null, (double) sizeReConvert(pageStyle.height()), (double) sizeReConvert(pageStyle.width()));

					if (i == 0 && this.docInfo != null) {
						this.pdf.getDocumentInfo().setKeywords(this.docInfo.getText());
					}
					
					if (i == 0 && this.hiddenText != null) {
						addSimpleText(this.hiddenText.text(), this.hiddenText.fontFamily(), this.hiddenText.left(), 
								(float)ClassLittle.getTop((int) pageStyle.height(), this.hiddenText.top(), (int) (this.hiddenText.height())), 
								this.hiddenText.width(), this.hiddenText.height());
					}
					
					for (ArchiveField field : page.fields()) {
						CssParser.Style fieldStyle = classparser.getClasses().get("y" + field.mdid());
						if (fieldStyle != null) {
							AdditionalMetadata.Metadata md = null;
							if (metadata != null) {
								md = metadata.get(field.mdid());
							}
							
							// Text (including userdef)
							
							if (field.contentType().equals("text") || field.contentType().equals("userdef")) {
								String value = field.contentType().equals("userdef") ? userdef : field.value();
								String accessType = "N";
								int headerLevel = 0;
								if (md != null) {
									accessType = md.accessMdType();
									headerLevel = Integer.parseInt(md.headerLevel());
								}
								itextres = this.textContent(fieldStyle, field.contentType(),
										-1, 0, value, new TableParams(), 0,
										(double) JavaItext.sizeConvert(field.left()),
										ClassLittle.getTop((int) pageStyle.height(), field.top(), (int) (fieldStyle.height())), 
										null, null, fieldStyle.linespace(), "no",
										"no", "no", headerLevel, accessType, "N", null, field.tagkey(), field.ordno(), index);			
							}
							
							// Image (including word insert)
							
							else if (field.contentType().equals("image")) {
								if (md != null) {
									itextres = this.imageContent(md.wordInsertType(), IMG_DIR, field.value().trim(), field.left(), field.top(),
											fieldStyle.height(), fieldStyle.width(),
											md.wordInsertPdf()/*Misc.readBinFile("C:\\Users\\davidz\\Desktop\\cutPdf7.pdf")*/, fieldStyle.getBorder(), md.objectDescr(), field.tagkey(), field.ordno(), index);
								} else {
									this.error_msgs.add("Additional Metadata not found for image field: " + field.mdid());
									res = ItextStatus.ERROR;
								}			
							} 
							
							// Barcode
							
							else if (field.contentType().equals("barcode")) {
								if (md != null) {
									itextres = this.barcodeContent((double) field.left(),
											(double) field.top(), fieldStyle, md,
											field.value(), "");
								} else {
									this.error_msgs.add("Additional Metadata not found for barcode field: " + field.mdid());
									res = ItextStatus.ERROR;
								}			
							}
							
							// SVG
							
							else if (field.contentType().equals("chart_pie") || field.contentType().equals("chart_bar")) {
								System.out.println(field.contentType());
								if (md != null) {
									itextres = this.SVGcontent(field.value(), md.goXslt(), field.left(), field.top(), md.objectDescr(), fieldStyle.fontSize());
								} else {
									this.error_msgs.add("Additional Metadata not found for SVG field: " + field.mdid());
									res = ItextStatus.ERROR; 
								}
							}
							
							// Textbox
							
							else if (field.contentType().equals("textbox")) {
								if (md != null) {
									if (md.visible().equals("true")) {
										float border = getLargestBorder(fieldStyle.getBorder());
										String borderColor = getFirstBorderColor(fieldStyle.getBorder());
										float left = sizeConvert(field.left());
										float top = this.pageHeight - sizeConvert(field.top()) - fieldStyle.height();
										FormParams textbox = FormParams.getTextParams(md.fieldName(), field.value(), (int)fieldStyle.fontSize(), border,
									            /*Integer borderstyle*/1, fieldStyle.fontFamily()/*, "Arial"*/, fieldStyle.color(), borderColor, fieldStyle.backgroundColor(),
									            left, top, left + fieldStyle.width(), 
									            top + fieldStyle.height(), fieldStyle.align());
										if (!this.frmwriter.addField(textbox)) {
											System.out.println("Error adding textbox: " + field.mdid());
										}
									} else {
										FormParams hidden = FormParams.getHiddenParams(md.fieldName(), field.value());
										if (!this.frmwriter.addField(hidden)) {
											System.out.println("Error adding hidden text field: " + field.mdid());
										}										
									}
								} else {
									this.error_msgs.add("Additional Metadata not found for textbox field: " + field.mdid());
									res = ItextStatus.ERROR; 									
								}
							}
							
							// textarea
							
							else if (field.contentType().equals("textarea")) {
								if (md != null) {
									if (md.visible().equals("true")) {
										float border = getLargestBorder(fieldStyle.getBorder());
										String borderColor = getFirstBorderColor(fieldStyle.getBorder());
										float left = sizeConvert(field.left());
										float top = this.pageHeight - sizeConvert(field.top()) - fieldStyle.height();
										FormParams textbox = FormParams.getTextAreaParams(md.fieldName(), field.value(), (int)fieldStyle.fontSize(), border,
									            /*Integer borderstyle*/1, fieldStyle.fontFamily()/*, "Arial"*/, fieldStyle.color(), borderColor, fieldStyle.backgroundColor(),
									            left, top, left + fieldStyle.width(), 
									            top + fieldStyle.height(), fieldStyle.align());
										if (!this.frmwriter.addField(textbox)) {
											System.out.println("Error adding textarea: " + field.mdid());
										}
									} else {
										FormParams hidden = FormParams.getHiddenParams(md.fieldName(), field.value());
										if (!this.frmwriter.addField(hidden)) {
											System.out.println("Error adding hidden text field: " + field.mdid());
										}
									}
								} else {
									this.error_msgs.add("Additional Metadata not found for textarea field: " + field.mdid());
									res = ItextStatus.ERROR; 									
								}
							}
							
//							// hidden textbox
//							
//							else if ((field.contentType().equals("textbox") || field.contentType().equals("textarea")) && !fieldStyle.visible()) {
//								if (md != null) {
//									FormParams hidden = FormParams.getHiddenParams(md.fieldName(), field.value());
//									if (!this.frmwriter.addField(hidden)) {
//										System.out.println("Error adding hidden text field: " + field.mdid());
//									}
//								} else {
//									this.error_msgs.add("Additional Metadata not found for hidden text box field: " + field.mdid());
//									res = ItextStatus.ERROR;
//								}
//							}
							
							// emailbox
							
							else if (field.contentType().equals("emailbox")) {
								if (md != null) {
									float border = getLargestBorder(fieldStyle.getBorder());
									String borderColor = getFirstBorderColor(fieldStyle.getBorder());
									float left = sizeConvert(field.left());
									float top = this.pageHeight - sizeConvert(field.top()) - fieldStyle.height();
									FormParams emailfield = FormParams.getEmailParams(md.fieldName(), field.value(), (int)fieldStyle.fontSize(), border,
								            /*Integer borderstyle*/1, fieldStyle.fontFamily()/*, "Arial"*/, fieldStyle.color(), borderColor, fieldStyle.backgroundColor(),
								            left, top, left + fieldStyle.width(), 
								            top + fieldStyle.height(), fieldStyle.align());
									if (!this.frmwriter.addField(emailfield)) {
										System.out.println("Error adding emailbox field: " + field.mdid());
									}
								} else {
									this.error_msgs.add("Additional Metadata not found for emailbox field: " + field.mdid());
									res = ItextStatus.ERROR;
								}
							}
							
							// datebox
							
							else if (field.contentType().equals("datebox")) {
								if (md != null) {
									float border = getLargestBorder(fieldStyle.getBorder());
									String borderColor = getFirstBorderColor(fieldStyle.getBorder());
									float left = sizeConvert(field.left());
									float top = this.pageHeight - sizeConvert(field.top()) - fieldStyle.height();
									FormParams datefield = FormParams.getDateParams(md.fieldName(), field.value(), md.dateFormat(), (int)fieldStyle.fontSize(), border,
								            /*Integer borderstyle*/1, fieldStyle.fontFamily()/*, "Arial"*/, fieldStyle.color(), borderColor, fieldStyle.backgroundColor(),
								            left, top, left + fieldStyle.width(), 
								            top + fieldStyle.height(), fieldStyle.align());
									if (!this.frmwriter.addField(datefield)) {
										System.out.println("Error adding datebox field: " + field.mdid());
									}								
								} else {
									this.error_msgs.add("Additional Metadata not found for datebox field: " + field.mdid());
									res = ItextStatus.ERROR;
								}
							}
							
							// combo
							
							else if (field.contentType().equals("combobox")) {
								if (md != null) {
									float border = getLargestBorder(fieldStyle.getBorder());
									String borderColor = getFirstBorderColor(fieldStyle.getBorder());
									float left = sizeConvert(field.left());
									float top = this.pageHeight - sizeConvert(field.top()) - fieldStyle.height();
									String[] vals = OptionsParse.getVals(md.comboList());
									FormParams combofield = FormParams.getComboParams(md.fieldName(), vals[0], vals[1], border, borderColor, 1, 
											fieldStyle.backgroundColor(), left, top, left + fieldStyle.width(), top + fieldStyle.height(), Integer.parseInt(vals[2]));
									if (!this.frmwriter.addField(combofield)) {
										System.out.println("Error adding combo field: " + field.mdid());
									}
								} else {
									this.error_msgs.add("Additional Metadata not found for combo field: " + field.mdid());
									res = ItextStatus.ERROR;									
								}
							}
							
							// button
							
							else if (field.contentType().equals("button")) {
								if (md != null) {
									float left = sizeConvert(field.left());
									float top = this.pageHeight - sizeConvert(field.top()) - fieldStyle.height();
									String action = md.buttonAction();
									FormParams button = null;
									if (action.equals("AdvancedSubmit")) { // not supported in PDF, and apparently not possible either
										// should we put the button in anyway?
									} else if (action.equals("Clear")) { // not yet working
										button = FormParams.getClearParams(md.fieldName()/*, "app.alert('hi');"*/, fixPath(IMG_DIR, field.value()), "imgdown", left, top, 
											left + fieldStyle.width(), top + fieldStyle.height());																				
									} else if (action.equals("Reset")) {
										button = FormParams.getResetParams(md.fieldName(), fixPath(IMG_DIR, field.value()), "imgdown", left, top, 
												left + fieldStyle.width(), top + fieldStyle.height());																														
									} else if (action.equals("Submit")) {
										//System.out.println("SUBMIT BUTTON: " + md.formUrl());
										button = FormParams.getSubmitParams(md.fieldName(), md.formUrl(), fixPath(IMG_DIR, field.value()), "imgdown", left, top, 
												left + fieldStyle.width(), top + fieldStyle.height());																				
									} else {
										this.error_msgs.add("Unknown button action type: '" + action + "': " + field.mdid());
										res = ItextStatus.ERROR;
									}
									if (button != null) {
										if (!this.frmwriter.addField(button)) {
											System.out.println("Error adding button field: " + field.mdid());
										}
									}
								} else {								
									this.error_msgs.add("Additional Metadata not found for button field: " + field.mdid());
									res = ItextStatus.ERROR;
								}
							}
							
							// checkbox
							
							else if (field.contentType().equals("checkbox")) {
								if (md != null) {
									float left = sizeConvert(field.left());
									float top = this.pageHeight - sizeConvert(field.top()) - fieldStyle.height();
									FormParams button = FormParams.getCheckParams(md.fieldName(), md.fieldName(), PdfFormField.TYPE_CHECK, 0f, "", fieldStyle.backgroundColor(), 0, 
											left, top, left + fieldStyle.width(), top + fieldStyle.height(), md.defaultSelected().equals("YES"));
									if (!this.frmwriter.addField(button)) {
										System.out.println("Error adding checkbox field: " + field.mdid());
									}
								} else {								
									this.error_msgs.add("Additional Metadata not found for checkbox field: " + field.mdid());
									res = ItextStatus.ERROR;
								}
							}
							
							if (!itextres.errorMsg().equals("")) {
								this.error_msgs.add(itextres.errorMsg());
								res = ItextStatus.ERROR;
							}
						} else {
							System.out.println("ERROR: No CSS found for field y" + page.mdid());
							this.error_msgs.add("ERROR: No CSS found for field y" + page.mdid());
							res = ItextStatus.ERROR;
						}
					}
				} else if (processThisPage) {
					System.out.println("ERROR: No CSS found for page y" + page.mdid());
					this.error_msgs.add("ERROR: No CSS found for page y" + page.mdid());
					res = ItextStatus.FAILED;
				}
			}
			catch (Exception e) {
				System.out.println("Exception thrown in processPages() - " + e.toString());
				this.error_msgs.add("Exception thrown in processPages() - " + e.toString());
				res = ItextStatus.ERROR;
			}
		}

		return res;
	}
	
	private float getLargestBorder(BorderParams borders) {
		float res = 0f;
		if (borders.borderBottom().getWidth() > res) {
			res = borders.borderBottom().getWidth();
		}
		if (borders.borderLeft().getWidth() > res) {
			res = borders.borderLeft().getWidth();
		}
		if (borders.borderRight().getWidth() > res) {
			res = borders.borderRight().getWidth();
		}
		if (borders.borderTop().getWidth() > res) {
			res = borders.borderTop().getWidth();
		}
		
		return res;
	}
	
	private static String getFirstBorderColor(BorderParams borders) {
		if (borders.borderBottom().getColor() != null) {
			return color2String(borders.borderBottom().getColor());
		}
		if (borders.borderLeft().getColor() != null) {
			return color2String(borders.borderLeft().getColor());
		}
		if (borders.borderRight().getColor() != null) {
			return color2String(borders.borderRight().getColor());
		}
		if (borders.borderTop().getColor() != null) {
			return color2String(borders.borderTop().getColor());
		}
		
		return null;
	}
	
	private static String color2String(com.itextpdf.kernel.colors.Color color) {
		float[] vals = color.getColorValue();
		int[] ivals = new int[vals.length];
		for (int i = 0; i < vals.length; i++) {
			ivals[i] = (int) (vals[i] * 255f);
		}
		return "#" + hexString(ivals[0]) + hexString(ivals[1]) + hexString(ivals[2]);
	}
	
	private static String hexString(int val) {
		String res = Integer.toString(val, 16);
		if (res.length() < 2) {
			res = "0" + res;
		}
		return res;
	}
	
	public static ArrayList<String> multiline(String fontName, boolean bold, int fontSize, float width, String text) {
		java.awt.Font font = new java.awt.Font(fontName, bold? java.awt.Font.BOLD: java.awt.Font.PLAIN, fontSize);
		return FontMeasurements.breakLines(font, FontMeasurements.Units.CM, width, text, true);
	}

	/**
	 * Kobi's method
	 * @param String_IN
	 * @return
	 */
	public static String reverseString(String String_IN, String[] prevs, boolean rtl) {
		if (String_IN == null || String_IN.length() < 2) {
			return String_IN;
		}
		
		//int Input_Len = String_IN.length();
		
		// Added by David Zalkin, December 2017
		boolean prevHeb = false;
		if (prevs != null) {
			for (String prev: prevs) {
				Bidi bidi = new Bidi(prev, Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT);
				if (!bidi.isLeftToRight()) {
					prevHeb = true;
				}
			}
		}

		Bidi allEnglish = new Bidi(String_IN, Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT);
		if (allEnglish.isLeftToRight() && !prevHeb && !rtl) {
			return String_IN;
		}

		Bidi bidi = new Bidi(String_IN, Bidi.DIRECTION_RIGHT_TO_LEFT);
		//int start;
		int end;

		StringBuilder result = new StringBuilder(String_IN.length() + 1);
		int j = 0;
		int countRun = bidi.getRunCount();
		for (int i = 0; i < countRun; i++) {
			//start = bidi.getRunStart(i);
			end = bidi.getRunLimit(i);
			for (; j < end; j++) {
				char A = String_IN.charAt(j);
				if (bidi.getLevelAt(j) % 2 == 1) {
					result.insert(0, A);
				} else {
					if (A == '-' && j > 0) {

						if ((int) String_IN.charAt(j - 1) >= 1488 // aleph
								&& (int) String_IN.charAt(j - 1) <= 1514) { // tav
							result.insert(0, '-');
						}
					} else {
						if (String_IN.charAt(end - 1) == '-') {
							if (j < end - 1) {
								result.insert(0,
										String_IN.substring(j, end - 1));
								result.insert(0, String_IN.charAt(end - 1));
								j = end - 1;
							} else
								result.insert(0, A);
						} else {
							result.insert(0, String_IN.substring(j, end));
							j = end - 1;
						}
					}
				}
			}
		}

		int index1 = result.indexOf("(");
		int index2 = result.indexOf(")");

		while (index1 != -1 || index2 != -1) {
			if ((index1 < index2 && index1 != -1) || index2 == -1) {
				result.replace(index1, index1 + 1, ")");
				index1 = result.indexOf("(", index1 + 1);
			} else {
				result.replace(index2, index2 + 1, "(");
				index2 = result.indexOf(")", index2 + 1);
			}
		}
		
		// Added by David Zalkin, December 2017
		index1 = result.indexOf("[");
		index2 = result.indexOf("]");

		while (index1 != -1 || index2 != -1) {
			if ((index1 < index2 && index1 != -1) || index2 == -1) {
				result.replace(index1, index1 + 1, "]");
				index1 = result.indexOf("[", index1 + 1);
			} else {
				result.replace(index2, index2 + 1, "[");
				index2 = result.indexOf("]", index2 + 1);
			}
		}
		
		return result.toString();
	}
	
	public static String reverseString(String String_IN) {
		return reverseString(String_IN, null, false);
	}

	// End of 'Helper code' snippet.

	public static void main(String[] args) throws Exception {
		java.security.CodeSource src = org.slf4j.impl.StaticLoggerBinder.class
				.getProtectionDomain().getCodeSource();
		// or: java.security.CodeSource src =
		// Class.forName("org.apache.commons.codec.binary.Base64").getProtectionDomain().getCodeSource();
		// but then need to catch ClassNotFoundException
		if (src != null) {
			java.net.URL jar = src.getLocation();
			System.out.println(jar.toString());
		}
//		byte[] pdf = Misc.readBinFile("C:\\Users\\davidz\\Documents\\problems\\kal auto\\2519738_170612462_1.pdf");
//		byte[] newpdf = optimizePDF(pdf, false);
//		Misc.writeBinFile(newpdf, "C:\\Users\\davidz\\Documents\\problems\\kal auto\\2519738_170612462_1b.pdf");
	}

	public enum  ItextStatus {
		/**
		 * Process concluded successfully with no errors or warnings.
		 */
		OK,
		/**
		 * Process concluded successfully, but there was at least one warning.
		 */
		WARNING,	
		/**
		 * Process concluded, but there was at least one error.
		 */
		ERROR,
		/**
		 * Process failed.
		 */
		FAILED;
	}

}
