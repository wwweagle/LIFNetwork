/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modelNetGen;

import java.util.ArrayList;
import java.util.Map;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.SynchronizedRandomGenerator;
import org.apache.commons.math3.random.Well44497b;

/**
 *
 * @author Libra
 */
public class Com {

    static final private int[] bins = {50, 100, 150, 200, 250, 350};
    static private RandomGenerator r = new SynchronizedRandomGenerator(new Well44497b());

    static private int getBin(int n, boolean lessThan300) {
        if (lessThan300) {
            return n < 250 ? n / 50 : 5;
        } else {
            for (int i = 0; i < bins.length; i++) {
                if (n < bins[i]) {
                    return i;
                }
            }
            return bins.length;
        }
//        return n < 250 ? n / 50 : 5;

    }

    static public RandomGenerator getR() {
        return r;
    }

    static public int getSetKey(int preId, int postID) {
        return (preId << 12) + postID;
    }

    static public int getMapKey(boolean fwdGlu, boolean revGlu, int dist, boolean lessThan300) {
        int key = 0;
        int bin = getBin(dist, lessThan300);
//        int type = (fwdGlu ? 0 : 1) + (revGlu ? 0 : 1);
//        key += type << 12;
        key += ((fwdGlu ? 0 : 1) << 13);
        key += ((revGlu ? 0 : 1) << 12);
        key += bin;
        return key;
    }

    static public int getMapKey(int id1, int id2, ArrayList<RndCell> cellList, boolean lessThan300) {
        boolean glu1 = cellList.get(id1).isGlu();
        boolean glu2 = cellList.get(id2).isGlu();
        return getMapKey(glu1, glu2, dist(id1, id2, cellList), lessThan300);
    }

    static public int dist(int id1, int id2, ArrayList<RndCell> cellList) {
        RndCell cell1 = cellList.get(id1);
        RndCell cell2 = cellList.get(id2);
        int x1 = cell1.getX();
        int y1 = cell1.getY();
        int x2 = cell2.getX();
        int y2 = cell2.getY();
        int dx = x1 - x2;
        int dy = y1 - y2;
        int dist = (int) Math.round(Math.sqrt(dx * dx + dy * dy) - 0.5);
        return dist;
    }

    static public int sGet(Map<Integer, Integer> map, Integer id) {
        return map.containsKey(id) ? map.get(id) : 0;
    }

    static public void sAdd(Map<Integer, Integer> map, Integer id) {
        if (map.containsKey(id)) {
            int value = map.get(id) + 1;
            map.put(id, value);
        } else {
            map.put(id, 1);
        }
    }

    static public int[] getIDsFromSetKey(int key) {
        int preID = key >>> 12;
        int postID = key & 4095;
        int[] rtn = {preID, postID};
        return rtn;
    }
//
//    static public int sGet(ConcurrentHashMap<Integer, Integer> map, Integer id) {
//        return map.containsKey(id) ? map.get(id) : 0;
//    }
//
//    static public void sAdd(ConcurrentHashMap<Integer, Integer> map, Integer id) {
//        if (map.containsKey(id)) {
//            int value = map.get(id) + 1;
//            map.put(id, value);
//        } else {
//            map.put(id, 1);
//        }
//    }
}
