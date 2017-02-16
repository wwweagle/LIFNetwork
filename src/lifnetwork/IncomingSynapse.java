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

    final private float drivingForce;
    final private LIFNeuron pre;
    final private int reversePotential;


    public IncomingSynapse(float drivingForce, LIFNeuron pre) {
        this.drivingForce = drivingForce;
        this.pre = pre;
        this.reversePotential = pre.getReversePotential();
    }

    public LIFNeuron getPre() {
        return pre;
    }

    public int getReversePotential() {
        return reversePotential;
    }

    public float getDrivingForce() {
        return drivingForce;
    }


}
