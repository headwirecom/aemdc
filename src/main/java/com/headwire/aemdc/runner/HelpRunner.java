package com.headwire.aemdc.runner;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.command.CommandMenu;
import com.headwire.aemdc.command.HelpCommand;
import com.headwire.aemdc.companion.Config;
import com.headwire.aemdc.companion.Constants;
import com.headwire.aemdc.companion.Resource;
import com.headwire.aemdc.replacer.Replacer;


/**
 * Help creator
 *
 */
public class HelpRunner extends BasisRunner {

  private static final Logger LOG = LoggerFactory.getLogger(HelpRunner.class);
  private static final String HELP_FOLDER = "help";

  /**
   * Invoker
   */
  private final CommandMenu menu = new CommandMenu();
  private final Config config;

  /**
   * Constructor
   *
   * @param resource
   *          - resource object
   */
  public HelpRunner(final Resource resource, final Config config) {
    this.resource = resource;
    this.config = config;

    LOG.debug("Help runner starting...");

    // Creates Invoker object, command object and configure them
    menu.setCommand(1, new HelpCommand(resource, config));
  }

  /**
   * Run commands
   *
   * @throws IOException
   */
  @Override
  public void run() throws IOException {
    // Invoker invokes command
    menu.runCommand(1);
  }

  @Override
  public String getHelpFolder() {
    return Constants.TYPES_STATIC_FOLDER + "/" + HELP_FOLDER;
  }

  @Override
  public String getTemplateHelpFolder() {
    return getHelpFolder();
  }

  @Override
  public String getSourceFolder() {
    return resource.getSourceFolderPath();
  }

  @Override
  public Collection<File> listAvailableTemplates(final File dir) {
    return null;
  }

  @Override
  public Replacer getPlaceHolderReplacer() {
    return null;
  }
}
