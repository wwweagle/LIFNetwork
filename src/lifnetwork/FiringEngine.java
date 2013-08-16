/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lifnetwork;

import java.util.ArrayList;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author Libra
 */
public class FiringEngine {

    String title = "Fire!";
    String xAxisLabel = "Time";
    String yAxisLabel = "Cell#";
    XYSeriesCollection datas = new XYSeriesCollection();
    boolean legend = false;
    boolean tooltips = false;
    boolean urls = false;
    JFreeChart chart;

    public FiringEngine(ArrayList<int[]> fireList) {
        XYSeries fires = new XYSeries("Fire!");
        for (int i = 0; i < fireList.size(); i++) {
            fires.add(fireList.get(i)[0], fireList.get(i)[1]);
        }
        datas.addSeries(fires);
        chart = ChartFactory.createScatterPlot(title, xAxisLabel, yAxisLabel, datas, PlotOrientation.HORIZONTAL, false, false, false);

    }

    public JFreeChart getChart() {
        return chart;
    }
}