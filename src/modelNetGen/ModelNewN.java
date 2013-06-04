/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modelNetGen;

import iodegree.lib.CommonsLib;
import iodegree.lib.D;
import iodegree.lib.devpModel.Com;
import iodegree.lib.devpModel.ModelDB;
import iodegree.lib.devpModel.ModelType;
import iodegree.lib.devpModel.RndCell;
import iodegree.lib.devpModel.RunState;
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import javax.swing.JTextArea;
import javax.swing.Timer;
import org.apache.commons.math3.random.RandomGenerator;
import savedParameters.NetworkParameters;

/**
 *
 * @author Libra
 */
public class ModelNewN implements Runnable {

    private ArrayList<RndCell> cellList;
    private RandomGenerator r;
    int[][] monitorPairSet;
    ExecutorService es;
    private Set<Integer> connected;
    private ArrayList<HashMap<Integer, Float>> obsConnProfile;
    private HashSet<Integer> filled;
    private AtomicIntegerArray gluIn;
    private AtomicIntegerArray gluOut;
    private AtomicIntegerArray gabaIn;
    private AtomicIntegerArray gabaOut;
    private boolean DEPOLAR_GABA;
    final private float ITERATE_FACTOR;
    final private float GLU_IO_COE;
    final private float GABA_IO_COE;
    private int dim;
    private String lastUpdate = "";
    private RunState runState; //0, stopped; 1, running; 2, pendding stop; 100, finished
    private boolean lessThan300;
    private ModelType TYPE;
    private JTextArea txtProg;
    private int genMonitorTime = 20;
    final private AtomicBoolean waiting;
    private AtomicInteger cycleCount;
    private float connProbScale;
    private boolean writeFile;
    private float weightScale;

    /**
     * Build a new iterate model
     *
     * @param type Type of Model (random-dist, bi-dir, uni-dir)
     * @param depolarGABA True if GABA is depolarizing.
     * @param gluE Glutamate IO coefficient
     * @param gabaE GABA IO coefficient
     * @param iterateFactor Expected average iterate cycle
     */
    ModelNewN(float gluE, float gabaE, float iterateFactor, AtomicBoolean sema) {
        r = Com.getR();
        this.GLU_IO_COE = gluE;
        this.GABA_IO_COE = gabaE;
        this.ITERATE_FACTOR = iterateFactor;
        this.waiting = sema;
    }

    public void setType(ModelType type) {
        this.TYPE = type;
    }

    public void setLessThan300(boolean lessThan300) {
        this.lessThan300 = lessThan300;
    }

    public void setDEPOLAR_GABA(boolean DEPOLAR_GABA) {
        this.DEPOLAR_GABA = DEPOLAR_GABA;
    }

    public void setEs(ExecutorService es) {
        this.es = es;
    }

    public void setRunning() {
        runState = RunState.Running;
    }

    public boolean init() {

        if (cellList.size() < 3 || obsConnProfile.isEmpty()) {
            return false;
        }
        monitorPairSet = genPairMonitor(4, genMonitorTime);
        connected = new HashSet<>();
        runState = RunState.Running;
        return true;
    }

    public void setGenMonitorTime(int genMonitorTime) {
        this.genMonitorTime = genMonitorTime;
    }

    public void setFile(String pathToFile) {
        ModelDB db = new ModelDB(pathToFile);
        obsConnProfile = db.getPBase(lessThan300);
    }

    private int getDimension(int nCell, int density) {
        float area = (float) nCell / density;
        float d = (float) Math.sqrt(area);
        d = d * 10000;//cm to micro-m;
        return Math.round(d);
    }

