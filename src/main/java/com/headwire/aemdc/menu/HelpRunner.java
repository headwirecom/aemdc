package com.headwire.aemdc.menu;

import java.io.IOException;

import com.headwire.aemdc.command.CommandMenu;
import com.headwire.aemdc.command.HelpCommand;
import com.headwire.aemdc.companion.Resource;


/**
 * Help creator
 *
 */
public class HelpRunner extends BasisRunner {

  // Invoker
  private final CommandMenu menu = new CommandMenu();

  /**
   * Constructor
   *
   * @param resource
   *          - resource object
   * @throws IOException
   */
  public HelpRunner(final Resource resource) {
    // Creates Invoker object, command object and configure them
    menu.setCommand("ShowHelp", new HelpCommand(resource));
  }

  /**
   * Run commands
   *
   * @throws IOException
   */
  @Override
  public void run() throws IOException {
    // Invoker invokes command
    menu.runCommand("ShowHelp");
  }
}
