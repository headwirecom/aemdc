package com.headwire.aemdc.runner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.companion.Constants;
import com.headwire.aemdc.companion.Resource;
import com.headwire.aemdc.replacer.Replacer;
import com.headwire.aemdc.util.ConfigUtil;


/**
 * Basis Runner interface
 *
 */
public abstract class BasisRunner {

  private static final Logger LOG = LoggerFactory.getLogger(BasisRunner.class);

  /**
   * Invoker invokes command here
   *
   * @throws IOException
   */
  protected abstract void run() throws IOException;

  /**
   * Get help folder name for template type.
   * Help folders are under /resources/help/..
   *
   * @return help folder name
   */
  public abstract String getHelpFolder();

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
   * Any checks for configuration paths or resource
   *
   * @return true if Ok.
   */
  public abstract boolean checkConfiguration();

  /**
   * Get place holder replacer.
   *
   * @return place holder replacer
   */
  public abstract Replacer getPlaceHolderReplacer();

  /**
   * Global Run command
   *
   * @throws IOException
   *           - IOException
   *
   */
  public void globalRun() throws IOException {
    if (checkConfiguration()) {
      run();
    }
  }

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
   * Get list of all existing root directories
   *
   * @param dir
   *          - the directory to list
   * @return list of all existing templates
   */
  public Collection<File> listRootDirs(final File dir) {
    final Collection<File> fileList = new ArrayList<File>();

    for (final File file : FileUtils.listFilesAndDirs(dir, FalseFileFilter.INSTANCE, DirectoryFileFilter.INSTANCE)) {
      LOG.debug("File: {}", file);
      // get only root directories
      final String name = getTemplateName(dir, file);
      if (StringUtils.isNotBlank(name) && !name.contains("/")) {
        fileList.add(file);
      }
    }
    return fileList;
  }

  /**
   * Get template name incl. subfolders.
   *
   * @param sourceDir
   *          - source templates directory
   * @param templateFile
   *          - template file under the directory
   * @return template name
   */
  public String getTemplateName(final File sourceDir, final File templateFile) {
    // get template name incl. subfolders, for ex. "impl/SampleServiceImpl.java"
    String name = StringUtils.substringAfter(templateFile.getPath(), sourceDir.getPath());

    // convert to unix path format
    name = name.replace("\\", "/");

    // cut first slash
    if (name.indexOf("/") == 0) {
      name = name.substring(1);
    }
    LOG.debug("Template Name: {}", name);

    return name;
  }

  /**
   * Set global config properties in the resource
   *
   * @param configProps
   * @param resource
   *          - resource to set properties
   */
  public void setGlobalConfigProperties(final Properties configProps, final Resource resource) {
    // Set extentions from config file
    final String[] extentions = ConfigUtil.getConfigExtensions(configProps);
    resource.setExtentions(extentions);

    // Set overwriting methods from config file
    if (Constants.EXISTING_DESTINATION_RESOURCES_WARN
        .equals(configProps.getProperty(Constants.CONFIGPROP_EXISTING_DESTINATION_RESOURCES_REPLACEMENT))) {
      resource.setToWarnDestDir(true);
    } else if (Constants.EXISTING_DESTINATION_RESOURCES_DELETE
        .equals(configProps.getProperty(Constants.CONFIGPROP_EXISTING_DESTINATION_RESOURCES_REPLACEMENT))) {
      resource.setToDeleteDestDir(true);
    }
  }
}
