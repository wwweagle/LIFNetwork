/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package matlabhelper;

import commonLibs.NetworkParameters;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;
import lifnetwork.NetworkCalc;

/**
 *
 * @author Librizzy
 */
public class Mat {

    public Float[] getWeights(String s) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(s))) {
            NetworkParameters save = (NetworkParameters) in.readObject();
            HashMap<Integer, Float> m = save.getDrivingForces();
            return m.values().toArray(new Float[m.size()]);
        } catch (ClassNotFoundException | IOException ex) {
            System.out.println(ex.toString());
        }
        return null;
    }

    public int[][] genFires(String s, int time, int gabaRevP) {
        ModelRun m = new ModelRun();
        m.setPathToFile(s);
        m.setTimeSec(time);
        m.setGABARevP(gabaRevP);
        m.run();
        List<int[]> l = m.getFireList();
        return l.toArray(new int[l.size()][]);
    }

    private class ModelRun extends Thread {

        String pathToFile;
        int timeSec = 1;
        int GABARevP = -45;
        float randProb = 0;
        int randAmp = 0;
        List<int[]> fireList;

        @Override
        public void run() {
            super.run(); //To change body of generated methods, choose Tools | Templates.
            runModel();
        }

        public void setGABARevP(int GABARevP) {
            this.GABARevP = GABARevP;
        }

        public void setTimeSec(int timeSec) {
            this.timeSec = timeSec;
        }

        public void setPathToFile(String pathToFile) {
            this.pathToFile = pathToFile;
        }

        private void runModel() {
            int timeNominal = timeSec * 1000000;
            System.out.println("Currently processing: " + pathToFile);
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(pathToFile))) {
                NetworkParameters save = (NetworkParameters) in.readObject();
                NetworkCalc network = new NetworkCalc(timeNominal, GABARevP, randProb, randAmp, save);
                network.setInjectionRatio(0);
                network.setInjectionCurrent(0);
                System.out.println("Total fires = " + network.cycle());//Actual calculation
                this.fireList = network.getFireList();
            } catch (Throwable ex) {
                System.out.println(ex.toString());
            }
        }

        public List<int[]> getFireList() {
            return fireList;
        }
    }

}
