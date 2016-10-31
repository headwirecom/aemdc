package com.headwire.aemc.command;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * Invoker class, which holds command object and invokes method
 *
 */
public class CommandMenu {

  Map<String, Command> menuItems = new HashMap<String, Command>();

  public void setCommand(final String operation, final Command cmd) {
    menuItems.put(operation, cmd);
  }

  public void runCommand(final String operation) throws IOException {
    menuItems.get(operation).execute();
  }
}
