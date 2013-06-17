/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modelNetGen;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicIntegerArray;
import javax.swing.Timer;
import org.apache.commons.math3.random.RandomGenerator;
import savedParameters.NetworkParameters;

/**
 *
 * @author Libra
 */
public class ModelNewN {

    final private RandomGenerator r;
    final private ArrayList<RndCell> cellList;
    final private Set<Integer> connected;
    private AtomicIntegerArray gluIn;
    private AtomicIntegerArray gluOut;
    private AtomicIntegerArray gabaIn;
    private AtomicIntegerArray gabaOut;
    private boolean DEPOLAR_GABA;
    final private float ITERATE_FACTOR;
    final private float GLU_IO_COE;
    final private float GABA_IO_COE;
    private int dim;
    private RunState runState;
    final private List<String> updates;
    private int progress;
    private ModelType TYPE;
//    private int genMonitorTime = 20;
    private float connProbScale;
    private boolean writeFile;
    private float weightScale;
    final private Monitor allPair;

    /**
     * Build a new iterate model
     *
     * @param type Type of Model (random-dist, bi-dir, uni-dir)
     * @param depolarGABA True if GABA is depolarizing.
     * @param gluE Glutamate IO coefficient
     * @param gabaE GABA IO coefficient
     * @param iterateFactor Expected average iterate cycle
     */
    ModelNewN(float gluE, float gabaE, float iterateFactor, int nCell, int density, float gluRate) {
        r = Com.getR();
        this.GLU_IO_COE = gluE;
        this.GABA_IO_COE = gabaE;
        this.ITERATE_FACTOR = iterateFactor;
        runState = RunState.Instantiated;
        updates = new LinkedList<>();
        progress = 0;
        connected = Collections.newSetFromMap(new ConcurrentHashMap<Integer, Boolean>(30000));
        cellList = new ArrayList<>();
        dim = getDimension(nCell, density);
        for (int i = 0; i < nCell; i++) {
            RndCell newCell = new RndCell(dim, gluRate);
            cellList.add(newCell);
        }
        allPair = genPairMonitor();
        if (cellList.size() < 3) {
            progressUpdate("Empty Cell List");
        }
        runState = RunState.ReadyGenCells;
    }

    public void setType(ModelType type) {
        this.TYPE = type;
    }

    public void setDEPOLAR_GABA(boolean DEPOLAR_GABA) {
        this.DEPOLAR_GABA = DEPOLAR_GABA;
    }

    private int getDimension(int nCell, int density) {
        float area = (float) nCell / density;
        float d = (float) Math.sqrt(area);
        d = d * 10000;//cm to micro-m;
        return Math.round(d);
    }

    private void setProgress(int currProgress, int maxProgress) {
        if (currProgress >= 0 && maxProgress >= currProgress) {
            this.progress = currProgress * 100 / maxProgress;
        }
    }

    public int getProgress() {
        return progress;
    }

    public void setConnProbScale(float connProbScale) {
        this.connProbScale = connProbScale;
    }

    public void setWriteFile(boolean writeFile) {
        this.writeFile = writeFile;
    }

    public void setWeightScale(float weightScale) {
        this.weightScale = weightScale;
    }

    private int countCluster(int type, int id1, int id2) {
        int setKey = Com.getSetKey(id1, id2);
        if (!connected.contains(setKey)) {
            return 0;
        }
        switch (type) {
            case 0:
                return 1;
            case 1:
                return cellList.get(id1).isGlu() ? 1 : 0;
            case 2:
                return cellList.get(id1).isGlu() ? 0 : 1;
            default:
                return 0;
        }

    }

    private Set<int[]> genGrpMonitor(int size, int timeInS) {
        int threads = Runtime.getRuntime().availableProcessors();
        ExecutorService es = Executors.newFixedThreadPool(threads);
        Set<int[]> monitor = Collections.newSetFromMap(new ConcurrentHashMap<int[], Boolean>());
        Set<Long> had = Collections.newSetFromMap(new ConcurrentHashMap<Long, Boolean>());
        CountDownLatch cdl = new CountDownLatch(threads);
        genGrpsClass[] genGrps = new genGrpsClass[threads];
        progressUpdate("build up monitor in " + timeInS + " seconds");

        for (int i = 0; i < threads; i++) {
            genGrps[i] = new genGrpsClass(size, monitor, had, cdl);
            es.execute(genGrps[i]);
            genGrps[i].startTimer(timeInS);
        }

        try {
            cdl.await();
        } catch (InterruptedException e) {
            System.out.println(e.toString());
        }
        progressUpdate("Stop Signal Sent");
        progressUpdate("monitor size:" + monitor.size());
        return monitor;
    }

