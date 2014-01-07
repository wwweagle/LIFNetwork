/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lifnetwork;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.Serializable;
import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author Libra
 */
public class ChartSave extends ChartPanel implements Serializable {

    final private JFreeChart fireChart;
    final private XYSeriesCollection fireCollection;
    final private XYSeries fireSeries;

    public ChartSave() {
        super(ChartFactory.createScatterPlot("Population Fire", "Time (s)", "Neuron #", (new XYSeriesCollection()), PlotOrientation.VERTICAL, false, false, false));
        fireChart = this.getChart();
        fireChart.setNotify(false);
        XYPlot plot = fireChart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShape(0, new Rectangle2D.Double(0, 0, 1, 1));
        fireCollection = (XYSeriesCollection) plot.getDataset();
        fireSeries = new XYSeries("fires");
        fireCollection.addSeries(fireSeries);
    }

    public File updateChart(List<int[]> fireList, String pathToFile) throws Throwable {
        for (int i = 0; i < fireList.size(); i++) {
            double time = (double) fireList.get(i)[0] / 1000000;
            fireSeries.add(time, fireList.get(i)[1]);
        }
        File f = new File(pathToFile.replace("_Conn.ser", ".png"));
        saveFile(f, 1600, 900, pathToFile.replace("_Conn.ser", ""));
        fireSeries.clear();
        return f;
    }

    private void saveFile(File f, int width, int height, String title) throws Throwable {
        setTitle(title);
        ChartUtilities.saveChartAsPNG(f, fireChart, width, height);
    }

    private void setTitle(String title) {
        fireChart.setTitle(title);
    }
}
