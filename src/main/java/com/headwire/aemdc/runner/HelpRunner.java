package com.headwire.aemdc.runner;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.command.CommandMenu;
import com.headwire.aemdc.command.HelpCommand;
import com.headwire.aemdc.companion.Resource;
import com.headwire.aemdc.replacer.Replacer;
import com.headwire.aemdc.util.HelpUtil;


/**
 * Help creator
 *
 */
public class HelpRunner extends BasisRunner {

  private static final Logger LOG = LoggerFactory.getLogger(HelpRunner.class);

  /**
   * Invoker
   */
  private final CommandMenu menu = new CommandMenu();
  private final Resource resource;

  /**
   * Constructor
   *
   * @param resource
   *          - resource object
   */
  public HelpRunner(final Resource resource) {
    this.resource = resource;

    LOG.debug("Help runner starting...");

    // Creates Invoker object, command object and configure them
    menu.setCommand("ShowHelp", new HelpCommand(resource));
  }

  /**
   * Run commands
   *
   * @throws IOException
   */
  @Override
  protected void run() throws IOException {
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

  @Override
  public boolean checkConfiguration() {
    return true;
  }

  @Override
  public Replacer getPlaceHolderReplacer() {
    return null;
  }
}
