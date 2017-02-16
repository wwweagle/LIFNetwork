/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modelNetGen;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import javax.swing.JFileChooser;

/**
 *
 * @author Librizzy
 */
public class FilesCommons {

    static public String searchFor(String s) {
        String pathToFile = FilesCommons.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        try {
            pathToFile = URLDecoder.decode(pathToFile, "UTF-8");
            pathToFile = (new File(pathToFile)).getParentFile().getCanonicalPath();
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
        String[] usualDirs = {pathToFile, "I:\\My  Paper"};

//        System.out.println(pathToFile);
        for (String path : usualDirs) {
            File f = new File(path + "\\" + s);
            if (f.exists()) {
                return f.getAbsolutePath();
            }
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
