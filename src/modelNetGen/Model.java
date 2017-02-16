/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modelNetGen;

//import commonLibs.ModelType;
import commonLibs.RndCell;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
//import java.util.Deque;
import java.util.HashMap;
//import java.util.HashSet;
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
//import jungClustering.Cluster;
import org.apache.commons.math3.random.RandomGenerator;
import commonLibs.NetworkParameters;
import java.text.DecimalFormat;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author Libra
 */
public class Model {

    final private RandomGenerator r;
    final private ArrayList<RndCell> cellList;
    final private Set<Integer> connected;
    private AtomicIntegerArray gluIn;
    private AtomicIntegerArray gluOut;
    private AtomicIntegerArray gabaIn;
    private AtomicIntegerArray gabaOut;
//    private boolean DEPOLAR_GABA;
    private float ITERATE_FACTOR;
    private final int dim;
    private RunState runState;
    final private List<String> updates;
    private int progress;
//    private ModelType TYPE;
//    private int genMonitorTime = 20;
    private float connProbScale;
    private boolean writeFile;
    private float weightScale;
    final private Monitor allPair;
    private String rndSuffix = "";
    private float networkFactor;
    private boolean clusteredWeight = false;

    /**
     * Build a new iterate model
     *
     * @param type Type of Model (random-dist, bi-dir, uni-dir)
     * @param depolarGABA True if GABA is depolarizing.
     * @param gluE Glutamate IO coefficient
     * @param gabaE GABA IO coefficient
     * @param iterateFactor Expected average iterate cycle
     */
    Model(int nCell, int density, float gluRate) {
        r = Com.getR();
        runState = RunState.Instantiated;
        updates = new LinkedList<>();
        progress = 0;
        connected = Collections.newSetFromMap(new ConcurrentHashMap<Integer, Boolean>(10000));
        cellList = new ArrayList<>();
        dim = setDimension(nCell, density);
        for (int i = 0; i < nCell; i++) {
            RndCell newCell = new RndCell(dim, gluRate);
            cellList.add(newCell);
        }
        allPair = genPairMonitor();
        allPair.listAll();
        if (cellList.size() < 3) {
            progressUpdate("Empty Cell List");
        }
        runState = RunState.ReadyGenCells;
    }

    public void setITERATE_FACTOR(float ITERATE_FACTOR) {
        this.ITERATE_FACTOR = ITERATE_FACTOR;
    }

