package com.headwire.aemdc.companion;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.util.FilesDirsUtil;


/**
 * Config
 *
 * @author Marat Saitov, 25.10.2016
 */
public class Config {

  private static final Logger LOG = LoggerFactory.getLogger(Config.class);
  private final Properties defaultConfigProps;
  private final Properties configProps;
  private final Map<String, Properties> dynamicConfigs = new HashMap<String, Properties>();
  private Collection<String> dynamicTypes;

  /**
   * Constructor
   */
  public Config() {
    // INIT default configuration file properties from resources folder
    defaultConfigProps = FilesDirsUtil
        .getPropertiesFromContextClassLoader(Constants.CONFIG_PROPS_FOLDER + "/" + Constants.CONFIG_PROPS_FILENAME);

    // INIT config properties
    configProps = replacePathPlaceHolders(FilesDirsUtil.getProperties(Constants.CONFIG_PROPS_FILENAME));

    // INIT dynamic configs
    for (final String type : getDynamicTypes()) {
      final Properties dynProps = readDynamicProperties(type);
      dynamicConfigs.put(type, dynProps);
    }
  }

  /**
   * Check whether configurated source paths exist
   *
   * @return true - if all paths exist, false - otherwise
   */
  public boolean checkConfiguration() {
    boolean status = true;
    if (!configProps.isEmpty()) {

      // validate config properties
      for (final String pathKey : Constants.SOURCE_PATHS) {
        final String path = configProps.getProperty(pathKey);
        if (StringUtils.isBlank(path)) {
          LOG.error("Please configurate the key [{}] in the configuration properties file [{}] in the root folder.",
              pathKey,
              Constants.CONFIG_PROPS_FILENAME);
          status = false;
        } else {
          final File file = new File(path);
          if (!file.exists()) {
            LOG.error("The path [{}] from configuration properties file [{}] doesn't exist.", path,
                Constants.CONFIG_PROPS_FILENAME);
            status = false;
          }
        }
      }

      for (final String pathKey : Constants.CONFIGPROPS_OTHER) {
        final String path = configProps.getProperty(pathKey);
        if (StringUtils.isBlank(path)) {
          LOG.error("Please configurate the key [{}] in the configuration properties file [{}] in the root folder.",
              pathKey,
              Constants.CONFIG_PROPS_FILENAME);
          status = false;
        }
      }

      // validate dynamic config properties
      for (final Map.Entry<String, Properties> entry : dynamicConfigs.entrySet()) {
        final Properties dynProps = entry.getValue();
        final String dynType = entry.getKey();

        for (final String pathKey : Constants.DYN_SOURCE_PATHS) {
          final String path = dynProps.getProperty(pathKey);
          if (StringUtils.isBlank(path)) {
            LOG.error(
                "Please configurate the key [{}] for the template type [{}] in the configuration properties file [{}].",
                pathKey, dynType, getDynamicConfigPath(dynType));
            status = false;
          } else {
            final File file = new File(path);
            if (!file.exists()) {
              LOG.error(
                  "The path [{}] for the template type [{}] from the configuration properties file [{}] doesn't exist.",
                  pathKey, dynType, getDynamicConfigPath(dynType));
              status = false;
            }
          }
        }

        for (final String pathKey : Constants.DYN_CONFIGPROPS_OTHER) {
          final String path = dynProps.getProperty(pathKey);
          if (StringUtils.isBlank(path)) {
            LOG.error(
                "Please configurate the key [{}] for the template type [{}] in the configuration properties file [{}].",
                pathKey, dynType, getDynamicConfigPath(dynType));
            status = false;
          }
        }
      }

    } else {
      status = false;
    }

    return status;
  }

  /**
   * Get property value
   *
   * @param key
   *          - property key
   *
   * @return property value
   */
  public String getProperty(final String key) {
    return configProps.getProperty(key);
  }

  /**
   * Get configuration properties
   *
   * @return configuration properties
   */
  public Properties getProperties() {
    return configProps;
  }

  /**
   * Get configuration properties as text
   *
   * @return configuration properties as text
   */
  public String getPropertiesAsText() {
    final String configText = getPropertiesAsText(getProperties());
    return configText;
  }

