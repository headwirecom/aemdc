package com.headwire.aemc.menu;

import java.io.IOException;
import java.util.Properties;

import com.headwire.aemc.command.CommandMenu;
import com.headwire.aemc.command.CreateFileCommand;
import com.headwire.aemc.command.ReplacePlaceHoldersCommand;
import com.headwire.aemc.companion.Constants;
import com.headwire.aemc.companion.Resource;
import com.headwire.aemc.util.Utils;


/**
 * Java Model creator
 *
 */
public class ModelRunner extends BasisRunner {

  // Invoker
  private final CommandMenu menu = new CommandMenu();

  /**
   * Constructor
   *
   * @param params
   *          - params
   * @throws IOException
   */
  public ModelRunner(final Resource resource) throws IOException {
    // Get Config Properties from config file
    final Properties configProps = Utils.getConfigProperties(true);

    resource.setSourceFolderPath(configProps.getProperty(Constants.CONFIGPROP_SOURCE_MODELS_FOLDER));
    resource.setTargetFolderPath(configProps.getProperty(Constants.CONFIGPROP_TARGET_MODELS_FOLDER));

    checkConfiguration(configProps, resource);

    // Set all other config properties in the resource
    setGlobalConfigProperties(configProps, resource);
    setJavaConfigProperties(configProps, resource);

    // Creates Invoker object, command object and configure them
    menu.setCommand("CreateFile", new CreateFileCommand(resource));
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
    menu.runCommand("CreateFile");
    menu.runCommand("ReplacePlaceHolders");
  }
}
