package il.co.aman.formit;

import il.co.aman.apps.Misc;

import java.awt.HeadlessException;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.attribute.standard.PrinterName;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageable; // in PdfBox 1.8.9
import org.ghost4j.display.PageRaster;
import org.ghost4j.document.DocumentException;
import org.ghost4j.document.PDFDocument;
import org.ghost4j.renderer.RendererException;
import org.ghost4j.renderer.SimpleRenderer;
import org.ghost4j.util.ImageUtil;
//import org.icepdf.ri.common.PrintHelper;

/**
 * Prints PDF files, using the {@link PDDocument} and {@link PDPageable}
 * classes.
 *
 * @author davidz
 */
public class Printing {

    public enum ErrorLocation {

        LOADING, PRINTING
    }
    PrinterJob printerjob;
    DocPrintJob job;
    Exception error;
    ErrorLocation errorLocation;
    String exclusion = "\\s\\(redirected\\s\\d+\\)"; // a regular expression defining a part of the printer name which is to be ignored when locating the desired service
    // the default is <space>(redirected<space><integer>)
    Pattern pattern = null;

    /**
     *
     */
    public Printing() {
        this.printerjob = PrinterJob.getPrinterJob();
        _comp();
    }

    /**
     *
     * @param printerName
     */
    public Printing(String printerName) {
        this();
        this.error = this.setPrinterName(printerName);
    }

    private void _comp() {
        try {
            this.pattern = Pattern.compile(this.exclusion);
        } catch (Exception e) {
            this.pattern = null;
        }
    }

    public Exception getError() {
        return this.error;
    }

    public ErrorLocation getErrorLocation() {
        return this.errorLocation;
    }

    public void setExclusion(String val) {
        this.exclusion = val;
        _comp();
    }

    /**
     *
     * @return The name of the current printer, or <code>&lt;NONE&gt;</code> if
     * none.
     */
    public String getPrinterName() {
        PrintService svc = this.printerjob.getPrintService();
        if (svc == null) {
            return "<NONE>";
        } else {
            javax.print.attribute.PrintServiceAttribute att = svc.getAttribute(PrinterName.class);
            return att.toString();
        }
    }

    private String _fixName(String name) {
        if (this.pattern != null) {
            return null;
        } else {
            return name;
        }
    }

    /**
     *
     * @param name
     * @return <code>null</code> if successful.
     */
    public final Exception setPrinterName(String name) {
        try {
            this.printerjob.setPrintService(_getServiceByName(name, true));
            //this.printerjob.setPrintService(null);
            this.job = this.printerjob.getPrintService().createPrintJob();
            return null;
        } catch (PrinterException e) {
            return e;
        }
    }

    public PrinterJob getPrinterJob() {
        return this.printerjob;
    }

    /**
     *
     * @return A <code>String</code> array of the available printer names.
     */
    public static String[] printerNames() {
        PrintService[] services = PrinterJob.lookupPrintServices();
        String[] names = new String[services.length];
        for (int i = 0; i < services.length; i++) {
            names[i] = services[i].getAttribute(PrinterName.class).toString();
        }
        return names;
    }

    private static PrintService _getServiceByName(String name, boolean debug) {
        if (debug) {
            System.out.println("Searching for service: " + name);
        }
        PrintService res = null;
        PrintService[] services = PrinterJob.lookupPrintServices();
        for (PrintService service : services) {
            if (service.getAttribute(PrinterName.class).toString().equals(name)) {
                if (debug) {
                    System.out.println("Found: " + service.getAttribute(PrinterName.class).toString());
                }
                res = service;
            }
        }
        return res;
    }