  /**
   * Get properties as text
   *
   * @param props
   *          - configuration properties
   * @return sorted configuration properties as text
   */
  public String getPropertiesAsText(final Properties props) {
    // get sorted list
    final List<String> sortedKeys = new ArrayList<String>();
    for (final String key : props.stringPropertyNames()) {
      sortedKeys.add(key);
    }
    Collections.sort(sortedKeys);

    final StringBuilder configText = new StringBuilder();
    for (final String key : sortedKeys) {
      final String value = props.getProperty(key);
      configText.append(key);
      configText.append("=");
      configText.append(value);
      configText.append("\n");
    }

    return configText.toString();
  }

  /**
   * Get properties from default configuration file from resources folder
   *
   * @return default configuration properties
   */
  public Properties getDefaultProperties() {
    return defaultConfigProps;
  }

  /**
   * Get properties from default configuration file as text
   *
   * @return default configuration properties as text
   */
  public String getDefaultPropertiesAsText() {
    final String configText = getPropertiesAsText(getDefaultProperties());
    return configText;
  }

  /**
   * Get dynamic type configuration properties
   *
   * @param type
   *          - dynamic template type
   * @return dynamic type configuration properties
   */
  public Properties getDynamicProperties(final String type) {
    return dynamicConfigs.get(type);
  }

  /**
   * Get dynamic types from placeholders aemdc-files project
   *
   * @return dynamic type list
   */
  public Collection<String> getDynamicTypes() {
    if (dynamicTypes == null) {
      Collection<String> list = new ArrayList<String>();

      // Get types dir
      final String typesDirPath = configProps.getProperty(Constants.CONFIGPROP_SOURCE_TYPES_FOLDER);

      if (StringUtils.isNotBlank(typesDirPath)) {
        final File dir = new File(typesDirPath);
        if (dir.exists()) {
          list = FilesDirsUtil.listRootDirNames(dir);
        }
      }
      dynamicTypes = list;
    }
    return dynamicTypes;
  }

  /**
   * Get properties from lazybones configuration file
   *
   * @return lazybones configuration properties if props file exists
   */
  public static Properties getLazybonesProperties() {
    Properties props = new Properties();

    // Get lazybones properties
    final File file = new File(Constants.LAZYBONES_CONFIG_PROPS_FILE_PATH);
    if (file.exists()) {
      props = FilesDirsUtil.getProperties(Constants.LAZYBONES_CONFIG_PROPS_FILE_PATH);
    }

    return props;
  }

  /**
   * Get extentions of files with placeholders
   *
   * @return file extentions
   */
  public String[] getFileExtensions() {
    final String extentionsAsString = configProps.getProperty(Constants.CONFIGPROP_FILES_WITH_PLACEHOLDERS_EXTENSIONS);
    String[] extentions = Constants.FILES_PH_EXTENSIONS_DEFAULT;
    if (StringUtils.isNotBlank(extentionsAsString)) {
      extentions = extentionsAsString.split(",");
    }
    return extentions;
  }

  /**
   * Get menu command for the type
   *
   * @param type
   *          - dynamic template type
   * @return menu commands
   */
  public String[] getCommands(final String type) {
    String[] cmds = {};
    final Properties props = getDynamicProperties(type);
    final String cmdsAsString = props.getProperty(Constants.DYN_CONFIGPROP_COMMAND_MENU);
    if (StringUtils.isNotBlank(cmdsAsString)) {
      cmds = cmdsAsString.split(",");
    }
    return cmds;
  }

  /**
   * Is template structure directory or file?
   *
   * @param type
   *          - dynamic template type
   * @return true - if DIR template structure, false - if FILE
   */
  public boolean isDirTemplateStructure(final String type) {
    boolean result = true;
    final Properties dynProps = getDynamicProperties(type);
    final String structure = dynProps.getProperty(Constants.DYN_CONFIGPROP_TEMPLATE_STRUCTURE);
    if (StringUtils.isNotBlank(structure) && Constants.TEMPLATE_STRUCTURE_FILE.equals(structure)) {
      result = false;
    }
    return result;
  }

  /**
   * Get dynamic configuration properties file path
   *
   * @param type
   *          - dynamic template type
   * @return dynamic configuration properties file path
   */
  private String getDynamicConfigPath(final String type) {
    final String dynConfigPath = configProps.getProperty(Constants.CONFIGPROP_SOURCE_TYPES_FOLDER) + "/" + type
        + "/" + Constants.DYNAMIC_CONFIG_PROPS_FILENAME;
    return dynConfigPath;
  }

