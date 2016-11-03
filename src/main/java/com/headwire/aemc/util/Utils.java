package com.headwire.aemc.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemc.companion.Constants;


/**
 * Runnable Companion Main Class
 *
 * @author Marat Saitov, 25.10.2016
 */
public class Utils {

  private static final Logger LOG = LoggerFactory.getLogger(Utils.class);

  /**
   * Constructor
   */
  private Utils() {
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
   * Read properties form configuration file
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
   * Read help text from helper file.
   *
   * @return help text
   * @throws IOException
   *           - IOException
   */
  public static String getHelpText() throws IOException {
    final InputStream in = Thread.currentThread().getContextClassLoader()
        .getResourceAsStream(Constants.AEMC_HELP_FILE_PATH);
    final StringWriter writer = new StringWriter();
    String helpText = "\n";
    try {
      IOUtils.copy(in, writer, Constants.ENCODING);
      helpText += writer.toString();
    } catch (final IOException e) {
      LOG.error("Sorry, can't show you help text");
      throw new IOException(e);
    }
    return helpText;
  }
}