    public void setCell(int nCell, int density, float gluRate) {
        dim = getDimension(nCell, density);
        cellList = new ArrayList<>();
        for (int i = 0; i < nCell; i++) {
            RndCell newCell = new RndCell(dim, gluRate);
            cellList.add(newCell);
        }
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

    public HashMap<Integer, Float> genConnProfile() {
        HashMap<Integer, Float> currConnProfile = new HashMap<>();
        HashMap<Integer, Integer> slotMap = new HashMap<>();
        HashMap<Integer, Integer> connMap = new HashMap<>();

        for (int[] pair : monitorPairSet) {
            int setKey = Com.getSetKey(pair[0], pair[1]);
            int mapKey = Com.getMapKey(pair[0], pair[1], cellList, lessThan300);

            Com.sAdd(slotMap, mapKey);
            if (connected.contains(setKey)) {
                Com.sAdd(connMap, mapKey);
            }
        }
//TEMP TEST
        Set<Map.Entry<Integer, Integer>> slots = slotMap.entrySet();
        for (Map.Entry<Integer, Integer> ent : slots) {
//            int boolFwd = (ent.getKey() >>> 13);
//            int boolRev = ((ent.getKey() & 4096) >>> 12);
//            int distZ = ent.getKey() & 4095;
            int nSlot = ent.getValue();
            int nConn = Com.sGet(connMap, ent.getKey());
            float ratio = (float) nConn / nSlot;
            currConnProfile.put(ent.getKey(), ratio);
//            D.tp(boolFwd + "," + boolRev + "," + distZ + "," + r);
        }
//        D.tp("pass");
        return currConnProfile;
    }

    public boolean fullfilled(int div, float ratio) {
//        float connRatio = ratio.length == 0 ? 0.99f : ratio[0];
//        D.tpi("enter fullfill");
//        HashMap<Integer, Float> obsConnProfile = obsConnProfile;
        HashMap<Integer, Float> genMap = genConnProfile();
        Set<Map.Entry<Integer, Float>> oriSet = obsConnProfile.get(div - 5).entrySet();

        int count = 0;
        for (Map.Entry<Integer, Float> ent : oriSet) {
            int key = ent.getKey();
            if (genMap.containsKey(key) && (genMap.get(key) >= (ent.getValue() * ratio))) {
                count++;
                filled.add(key);
                if (count >= oriSet.size()) {
//                    directUpdate(count + "/" + oriSet.size() + " catagories met");
//                    directUpdate("Generation complete.");
//                    //                    TEMP DEBUG
//                    D.tp("ratio check: div" + div);
//                    for (Map.Entry<Integer, Float> chkEnt : oriSet) {
//                        D.tp(chkEnt.getKey(), chkEnt.getValue());
//                    }
//                    D.tp("gen");
//                    Set<Map.Entry<Integer, Float>> genSet = genMap.entrySet();
//                    for (Map.Entry<Integer, Float> chkEnt : genSet) {
//                        D.tp(chkEnt.getKey(), chkEnt.getValue());
//                    }//END DEBUG

                    return true;
                }
            }
        }
        lastUpdate = count + "/" + oriSet.size() + " catagories met";
//        m0.updateProfile(gen);

//        D.tp("pass fullfill");
        return false;
    }

    private Set<int[]> genGrpMonitor(int threads, int size, int timeInS) {
        Set<int[]> monitor = Collections.newSetFromMap(new ConcurrentHashMap<int[], Boolean>());
        Set<Long> had = Collections.newSetFromMap(new ConcurrentHashMap<Long, Boolean>());
        CountDownLatch cdl = new CountDownLatch(threads);
        genGrpsClass[] genGrps = new genGrpsClass[threads];
        directUpdate("build up monitor in " + timeInS + " seconds");

        for (int i = 0; i < threads; i++) {
            genGrps[i] = new genGrpsClass(size, monitor, had, cdl);
            es.execute(genGrps[i]);
            genGrps[i].startTimer(timeInS);
        }

        try {
            cdl.await();
        } catch (InterruptedException e) {
        }
        directUpdate("Stop Signal Sent");
        directUpdate("monitor size:" + monitor.size());
        return monitor;
    }

    private int[][] genPairMonitor(int threads, int timeInS) {
        Set<int[]> monitor = Collections.newSetFromMap(new ConcurrentHashMap<int[], Boolean>());
        Set<Integer> had = Collections.newSetFromMap(new ConcurrentHashMap<Integer, Boolean>());
        CountDownLatch cdl = new CountDownLatch(threads);
        genPairsClass[] genPairs = new genPairsClass[threads];
        directUpdate("build up monitor in " + timeInS + " seconds");

        for (int i = 0; i < threads; i++) {
            genPairs[i] = new genPairsClass(monitor, had, cdl);
            es.execute(genPairs[i]);
            genPairs[i].startTimer(timeInS);
        }

        try {
            cdl.await();
        } catch (InterruptedException e) {
        }
//        directUpdate("Stop Signal Sent");
        int[][] monitorArr = new int[monitor.size() * 2][2];
        int i = 0;
        for (int[] pair : monitor) {
            monitorArr[i] = pair;
            i++;
            int[] rev = {pair[1], pair[0]};
            monitorArr[i] = rev;
            i++;
        }
        directUpdate("monitor size:" + monitor.size());
        return monitorArr;
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
            } while (!cellList.get(id1).near(cellList.get(id2), lessThan300) || id2 == id1 || had.contains(getSetKey(id1, id2)));

//            D.tp(Com.dist(id1, id2, cellList)+","+farApart(id1,id2));
            int[] rtn = {id1, id2};
            had.add(getSetKey(id1, id2));
            return rtn;
        }