  /**
   * Read properties from dynamic configuration file
   *
   * @param type
   *          - dynamic template type
   * @return dynamic configuration properties if props file exists
   */
  private Properties readDynamicProperties(final String type) {
    Properties dynProps = FilesDirsUtil.getProperties(getDynamicConfigPath(type));

    // replace path place holders
    if (!dynProps.isEmpty()) {
      // source path placeholder values
      dynProps = replacePathPlaceHolder(dynProps, Constants.CONFIGPROP_SOURCE_FOLDER,
          configProps.getProperty(Constants.CONFIGPROP_SOURCE_FOLDER));
      dynProps = replacePathPlaceHolder(dynProps, Constants.CONFIGPROP_SOURCE_TYPES_FOLDER,
          configProps.getProperty(Constants.CONFIGPROP_SOURCE_TYPES_FOLDER));
      dynProps = replacePathPlaceHolder(dynProps, Constants.CONFIGPROP_SOURCE_UI_FOLDER,
          configProps.getProperty(Constants.CONFIGPROP_SOURCE_UI_FOLDER));
      dynProps = replacePathPlaceHolder(dynProps, Constants.CONFIGPROP_SOURCE_PROJECT_ROOT,
          configProps.getProperty(Constants.CONFIGPROP_SOURCE_PROJECT_ROOT));
      dynProps = replacePathPlaceHolder(dynProps, Constants.CONFIGPROP_SOURCE_JAVA_FOLDER,
          configProps.getProperty(Constants.CONFIGPROP_SOURCE_JAVA_FOLDER));

      // target path placeholder values
      dynProps = replacePathPlaceHolder(dynProps, Constants.CONFIGPROP_TARGET_UI_FOLDER,
          configProps.getProperty(Constants.CONFIGPROP_TARGET_UI_FOLDER));
      dynProps = replacePathPlaceHolder(dynProps, Constants.CONFIGPROP_TARGET_PROJECT_APPS_FOLDER,
          configProps.getProperty(Constants.CONFIGPROP_TARGET_PROJECT_APPS_FOLDER));
      dynProps = replacePathPlaceHolder(dynProps, Constants.CONFIGPROP_TARGET_PROJECT_CONF_FOLDER,
          configProps.getProperty(Constants.CONFIGPROP_TARGET_PROJECT_CONF_FOLDER));
      dynProps = replacePathPlaceHolder(dynProps, Constants.CONFIGPROP_TARGET_PROJECT_DESIGN_FOLDER,
          configProps.getProperty(Constants.CONFIGPROP_TARGET_PROJECT_DESIGN_FOLDER));
      dynProps = replacePathPlaceHolder(dynProps, Constants.CONFIGPROP_TARGET_PROJECT_ROOT,
          configProps.getProperty(Constants.CONFIGPROP_TARGET_PROJECT_ROOT));
      dynProps = replacePathPlaceHolder(dynProps, Constants.CONFIGPROP_TARGET_JAVA_FOLDER,
          configProps.getProperty(Constants.CONFIGPROP_TARGET_JAVA_FOLDER));
      dynProps = replacePathPlaceHolder(dynProps, Constants.CONFIGPROP_TARGET_JAVA_PACKAGE,
          configProps.getProperty(Constants.CONFIGPROP_TARGET_JAVA_PACKAGE));
      dynProps = replacePathPlaceHolder(dynProps, Constants.CONFIGPROP_TARGET_JAVA_PACKAGE_FOLDER,
          configProps.getProperty(Constants.CONFIGPROP_TARGET_JAVA_PACKAGE_FOLDER));
      dynProps = replacePathPlaceHolder(dynProps, Constants.CONFIGPROP_TARGET_OSGI_SUBFOLDER,
          configProps.getProperty(Constants.CONFIGPROP_TARGET_OSGI_SUBFOLDER));
    }
    return dynProps;
  }

