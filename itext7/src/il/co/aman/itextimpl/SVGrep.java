package il.co.aman.itextimpl;

import com.itextpdf.awt.DefaultFontMapper;
import com.itextpdf.awt.PdfGraphics2D;
//import com.itextpdf.awt.geom.Rectangle;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Image;
import com.itextpdf.text.pdf.PdfTemplate;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.imageio.ImageIO;

import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.svg.SVGDocument;

/**
 * Converts the SVG data into its internal representation/<br>
 * Accepts the SVG either as a string or a full filepath
 */
public class SVGrep {

    BridgeContext ctx;
    SVGDocument doc;
    GVTBuilder builder;
    public boolean success;
    String svg;
    public Float[] dims;
    public Exception error;

    public SVGrep(String SVG, boolean fromFile) {
        try {
            if (fromFile) {
                this.svg = il.co.aman.apps.Misc.removeBOM(il.co.aman.apps.Misc.readFile(SVG));
            } else {
                this.svg = il.co.aman.apps.Misc.removeBOM(SVG);
            }
            String parser = XMLResourceDescriptor.getXMLParserClassName();
            SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
            doc = factory.createSVGDocument("", new StringReader(svg));
            UserAgent userAgent = new UserAgentAdapter();
            DocumentLoader loader = new DocumentLoader(userAgent);
            ctx = new BridgeContext(userAgent, loader);
            builder = new GVTBuilder();
            ctx.setDynamicState(BridgeContext.DYNAMIC);
            this.dims = getSvgDims(new javax.xml.transform.stream.StreamSource(new java.io.ByteArrayInputStream(this.svg.getBytes("UTF-8"))));
            success = true;
            error = null;
        } catch (IOException e) {
            success = false;
            error = e;
        }
    }

    /**
     * 
     * @param SVGdoc
     * @param x
     * @param y
     * @param javaitext
     * @return <code>null</code> if successful.
     */
    public static Exception addSVG(SVGrep SVGdoc, int x, int y, String altText, JavaItext javaitext) {
    	if (SVGdoc.success) {
    		float corr = 0.75f;
    		corr = 0.85f;
    		try {
    			if (SVGdoc.dims != null) {
    				int width = (int)Math.floor((double)SVGdoc.dims[0] * corr);
    				int height = (int)Math.floor((double)SVGdoc.dims[1] * corr);  
    				BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    				Graphics2D g2d = img.createGraphics();
    				g2d.scale(corr, corr);
    				GraphicsNode mapGraphics = SVGdoc.builder.build(SVGdoc.ctx, SVGdoc.doc);
    				mapGraphics.paint(g2d);
    				g2d.dispose();
    				javaitext.addImage(x, y, width / javaitext.svgFactor, height / javaitext.svgFactor, convertToImage(img), "", altText);
    				return null;
    			} else {
    				return new Exception("Error determining SVG dimensions");
    			}
    		}
    		catch (Exception e) {
    			return e;
    		}
    	} else {
    		return SVGdoc.error;
    	}
    }
    
    private static Image convertToImage(BufferedImage original) throws IOException {
    	Image result = null;
    	try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
    		ImageIO.write(original, "png", os);
    		os.flush();
    		ImageData image = ImageDataFactory.create(os.toByteArray());
    		result = new Image(image);
    	}
    	
    	return result;
    }
    
    static Float[] getSvgDims(javax.xml.transform.stream.StreamSource svg) {
        final String XSLT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" version=\"1.0\" xmlns:svg=\"http://www.w3.org/2000/svg\">"
                + "<xsl:output method=\"text\"/>"
                + "<xsl:template match=\"/\">"
                + "<xsl:value-of select=\"concat(svg:svg/@width, ',', svg:svg/@height)\"/>"
                + "</xsl:template>"
                + "</xsl:stylesheet>";
        try {
            il.co.aman.apps.XsltTransform transform = 
            		new il.co.aman.apps.XsltTransform(new javax.xml.transform.stream.StreamSource(new java.io.ByteArrayInputStream(XSLT.getBytes("UTF-8"))));
            java.io.StringWriter sw = new java.io.StringWriter();
            javax.xml.transform.stream.StreamResult res = new javax.xml.transform.stream.StreamResult(sw);
            transform.transform(svg, res);
            String[] dims = sw.toString().split(",");
            return new Float[]{Float.valueOf(dims[0]), Float.valueOf(dims[1])};
        } catch (UnsupportedEncodingException e) {
            return null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
