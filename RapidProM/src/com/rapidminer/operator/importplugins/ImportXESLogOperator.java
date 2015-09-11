package com.rapidminer.operator.importplugins;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.rapidprom.operators.abstracts.AbstractProMOperator;
import org.rapidprom.prom.CallProm;
import org.rapidprom.prom.ProMPluginContextManager;

import com.rapidminer.ioobjectrenderers.XLogIOObjectRenderer;
import com.rapidminer.ioobjectrenderers.XLogIOObjectVisualizationType;
import com.rapidminer.ioobjects.XLogIOObject;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.UserError;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.operator.ports.metadata.GenerateNewMDRule;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeCategory;
import com.rapidminer.parameter.ParameterTypeFile;
import com.rapidminer.parameter.UndefinedParameterError;
import com.rapidminer.parameters.Parameter;
import com.rapidminer.parameters.ParameterCategory;
import com.rapidminer.tools.LogService;
import com.rapidminer.util.ProMIOObjectList;


public class ImportXESLogOperator extends AbstractProMOperator {

	public enum ImplementingPlugin {
		BUFFERED("Buffered", "Open XES Log File (Buffered)"), LIGHT_WEIGHT(
				"Lightweight",
				"Open XES Log File (Lightweight & Sequential IDs)"), MAPDB(
				"Buffered by MAPDB",
				"Open XES Log File (Disk-buffered by MapDB)"), NAIVE("Naive",
				"Open XES Log File (Naive)"), NORMAL("Normal",
				"Open XES Log File");

		private final String name;

		private final String pluginName;

		private ImplementingPlugin(final String name, final String pluginName) {
			this.name = name;
			this.pluginName = pluginName;
		}

		public String getPluginName() {
			return pluginName;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	private static final String PARAMETER_LABEL_FILENAME = "Filename";	
	private static final String PARAMETER_LABEL_IMPORTERS = "Importer";
	private static final String PARAMETER_LABEL_VISUALIZER= "Log visualizer";

	private Parameter importerParameter = null;
	private OutputPort output = getOutputPorts().createPort("Event Log (XLog)");
	private Parameter visualizerParameter = null;

	public ImportXESLogOperator(OperatorDescription description) {
		super(description);
		getTransformer().addRule(
				new GenerateNewMDRule(output, XLogIOObject.class));
	}

	protected void checkMetaData() throws UserError {
		try {
			File file = getParameterAsFile(PARAMETER_LABEL_FILENAME);

			if (!file.exists()) {
				throw new UserError(this, "301", file);
			} else if (!file.canRead()) {
				throw new UserError(this, "302", file, "");
			}
		} catch (UndefinedParameterError e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doWork() throws OperatorException {

		Logger logger = LogService.getRoot();
		logger.log(Level.INFO, "Start importing .xes log");
		long startExecution = System.currentTimeMillis();

		PluginContext context = ProMPluginContextManager.instance()
				.getContext();

		// run the plugin for loading the log
		File file = getParameterAsFile(PARAMETER_LABEL_FILENAME);
		// int fileType = getParameterAsInt(PARAMETER_IMPORTERS);
		// try to get the visualization par
		Parameter parameter1 = visualizerParameter;
		int par1int = getParameterAsInt(parameter1.getNameParameter());
		XLogIOObjectVisualizationType valPar1 = (XLogIOObjectVisualizationType) parameter1
				.getValueParameter(par1int);
		ImplementingPlugin importPlugin = (ImplementingPlugin) importerParameter
				.getValueParameter(getParameterAsInt(importerParameter
						.getNameParameter()));
		// check if file exists and is readable
		if (file == null || !file.exists()) {
			throw new UserError(this, "301", file);
		} else if (!file.canRead()) {
			throw new UserError(this, "302", file, "");
		}

		List<Object> parameters = new ArrayList<Object>();
		parameters.add(file);
		XLog promLog = null;
		CallProm tp = new CallProm();

		System.out.println(tp.toString());

		try {
			promLog = (XLog) (tp.runPlugin(context, "000",
					importPlugin.getPluginName(), parameters))[0];
		} catch (Throwable e) {
			e.printStackTrace();
			for (Object o : parameters)
				System.out.println(o.toString());
			JOptionPane
					.showMessageDialog(
							null,
							"The Log could not be read. Please have a look at the error trace. Perhaps something is wrong with the file?",
							"Read Log File Operator Error",
							JOptionPane.ERROR_MESSAGE);
		}
		// end plugin
		XLogIOObject xLogIOObject = new XLogIOObject(promLog);
		xLogIOObject.setPluginContext(context);

		xLogIOObject.setVisualizationType(valPar1);

		output.deliver(xLogIOObject);
		// add to list so that afterwards it can be cleared if needed
		ProMIOObjectList instance = ProMIOObjectList.getInstance();
		instance.addToList(xLogIOObject);
		logger.log(Level.INFO, "end do work first prom task");
	}

	@Override
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> parameterTypes = super.getParameterTypes();

		ParameterTypeFile logFileParameter = new ParameterTypeFile(
				PARAMETER_LABEL_FILENAME, "File to open", null, true, false);

		ParameterCategory importersParameterCategory = new ParameterCategory(
				EnumSet.allOf(ImplementingPlugin.class).toArray(),
				ImplementingPlugin.NAIVE, ImplementingPlugin.class,
				PARAMETER_LABEL_IMPORTERS, PARAMETER_LABEL_IMPORTERS);

		ParameterTypeCategory importersParameterTypeCategory = new ParameterTypeCategory(
				importersParameterCategory.getNameParameter(),
				importersParameterCategory.getDescriptionParameter(),
				importersParameterCategory.getOptionsParameter(),
				importersParameterCategory
						.getIndexValue(importersParameterCategory
								.getDefaultValueParameter()));

		EnumSet<XLogIOObjectVisualizationType> visualizers = EnumSet
				.allOf(XLogIOObjectVisualizationType.class);
		Object[] par1Categories = visualizers.toArray();

		ParameterCategory visualizersParameterCategory = new ParameterCategory(
				par1Categories, XLogIOObjectVisualizationType.EXAMPLE_SET,
				XLogIOObjectVisualizationType.class, "Visualize Log",
				"Visualize Log");

		ParameterTypeCategory visualizersParameterTypeCategroy = new ParameterTypeCategory(
				visualizersParameterCategory.getNameParameter(),
				visualizersParameterCategory.getDescriptionParameter(),
				visualizersParameterCategory.getOptionsParameter(),
				visualizersParameterCategory
						.getIndexValue(visualizersParameterCategory
								.getDefaultValueParameter()));

		parameterTypes.add(logFileParameter);
		parameterTypes.add(importersParameterTypeCategory);
		parameterTypes.add(visualizersParameterTypeCategroy);

		visualizerParameter = visualizersParameterCategory;
		importerParameter = importersParameterCategory;
		return parameterTypes;
	}
}
