package com.headwire.aemdc.companion;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Resource object
 *
 */
public class Resource {

  private static final Logger LOG = LoggerFactory.getLogger(Resource.class);

  private String type;
  private String sourceName;
  private String targetName;
  private Map<String, Map<String, String>> jcrProperties;
  private boolean help;
  private String tempFolder;
  private String sourceFolderPath;
  private String targetFolderPath;
  private String[] extentions;
  private boolean toDeleteDestDir;
  private boolean toWarnDestDir;

  /**
   * Constructor
   */
  public Resource() {
  }

  /**
   * Constructor with command arguments
   *
   * @param args
   *          - all command arguments
   */
  public Resource(final String[] cmdArgs) {
    // check for mandatory arguments
    if (cmdArgs == null || cmdArgs.length == 0) {
      setHelp(true);

    } else {
      // 1 arg
      if (cmdArgs.length == 1) {
        if (Constants.PARAM_OPTION_HELP.equals(cmdArgs[0])) {
          setHelp(true);
        } else if (cmdArgs[0].startsWith(Constants.PARAM_OPTION_TEMP)) {
          setHelp(true);
          setTempFolder(new Param(cmdArgs[0]).getValue());
        } else if (Constants.TYPE_CONFIG_PROPS.equals(cmdArgs[0])) {
          setType(cmdArgs[0]);
        } else {
          setHelp(true);
          setType(cmdArgs[0]);
        }
      }

      // 2 args
      if (cmdArgs.length == 2) {
        if (Constants.PARAM_OPTION_HELP.equals(cmdArgs[0])) {
          setHelp(true);
          setType(cmdArgs[1]);
        } else if (cmdArgs[0].startsWith(Constants.PARAM_OPTION_TEMP)) {
          if (!Constants.TYPE_CONFIG_PROPS.equals(cmdArgs[1])) {
            setHelp(true);
          }
          setTempFolder(new Param(cmdArgs[0]).getValue());
          setType(cmdArgs[1]);
        } else {
          setHelp(true);
          setType(cmdArgs[0]);
          setSourceName(cmdArgs[1]);
        }
      }

      // 3 args
      if (cmdArgs.length == 3) {
        if (Constants.PARAM_OPTION_HELP.equals(cmdArgs[0])) {
          setHelp(true);
          setType(cmdArgs[1]);
          setSourceName(cmdArgs[2]);
        } else if (cmdArgs[0].startsWith(Constants.PARAM_OPTION_TEMP)) {
          if (!Constants.TYPE_CONFIG_PROPS.equals(cmdArgs[1])) {
            setHelp(true);
          }
          setTempFolder(new Param(cmdArgs[0]).getValue());
          setType(cmdArgs[1]);
          setSourceName(cmdArgs[2]);
        } else {
          setType(cmdArgs[0]);
          setSourceName(cmdArgs[1]);
          setTargetName(cmdArgs[2]);

          // Set JCR Properties from arguments
          setJcrProperties(convertArgsToMaps(cmdArgs, 3));
        }
      }

      // > 3 args
      if (cmdArgs.length > 3) {
        if (Constants.PARAM_OPTION_HELP.equals(cmdArgs[0])) {
          setHelp(true);
          setType(cmdArgs[1]);
          setSourceName(cmdArgs[2]);
          setTargetName(cmdArgs[3]);

          // Set JCR Properties from arguments
          setJcrProperties(convertArgsToMaps(cmdArgs, 4));

        } else if (cmdArgs[0].startsWith(Constants.PARAM_OPTION_TEMP)) {
          setTempFolder(new Param(cmdArgs[0]).getValue());
          setType(cmdArgs[1]);
          setSourceName(cmdArgs[2]);
          setTargetName(cmdArgs[3]);

          // Set JCR Properties from arguments
          setJcrProperties(convertArgsToMaps(cmdArgs, 4));

        } else {
          setType(cmdArgs[0]);
          setSourceName(cmdArgs[1]);
          setTargetName(cmdArgs[2]);

          // Set JCR Properties from arguments
          setJcrProperties(convertArgsToMaps(cmdArgs, 3));
        }
      }
    }
  }

