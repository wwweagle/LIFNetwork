/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modelNetGen;

import java.io.Serializable;
//import org.apache.commons.math3.random.RandomGenerator;

/**
 *
 * @author Libra
 */
public class RndCell implements Serializable{

    private Integer x;
    private Integer y;
    private Boolean isGlu;
//    private int zoneL;
//    private int zoneR;
//    private int zoneU;
//    private int zoneD;
//    private int zoneDim = 200;
//    private int nearDist = 300;
    private final Integer veryNearDist = 200;
//    private int[] zone;
//    RandomGenerator r;
//
//    public boolean near(int[] zone) {
//        int targetX = zone[0];
//        int targetY = zone[1];
//        boolean xNear = (zoneL == targetX || zoneR == targetX);
//        boolean yNear = (zoneU == targetY || zoneD == targetY);
//        return xNear && yNear;
//    }

    RndCell(int dim, float gluRate) {
//        this.r = Com.getR();
        setRandomCoord(dim);
        setRandomGlu(gluRate);
    }

    public RndCell() {
    }
    
    

    final public void setRandomCoord(int dim) {
        x = Com.getR().nextInt(dim);
        y = Com.getR().nextInt(dim);
        //Zone Const
//        int subZoneX = x / zoneDim;
//        int subZoneY = y / zoneDim;

//        zoneL = subZoneX > 0 ? subZoneX - 1 : subZoneX;
//        zoneR = subZoneX;
//        zoneU = subZoneY > 0 ? subZoneY - 1 : subZoneY;
//        zoneD = subZoneY;
//        zone = getRndZone();
    }

    final public void setRandomGlu(float gluRate) {
        isGlu = Com.getR().nextFloat() < gluRate ? true : false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean near(RndCell c) {
        int x2 = c.getX();
        int y2 = c.getY();

        double dist = Math.sqrt((x - x2) * (x - x2) + (y - y2) * (y - y2));
        return (dist < 500);
    }

    public boolean veryNear(RndCell c) {
        int x2 = c.getX();
        int y2 = c.getY();

        double dist = Math.sqrt((x - x2) * (x - x2) + (y - y2) * (y - y2));
        return (dist < veryNearDist);
    }

    public boolean isGlu() {
        return isGlu;
    }
}
