/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lifnetwork;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.beans.*;
import java.io.Serializable;
import javax.swing.JPanel;

/**
 *
 * @author Libra
 */
public class FiringBean extends JPanel implements Serializable {

    private PropertyChangeSupport propertySupport;

    /*
     * Standard Bean methods
     */
    public FiringBean() {
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
    /*
     * Customized code
     */
    private Graphics2D g2d;
    final private BasicStroke basicStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
    private int canvasHeight = 1;
    private int canvasWidth = 1;
    private int cellNumber;
    private int timeFragments;
    private int LBound;
    private int RBound;
    private int UBound;
    private int BBound;


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
//        if (null == cellList || null == connected) {
//        } else {
//            drawConns();
//            drawCells();
//        }
    }

    private void setDefaultPara() {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setStroke(basicStroke);
        canvasHeight = (int) Math.round(this.getPreferredSize().getHeight());
        canvasWidth = (int) Math.round(this.getPreferredSize().getWidth());
    }

    private void drawBG() {
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, canvasWidth, canvasHeight);
    }
    
    private void drawPoint(int time, int cellNo){
        
    }
    
    private int[] convertCoord(int time, int cellNo){
        int figureWidth=canvasWidth-LBound-RBound;
        int figureHeight=canvasHeight-UBound-BBound;
        int inCanvasX=LBound+(time/timeFragments)*figureWidth;
        int inCanvasY=figureHeight-BBound-(cellNo/cellNumber)*figureHeight;
//        timeFragments
        int[] rtn={inCanvasX,inCanvasY};
        return rtn;
    }
}
