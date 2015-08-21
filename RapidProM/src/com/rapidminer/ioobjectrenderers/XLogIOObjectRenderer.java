package com.rapidminer.ioobjectrenderers;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;

import com.rapidminer.callprom.CallProm;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.gui.plotter.PlotterAdapter;
import com.rapidminer.gui.renderer.AbstractRenderer;
import com.rapidminer.gui.renderer.DefaultComponentRenderable;
import com.rapidminer.gui.renderer.DefaultReadable;
import com.rapidminer.gui.tools.ExtendedJScrollPane;
import com.rapidminer.gui.tools.ExtendedJTable;
import com.rapidminer.gui.viewer.MetaDataViewerTableModel;
import com.rapidminer.ioobjects.XLogIOObject;
import com.rapidminer.ioobjects.XLogIOObject.VisualizationType;
import com.rapidminer.operator.IOContainer;
import com.rapidminer.report.Reportable;
import com.rapidminer.tools.ParameterService;
import com.rapidminer.util.Utilities;
import com.rapidminer.util.XLogUtils;
import com.rapidminer.util.XLogUtils.ColumnNamesLog;
import com.rapidminer.util.XLogUtils.TableModelXLog;

public class XLogIOObjectRenderer extends AbstractRenderer{
	
	@Override
	public String getName() {
		return "ProM Log";
	}

	@Override
	public Component getVisualizationComponent(Object renderable, IOContainer ioContainer) {
		if (renderable instanceof XLogIOObject) {
			if (((XLogIOObject) renderable).getVisualizationType().equals(VisualizationType.Table)) {
				XLogIOObject object = (XLogIOObject) renderable;
				final List<String> columnNames = new ArrayList<String>();
				ColumnNamesLog columnNames2 = XLogUtils.getColumnNames(object.getPromLog());
				columnNames.addAll(columnNames2.getAttribsTrace());
				columnNames.addAll(columnNames2.getAttribsEvents());
				try {
					TableModelXLog convertLogToStringTable = XLogUtils.convertLogToStringTable(object.getPromLog(), false);
					return new ExtendedJScrollPane(new ExtendedJTable(convertLogToStringTable, true, true));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return new ExtendedJScrollPane(new ExtendedJTable(new DefaultTableModel(), true, true));
			}
			else if(((XLogIOObject) renderable).getVisualizationType().equals(VisualizationType.XDottedChart)) {
				CallProm tp = new CallProm();
				List<Object> parameters = new ArrayList<Object>();
				XLogIOObject logioobject = (XLogIOObject) renderable;
				XLog xLog = logioobject.getXLog();
				parameters.add(xLog);
				
				PluginContext pluginContext = logioobject.getPluginContext();
				
				Object[] runVisualizationPlugin = tp.runPlugin(pluginContext, "XX","XDotted Chart" , parameters);
				JComponent result = (JComponent) runVisualizationPlugin[0];
				return result;
			}
			else {
				// get the prom view
				try{
				CallProm tp = new CallProm();
				List<Object> parameters = new ArrayList<Object>();
				XLogIOObject logioobject = (XLogIOObject) renderable;
				XLog xLog = logioobject.getXLog();
				parameters.add(xLog);
				
				// get location of logDialog package
				String path = ParameterService.getParameterValue("prom_folder");
				path = path.replace("ProM.ini", "");
				path = path + "packages" + File.separator + "logdialog-6.5.15" + File.separator + "lib";
				parameters.add(path);
				
				PluginContext pluginContext = logioobject.getPluginContext();
				JComponent runVisualizationPlugin = tp.runVisualizationPlugin(pluginContext,"x",parameters);
				//Object[] objs = tp.runPlugin(pluginContext, "x", "Log Summary", parameters);
				
				return runVisualizationPlugin;
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return new ExtendedJScrollPane(new ExtendedJTable(new DefaultTableModel(), true, true));
	}

	public Reportable createReportable(Object renderable, IOContainer ioContainer, int desiredWidth, int desiredHeight) {
		JComponent panel = (JComponent) getVisualizationComponent(renderable, ioContainer);
		return new DefaultComponentRenderable(Utilities.getSizedPanel(panel,panel, desiredWidth, desiredHeight));
	}

}