  /**
   * @return the type
   */
  public String getType() {
    return type;
  }

  /**
   * @param type
   *          the type to set
   */
  public void setType(final String type) {
    this.type = type;
  }

  /**
   * @return the sourceName
   */
  public String getSourceName() {
    return sourceName;
  }

  /**
   * @param sourceName
   *          the sourceName to set
   */
  public void setSourceName(final String sourceName) {
    this.sourceName = sourceName;
  }

  /**
   * @return the targetName
   */
  public String getTargetName() {
    return targetName;
  }

  /**
   * @param targetName
   *          the targetName to set
   */
  public void setTargetName(final String targetName) {
    this.targetName = targetName;
  }

  /**
   * @return the jcrProperties
   */
  public Map<String, Map<String, String>> getJcrProperties() {
    if (jcrProperties == null) {
      jcrProperties = new HashMap<String, Map<String, String>>();

      // set common jcr props set in any case.
      // It allows to call Custom XML/Text PH Replacer functions from Replacer object
      final Map<String, String> jcrPropsCommon = new HashMap<String, String>();
      jcrProperties.put(Constants.PLACEHOLDER_PROPS_SET_COMMON, jcrPropsCommon);
    }
    return jcrProperties;
  }

  /**
   * @param key
   *          properties set key
   * @return the jcrPropertiesSet by key
   */
  public Map<String, String> getJcrPropsSet(final String key) {
    return getJcrProperties().get(key);
  }

  /**
   * @param jcrProperties
   *          the jcrProperties to set
   */
  public void setJcrProperties(final Map<String, Map<String, String>> jcrProperties) {
    this.jcrProperties = jcrProperties;
  }

  /**
   * @return the help
   */
  public boolean isHelp() {
    return help;
  }

  /**
   * @param help
   *          the help to set
   */
  public void setHelp(final boolean help) {
    this.help = help;
  }

  /**
   * @return the tempFolder
   */
  public String getTempFolder() {
    return tempFolder;
  }

  /**
   * @param tempFolder
   *          the tempFolder to set
   */
  public void setTempFolder(final String tempFolder) {
    this.tempFolder = tempFolder;
  }

  /**
   * @return the sourceFolderPath
   */
  public String getSourceFolderPath() {
    return sourceFolderPath;
  }

  /**
   * @param sourceFolderPath
   *          the sourceFolderPath to set
   */
  public void setSourceFolderPath(final String sourceFolderPath) {
    this.sourceFolderPath = sourceFolderPath;
  }

  /**
   * @return the targetFolderPath
   */
  public String getTargetFolderPath() {
    return targetFolderPath;
  }

  /**
   * @param targetFolderPath
   *          the targetFolderPath to set
   */
  public void setTargetFolderPath(final String targetFolderPath) {
    this.targetFolderPath = targetFolderPath;
  }

  /**
   * @return the extentions
   */
  public String[] getExtentions() {
    return extentions;
  }

  /**
   * @return the extentions as list
   */
  public List<String> getExtentionsList() {
    final List<String> extAsList = Arrays.asList(extentions);
    return extAsList;
  }

  /**
   * @param extentions
   *          the extentions to set
   */
  public void setExtentions(final String[] extentions) {
    this.extentions = extentions;
  }

  /**
   * @return the toDeleteDestDir
   */
  public boolean isToDeleteDestDir() {
    return toDeleteDestDir;
  }

  /**
   * @param toDeleteDestDir
   *          the toDeleteDestDir to set
   */
  public void setToDeleteDestDir(final boolean toDeleteDestDir) {
    this.toDeleteDestDir = toDeleteDestDir;
  }

  /**
   * @return the toWarnDestDir
   */
  public boolean isToWarnDestDir() {
    return toWarnDestDir;
  }

  /**
   * @param toWarnDestDir
   *          the toWarnDestDir to set
   */
  public void setToWarnDestDir(final boolean toWarnDestDir) {
    this.toWarnDestDir = toWarnDestDir;
  }

