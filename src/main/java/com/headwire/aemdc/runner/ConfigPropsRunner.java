package com.headwire.aemdc.runner;

import java.io.IOException;

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
  private static final String HELP_FOLDER = "help";
  public static final String SOURCE_TYPE_FOLDER = Constants.TYPES_STATIC_FOLDER + "/" + Constants.TYPE_CONFIG_PROPS;
  public static final String SOURCE_NAME_FOLDER = SOURCE_TYPE_FOLDER + "/aemdc/files";
  public static final String CONFIG_PROPS_FILENAME = "aemdc-config.properties";

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
  public ConfigPropsRunner(final Resource resource, final Config config) {
    this.resource = resource;
    this.config = config;

    LOG.debug("AEMDC Config Properties runner starting...");

    resource.setSourceFolderPath(SOURCE_NAME_FOLDER);
    resource.setSourceName(CONFIG_PROPS_FILENAME);
    if (StringUtils.isNotBlank(resource.getTempFolder())) {
      resource.setTargetFolderPath(resource.getTempFolder());
    } else {
      resource.setTargetFolderPath(".");
    }
    resource.setTargetName(CONFIG_PROPS_FILENAME);

    // Set global config properties in the resource
    setGlobalConfigProperties(config);

    // Creates Invoker object, command object and configure them
    menu.setCommand(1, new CreateFileFromResourceCommand(resource, config));
    menu.setCommand(2, new ReplacePlaceHoldersCommand(resource, config, getPlaceHolderReplacer()));
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
    return SOURCE_TYPE_FOLDER + "/" + HELP_FOLDER;
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
  public Replacer getPlaceHolderReplacer() {
    return new ConfigPropsReplacer(resource);
  }
}
