/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lifnetwork;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Libra
 */
public class Commons {

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

    static public void writeList(String pathToFile, ArrayList<int[]> list) {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(pathToFile))) {
            for (int i = 0; i < list.size(); i++) {
                w.write(list.get(i)[0]+","+list.get(i)[1]+"\n");
            }
            w.flush();
        } catch (IOException e) {
        }
    }
}
