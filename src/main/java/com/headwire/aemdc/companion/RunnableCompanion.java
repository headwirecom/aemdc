package com.headwire.aemdc.companion;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.runner.BasisRunner;
import com.headwire.aemdc.runner.HelpRunner;

import ch.qos.logback.classic.Level;


/**
 * Runnable Companion Main Class
 *
 * @author Marat Saitov, 25.10.2016
 */
public class RunnableCompanion {

  private static final Logger LOG = LoggerFactory.getLogger(RunnableCompanion.class);
  private static final ch.qos.logback.classic.Logger ROOT_LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory
      .getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);

  private static Config config;

  /**
   * Main start method.
   *
   * @param args
   *          - arguments
   * @throws IOException
   *           - IOException
   */
  public static void main(final String[] args) throws IOException {

    // set default INFO log level to avoid logging from ConfigUtil
    ROOT_LOGGER.setLevel(Level.INFO);

    // Get Properties Config from config file
    config = new Config();

    // setup custom log level
    if (setupCustomLogLevel()) {
      // Check configuration from configuration properties file
      config.checkConfiguration();
    }

    // set mandatories from arguments
    final Resource resource = new Resource(args);

    // Get Runner
    BasisRunner runner = new HelpRunner(resource);
    if (!resource.isHelp()) {
      final Reflection reflection = new Reflection(config);
      runner = reflection.getRunner(resource);
      if (runner == null) {
        runner = new HelpRunner(resource);
      }
    }

    // Run to create template structure
    runner.run();
  }

  /**
   * Setup custom log level based on the LOG_LEVEL configuration parameter.
   * Possible values: ALL/TRACE/DEBUG/INFO/WARN/ERROR/OFF
   *
   * @return true if can setup log level from configuration file
   */
  private static boolean setupCustomLogLevel() {
    boolean status = true;

    final Properties configProps = config.getProperties();

    if (!configProps.isEmpty()) {
      final String logLevel = configProps.getProperty(Constants.CONFIGPROP_LOG_LEVEL);

      if (!Level.INFO.toString().equalsIgnoreCase(logLevel)) {
        if (Level.ALL.toString().equalsIgnoreCase(logLevel)) {
          ROOT_LOGGER.setLevel(Level.ALL);
        } else if (Level.TRACE.toString().equalsIgnoreCase(logLevel)) {
          ROOT_LOGGER.setLevel(Level.TRACE);
        } else if (Level.DEBUG.toString().equalsIgnoreCase(logLevel)) {
          ROOT_LOGGER.setLevel(Level.DEBUG);
        } else if (Level.WARN.toString().equalsIgnoreCase(logLevel)) {
          ROOT_LOGGER.setLevel(Level.WARN);
        } else if (Level.ERROR.toString().equalsIgnoreCase(logLevel)) {
          ROOT_LOGGER.setLevel(Level.ERROR);
        } else if (Level.OFF.toString().equalsIgnoreCase(logLevel)) {
          ROOT_LOGGER.setLevel(Level.OFF);
        }
      }
    } else {
      status = false;
    }

    LOG.debug("Current LOG Level: {}", ROOT_LOGGER.getLevel());

    return status;
  }

}