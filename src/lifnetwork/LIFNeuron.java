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
    final private int r;
    final private int c;
    final private int tau;
    final private int refractoryPeriod;
    final private int reversePotential;
    final private int restPotential;
    //state dependent
    private float V;
    private float currentIn = 0;
    private boolean firing = false;
    private float refractoryTime;
    private float synaticDynamics = 0;
    private ArrayList<synapticEvent> eventList;
    private ArrayList<IncomingSynapse> incomingList;

    /**
     *
     * @param type
     * @param r
     * @param c
     * @param restPotential
     * @param refractoryPeriod
     * @param tau
     */
  
    public LIFNeuron(NeuronType type, int r, int c, int tau, int refractoryPeriod, int reversePotential, int restPotential) {
        this.type = type;
        this.r = r;
        this.c = c;
        this.tau = tau;
        this.refractoryPeriod = refractoryPeriod;
        this.reversePotential = reversePotential;
        this.restPotential = restPotential;
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

    public void updateSynapticDynamics(int currentTime) {
        for (synapticEvent aEvent : eventList) {
            float eventDynamics = aEvent.getEventDynamics(currentTime);
            if (eventDynamics < 0) {
                eventList.remove(aEvent);
            } else if (eventDynamics > synaticDynamics) {
                synaticDynamics = eventDynamics;
            }
        }
    }

    private final class synapticEvent {

        final private int riseTime = 2000;
        final private int synaticDelay = 2000;
        final private int eventTime;

        public synapticEvent(int eventTime) {
            this.eventTime = eventTime;
        }

        public float getEventDynamics(int currentTime) {
            int postAPTime = currentTime - eventTime;
            float eventDynamics = (postAPTime <= synaticDelay)
                    ? 0
                    : (postAPTime <= synaticDelay + riseTime)
                    ? (float) (postAPTime - synaticDelay) / riseTime
                    : (postAPTime <= 100 * 1000)
                    ? (float) Math.pow(Math.E, (double) (synaticDelay + riseTime - postAPTime) / tau)
                    : -1;
            return eventDynamics;
        }
    }
}
