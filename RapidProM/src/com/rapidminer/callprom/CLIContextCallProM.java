package com.rapidminer.callprom;

import org.processmining.contexts.cli.CLIContext;
import org.processmining.contexts.cli.CLIPluginContext;

public class CLIContextCallProM extends CLIContext {
	
	protected CLIPluginContext mainPluginContext;
	
	public CLIContextCallProM() {
		super();

		mainPluginContext = new CLIPluginContext(this, "Main Plugin Context");
		ReferenceMainPluginContext instance = ReferenceMainPluginContext.getInstance();
		instance.setCliContextCallProm(this);
	}
	
	@Override
	public CLIPluginContext getMainPluginContext() {
		return mainPluginContext;
	}
	
	public void setMainPluginContext(CLIPluginContext pc) {
		this.mainPluginContext = pc;
	}

}
