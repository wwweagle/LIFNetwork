/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package commonLibs;

import java.io.Serializable;
import modelNetGen.Com;

/**
 *
 * @author Libra
 */
final public class RndCell implements Serializable {

    private final Integer x;
    private final Integer y;
    private final Boolean isGlu;
    private final Integer veryNearDist = 200;

    public RndCell(int dim, float gluRate) {
        x = Com.getR().nextInt(dim);
        y = Com.getR().nextInt(dim);
        isGlu = Com.getR().nextFloat() < gluRate;
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
        return (dist < 300);
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
