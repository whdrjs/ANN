package neural;
/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * -----------------
 * XYSeriesDemo.java
 * -----------------
 * (C) Copyright 2002-2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: XYSeriesDemo.java,v 1.13 2004/04/26 19:12:04 taqua Exp $
 *
 * Changes
 * -------
 * 08-Apr-2002 : Version 1 (DG);
 * 11-Jun-2002 : Inserted value out of order to see that it works (DG);
 * 11-Oct-2002 : Fixed issues reported by Checkstyle (DG);
 *
 */



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * A simple demo showing a dataset created using the {@link XYSeriesCollection} class.
 *
 */
public class XYSeriesDemo extends ApplicationFrame {

   public static BufferedReader in;   // 채팅할때 쓰는 

   public static Socket dataSocket= null ;
    /**
     * A demonstration application showing an XY series containing a null value.
     *
     * @param title  the frame title.
     */
	public XYSeries series = new XYSeries("Input1 to Hidden1");
	public XYSeries series2 = new XYSeries("Input1 to Hidden2");
	public XYSeries series3 = new XYSeries("Input1 to Hidden3");
	public XYSeries series4 = new XYSeries("Input1 to Hidden4");
	public XYSeries series5 = new XYSeries("Input2 to Hidden1");
	public XYSeries series6 = new XYSeries("Input2 to Hidden2");
	public XYSeries series7 = new XYSeries("Input2 to Hidden3");
	public XYSeries series8 = new XYSeries("Input2 to Hidden4");
	public XYSeries series9 = new XYSeries("Hidden1 to Output");
	public XYSeries series10 = new XYSeries("Hidden2 to Output");
	public XYSeries series11 = new XYSeries("Hidden3 to Output");
	public XYSeries series12 = new XYSeries("Hidden4 to Output");
    public XYSeriesDemo(final String title) {

        super(title); 
        final XYSeriesCollection data = new XYSeriesCollection();
        data.addSeries(series);
        data.addSeries(series2);
        data.addSeries(series3);
    	data.addSeries(series4);
    	data.addSeries(series5);
    	data.addSeries(series6);
    	data.addSeries(series7);
    	data.addSeries(series8);
    	data.addSeries(series9);
    	data.addSeries(series10);
    	data.addSeries(series11);
    	data.addSeries(series12);
        final JFreeChart chart = ChartFactory.createXYLineChart(
            "Film Box neural",
            "Number of Trainng", 
            "WEIGHT", 
            data,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );

        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);        
    }
    public void run() throws IOException {
    	
    }
    // ****************************************************************************
    // * JFREECHART DEVELOPER GUIDE                                               *
    // * The JFreeChart Developer Guide, written by David Gilbert, is available   *
    // * to purchase from Object Refinery Limited:                                *
    // *                                                                          *
    // * http://www.object-refinery.com/jfreechart/guide.html                     *
    // *                                                                          *
    // * Sales are used to provide funding for the JFreeChart project - please    * 
    // * support us so that we can continue developing free software.             *
    // ****************************************************************************
    
    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     * @throws IOException 
     */
}
