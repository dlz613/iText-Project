package il.co.aman.formit;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfCopyFields;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.SimpleBookmark;
import com.itextpdf.text.pdf.SimpleNamedDestination;
import com.itextpdf.text.pdf.PdfASmartCopy;
import com.itextpdf.text.pdf.PdfSmartCopy;

import il.co.aman.apps.Misc;
import il.co.aman.formit.sendit.PdfResult;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;

/**
 * Concatenates two or more PDFs.
 *
 * @author davidz
 */
public class PdfConcat {

	ArrayList<String> _invalid;
	Exception _error;
	// PdfCopyFields _copy;
	boolean _overwrite, _open, _first, _delete;
	String _outpath, _tmpdir, _pdftkpath;
	int _batch; // how many pages to combine before writing the intermediate
				// version to the disk
	int _current;
	StreamResult _intermediate;

	// final static String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

	public ArrayList<String> getInvalid() {
		return this._invalid;
	}

	public Exception getError() {
		return this._error;
	}

	public void resetError() {
		this._error = null;
	}

	public boolean isOpen() {
		return this._open;
	}

	/**
	 * Don't delete the temp folder after the concatenation.
	 */
	public void noDelete() {
		this._delete = false;
	}

	public PdfConcat() {
		this._open = false;
		this._error = null;
		this._batch = 10;
		this._delete = true;
	}

	public void open(String outpath, boolean overwrite) {
		this._outpath = outpath;
		this._overwrite = overwrite;
		this._open = true;
		this._first = true;
		this._intermediate = null;
		this._current = 0;
		this._tmpdir = null;
	}

	/**
	 * Uses <code>pdftk</code> to do the concatenation.<br>
	 * Uses the values in {@link PdfAppsParams} for the temporary folder and the
	 * path to <code>pdftk.exe</code>
	 *
	 * @param outpath
	 */
	public void open(String outpath) {
		open(outpath, PdfAppsParams.PDF_TEMP, PdfAppsParams.PDFTK_PATH);
	}

	/**
	 * Uses <code>pdftk</code> to do the concatenation.
	 *
	 * @param outpath
	 * @param tmpdir
	 *            The folder to write the PDFs before concatenating them. It
	 *            will be deleted after the concatenation.
	 * @param pdftkpath
	 *            The full path to <code>pdftk.exe</code>
	 */
	public void open(String outpath, String tmpdir, String pdftkpath) {
		this._outpath = outpath;
		this._tmpdir = tmpdir;
		this._pdftkpath = pdftkpath;
		_init();
	}

	public String close() {
		if (Misc.notNullOrEmpty(this._tmpdir)) {
			if (this._open) {
				Object success = pdftkConcat(this._pdftkpath, this._tmpdir,
						this._outpath, this._delete, true);
				this._open = false;
				if (success instanceof String) {
					return (String) success;
				} else {
					this._error = (Exception) success;
					return null;
				}
			} else {
				this._error = new Exception(
						"Concatenation process not properly opened.");
				return null;
			}
		} else {
			this._open = false;
			this._error = null;
			if (this._intermediate != null) {
				this._intermediate.outPDF.writeFile(this._outpath);
			}
			return null;
		}
	}

	// At Menora there is a different version of this same method, which only
	// does normalization
	public static Object pdftkConcat(String pdftkpath, String tmpdir,
			String outpath, boolean delete, boolean normalize) {
		StringBuilder res = new StringBuilder();
		try {
			callPdftk(pdftkpath, tmpdir + "*.pdf", outpath);
			if (delete) {
				File tmp = new File(tmpdir);
				FileUtils.cleanDirectory(tmp);
				tmp.delete();
			}

			if (normalize) {
				// June 2014 - remove the duplicated fonts and other resources
				Misc.writeBinFile(outpath, normalizeFile(new File(outpath), false));
			}

			return res.toString();
		} catch (Exception e) {
			return e;
		}
	}

