/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package commonLibs;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Libra
 */
public class WriteFile {

    static public <T> void writeMatrix(String pathToFile, T[][] mat) {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(pathToFile, true))) {
            for (T[] row : mat) {
                for (T column : row) {
                    w.write(column + "\t");
                }
                w.write("\n");
            }
            w.flush();
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    static public void writeString(String pathToFile, String s) {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(pathToFile, true))) {
            w.write(s + "\n");
            w.flush();
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    static public <T> void writeList(String pathToFile, List<T> list) {
        if (list.isEmpty()) {
            try (FileWriter f = new FileWriter(pathToFile)) {
            } catch (IOException e) {
                System.out.println(e.toString());
            }
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
        } catch (IOException e) {
            System.out.println("write csv Error:");
            System.out.println(e.toString());
        }
    }
}
