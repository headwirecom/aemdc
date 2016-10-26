package com.headwire.aemc.command;

import java.util.HashMap;
import java.util.Map;

/**
 * Invoker class, which holds command object and invokes method
 *
 */
public class Menu {
	Map<String, Command> menuItems = new HashMap<String, Command>();

	public void setCommand(String operation, Command cmd) {
		menuItems.put(operation, cmd);
	}

	public void runCommand(String operation) {
		menuItems.get(operation).execute();
	}
}
