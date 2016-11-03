package com.headwire.aemc.menu;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import com.headwire.aemc.command.CommandMenu;
import com.headwire.aemc.command.CreateFileCommand;
import com.headwire.aemc.command.ReplacePlaceHoldersCommand;
import com.headwire.aemc.companion.Constants;
import com.headwire.aemc.companion.Resource;
import com.headwire.aemc.util.ConfigUtil;


/**
 * OSGI creator
 *
 */
public class OsgiRunner extends BasisRunner {

  // Invoker
  private final CommandMenu menu = new CommandMenu();

  /**
   * Constructor
   *
   * @param params
   *          - params
   * @throws IOException
   */
  public OsgiRunner(final Resource resource) throws IOException {
    // Get Config Properties from config file
    final Properties configProps = ConfigUtil.getConfigProperties(true);

    // set target folder patch
    resource.setSourceFolderPath(configProps.getProperty(Constants.CONFIGPROP_SOURCE_OSGI_FOLDER));
    final Map<String, String> commonJcrProps = resource.getJcrPropsSet(Constants.PLACEHOLDERS_PROPS_SET_COMMON);
    final String runmode = commonJcrProps.get(Constants.PARAM_RUNMODE);
    if (StringUtils.isNotBlank(runmode)) {
      // add config.<runmode> to the target path
      resource
          .setTargetFolderPath(configProps.getProperty(Constants.CONFIGPROP_TARGET_OSGI_FOLDER) + "." + runmode);
    } else {
      resource.setTargetFolderPath(configProps.getProperty(Constants.CONFIGPROP_TARGET_OSGI_FOLDER));
    }

    checkConfiguration(configProps, resource);

    // Set global config properties in the resource
    setGlobalConfigProperties(configProps, resource);

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
