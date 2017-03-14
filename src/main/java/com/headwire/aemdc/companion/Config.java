package com.headwire.aemdc.companion;

import com.headwire.aemdc.runner.ConfigPropsRunner;
import com.headwire.aemdc.util.FilesDirsUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;


/**
 * Config
 *
 * @author Marat Saitov, 25.10.2016
 */
public class Config {

  private static final Logger LOG = LoggerFactory.getLogger(Config.class);

  private static File projectRoot = new File(".");
  private final File baseFolder;
  private final String configPropertiesFileName;
  private final Properties defaultConfigProps;
  private final Properties configProps;
  private final Map<String, Properties> dynamicConfigs = new HashMap<String, Properties>();
  private Collection<String> dynamicTypes;

  /**
   * Constructor
   */
  public Config() {
    this(projectRoot, ConfigPropsRunner.CONFIG_PROPS_FILENAME);
  }

  /**
   * Constructor
   */
  public Config(final File baseFolder, final String configPropertiesFileName) {
    if (baseFolder == null) {
      throw new IllegalArgumentException("Base Folder must be provided");
    } else if (!baseFolder.exists()) {
      throw new IllegalArgumentException("Base Folder: '" + baseFolder.getPath() + "' does not exist");
    } else if (!baseFolder.isDirectory()) {
      throw new IllegalArgumentException("Base Folder: '" + baseFolder.getPath() + "' is not a folder");
    } else if (configPropertiesFileName == null || configPropertiesFileName.isEmpty()) {
      throw new IllegalArgumentException("Config Properties File Name must be provided");
    }

    this.baseFolder = baseFolder;
    this.configPropertiesFileName = configPropertiesFileName;

    // init default configuration file properties from resources folder
    defaultConfigProps = FilesDirsUtil
        .getPropertiesFromContextClassLoader(
            ConfigPropsRunner.SOURCE_NAME_FOLDER + "/" + ConfigPropsRunner.CONFIG_PROPS_FILENAME);

    // init config properties
    configProps = replacePathPlaceHolders(
        FilesDirsUtil.getProperties(baseFolder.getPath() + "/" + configPropertiesFileName));

    // init dynamic configs
    for (final String type : getDynamicTypes()) {
      Properties dynProps = readDynamicProperties(type, null);

      Key key = new Key(type, "");
      dynamicConfigs.put(key.getKey(), dynProps);

      for (final String name : getTemplateNames(type)) {
        dynProps = readDynamicProperties(type, name);
        key = new Key(type, name);
        dynamicConfigs.put(key.getKey(), dynProps);
      }
    }
  }

  /**
   * Get project root path
   *
   * @return project root directory path
   */
  public static String getProjectRootPath() {
    return projectRoot.getPath();
  }

  /**
   * Set project root path
   *
   * @param projectRootPath
   *          - project root path
   * @return true if project root path is Ok
   */
  public static boolean setProjectRootPath(final String projectRootPath) {
    boolean answer = true;
    if (projectRootPath == null || projectRootPath.isEmpty()) {
      answer = false;
      LOG.error("Project Root Path is not provide -> ignore and it stays as: {}", Config.projectRoot.getPath());
    } else {
      final File temp = new File(projectRootPath);
      if (!temp.exists()) {
        answer = false;
        LOG.error("Project Root Path does not exist -> ignore and it stays as: {}", Config.projectRoot.getPath());
      } else if (!temp.isDirectory()) {
        answer = false;
        LOG.error("Project Root Path is not a folder -> ignore and it stays as: {}", Config.projectRoot.getPath());
      } else {
        projectRoot = temp;
      }
    }
    return answer;
  }

  /**
   * Check whether configurated source paths exist
   *
   * @return true - if all paths exist, false - otherwise
   */
  public boolean checkConfiguration() {
    final List<String> validationReports = validateConfiguration();
    final boolean status = validationReports.isEmpty();
    if (!status) {
      for (final String report : validationReports) {
        LOG.error(report);
      }
    }
    return status;
  }

