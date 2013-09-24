/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jungClustering;

import edu.uci.ics.jung.algorithms.cluster.VoltageClusterer;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import modelNetGen.RndCell;

/**
 *
 * @author Libra
 */
public class Cluster {

    public  HashSet<HashSet<Integer>> getClusteredSets(ArrayList<RndCell> cellList, Set<Integer> connected) {
        int totalVertices = cellList.size();
        Graph<Integer, Integer> g = new DirectedSparseMultigraph<>();
        for (int i = 0; i < totalVertices; i++) {
            g.addVertex(i);
        }
        for (Integer key : connected) {
            int preID = key >>> 12;
            int postID = key & 4095;
            g.addEdge(key, preID, postID);
        }

        VoltageClusterer<Integer, Integer> vc = new VoltageClusterer(g, 20);
        Collection<Set<Integer>> clusters = vc.cluster(20);
        HashSet<HashSet<Integer>> c=new HashSet<>();
        for(Set<Integer> s:clusters){
            HashSet newSet=new HashSet<>();
            newSet.addAll(s);
            c.add(newSet);
        }
        return c;
    }
    
//    public void ebcCluser(){
        
//        EdgeBetweennessClusterer ebc = new EdgeBetweennessClusterer(5);
//        for (int j = 0; j < 4000; j += 5) {
//            Set<Set<Integer>> clusters = ebc.transform(g);
//            List<Integer> removedEdges = ebc.getEdgesRemoved();
//            for (Integer e : removedEdges) {
//                g.removeEdge(e);
//                connected.remove(e);
//            }
//    }
    
//    public void listClusters(){
//        int k = 0;
////        for (Set<Integer> s : clusters) {
////            System.out.println("Set " + k + ": " + s.size() + " cells." );
////            k++;
////        }
//        //redo CellList & Conned set here
//
//        ArrayList<RndCell> clusterdList = new ArrayList<>();
//        Set<Integer> clusteredConnects = Collections.newSetFromMap(new ConcurrentHashMap<Integer, Boolean>(10000));
//        int newIdx = 0;
//        int[] lookUpTable = new int[cellList.size()];
//        for (Set<Integer> cluster : clusters) {
//            for (Integer i : cluster) {
//                lookUpTable[i] = newIdx;
//                clusterdList.add(newIdx, cellList.get(i));
//                newIdx++;
//            }
//        }
//        cellList=clusterdList;
////        for
//        
//        
//    }
}