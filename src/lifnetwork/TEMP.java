/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lifnetwork;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import savedParameters.NetworkParameters;

/**
 *
 * @author Libra
 */
public class TEMP {

    /*
     * TEMP deserialize test
     */
    public static void test() {
        NetworkParameters save;
        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream("conn_Net_C_1.0_W_1.0.ser"))) {
            save = (NetworkParameters) in.readObject();
            System.out.println("deserialize succeed");
            System.out.println(save.getNeuronIsGlu().size());
            System.out.println(save.getSynapticWeights().size());

        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.toString());
            System.out.println("deserialize failed");
        }
    }
}
