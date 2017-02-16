/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modelNetGen;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;

/**
 *
 * @author Librizzy
 */
public class FilesCommons {

    static public String getJarFolder(String s) {
        String pathToFile="";
        File f = null;
        try {
            pathToFile = FilesCommons.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            pathToFile = java.net.URLDecoder.decode((new File(pathToFile)).getCanonicalPath(),"UTF8");
            if (pathToFile.endsWith(".jar")) {
                f = new File(new File(pathToFile).getParentFile().getParent() + File.separator + s);
//                System.out.println(new File(pathToFile).getParentFile().getParent() + File.separator + s);
            } else if (pathToFile.endsWith("\\build\\classes")) {
                f = new File(pathToFile.replaceAll("build\\\\classes", s));
            }
            if (f != null && f.exists()) {
                return f.getAbsolutePath();
            }
        } catch (IOException ex) {
            Logger.getLogger(FilesCommons.class.getName()).log(Level.SEVERE, null, ex);
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
