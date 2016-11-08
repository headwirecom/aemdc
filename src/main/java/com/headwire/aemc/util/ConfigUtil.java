package com.headwire.aemc.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemc.companion.Constants;


/**
 * Config Util
 *
 * @author Marat Saitov, 25.10.2016
 */
public class ConfigUtil {

  private static final Logger LOG = LoggerFactory.getLogger(ConfigUtil.class);

  /**
   * Constructor
   */
  private ConfigUtil() {
  }

  /**
   * Get properties from configuration file
   *
   * @return configuration properties
   * @throws IOException
   *           - IOException
   */
  public static Properties getConfigProperties() throws IOException {
    final Properties props = new Properties();
    InputStream input = null;

    try {
      input = new FileInputStream(Constants.CONFIG_FILENAME);
      // load a properties file from class path
      props.load(input);

    } catch (final IOException e) {
      LOG.error("Sorry, unable to find or read properties from configuration file [{}] in the root of your project.",
          Constants.CONFIG_FILENAME);
      throw new IOException(e);
    } finally {
      if (input != null) {
        try {
          input.close();
        } catch (final IOException e) {
          LOG.error("Sorry, unable to close input stream from configuration file {}.", Constants.CONFIG_FILENAME);
          throw new IOException(e);
        }
      }
    }
    return props;
  }

  /**
   * Get properties from configuration file as text
   *
   * @return configuration properties as text
   * @throws IOException
   */
  public static String getConfigPropertiesAsText() throws IOException {
    final Properties props = getConfigProperties();
    final String configText = getConfigPropertiesAsText(props);
    return configText;
  }

  /**
   * Get properties from configuration file as text
   *
   * @param props
   *          - configuration properties
   * @return configuration properties as text
   */
  public static String getConfigPropertiesAsText(final Properties props) {
    final StringBuilder configText = new StringBuilder();
    configText.append("Properties from configuration file \"");
    configText.append(Constants.CONFIG_FILENAME);
    configText.append("\":\n");
    final Enumeration<?> e = props.propertyNames();
    while (e.hasMoreElements()) {
      final String key = (String) e.nextElement();
      final String value = props.getProperty(key);
      configText.append(key);
      configText.append("=");
      configText.append(value);
      configText.append("\n");
    }
    return configText.toString();
  }

  /**
   * Read extentions property from configuration file
   *
   * @param configProps
   *          - configuration properties
   * @return file extentions
   */
  public static String[] getConfigExtensions(final Properties configProps) {
    final String extentionsAsString = configProps.getProperty(Constants.CONFIGPROP_FILES_WITH_PLACEHOLDERS_EXTENSIONS);
    String[] extentions = Constants.FILES_PH_EXTENSIONS_DEFAULT;
    if (StringUtils.isNotBlank(extentionsAsString)) {
      extentions = extentionsAsString.split(",");
    }
    return extentions;
  }

  /**
   * Get template type source folder path.
   *
   * @param configProps
   *          - configuration properties
   * @param type
   *          - template type
   * @return type source folder
   */
  public static String getTypeSourceFolder(final Properties configProps, final String type) {
    String typeSrcPath = "";
    switch (type) {
      case Constants.TYPE_TEMPLATE:
      case Constants.TYPE_TEMPLATE_FULL:
        typeSrcPath = configProps.getProperty(Constants.CONFIGPROP_SOURCE_TEMPLATES_FOLDER);
        break;
      case Constants.TYPE_COMPONENT:
      case Constants.TYPE_COMPONENT_FULL:
        typeSrcPath = configProps.getProperty(Constants.CONFIGPROP_SOURCE_COMPONENTS_FOLDER);
        break;
      case Constants.TYPE_OSGI:
        typeSrcPath = configProps.getProperty(Constants.CONFIGPROP_SOURCE_OSGI_FOLDER);
        break;
      case Constants.TYPE_MODEL:
        typeSrcPath = configProps.getProperty(Constants.CONFIGPROP_SOURCE_MODELS_FOLDER);
        break;
      case Constants.TYPE_SERVICE:
        typeSrcPath = configProps.getProperty(Constants.CONFIGPROP_SOURCE_SERVICES_FOLDER);
        break;
      case Constants.TYPE_SERVLET:
        typeSrcPath = configProps.getProperty(Constants.CONFIGPROP_SOURCE_SERVLETS_FOLDER);
        break;
      default:
        throw new IllegalStateException("Unknown <type> argument: " + type);
    }
    return typeSrcPath;
  }
}