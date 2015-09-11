package org.rapidprom.prom;

import java.io.File;

import org.processmining.contexts.cli.CLIPluginContext;
import org.processmining.framework.plugin.PluginContext;

import com.rapidminer.configuration.GlobalProMParameters;

/**
 * Singleton object that instantiates ProM such that RapidMiner operators can
 * access the plugin context.
 * 
 * @author svzelst
 *
 */
public class ProMPluginContextManager {

	protected static ProMPluginContextManager instance = null;

	protected PluginContext context = null;

	protected ProMPluginContextManager() {
		setupProMContext();
	}

	public static ProMPluginContextManager instance() {
		if (instance == null) {
			instance = new ProMPluginContextManager();
		}
		return instance;
	}

	public PluginContext getContext() {
		return context;
	}

	protected void setupProMContext() {
		File promLocation = null;
		GlobalProMParameters instance = GlobalProMParameters.getInstance();
		String promLocationStr = instance.getProMLocation();
		promLocation = new File(promLocationStr);
		CallProm tp = new CallProm();
		CLIPluginContext promContext = tp.instantiateProMContext(promLocation);
		context = promContext.createChildContext("RapidProMChildContext");
	}
}