  /**
   * Replace path placeholders in the configuration properties path values.
   *
   * @param props
   *          - configuration properties where to replace placeholders
   * @return initialized configuration properties
   */
  private Properties replacePathPlaceHolders(final Properties props) {
    Properties newProps = props;

    LOG.debug("Configuration properties path placeholders replacing... ");

    if (!newProps.isEmpty()) {
      // source path placeholder values
      newProps = replacePathPlaceHolder(newProps, Constants.CONFIGPROP_SOURCE_FOLDER,
          newProps.getProperty(Constants.CONFIGPROP_SOURCE_FOLDER));
      newProps = replacePathPlaceHolder(newProps, Constants.CONFIGPROP_SOURCE_TYPES_FOLDER,
          newProps.getProperty(Constants.CONFIGPROP_SOURCE_TYPES_FOLDER));
      newProps = replacePathPlaceHolder(newProps, Constants.CONFIGPROP_SOURCE_UI_FOLDER,
          newProps.getProperty(Constants.CONFIGPROP_SOURCE_UI_FOLDER));
      newProps = replacePathPlaceHolder(newProps, Constants.CONFIGPROP_SOURCE_PROJECT_ROOT,
          newProps.getProperty(Constants.CONFIGPROP_SOURCE_PROJECT_ROOT));
      newProps = replacePathPlaceHolder(newProps, Constants.CONFIGPROP_SOURCE_JAVA_FOLDER,
          newProps.getProperty(Constants.CONFIGPROP_SOURCE_JAVA_FOLDER));

      // target path placeholder values
      newProps = replacePathPlaceHolder(newProps, Constants.CONFIGPROP_TARGET_UI_FOLDER,
          newProps.getProperty(Constants.CONFIGPROP_TARGET_UI_FOLDER));
      newProps = replacePathPlaceHolder(newProps, Constants.CONFIGPROP_TARGET_PROJECT_APPS_FOLDER,
          newProps.getProperty(Constants.CONFIGPROP_TARGET_PROJECT_APPS_FOLDER));
      newProps = replacePathPlaceHolder(newProps, Constants.CONFIGPROP_TARGET_PROJECT_CONF_FOLDER,
          newProps.getProperty(Constants.CONFIGPROP_TARGET_PROJECT_CONF_FOLDER));
      newProps = replacePathPlaceHolder(newProps, Constants.CONFIGPROP_TARGET_PROJECT_DESIGN_FOLDER,
          newProps.getProperty(Constants.CONFIGPROP_TARGET_PROJECT_DESIGN_FOLDER));
      newProps = replacePathPlaceHolder(newProps, Constants.CONFIGPROP_TARGET_PROJECT_ROOT,
          newProps.getProperty(Constants.CONFIGPROP_TARGET_PROJECT_ROOT));
      newProps = replacePathPlaceHolder(newProps, Constants.CONFIGPROP_TARGET_JAVA_FOLDER,
          newProps.getProperty(Constants.CONFIGPROP_TARGET_JAVA_FOLDER));
      newProps = replacePathPlaceHolder(newProps, Constants.CONFIGPROP_TARGET_JAVA_PACKAGE,
          newProps.getProperty(Constants.CONFIGPROP_TARGET_JAVA_PACKAGE));
      newProps = replacePathPlaceHolder(newProps, Constants.CONFIGPROP_TARGET_JAVA_PACKAGE_FOLDER,
          newProps.getProperty(Constants.CONFIGPROP_TARGET_JAVA_PACKAGE_FOLDER));
    }

    return newProps;
  }

  /**
   * Replace path placeholder in the configuration properties path values.
   *
   * @param props
   *          - configuration properties
   * @param placeHolderName
   *          - placeholder name
   * @param placeHolderValue
   *          - placeholder value
   * @return initialized configuration properties
   */
  private Properties replacePathPlaceHolder(final Properties props, final String placeHolderName,
      final String placeHolderValue) {
    final Properties newProps = new Properties();

    final Enumeration<?> e = props.propertyNames();
    while (e.hasMoreElements()) {
      final String key = (String) e.nextElement();
      String value = props.getProperty(key);

      LOG.debug("Original {}={}", key, value);
      // check for NullPointer
      if (StringUtils.isNotBlank(placeHolderName) && placeHolderValue != null) {
        value = value.replace("{{" + placeHolderName + "}}", placeHolderValue);
      }
      LOG.debug("Replaced {}={}", key, value);

      // add with replaced path values
      newProps.put(key, value);
    }
    return newProps;
  }
}