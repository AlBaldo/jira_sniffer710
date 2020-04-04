package logic.control;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;

import logic.entity.ChartData;

public class MyIssueGrapher{
	private double meanval;
	private double lclval;
	private double uclval;
	XYDataset dataset;
	
	
    private CrosshairOverlay crosshairOverlay = new CrosshairOverlay();
    private Crosshair xCrosshair = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
    private Crosshair yCrosshair = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));

    private static final Shape circle = new Ellipse2D.Double(-3, -3, 6, 6);
	
	TimeSeries ts;

	public MyIssueGrapher(List<ChartData> lcd) {
		this.dataset = createDataset(lcd);
		
	    getDatasFromList(lcd); //meanval, lclval, uclval

	}
	
	public void showGraph(String title, String yaxis, String xaxis) throws IOException{
		JFrame myFrame = new JFrame();
		
		JFreeChart chart = ChartFactory.createTimeSeriesChart(  
		    title, // Chart  
		    xaxis, // X-Axis Label  
		    yaxis, // Y-Axis Label  
		    dataset);  

		ChartPanel panel = new ChartPanel(chart);
		JPanel fp = new JPanel(new FlowLayout());
			
		XYPlot plot = (XYPlot)chart.getPlot();
		
		long x1 = ts.getTimePeriod(0).getFirstMillisecond();
		long x2 = ts.getNextTimePeriod().getLastMillisecond();
		
		XYLineAnnotation line1 = new XYLineAnnotation(
			    x1, lclval, x2, lclval, new BasicStroke(2.0f), Color.red);
		
			
		XYLineAnnotation line2 = new XYLineAnnotation(
		    x1, uclval, x2, uclval, new BasicStroke(2.0f), Color.orange);
		
		
		XYLineAnnotation line3 = new XYLineAnnotation(
			    	    x1, meanval, x2, meanval, new BasicStroke(2.0f), Color.green);
		
			
		plot.setRenderer(new XYLineAndShapeRenderer());
		plot.setBackgroundPaint(new Color(255,240,220));
		
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
		
		plot.setDomainPannable(true);
		plot.setRangePannable(true);
		
		
		panel.setMouseZoomable(true);
		panel.setMouseWheelEnabled(true);
		
		myFrame.setLayout(new BorderLayout());
		myFrame.getContentPane().add(panel, BorderLayout.CENTER);
		
		JPanel container = new JPanel();
		container.setBorder(BorderFactory.createTitledBorder(title));
		
		BoxLayout layout = new BoxLayout(container, BoxLayout.Y_AXIS);
		container.setLayout(layout);
		
		
		JButton[] jbtns = new JButton[4];
		
		ImageIcon icominus = new ImageIcon(ImageIO.read(getClass().getResource("/resources/images/minus_nobg_20x20.png")));
		ImageIcon icoplus = new ImageIcon(ImageIO.read(getClass().getResource("/resources/images/plus_nobg_20x20.png")));
		
		
		for(int i = 0; i < 4; i++) {
			if(i == 0) {
				fp.add(new Label("Zoom X axis:"));
			}
			if(i == 2) {
				fp.add(new Label("Zoom Y axis"));
			}
			
			jbtns[i] = new JButton();
			jbtns[i].setPreferredSize(new Dimension(25, 25));
			fp.add(jbtns[i]);
			
			if((i % 2) == 0) {
				jbtns[i].setIcon(icominus);
			}else {
				jbtns[i].setIcon(icoplus);
			}
		}
		
		jbtns[0].addActionListener(e -> panel.zoomOutDomain(1, 2));
		jbtns[1].addActionListener(e -> panel.zoomInDomain(1, 2));
		jbtns[2].addActionListener(e -> panel.zoomOutRange(1, 2));
		jbtns[3].addActionListener(e -> panel.zoomInRange(1, 2));
		
		xCrosshair.setLabelVisible(true);
		xCrosshair.setLabelGenerator(ch -> dateStrOfDoubleMillisString(ch.getValue()));
		xCrosshair.setLabelOutlineVisible(false);
		xCrosshair.setLabelBackgroundPaint(new Color(1f, 1f, 1f, 0.7f));
		xCrosshair.setLabelFont(yCrosshair.getLabelFont().deriveFont(11f));
		
		yCrosshair.setLabelVisible(true);
		yCrosshair.setLabelOutlineVisible(false);
		yCrosshair.setLabelBackgroundPaint(new Color(1f, 1f, 1f, 0.7f));
		yCrosshair.setLabelFont(yCrosshair.getLabelFont().deriveFont(11f));
		

		crosshairOverlay.addDomainCrosshair(xCrosshair);
		crosshairOverlay.addRangeCrosshair(yCrosshair);
		
		
		panel.addOverlay(crosshairOverlay);
			
		panel.addChartMouseListener(new ChartMouseListener() {
			
			@Override
			public void chartMouseMoved(ChartMouseEvent event) {
			    Rectangle2D dataArea = panel.getScreenDataArea();
			    JFreeChart chart = event.getChart();
			    XYPlot plot = (XYPlot) chart.getPlot();
			    ValueAxis xAxis = plot.getDomainAxis();
			    double x = xAxis.java2DToValue(event.getTrigger().getX(), dataArea, 
			            RectangleEdge.BOTTOM);
			    double y = DatasetUtilities.findYValue(plot.getDataset(), 0, x);

			    if(isMonthInTimeseries(x)) {
			    	getXOverlay().setValue(x);
			    	getYOverlay().setValue(Math.round(y));
			    }
			}
			
			
			@Override
			public void chartMouseClicked(ChartMouseEvent e) {
				//ignored because unnecessary
			}

		});

		
		container.setAlignmentX(Component.CENTER_ALIGNMENT);
		container.add(fp);
		
		myFrame.getContentPane().add(container, BorderLayout.SOUTH);
			
		SwingUtilities.invokeLater(() -> {
		      myFrame.setSize(1200, 800);
		      myFrame.setLocationRelativeTo(null);  
		      myFrame.setVisible(true);  
		      myFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		});  
	}

	private boolean isMonthInTimeseries(double d) {
		TimeSeriesCollection tsc = (TimeSeriesCollection) dataset;
		TimeSeries t = tsc.getSeries(0);
		Month rtp;
		
		int s = t.getTimePeriods().size();
    	
		for(int i = 0; i < s; i++) {
			rtp = (Month) t.getDataItem(i).getPeriod();
			if(dateStrOfDoubleMillisString(d).equals("1-" + rtp.getMonth() + "-" + rtp.getYearValue())) {
				return true;
			}	
		}
		return false;
	}

    private String dateStrOfDoubleMillisString(double x) {
    	long millis = Math.round(x);

    	LocalDate date =
    		    Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate();
    	
    	return date.getDayOfMonth() + "-" + date.getMonthValue() + "-" + date.getYear();
    }
	
	private Crosshair getYOverlay() {
		return yCrosshair;
	}
	
	private Crosshair getXOverlay() {
		return xCrosshair;
	}

	private XYDataset createDataset(List<ChartData> lcd) {
		TimeSeriesCollection tsc = new TimeSeriesCollection();  
		  
		ts = new TimeSeries("Fixed Bugs");
		
		for(ChartData cd : lcd) {
		    ts.add(new Month(cd.getMonth(), cd.getYear()), cd.getY());
		}
		
	
	    tsc.addSeries(ts);
	    
	    tsc.addSeries(new TimeSeries("Lower Control Limit"));
	    tsc.addSeries(new TimeSeries("Upper Control Limit"));
	    tsc.addSeries(new TimeSeries("Mean Control Limit"));
	    
	      
	    return tsc;
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
