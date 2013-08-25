/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lifnetwork;

import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.FastScatterPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author Libra
 */
public class FiringEngine {

    private List<int[]> fireList;
    final private String chartTitle = "Population Fire";
    final private String xAxisLabel = "Time (s)";
    final private String yAxisLabel = "Cell #";
    private JFreeChart chart;

    public FiringEngine() {
    }

    public void setFireList(List<int[]> fireList) {
        this.fireList = fireList;
    }

    public JFreeChart getScatterPlot() {
        XYSeries fires=new XYSeries(chartTitle);
        for (int i = 0; i < fireList.size(); i++) {
            fires.add(fireList.get(i)[0], fireList.get(i)[1]);
        }
        XYSeriesCollection collection = new XYSeriesCollection(fires);
        boolean legend = false;
        boolean tooltips = false;
        boolean urls = false;
        chart= ChartFactory.createScatterPlot(chartTitle, xAxisLabel, yAxisLabel, collection, PlotOrientation.VERTICAL, legend, tooltips, urls);
        return chart;
    }

    public JFreeChart getFastScatterPlot() {
        float[][] data = new float[2][fireList.size()];
        for (int i = 0; i < fireList.size(); i++) {
            data[0][i] = (float) fireList.get(i)[0] / 1000000;
            data[1][i] = (float) fireList.get(i)[1];
        }
        NumberAxis domainAxis = new NumberAxis(xAxisLabel);
        NumberAxis rangeAxis = new NumberAxis(yAxisLabel);
        FastScatterPlot fs = new FastScatterPlot(data, domainAxis, rangeAxis);
        fs.setDomainGridlinesVisible(false);
        fs.setRangeGridlinesVisible(false);
        chart = new JFreeChart(chartTitle, fs);
        return chart;
    }

    public JFreeChart getChart() {
        return getScatterPlot();
    }
}