    private Monitor genPairMonitor() {
        int threads = Runtime.getRuntime().availableProcessors();
        int threadLength = cellList.size() / threads;
        ExecutorService es = Executors.newFixedThreadPool(threads);

        class genPairClass implements Callable<Monitor> {

            final int start;
            final int end;

            public genPairClass(int start, int end) {
                this.start = start;
                this.end = end;
            }

            @Override
            public Monitor call() {
                Monitor monitor = new Monitor();
                for (int pre = start; pre < end; pre++) {
                    for (int post = 0; post < end; post++) {
                        int dist = distanceBetween(pre, post);
                        if (dist > 0) {//if pre near post, add to pairMonitor
                            int mapkey = Com.getMapKey(cellList.get(pre).isGlu(),
                                    cellList.get(post).isGlu(), dist);
                            monitor.addPairToConn(pre, post, mapkey);
                        }
                    }

                }
                return monitor;
            }

            private int distanceBetween(int pre, int post) {
                int x1 = cellList.get(pre).getX();
                int x2 = cellList.get(post).getX();
                int y1 = cellList.get(pre).getY();
                int y2 = cellList.get(post).getY();
                int dx = (x2 >= x1) ? (x2 - x1) : (x1 - x2);
                int dy = (y2 >= y1) ? (y2 - y1) : (y1 - y2);
                if (dx > 500 || dy > 500 || (dx + dy) > 708) {
                    return -1;
                }
                int dist = (int) Math.sqrt(dx * dx + dy * dy);
                return dist > 500 ? -1 : dist;
            }
        }
        ArrayList<Future<Monitor>> handles = new ArrayList<>();
//        Future<?>[] handle = new Future<?>[threads];
        for (int i = 0; i < threads - 1; i++) {
            handles.add(es.submit(new genPairClass(threadLength * i, threadLength * (i + 1))));
        }
        handles.add(es.submit(new genPairClass(threadLength * (threads - 1), cellList.size())));

        Monitor monitor = new Monitor();
        for (Future<Monitor> f : handles) {
            try {
                Monitor m = f.get();
                monitor.addAll(m);
            } catch (InterruptedException | ExecutionException ex) {
                System.out.println(ex.toString());
            }
        }
//        System.out.println("return monitor");
//        monitor.listAll();
        progressUpdate("Monitor Generated.");
//        monitor.listAll();
        return monitor;
    }

    class genPairsClass implements Runnable {

