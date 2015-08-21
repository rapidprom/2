package com.rapidminer.callprom;

public class ReferenceMainPluginContext {
	private static ReferenceMainPluginContext instance = null;
	private CLIContextCallProM cliContextCallProm = null;
	
	private ReferenceMainPluginContext () {
		
	}
	
	public static ReferenceMainPluginContext getInstance () {
		if (instance == null) {
			instance = new ReferenceMainPluginContext();
		}
		return instance;
	}
	
	public void setCliContextCallProm(CLIContextCallProM cliContextCallProm) {
		this.cliContextCallProm = cliContextCallProm;
	}
	
	public CLIContextCallProM getCliContextCallProm() {
		return this.cliContextCallProm;
	}
}
