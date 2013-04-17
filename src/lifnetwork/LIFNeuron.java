/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lifnetwork;

/**
 *
 * @author Libra
 */
public class LIFNeuron {
    //
    final private NeuronType type;
    final private int r;
    final private int c;
    final private int refractoryPeriod;
    final private int tau;
    //state dependent
    private float prV;
    private float currentIn;
    private int pspTime;
    private boolean firing;
    private float refractoryTime;
    
    /**
     * 
     * @param type
     * @param r
     * @param c
     * @param restPotential
     * @param refractoryPeriod
     * @param tau 
     */

    public LIFNeuron(NeuronType type, int r, int c, int refractoryPeriod, int tau) {
        this.type = type;
        this.r = r;
        this.c = c;
        this.refractoryPeriod = refractoryPeriod;
        this.tau = tau;
    }
    

}
