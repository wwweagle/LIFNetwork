/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lifnetwork;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.concurrent.BlockingQueue;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
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
public class chartBean extends ChartPanel implements Serializable {

    final private JFreeChart fireChart;
    final private XYSeriesCollection fireCollection;
    final private XYSeries fireSeries;
    private double currentTime = 0;
    private boolean drawing = false;
    private boolean needClear = false;
//    private boolean updating;
//    private boolean waiting = false;

    public chartBean() {
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
                    fireSeries.clear();
                }
            }
            currentTime = newTime;
            fireSeries.add(currentTime, fireQueue.remove()[1]);
        }
        fireChart.setNotify(true);
    }
}
