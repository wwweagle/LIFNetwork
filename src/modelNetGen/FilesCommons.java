/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modelNetGen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JFileChooser;

/**
 *
 * @author Librizzy
 */
public class FilesCommons {

    static public String getDefaultFile() {
        String pathToFile = "C:\\Users\\Libra\\Desktop\\GAD0.accdb";
        File f = new File(pathToFile);
        if (f.exists()) {
            return pathToFile;
        }

        pathToFile = "C:\\Users\\Librizzy\\Desktop\\CircuitData\\GAD0.accdb";
        f = new File(pathToFile);
        if (f.exists()) {
            return pathToFile;
        }

        javax.swing.JFileChooser DataFileChooser = new javax.swing.JFileChooser();

        int returnVal = DataFileChooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            pathToFile = DataFileChooser.getSelectedFile().getAbsolutePath();
        }
        return pathToFile;
    }

    static public void writeMatrix(String pathToFile, double[][] mat) {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(pathToFile))) {
            for (int i = 0; i < mat.length; i++) {
                for (int j = 0; j < mat[i].length - 1; j++) {
                    w.write(mat[i][j] + ",");
                }
                w.write(mat[i][mat[i].length - 1] + "\n");
            }
            w.flush();
        } catch (IOException e) {
        }
    }

    static public void writeMatrix(String pathToFile, Object[][] mat) {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(pathToFile))) {
            for (int i = 0; i < mat.length; i++) {
                for (int j = 0; j < mat[i].length - 1; j++) {
                    w.write(mat[i][j].toString() + ",");
                }
                w.write(mat[i][mat[i].length - 1] + "\n");
            }
            w.flush();
        } catch (IOException e) {
        }
    }

    static public void writeMatrix(String pathToFile, float[][] mat) {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(pathToFile))) {
            for (int i = 0; i < mat.length; i++) {
                for (int j = 0; j < mat[i].length - 1; j++) {
                    w.write(mat[i][j] + ",");
                }
                w.write(mat[i][mat[i].length - 1] + "\n");
            }
            w.flush();
        } catch (IOException e) {
        }
    }
}
