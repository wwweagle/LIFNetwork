/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lifnetwork;

import java.util.ArrayList;

/**
 *
 * @author Libra
 */
public class LIFNeuron {
    //

    final private NeuronType type;
    final private int rm;
    final private int cm;
    final private int refracConst;
    final private int reversePotential;
    final private int restPotential = -70;
    final private int threshold = -50;
    private float V;
    private float currentIn = 0;
    private int timeInFiring=Integer.MAX_VALUE;

    final private ArrayList<IncomingSynapse> incomingList;

    /**
     *
     * @param type
     * @param r
     * @param c
     * @param refractoryPeriod
     * @param reversePotential
     */
    public LIFNeuron(NeuronType type, int r, int c, int refractoryPeriod, int reversePotential) {
        this.type = type;
        this.rm = r;
        this.cm = c;
        this.refracConst = refractoryPeriod;
        this.reversePotential = reversePotential;

        this.incomingList = new ArrayList<>();
        this.V = restPotential;
    }

    public void addInput(IncomingSynapse incoming) {
        this.incomingList.add(incoming);
    }

    public void addCurrent(float i) {
        this.currentIn += i;
    }

    public void updateCurrentInput() {
        currentIn = 0;
        for (IncomingSynapse incoming : incomingList) {
//            if (incoming.getPre().getTimeInFiring() < refracConst) {
            if (incoming.getPre().getTimeInFiring() < 50000) {
                /*
                 * accumulate current inputs;
                 */
                currentIn += SynapticEvent.getEventDynamics(incoming.getPre().getType(), this.type, incoming.getPre().getTimeInFiring()) * (incoming.getReversePotential() - this.V) * incoming.getDrivingForce();
            }
        }
    }

    public int getReversePotential() {
        return this.reversePotential;
    }

    public int getTimeInFiring() {
        return timeInFiring;
    }

    public NeuronType getType() {
        return type;
    }

    public float getV() {
        return V;
    }

    public float getCurrentIn() {
        return currentIn;
    }

    public void updateTimeInFiring(int dT) {
        timeInFiring += dT;
    }

    public void triggerFire(){
        this.V=30;
    }

    public boolean updateVoltageAndFire(int dT) {
        if (timeInFiring < refracConst) {
            this.V = restPotential;
            timeInFiring += dT;
            return false;
        } else {
            this.V += (float) dT * (currentIn - (this.V - restPotential) / (rm / 1000f))
                    / this.cm / 1000f;//rm*1000,dT*1000
            if (this.V > this.threshold) {
                timeInFiring = 0;
                return true;
            } else {
                return false;
            }
        }
    }

}
