/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lifnetwork;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author Libra
 */
public class ChartBean extends ChartPanel implements Serializable {

    final private JFreeChart fireChart;
    final private XYSeriesCollection fireCollection;
    final private XYSeries fireSeries;
    private double currentTime = 0;
    private boolean drawing = false;
    private boolean needClear = false;
//    private boolean updating;
//    private boolean waiting = false;

    public ChartBean() {
        super(ChartFactory.createScatterPlot("Population Fire", "Time (s)", "Neuron #", (new XYSeriesCollection()), PlotOrientation.VERTICAL, false, false, false));
        fireChart = this.getChart();
        fireChart.addProgressListener(this);
        XYPlot plot = fireChart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShape(0, new Rectangle2D.Double(0, 0, 1, 1));
        fireCollection = (XYSeriesCollection) plot.getDataset();
        fireSeries = new XYSeries("fires");
        fireCollection.addSeries(fireSeries);
        currentTime = 0;

    }

    @Override
    public void chartProgress(ChartProgressEvent event) {
        if (event.getType() == ChartProgressEvent.DRAWING_FINISHED) {
            if (needClear) {
                saveFile("temp.png", 500, 500, "test");
                fireSeries.clear();
                needClear = false;
                currentTime = 0;
            }
            drawing = false;
        } else if (event.getType() == ChartProgressEvent.DRAWING_STARTED) {
            drawing = true;
        }
    }

    public void updateChart(BlockingQueue<int[]> fireQueue) {
        fireChart.setNotify(false);
        while (!fireQueue.isEmpty()) {
            double newTime = (double) fireQueue.peek()[0] / 1000000;
            if (newTime < currentTime) {
                if (drawing) {
                    needClear = true;
                    break;
                } else {
                    saveFile("temp.png", 500, 500, "test");
                    fireSeries.clear();
                }
            }
            currentTime = newTime;
            fireSeries.add(currentTime, fireQueue.remove()[1]);
        }
        fireChart.setNotify(true);
    }

    public void updateChart(List<int[]> fireList, String pathToFile) {
        fireChart.setNotify(false);
//        while (!fireList.isEmpty()) {
//            double newTime = (double) fireList.peek()[0] / 1000000;
//            if (newTime < currentTime) {
//                if (drawing) {
//                    needClear = true;
//                    break;
//                } else {
//                    saveFile("temp.png", 500, 500, "test");
//                    fireSeries.clear();
//                }
//            }
//            currentTime = newTime;
//            fireSeries.add(currentTime, fireList.remove()[1]);
//        }
        for (int i = 0; i < fireList.size(); i++) {
            double time = (double) fireList.get(i)[0] / 1000000;
            fireSeries.add(time, fireList.get(i)[1]);
        }
        fireChart.setNotify(true);
        saveFile(pathToFile.replace("_Conn.ser", ".png"), 1600, 900, pathToFile.replace("_Conn.ser", ""));
        

    }

    private void saveFile(String pathToFile, int width, int height, String title) {
        try {
            setTitle(title);
            File f = new File(pathToFile);
            ChartUtilities.saveChartAsPNG(f, fireChart, width, height);
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
    }

    private void setTitle(String title) {
        fireChart.setTitle(title);
    }
}
