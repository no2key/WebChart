/* ===============
 * Eastwood Charts
 * ===============
 *
 * (C) Copyright 2007, 2008, by Object Refinery Limited.
 *
 * Project Info:  http://www.jfree.org/eastwood/index.html
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 * ------------
 * GXYPlot.java
 * ------------
 * (C) Copyright 2007, 2008, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 *
 * Changes
 * -------
 * 13-Dec-2007 : Version 1 (DG);
 * 30-Jun-2008 : Added support for specifying the step size to use for
 *               grid lines (NT);
 *
 */

package com.lfx.web;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Iterator;
import java.awt.Composite;
import java.awt.AlphaComposite;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.axis.ValueTick;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.axis.TickType;




/**
 * A custom plot class (adds support for drawing background gradients and
 * specyfing the step size to use for grid lines).
 */
class WebChartXYPlot extends XYPlot {

   /**
     * Draws the gridlines for the plot, if they are visible.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param ticks  the ticks.
     *
     * @see #drawRangeGridlines(Graphics2D, Rectangle2D, List)
     */

    protected void drawRangeGridlines(Graphics2D g2, Rectangle2D dataArea, List ticks)
   {
		Composite oldcomp = g2.getComposite();
                Paint bandPaint = getBackgroundPaint();
                if (bandPaint != null && bandPaint instanceof java.awt.Color) 
		{
		    // g2.setComposite(AlphaComposite.SrcO);
		    java.awt.Color bandcolor = (java.awt.Color)(bandPaint);
                    boolean fillBand = false;
                    ValueAxis axis = getRangeAxis();
                    double previous = axis.getLowerBound();
                    Iterator iterator = ticks.iterator();
                    while (iterator.hasNext()) {
                        ValueTick tick = (ValueTick) iterator.next();
			if (!tick.getTickType().equals(TickType.MAJOR)) continue;
                        double current = tick.getValue();
			double y1 = axis.valueToJava2D(previous, dataArea, getRangeAxisEdge());
			double y2 = axis.valueToJava2D(current, dataArea, getRangeAxisEdge());
			Rectangle2D band = new Rectangle2D.Double(dataArea.getMinX(), y2, dataArea.getWidth(), y1 - y2);
                        if (fillBand) 
                            g2.setPaint(bandcolor);
			else
                            g2.setPaint(bandcolor.darker());
                        g2.fill(band);
                        previous = current;
                        fillBand = !fillBand;
                    }
                    double end = axis.getUpperBound();
		    double y1 = axis.valueToJava2D(previous, dataArea, getRangeAxisEdge());
		    double y2 = axis.valueToJava2D(end, dataArea, getRangeAxisEdge());
		    Rectangle2D band = new Rectangle2D.Double(dataArea.getMinX(), y2, dataArea.getWidth(), y1 - y2);
                    if (fillBand) 
                            g2.setPaint(bandcolor);
		    else
                            g2.setPaint(bandcolor.darker());
                    g2.fill(band);
                }
		else
		{
		    super.drawRangeGridlines(g2,dataArea,ticks);
		}
		g2.setComposite(oldcomp);
    }

    /**
     * Draws the gridlines for the plot's primary range axis, if they are
     * visible.
     *
     * @param g2  the graphics device.
     * @param area  the data area.
     * @param ticks  the ticks.
     *
     * @see #drawDomainGridlines(Graphics2D, Rectangle2D, List)
     */
    protected void drawDomainGridlines(Graphics2D g2, Rectangle2D dataArea, List ticks)
   {
		Composite oldcomp = g2.getComposite();
                Paint bandPaint = getBackgroundPaint();
                if (bandPaint != null && bandPaint instanceof java.awt.Color) 
		{
		    g2.setComposite(AlphaComposite.SrcIn);
		    java.awt.Color bandcolor = (java.awt.Color)(bandPaint);
                    boolean fillBand = true;
                    ValueAxis axis = getDomainAxis();
                    double previous = axis.getLowerBound();
                    Iterator iterator = ticks.iterator();
                    while (iterator.hasNext()) {
                        ValueTick tick = (ValueTick) iterator.next();
			if (!tick.getTickType().equals(TickType.MAJOR)) continue;
                        double current = tick.getValue();
			double y1 = axis.valueToJava2D(previous, dataArea, getDomainAxisEdge());
			double y2 = axis.valueToJava2D(current, dataArea, getDomainAxisEdge());
			Rectangle2D band = new Rectangle2D.Double(y1, dataArea.getMinY(), y2-y1, dataArea.getWidth());
                        if (fillBand) 
                            g2.setPaint(bandcolor);
			else
                            g2.setPaint(bandcolor.darker());
                        g2.fill(band);
                        previous = current;
                        fillBand = !fillBand;
                    }
                    double end = axis.getUpperBound();
		    double y1 = axis.valueToJava2D(previous, dataArea, getDomainAxisEdge());
		    double y2 = axis.valueToJava2D(end, dataArea, getDomainAxisEdge());
		    Rectangle2D band = new Rectangle2D.Double(y1, dataArea.getMinY(), y2-y1, dataArea.getWidth());
                    if (fillBand) 
                            g2.setPaint(bandcolor);
		    else
                            g2.setPaint(bandcolor.darker());
                    g2.fill(band);
                }
		else
		{
		    super.drawDomainGridlines(g2,dataArea,ticks);
		}
		g2.setComposite(oldcomp);
    }
}
