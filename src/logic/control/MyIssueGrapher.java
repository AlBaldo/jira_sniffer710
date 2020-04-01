package logic.control;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import logic.entity.ChartData;

public class MyIssueGrapher extends JFrame{
	private double meanval;
	private double lclval;
	private double uclval;

    private static final Shape circle = new Ellipse2D.Double(-3, -3, 6, 6);
	
	TimeSeries ts;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MyIssueGrapher(String title, String yaxis, String xaxis, List<ChartData> lcd) {
		super(title);  

	    XYDataset dataset = createDataset(lcd);  

	    JFreeChart chart = ChartFactory.createTimeSeriesChart(  
	        title, // Chart  
	        xaxis, // X-Axis Label  
	        yaxis, // Y-Axis Label  
	        dataset);  

	    getDatasFromList(lcd);
	    
	    long x1 = ts.getTimePeriod(0).getFirstMillisecond();
	    long x2 = ts.getNextTimePeriod().getLastMillisecond();
	    
	    	
	    XYPlot plot = (XYPlot)chart.getPlot();
	    plot.setRenderer(new XYLineAndShapeRenderer());
	    plot.setBackgroundPaint(new Color(255,240,220));
	    
	    XYLineAnnotation line1 = new XYLineAnnotation(
	    	    x1, lclval, x2, lclval, new BasicStroke(2.0f), Color.red);
	    
	    	
		XYLineAnnotation line2 = new XYLineAnnotation(
		    x1, uclval, x2, uclval, new BasicStroke(2.0f), Color.orange);
		
		
		XYLineAnnotation line3 = new XYLineAnnotation(
			    	    x1, meanval, x2, meanval, new BasicStroke(2.0f), Color.green);

	    plot.addAnnotation(line1);
		plot.addAnnotation(line2);
		plot.addAnnotation(line3);
	    
		plot.getRenderer().setSeriesPaint(1, Color.red);
		plot.getRenderer().setSeriesPaint(2, Color.orange);
		plot.getRenderer().setSeriesPaint(3, Color.green);
		
		plot.getRenderer().setSeriesStroke(0, new BasicStroke(2.0f));
		plot.getRenderer().setSeriesStroke(1, new BasicStroke(3.0f));
		plot.getRenderer().setSeriesStroke(2, new BasicStroke(3.0f));
		plot.getRenderer().setSeriesStroke(3, new BasicStroke(3.0f));
		
		plot.getRenderer().setSeriesShape(0, circle);
		
	    ChartPanel panel = new ChartPanel(chart);
	    panel.zoomOutRange(1, 2);
	    setContentPane(panel);  
	}
	
	public void showGraph(){
		SwingUtilities.invokeLater(() -> {  
		      this.setSize(1200, 800);  
		      this.setLocationRelativeTo(null);  
		      this.setVisible(true);  
		      this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);  
		    });  
	}

	private XYDataset createDataset(List<ChartData> lcd) {
		TimeSeriesCollection dataset = new TimeSeriesCollection();  
		  
		ts = new TimeSeries("Fixed Bugs");
		
		for(ChartData cd : lcd) {
		    ts.add(new Month(cd.getMonth(), cd.getYear()), cd.getY() + 0.5232);
		}
		
	
	    dataset.addSeries(ts);
	    
	    dataset.addSeries(new TimeSeries("Lower Control Limit"));
	    dataset.addSeries(new TimeSeries("Upper Control Limit"));
	    dataset.addSeries(new TimeSeries("Mean Control Limit"));
	    
	      
	    return dataset;
	}

	private void getDatasFromList(List<ChartData> lcd) {
		double sum = 0;
		double stdd = 0;
		
		for(ChartData cd : lcd) {
			sum += cd.getY();
		}

		this.meanval = sum / (lcd.size());
		
		for(ChartData cd : lcd) {
			stdd += Math.pow((cd.getY() - this.meanval), 2);
		}
		
		stdd = Math.sqrt(stdd / lcd.size());
		
		this.uclval = this.meanval + 3 * stdd;
		this.lclval = this.meanval - 3 * stdd;

	}
}