	/**
	 * Receives the input files as elements of a {@link String} array.
	 *
	 * @param pdftkpath
	 * @param input
	 *            The full paths to the PDF files to be concatenated.
	 * @param outpath
	 * @param wd
	 *            working directory in which to run <code>pdftk</code>
	 * @return
	 * @throws IOException
	 */
	public static String callPdftk(String pdftkpath, String[] input,
			String outpath, String wd) throws IOException {
		String[] arg_array = new String[input.length + 4];
		arg_array[0] = pdftkpath;
		int last_args = input.length + 1;
		for (int i = 1; i <= input.length; i++) {
			arg_array[i] = input[i - 1];
		}
		arg_array[last_args] = "cat";
		arg_array[last_args + 1] = "output";
		arg_array[last_args + 2] = outpath;
		ProcessBuilder pb = new ProcessBuilder(arg_array);
		return Misc.runProcess(arg_array, wd);
	}

	/**
	 * Receives the input files as CSV.
	 *
	 * @param pdftkpath
	 * @param input
	 *            The full paths to the PDF files to be concatenated, delimited
	 *            by a comma.
	 * @param outpath
	 * @param wd
	 *            working directory in which to run <code>pdftk</code>
	 * @return
	 * @throws IOException
	 */
	public static String callPdftk(String pdftkpath, String input,
			String outpath, String wd) throws IOException {
		String[] inputs = input.split(",");
		return callPdftk(pdftkpath, inputs, outpath, wd);
	}

	public static String callPdftk(String pdftkpath, String input,
			String outpath) throws IOException {
		StringBuilder res = new StringBuilder();
		Process process = new ProcessBuilder(pdftkpath, input, "cat", "output",
				outpath).start();
		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line;
		while ((line = br.readLine()) != null) {
			res.append(line);
		}
		return res.toString();
	}

	public static void normalize(String pdfpath, boolean tagged) throws Exception {
		Misc.writeBinFile(pdfpath, normalizeFile(new File(pdfpath), tagged));
	}

	public void setBatch(int val) {
		this._batch = val;
	}

	/**
	 * Copies a PDF, removing duplicate resources (fonts, images). When
	 * <code>pdftk</code> is used to concatenate PDFs, the resources are
	 * duplicated even if they are identical - this method can greatly reduce
	 * the size of the concatenated PDF. This problem also occurs when
	 * {@link RemoveAreas} is used to remove content from a PDF.
	 *
	 * @param srcPdfFile
	 *            This is a {@link File} object because <code>pdftk</code>
	 *            writes to a file.
	 * @return The improved PDF as a byte array.
	 * @throws Exception
	 */
	public static byte[] normalizeFile(File srcPdfFile, boolean tagged) throws Exception {
		return normalizeFile(new PdfReader(srcPdfFile.getCanonicalPath()), tagged);
	}

	/**
	 * Copies a PDF, removing duplicate resources (fonts, images). When
	 * <code>pdftk</code> is used to concatenate PDFs, the resources are
	 * duplicated even if they are identical - this method can greatly reduce
	 * the size of the concatenated PDF. This problem also occurs when
	 * {@link RemoveAreas} is used to remove content from a PDF.
	 *
	 * @param pdfReader
	 * @return The optimized PDF as a byte array.
	 * @throws Exception
	 */
	public static byte[] normalizeFile(PdfReader pdfReader, boolean tagged) throws Exception {
		Document pdfDocument = new Document();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			//PdfCopy pdfSmartCopy = new PdfASmartCopy(pdfDocument, bos, com.itextpdf.text.pdf.PdfAConformanceLevel.PDF_A_3B);
			PdfCopy pdfSmartCopy = new PdfSmartCopy(pdfDocument, bos);
			if (tagged) {
				pdfSmartCopy.setTagged();
			}
			pdfDocument.open();

			// Where the magic happens
			
			for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
				pdfSmartCopy.addPage(pdfSmartCopy.getImportedPage(pdfReader, i));
			}

			// Preserve bookmarks and internal links
			pdfSmartCopy.addNamedDestinations(SimpleNamedDestination.getNamedDestination(pdfReader, false), 0);
			// ArrayList<HashMap<String, Object>> allbookmarks = new
			// ArrayList<HashMap<String, Object>>();
			List<HashMap<String, Object>> bookmarks = SimpleBookmark.getBookmark(pdfReader);
			SimpleBookmark.shiftPageNumbers(bookmarks, 0, null);
			// allbookmarks.addAll(bookmarks);
			pdfSmartCopy.setOutlines(/* all */bookmarks);

