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
    final private int riseTime = 2000;
    final private int synaticDelay=2000;
    //state dependent
    private float prV;
    private float currentIn = 0;
    private int postAPTime;
    private boolean firing = false;
    private float refractoryTime;
    private float synaticDynamics = 0;

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
        postAPTime = 500 * 1000;
    }

    public void addCurrent(float i) {
        this.currentIn += i;
    }

    public boolean isFiring() {
        return firing;
    }

    public void updateTimeState(int dT) {
        if (firing) {
            postAPTime = 0;
        } else {
            postAPTime += dT;
        }

        //rising phase

        synaticDynamics = (postAPTime <= riseTime)
                ? (float) postAPTime / riseTime
                : (postAPTime > 100 * 1000)
                ? 0
                : (float) Math.pow(Math.E, (double) (riseTime - postAPTime) / tau);
    }
    
//    public
}
