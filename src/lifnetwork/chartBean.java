/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lifnetwork;

import java.io.Serializable;
import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
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
    private int currentIndex;

    public chartBean() {
        super(ChartFactory.createScatterPlot("Population Fire", "Time (s)", "Neuron #", (new XYSeriesCollection()), PlotOrientation.VERTICAL, false, false, false));
        fireChart = this.getChart();
        XYPlot plot = fireChart.getXYPlot();
        fireCollection = (XYSeriesCollection) plot.getDataset();
        fireSeries = new XYSeries("fires");
        currentIndex = 0;
    }

    public void updateChart(List<int[]> fireList) {
        if (fireList.size() <= currentIndex) {
            return;
        }
        for (int i = currentIndex; i < fireList.size(); i++) {
            fireSeries.add(fireList.get(i)[0], fireList.get(i)[0]);
        }
        currentIndex = fireList.size();
    }
}
