/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modelNetGen;

import java.io.Serializable;

/**
 *
 * @author Libra
 */
public class RndCell implements Serializable {

    private final Integer x;
    private final Integer y;
    private final Boolean isGlu;
    private final Integer veryNearDist = 200;

    RndCell(int dim, float gluRate) {
        x = Com.getR().nextInt(dim);
        y = Com.getR().nextInt(dim);
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
