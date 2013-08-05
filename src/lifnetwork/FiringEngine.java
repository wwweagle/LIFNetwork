/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lifnetwork;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

/**
 *
 * @author Libra
 */
public class FiringEngine {

    String title = "Fire!";
    String xAxisLabel = "Time";
    String yAxisLabel = "Cell#";
    TimeSeriesCollection datas = new TimeSeriesCollection();
    boolean legend = false;
    boolean tooltips = false;
    boolean urls = false;
    JFreeChart chart;

    public FiringEngine() {
        TimeSeries s = new TimeSeries("fires");
        s.add(new FixedMillisecond(0), 2);
        s.add(new FixedMillisecond(2), 4);
        s.add(new FixedMillisecond(4), 6);
        s.add(new FixedMillisecond(6), 8);
        datas.addSeries(s);
        chart = ChartFactory.createScatterPlot(title, xAxisLabel, yAxisLabel, datas, PlotOrientation.HORIZONTAL, false, false, false);
    }

    public JFreeChart getChart() {
        return chart;
    }
    
}