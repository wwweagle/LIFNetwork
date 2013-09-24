package savedNetworkParameter;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import modelNetGen.RndCell;

/**
 *
 * @author Libra
 */
public class NetworkParameters implements Serializable {
    /*
     * data structure for save
     */

    final private ArrayList<RndCell> cellList;
    final private HashMap<Integer, Float> synapticWeights;
    final private HashSet<HashSet<Integer>> clusters;

    public NetworkParameters(ArrayList<RndCell> cellList, HashMap<Integer, Float> synapticWeights, HashSet<HashSet<Integer>> clusters) {
        this.cellList = cellList;
        this.synapticWeights = synapticWeights;
        this.clusters = clusters;
    }

    public ArrayList<RndCell> getCellList() {
        return cellList;
    }

    public HashMap<Integer, Float> getSynapticWeights() {
        return synapticWeights;
    }

    public HashSet<HashSet<Integer>> getClusters() {
        return clusters;
    }
    
    
}
