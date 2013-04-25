/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lifnetwork;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
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

    private void readParameters() {
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
        if (save == null) {
            System.out.println("Null save file");
            return;
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
        /*
         * temp test statics
         */
        int[] in = new int[1000];
        int[] out = new int[1000];

        for (Map.Entry<Integer, Float> synapse : synapses) {
            int pre = synapse.getKey() >>> 12;
            int post = synapse.getKey() & 4095;
            in[post]++;
            out[pre]++;
        }
        int[] statics = new int[50];
        for (int i = 0; i < in.length; i++) {
            statics[in[i]]++;
        }
        System.out.println("in");
        System.out.println(Arrays.toString(statics));
        statics = new int[50];
        for (int i = 0; i < out.length; i++) {
            statics[out[i]]++;
        }
        System.out.println("out");
        System.out.println(Arrays.toString(statics));


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

    public void cycle() {
        readParameters();
        /*
         * progress through time
         */
        ArrayList<int[]> fireList = new ArrayList<>(1000);
        ArrayList<Float> vSample = new ArrayList<>(10000);
        ArrayList<Float> iSample = new ArrayList<>(10000);
        ArrayList<Float> sSample = new ArrayList<>(10000);

        for (int currentTime = 0; currentTime < progressTime; currentTime += dT) {
//            System.out.println("\ncurrentTime " + currentTime);
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
            ArrayList<Integer> fired = voltageCalc(dT, currentTime);
            for (Integer cell : fired) {
                int time = currentTime;
                int[] record = {time, cell};
                fireList.add(record);
            }
            /*
             * calc and record history
             */
            vSample.add(neuronList.get(63).getV());
            iSample.add(neuronList.get(63).getCurrentIn());
            sSample.add(neuronList.get(63).getSynapticDynamics());

            /*
             * status report
             */
            statusReport(currentTime);
        }

        System.out.println(fireList.size());
        Commons.writeList("vHistory.csv", vSample);
        Commons.writeList("iHistory.csv", iSample);
        Commons.writeList("sHistory.csv", sSample);
        Commons.writeList("fireHistory.csv", fireList);
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
        int toApply = neuronList.size() * randFactor / 100;
        for (int notApplied = 1000; toApply > 0; notApplied--) {
            if (r.nextDouble() < ((double) toApply / notApplied)) {
                toApply--;
                neuronList.get(notApplied-1).addCurrent(randCurrent);
            } else {
            }
        }

    }

    private ArrayList<Integer> voltageCalc(int dT, int currentTime) {
        ArrayList<Integer> fireList = new ArrayList<>();
        for (int i = 0; i < neuronList.size(); i++) {
            if (neuronList.get(i).updateVoltageAndFire(dT, currentTime)) {
//                System.out.print(i+",");
                fireList.add(i);
            }
        }
        return fireList;
    }

    private void statusReport(int currentTime) {
        if (currentTime % (progressTime / 100) == 0) {
            System.out.println(currentTime * 100 / progressTime + "%");
        }
    }
}
