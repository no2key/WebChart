package com.lfx.web;

import java.util.HashMap;
import com.lfx.db.TextUtils;
import com.lfx.db.DBRowCache;
import com.lfx.db.VariableTable;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Dimension;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.view.mxGraph;

public class WebChartFlow
{

    private static int getInt(String temp,int idef)
    {
	if (temp == null)
		return idef;
	try {
		return Integer.valueOf(temp).intValue();
	}
	 catch (NumberFormatException nfe) {}
	return idef;
    }

    public static BufferedImage getFlowChart(java.awt.Color bgcolor, int width, int height, 
                         DBRowCache data, String vlabel, String elabel, String fmtcol, String fmtcol2)
    {
           HashMap objcache = new HashMap();
           VariableTable vt = new VariableTable();
	   mxGraph jgraph = new mxGraph();
	   Object parent = jgraph.getDefaultParent();
           Dimension chart_size = new Dimension(width, height);
           int posx=0, posy=0;
           String xpos=null, ypos = null;

           mxGraphComponent graphComponent = new mxGraphComponent(jgraph);

           /*
           graphComponent.getViewport().setPreferredSize(chart_size);
           graphComponent.getViewport().setViewSize(chart_size);
           graphComponent.setPreferredSize(chart_size);
           graphComponent.setSize(chart_size);
           */

           if (bgcolor != null)
               graphComponent.setBackground(bgcolor); 

           jgraph.getModel().beginUpdate();
           if (data != null)
           {
	       if (data.getRowCount() > 0 && data.getColumnCount() > 2)
               {
                    for(int i=1;i<=data.getRowCount();i++)
		    {
                        if (data.getItem(i,1) != null)
			{
			   if (!objcache.containsKey(data.getItem(i,1).toString()))
			   {
                               String objt = null;
                               if (vlabel == null)
                                   objt = data.getItem(i,1).toString();
                               else
                                   objt = data.parseString(vlabel, vt, i, 0);
                               xpos = null;
                               ypos = null;
                               if (data.getItem(i,2) != null)
                               {
                                   xpos = data.getItem(i,2).toString();
                                   if (xpos.startsWith("+"))
                                       posx = posx + getInt(xpos.substring(1),0) * width/2;
                                   else if (xpos.startsWith("-"))
                                       posx = posx - getInt(xpos.substring(1),0) * width/2;
                                   else if (xpos.startsWith("#"))
                                       posx = getInt(xpos.substring(1),0) * width/2;
                                   else
                                       posx = getInt(xpos,0);
                               }
                               else
                               {
                                   posx = posx + width/2;
                               }
                               if (data.getItem(i,3) != null)
                               {
                                   ypos = data.getItem(i,3).toString();
                                   if (ypos.startsWith("+"))
                                       posy = posy + getInt(ypos.substring(1),0) * height/2;
                                   else if (ypos.startsWith("-"))
                                       posy = posy - getInt(ypos.substring(1),0) * height/2;
                                   else if (ypos.startsWith("#"))
                                       posy = getInt(ypos.substring(1),0) * height/2;
                                   else
                                       posy = getInt(ypos,0);
                               }
                               if (fmtcol != null && data.getItem(i,fmtcol) != null)
                               {
                                   Object objv = jgraph.insertVertex(parent, 
                                                 data.getItem(i,1).toString(), objt, 
                                                 posx, posy, width,height, 
                                                 data.getItem(i,fmtcol).toString());
                                   objcache.put(data.getItem(i,1), objv);
                                   posx=posx + width;
                               }
                               else
                               {
                                   Object objv = jgraph.insertVertex(parent, 
                                                 data.getItem(i,1).toString(), objt, 
                                                 posx, posy, width,height);
                                   objcache.put(data.getItem(i,1), objv);
                                   posx=posx + width;
                               }
                            }
			}
                    }
                    for(int i=1;i<=data.getRowCount();i++)
		    {
                           if (data.getItem(i,4) != null)
			   {
                                if (objcache.containsKey(data.getItem(i,1)))
                                {
                                    if (data.getColumnCount() == 4)
                                    {
				        String targets[] = TextUtils.toStringArray(TextUtils.getWords(data.getItem(i,4).toString(), ","));
  				        for(int j=0;j<targets.length;j++)
                                        {
                                            String _vals[] = TextUtils.toStringArray(TextUtils.getWords(targets[j], "="));
                                            if (_vals.length > 1)
                                            {
                                                if (objcache.containsKey(_vals[0]))
                                                    jgraph.insertEdge(parent, null, _vals[1],
                                                           objcache.get(data.getItem(i,1)),objcache.get(_vals[0]));
                                            }
                                            else
				            {
                                                if (objcache.containsKey(targets[j]))
                                                    jgraph.insertEdge(parent, null, null,
                                                           objcache.get(data.getItem(i,1)),objcache.get(targets[j]));
				            }
                                        }
                                    }
                                    else
                                    {
                                        if (objcache.containsKey(data.getItem(i,4)))
                                        {
				           String objt=null;
                                           if (elabel != null)
                                               objt = data.parseString(elabel, vt, i, 0);
                                           else
                                           {
                                               if (data.getItem(i,5)!=null) objt = data.getItem(i,5).toString();
                                           }
                                           if (fmtcol2 != null && data.getItem(i,fmtcol2) != null)
                                           {
                                               jgraph.insertEdge(parent, null, objt,
                                                   objcache.get(data.getItem(i,1)), 
                                                   objcache.get(data.getItem(i,4)),
                                                   data.getItem(i,fmtcol2).toString());
                                           }
                                           else
                                           {
                                               jgraph.insertEdge(parent, null, objt,
                                                   objcache.get(data.getItem(i,1)), 
                                                   objcache.get(data.getItem(i,4)));
                                           }
                                        }
                                    }
                                }
			   }
                    }
               }
          }
          jgraph.getModel().endUpdate();

          BufferedImage img =  mxCellRenderer.createBufferedImage(jgraph, null, 1, graphComponent.getBackground(),
                    graphComponent.isAntiAlias(), null, graphComponent.getCanvas());

          return img;
    }

}

