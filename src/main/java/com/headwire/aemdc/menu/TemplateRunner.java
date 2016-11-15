package com.headwire.aemdc.menu;

import java.io.IOException;
import java.util.Properties;

import com.headwire.aemdc.command.CommandMenu;
import com.headwire.aemdc.command.CreateDirCommand;
import com.headwire.aemdc.command.ReplacePlaceHoldersCommand;
import com.headwire.aemdc.companion.Constants;
import com.headwire.aemdc.companion.Resource;
import com.headwire.aemdc.util.ConfigUtil;


/**
 * Template creator
 *
 */
public class TemplateRunner extends BasisRunner {

  // Invoker
  private final CommandMenu menu = new CommandMenu();

  /**
   * Constructor
   *
   * @param resource
   *          - resource object
   * @throws IOException
   *           - IOException
   */
  public TemplateRunner(final Resource resource) throws IOException {
    // Get Config Properties from config file
    final Properties configProps = ConfigUtil.getConfigProperties();

    resource.setSourceFolderPath(configProps.getProperty(Constants.CONFIGPROP_SOURCE_TEMPLATES_FOLDER));
    resource.setTargetFolderPath(configProps.getProperty(Constants.CONFIGPROP_TARGET_TEMPLATES_FOLDER));

    checkConfiguration(configProps, resource);

    // Set global config properties in the resource
    setGlobalConfigProperties(configProps, resource);

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
