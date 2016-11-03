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
   * Read properties form configuration file
   *
   * @return configuration properties
   * @throws IOException
   *           - IOException
   */
  public static Properties getConfigProperties() throws IOException {
    return getConfigProperties(false);
  }

  /**
   * Read properties from configuration file
   *
   * @param show
   *          - show configuration properties
   * @return configuration properties
   * @throws IOException
   *           - IOException
   */
  public static Properties getConfigProperties(final boolean show) throws IOException {
    final Properties props = new Properties();
    InputStream input = null;

    try {
      input = new FileInputStream(Constants.CONFIG_FILENAME);

      // load a properties file from class path, inside static method
      props.load(input);

      if (show) {
        LOG.info("=========================================================================");
        LOG.info("Properties from configuration file:");
        LOG.info("=========================================================================");
      }
      final Enumeration<?> e = props.propertyNames();
      while (e.hasMoreElements()) {
        final String key = (String) e.nextElement();
        final String value = props.getProperty(key);
        if (show) {
          LOG.info(key + "=" + value);
        }
      }
      if (show) {
        LOG.info("=========================================================================");
      }

    } catch (final IOException e) {
      LOG.error(
          "Sorry, unable to find or read properties from configuration file [" + Constants.CONFIG_FILENAME
              + "] in the root of your project.");
      throw new IOException(e);
    } finally {
      if (input != null) {
        try {
          input.close();
        } catch (final IOException e) {
          LOG.error("Sorry, unable to close input stream from configuration file. ");
          throw new IOException(e);
        }
      }
    }
    return props;
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
}