        private int getSetKey(int id1, int id2) {
            return id1 < id2 ? (id1 << 12) + id2 : (id2 << 12) + id1;
        }
    }

    public void stop() {
        runState = RunState.Stoping;
    }

    public void setExit() {
        runState = RunState.Quiting;
    }

    private void directUpdate(String s) {
        if (null != txtProg) {
            txtProg.append(s + "\r\n");
        }

    }

    public void setTxtProg(JTextArea txtProg) {
        this.txtProg = txtProg;
    }

    public boolean isRunning() {
        return runState == RunState.Running;
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

    public boolean isFinished() {
        return runState == RunState.Finished;
    }

    public String getLastUpdate() {
        return lastUpdate;
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
        Set<int[]> monitorSet = genGrpMonitor(4, 3, time);
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
        D.tpi((fwdGlu ? "Glu" : "GABA") + "->" + (revGlu ? "Glu" : "GABA"));
//        D.tp("r_Wo_GNei,r_W_GNei, r_Wo_ANei,r_W_ANei");
        D.tp(r_Wo_GNei + "\t" + r_W_GNei + "\t" + r_Wo_ANei + "\t" + r_W_ANei);
    }

    public int[] probeIO(int time, boolean glu, boolean input) {
        int[] degrees = new int[4];
        Set<int[]> monitorSet = genGrpMonitor(4, 4, time);
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
//        HashSet<Long> had = new HashSet<>();
//        int[][] grpList = new int[sampleSize][size];
//        for (int i = 0; i < sampleSize; i++) {
//            grpList[i] = genRndGrp(size, had);
//        }
        Set<int[]> monitorSet = genGrpMonitor(4, size, time);
//        D.tp("mon gened");

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
             * This after is unfinished but highly usable
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
        CommonsLib.writeMatrix("cellNum" + suffix + ".csv", cellNum);


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
        CommonsLib.writeMatrix(fileName, weight);


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
            D.tp("ser io error");
        }
    }

    private float calcWeight(int sum, float avg, float scale) {
        float ceiling = 4.0f * avg;
        return sum > ceiling ? 1.5f * scale : sum / ceiling + 0.5f * scale + (float) r.nextGaussian() * 0.1f;

    }

    private void waitSema(AtomicBoolean waiting) {
        synchronized (waiting) {
            while (waiting.get()) {
                try {
                    waiting.wait();
                } catch (InterruptedException e) {
                }
            }
            waiting.set(true);
        }
    }

    private void reinit() {
        int nCell = cellList.size();
        gluIn = new AtomicIntegerArray(nCell);
        gluOut = new AtomicIntegerArray(nCell);
        gabaIn = new AtomicIntegerArray(nCell);
        gabaOut = new AtomicIntegerArray(nCell);
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
        D.tp("Glu In=====================================");
        for (int i = 0; i < gluInMap.size(); i++) {
            D.tp(i, gluInMap.get(i));
        }
        D.tp("Glu Out=====================================");
        for (int i = 0; i < gluOutMap.size(); i++) {
            D.tp(i, gluInMap.get(i));
        }
        D.tp("GABA In=====================================");
        for (int i = 0; i < gabaInMap.size(); i++) {
            D.tp(i, gluInMap.get(i));
        }
        D.tp("GABA Out=====================================");
        for (int i = 0; i < gabaOutMap.size(); i++) {
            D.tp(i, gluInMap.get(i));
        }

    }

    @Override
    public void run() {
        int step = cellList.size() >>> 5;
        do {
            waitSema(waiting);
            reinit();
            if (runState == RunState.Quiting) {
                break;
            }
            connected = Collections.newSetFromMap(new ConcurrentHashMap<Integer, Boolean>());
//            int connCount = 0;
            ForkJoinPool fjp = new ForkJoinPool();
            cycleCount = new AtomicInteger();
            cycleCount.set(0);
            for (int div = 5; div < 9; div++) {

                filled = new HashSet<>();
                directUpdate(TYPE + (DEPOLAR_GABA ? ", GABA_DEP" : ", GABA_HYP") + ", DIV" + div + " started");
                while (runState != RunState.Stoping && !fullfilled(div, connProbScale)) {
                    fjp.invoke(new newConnClass(step, div));
                }
                D.tp(cycleCount.get());
            }
            sumUp();
        } while (true);
        runState = RunState.Finished;
        directUpdate("Model exit.");
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

        directUpdate(sumGlu + " glu connections, " + sumGABA + " GABA connections.");


        D.tp("===========================================");
        D.tp(TYPE + (DEPOLAR_GABA ? ", GABA_DEP" : ", GABA_HYP") + " Finished");
        D.tp("===========================================");
        Toolkit.getDefaultToolkit().beep();
        if (writeFile) {
            writeSave(weightScale);
        }
    }

    class newConnClass extends RecursiveAction {

        int need;
        int div;

        newConnClass(int need, int div) {
            this.need = need;
            this.div = div;
        }

        @Override
        protected void compute() {
            boolean found = false;
            if (need == 1) {
                while (runState != RunState.Stoping && !found) {
//                    int maxCellId = cellList.size();
//                    int id1 = r.nextInt(maxCellId);
//                    int id2 = r.nextInt(maxCellId);
//                    if (id1 == id2 || connected.contains(Com.getKey(id1, id2))) {
//                        continue;
//                    }

                    int rnd = r.nextInt(monitorPairSet.length);
                    int id1 = monitorPairSet[rnd][0];
                    int id2 = monitorPairSet[rnd][1];
                    if (connected.contains(Com.getSetKey(id1, id2))) {
                        continue;
                    } else {
                        cycleCount.getAndIncrement();
                    }
                    found = newConnection(div, id1, id2);
                }
            } else {
                invokeAll(new newConnClass(need - 1, div), new newConnClass(1, div));
            }
        }

        boolean newConnection(int div, int id1, int id2) {//from 1 to 2

            int mapKey = Com.getMapKey(id1, id2, cellList, lessThan300);

            if (filled.contains(mapKey) || (!obsConnProfile.get(div - 5).containsKey(mapKey)) || !cellList.get(id1).near(cellList.get(id2), lessThan300)) {
                return false;
            }
            float p = obsConnProfile.get(div - 5).get(mapKey) / ITERATE_FACTOR;
            /*
             * Activity dependent connection
             */
            if (TYPE == ModelType.NetworkBiDir) {
                int gluIO = gluIn.get(id1) + gluIn.get(id2) + gluOut.get(id1) + gluOut.get(id2);
                int gabaIO = gabaIn.get(id1) + gabaIn.get(id2) + gabaOut.get(id1) + gabaOut.get(id2);

//                int gluIO = Com.sGet(gluIn, id1) + Com.sGet(gluIn, id2) + Com.sGet(gluOut, id1) + Com.sGet(gluOut, id2);
//                int gabaIO = Com.sGet(gabaIn, id1) + Com.sGet(gabaIn, id2) + Com.sGet(gabaOut, id1) + Com.sGet(gabaOut, id2);
                float gluIOFactor = gluIO * GLU_IO_COE;
                float gabaIOFactor = (DEPOLAR_GABA ? 1f : -1f) * gabaIO * GABA_IO_COE;
                float IOFactor = (gluIOFactor + gabaIOFactor) > 0 ? (gluIOFactor + gabaIOFactor) : 0;
                p *= (IOFactor + 1f);
            } else if (TYPE == ModelType.Network) {
                int gluIO = gluOut.get(id1) + gluOut.get(id2);
                int gabaIO = gabaOut.get(id1) + gabaOut.get(id2);
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
            if (p > r.nextFloat()) {
                if (cellList.get(id1).isGlu()) {
//                    Com.sAdd(gluOut, id1);
//                    Com.sAdd(gluIn, id2);
                    gluOut.incrementAndGet(id1);
                    gluIn.incrementAndGet(id2);
                } else {
//                    Com.sAdd(gabaOut, id1);
//                    Com.sAdd(gabaIn, id2);
                    gabaOut.incrementAndGet(id1);
                    gabaIn.incrementAndGet(id2);
                }

                connected.add(Com.getSetKey(id1, id2));

                return true;
            }
            return false;
        }
    }

    public void runNew() {
        int step = cellList.size() >>> 5;
        do {
            waitSema(waiting);
            reinit();
            if (runState == RunState.Quiting) {
                break;
            }
            connected = Collections.newSetFromMap(new ConcurrentHashMap<Integer, Boolean>());
//            int connCount = 0;
            ForkJoinPool fjp = new ForkJoinPool();
            cycleCount = new AtomicInteger();
            cycleCount.set(0);
            for (int div = 5; div < 9; div++) {

                filled = new HashSet<>();
                directUpdate(TYPE + (DEPOLAR_GABA ? ", GABA_DEP" : ", GABA_HYP") + ", DIV" + div + " started");
                while (runState != RunState.Stoping && !fullfilled(div, connProbScale)) {
                    fjp.invoke(new newConnClass(step, div));
                }
                D.tp(cycleCount.get());
            }
            sumUp();
        } while (true);
        runState = RunState.Finished;
        directUpdate("Model exit.");
    }
}
