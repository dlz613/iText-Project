package il.co.aman.formit;

import com.itextpdf.text.Image;
import il.co.aman.formit.sendit.PdfResult;
import il.co.aman.formit.sendit.PdfSource;
import java.util.ArrayList;
import org.xml.sax.Attributes;

public class AddSigns {

	private Exception _error = null;
	private String _errmsg = "OK";
	private PdfResult _res;
	private String _ext = ".png";

	public Exception error() {
		return this._error;
	}

	public String message() {
		return this._errmsg;
	}

	public PdfResult result() {
		return this._res;
	}
	
	/**
	 * Sets the extension for the signature files. The default is <code>.png</code>.
	 * @param val
	 */
	public void setExt(String val) {
		if (!val.startsWith(".")) {
			val = "." + val;
		}
		this._ext = val;
	}

	private static class Parser extends GenericSAX {
		private int _num, _page;
		private float _x, _y, _width, _height;
		private boolean _required; // Added March 23, 2017
		private ArrayList<Signature> _res;

		public ArrayList<Signature> result() {
			return this._res;
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) {
			if (qName.equals("signs")) {
				this._res = new ArrayList<Signature>();
			} else if (qName.equals("sign")) {
				this._num = Integer.parseInt(attributes.getValue("order"));
				this._page = Integer.parseInt(attributes.getValue("page"));
				this._required = Integer.parseInt(attributes.getValue("required")) == 1;
			} else if (qName.equals("location")) {
				this._x = Float.parseFloat(attributes.getValue("x"));
				this._y = Float.parseFloat(attributes.getValue("y"));
			} else if (qName.equals("size")) {
				this._width = Float.parseFloat(attributes.getValue("width"));
				this._height = Float.parseFloat(attributes.getValue("height"));
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) {
			if (qName.equals("sign")) {
				this._res.add(new Signature(this._num, this._page, this._x,
						this._y, this._width, this._height, this._required));
			}
		}

		private class Signature {
			private int _num, _page;
			private float _x, _y, _width, _height;
			boolean _required;

			public int num() {
				return this._num;
			}

			public int page() {
				return this._page;
			}

			public float x() {
				return this._x;
			}

			public float y() {
				return this._y;
			}

			public float width() {
				return this._width;
			}

			public float height() {
				return this._height;
			}
			
			public boolean isRequired() {
				return this._required;
			}

			@Override
			public String toString() {
				return String
						.format("Number: %d, Page: %d, x: %f, y: %f, width: %f, height: %f, required: %b",
								this.num(), this.page(), this.x(), this.y(),
								this.width(), this.height(), this.isRequired());
			}

			public Signature(int num, int page, float x, float y, float width,
					float height, boolean required) {
				this._num = num;
				this._page = page;
				this._x = x;
				this._y = y;
				this._width = width;
				this._height = height;
				this._required = required;
			}
		}
	}

	/**
	 * Adds multiple signatures (or other images) to a PDF.
	 * @param pdfpath The full path to the input PDF file.
	 * @param xmlpath The full path to the XML signature definitions file. <br>
	 * Sample XML:<br>
	 * <pre>&lt;signs>
    &lt;sign order="0" required="1" page="1" underline="1" close="0">
        &lt;location x="1.3" y="15.5"/>
        &lt;size height="4.5" width="8.0"/>
        &lt;background-color>aqua&lt;/background-color>
    &lt;/sign>
    &lt;sign order="1" required="0" page="2" underline="0" close="1">
        &lt;location x="1.7" y="10.0"/>
        &lt;size height="4.5" width="8.0"/>
        &lt;background-color>transparent&lt;/background-color>
    &lt;/sign>
&lt;/signs></pre>
	 * @param ID The ID of the PDF - serves as the ID of the output file, and as the base of the name of the image files: <code>ID_0.png</code>, etc.
	 * @param imagefolder The location of the image files (must include final delimiter).
	 * @param outpath The full path of the output PDF).
	 * @return <code>true</code> if successful. If unsuccessful, returns an {@link Exception} in <code>error()</code> and a message in <code>message()</code>.
	 */
	public boolean addSigns(String pdfpath, String xmlpath, String ID, String imagefolder, String outpath) {
		String xml;
		try {
			PdfSource src = new PdfSource(pdfpath);
			try {
				xml = il.co.aman.apps.Misc.readFile(xmlpath);
				Parser parser = new Parser();
				Exception res = Parser.parseEx(parser, xml, false);
				if (res != null) {
					this._error = res;
					this._errmsg = "Error parsing XML signature definitions.";
					return false;
				}
				try {
					StampPdf stamper = new StampPdf(src);
					for (Parser.Signature signature : parser.result()) {
						//System.out.println(signature.toString());
						String imgpath = imagefolder + ID + "_"	+ Integer.toString(signature.num()) + this._ext;
						Image img = null;
						try {
							img = Image.getInstance(imgpath);
						}
						catch (Exception e) {
							if (signature.isRequired()) {
								this._error = e;
								this._errmsg = "Error adding signature number "	+ Integer.toString(signature.num());
								return false;								
							}
						}
						boolean imgRes = stamper.addImage(signature.page(), img,
								signature.x(), signature.y(),
								StampPdf.sizeConvert(signature.width()),
								StampPdf.sizeConvert(signature.height()));
						if (!imgRes && signature.isRequired()) {
							this._error = stamper.getError();
							this._errmsg = "Error adding signature number "	+ Integer.toString(signature.num());
							return false;
						}
					}
					stamper.close();
					this._res = stamper.outPdf();
					stamper.outPdf().writeFile(outpath);
					return true;
				} catch (Exception e) {
					this._error = e;
					this._errmsg = "Error adding signatures to PDF.";
					return false;
				}
			} catch (java.io.IOException e) {
				this._error = e;
				this._errmsg = "Error reading XML signature definitions.";
				return false;
			}
		} catch (Exception e) {
			this._error = e;
			this._errmsg = "Error reading PDF file.";
			return false;
		}
	}

	public static void main(String[] args) throws java.io.IOException {
		String pdf = "C:\\Users\\davidz\\Desktop\\PDFs\\kalauto2_with page numbers.pdf";
		String xmldefs = "C:\\Users\\davidz\\Desktop\\sign.xml";
		String outpdf = "C:\\Users\\davidz\\Desktop\\1234.pdf";
		String imgpath = "C:\\Users\\davidz\\Desktop\\signs\\";
		String ID = "1234";
		AddSigns addsigns = new AddSigns();
		if (!addsigns.addSigns(pdf, xmldefs, ID, imgpath, outpdf)) {
			System.out.println(addsigns.error());
			System.out.println(addsigns.message());
		} else {
			//addsigns.result().writeFile(outpdf);
			System.out.println("done");
		}
	}

}
