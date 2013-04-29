/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lifnetwork;

/**
 *
 * @author Libra
 */
final public class IncomingSynapse {
    final private float weight;
    final private LIFNeuron pre;
    final private float g;
    final private int reversePotential;

    public IncomingSynapse(float weight, LIFNeuron pre, float g) {
        this.weight = weight;
        this.pre = pre;
        this.g = g;
        this.reversePotential=pre.getReversePotential();
    }


    public LIFNeuron getPre() {
        return pre;
    }
    
    public float getSynapticDynamics(){
        return pre.getSynapticDynamics();
    }
    
    public int getReversePotential(){
        return reversePotential;
    }

    public float getG() {
        return g;
    }

    public float getWeight() {
        return weight;
    }
    
}