    /**
     *
     * @param doc
     * @param silent If <code>true</code>, prints silently.
     * @param times The number of times to print the document.
     * @return <code>null</code> if successful.
     */
    public Exception print(PDDocument doc, boolean silent, int times) {
        if (this.printerjob == null) {
            return new PrinterException("The given printer job is null.");
        } else {
            try {
                Exception res = null;
                this.printerjob.setPageable(new PDPageable(doc, this.printerjob));
                if (silent || this.printerjob.printDialog()) {
                    for (int i = 0; i < times; i++) {
                        try {
                            this.printerjob.print();
                        } catch (java.awt.print.PrinterException e) {
                            res = e;
                        }
                    }
                }
                return res;
            } catch (IllegalArgumentException e) {
                return e;
            } catch (NullPointerException e) {
                return e;
            } catch (HeadlessException e) {
                return e;
            } catch (PrinterException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return e1;
			}
        }
    }

    public class OKException extends Exception {

        public OKException(String msg) {
            super(msg);
        }
    }

    /**
     * Prints only one time.
     *
     * @param doc
     * @param silent If <code>true</code>, prints silently.
     * @return <code>null</code> if successful.
     */
    public Exception print(PDDocument doc, boolean silent) {
        return print(doc, silent, 1);
    }

    /**
     *
     * @param pdf
     * @param silent If <code>true</code>, prints silently.
     * @param times The number of times to print the document.
     * @return <code>null</code> if successful.
     */
    public Exception printPdf(InputStream pdf, boolean silent, int times) {
        try {
            this.errorLocation = ErrorLocation.LOADING;
            PDDocument doc = PDDocument.load(pdf);
            this.errorLocation = ErrorLocation.PRINTING;
            Exception e = this.print(doc, silent, times);
            doc.close();

            return e;
        } catch (IOException e) {
            return e;
        }
    }

    /**
     * Prints only one time.
     *
     * @param pdf
     * @param silent If <code>true</code>, prints silently.
     * @return <code>null</code> if successful.
     */
    public Exception printPdf(InputStream pdf, boolean silent) {
        return printPdf(pdf, silent, 1);
    }

    /**
     *
     * @param pdf
     * @param times The number of times to print the document.
     * @return <code>null</code> if successful.
     */
    public Exception silentPrintPdf(InputStream pdf, int times) {
        return printPdf(pdf, true, times);
    }

    /**
     * Prints only one time.
     *
     * @param pdf
     * @return <code>null</code> if successful.
     */
    public Exception silentPrintPdf(InputStream pdf) {
        return silentPrintPdf(pdf, 1);
    }

    public static Exception gsprint(String pdf, String printername) {
        try {
            PDFDocument doc = new PDFDocument();
            doc.load(new java.io.File(pdf));

            SimpleRenderer renderer = new SimpleRenderer();
            renderer.setAntialiasing(SimpleRenderer.OPTION_ANTIALIASING_HIGH);
            //renderer.setResolution(300);
            com.lowagie.text.pdf.PdfReader reader = new com.lowagie.text.pdf.PdfReader(pdf);
            com.lowagie.text.Rectangle rect = reader.getPageSize(1);
            final int height = (int) rect.getHeight();
            final int width = (int) rect.getWidth();
            java.util.List<PageRaster> pages = renderer.run(doc, 0, doc.getPageCount());
            java.util.List<java.awt.Image> images = ImageUtil.convertPageRastersToImages(pages);
            for (final java.awt.Image image : images) {
                printImage(image, printername);
//                java.awt.print.PrinterJob printerJob = java.awt.print.PrinterJob.getPrinterJob();
//                printerJob.setPrintService(_getServiceByName(printername, false));
//                printerJob.setPrintable(new java.awt.print.Printable() {
//                    public int print(java.awt.Graphics graphics, java.awt.print.PageFormat pageFormat, int pageIndex) throws PrinterException {
//                        if (pageIndex != 0) {
//                            return NO_SUCH_PAGE;
//                        }
//                        //graphics.drawImage(image, 0, 0, image.getWidth(null), image.getHeight(null), null);
//                        graphics.drawImage(image, 0, 0, width, height, null);
//                        return PAGE_EXISTS;
//                    }
//                });
//                //if (printerJob.printDialog()) {
//                try {
//                    printerJob.print();
//                } catch (Exception prt) {
//                    System.err.println(prt.getMessage());
//                }
                //}
            }
            return null;
        } catch (IOException e) {
            return e;
        } catch (DocumentException e) {
            return e;
        } catch (RendererException e) {
            return e;
        }
    }

