/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lifnetwork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 *
 * @author Libra
 */
public class NetworkCalc {

    int typeOfSim = 0; //Type of connection set, 0 for pseudo-random, 1 for self-organizing
    int progressTime = 10 * 1000 * 1000; //simulation time in micro seconds (us)
    int refractFactor = 100;
    int neuronNum;
    int gabaNum;
    int gluNum;
    int gabaReverseP = -50;
    int gluReverseP = 0;
    int reversePotential = -65;
    float gFactor = 0.30f;
    int threshold = -50;
    int dT = 100;// micro seconds (us)
    float gaba_glu_g = 5 * gFactor;//conductence of connectivity from gaba cell to glu cell in same column in microS
    float gaba_gaba_g = 5 * gFactor;//conductence of connectivity from gaba cell to gaba cell in same column
    float glu_gaba_g = 2 * gFactor;//conductence of connectivity from glu cell to gaba cell in same column
    float glu_glu_g = 0.5f * gFactor;//conductence of connectivity from glu cell to glu cell in same column
    int neuronTotalNumber = 1000;
    int gluTotalNumber = 800;
    int gabaTotalNumber = 200;
    //for random current
    int randFactor = 40;//percentage
    int randCurrent = 40;
    //Random generator
    Random r = new Random();
    /*
     * g
     */
    HashMap<LIFNeuron, Float> weight;
//    HashMap<> g

    private void initNeurons() {
        /*
         * init cells
         */
        ArrayList<LIFNeuron> neurons = new ArrayList<>(1024); //GABA first, then Glu
        //calc cell number, glu number, gaba number;
        for (int i = 0; i < gabaTotalNumber; i++) {
            //init a new gaba neuron
            NeuronType type = NeuronType.GABA;
            int r = 560;
            int c = 36;
            int refractoryPeriod = 100 * 1000;
            int tau = 25 * 1000;

            LIFNeuron gabaNeuron = new LIFNeuron(type, r, c, refractoryPeriod, tau);

            neurons.add(gabaNeuron);
        }

        for (int i = 0; i < gluTotalNumber; i++) {
            //init a new gaba neuron
            NeuronType type = NeuronType.GLU;
            int r = 790;
            int c = 36;
            int refractoryPeriod = 100 * 1000;
            int tau = 4 * 1000;

            LIFNeuron gluNeuron = new LIFNeuron(type, r, c, refractoryPeriod, tau);

            neurons.add(gluNeuron);
        }
    }

    private void cycle() {
        /*
         * progress through time
         */

        for (int currentTime = 0; currentTime < progressTime; currentTime += dT) {
            /*
             * injection
             */
            /*
             * calc current here
             * in matlab:[CurrentIn,pspT]=CurrentCal2(Weight,NeuronsV,ReversePotential,g,neuronNum,dT,FiringMap,tau,Injection,gabaNum,gluNum, pspT, riseT);
             */
            /*
             * calc LIF state
             */
            /*
             * calc and record history
             */
            /*
             * status report
             */
        }

    }

    private void currentCalc(ArrayList<LIFNeuron> neurons, int currentTime) {
        // refresh the new connectivity strength
        for (LIFNeuron aNeuron : neurons) {
            aNeuron.updateTime_State(currentTime);
        }

        /*
         * apply synaptic current
         */


        /*
         * Random current
         */ {
            for (int i = 0; i < neuronTotalNumber * randFactor / 100; i++) {
                neurons.get(r.nextInt(neuronTotalNumber)).addCurrent(randCurrent);
            }
        }
    }
}
