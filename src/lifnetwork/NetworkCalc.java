/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lifnetwork;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import savedParameters.NetworkParameters;

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
    //for random current
    int randFactor = 40;//percentage
    int randCurrent = 40;
    //Random generator
    Random r = new Random();
    ArrayList<LIFNeuron> neuronList = new ArrayList<>(1024); //GABA first, then Glu

    private void initNeurons() {
        /*
         * read data from file
         */

        NetworkParameters save = null;
        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream("conn_Net_C_1.0_W_1.0.ser"))) {
            save = (NetworkParameters) in.readObject();
            System.out.println("deserialize succeed");
            System.out.println(save.getNeuronIsGlu().size());
            System.out.println(save.getSynapticWeights().size());

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("deserialize failed");
        }


        /*
         * init cells
         */
        ArrayList<Boolean> neuronIsGlu = save.getNeuronIsGlu();

        for (int i = 0; i < neuronIsGlu.size(); i++) {
            if (neuronIsGlu.get(i)) {
                //init a new glu neuron
                NeuronType type = NeuronType.GLU;
                int rm = 790;
                int cm = 36;
                int refractoryPeriod = 100 * 1000;
                int tau = 4 * 1000;
                int reversePotential = gluReverseP;
                LIFNeuron gluNeuron = new LIFNeuron(type, rm, cm, tau, refractoryPeriod, reversePotential);

                neuronList.add(gluNeuron);
            } else {
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
        }
        /*
         * init synapses
         */
        HashMap<Integer, Float> synapticWeights = save.getSynapticWeights();
        Set<Map.Entry<Integer, Float>> synapses = synapticWeights.entrySet();
        for (Map.Entry<Integer, Float> synapse : synapses) {
            int pre = synapse.getKey() >>> 12;
            int post = synapse.getKey() & 4095;
            IncomingSynapse incoming = new IncomingSynapse(
                    synapse.getValue(),
                    neuronList.get(pre),
                    getG(neuronIsGlu.get(pre), neuronIsGlu.get(post)));
            neuronList.get(post).addInput(incoming);
        }
    }

    private float getG(boolean preIsGlu, boolean postIsGlu) {
//        float gaba_glu_g = 5 * gFactor;//conductence of connectivity from gaba cell to glu cell in same column in microS
//        float gaba_gaba_g = 5 * gFactor;//conductence of connectivity from gaba cell to gaba cell in same column
//        float glu_gaba_g = 2 * gFactor;//conductence of connectivity from glu cell to gaba cell in same column
//        float glu_glu_g = 0.5f * gFactor;//conductence of connectivity from glu cell to glu cell in same column
        int key = (preIsGlu ? 0 : 2) + (postIsGlu ? 0 : 1);
        switch (key) {
            case 0:
                return 0.5f * gFactor;
            case 1:
                return 2 * gFactor;
            case 2:
                return 5 * gFactor;
            case 3:
                return 5 * gFactor;
            default:
                System.out.println("Unknown synapse combination during calculaing g");
                return 1;
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
         */
//            for (int i = 0; i < neuronTotalNumber * randFactor / 100; i++) {
//                neuronList.get(r.nextInt(neuronTotalNumber)).addCurrent(randCurrent);
//            }

    }

    private void voltageCalc(int dT) {
        for (LIFNeuron neuron : neuronList) {
            neuron.updateVoltage(dT);
        }
    }
}