  @Override
  public Resource clone() {
    final Resource newResource = new Resource();

    // clone params
    newResource.setType(getType());
    newResource.setSourceName(getSourceName());
    newResource.setTargetName(getTargetName());

    // clone Jcr Properties as Args
    final Map<String, Map<String, String>> props = shallowCopy(getJcrProperties());
    newResource.setJcrProperties(props);

    // clone options
    newResource.setHelp(isHelp());
    newResource.setTempFolder(getTempFolder());

    // clone other properties
    newResource.setSourceFolderPath(getSourceFolderPath());
    newResource.setTargetFolderPath(getTargetFolderPath());
    newResource.setExtentions(getExtentions());
    newResource.setToDeleteDestDir(isToDeleteDestDir());
    newResource.setToWarnDestDir(isToWarnDestDir());

    return newResource;
  }

  /**
   * Clone Jcr Properties Set
   *
   * @param source
   *          - source map
   * @return cloned map
   */
  private Map<String, Map<String, String>> shallowCopy(final Map<String, Map<String, String>> source) {
    Map<String, Map<String, String>> newMap = null;

    if (source != null) {
      newMap = new HashMap<String, Map<String, String>>();

      for (final Map.Entry<String, Map<String, String>> entry : source.entrySet()) {
        final String key = entry.getKey();
        final Map<String, String> value = entry.getValue();

        // clone single map
        final Map<String, String> newValueMap = new HashMap<String, String>();
        newValueMap.putAll(value);

        // add single cloned map to a new Properties Set Map
        newMap.put(key, newValueMap);
      }
    }
    return newMap;
  }

  /**
   * Convert arguments to jcr properties with values
   *
   * @param args
   *          - arguments
   * @param start
   *          - start position for not mandatory arguments
   * @return map of jcr properties sets with values
   */
  private Map<String, Map<String, String>> convertArgsToMaps(final String[] args, final int start) {
    final Map<String, Map<String, String>> jcrPropAllSets = new HashMap<String, Map<String, String>>();

    // common jcr props set
    final Map<String, String> jcrPropsCommon = new HashMap<String, String>();
    for (int i = start; i < args.length; i++) {

      final Param param = new Param(args[i]);
      final String key = param.getKey();
      final String value = param.getValue();

      if (key.startsWith(Constants.PLACEHOLDER_PROPS_SET_PREFIX)) {
        // get "ph_" jcr props set
        final int pos = key.indexOf(":");
        final String phSetKey = key.substring(0, pos);
        Map<String, String> jcrPropsSet = jcrPropAllSets.get(phSetKey);

        if (jcrPropsSet == null) {
          jcrPropsSet = new HashMap<String, String>();
        }

        final String ph_key = key.substring(pos + 1);
        jcrPropsSet.put(ph_key, value);
        jcrPropAllSets.put(phSetKey, jcrPropsSet);

        LOG.debug("ph_key={}, value={}", ph_key, value);
      } else {
        jcrPropsCommon.put(key, value);
        LOG.debug("key={}, value={}", key, value);
      }
    }

    // put the common set at the end to the sets list
    jcrPropAllSets.put(Constants.PLACEHOLDER_PROPS_SET_COMMON, jcrPropsCommon);

    return jcrPropAllSets;
  }

  /**
   * Argumets parameter
   */
  class Param {

    String key;
    String value;

    Param(final String arg) {
      // check for valid params like "paramName=paramValue"
      final String message = "Parameter [" + arg + "] must be in form \"paramName=paramValue\"";
      if (StringUtils.isBlank(arg)) {
        throw new IllegalArgumentException(message);
      }
      final int splitPos = arg.indexOf("=");
      if (splitPos < 1) {
        throw new IllegalArgumentException(message);
      }

      // get param key and value
      key = StringUtils.trim(arg.substring(0, splitPos));
      if (StringUtils.isBlank(key)) {
        throw new IllegalArgumentException(message);
      }

      if (arg.length() > (splitPos + 1)) {
        value = StringUtils.trim(arg.substring(splitPos + 1));
      } else {
        value = "";
      }
    }

    /**
     * @return the key
     */
    String getKey() {
      return key;
    }

    /**
     * @return the value
     */
    String getValue() {
      return value;
    }
  }

}
