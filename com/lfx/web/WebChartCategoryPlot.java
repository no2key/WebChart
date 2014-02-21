package com.lfx.web;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.util.List;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.axis.Tick;

class WebChartCategoryPlot extends CategoryPlot {

    protected void drawRangeGridlines(Graphics2D g2, Rectangle2D dataArea, List ticks)
   {
        double y = 0;
        if (isRangeGridlinesVisible())
 	{
            Stroke gridStroke = getRangeGridlineStroke();
            Paint gridPaint = getRangeGridlinePaint();
            if ((gridStroke != null) && (gridPaint != null)) {
                ValueAxis axis = getRangeAxis();
                if (axis != null) {
		    java.util.Iterator iter = ticks.iterator();
                    while (iter.hasNext())
		    {
			Tick tmptick = (Tick)(iter.next());
			y = tmptick.getAngle();
                        Paint paint = gridPaint;
                        try {
                            setRangeGridlinePaint(paint);
                            getRenderer().drawRangeGridline(g2, this,
                            		getRangeAxis(), dataArea, y);
                        }
                        finally {
                            setRangeGridlinePaint(gridPaint);
                        }
                    }
                }
            }
        }
    }
}
