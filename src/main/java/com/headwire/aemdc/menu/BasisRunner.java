package com.headwire.aemdc.menu;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import com.headwire.aemdc.companion.Constants;
import com.headwire.aemdc.companion.Resource;
import com.headwire.aemdc.util.ConfigUtil;


/**
 * Basis Runner interface
 *
 */
public abstract class BasisRunner {

  public abstract void run() throws IOException;

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
      final String targetProjectJcrPath = configProps.getProperty(Constants.CONFIGPROP_TARGET_PROJECT_ROOT);
      final int pos = targetPath.indexOf(targetProjectJcrPath);
      if (pos == -1) {
        throw new IllegalStateException("The target project jcr path " + Constants.CONFIGPROP_TARGET_PROJECT_ROOT
            + " is different to " + targetPath + " in the config file.");
      }
    } else if (Constants.TYPE_CORE_LIST.contains(resource.getType())) {
      // get target java source folder
      final String targetJavaSrcFolder = configProps.getProperty(Constants.CONFIGPROP_TARGET_JAVA_FOLDER);
      final int pos = targetPath.indexOf(targetJavaSrcFolder);
      if (pos == -1) {
        throw new IllegalStateException(
            "The target java source folder " + Constants.CONFIGPROP_TARGET_JAVA_FOLDER
                + " is different to " + targetPath + " in the config file.");
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
    // Set target project jcr path from config file
    final String targetProjectJcrPath = configProps.getProperty(Constants.CONFIGPROP_TARGET_PROJECT_ROOT);
    resource.setTargetProjectJcrPath(targetProjectJcrPath);

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
