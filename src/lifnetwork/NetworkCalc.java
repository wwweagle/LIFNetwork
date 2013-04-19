/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lifnetwork;

import java.util.ArrayList;
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
    float gFactor = 0.30f;
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
    ArrayList<LIFNeuron> neuronList = new ArrayList<>(1024); //GABA first, then Glu

    private void initNeurons() {
        /*
         * init cells
         */

        //calc cell number, glu number, gaba number;
        for (int i = 0; i < gabaTotalNumber; i++) {
            //init a new gaba neuron
            NeuronType type = NeuronType.GABA;
            int rm = 560;
            int cm = 36;
            int refractoryPeriod = 100 * 1000;
            int tau = 25 * 1000;
            int reversePotential = gabaReverseP;

            LIFNeuron gabaNeuron = new LIFNeuron(type, rm, cm, tau, refractoryPeriod, reversePotential);

            neuronList.add(gabaNeuron);
        }

        for (int i = 0; i < gluTotalNumber; i++) {
            //init a new gaba neuron
            NeuronType type = NeuronType.GLU;
            int rm = 790;
            int cm = 36;
            int refractoryPeriod = 100 * 1000;
            int tau = 4 * 1000;
            int reversePotential = gluReverseP;

            LIFNeuron gluNeuron = new LIFNeuron(type, rm, cm, tau, refractoryPeriod, reversePotential);

            neuronList.add(gluNeuron);
        }
    }

    private void initSynapses() {
        
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
             */
            currentCalc(currentTime);
            /*
             * calc LIF state
             */
            voltageCalc(dT);
            /*
             * calc and record history
             */
            
            /*
             * status report
             */
        }

    }

    private void currentCalc(int currentTime) {
        /*
         * refresh the new connectivity strength
         */
        for (LIFNeuron aNeuron : neuronList) {
            aNeuron.updateSynapticDynamics(currentTime);
        }

        /*
         * apply synaptic current
         */
        for (LIFNeuron aNeuron : neuronList) {
            aNeuron.updateCurrentInput();
        }

        /*
         * Random current
         */ {
            for (int i = 0; i < neuronTotalNumber * randFactor / 100; i++) {
                neuronList.get(r.nextInt(neuronTotalNumber)).addCurrent(randCurrent);
            }
        }
    }

    private void voltageCalc(int dT) {
        for (LIFNeuron neuron : neuronList) {
            neuron.updateVoltage(dT);
        }
    }
}
