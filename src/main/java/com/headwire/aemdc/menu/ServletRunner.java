package com.headwire.aemdc.menu;

import java.io.IOException;
import java.util.Properties;

import com.headwire.aemdc.command.CommandMenu;
import com.headwire.aemdc.command.CreateFileCommand;
import com.headwire.aemdc.command.ReplacePlaceHoldersCommand;
import com.headwire.aemdc.companion.Constants;
import com.headwire.aemdc.companion.Resource;
import com.headwire.aemdc.util.ConfigUtil;


/**
 * Java Servlet creator
 *
 */
public class ServletRunner extends BasisRunner {

  // Invoker
  private final CommandMenu menu = new CommandMenu();

  /**
   * Constructor
   *
   * @param params
   *          - params
   * @throws IOException
   */
  public ServletRunner(final Resource resource) throws IOException {
    // Get Config Properties from config file
    final Properties configProps = ConfigUtil.getConfigProperties();

    resource.setSourceFolderPath(configProps.getProperty(Constants.CONFIGPROP_SOURCE_SERVLETS_FOLDER));
    resource.setTargetFolderPath(configProps.getProperty(Constants.CONFIGPROP_TARGET_SERVLETS_FOLDER));

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
