/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lifnetwork;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

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

    static public <T> void writeList(String pathToFile, List<T> list) {
        if (list.isEmpty()) {
            try (FileWriter f=new FileWriter(pathToFile)) {
                }catch (IOException e) {
            };
        }

        try (BufferedWriter w = new BufferedWriter(new FileWriter(pathToFile))) {
            if (list.get(1) instanceof int[]) {
                for (int i = 0; i < list.size(); i++) {
                    int[] array = (int[]) list.get(i);
                    for (int j = 0; j < array.length - 1; j++) {
                        w.write(array[j] + ",");
                    }
                    w.write(array[array.length - 1] + "\n");
                }
            } else if (list.get(1) instanceof Float) {
                for (int i = 0; i < list.size(); i++) {
                    float value = (Float) list.get(i);
                    w.write(value + "\n");
                }
            }
            w.flush();
        } catch (IOException e) {
            System.out.println("write csv Error:");
            System.out.println(e.toString());
        }
    }
}
