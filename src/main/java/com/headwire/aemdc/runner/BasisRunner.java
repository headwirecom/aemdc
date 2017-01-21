package com.headwire.aemdc.runner;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.companion.Config;
import com.headwire.aemdc.companion.Constants;
import com.headwire.aemdc.companion.Resource;
import com.headwire.aemdc.replacer.Replacer;


/**
 * Basis Runner interface
 *
 */
public abstract class BasisRunner {

  private static final Logger LOG = LoggerFactory.getLogger(BasisRunner.class);

  protected Resource resource;

  /**
   * Invoker invokes commands here
   *
   * @throws IOException
   */
  public abstract void run() throws IOException;

  /**
   * Get help folder name for template type.
   * Help folders files are under /resources/types/<type>/help/..
   * or /aemdc-files/<type>/help/..
   *
   * @return help folder path
   */
  public abstract String getHelpFolder();

  /**
   * Get help folder name for template name.
   * Help folders files are under /aemdc-files/<type>/<template name>/help/..
   *
   * @return help folder path
   */
  public abstract String getTemplateHelpFolder();

  /**
   * Get template type source folder path.
   *
   * @return
   */
  public abstract String getSourceFolder();

  /**
   * Get place holder replacer.
   *
   * @return place holder replacer
   */
  public abstract Replacer getPlaceHolderReplacer();

  /**
   * Get resource
   *
   * @return resource
   */
  public Resource getResource() {
    return resource;
  }

  /**
   * Set global config properties in the resource
   *
   * @param config
   *          - properties config
   */
  public void setGlobalConfigProperties(final Config config) {
    // Set extentions from config file
    final String[] extentions = config.getFileExtensions();
    getResource().setExtentions(extentions);

    // Set overwriting methods from config file
    if (Constants.EXISTING_DESTINATION_RESOURCES_WARN
        .equals(config.getProperty(Constants.CONFIGPROP_EXISTING_DESTINATION_RESOURCES_REPLACEMENT))) {
      getResource().setToWarnDestDir(true);
    } else if (Constants.EXISTING_DESTINATION_RESOURCES_DELETE
        .equals(config.getProperty(Constants.CONFIGPROP_EXISTING_DESTINATION_RESOURCES_REPLACEMENT))) {
      getResource().setToDeleteDestDir(true);
    }
  }
}
