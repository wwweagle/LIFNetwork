/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modelNetGen;

import java.io.File;
import java.net.URLDecoder;
import javax.swing.JFileChooser;

/**
 *
 * @author Librizzy
 */
public class FilesCommons {

    static public String getJarFolder(String s) {
        String pathToFile = FilesCommons.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        try {
            pathToFile = URLDecoder.decode(pathToFile, "UTF-8");
            pathToFile = (new File(pathToFile)).getParentFile().getCanonicalPath() + "\\" + s;
        } catch (Throwable ex) {
            System.out.println(ex.toString());
        }

//        System.out.println(pathToFile);

        File f = new File(pathToFile);
        if (f.exists()) {
            return f.getAbsolutePath();
        }

        javax.swing.JFileChooser DataFileChooser = new javax.swing.JFileChooser();

        int returnVal = DataFileChooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            pathToFile = DataFileChooser.getSelectedFile().getAbsolutePath();
        }
//        System.out.println(pathToFile);
        return pathToFile;
    }
}
