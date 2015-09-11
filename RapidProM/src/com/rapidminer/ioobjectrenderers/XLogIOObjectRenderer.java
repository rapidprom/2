package com.rapidminer.ioobjectrenderers;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.table.DefaultTableModel;

import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.plugins.log.ui.logdialog.LogDialogInitializer;
import org.processmining.plugins.log.ui.logdialog.SlickerOpenLogSettings;
import org.rapidprom.AbstractMultipleVisualizersRenderer;
import org.rapidprom.prom.CallProm;
import org.rapidprom.prom.ProMPluginContextManager;

import com.rapidminer.gui.renderer.DefaultComponentRenderable;
import com.rapidminer.gui.tools.ExtendedJScrollPane;
import com.rapidminer.gui.tools.ExtendedJTable;
import com.rapidminer.ioobjects.XLogIOObject;
import com.rapidminer.operator.IOContainer;
import com.rapidminer.report.Reportable;
import com.rapidminer.util.Utilities;
import com.rapidminer.util.XLogUtils;
import com.rapidminer.util.XLogUtils.ColumnNamesLog;
import com.rapidminer.util.XLogUtils.TableModelXLog;

public class XLogIOObjectRenderer extends
		AbstractMultipleVisualizersRenderer<XLogIOObjectVisualizationType> {

	public XLogIOObjectRenderer() {
		super(EnumSet.allOf(XLogIOObjectVisualizationType.class),
				"XLog renderer");
	}

	Component exampleSetComponent = null;
	Component defaultComponent = null;
	Component xDottedChartComponent = null;

	protected Component visualizeRendererOption(
			XLogIOObjectVisualizationType e, Object renderable,
			IOContainer ioContainer) {
		Component result;
		switch (e) {
		case DEFAULT:
			result = createDefaultVisualizerComponent(renderable, ioContainer);
			break;
		case EXAMPLE_SET:
		default:
			result = createExampleSetComponet(renderable, ioContainer);
			break;
		}
		return result;
	}

	protected Component createExampleSetComponet(Object renderable,
			IOContainer ioContainer) {
		if (exampleSetComponent == null) {
			XLogIOObject object = (XLogIOObject) renderable;
			final List<String> columnNames = new ArrayList<String>();
			ColumnNamesLog columnNames2 = XLogUtils.getColumnNames(object
					.getPromLog());
			columnNames.addAll(columnNames2.getAttribsTrace());
			columnNames.addAll(columnNames2.getAttribsEvents());
			try {
				TableModelXLog convertLogToStringTable = XLogUtils
						.convertLogToStringTable(object.getPromLog(), false);
				return new ExtendedJScrollPane(new ExtendedJTable(
						convertLogToStringTable, true, true));
			} catch (Exception error) {
				error.printStackTrace();
			}
			exampleSetComponent = new ExtendedJScrollPane(new ExtendedJTable(
					new DefaultTableModel(), true, true));
		}
		return exampleSetComponent;
	}

	protected Component createDefaultVisualizerComponent(Object renderable,
			IOContainer ioContainer) {
		if (defaultComponent == null) {
			try {
				CallProm tp = new CallProm();
				List<Object> parameters = new ArrayList<Object>();
				XLogIOObject logioobject = (XLogIOObject) renderable;
				XLog xLog = logioobject.getXLog();
				parameters.add(xLog);

				// get location of logDialog package
				// String path =
				// ParameterService.getParameterValue("prom_folder");
				// path = path.replace("ProM.ini", "");
				// path = path + "packages" + File.separator +
				// "logdialog-6.5.15"
				// + File.separator + "lib";
				// parameters.add(path);

				PluginContext pluginContext = logioobject.getPluginContext();
				// defaultComponent = tp.runVisualizationPlugin(pluginContext,
				// "000", parameters);
				LogDialogInitializer i = new LogDialogInitializer();
				SlickerOpenLogSettings o = new SlickerOpenLogSettings();

				defaultComponent = o.showLogVis(ProMPluginContextManager
						.instance().getContext(), xLog);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return defaultComponent;
	}

	// @Override
	// public Component getVisualizationComponent(Object renderable,
	// IOContainer ioContainer) {
	// if (renderable instanceof XLogIOObject) {
	// if (((XLogIOObject) renderable).getVisualizationType().equals(
	// VisualizationType.EXAMPLE_SET)) {
	// XLogIOObject object = (XLogIOObject) renderable;
	// final List<String> columnNames = new ArrayList<String>();
	// ColumnNamesLog columnNames2 = XLogUtils.getColumnNames(object
	// .getPromLog());
	// columnNames.addAll(columnNames2.getAttribsTrace());
	// columnNames.addAll(columnNames2.getAttribsEvents());
	// try {
	// TableModelXLog convertLogToStringTable = XLogUtils
	// .convertLogToStringTable(object.getPromLog(), false);
	// return new ExtendedJScrollPane(new ExtendedJTable(
	// convertLogToStringTable, true, true));
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return new ExtendedJScrollPane(new ExtendedJTable(
	// new DefaultTableModel(), true, true));
	// } else if (((XLogIOObject) renderable).getVisualizationType()
	// .equals(VisualizationType.X_DOTTED_CHART)) {
	// CallProm tp = new CallProm();
	// List<Object> parameters = new ArrayList<Object>();
	// XLogIOObject logioobject = (XLogIOObject) renderable;
	// XLog xLog = logioobject.getXLog();
	// parameters.add(xLog);
	//
	// PluginContext pluginContext = logioobject.getPluginContext();
	//
	// Object[] runVisualizationPlugin = tp.runPlugin(pluginContext,
	// "XX", "XDotted Chart", parameters);
	// JComponent result = (JComponent) runVisualizationPlugin[0];
	// return result;
	// } else {
	// // get the prom view
	// try {
	// CallProm tp = new CallProm();
	// List<Object> parameters = new ArrayList<Object>();
	// XLogIOObject logioobject = (XLogIOObject) renderable;
	// XLog xLog = logioobject.getXLog();
	// parameters.add(xLog);
	//
	// // get location of logDialog package
	// String path = ParameterService
	// .getParameterValue("prom_folder");
	// path = path.replace("ProM.ini", "");
	// path = path + "packages" + File.separator
	// + "logdialog-6.5.15" + File.separator + "lib";
	// parameters.add(path);
	//
	// PluginContext pluginContext = logioobject
	// .getPluginContext();
	// JComponent runVisualizationPlugin = tp
	// .runVisualizationPlugin(pluginContext, "x",
	// parameters);
	// // Object[] objs = tp.runPlugin(pluginContext, "x",
	// // "Log Summary", parameters);
	//
	// return runVisualizationPlugin;
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// return new ExtendedJScrollPane(new ExtendedJTable(
	// new DefaultTableModel(), true, true));
	// }

	public Reportable createReportable(Object renderable,
			IOContainer ioContainer, int desiredWidth, int desiredHeight) {
		JComponent panel = (JComponent) getVisualizationComponent(renderable,
				ioContainer);
		return new DefaultComponentRenderable(Utilities.getSizedPanel(panel,
				panel, desiredWidth, desiredHeight));
	}

}