  /**
   * Validate this configuration
   *
   * @param baseFolder
   *          - base project root directory path
   * @param configPropertiesFileName
   *          - config props file name
   * @return list of configuration error issues or empty if OK
   */
  public static List<String> validateThisConfiguration(final File baseFolder, final String configPropertiesFileName) {
    final Config config = new Config(baseFolder, configPropertiesFileName);
    return config.validateConfiguration();
  }

  /**
   * Check the Configuration and report any issues
   *
   * @return List of Configuration Issues or empty if OK
   */
  public List<String> validateConfiguration() {
    final List<String> answer = new ArrayList<>();
    // String configPath = baseFolder.getPath() + "/" + configPropertiesFileName;
    if (!configProps.isEmpty()) {
      // validate config properties
      for (final String pathKey : Constants.SOURCE_PATHS) {
        final String path = configProps.getProperty(pathKey);
        if (StringUtils.isBlank(path)) {
          answer.add(
              "Please configure the key [" + pathKey + "] in the configuration properties file ["
                  + configPropertiesFileName + "].");
        } else {
          final File file = new File(path);
          if (!file.exists()) {
            answer.add(
                "The path [" + path + "] of key [" + pathKey + "] from configuration properties file ["
                    + configPropertiesFileName + "] doesn't exist.");
          }
        }
      }

      for (final String pathKey : Constants.CONFIGPROPS_OTHER) {
        final String path = configProps.getProperty(pathKey);
        if (StringUtils.isBlank(path)) {
          answer.add(
              "Please configure the key [" + pathKey + "] in the configuration properties file ["
                  + configPropertiesFileName + "].");
        }
      }

      // validate dynamic config properties
      for (final Map.Entry<String, Properties> entry : dynamicConfigs.entrySet()) {
        final Properties dynProps = entry.getValue();
        final Key key = new Key(entry.getKey());
        final String dynType = key.getType();
        final String dynName = key.getName();

        for (final String pathKey : Constants.DYN_SOURCE_PATHS) {
          final String path = dynProps.getProperty(pathKey);
          if (StringUtils.isBlank(path)) {
            answer.add(
                "Please configure the source key [" + pathKey + "] for the template type [" + dynType + "] " +
                    "and name [" + dynName + "] in the configuration properties file ["
                    + getDynamicConfigPath(dynType, dynName) + "].");
          } else {
            final File file = new File(path);
            if (!file.exists()) {
              answer.add(
                  "The path [" + pathKey + "] for the template type [" + dynType + "] and name [" + dynName + "] " +
                      "from the configuration property files [" + getDynamicConfigPath(dynType, null) + "] " +
                      "and [" + getDynamicConfigPath(dynType, dynName) + "] doesn't exist.");
            }
          }
        }

        for (final String pathKey : Constants.DYN_CONFIGPROPS_OTHER) {
          if (!dynProps.containsKey(pathKey)) {
            answer.add(
                "Please configure the key [" + pathKey + "] for the template type [" + dynType + "] and name ["
                    + dynName + "] " +
                    "in the configuration property files [+ " + getDynamicConfigPath(dynType, null) + "] " +
                    " or [" + getDynamicConfigPath(dynType, dynName) + "].");
          }
        }
      }
    } else {
      answer.add(
          "No Config Properties provided or loaded");
    }

    return answer;
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
   * @param name
   *          - dynamic template name
   * @return dynamic type configuration properties
   */
  public Properties getDynamicProperties(final String type, final String name) {
    Key key = new Key(type, name);
    Properties dynProps = dynamicConfigs.get(key.getKey());

    if (dynProps == null) {
      LOG.debug("Unknown [name] argument [{}].", name);
      key = new Key(type, null);
      dynProps = dynamicConfigs.get(key.getKey());
    }
    return dynProps;
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

      // remove forbidden types
      for (final String type : getForbiddenTypes()) {
        list.remove(type);
      }

      dynamicTypes = list;
    }
    return dynamicTypes;
  }

