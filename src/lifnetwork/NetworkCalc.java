/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lifnetwork;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import static java.util.concurrent.ForkJoinTask.invokeAll;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.ThreadLocalRandom;
import savedParameters.NetworkParameters;

/**
 *
 * @author Libra
 */
public class NetworkCalc {

    private final int simulateTime; //simulation time in micro seconds (us)
    private final int refractTime = 100;
    private final int gabaReverseP;
    private final int gluReverseP = 0;
    private final float gFactor = 0.30f;
    private final int dT = 100;// micro seconds (us)
    //for random current
    private final int randFactor = 40;//percentage
    private final int randCurrent = 40;
    //Random generator
    private List<LIFNeuron> neuronList = new ArrayList<>(1024); //GABA first, then Glu
    //Runtime mechanisms
    private ForkJoinPool fjpool = new ForkJoinPool();
    private RunState runState = RunState.BeforeRun;
    private final String pathToFile;
    private int currentTime;

    /**
     *
     * @param progressTime
     * @param gabaReverseP
     * @param pathToFile
     */
    public NetworkCalc(int progressTime, int gabaReverseP, String pathToFile) {
        this.simulateTime = progressTime;
        this.gabaReverseP = gabaReverseP;
        this.pathToFile = pathToFile;
    }

    private void readParameters() {

        /*
         * read data from file
         */

        NetworkParameters save = null;
        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream(pathToFile))) {
            save = (NetworkParameters) in.readObject();
//            System.out.println("deserialize succeed");
//            System.out.println(save.getNeuronIsGlu().size());
//            System.out.println(save.getSynapticWeights().size());

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
                int refractoryPeriod = refractTime * 1000;
                int tau = 4 * 1000;
                int reversePotential = gluReverseP;
                LIFNeuron gluNeuron = new LIFNeuron(type, rm, cm, tau, refractoryPeriod, reversePotential);

                neuronList.add(gluNeuron);
            } else {
                //init a new gaba neuron
                NeuronType type = NeuronType.GABA;
                int rm = 560;
                int cm = 36;
                int refractoryPeriod = refractTime * 1000;
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

    public void cycle() {

        runState = RunState.Running;

        readParameters();
        /*
         * progress through time
         */
        ArrayList<int[]> fireList = new ArrayList<>(1000);
//        ArrayList<Float> vSample = new ArrayList<>(10000);
//        ArrayList<Float> iSample = new ArrayList<>(10000);
//        ArrayList<Float> sSample = new ArrayList<>(10000);

        for (currentTime = 0; currentTime < simulateTime;) {

            if (runState == RunState.Stop) {
                return;
            }
            /*
             * injection
             */
            /*
             * calc current here
             */
            fjpool.invoke(new SynapticEventCalcFork(0, neuronList.size(), currentTime));
            fjpool.invoke(new CurrentCalcFork(0, neuronList.size()));

            /*
             * calc LIF state
             */
            List<Integer> fired = Collections.synchronizedList(new ArrayList<Integer>());
            fjpool.invoke(new VoltageCalcFork(fired, 0, neuronList.size(), dT, currentTime));
            synchronized (fired) {
                for (Integer cell : fired) {
                    int[] record = {currentTime, cell};
                    fireList.add(record);
                }
            }
            /*
             * calc and record history
             */
//            vSample.add(neuronList.get(63).getV());
//            iSample.add(neuronList.get(63).getCurrentIn());
//            sSample.add(neuronList.get(63).getSynapticDynamics());

            /*
             * status report
             */
//            statusReport(currentTime);

            int newTime = currentTime + dT;
            currentTime = newTime;
        }

        System.out.println(fireList.size());
//        Commons.writeList("vHistory.csv", vSample);
//        Commons.writeList("iHistory.csv", iSample);
//        Commons.writeList("sHistory.csv", sSample);
        Commons.writeList("fireHistory.csv", fireList);
    }

    public int getProgress() {
        return currentTime * 100 / simulateTime;
    }

    public void stopCycle() {
        runState = RunState.Stop;
    }

    public boolean isRunning() {
        return runState == RunState.Running;
    }

    private void statusReport(int currentTime) {
        if (currentTime % (simulateTime / 10) == 0) {
            System.out.println(currentTime * 100 / simulateTime + "%");
        }
    }

    final private class SynapticEventCalcFork extends RecursiveAction {

        final private int start;
        final private int end;
        final private int currentTime;

        public SynapticEventCalcFork(int start, int end, int currentTime) {
            this.start = start;
            this.end = end;
            this.currentTime = currentTime;
        }

        private void SynEvtCalc(int start, int end, int currentTime) {
            for (int i = start; i < end; i++) {
                neuronList.get(i).updateSynapticDynamics(currentTime);
            }
        }

        @Override
        protected void compute() {
            if ((end - start) <= 250) {
                SynEvtCalc(start, end, currentTime);
                return;
            }
            int middle = (end - start) / 2 + start;
            invokeAll(new SynapticEventCalcFork(start, middle, currentTime),
                    new SynapticEventCalcFork(middle, end, currentTime));

        }
    }

    final private class CurrentCalcFork extends RecursiveAction {

        final private int start;
        final private int end;

        public CurrentCalcFork(int start, int end) {
            this.start = start;
            this.end = end;
        }

        private void currentCalc(int start, int end) {
            for (int i = start; i < end; i++) {
                neuronList.get(i).updateCurrentInput();
                /*
                 * Random current
                 */

                if (ThreadLocalRandom.current().nextInt(100) < randFactor) {
                    neuronList.get(i).addCurrent(randCurrent);
                }
            }
        }

        @Override
        protected void compute() {
            if ((end - start) <= 250) {
                currentCalc(start, end);
                return;
            }
            int middle = (end - start) / 2 + start;
            invokeAll(new CurrentCalcFork(start, middle),
                    new CurrentCalcFork(middle, end));

        }
    }

    final private class VoltageCalcFork extends RecursiveAction {

        final private List fired;
        final private int start;
        final private int end;
        final private int dT;
        final private int currentTime;

        public VoltageCalcFork(List fired, int start, int end, int dT, int currentTime) {
            this.start = start;
            this.end = end;
            this.fired = fired;
            this.dT = dT;
            this.currentTime = currentTime;
        }

        private void voltageCalc(int start, int end, int dT, int currentTime) {
            for (int i = start; i < end; i++) {
                if (neuronList.get(i).updateVoltageAndFire(dT, currentTime)) {
                    fired.add(i);
                }
            }
        }

        @Override
        protected void compute() {
            if ((end - start) <= 250) {
                voltageCalc(start, end, dT, currentTime);
                return;
            }
            int middle = (end - start) / 2 + start;
            invokeAll(new VoltageCalcFork(fired, start, middle, dT, currentTime),
                    new VoltageCalcFork(fired, middle, end, dT, currentTime));

        }
    }

    private enum RunState {

        BeforeRun, Running, Stop, Finished
    }
}
