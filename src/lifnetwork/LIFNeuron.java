/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lifnetwork;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author Libra
 */
public class LIFNeuron {
    //

    final private NeuronType type;
    final private int rm;
    final private int cm;
    final private int tau;
    final private int refractoryPeriod;
    final private int reversePotential;
    final private int restPotential = -65;
    final private int threshold = -50;
    //state dependent
    private float V;
    private float currentIn = 0;
    private boolean firing = false;
    private float refractoryTime;
    private float synaticDynamics = 0;
    final private ArrayList<synapticEvent> eventList;
    final private ArrayList<IncomingSynapse> incomingList;

    /**
     *
     * @param type
     * @param r
     * @param c
     * @param tau
     * @param refractoryPeriod
     * @param reversePotential
     */
    public LIFNeuron(NeuronType type, int r, int c, int tau, int refractoryPeriod, int reversePotential) {
        this.type = type;
        this.rm = r;
        this.cm = c;
        this.tau = tau;
        this.refractoryPeriod = refractoryPeriod;
        this.reversePotential = reversePotential;

        this.eventList = new ArrayList<>();
        this.incomingList = new ArrayList<>();
        this.V = restPotential;
    }

    public void addInput(IncomingSynapse incoming) {
        this.incomingList.add(incoming);
    }

    public void addCurrent(float i) {
        this.currentIn += i;
    }

    public boolean isFiring() {
        return firing;
    }

    public void updateCurrentInput() {
        currentIn = 0;
        for (IncomingSynapse incoming : incomingList) {
            if (incoming.getSynapticDynamics() > 0) {
                /*
                 * accumulate current inputs;
                 */
                //Current(post)=Current(post)+(s(pre))*(ReversePotential(pre)-NeuronsV(post))*g(pre,post)*Weight(pre,post);
                currentIn += incoming.getSynapticDynamics() * (incoming.getReversePotential() - this.V) * incoming.getG() * incoming.getWeight();
            }
        }
    }

    public int getReversePotential() {
        return this.reversePotential;
    }

    public float getSynapticDynamics() {
        return synaticDynamics;
    }

    public float getV() {
        return V;
    }

    public float getCurrentIn() {
        return currentIn;
    }

    public void updateSynapticDynamics(int currentTime) {

        if (eventList.isEmpty()) {
            return;
        }
        float max = 0;
        boolean[] toRemove = new boolean[eventList.size()];
        for (int i = 0; i < eventList.size(); i++) {
//            System.out.print("-1,");
            float eventDynamics = eventList.get(i).getEventDynamics(currentTime);
//            System.out.print("1,");
            if (eventDynamics == -1) {
                toRemove[i] = true;
            } else if (eventDynamics > max) {
                max = eventDynamics;
            }
        }
        synaticDynamics = max;
        int removed = 0;
        for (int i = 0; i < toRemove.length; i++) {
            if (toRemove[i]) {
                eventList.remove(i - removed);
                removed++;
            }
        }
    }

    private final class synapticEvent {

        final private float scale;
        final private int riseTime = 2000;
        final private int synaticDelay = 2000;
        final private int eventTime;

        public synapticEvent(int eventTime) {
            this.eventTime = eventTime;
            scale = ThreadLocalRandom.current().nextFloat();
        }

        public float getEventDynamics(int currentTime) {

            int postAPTime = currentTime - eventTime;
            if (postAPTime <= synaticDelay) {
                return 0;
            } else if ((postAPTime <= synaticDelay + riseTime)) {
                return (float) (postAPTime - synaticDelay) / riseTime;
            } else {
                float value = (float) Math.pow(Math.E, (double) (synaticDelay + riseTime - postAPTime) / tau) * scale;
//                float value = (synaticDelay + riseTime - postAPTime) + (2 * tau);
                return value > 0.001 ? value : -1;
            }
        }
    }

    public boolean updateVoltageAndFire(int dT, int currentTime) {
        this.V += (float) dT * (currentIn - (this.V - restPotential) / (rm / 1000f))
                / this.cm / 1000f;//rm*1000,dT*1000
        firing = (this.V > this.threshold) && (refractoryTime <= 0);
        this.V = (this.V > this.threshold) ? restPotential : this.V;
        if (firing) {
            refractoryTime = refractoryPeriod;
            this.eventList.add(new synapticEvent(currentTime));
            return true;
        } else {
            refractoryTime -= refractoryTime > 0 ? dT : 0;
            return false;
        }
    }
}
