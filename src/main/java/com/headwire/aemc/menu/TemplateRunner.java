package com.headwire.aemc.menu;

import java.io.IOException;

import com.headwire.aemc.command.CommandMenu;
import com.headwire.aemc.command.CreateDirCommand;
import com.headwire.aemc.command.ReplacePlaceHoldersCommand;
import com.headwire.aemc.companion.Resource;


/**
 * Template creator
 *
 */
public class TemplateRunner implements BasisRunner {

  // Invoker
  private final CommandMenu menu = new CommandMenu();

  /**
   * Constructor
   *
   * @param params
   *          - params
   */
  public TemplateRunner(final Resource resource) {
    // Creates Invoker object, command object and configure them
    menu.setCommand("CreateDir", new CreateDirCommand(resource));
    menu.setCommand("ReplacePlaceHolders", new ReplacePlaceHoldersCommand(resource));
  }

  /**
   * Run commands
   *
   * @throws IOException
   */
  @Override
  public void run() throws IOException {
    // Invoker invokes command
    menu.runCommand("CreateDir");
    menu.runCommand("ReplacePlaceHolders");
  }
}