    public void setNetworkFactor(float networkFactor) {
        this.networkFactor = networkFactor;
    }

//    public void setType(ModelType type) {
//        this.TYPE = type;
//    }
//    public void setDEPOLAR_GABA(boolean DEPOLAR_GABA) {
//        this.DEPOLAR_GABA = DEPOLAR_GABA;
//    }
    private int setDimension(int nCell, int density) {
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

    public void setClusteredWeight(boolean clusteredWeight) {
        this.clusteredWeight = clusteredWeight;
    }

    private int countGroupDensity(int type, int id1, int id2) {
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
                    for (int post = 0; post < cellList.size(); post++) {
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
//        monitor.listAll();
//        progressUpdate("Monitor Generated.");
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

    public float[] probeCommNeib(int time, boolean fwdGlu, boolean revGlu) {
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
//        System.out.println((fwdGlu ? "Glu" : "GABA") + "->" + (revGlu ? "Glu" : "GABA"));
//        D.tp("r_Wo_GNei,r_W_GNei, r_Wo_ANei,r_W_ANei");
//        System.out.println(r_Wo_GNei + "\t" + r_W_GNei + "\t" + r_Wo_ANei + "\t" + r_W_ANei);
        float[] rtn = {r_Wo_GNei, r_W_GNei, r_Wo_ANei, r_W_ANei};
        return rtn;
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

    public int[] probeGroupDensity(int time, int type, int size) {

        Set<int[]> monitorSet = genGrpMonitor(size, time);

        int[] histo = new int[(size == 3) ? 7 : 13];
        for (int[] grp : monitorSet) {
            int id1 = grp[0];
            int id2 = grp[1];
            int id3 = grp[2];
            int count = 0;
            count += countGroupDensity(type, id1, id2);
            count += countGroupDensity(type, id1, id3);
            count += countGroupDensity(type, id2, id1);
            count += countGroupDensity(type, id2, id3);
            count += countGroupDensity(type, id3, id1);
            count += countGroupDensity(type, id3, id2);

            /*
             * Here after is unfinished but highly usable
             * DO NOT DELETE
             */
            if (size == 4) {
                int id4 = grp[3];
                count += countGroupDensity(type, id1, id4);
                count += countGroupDensity(type, id4, id1);
                count += countGroupDensity(type, id2, id4);
                count += countGroupDensity(type, id4, id2);
                count += countGroupDensity(type, id3, id4);
                count += countGroupDensity(type, id4, id3);

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

            public void actionPerformed(ActionEvent evt) {
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
            run = true;
            while (run) {
                int[] grp = genRndGrp(size, had);
                if (null != grp) {
                    monitor.add(grp);
                }
            }
            cdl.countDown();
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

    public HashMap<Integer, Float> calcNetWeight(boolean clusteredWeight, float weightScale) {
        /*
         * prepare for calc weight according to GABA conns
         */
        TreeSet<Integer> degreeSet = new TreeSet<>();

        for (Integer key : connected) {
            int[] pair = Com.getIDsFromSetKey(key);
            int pre = pair[0];
            int post = pair[1];
            int clusterLevel = gabaIn.get(pre) + gabaOut.get(pre) + gluIn.get(pre) + gluOut.get(pre)
                    + gabaIn.get(post) + gabaOut.get(post) + gluIn.get(post) + gluOut.get(post);
            degreeSet.add(clusterLevel);
        }

        HashMap<Integer, Float> drivingForces = new HashMap<>(connected.size());
        for (Integer key : connected) {
            int[] pair = Com.getIDsFromSetKey(key);
            int pre = pair[0];
            int post = pair[1];
            int clusterLevel = gabaIn.get(pre) + gabaOut.get(pre) + gluIn.get(pre) + gluOut.get(pre)
                    + gabaIn.get(post) + gabaOut.get(post) + gluIn.get(post) + gluOut.get(post);
            float position = clusteredWeight ? (float) degreeSet.headSet(clusterLevel).size() / degreeSet.size() : ThreadLocalRandom.current().nextFloat();
//            System.out.println(clusterLevel + ", " + position);
            float drivingForce = ModelDB.getDrivingForce(cellList.get(pre).isGlu(), cellList.get(post).isGlu(), position);
            drivingForces.put(key, drivingForce);
        }
        return drivingForces;
    }

    public void writeSave(HashMap<Integer, Float> synapticWeights, double[] clusterCoef) {


        /*
         * actually writing serialized saves
         */
//        HashSet<HashSet<Integer>> clusters = (new Cluster()).getClusteredSets(cellList, connected);
        NetworkParameters save = new NetworkParameters(cellList, synapticWeights, /*clusters, TYPE,*/ connProbScale, weightScale, rndSuffix);
//        String type = TYPE == ModelType.Network ? "Net" : "Ctl";
//        String suffix = "_C" + dformat.format(connProbScale) + "_W" + dformat.format(weightScale) + "_" + rndSuffix;
        final DecimalFormat dformat = new DecimalFormat("0.00");
        String suffix = "net" + dformat.format(networkFactor) + "_Coef" + dformat.format(clusterCoef[0]) + "_cCoef" + dformat.format(clusterCoef[1]) + "_" + rndSuffix;

        try (ObjectOutputStream o = new ObjectOutputStream(
                new FileOutputStream(FilesCommons.getJarFolder("") + "\\" + /*type +*/ suffix + "_Conn.ser"))) {
                    o.writeObject(save);
                } catch (IOException e) {
                    System.out.println("ser io error");
                }
    }

    public void setRndSuffix(String rndSuffix) {
        this.rndSuffix = rndSuffix;
    }

    public Queue<TreeMap<Integer, Integer>> probeGlobalDegrees() {
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

        Queue<TreeMap<Integer, Integer>> q = new LinkedList<>();
        q.offer(gluInMap);
        q.offer(gluOutMap);
        q.offer(gabaInMap);
        q.offer(gabaOutMap);
        return q;
    }

    private void sumUp(double[] coefs) {
        int sumGlu = 0;
        for (int i = 0; i < gluOut.length(); i++) {
            sumGlu += gluOut.get(i);
        }

        int sumGABA = 0;
        for (int i = 0; i < gabaOut.length(); i++) {
            sumGABA += gabaOut.get(i);
        }

        progressUpdate(sumGlu + " glu connections, " + sumGABA + " GABA connections.");

//        Toolkit.getDefaultToolkit().beep();
        if (writeFile) {
            writeSave(calcNetWeight(clusteredWeight, weightScale), coefs);
        }
        runState = RunState.NetGenerated;
        progressUpdate(/*TYPE +(DEPOLAR_GABA ? ", GABA_Depolarize" : ", GABA_Hyperpolarize") +*/" Model Generated");

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
                                obsConnProfile.get(i).get(key) * allPair.getList(key).size() * connProbScale);
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

            class genConn implements Callable<List<int[]>[]> {

                final private List<int[]> conned;
                final private List<int[]> unConned;
                final private List<int[]> toConn;
                final private int nConnNeeded;
                final private float prob;

                public genConn(List<int[]> toConn, int nNeeded, float prob) {
                    this.toConn = toConn;
                    Collections.shuffle(this.toConn);
                    this.conned = new LinkedList<>();
                    this.unConned = new LinkedList<>();
                    this.nConnNeeded = nNeeded;
                    this.prob = prob;
                }

                @Override
                public List<int[]>[] call() {

                    for (int[] pair : toConn) {
                        if (conned.size() >= nConnNeeded) {
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
                            //if fulfill break
                        } else {
                            unConned.add(pair);
                        }
                    }
                    return new List[]{conned, unConned};
                }

                boolean newConnection(float prob, int pre, int post) {//from 1 to 2
                    float p = prob;
                    /*
                     * Activity dependent connection
                     */
//                    if (TYPE == ModelType.NetworkBiDir) {
//                        int gluIO = gluIn.get(pre) + gluIn.get(post) + gluOut.get(pre) + gluOut.get(post);
//                        int gabaIO = gabaIn.get(pre) + gabaIn.get(post) + gabaOut.get(pre) + gabaOut.get(post);
//                        float gluIOFactor = gluIO;
//                        float gabaIOFactor = (DEPOLAR_GABA ? 1f : -1f) * gabaIO;
//                        float IOFactor = (gluIOFactor + gabaIOFactor) > 0 ? (gluIOFactor + gabaIOFactor) : 0;
//                        p *= (IOFactor + 1f);
//                    } else if (TYPE == ModelType.Network) {
//                        int gluIO = gluOut.get(pre) + gluOut.get(post);
//                        int gabaIO = gabaOut.get(pre) + gabaOut.get(post);
//                        float gluIOFactor = gluIO;
//                        float gabaIOFactor = (DEPOLAR_GABA ? 1f : -1f) * gabaIO;
                    float IOFactor = gluOut.get(pre) + gluOut.get(post) + gabaOut.get(pre) + gabaOut.get(post)
                            + gluIn.get(pre) + gluIn.get(post) + gabaIn.get(pre) + gabaIn.get(post);
//                    p *= IOFactor * networkFactor + 1;
                    p *= Math.pow(networkFactor, IOFactor);
//                    } else if (TYPE == ModelType.Ctrl) {
//                        p *= ITERATE_FACTOR;
//                    }
                    /*
                     * Active or not
                     */
                    return p > r.nextFloat();
                }
            }
            int threads = Runtime.getRuntime().availableProcessors();
            ExecutorService es = Executors.newFixedThreadPool(threads);
            runState = RunState.GeneratingNet;
            for (int div = 0; div < 4; div++) { //bias by 5
                progressUpdate("Modeling Div " + (div + 5));
                Map<Integer, Integer> connNeedPerDiv = connNeeded.get(div);
                Map<Integer, Float> probMap = obsConnProfile.get(div);

                int totalProgress = connNeedPerDiv.size();
                while (connNeedPerDiv.size() > 0) {
                    if (runState == RunState.UserRequestStop) {
                        break;
                    }

                    setProgress(totalProgress - connNeedPerDiv.size(), totalProgress);
                    Queue<Integer> keyList = new LinkedList<>();
                    Queue<Future<List<int[]>[]>> handles = new LinkedList<>();
                    Set<Integer> keysNeeded = connNeedPerDiv.keySet();
                    for (Integer key : keysNeeded) {
                        int toConn = connNeedPerDiv.get(key);
                        float prob = probMap.get(key) / ITERATE_FACTOR;
                        keyList.offer(key);
                        handles.offer(es.submit(new genConn(unconnPair.getList(key), toConn, prob)));
                    }

//        runState = RunState.GeneratingNet;
                    while (handles.size() > 0) {
                        Integer key = keyList.poll();
                        Future<List<int[]>[]> handle = handles.poll();
                        List<int[]>[] q = handle.get();
                        List<int[]> conned = q[0];
                        int newConn = conned.size();
                        for (int[] pair : conned) {
                            connected.add((pair[0] << 12) + pair[1]);
                        }
                        int wasNeeded = connNeedPerDiv.get(key);
                        if (wasNeeded <= newConn) {
                            connNeedPerDiv.remove(key);
                        } else {
                            connNeedPerDiv.put(key, wasNeeded - newConn);
                            unconnPair.setList(key, q[1]);
                        }
                    }
                }
                //while end
            }
            //for div 5-9 end
            double[] coefs = calcClusteringCoefficient();
            sumUp(coefs);
            runState = (runState == RunState.UserRequestStop) ? RunState.StoppedByUser : RunState.NetGenerated;
        } catch (InterruptedException | ExecutionException ex) {
            System.out.println(ex.toString());
        }
    }

    private double[] calcClusteringCoefficient() {
        class CoefCalc extends Thread {

            int connCount;
            int connedTriplets = 0;
            int closeConnedTriplets = 0;
            int triangle = 0;
            int closeTriangle = 0;
            int start;
            int end;

            public void setRange(int start, int end) {
                this.start = start;
                this.end = end;
            }

            public int[] getClusterCoef() {
                return new int[]{triangle, connedTriplets};
            }

            public int[] getCloseClusterCoef() {
                return new int[]{closeTriangle, closeConnedTriplets};
            }

            @Override
            public void run() {
                for (int i = start; i < end; i++) {
                    for (int j = i + 1; j < cellList.size(); j++) {
                        for (int k = j + 1; k < cellList.size(); k++) {
                            connCount = 0;
                            connCount += (connected.contains(Com.getSetKey(i, j)) || connected.contains(Com.getSetKey(j, i))) ? 1 : 0;
                            connCount += (connected.contains(Com.getSetKey(j, k)) || connected.contains(Com.getSetKey(k, j))) ? 1 : 0;
                            if (connCount == 0) {
                                continue;
                            }
                            connCount += (connected.contains(Com.getSetKey(i, k)) || connected.contains(Com.getSetKey(k, i))) ? 1 : 0;

                            boolean close = cellList.get(i).near(cellList.get(j))
                                    && cellList.get(j).near(cellList.get(k))
                                    && cellList.get(k).near(cellList.get(i));

                            switch (connCount) {
                                case 2:
                                    connedTriplets++;
                                    closeConnedTriplets += close ? 1 : 0;
                                    break;
                                case 3:
                                    connedTriplets += 3;
                                    closeConnedTriplets += close ? 3 : 0;
                                    triangle += 3;
                                    closeTriangle += close ? 3 : 0;
                                    break;
                            }
                        }
                    }
                }
            }

        }

        CoefCalc[] c = new CoefCalc[4];
        int n = cellList.size();
        int[] breakPoints = {0, n * 1 / 10, n * 2 / 10, n * 4 / 10, n};
        for (int i = 0; i < 4; i++) {
            c[i] = new CoefCalc();
            c[i].setRange(breakPoints[i], breakPoints[i + 1]);
            c[i].start();
        }

        int[] coef = new int[4];
        int[] closeCoef = new int[4];
        try {
            for (int i = 0; i < 4; i++) {
                c[i].join();

                coef[0] += c[i].getClusterCoef()[0];
                coef[1] += c[i].getClusterCoef()[1];
                closeCoef[0] += c[i].getCloseClusterCoef()[0];
                closeCoef[1] += c[i].getCloseClusterCoef()[1];
            }

//            System.out.print(networkFactor+"\topen Cluster Coef\t" + Double.toString((double) coef[0] / coef[1]));
//            System.out.println("\tclose Cluster Coef\t" + Double.toString((double) closeCoef[0] / closeCoef[1]));
            System.out.print(String.format("%.2f", networkFactor) + "\t" + Double.toString((double) coef[0] / coef[1]));
            System.out.println("\t" + Double.toString((double) closeCoef[0] / closeCoef[1]));
            return new double[]{(double) coef[0] / coef[1], (double) closeCoef[0] / closeCoef[1]};
        } catch (InterruptedException ex) {
            System.out.println(ex.toString());
        }
        return null;
    }

    class Monitor {

        final private Map<Integer, List<int[]>> toConn;
        final private int distBinCount = 7;//Less Than 50 100 150 200 250 350 500
        final private Queue<Integer> keySet;

        public Monitor() {
            toConn = new HashMap<>();
            keySet = new LinkedList<>();
            final int preGlu = 1 << 13;
            final int postGlu = 1 << 12;
            for (int i = 0; i < distBinCount; i++) {
                keySet.add(preGlu + postGlu + i);
                keySet.add(preGlu + i);
                keySet.add(postGlu + i);
                keySet.add(i);
            }
            for (Integer key : keySet) {
                toConn.put(key, new LinkedList<int[]>());
            }

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

        public List<int[]> getList(Integer key) {
            return toConn.get(key);
        }

        public Queue<Integer> getKeySet() {
            return keySet;
        }

        public void listAll() {
//            System.out.println("List Map Sizes");
            int total = 0;
            for (Integer key : keySet) {
//                System.out.print(key + ",");
//                System.out.println(toConn.get(key).size());
                total += toConn.get(key).size();
            }
            progressUpdate("Total " + total + " possible pairs");
        }

        public void setList(Integer key, List<int[]> value) {
            toConn.put(key, value);
        }
    }

    //TODO clustering coefficient
}
