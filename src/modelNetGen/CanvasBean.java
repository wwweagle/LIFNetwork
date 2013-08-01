/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modelNetGen;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.beans.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;
import javax.swing.JPanel;

/**
 *
 * @author Libra
 */
public class CanvasBean extends JPanel implements Serializable {

    private PropertyChangeSupport propertySupport;

    public CanvasBean() {
        propertySupport = new PropertyChangeSupport(this);

    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }
    //////////////////////////////////////////////////////////////////////////////
    //CUSTOMIZED CODE
    private Graphics2D g2d;
    final private BasicStroke basicStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
    private int canvasDim = 1;
    private int modelDim = 1;
    private float scale = 1;
    private ArrayList<RndCell> cellList;
    private Set<Integer> connected;
    private int[] outputCount;
//    private boolean drawReady;

    @Override
    public boolean isOpaque() {
        return true;
    }

    @Override
    protected void paintComponent(Graphics g) {
//        System.out.println("painted");
        g2d = (Graphics2D) g;
        setDefaultPara();
        drawBG();
        if (null == cellList || null == connected) {
        } else {
            drawConns();
            drawCells();
        }

    }

    private void setDefaultPara() {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setStroke(basicStroke);
        canvasDim = (int) Math.round(this.getPreferredSize().getWidth());
        scale = (float) canvasDim / modelDim;
    }

//    public void setDrawReady(boolean drawReady) {
//        this.drawReady = drawReady;
//    }
    public void setCellList(ArrayList<RndCell> cellList) {
        this.cellList = cellList;
//        System.out.println("Canvas noticed cell number " + cellList.size());
    }

    public void setModelDim(int dim) {
        this.modelDim = dim;
    }

    public void setConnected(Set<Integer> connected) {
        this.connected = connected;
//        System.out.println("Canvas noticed conn number " + this.connected.size());
    }

    private void drawBG() {
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, canvasDim, canvasDim);
    }

//    private void drawACellBody(int x, int y, boolean isGlu) {
//        Point2D.Double cell = new Point2D.Double(sclDn(x), sclDn(y));
//        double dd = canvasDim / 200;
//        double rr = dd / 2;
//        g2d.setColor(isGlu ? Color.RED : Color.GREEN);
//        Ellipse2D body = new Ellipse2D.Double(cell.getX() - rr, cell.getY() - rr, dd, dd);
//        g2d.draw(body);
//        g2d.fill(body);
//    }

    private void drawACellBody(int x, int y, int outputs, boolean isGlu) {
        int relativeDD = outputs / 4 + 1;
        Point2D.Double cell = new Point2D.Double(sclDn(x), sclDn(y));
        double dd = canvasDim / 400 * relativeDD;
        double rr = dd / 2;
        g2d.setColor(isGlu ? Color.RED : Color.BLUE);
        Ellipse2D body = new Ellipse2D.Double(cell.getX() - rr, cell.getY() - rr, dd, dd);
        g2d.draw(body);
        g2d.fill(body);
    }

    private void drawAConn(Point2D.Double fromPt, Point2D.Double toPt, boolean isGlu) {
//        Point2D.Double transX = biasPt(fromPt, toPt, true);
//        Point2D.Double transY = biasPt(fromPt, toPt, false);
        Point2D.Double[] trimPts = biasPt(fromPt, toPt);

        g2d.setColor(isGlu ? Color.RED : Color.BLUE);
        g2d.draw(new Line2D.Double(trimPts[0], trimPts[1]));
        g2d.setColor(isGlu ? Color.PINK : Color.CYAN);
        g2d.draw(new Line2D.Double(trimPts[1], trimPts[2]));

//        drawArrowHead(transX, transY);
    }

    private void drawCells() {   
        //TODO unnecessary check
//        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));
        if (cellList.size() > 0) {
            for (int i = 0; i < cellList.size(); i++) {
                int x = cellList.get(i).getX();
                int y = cellList.get(i).getY();
                int output = outputCount[i];
                boolean isGlu = cellList.get(i).isGlu();
                drawACellBody(x, y, output, isGlu);
            }
        }
    }

    private int sclDn(int x) {
        return Math.round(x * scale - 0.5f);
    }

    private void drawConns() {
        //TODO unnecessary check
        outputCount = new int[cellList.size()];
        float connLineWidth = canvasDim > 1500 ? canvasDim / 1500f : 1.0f;
//        System.out.println(canvasDim);
//        System.out.println(lineWidth);
        BasicStroke connStroke = new BasicStroke(connLineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
        g2d.setStroke(connStroke);
        if (connected.size()
                > 0) {
            for (Integer i : connected) {
                int id1 = i >>> 12;
                int id2 = i & 4095;
                outputCount[id1]++;
                int x1 = sclDn(cellList.get(id1).getX());
                int y1 = sclDn(cellList.get(id1).getY());
                int x2 = sclDn(cellList.get(id2).getX());
                int y2 = sclDn(cellList.get(id2).getY());
                boolean isGlu = cellList.get(id1).isGlu();

                drawAConn(new Point2D.Double(x1, y1), new Point2D.Double(x2, y2), isGlu);
            }
        }
        g2d.setStroke(basicStroke);
    }

    private enum pointType {

        ORIGIN, TARGET, MIDDLE;
    }


    private Point2D.Double[] biasPt(Point2D.Double fromPt, Point2D.Double toPt) {
        double crossD = 2;
        double trim = 2;

        double lineRad = Math.atan2(fromPt.getY() - toPt.getY(), fromPt.getX() - toPt.getX());

        double trimX = trim * Math.cos(lineRad);
        double trimY = trim * Math.sin(lineRad);

        double biasX = crossD * Math.sin(lineRad);
        double biasY = crossD * Math.cos(lineRad);

        Point2D.Double originPt, targetPt, middlePt;

        originPt = new Point2D.Double(fromPt.getX() - biasX - trimX, fromPt.getY() + biasY - trimY);

        targetPt = new Point2D.Double(toPt.getX() - biasX + trimX, toPt.getY() + biasY + trimY);

        middlePt = new Point2D.Double(originPt.getX() + (targetPt.getX() - originPt.getX()) / 2,
                originPt.getY() + (targetPt.getY() - originPt.getY()) / 2);
        Point2D.Double[] rtn = {originPt, middlePt, targetPt};
        return rtn;

    }

    private void drawArrowHead(Point2D fromPt, Point2D toPt) {

        double arrowLength = canvasDim / 200;
        double arrowDeg = 20;

        double arrowRad = arrowDeg / 180 * 3.14;
        Point2D.Double cwP, ccwP;
        double lineDeg, cwDeg, ccwDeg;

        lineDeg = Math.atan2(fromPt.getY() - toPt.getY(), fromPt.getX() - toPt.getX());
        cwDeg = lineDeg + arrowRad;
        ccwDeg = lineDeg - arrowRad;

        cwP = new Point2D.Double(toPt.getX() + arrowLength * Math.cos(cwDeg),
                toPt.getY() + arrowLength * Math.sin(cwDeg));
        ccwP = new Point2D.Double(toPt.getX() + arrowLength * Math.cos(ccwDeg),
                toPt.getY() + arrowLength * Math.sin(ccwDeg));

        g2d.draw(new Line2D.Double(toPt, cwP));
        g2d.draw(new Line2D.Double(toPt, ccwP));
    }
}