  /**
   * Is dynamic type
   *
   * @return true - if dynamic type
   */
  public boolean isDynamicType(final String type) {
    return getDynamicTypes().contains(type);
  }

  /**
   * Get template names from placeholders aemdc-files project
   *
   * @return template names list
   */
  public Collection<String> getTemplateNames(final String type) {
    Collection<String> list = new ArrayList<String>();

    // Get type templates dir
    final String path = configProps.getProperty(Constants.CONFIGPROP_SOURCE_TYPES_FOLDER) + "/" + type;

    if (StringUtils.isNotBlank(path)) {
      final File dir = new File(path);
      if (dir.exists()) {
        list = FilesDirsUtil.listRootDirNames(dir);
      }
    }

    // remove forbidden types
    for (final String denyType : getForbiddenTypes()) {
      list.remove(denyType);
    }

    return list;
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
   * Get extensions of files with placeholders
   *
   * @return file extensions
   */
  public String[] getFileExtensions() {
    final String extensionsAsString = configProps.getProperty(Constants.CONFIGPROP_FILES_WITH_PLACEHOLDERS_EXTENSIONS);
    String[] extensions = Constants.FILES_PH_EXTENSIONS_DEFAULT;
    if (StringUtils.isNotBlank(extensionsAsString)) {
      extensions = extensionsAsString.split(",");
    }
    return extensions;
  }

  /**
   * Get forbidden template types
   *
   * @return forbidden template type list
   */
  public String[] getForbiddenTypes() {
    final String typesAsString = configProps.getProperty(Constants.CONFIGPROP_FORBIDDEN_TEMPLATE_TYPES);
    String[] types = Constants.FORBIDDEN_TYPES_DEFAULT;
    if (StringUtils.isNotBlank(typesAsString)) {
      types = typesAsString.split(",");
    }
    return types;
  }

  /**
   * Get menu command for the dynamic type
   *
   * @param type
   *          - dynamic template type
   * @param name
   *          - dynamic template name
   * @return menu commands
   */
  public String[] getCommands(final String type, final String name) {
    String[] cmds = {};
    final Properties dynProps = getDynamicProperties(type, name);
    if (dynProps != null) {
      final String cmdsAsString = dynProps.getProperty(Constants.DYN_CONFIGPROP_COMMAND_MENU);
      if (StringUtils.isNotBlank(cmdsAsString)) {
        cmds = cmdsAsString.split(",");
      }
    }
    return cmds;
  }

  /**
   * Get compound list for the compound type
   *
   * @param name
   *          - compound template name
   * @return compound set
   */
  public Map<String, Set<String>> getCompoundList(final String name) {
    final Map<String, Set<String>> list = new HashMap<String, Set<String>>();

    final Properties dynProps = getDynamicProperties(Constants.TYPE_COMPOUND, name);

    if (dynProps != null) {
      final String compoundsAsString = dynProps.getProperty(Constants.DYN_CONFIGPROP_COMPOUND);

      if (StringUtils.isNotBlank(compoundsAsString)) {
        final String[] compounds = compoundsAsString.split(",");

        for (final String comp : compounds) {

          Set<String> set = new HashSet<String>();
          final String[] aSplited = comp.split(":");
          final String aTemplateType = aSplited[0];

          for (final String compound : compounds) {
            final String[] splited = compound.split(":");
            final String templateType = splited[0]; //To compare
            String templateName = "";
            if (splited.length == 2) {
              templateName = splited[1];
            }

            if (StringUtils.equalsIgnoreCase(templateType, aTemplateType)) {
              set.add(templateName);
            }

            if (Constants.TYPE_COMPOUND.equals(templateType)) {
              LOG.error(
                  "Not allow to configurate a compound type [{}] inside of another compound type [{}].",
                  templateName, name);
            } else {
              LOG.debug("templateType: {}, templateName: {}", templateType, templateName);
            }
          }
          list.put(aTemplateType, set);
        }
      }
    }
    return list;
  }

  /**
   * Is template structure directory or file?
   *
   * @param type
   *          - dynamic template type
   * @param name
   *          - dynamic template name
   * @return true - if DIR template structure, false - if FILE
   */
  public boolean isDirTemplateStructure(final String type, final String name) {
    boolean result = true;
    final Properties dynProps = getDynamicProperties(type, name);
    if (dynProps != null) {
      final String structure = dynProps.getProperty(Constants.DYN_CONFIGPROP_TEMPLATE_STRUCTURE);
      if (StringUtils.isNotBlank(structure) && Constants.TEMPLATE_STRUCTURE_FILE.equals(structure)) {
        result = false;
      }
    }
    return result;
  }

  /**
   * Get dynamic configuration properties file path
   *
   * @param type
   *          - dynamic template type
   * @param name
   *          - template name
   * @return dynamic configuration properties file path
   */
  private String getDynamicConfigPath(final String type, final String name) {
    String dynConfigPath = configProps.getProperty(Constants.CONFIGPROP_SOURCE_TYPES_FOLDER) + "/" + type;
    if (StringUtils.isNotBlank(name)) {
      dynConfigPath += "/" + name;
    }
    dynConfigPath += "/" + Constants.DYNAMIC_CONFIG_PROPS_FILENAME;
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
    final Properties dynProps = readDynamicProperties(type, null);
    return dynProps;
  }

  /**
   * Read properties from dynamic configuration file
   *
   * @param type
   *          - dynamic template type
   * @param name
   *          - template name
   * @return dynamic configuration properties if props file exists
   */
  private Properties readDynamicProperties(final String type, final String name) {
    Properties dynProps = FilesDirsUtil.getProperties(getDynamicConfigPath(type, null));
    dynProps.putAll(FilesDirsUtil.getProperties(getDynamicConfigPath(type, name)));

    // replace path place holders
    if (!dynProps.isEmpty()) {
      // source path placeholder values
      dynProps = replacePathPlaceHolder(dynProps, Constants.CONFIGPROP_SOURCE_FOLDER,
          configProps.getProperty(Constants.CONFIGPROP_SOURCE_FOLDER));
      dynProps = replacePathPlaceHolder(dynProps, Constants.CONFIGPROP_SOURCE_TYPES_FOLDER,
          configProps.getProperty(Constants.CONFIGPROP_SOURCE_TYPES_FOLDER));

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
      dynProps = replacePathPlaceHolder(dynProps, Constants.CONFIGPROP_TARGET_JAVA_MODEL_SUBPACKAGE,
          configProps.getProperty(Constants.CONFIGPROP_TARGET_JAVA_MODEL_SUBPACKAGE));
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
      newProps = replacePathPlaceHolder(newProps, Constants.CONFIGPROP_TARGET_JAVA_MODEL_SUBPACKAGE,
          newProps.getProperty(Constants.CONFIGPROP_TARGET_JAVA_MODEL_SUBPACKAGE));
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

  /**
   * Dynamic config key
   */
  class Key {

    String key;
    String type;
    String name;

    Key(final String type, final String name) {
      this.type = type;

      if (StringUtils.isNotBlank(name)) {
        this.name = name;
      } else {
        this.name = "";
      }

      this.key = getType() + ":" + getName();
    }

    Key(final String key) {
      this.key = key;

      final String[] splited = key.split(":");
      type = splited[0];
      if (splited.length == 2) {
        name = splited[1];
      }
    }

    /**
     * @return the key
     */
    String getKey() {
      return key;
    }

    /**
     * @return the type
     */
    String getType() {
      return type;
    }

    /**
     * @return the name
     */
    String getName() {
      return name;
    }
  }
}
