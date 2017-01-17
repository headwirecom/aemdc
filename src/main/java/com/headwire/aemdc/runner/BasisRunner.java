package com.headwire.aemdc.runner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
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
   * Get list available templates in the directory.
   *
   * @return list of all existing templates
   */
  public abstract Collection<File> listAvailableTemplates(final File dir);

  /**
   * Get place holder replacer.
   *
   * @return place holder replacer
   */
  public abstract Replacer getPlaceHolderReplacer();

  /**
   * Get list of all available templates.
   *
   * @return list of all existing templates
   */
  public Collection<File> getAvailableTemplates() {
    Collection<File> fileList = new ArrayList<File>();
    final String searchPath = getSourceFolder();

    LOG.debug("Directory {}", searchPath);

    if (StringUtils.isNotBlank(searchPath)) {
      final File dir = new File(searchPath);
      if (!dir.exists()) {
        LOG.error("Can't get available templates. Directory {} doesn't exist.", searchPath);
      } else {
        if (dir.isDirectory()) {
          // find available templates
          fileList = listAvailableTemplates(dir);
        } else {
          LOG.error("Can't get available templates. The {} isn't directory.", searchPath);
        }
      }
    } else {
      LOG.error("Can't get available templates. Source directory is blank.");
    }
    return fileList;
  }

  /**
   * Set global config properties in the resource
   *
   * @param config
   *          - properties config
   * @param resource
   *          - resource to set properties
   */
  public void setGlobalConfigProperties(final Config config, final Resource resource) {
    // Set extentions from config file
    final String[] extentions = config.getFileExtensions();
    resource.setExtentions(extentions);

    // Set overwriting methods from config file
    if (Constants.EXISTING_DESTINATION_RESOURCES_WARN
        .equals(config.getProperty(Constants.CONFIGPROP_EXISTING_DESTINATION_RESOURCES_REPLACEMENT))) {
      resource.setToWarnDestDir(true);
    } else if (Constants.EXISTING_DESTINATION_RESOURCES_DELETE
        .equals(config.getProperty(Constants.CONFIGPROP_EXISTING_DESTINATION_RESOURCES_REPLACEMENT))) {
      resource.setToDeleteDestDir(true);
    }
  }
}
