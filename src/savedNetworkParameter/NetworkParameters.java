package savedNetworkParameter;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Libra
 */
public class NetworkParameters implements Serializable {
    /*
     * data structure for save
     */

    final private ArrayList<Boolean> neuronIsGlu;
    final private HashMap<Integer, Float> synapticWeights;
//    final private ArrayList<int[]> neuronCoord;

    public NetworkParameters(ArrayList<Boolean> neuronIsGlu, HashMap<Integer, Float> synapticWeights) {
        this.neuronIsGlu = neuronIsGlu;
        this.synapticWeights = synapticWeights;
//        this.neuronCoord = neuronCoord;
    }

    public ArrayList<Boolean> getNeuronIsGlu() {
        return neuronIsGlu;
    }

    public HashMap<Integer, Float> getSynapticWeights() {
        return synapticWeights;
    }
//
//    public ArrayList<int[]> getNeuronCoord() {
//        return neuronCoord;
//    }
    
}
