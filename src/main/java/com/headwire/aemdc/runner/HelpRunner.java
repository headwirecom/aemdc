package com.headwire.aemdc.runner;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import com.headwire.aemdc.command.CommandMenu;
import com.headwire.aemdc.command.HelpCommand;
import com.headwire.aemdc.companion.Resource;
import com.headwire.aemdc.util.HelpUtil;


/**
 * Help creator
 *
 */
public class HelpRunner extends BasisRunner {

  /**
   * Invoker
   */
  private final CommandMenu menu = new CommandMenu();
  private Resource resource;

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

  @Override
  public String getHelpFolder() {
    return HelpUtil.HELP_COMMON_FOLDER;
  }

  @Override
  public String getSourceFolder() {
    // return HELP_ROOT_FOLDER;
    return resource.getSourceFolderPath();
  }

  @Override
  public Collection<File> listAvailableTemplates(final File dir) {
    return null;
  }
}
