package com.headwire.aemdc.runner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.command.CommandMenu;
import com.headwire.aemdc.companion.Config;
import com.headwire.aemdc.companion.Constants;
import com.headwire.aemdc.companion.Resource;
import com.headwire.aemdc.replacer.DynamicReplacer;
import com.headwire.aemdc.replacer.Replacer;
import com.headwire.aemdc.util.FilesDirsUtil;


/**
 * Dynamic Runner for most of types.
 *
 */
public class DynamicRunner extends BasisRunner {

  private static final Logger LOG = LoggerFactory.getLogger(DynamicRunner.class);
  private static final String HELP_FOLDER = "help";

  /**
   * Invoker
   */
  private final CommandMenu menu = new CommandMenu();
  private final Resource resource;
  private final Config config;
  private final Properties dynProps;

  /**
   * Constructor
   *
   * @param resource
   *          - resource object
   */
  public DynamicRunner(final Resource resource) {
    this.resource = resource;

    // Get Properties Config from config file
    config = new Config();

    LOG.debug("Dynamic runner for type [{}] starting...", resource.getType());

    dynProps = config.getDynamicProperties(resource.getType());

    resource.setSourceFolderPath(dynProps.getProperty(Constants.DYN_CONFIGPROP_SOURCE_TYPE_FOLDER));
    resource
        .setTargetFolderPath(dynProps.getProperty(Constants.DYN_CONFIGPROP_TARGET_TYPE_FOLDER) + getRunmodeSuffix());

    // Set global config properties in the resource
    setGlobalConfigProperties(config, resource);

    // Creates Invoker object, command object and configure them
    final String[] operations = config.getCommands(resource.getType());
    menu.setCommands(operations, resource, getPlaceHolderReplacer());
  }

  /**
   * Run commands
   *
   * @throws IOException
   */
  @Override
  public void run() throws IOException {
    // Invoker invokes commands
    menu.runCommands();
  }

  @Override
  public String getHelpFolder() {
    final String helpPath = config.getProperty(Constants.CONFIGPROP_SOURCE_TYPE_CONFIG_FOLDER) + "/"
        + resource.getType() + "/" + HELP_FOLDER;
    return helpPath;
  }

  @Override
  public String getSourceFolder() {
    return resource.getSourceFolderPath();
  }

  @Override
  public Collection<File> listAvailableTemplates(final File dir) {
    Collection<File> fileList = new ArrayList<File>();
    if (config.isDirTemplateStructure(resource.getType())) {
      fileList = FilesDirsUtil.listRootDirs(dir);
    } else {
      fileList = FilesDirsUtil.listFiles(dir);
    }
    return fileList;
  }

  @Override
  public Replacer getPlaceHolderReplacer() {
    return new DynamicReplacer(resource);
  }

  /**
   * Get runmode suffix to add to the target path
   * like ".&lt;runmode&gt;"
   *
   * @return runmode suffix
   */
  private String getRunmodeSuffix() {
    String runmode = "";
    final Map<String, String> commonJcrProps = resource.getJcrPropsSet(Constants.PLACEHOLDER_PROPS_SET_COMMON);
    if (commonJcrProps != null) {
      runmode = commonJcrProps.get(Constants.PLACEHOLDER_RUNMODE);
      if (StringUtils.isNotBlank(runmode)) {
        // add ".<runmode>" to the target path
        runmode = "." + runmode;
      } else {
        runmode = "";
      }
    }
    return runmode;
  }

}
