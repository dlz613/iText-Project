package il.co.aman.formit;

import il.co.aman.apps.Misc;
import java.io.File;
import java.io.IOException;

/**
 * Merges PDFs using ghostscript<br>
 * It is not practical for large numbers of files (~1000).
 *
 * @author davidz
 */
public class GsMerge {

    private static String createListFile(String folder, boolean numSort) throws Exception, IOException {
        String[] files = Misc.filesByExtension(folder, ".pdf", numSort);
        String tmp = File.createTempFile("gslist", ".list").getCanonicalPath();
        StringBuilder list = new StringBuilder();
        for (String file : files) {
            list.append("\"").append(file).append("\"\n");
        }
        Exception e = Misc.writeTextFile(tmp, list.toString());
        if (e != null) {
            throw e;
        }
        return tmp;
    }

    /**
     * Merges all of the PDF files in the specified folder.
     * @param gsCmd
     * @param folder
     * @param outpath
     * @param numSort If <code>true</code>, the files are processed in numerical order of the file name.
     * @return <code>null</code> on success.
     */
    public static Exception merge(String gsCmd, String folder, String outpath, boolean numSort) {
        try {
            String listFile = createListFile(folder, numSort);
            //return merge(gsCmd, "@" + listFile, outpath);
            String[] arg_array = new String[6];
            arg_array[0] = gsCmd;
            arg_array[1] = "-o";
            arg_array[2] = outpath;
            arg_array[3] = "-sDEVICE=pdfwrite";
            arg_array[4] = "-dPDFSETTINGS=/prepress";
            arg_array[5] = "@" + listFile;
            Misc.runProcess(arg_array, folder);
            return null;
        } catch (Exception e) {
            return e;
        }
    }

//    public static Exception merge(String gsCmd, String filepath, String outpath) {
//        try {
//            String[] arg_array = new String[6];
//            arg_array[0] = gsCmd;
//            arg_array[1] = "-o";
//            arg_array[2] = outpath;
//            arg_array[3] = "-sDEVICE=pdfwrite";
//            arg_array[4] = "-dPDFSETTINGS=/prepress";
//            arg_array[5] = filepath;
//            filepath = filepath.startsWith("@")? filepath.substring(1): filepath;
//            Misc.runProcess(arg_array, new File(filepath).getParent());
//            return null;
//        } catch (Exception e) {
//            return e;
//        }
//    }

    public static void main(String[] args) {
        try {
            Exception run = merge("gswin64c", "C:\\Users\\davidz\\Desktop\\nf", "C:\\Users\\davidz\\Desktop\\10000gs.pdf", false);
            if (run != null) {
                System.out.println(((Exception) run).getMessage());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