    public static Exception printImage(final java.awt.Image image, String printername) {
        try {
            PrinterJob printerJob = PrinterJob.getPrinterJob();
            printerJob.setPrintService(_getServiceByName(printername, false));
            printerJob.setPrintable(new java.awt.print.Printable() {
                @Override
                public int print(java.awt.Graphics graphics, java.awt.print.PageFormat pageFormat, int pageIndex) throws PrinterException {
                    if (pageIndex != 0) {
                        return NO_SUCH_PAGE;
                    }
                    graphics.drawImage(image, 0, 0, image.getWidth(null), image.getHeight(null), null);
                    //graphics.drawImage(image, 0, 0, width, height, null);
                    return PAGE_EXISTS;
                }
            });
            printerJob.print();
            return null;
        } catch (PrinterException prt) {
            return prt;
        }
    }

    public static Exception pdf2imageprint(String pdf, String printername) {
        try {
            String cmd = "C:\\users\\davidz\\Dropbox\\work\\FormIT_5.8.0.0\\lib\\pdf2image-0.53-win32\\pdf2image.exe";
            String args = "-dev png16m -zoom 2.7 \"PDFPATH\"".replace("PDFPATH", pdf);
            ProcessBuilder pb = new ProcessBuilder(cmd, args);
            java.util.Map<String, String> environ = pb.environment();
            String path = null;
            java.util.Iterator it = environ.entrySet().iterator();
            while (it.hasNext()) {
                String key = it.next().toString();
                if (key.toUpperCase().startsWith("PATH=")) {
                    path = key;
                    break;
                }
            }
            if (path != null) {
                environ.remove(path);
                path += ";C:\\Program Files\\gs\\gs9.05\\bin";
                environ.put("Path", path.substring(path.indexOf("=")));
                final Process process = pb.start();
                InputStream is = process.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line;
//                line = br.readLine();
//                while (line != null) {
//                    System.out.println(line);
//                    line = br.readLine();
//                }
                is.close();
            }

            return null;
        } catch (IOException e) {
            return e;
        }
    }

//    public static Exception pdfRendererPrint(String printerName, String pdfPath) {
//        try {
//            File f = null;
//            RandomAccessFile fis = null;
//            FileChannel fc = null;
//            ByteBuffer bb = null;
//            String printer = printerName;
//            PrintService printService = PrintHelper.getPrintService(printer);
//
//            f = new File(pdfPath);
////Read only access would work too
//            fis = new RandomAccessFile(f, "rw");
//            fc = fis.getChannel();
//            bb = ByteBuffer.allocate((int) fc.size());
//            fc.read(bb);
//            ﻿﻿﻿﻿﻿﻿﻿﻿
//
////Do not map the file to a ByteBuffer as the examples show.
//// There is a reason why in java bug #474038
//// http://bugs.sun.com/view_bug.do?bug_id=4724038
////fc.map(FileChannel.MapMode.READ_WRITE, 0, fc.size());
////bb = fc.map(FileChannel.MapMode.READ_WRITE, 0, fc.size());
//            PDFFile pdfFile = new PDFFile(bb); // Create PDF Print Page
//            PDFPrintPage pages = new PDFPrintPage(pdfFile);
//// Create Print Job
//            PrinterJob pjob = PrinterJob.getPrinterJob();
//            pjob.setPrintService(printService);
//
//            PageFormat pf = PrinterJob.getPrinterJob().defaultPage();
//
//            pf.setOrientation(PageFormat.PORTRAIT);
//
//            Paper paper = new Paper();
//
////This is to fix an error in PDF-Renderer
////View http://juixe.com/techknow/index.php/2008/01/17/print-a-pdf-document-in-java/ for details
////Printing a PDF is also possible by sending the bytes directly to the printer, but
////  the printer would have to support it.
//            paper.setImageableArea(0, 0, paper.getWidth() * 2, paper.getHeight());
//
//            pf.setPaper(paper);
//
//            pjob.setJobName(f.getName());
//
//            Book book = new Book();
//            book.append(pages, pf, pdfFile.getNumPages());
//            pjob.setPageable(book);
//            pjob.print();
//
//        } catch (FileNotFoundException e) {
////do your error action
//        } catch (IOException e) {
////do your error action
//        } catch (PrinterException e) {
////do your error action
//        } finally {
//            try {
//                if (fc != null) {
//                    fc.close();
//                    fc = null;
//                }
//            } catch (IOException e) {
//                log.error(e);
////handle error here
//            }
//            try {
//                if (fis != null) {
//                    fis.close();
//                    fis = null;
//                }
//            } catch (IOException e) {
////handle error here
//            }
//            if (bb != null) {
//                bb.clear();
//            }
//        }
//    }

