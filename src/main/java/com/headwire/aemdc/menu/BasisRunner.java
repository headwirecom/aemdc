package com.headwire.aemdc.menu;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.companion.Constants;
import com.headwire.aemdc.companion.Resource;
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
  public abstract void run() throws IOException;

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
   * Get list of all available templates.
   * 
   * @return list of all existing templates
   */
  public Collection<File> getAvailableTemplates() {
    Collection<File> fileList = new ArrayList<File>();
    final String searchPath = getSourceFolder();
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
      // get only root directories
      final String name = getTemplateName(dir.getPath(), file.getPath());
      if (StringUtils.isNotBlank(name) && !name.contains("/")) {
        fileList.add(file);
      }
    }
    return fileList;
  }

  /**
   * Get template name incl. subfolders.
   *
   * @param sourceDirPath
   *          - source templates directory path
   * @param templateFilePath
   *          - template file path under the directory
   * @return template name
   */
  public String getTemplateName(final String sourceDirPath, final String templateFilePath) {
    // get template name incl. subfolders, for ex. "impl/SampleServiceImpl.java"
    String name = StringUtils.substringAfter(sourceDirPath, templateFilePath);

    // convert to unix path format
    name = name.replace("\\", "/");

    // cut first slash
    if (name.indexOf("/") == 0) {
      name = name.substring(1);
    }
    return name;
  }

  /**
   * @param configProps
   * @param resource
   *          - resource with properties
   * @return true if Ok.
   */
  public boolean checkConfiguration(final Properties configProps, final Resource resource) {
    final String targetPath = resource.getTargetFolderPath();

    if (Constants.TYPE_APPS_UI_LIST.contains(resource.getType())) {
      // get target project jcr path
      final String targetProjectRoot = configProps.getProperty(Constants.CONFIGPROP_TARGET_PROJECT_ROOT);
      final int pos = targetPath.indexOf(targetProjectRoot);
      if (pos == -1) {
        throw new IllegalStateException("The target project root jcr path " + Constants.CONFIGPROP_TARGET_PROJECT_ROOT
            + " is different to target path  " + targetPath + " in the config file.");
      }
    } else if (Constants.TYPE_CONF_UI_LIST.contains(resource.getType())) {
      // get target UI folder
      final String targetUIFolder = configProps.getProperty(Constants.CONFIGPROP_TARGET_UI_FOLDER);
      final int pos = targetPath.indexOf(targetUIFolder);
      if (pos == -1) {
        throw new IllegalStateException("The target UI folder " + Constants.CONFIGPROP_TARGET_UI_FOLDER
            + " is different to target path " + targetPath + " in the config file.");
      }
    } else if (Constants.TYPE_CORE_LIST.contains(resource.getType())) {
      // get target java source folder
      final String targetJavaSrcFolder = configProps.getProperty(Constants.CONFIGPROP_TARGET_JAVA_FOLDER);
      final int pos = targetPath.indexOf(targetJavaSrcFolder);
      if (pos == -1) {
        throw new IllegalStateException(
            "The target java source folder " + Constants.CONFIGPROP_TARGET_JAVA_FOLDER
                + " is different to target path " + targetPath + " in the config file.");
      }
    } else {
      throw new IllegalStateException("The type " + resource.getType() + " is not defined");
    }
    return true;
  }

  /**
   * Set global config properties in the resource
   *
   * @param configProps
   * @param resource
   *          - resource to set properties
   * @throws IOException
   */
  public void setGlobalConfigProperties(final Properties configProps, final Resource resource) throws IOException {
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

  /**
   * @param configProps
   * @param resource
   *          - resource to set properties
   * @return true if Ok
   */
  public void setJavaConfigProperties(final Properties configProps, final Resource resource) {
    // set java class name
    final String javaClassName = FilenameUtils.getBaseName(resource.getTargetName());
    resource.setJavaClassName(javaClassName);

    // set java class package
    final String javaClassFileName = FilenameUtils.getName(resource.getTargetName());
    final String targetPath = resource.getTargetFolderPath() + "/" + resource.getTargetName();
    final String targetJavaSrcFolder = configProps.getProperty(Constants.CONFIGPROP_TARGET_JAVA_FOLDER);

    // cut java file name, replace "/" with "."
    String javaPackage = StringUtils.substringAfter(targetPath, targetJavaSrcFolder + "/");
    javaPackage = StringUtils.substringBefore(javaPackage, "/" + javaClassFileName);
    javaPackage = StringUtils.replace(javaPackage, "/", ".");
    resource.setJavaClassPackage(javaPackage);
  }
}
