package com.headwire.aemdc.runner;

import java.io.IOException;
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
  private final Config config;
  private final Replacer replacer;

  /**
   * Constructor
   *
   * @param pResource
   *          - resource object
   */
  public DynamicRunner(final Resource pResource, final Config pConfig) {
    LOG.debug("Dynamic runner for type [{}] starting...", pResource.getType());
    resource = pResource;
    config = pConfig;
    replacer = new DynamicReplacer(resource, config);

    LOG.debug("templateType: {}, templateName: {}", resource.getType(), resource.getSourceName());

    final Properties dynProps = config.getDynamicProperties(resource.getType(), resource.getSourceName());
    if (dynProps == null) {
      LOG.error("Unknown <type>=[{}] and [name]=[{}] argument.", resource.getType(), resource.getSourceName());
      return;
    }

    // set source folder path
    resource.setSourceFolderPath(dynProps.getProperty(Constants.DYN_CONFIGPROP_SOURCE_TYPE_FOLDER));

    final String targetPath = replacer
        .replacePathPlaceHolders(dynProps.getProperty(Constants.DYN_CONFIGPROP_TARGET_TYPE_FOLDER));
    if (StringUtils.isNotBlank(resource.getTempFolder())) {
      resource.setTargetFolderPath(resource.getTempFolder() + "/" + targetPath);
    } else {
      resource.setTargetFolderPath(targetPath);
    }

    // Set global config properties in the resource
    setGlobalConfigProperties(config);

    // Creates Invoker object, command object and configure them
    final String[] operations = config.getCommands(resource.getType(), resource.getSourceName());
    menu.setCommands(operations, resource, getPlaceHolderReplacer(), config);
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
    final String helpPath = config.getProperty(Constants.CONFIGPROP_SOURCE_TYPES_FOLDER) + "/"
        + resource.getType() + "/" + HELP_FOLDER;
    return helpPath;
  }

  @Override
  public String getTemplateHelpFolder() {
    String helpPath = getHelpFolder();
    if (StringUtils.isNoneBlank(resource.getSourceName())) {
      helpPath = config.getProperty(Constants.CONFIGPROP_SOURCE_TYPES_FOLDER) + "/"
          + resource.getType() + "/" + resource.getSourceName() + "/" + HELP_FOLDER;
    }
    return helpPath;
  }

  @Override
  public String getSourceFolder() {
    return resource.getSourceFolderPath();
  }

  @Override
  public Replacer getPlaceHolderReplacer() {
    return replacer;
  }
}