    public static void main(String[] args) {
		java.security.CodeSource src = org.apache.pdfbox.pdmodel.PDDocument.class
				.getProtectionDomain().getCodeSource();
		// or: java.security.CodeSource src =
		// Class.forName("org.apache.commons.codec.binary.Base64").getProtectionDomain().getCodeSource();
		// but then need to catch ClassNotFoundException
		if (src != null) {
			java.net.URL jar = src.getLocation();
			System.out.println(jar.toString());
		}
		System.exit(0);

    	
//        String filename = "\\\\chaims\\c$\\Documents and Settings\\davidz\\Desktop\\PDFs\\betterplace.pdf";
//        if (args.length > 0) {
//            filename = args[0];
//        }
//        Printing printing = new Printing();
//        //System.out.println(printing.getPrinterName());
//        il.co.aman.apps.Misc.printIfNotNull(printing.setPrinterName("\\\\amanad\\HP3015"));
//        System.out.println(printing.getPrinterName());
//        try {
//            Exception e = printing.silentPrintPdf(new java.io.FileInputStream(filename), 2);
//            if (e != null) { // it IS possible to get here, because the Exception returned by silentPrintPdf is caught by a try-catch in the method
//                System.out.println(il.co.aman.apps.Misc.message(e));
//                System.out.println(printing.errorLocation);
//            }
//        } catch (Exception e) {
//            System.out.println(il.co.aman.apps.Misc.message(e));
//        }
//
//        String[] names = printerNames();
//        System.out.println("\n*** Available printers: ***\n");
//        for (int i = 0; i < names.length; i++) {
//            System.out.println(names[i]);
//        }
        Printing printing = new Printing("\\\\AMANAD\\HP3015");
        System.out.println(Misc.message(printing.getError()));
        Exception res = null;
        try {
            res = printing.silentPrintPdf(new java.io.FileInputStream(new java.io.File("\\\\davidz\\c$\\Users\\davidz\\Desktop\\PDFs\\amanSeptember.pdf")));
        } catch (FileNotFoundException e) {
            System.out.println(Misc.message(e));
        }
        if (res != null) {
            System.out.println(">>>>>>>>>>>>>> " + Misc.message(res));
        }
//        Exception e = pdf2imageprint("\\\\chaims\\c$\\Documents and Settings\\davidz\\Desktop\\PDFs\\amanSeptember.pdf", "\\\\amanad\\HP3015");
//        if (e != null) {
//            System.out.println(Misc.message(e));
//        }

//        Exception e = gsprint("\\\\chaims\\c$\\Documents and Settings\\davidz\\Desktop\\PDFs\\amanSeptember.pdf", "\\\\amanad\\HP3015");
//        if (e != null) {
//            System.out.println(Misc.message(e));
//        }
    }
}