        Set<int[]> monitorPairSet;
        Set<Integer> had;
        boolean run;
        Timer timer;
        CountDownLatch cdl;
        ActionListener taskPerformer = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                timer.stop();
                run = false;
            }
        };

        genPairsClass(Set<int[]> monitorPairSet, Set<Integer> had, CountDownLatch cdl) {
            this.monitorPairSet = monitorPairSet;
            this.had = had;
            this.cdl = cdl;
        }

        public void startTimer(int time) {
            int delay = time * 1000; //milliseconds
            timer = new Timer(delay, taskPerformer);
            timer.start();
        }

        @Override
        public void run() {
//            D.tp("Enter pair gen");
//            monitorPairSet = new HashSet<>();
            run = true;
//            HashSet<Integer> had = new HashSet<>();
            while (run) {
                int[] grp = genRndPair(had);
                if (null != grp) {
                    monitorPairSet.add(grp);
                }
            }
            cdl.countDown();
        }

        private int[] genRndPair(Set<Integer> had) {
            //Get id1
            int id1 = 0;
            int id2 = 0;
            int watchdog = 0;
            do {
                watchdog++;
                if (watchdog > 500) {
                    return null;
                }
                id1 = r.nextInt(cellList.size());
                id2 = r.nextInt(cellList.size());
            } while (!cellList.get(id1).near(cellList.get(id2)) || id2 == id1 || had.contains(getSetKey(id1, id2)));

//            D.tp(Com.dist(id1, id2, cellList)+","+farApart(id1,id2));
            int[] rtn = {id1, id2};
            had.add(getSetKey(id1, id2));
            return rtn;
        }

        private int getSetKey(int id1, int id2) {
            return id1 < id2 ? (id1 << 12) + id2 : (id2 << 12) + id1;
        }
    }

    public void setRunState(RunState runState) {
        this.runState = runState;
    }

    public RunState getRunState() {
        return runState;
    }

    private void progressUpdate(String s) {
        synchronized (updates) {
            if (updates.size() > 0 && s.equals(updates.get(updates.size() - 1))) {
                return;
            }
            updates.add(s);
        }
    }

    public Set<Integer> getConnected() {
        return connected;
    }

    public int getDimension() {
        return dim;
    }

    public ArrayList<RndCell> getCellList() {
        return cellList;
    }

    public List<String> getUpdates() {
        synchronized (updates) {
            return updates;
        }
    }

    private int oneCommNeib(int id1, int id2, int id3) {
//        boolean[] rtn = new boolean[6];//neib_isGlu neib_conned L-R_isGlu L-R_Conned
        int key = 0;
        key += cellList.get(id1).isGlu() ? 1 : 0;
        key += (connected.contains(Com.getSetKey(id1, id2))
                && connected.contains(Com.getSetKey(id1, id3))) ? (1 << 1) : 0;
        key += connected.contains(Com.getSetKey(id2, id3)) ? (1 << 2) : 0;
        key += cellList.get(id2).isGlu() ? (1 << 3) : 0;
        key += cellList.get(id3).isGlu() ? (1 << 4) : 0;
        return key;
    }

    private int getCommonNeibKey(boolean NeibGlu, boolean NeibConned, boolean pairConned, boolean fwdGlu, boolean revGlu) {
        int key = 0;
        key += NeibGlu ? 1 : 0;
        key += NeibConned ? 1 << 1 : 0;
        key += pairConned ? 1 << 2 : 0;
        key += fwdGlu ? 1 << 3 : 0;
        key += revGlu ? 1 << 4 : 0;
        return key;
    }

    public void probeCommNeib(int time, boolean fwdGlu, boolean revGlu) {
        HashMap<Integer, Integer> commNeib = new HashMap<>();
        Set<int[]> monitorSet = genGrpMonitor(3, time);
        for (int[] grp : monitorSet) {
//            int[] grp = genRndGrp(3, had);
            Com.sAdd(commNeib, oneCommNeib(grp[0], grp[1], grp[2]));
            Com.sAdd(commNeib, oneCommNeib(grp[0], grp[2], grp[1]));
            Com.sAdd(commNeib, oneCommNeib(grp[1], grp[0], grp[2]));
            Com.sAdd(commNeib, oneCommNeib(grp[1], grp[2], grp[0]));
            Com.sAdd(commNeib, oneCommNeib(grp[2], grp[0], grp[1]));
            Com.sAdd(commNeib, oneCommNeib(grp[2], grp[1], grp[0]));
        }
        //CommNeiIsGlu,CommNeiExist,ThisPariConned,ThisPairPreGlu,ThisPairPostGlu
        int conned_Wo_GNei = Com.sGet(commNeib, getCommonNeibKey(true, false, true, fwdGlu, revGlu))
                + Com.sGet(commNeib, getCommonNeibKey(false, false, true, fwdGlu, revGlu))
                + Com.sGet(commNeib, getCommonNeibKey(false, true, true, fwdGlu, revGlu));
        int noConn_Wo_GNei = Com.sGet(commNeib, getCommonNeibKey(true, false, false, fwdGlu, revGlu))
                + Com.sGet(commNeib, getCommonNeibKey(false, false, false, fwdGlu, revGlu))
                + Com.sGet(commNeib, getCommonNeibKey(false, true, false, fwdGlu, revGlu));

        int conned_W_GNei = Com.sGet(commNeib, getCommonNeibKey(true, true, true, fwdGlu, revGlu));
        int noConn_W_GNei = Com.sGet(commNeib, getCommonNeibKey(true, true, false, fwdGlu, revGlu));


        int conned_Wo_ANei = Com.sGet(commNeib, getCommonNeibKey(true, false, true, fwdGlu, revGlu))
                + Com.sGet(commNeib, getCommonNeibKey(true, true, true, fwdGlu, revGlu))
                + Com.sGet(commNeib, getCommonNeibKey(false, false, true, fwdGlu, revGlu));
        int noConn_Wo_ANei = Com.sGet(commNeib, getCommonNeibKey(true, false, false, fwdGlu, revGlu))
                + Com.sGet(commNeib, getCommonNeibKey(true, true, false, fwdGlu, revGlu))
                + Com.sGet(commNeib, getCommonNeibKey(false, false, false, fwdGlu, revGlu));

        int conned_W_ANei = Com.sGet(commNeib, getCommonNeibKey(false, true, true, fwdGlu, revGlu));
        int noConn_W_ANei = Com.sGet(commNeib, getCommonNeibKey(false, true, false, fwdGlu, revGlu));


        float r_Wo_GNei = (float) conned_Wo_GNei / (conned_Wo_GNei + noConn_Wo_GNei);
        float r_W_GNei = (float) conned_W_GNei / (conned_W_GNei + noConn_W_GNei);
        float r_Wo_ANei = (float) conned_Wo_ANei / (conned_Wo_ANei + noConn_Wo_ANei);
        float r_W_ANei = (float) conned_W_ANei / (conned_W_ANei + noConn_W_ANei);
        System.out.println((fwdGlu ? "Glu" : "GABA") + "->" + (revGlu ? "Glu" : "GABA"));
//        D.tp("r_Wo_GNei,r_W_GNei, r_Wo_ANei,r_W_ANei");
        System.out.println(r_Wo_GNei + "\t" + r_W_GNei + "\t" + r_Wo_ANei + "\t" + r_W_ANei);
    }

    public int[] probeIO(int time, boolean glu, boolean input) {
        int[] degrees = new int[4];
        Set<int[]> monitorSet = genGrpMonitor(4, time);
        for (int[] grp : monitorSet) {
            for (int i = 0; i < grp.length; i++) {
                if (!(cellList.get(i).isGlu() == glu || input)) {
                    continue;
                }
                int count = 0;
                for (int j = 0; j < grp.length; j++) {
                    if (i == j) {
                        continue;
                    } else {
                        int setKey = input ? Com.getSetKey(grp[j], grp[i]) : Com.getSetKey(grp[i], grp[j]);
                        count += (connected.contains(setKey) && (cellList.get(i).isGlu() == glu)) ? 1 : 0;
                    }
                }
                degrees[count]++;
            }
        }
        return degrees;
    }

    public int[] probeCluster(int time, int type, int size) {

        Set<int[]> monitorSet = genGrpMonitor(size, time);

        int[] histo = new int[(size == 3) ? 7 : 13];
        for (int[] grp : monitorSet) {
            int id1 = grp[0];
            int id2 = grp[1];
            int id3 = grp[2];
            int count = 0;
            count += countCluster(type, id1, id2);
            count += countCluster(type, id1, id3);
            count += countCluster(type, id2, id1);
            count += countCluster(type, id2, id3);
            count += countCluster(type, id3, id1);
            count += countCluster(type, id3, id2);

            /*
             * Here after is unfinished but highly usable
             * DO NOT DELETE
             */
            if (size == 4) {
                int id4 = grp[3];
                count += countCluster(type, id1, id4);
                count += countCluster(type, id4, id1);
                count += countCluster(type, id2, id4);
                count += countCluster(type, id4, id2);
                count += countCluster(type, id3, id4);
                count += countCluster(type, id4, id3);

            }
            histo[count]++;
        }
        return histo;


    }

    class genGrpsClass implements Runnable {

        Set<int[]> monitor;
        Set<Long> had;
        boolean run;
        Timer timer;
        int size;
        CountDownLatch cdl;
        ActionListener taskPerformer = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
//                D.tp("time;s up");
                timer.stop();
                run = false;
            }
        };

        genGrpsClass(int size, Set<int[]> monitor, Set<Long> had, CountDownLatch cdl) {
            this.size = size;
            this.monitor = monitor;
            this.had = had;
            this.cdl = cdl;
        }

        public void startTimer(int time) {
            int delay = time * 1000; //milliseconds
            timer = new Timer(delay, taskPerformer);
            timer.start();
        }

        @Override
        public void run() {
//            monitor = new HashSet<>();
//            HashSet<Integer> had = new HashSet<>();
//            D.tp("gen Grp running");
            run = true;
            while (run) {
                int[] grp = genRndGrp(size, had);
                if (null != grp) {
                    monitor.add(grp);
                }
            }
            cdl.countDown();
//            D.tp("gen Grp Stopped");
        }

        private int[] genRndGrp(int size, Set<Long> had) {
            int[] grp = new int[size];
            int watchLimit = 1000;
            boolean flag = true;
            int watchDog = 0;
            do {
                grp[0] = r.nextInt(cellList.size());
                for (int currPtr = 1; currPtr < size;) {
                    watchDog++;
                    if (watchDog > watchLimit) {
                        return null;
                    }
                    int c = r.nextInt(cellList.size());
                    boolean notInGrp = true;
                    boolean nearAll = true;
                    for (int i = 0; i < currPtr; i++) {
                        notInGrp = (grp[i] == c) ? false : notInGrp;
                        nearAll = (cellList.get(c).veryNear(cellList.get(grp[i]))) ? nearAll : false;
                    }
                    if (nearAll && notInGrp) {
                        grp[currPtr] = c;
                        currPtr++;
                        flag = (currPtr < size);
                    }
                }
            } while (flag || had.contains(getKey(grp)));
            had.add(getKey(grp));
            return grp;
        }

        private long getKey(int[] in) {
            Arrays.sort(in);
            long key = 0;
            for (int i = 0; i < in.length; i++) {
                key += in[i] << (12 * i);
            }
            return key;
        }
    }

    public void writeMatrix(float weightScale) {
        String suffix = "_C_" + Float.toString(connProbScale) + "_W_" + Float.toString(weightScale);
//        String suff = suffix.length == 0 ? "" : suffix[0];
        /*
         * prepare for calc weight according to GABA conns
         */
        int[] gabaIOCount = new int[cellList.size()];
        int sum = 0;
        for (int i = 0; i < gabaIOCount.length; i++) {
            gabaIOCount[i] = gabaIn.get(i) + gabaOut.get(i);
            sum += gabaIOCount[i];
        }
        float avg = (float) sum / cellList.size();
//        D.tp(avg);

        /*
         * seperate glu cells and gaba cells
         */

        ArrayList<Integer> gluCells = new ArrayList<>();
        ArrayList<Integer> gabaCells = new ArrayList<>();
        for (int i = 0; i < cellList.size(); i++) {
            if (cellList.get(i).isGlu()) {
                gluCells.add(i);
            } else {
                gabaCells.add(i);
            }
        }

        Object[][] cellNum = new Object[2][1];
        cellNum[0][0] = gabaCells.size();
        cellNum[1][0] = gluCells.size();
        FilesCommons.writeMatrix("cellNum" + suffix + ".csv", cellNum);


        int totalCount = cellList.size();
//        int gluCount = gluCells.size();
        int gabaCount = gabaCells.size();
        float[][] weight = new float[totalCount][totalCount];
        for (int pre = 0; pre < totalCount; pre++) {
            weight[pre][pre] = 0;
            for (int post = pre + 1; post < totalCount; post++) {
                int actPre = pre < gabaCount ? gabaCells.get(pre) : gluCells.get(pre - gabaCount);
                int actPost = post < gabaCount ? gabaCells.get(post) : gluCells.get(post - gabaCount);
                int setKey = Com.getSetKey(actPre, actPost);
                weight[pre][post] = connected.contains(setKey) ? calcWeight(gabaIOCount[actPre] + gabaIOCount[actPost], avg, weightScale) : 0;
                setKey = Com.getSetKey(actPost, actPre);
                weight[post][pre] = connected.contains(setKey) ? calcWeight(gabaIOCount[actPre] + gabaIOCount[actPost], avg, weightScale) : 0;
            }
        }
        String fileName = TYPE == ModelType.Network ? "actOrg" + suffix + ".csv" : "distOrg" + suffix + ".csv";
        FilesCommons.writeMatrix(fileName, weight);


    }

    public void writeSave(float weightScale) {
        /*
         * prepare for calc weight according to GABA conns
         */
        int[] gabaIOCount = new int[cellList.size()];
        int sum = 0;
        for (int i = 0; i < gabaIOCount.length; i++) {
            gabaIOCount[i] = gabaIn.get(i) + gabaOut.get(i);
            sum += gabaIOCount[i];
        }
        float avg = (float) sum / cellList.size();

        ArrayList<Boolean> neurons = new ArrayList<>(cellList.size());
        ArrayList<int[]> neuronCoord = new ArrayList<>();
        for (int i = 0; i < cellList.size(); i++) {
            neurons.add(cellList.get(i).isGlu());
            int[] coord = {cellList.get(i).getX(), cellList.get(i).getY()};
            neuronCoord.add(coord);
        }


        HashMap<Integer, Float> synapticWeights = new HashMap<>(15 * cellList.size());
        for (Integer key : connected) {
            int[] pair = Com.getIDsFromSetKey(key);
            int pre = pair[0];
            int post = pair[1];
            synapticWeights.put(key, calcWeight(gabaIOCount[pre] + gabaIOCount[post], avg, weightScale));
        }



        /*
         * actually writing serialized saves
         */
        NetworkParameters save = new NetworkParameters(neurons, synapticWeights, neuronCoord);
        String type = TYPE == ModelType.Network ? "Net" : "Ctl";
        String suffix = "_C_" + Float.toString(connProbScale) + "_W_" + Float.toString(weightScale);

        try (ObjectOutputStream o = new ObjectOutputStream(
                new FileOutputStream("conn_" + type + suffix + ".ser"))) {
            o.writeObject(save);
        } catch (IOException e) {
            System.out.println("ser io error");
        }
    }

    private float calcWeight(int sum, float avg, float scale) {
        float ceiling = 4.0f * avg;
        return sum > ceiling ? 1.5f * scale : sum / ceiling + 0.5f * scale + (float) r.nextGaussian() * 0.1f;

    }

    public void probeGlobalDegrees() {
        TreeMap<Integer, Integer> gluInMap = new TreeMap<>();
        TreeMap<Integer, Integer> gluOutMap = new TreeMap<>();
        TreeMap<Integer, Integer> gabaInMap = new TreeMap<>();
        TreeMap<Integer, Integer> gabaOutMap = new TreeMap<>();

        for (int i = 0; i < cellList.size(); i++) {
            Com.sAdd(gluInMap, gluIn.get(i));
            Com.sAdd(gluOutMap, gluOut.get(i));
            Com.sAdd(gabaInMap, gabaIn.get(i));
            Com.sAdd(gabaOutMap, gabaOut.get(i));
        }
        System.out.println("Glu In=====================================");
        for (int i = 0; i < gluInMap.size(); i++) {
            Com.tp(i, gluInMap.get(i));
        }
        Com.tp("Glu Out=====================================");
        for (int i = 0; i < gluOutMap.size(); i++) {
            Com.tp(i, gluInMap.get(i));
        }
        Com.tp("GABA In=====================================");
        for (int i = 0; i < gabaInMap.size(); i++) {
            Com.tp(i, gluInMap.get(i));
        }
        Com.tp("GABA Out=====================================");
        for (int i = 0; i < gabaOutMap.size(); i++) {
            Com.tp(i, gluInMap.get(i));
        }

    }

    private void sumUp() {
        int sumGlu = 0;
        for (int i = 0; i < gluOut.length(); i++) {
            sumGlu += gluOut.get(i);
        }

        int sumGABA = 0;
        for (int i = 0; i < gabaOut.length(); i++) {
            sumGABA += gabaOut.get(i);
        }

        progressUpdate(sumGlu + " glu connections, " + sumGABA + " GABA connections.");


        System.out.println("===========================================");
        System.out.println(TYPE + (DEPOLAR_GABA ? ", GABA_DEP" : ", GABA_HYP") + " Finished");
        System.out.println("===========================================");
        Toolkit.getDefaultToolkit().beep();
        if (writeFile) {
            writeSave(weightScale);
        }
        runState = RunState.NetGenerated;
        progressUpdate("Model Generated");

    }

    public void genModelNetwork(String pathToFile) {
        try {
            //Target Numbers

            final Queue<Integer> keys = allPair.getKeySet();
            final Monitor unconnPair;
            final List<Map<Integer, Integer>> connNeeded = new ArrayList<>();
            final ModelDB db = new ModelDB(pathToFile);
            final List<HashMap<Integer, Float>> obsConnProfile = db.getPBase();
            for (int i = 0;
                    i < obsConnProfile.size();
                    i++) {
//                Com.tp("div", (i + 5));
                Map<Integer, Integer> needed = new HashMap<>();
                for (Integer key : keys) {
                    if (obsConnProfile.get(i).containsKey(key)) {

                        int currNeed = Math.round(
                                obsConnProfile.get(i).get(key) * allPair.getList(key).size());
//                        Com.tp("key", key, "ratio", obsConnProfile.get(i).get(key),"size", allPair.getList(key).size(),"num", currNeed);
                        for (int j = i - 1; j >= 0; j--) { // diffential growth
                            currNeed -= (connNeeded.get(j).containsKey(key))
                                    ? connNeeded.get(j).get(key) : 0;
                        }
                        needed.put(key, currNeed);

                    }
                }
                connNeeded.add(i, needed);

            }
            //Tool Variables
            final int nCell = cellList.size();

            connected.clear();
            gluIn = new AtomicIntegerArray(nCell);
            gluOut = new AtomicIntegerArray(nCell);
            gabaIn = new AtomicIntegerArray(nCell);
            gabaOut = new AtomicIntegerArray(nCell);
            //TODO if allPair is not used elsewhere should move here locally
            unconnPair = new Monitor();

            unconnPair.addAll(allPair);

            class genConn implements Callable<Queue<Queue<int[]>>> {

                final private Queue<int[]> conned;
                final private Queue<int[]> unConned;
                final private Queue<int[]> toConn;
                final private int nConnNeeded;
                final private float prob;

                public genConn(Queue<int[]> toConn, int nNeeded, float prob) {
                    this.toConn = toConn;
                    this.conned = new LinkedList<>();
                    this.unConned = new LinkedList<>();
                    this.nConnNeeded = nNeeded;
                    this.prob = prob;
                }

                @Override
                public Queue<Queue<int[]>> call() {
//                System.out.println("615 called generation");
                    int newConn = 0;
//                    System.out.println(key + "\t" + toConn.size() + "\t" + nConnNeeded);

                    for (int[] pair : toConn) {
                        if (newConn >= nConnNeeded) {
                            break;
                        }
                        if (newConnection(prob, pair[0], pair[1])) {
                            if (cellList.get(pair[0]).isGlu()) {
                                gluOut.incrementAndGet(pair[0]);
                                gluIn.incrementAndGet(pair[1]);
                            } else {
                                gabaOut.incrementAndGet(pair[0]);
                                gabaIn.incrementAndGet(pair[1]);
                            }
                            conned.add(pair);
                            newConn++;
                            //if fulfill break
                        } else {
                            unConned.add(pair);
                        }
                    }
                    Queue<Queue<int[]>> rtn = new LinkedList<>();
                    rtn.offer(conned);
                    rtn.offer(unConned);
//                System.out.println("615 returned que");
                    return rtn;
                }

                boolean newConnection(float prob, int pre, int post) {//from 1 to 2
//                System.out.println("615 enter new conn");
                    float p = prob;
                    /*
                     * Activity dependent connection
                     */
                    if (TYPE == ModelType.NetworkBiDir) {
                        int gluIO = gluIn.get(pre) + gluIn.get(post) + gluOut.get(pre) + gluOut.get(post);
                        int gabaIO = gabaIn.get(pre) + gabaIn.get(post) + gabaOut.get(pre) + gabaOut.get(post);
                        float gluIOFactor = gluIO * GLU_IO_COE;
                        float gabaIOFactor = (DEPOLAR_GABA ? 1f : -1f) * gabaIO * GABA_IO_COE;
                        float IOFactor = (gluIOFactor + gabaIOFactor) > 0 ? (gluIOFactor + gabaIOFactor) : 0;
                        p *= (IOFactor + 1f);
                    } else if (TYPE == ModelType.Network) {
                        int gluIO = gluOut.get(pre) + gluOut.get(post);
                        int gabaIO = gabaOut.get(pre) + gabaOut.get(post);
                        float gluIOFactor = gluIO * GLU_IO_COE;
                        float gabaIOFactor = (DEPOLAR_GABA ? 1f : -1f) * gabaIO * GABA_IO_COE;
                        float IOFactor = (gluIOFactor + gabaIOFactor) > 0 ? (gluIOFactor + gabaIOFactor) : 0;
                        p *= (IOFactor + 1f);
                    } else if (TYPE == ModelType.Ctrl) {
                        p *= 10;
                    }
                    /*
                     * Active or not
                     */
//                System.out.println("615 ended new conn");
                    return p > r.nextFloat() ? true : false;
                }
            }
            int threads = Runtime.getRuntime().availableProcessors();
            ExecutorService es = Executors.newFixedThreadPool(threads);
            runState = RunState.GeneratingNet;
            for (int div = 0;
                    div < 4; div++) { //bias by 5
                progressUpdate("Modeling Div " + (div + 5));
                Map<Integer, Integer> connNeedPerDiv = connNeeded.get(div);
                Map<Integer, Float> probMap = obsConnProfile.get(div);

                int totalProgress = connNeedPerDiv.size();
                while (connNeedPerDiv.size() > 0 && runState != RunState.UserRequestStop) {
                    setProgress(totalProgress - connNeedPerDiv.size(), totalProgress);
                    Queue<Integer> keyList = new LinkedList<>();
                    Queue<Future<Queue<Queue<int[]>>>> handles = new LinkedList<>();
                    Set<Integer> keysNeeded = connNeedPerDiv.keySet();
                    for (Integer key : keysNeeded) {
                        int toConn = connNeedPerDiv.get(key);
                        float prob = probMap.get(key) / ITERATE_FACTOR;
                        keyList.offer(key);
                        handles.offer(es.submit(new genConn(unconnPair.getList(key), toConn, prob)));
                    }

//        runState = RunState.GeneratingNet;

                    while (handles.size() > 0) {
                        try {
                            Integer key = keyList.poll();
                            Future<Queue<Queue<int[]>>> handle = handles.poll();
                            Queue<Queue<int[]>> q = handle.get();
                            Queue<int[]> conned = q.poll();
                            int newConn = conned.size();
                            for (int[] pair : conned) {
                                connected.add((pair[0] << 12) + pair[1]);
                            }
                            int wasNeeded = connNeedPerDiv.get(key);
                            if (wasNeeded <= newConn) {
                                connNeedPerDiv.remove(key);
                            } else {
                                connNeedPerDiv.put(key, wasNeeded - newConn);
                                Queue<int[]> stillNeedConn = q.poll();
                                unconnPair.setList(key, stillNeedConn);
                            }
                        } catch (InterruptedException | ExecutionException ex) {
                            System.out.println("Net Model, Get Que");
                            System.out.println(ex.toString());
                        }
                    }
                }
                //while end
            }
            //for div 5-9 end
            runState = (runState == RunState.UserRequestStop) ? RunState.StoppedByUser : RunState.NetGenerated;

            sumUp();
        } catch (Throwable ex) {
            System.out.println(ex.toString());


        }
    }

    class Monitor {

        final private Map<Integer, Queue<int[]>> toConn;
        final private int distBinCount = 7;//Less Than 50 100 150 200 250 350 500
        final private Queue<Integer> keySet;

        public Monitor() {
            toConn = new HashMap<>();
            keySet = new LinkedList<>();
            for (int i = 0; i < distBinCount; i++) {
                keySet.add(iterateKey(true, true, i));
                keySet.add(iterateKey(true, false, i));
                keySet.add(iterateKey(false, true, i));
                keySet.add(iterateKey(false, false, i));
            }
            for (Integer key : keySet) {
                toConn.put(key, new LinkedList<int[]>());
            }

        }

        private Integer iterateKey(boolean preGlu, boolean postGlu, int distBin) {
            int key = 0;
            key += ((preGlu ? 0 : 1) << 13);
            key += ((postGlu ? 0 : 1) << 12);
            key += distBin;
            return key;
        }

        public void addPairToConn(int pre, int post, Integer mapKey) {
            int[] newPair = {pre, post};
            toConn.get(mapKey).add(newPair);
        }

        public void addAll(Monitor input) {
            for (Integer key : keySet) {
                toConn.get(key).addAll(input.getList(key));
            }
        }

        public Queue<int[]> getList(Integer key) {
            return toConn.get(key);
        }

        public Queue<Integer> getKeySet() {
            return keySet;
        }

        public void listAll() {
            System.out.println("List Map Sizes");
            int total = 0;
            for (Integer key : keySet) {
                System.out.print(key + ",");
                System.out.println(toConn.get(key).size());
                total += toConn.get(key).size();
            }
            System.out.println("total " + total);
        }

        public void setList(Integer key, Queue<int[]> value) {
            toConn.put(key, value);
        }
    }
}
