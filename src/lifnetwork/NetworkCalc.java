/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lifnetwork;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import static java.util.concurrent.ForkJoinTask.invokeAll;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.ThreadLocalRandom;
import commonLibs.RndCell;
import commonLibs.NetworkParameters;
import java.util.Map.Entry;
import org.apache.commons.math3.distribution.NormalDistribution;

/**
 *
 * @author Libra
 */
public class NetworkCalc {

    private final int simulateTime; //simulation time in micro seconds (us)
    private final int refractTime = 100;
    private final int gabaReverseP;
    private final int gluReverseP = 0;
    private final int dT = 100;// micro seconds (us)
    //for random current
    private final float randPercent;//percentage
    private final int randCurrent;
    //Random generator
    private List<LIFNeuron> neuronList; //GABA first, then Glu
    //Runtime mechanisms
    final private ForkJoinPool fjpool = new ForkJoinPool();
//    final private BlockingQueue<int[]> fireQueue;
    final private List<int[]> fireList = Collections.synchronizedList(new ArrayList<int[]>());
    private RunState runState = RunState.BeforeRun;
    private final NetworkParameters save;
    private int currentTime;
    private int injectionRatio = 0;
    private int injectionCurrent = 0;
    private int seqInterval=10000;
    private int seqCounter=0;
//    private int[] lookUpTable;
//    final private AtomicInteger forkMon = new AtomicInteger();
//    private int debugCycle;

    /**
     *
     * @param simulateTime
     * @param gabaReverseP
     * @param randProb proportion of neurons with random current
     * @param randCurrent amplitude of random current
     * @param save
     */
    public NetworkCalc(int simulateTime, int gabaReverseP, float randProb, int randCurrent, NetworkParameters save) {
        this.simulateTime = simulateTime;
        this.gabaReverseP = gabaReverseP;
        this.randPercent = randProb;
        this.randCurrent = randCurrent;
        this.save = save;
//        this.fireQueue = fireQueue;
    }

    public List<int[]> getFireList() {
        return fireList;
    }

    private void readParameters() {
        neuronList = new ArrayList<>();
//        System.out.println("read in");
//        HashSet<HashSet<Integer>> clusters = save.getClusters();
//        lookUpTable = new int[save.getCellList().size()];
//        int idx = 0;
//        for (Set<Integer> s : clusters) {
//            for (Integer i : s) {
//                lookUpTable[i] = idx;
//                idx++;
//            }
//        }

        /*
         * init cells
         */
        ArrayList<RndCell> cellList = save.getCellList();
        NormalDistribution gluR = new NormalDistribution(786, 410);
        NormalDistribution gabaR = new NormalDistribution(558, 229);

        for (RndCell cell : cellList) {
            if (cell.isGlu()) {
                //init a new glu neuron
                NeuronType type = NeuronType.GLU;
                int rm = getReasonableR(gluR);
                int cm = 28440 / rm;
                int refractoryPeriod = refractTime * 1000;
                int reversePotential = gluReverseP;
                LIFNeuron gluNeuron = new LIFNeuron(type, rm, cm, refractoryPeriod, reversePotential);

                neuronList.add(gluNeuron);
            } else {
                //init a new gaba neuron
                NeuronType type = NeuronType.GABA;
                int rm = getReasonableR(gabaR);
                int cm = 20160 / rm;
                int refractoryPeriod = refractTime * 1000;
                int reversePotential = gabaReverseP;

                LIFNeuron gabaNeuron = new LIFNeuron(type, rm, cm, refractoryPeriod, reversePotential);

                neuronList.add(gabaNeuron);
            }
        }
        neuronList = Collections.unmodifiableList(neuronList);
        /*
         * init synapses
         */
        Set<Entry<Integer, Float>> drivingForces = save.getDrivingForces().entrySet();

        for (Entry<Integer, Float> synapse : drivingForces) {
            int pre = synapse.getKey() >>> 12;
            int post = synapse.getKey() & 4095;
            IncomingSynapse incoming = new IncomingSynapse(
                    synapse.getValue(),
                    neuronList.get(pre));
            neuronList.get(post).addInput(incoming);
        }
    }

    public void setInjectionRatio(int injectionRatio) {
        this.injectionRatio = injectionRatio;
    }

    public void setInjectionCurrent(int injectionCurrent) {
        this.injectionCurrent = injectionCurrent;
    }

    private int getReasonableR(NormalDistribution d) {
        int r;
        do {
            r = (int) Math.round(d.sample());
        } while (r > 1200 || r < 250);
        return r;
    }

