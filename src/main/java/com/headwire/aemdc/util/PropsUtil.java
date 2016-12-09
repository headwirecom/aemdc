package com.headwire.aemdc.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Properties file Util
 *
 * @author Marat Saitov, 15.11.2016
 */
public class PropsUtil {

  private static final Logger LOG = LoggerFactory.getLogger(PropsUtil.class);

  /**
   * Constructor
   *
   */
  private PropsUtil() {
  }

  /**
   * Get properties from property file
   *
   * @param filepath
   *          - file path
   * @return properties object
   */
  public static Properties getProperties(final String filepath) {
    final Properties props = new Properties();
    InputStream input = null;

    try {
      input = new FileInputStream(filepath);

      // load a properties file from class path
      props.load(input);

    } catch (final IOException e) {
      LOG.error("Sorry, unable to find or read properties from file [{}].", filepath);
    } finally {
      if (input != null) {
        try {
          input.close();
        } catch (final IOException e) {
          LOG.error("Sorry, unable to close input stream from file [{}].", filepath);
        }
      }
    }
    return props;
  }

  /**
   * Get properties from property file from Context Class Loader
   *
   * @param filepath
   *          - file path
   * @return properties object
   */
  public static Properties getPropertiesFromContextClassLoader(final String filepath) {
    final Properties props = new Properties();
    InputStream input = null;

    try {
      input = Thread.currentThread().getContextClassLoader().getResourceAsStream(filepath);

      // load a properties file from class path
      props.load(input);

    } catch (final IOException e) {
      LOG.error("Sorry, unable to find or read properties from file [{}].", filepath);
    } finally {
      if (input != null) {
        try {
          input.close();
        } catch (final IOException e) {
          LOG.error("Sorry, unable to close input stream from file {}.", filepath);
        }
      }
    }
    return props;
  }

}