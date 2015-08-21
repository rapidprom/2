package com.rapidminer.callprom;

import org.processmining.contexts.cli.CLI;
import org.processmining.contexts.cli.CLIPluginContext;
import org.processmining.framework.boot.Boot;
import org.processmining.framework.boot.Boot.Level;
import org.processmining.framework.plugin.annotations.Bootable;
import org.processmining.framework.util.CommandLineArgumentList;

public class CLI_CallProM extends CLI {
	
	@Bootable
	public Object main(CommandLineArgumentList commandlineArguments) throws Throwable {
		//try {
		if (Boot.VERBOSE != Level.NONE) {
			System.out.println("Starting script execution engine...");
			System.out.println(commandlineArguments);
		}

		CLIContextCallProM globalContext = new CLIContextCallProM();
		CLIPluginContext mainPluginContext = globalContext.getMainPluginContext();
		return mainPluginContext;
	}
}