			pdfDocument.close();
			return bos.toByteArray();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println(e);
			return null;
		} finally {
			if (pdfReader != null) {
				pdfReader.close();
			}
		}
	}
	
	public static byte[] normalizeFile(PdfReader pdfReader) throws Exception {
		return normalizeFile(pdfReader, false);
	}

	/**
	 * Adds a single PDF to a file, writing the result immediately to the disk.<br>
	 * Must be called only <b>after</b>
	 * <code>open(String outpath, boolean overwrite)</code>. <br>
	 * If any error occurred, the {@link Exception} will be in
	 * <code>getError()</code>.
	 *
	 * @param inPDF
	 */
	public void addPdf(byte[] inPDF) {
		if (this._open) {
			if (this._first) {
				try {
					if (this._overwrite) {
						Misc.writeBinFile(this._outpath, inPDF);
					} else {
						this._intermediate = pdfConcat(new ConcatSource(
								new byte[][] { Misc.readBinFile(this._outpath),
										inPDF }));
						this._error = this._intermediate.error;
						this._current++;
						// this._error = pdfConcat(new ConcatSource(new
						// byte[][]{Misc.readBinFile(this._outpath), inPDF}),
						// this._outpath);
					}
					this._first = false;
				} catch (Exception e) {
					this._error = e;
				}
			} else {
				try {
					if (this._intermediate == null) {
						this._intermediate = pdfConcat(new ConcatSource(
								new byte[][] { Misc.readBinFile(this._outpath),
										inPDF }));
					} else {
						this._intermediate = pdfConcat(new ConcatSource(
								new byte[][] {
										this._intermediate.outPDF.getBytes(),
										inPDF }));
						this._error = this._intermediate.error;
						this._current++;
						if (this._current > this._batch) {
							this._intermediate.outPDF.writeFile(this._outpath);
							this._current = 0;
							this._intermediate = null;
						}
					}
					// this._error = pdfConcat(new ConcatSource(new
					// byte[][]{Misc.readBinFile(this._outpath), inPDF}),
					// this._outpath);
				} catch (Exception e) {
					this._error = e;
				}
			}
		}
	}

	/**
	 * For use with PDFs without fields. Adds a single PDF to a file, writing
	 * the result immediately to the disk.<br>
	 * Must be called only <b>after</b>
	 * <code>open(String outpath, boolean overwrite)</code>. <br>
	 * If any error occurred, the {@link Exception} will be in
	 * <code>getError()</code>.
	 *
	 * @param inPDF
	 */
	public void addPdfSimple(byte[] inPDF) {
		if (this._open) {
			if (this._first) {
				try {
					if (this._overwrite) {
						Misc.writeBinFile(this._outpath, inPDF);
					} else {
						this._error = pdfConcatSimple(new ConcatSource(
								new byte[][] { Misc.readBinFile(this._outpath),
										inPDF }), this._outpath);
					}
					this._first = false;
				} catch (Exception e) {
					this._error = e;
				}
			} else {
				try {
					this._error = pdfConcatSimple(new ConcatSource(
							new byte[][] { Misc.readBinFile(this._outpath),
									inPDF }), this._outpath);
				} catch (Exception e) {
					this._error = e;
				}
			}
		}
	}

	/**
	 *
	 * @param source
	 *            The PDFs to be concatenated.
	 * @return The concatenated PDF.
	 */
	public static StreamResult pdfConcat(ConcatSource source) {
		if (source != null) {
			try {
				ByteArrayOutputStream outPDF = new ByteArrayOutputStream();
				PdfCopyFields copy = new PdfCopyFields(outPDF);
				copy.open();
				for (InputStream inPDF : source.inPDFs) {
					copy.addDocument(new PdfReader(inPDF));
				}
				copy.close();
				return new StreamResult(new PdfResult(outPDF), null);
			} catch (DocumentException e) {
				return new StreamResult(null, e);
			} catch (IOException e) {
				return new StreamResult(null, e);
			}
		} else {
			return new StreamResult(null, new Exception(
					"Null ConcatSource supplied."));
		}
	}

	public static StreamResult pdfConcat_2(ConcatSource source) {
		if (source != null) {
			try {
				ByteArrayOutputStream outPDF = new ByteArrayOutputStream();
				Document doc = new Document();
				PdfCopy copy = new PdfCopy(doc, outPDF);
				copy.open();
				doc.open();
				for (InputStream inPDF : source.inPDFs) {
					copy.addDocument(new PdfReader(inPDF));
				}
				copy.close();
				return new StreamResult(new PdfResult(outPDF), null);
			} catch (DocumentException e) {
				return new StreamResult(null, e);
			} catch (IOException e) {
				return new StreamResult(null, e);
			}
		} else {
			return new StreamResult(null, new Exception(
					"Null ConcatSource supplied."));
		}
	}

	/**
	 * Saves the concatenated PDF as a file.
	 *
	 * @param source
	 * @param filepath
	 * @return An {@link Exception}, if any occurred, otherwise
	 *         <code>null</code>.
	 */
	public static Exception pdfConcat(ConcatSource source, String filepath) {
		if (source != null) {
			try {
				ByteArrayOutputStream outPDF = new ByteArrayOutputStream();
				PdfCopyFields copy = new PdfCopyFields(outPDF);
				copy.open();
				for (InputStream inPDF : source.inPDFs) {
					copy.addDocument(new PdfReader(inPDF));
				}
				copy.close();
				PdfResult res = new PdfResult(outPDF);
				res.writeFile(filepath);
				// return new PdfConcat().new StreamResult(res, null);
				return null;
			} catch (DocumentException e) {
				// return new PdfConcat().new StreamResult(null, e);
				return e;
			} catch (IOException e) {
				// return new PdfConcat().new StreamResult(null, e);
				return e;
			}
		} else {
			return new Exception("Null ConcatSource supplied.");
		}
	}

	/**
	 * @param inputFile
	 *            A text file consisting of full file paths, one on each line.
	 *            The first file path is the output file, the rest are the input
	 *            files.
	 * @return "OK" or an error message.
	 */
	public static String pdfConcat(String inputFile) {
		try {
			String[] files = il.co.aman.apps.Misc.readFileLines(inputFile);
			if (files.length >= 2) {
				String outPDF = files[0];
				String[] inPDFs = Arrays.copyOfRange(files, 1, files.length);
				return Misc.message(pdfConcat(new ConcatSource(inPDFs), outPDF));
			} else {
				return "Invalid list file: " + inputFile
						+ " - insufficient entries.";
			}
		} catch (IOException e) {
			return Misc.message(e);
		}
	}

	/**
	 * Concatenates all of the PDF files in a folder, writing the intermediate
	 * PDFs to the output file each time. The idea is to avoid having the whole
	 * concatenated PDF in the memory at the same time.
	 *
	 * @param outfile
	 * @param folder
	 * @param numSort
	 *            If true, the filenames are sorted numerically (if possible);
	 *            otherwise alphabetically.
	 * @return An {@link Exception} if failed, <code>null</code> otherwise.<br>
	 *         If one or more of the PDFs were invalid, the output file is still
	 *         created, and the method returns {@link InvalidPdfException}.<br>
	 *         In such a case, the list of invalid PDFs is returned in
	 *         <code>getInvalid()</code>.
	 */
	public Exception pdfConcatByOne(String outfile, String folder,
			boolean numSort) {
		return pdfConcatByOne(outfile, folder, ".pdf", numSort);
	}

	/**
	 * Concatenates all of the PDF files in a folder, writing the intermediate
	 * PDFs to the output file each time. The idea is to avoid having the whole
	 * concatenated PDF in the memory at the same time.
	 *
	 * @param outfile
	 * @param folder
	 * @param extension
	 * @param numSort
	 *            If true, the filenames are sorted numerically (if possible);
	 *            otherwise alphabetically.
	 * @return An {@link Exception} if failed, <code>null</code> otherwise.<br>
	 *         If one or more of the PDFs were invalid, the output file is still
	 *         created, and the method returns {@link InvalidPdfException}.<br>
	 *         In such a case, the list of invalid PDFs is returned in
	 *         <code>getInvalid()</code>.
	 */
	public Exception pdfConcatByOne(String outfile, String folder,
			final String extension, boolean numSort) {
		this._invalid = new ArrayList<String>();
		Exception res = null;
		try {
			String[] filenames = Misc.filesByExtension(folder, extension,
					numSort);
			Misc.writeBinFile(outfile, Misc.readBinFile(filenames[0])); // initialize
																		// the
																		// destination
																		// PDF

			for (int i = 1; i < filenames.length; i++) {
				try {
					pdfConcat(new ConcatSource(new String[] { outfile,
							filenames[i] })).outPDF.writeFile(outfile);
				} catch (Exception e) {
					this._invalid.add(filenames[i]);
					res = new InvalidPdfException();
				}
			}

			return res;
		} catch (Exception e) {
			return e;
		}
	}

	public void copy2Temp(byte[] inPDF, int num) {
		addPdf(inPDF, num);
	}

	public void addPdf(byte[] inPDF, int num) {
		Misc.writeBinFile(this._tmpdir + encode3(num, 10) + ".pdf", inPDF);
	}

	public static String encode3(Integer number, int places) {
		StringBuilder res = new StringBuilder();
		res.append(number.toString());
		while (res.length() < places) {
			res.insert(0, "0");
		}
		return res.toString();
	}

	// Private methods
	private boolean _init() {
		new File(this._outpath).delete();
		File dir = new File(this._tmpdir);
		dir.mkdirs(); // OK even if it already exists
		if (!this._tmpdir.endsWith(File.separator)) {
			this._tmpdir += File.separator;
		}
		try {
			FileUtils.cleanDirectory(dir);
			this._open = true;
		} catch (IOException e) {
			this._error = e;
			this._open = false;
			return false;
		}
		return true;
	}

	/**
	 * For use with PDFs without fields.
	 *
	 * @param source
	 * @param filepath
	 * @return An {@link Exception}, if any occurred, otherwise
	 *         <code>null</code>.
	 */
	private static Exception pdfConcatSimple(ConcatSource source, String filepath) {
		try {
			ByteArrayOutputStream outPDF = new ByteArrayOutputStream();
			Document doc = new Document();
			PdfCopy copy = new PdfCopy(doc, outPDF);
			doc.open();
			// copy.open();
			for (InputStream inPDF : source.inPDFs) {
				PdfReader reader = new PdfReader(inPDF);
				for (int i = 1; i <= reader.getNumberOfPages(); i++) {
					PdfImportedPage newpage = copy.getImportedPage(reader, i);
					copy.addPage(newpage);
				}
				copy.freeReader(reader);
			}
			// copy.close();
			doc.close();
			PdfResult res = new PdfResult(outPDF);
			res.writeFile(filepath);
			normalize(filepath, false);
			return null;
		} catch (DocumentException e) {
			return e;
		} catch (IOException e) {
			return e;
		} catch (Exception e) {
			return e;
		}
	}

	/**
	 * Indicates that one or more of the PDFs is invalid
	 */
	public static class InvalidPdfException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	}

	/**
	 * Encapsulates a {@link PdfResult} object and an {@link Exception}.
	 */
	public static class StreamResult {

		public PdfResult outPDF;
		public Exception error;

		public StreamResult(PdfResult outPDF, Exception error) {
			this.outPDF = outPDF;
			this.error = error;
		}
	}

	/**
	 * Provides the source for PDF concatenation.
	 */
	public static class ConcatSource {

		private enum SourceTypes {
			STREAMS, PATHS, BINARIES, FOLDER, NONE;
		}

		private SourceTypes _srcType;
		public InputStream[] inPDFs;
		public String[] paths;
		public byte[][] binaries;
		public String folder, extension;
		public boolean numSort;
		/**
		 * If <b>any</b> error occurred in the Constructor, this will be
		 * non-null.
		 */
		public Exception error = null;
		/**
		 * Contains a means of identifying where the error, if any, occurred.
		 */
		public String error_id = null;

		public ConcatSource(InputStream[] inPDFs) {
			this._srcType = SourceTypes.STREAMS;
		}
		
		/**
		 * 
		 * @param inPDFs a list of file paths.
		 */
		public ConcatSource(java.util.ArrayList<String> inPDFs) {
			this(arrayList2Array(inPDFs));
		}
		
		private static String[] arrayList2Array(java.util.ArrayList<String> inPDFs) {			
			String[] res = new String[inPDFs.size()];
			for (int i = 0; i < inPDFs.size(); i++) {
				res[i] = inPDFs.get(i);
			}
			return res;
		}
		
		/**
		 *
		 * @param inPDFs
		 *            A {@link String} array of file paths.
		 */
		public ConcatSource(String[] inPDFs) {
			try {
				this.inPDFs = _files2Streams(inPDFs);
				this.paths = inPDFs;
				this._srcType = SourceTypes.PATHS;
			} catch (PdfConcatException e) {
				this.error = e;
				this.inPDFs = null;
				this.paths = null;
				this._srcType = SourceTypes.NONE;
			}
		}

		/**
		 *
		 * @param inPDFs
		 *            An array of <code>byte[]</code>.
		 */
		public ConcatSource(byte[][] inPDFs) {
			try {
				this.binaries = inPDFs.clone();
				this.inPDFs = new ByteArrayInputStream[inPDFs.length];
				for (int i = 0; i < inPDFs.length; i++) {
					if (inPDFs[i] == null || inPDFs[i].length <= 0) {
						this.inPDFs = null;
						throw new PdfConcatException(
								"Null PDF stream encountered.",
								Integer.toString(i));
					} else {
						this.inPDFs[i] = new ByteArrayInputStream(inPDFs[i]);
					}
				}
				this._srcType = SourceTypes.BINARIES;
			} catch (PdfConcatException e) {
				this.inPDFs = null;
				this.error = e;
				this.error_id = e.error_id;
				this._srcType = SourceTypes.NONE;
			}
		}

		/**
		 * Constructs a PDF source consisting of all the files with the
		 * extension <code>".pdf"</code> (case-insensitive) in the specified
		 * folder.
		 *
		 * @param folder
		 * @param numSort
		 *            If true, the filenames are sorted numerically (if
		 *            possible); otherwise alphabetically.
		 */
		public ConcatSource(String folder, boolean numSort) {
			this(folder, ".pdf", numSort);
		}

		/**
		 * Constructs a PDF source consisting of all the files with the
		 * specified extension (case-insensitive) in the specified folder.
		 *
		 * @param folder
		 * @param extension
		 *            The desired extension, with or without the period.
		 * @param numSort
		 *            If true, the filenames are sorted numerically (if
		 *            possible); otherwise alphabetically.
		 */
		public ConcatSource(String folder, final String extension,
				boolean numSort) {
			try {
				String[] filenames = Misc.filesByExtension(folder, extension,
						numSort);
				this.inPDFs = _files2Streams(filenames);
				this.paths = filenames;
				/*
				 * this.folder = folder; this.extension = extension;
				 */
				this._srcType = SourceTypes.FOLDER;
				this.numSort = numSort;
			} catch (PdfConcatException e) {
				this.inPDFs = null;
				this.error = e;
				this._srcType = SourceTypes.NONE;
			}
		}

		public ConcatSource clone() {
			ConcatSource res = null;

			switch (this._srcType) {
			case BINARIES:
				return new ConcatSource(this.binaries);
			case FOLDER:
				return new ConcatSource(this.paths);
			case NONE:
				break;
			case PATHS:
				return new ConcatSource(this.paths);
			case STREAMS:
				break;
			default:
				break;

			}
			return res;
		}

		private InputStream[] _files2Streams(String[] filenames)
				throws PdfConcatException {
			try {
				InputStream[] res = new InputStream[filenames.length];
				for (int i = 0; i < filenames.length; i++) {
					File file = new File(filenames[i]);
					if (file.length() <= 0) {
						throw new PdfConcatException(
								"Null PDF file encountered.", filenames[i]);
					} else {
						res[i] = new FileInputStream(file);
					}
				}
				return res;
			} catch (PdfConcatException e) {
				this.error = e;
				this.error_id = e.error_id;
				return null;
			} catch (FileNotFoundException e) {
				this.error = e;
				return null;
			}
		}
	}

	public static class PdfConcatException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String error_id;

		/**
		 *
		 * @return file path, index to array, etc., to identify where the
		 *         Exception occurred
		 */
		public String getErrorId() {
			return this.error_id;
		}

		public PdfConcatException(String msg) {
			super(msg);
			this.error_id = null;
		}

		public PdfConcatException(String msg, String error_id) {
			super(msg);
			this.error_id = error_id;
		}
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) throws IOException, Exception {
		Misc.writeBinFile(
				"C:\\Users\\davidz\\Desktop\\itext7optimized.pdf",
				normalizeFile(new File(
						"C:\\Users\\davidz\\Desktop\\shnati.pdf"), false));
		System.exit(0);
		ConcatSource src = new ConcatSource(
				"C:\\Users\\davidz\\Desktop\\concatsource", false);
		ConcatSource src_2 = src.clone();
		StreamResult res = pdfConcat(src);
		StreamResult res_2 = pdfConcat_2(src_2);
		// res.outPDF.writeFile("C:\\Users\\davidz\\Desktop\\res.pdf");
		// res_2.outPDF.writeFile("C:\\Users\\davidz\\Desktop\\res_2.pdf");
		src = new ConcatSource(
				new String[] {
						"C:\\Users\\davidz\\Desktop\\concatsource\\1008_old.pdf",
						"C:\\Users\\davidz\\Desktop\\concatsource\\2562875.pdf",
						"C:\\Users\\davidz\\Desktop\\concatsource\\dMueMDGE.pdf",
						"C:\\Users\\davidz\\Desktop\\concatsource\\subset_no normalize_from_list.pdf" });
		src_2 = src.clone();
		res = pdfConcat(src);
		//res_2 = pdfConcat_2(src_2);
		res.outPDF.writeFile("C:\\Users\\davidz\\Desktop\\res_files.pdf");
		//res_2.outPDF.writeFile("C:\\Users\\davidz\\Desktop\\res_2_files.pdf");
		System.exit(0);
		byte[][] pdfs = new byte[2][];
		pdfs[0] = Misc
				.readBinFile("C:\\Users\\davidz\\Desktop\\concatsource\\1008_old.pdf");
		pdfs[1] = Misc
				.readBinFile("C:\\Users\\davidz\\Desktop\\concatsource\\2562875.pdf");
		src = new ConcatSource(pdfs);
		src_2 = src.clone();
		res = pdfConcat(src);
		res_2 = pdfConcat(src_2);
		res.outPDF.writeFile("C:\\Users\\davidz\\Desktop\\res_bytes.pdf");
		res_2.outPDF.writeFile("C:\\Users\\davidz\\Desktop\\res_2_bytes.pdf");
		System.exit(0);

		try {
			callPdftk(
					PdfAppsParams.PDFTK_PATH,
					"C:\\Users\\davidz\\Desktop\\subset\\kalauto.pdf,C:\\Users\\davidz\\Desktop\\subset\\kalauto_1.pdf,C:\\Users\\davidz\\Desktop\\subset\\kalauto_2.pdf,C:\\Users\\davidz\\Desktop\\subset\\kalauto_11.pdf,C:\\Users\\davidz\\Desktop\\subset\\kalauto_22.pdf",
					"C:\\Users\\davidz\\Desktop\\subset_no normalize_from_list.pdf",
					new java.io.File(
							"C:\\Users\\davidz\\Desktop\\subset_no normalize_from_list.pdf")
							.getParent());
			long start = System.nanoTime();
			Misc.writeBinFile(
					"C:\\Users\\davidz\\Desktop\\small.pdf",
					normalizeFile(new File(
							"C:\\Users\\davidz\\Desktop\\subset_no normalize_from_list.pdf"), false));
			System.out.println((System.nanoTime() - start) / 1e9);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

}
