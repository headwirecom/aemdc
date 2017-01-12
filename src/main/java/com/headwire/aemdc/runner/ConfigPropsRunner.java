package com.headwire.aemdc.runner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.command.CommandMenu;
import com.headwire.aemdc.command.CreateFileFromResourceCommand;
import com.headwire.aemdc.command.ReplacePlaceHoldersCommand;
import com.headwire.aemdc.companion.Config;
import com.headwire.aemdc.companion.Constants;
import com.headwire.aemdc.companion.Resource;
import com.headwire.aemdc.replacer.ConfigPropsReplacer;
import com.headwire.aemdc.replacer.Replacer;


/**
 * AEMDC Configuration Properties creator
 *
 */
public class ConfigPropsRunner extends BasisRunner {

  private static final Logger LOG = LoggerFactory.getLogger(ConfigPropsRunner.class);
  private static final String HELP_FOLDER = "config";

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
  public ConfigPropsRunner(final Resource resource) {
    this.resource = resource;

    // Get Properties Config from config file
    final Config config = new Config();

    LOG.debug("AEMDC Config Properties runner starting...");

    resource.setSourceFolderPath(Constants.CONFIG_PROPS_FOLDER);
    resource.setSourceName(Constants.CONFIG_PROPS_FILENAME);
    if (StringUtils.isNotBlank(resource.getTempFolder())) {
      resource.setTargetFolderPath(resource.getTempFolder());
    } else {
      resource.setTargetFolderPath(".");
    }
    resource.setTargetName(Constants.CONFIG_PROPS_FILENAME);

    // Set global config properties in the resource
    setGlobalConfigProperties(config, resource);

    // Creates Invoker object, command object and configure them
    menu.setCommand(1, new CreateFileFromResourceCommand(resource));
    menu.setCommand(2, new ReplacePlaceHoldersCommand(resource, getPlaceHolderReplacer()));
  }

  /**
   * Run commands
   *
   * @throws IOException
   */
  @Override
  public void run() throws IOException {
    // Invoker invokes command
    menu.runCommands();
  }

  @Override
  public String getHelpFolder() {
    return HELP_FOLDER;
  }

  @Override
  public String getSourceFolder() {
    return resource.getSourceFolderPath();
  }

  @Override
  public Collection<File> listAvailableTemplates(final File dir) {
    return new ArrayList<File>();
  }

  @Override
  public Replacer getPlaceHolderReplacer() {
    return new ConfigPropsReplacer(resource);
  }
}