    public int cycle() {

        runState = RunState.Running;

        readParameters();
        /*
         * progress through time
         */
//        fireList = Collections.synchronizedList(new ArrayList<int[]>());
//        ArrayList<Float> vSample = new ArrayList<>(10000);
//        ArrayList<Float> iSample = new ArrayList<>(10000);
//        ArrayList<Float> sSample = new ArrayList<>(10000);
//        int step = simulateTime / 10;
        for (currentTime = 0; currentTime < simulateTime; currentTime += dT) {
            if (runState == RunState.Stop) {
                return fireList.size();
            }

            /*
             * calc current here
             */
            fjpool.invoke(new CurrentCalcFork(0, neuronList.size(), false));

            /*
             * injection
             */
            
//            if (currentTime % step < 2000 && injectionRatio > 0 && injectionCurrent > 0) {
//                for (int i = 0; i < neuronList.size() * injectionRatio / 100; i++) {
//                    neuronList.get(i).addCurrent(injectionCurrent);
//                }
//            }
            
            /*
            Seq
            */
            
            if(seqInterval>0 && currentTime%seqInterval==0){
                neuronList.get(seqCounter%neuronList.size()).triggerFire();
                seqCounter++;
            }
            
            /*
             * calc LIF state
             */
            List<Integer> fired = Collections.synchronizedList(new ArrayList<Integer>());
            fjpool.invoke(new VoltageCalcFork(fired, 0, neuronList.size(), dT));
            
            
            synchronized (fired) {
                for (Integer cell : fired) {
//                    int[] record = {currentTime, lookUpTable[cell]};
                    int[] record = {currentTime, cell};
//                    try {
//                        fireQueue.put(record);
                    fireList.add(record);
//                    } catch (InterruptedException ex) {
//                        System.out.println(ex.toString());
//                    }
                }
            }
            /*
             * calc and record history
             */
//            vSample.add(neuronList.get(63).getV());
//            iSample.add(neuronList.get(63).getCurrentIn());
//            sSample.add(neuronList.get(217).getSynapticDynamics());

            /*
             * status report
             */
//            statusReport(currentTime);
//            int newTime = currentTime + dT;
//            currentTime = newTime;
//            currentTime += dT;
        }

//        Commons.writeList(save.getType() == ModelType.Network ? "Net" : "Ctl"
//                + "_C" + save.getConnProb() + "_W" + save.getWeightScale()
//                + save.getHashString() + "_fireHistory.csv", fireList);
//        Commons.writeList("vHistory.csv", vSample);
//        Commons.writeList("iHistory.csv", iSample);
//        Commons.writeList("sHistory.csv", sSample);
//        getMaxFirePopulation(fireList);
        runState = RunState.Stop;
        return fireList.size();
    }

    public boolean isStopped() {
        return runState == RunState.Stop;
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

    final private class CurrentCalcFork extends RecursiveAction {

        final private int start;
        final private int end;
        final private boolean rand;

        public CurrentCalcFork(int start, int end, boolean rand) {
            this.start = start;
            this.end = end;
            this.rand = rand;
        }

        private void currentCalc(int start, int end) {
            for (int i = start; i < end; i++) {

                neuronList.get(i).updateCurrentInput();
                /*
                 * Random current
                 */

                if (randPercent > 0 && randCurrent > 0 && ThreadLocalRandom.current().nextFloat() * 100 < randPercent) {
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
            invokeAll(new CurrentCalcFork(start, middle, rand),
                    new CurrentCalcFork(middle, end, rand));

        }
    }

    final private class VoltageCalcFork extends RecursiveAction {

        final private List fired;
        final private int start;
        final private int end;
        final private int dT;

        public VoltageCalcFork(List fired, int start, int end, int dT) {
            this.start = start;
            this.end = end;
            this.fired = fired;
            this.dT = dT;
        }

        private void voltageCalc(int start, int end, int dT) {
            for (int i = start; i < end; i++) {
                
                if (neuronList.get(i).updateVoltageAndFire(dT)) {
                    fired.add(i);
                }
            }
        }

        @Override
        protected void compute() {
            if ((end - start) <= 250) {
                voltageCalc(start, end, dT);
                return;
            }
            int middle = (end - start) / 2 + start;
            invokeAll(new VoltageCalcFork(fired, start, middle, dT),
                    new VoltageCalcFork(fired, middle, end, dT));

        }
    }

    private enum RunState {

        BeforeRun, Running, Stop, Finished, Paused
    }

}
