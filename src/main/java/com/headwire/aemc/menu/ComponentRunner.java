package com.headwire.aemc.menu;

import java.io.IOException;
import java.util.Properties;

import com.headwire.aemc.command.CommandMenu;
import com.headwire.aemc.command.CreateDirCommand;
import com.headwire.aemc.command.ReplacePlaceHoldersCommand;
import com.headwire.aemc.companion.Constants;
import com.headwire.aemc.companion.Resource;
import com.headwire.aemc.util.ConfigUtil;


/**
 * Component creator
 *
 */
public class ComponentRunner extends BasisRunner {

  // Invoker
  private final CommandMenu menu = new CommandMenu();

  /**
   * Constructor
   *
   * @param params
   *          - params
   * @throws IOException
   */
  public ComponentRunner(final Resource resource) throws IOException {
    // Get Config Properties from config file
    final Properties configProps = ConfigUtil.getConfigProperties(true);

    resource.setSourceFolderPath(configProps.getProperty(Constants.CONFIGPROP_SOURCE_COMPONENTS_FOLDER));
    resource.setTargetFolderPath(configProps.getProperty(Constants.CONFIGPROP_TARGET_COMPONENTS_FOLDER));

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
