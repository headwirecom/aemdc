package com.headwire.aemdc.companion;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.runner.BasisRunner;
import com.headwire.aemdc.runner.HelpRunner;
import com.headwire.aemdc.util.ConfigUtil;

import ch.qos.logback.classic.Level;


/**
 * Runnable Companion Main Class
 *
 * @author Marat Saitov, 25.10.2016
 */
public class RunnableCompanion {

  private static final Logger LOG = LoggerFactory.getLogger(RunnableCompanion.class);

  /**
   * Main start method.
   *
   * @param args
   *          - arguments
   * @throws IOException
   *           - IOException
   */
  public static void main(final String[] args) throws IOException {

    // set log level
    setLogLevel();

    // Check configuration from configuration properties file
    ConfigUtil.checkConfiguration();

    // Set mandatories from arguments
    final Resource resource = new Resource(args);

    BasisRunner runner;

    if (resource.isHelp()) {
      // Get Help Runner
      runner = new HelpRunner(resource);
    } else {
      // Get Runner
      final Reflection reflection = new Reflection();
      runner = reflection.getRunner(resource);

      if (runner == null) {
        runner = new HelpRunner(resource);
      }
    }

    // Run to create template structure
    runner.globalRun();
  }

  /**
   * Set log level based on the LOG_LEVEL configuration parameter.
   * Possible values: ALL/TRACE/DEBUG/INFO/WARN/ERROR/OFF
   *
   * @throws IOException
   *           - IOException
   */
  private static void setLogLevel() throws IOException {
    final ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory
        .getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);

    // set default INFO log level to avoid logging from ConfigUtil
    rootLogger.setLevel(Level.INFO);

    // Get Config Properties from config file
    final Properties configProps = ConfigUtil.getConfigProperties();
    final String logLevel = configProps.getProperty(Constants.CONFIGPROP_LOG_LEVEL);

    if (!Level.INFO.toString().equalsIgnoreCase(logLevel)) {
      if (Level.ALL.toString().equalsIgnoreCase(logLevel)) {
        rootLogger.setLevel(Level.ALL);
      } else if (Level.TRACE.toString().equalsIgnoreCase(logLevel)) {
        rootLogger.setLevel(Level.TRACE);
      } else if (Level.DEBUG.toString().equalsIgnoreCase(logLevel)) {
        rootLogger.setLevel(Level.DEBUG);
      } else if (Level.WARN.toString().equalsIgnoreCase(logLevel)) {
        rootLogger.setLevel(Level.WARN);
      } else if (Level.ERROR.toString().equalsIgnoreCase(logLevel)) {
        rootLogger.setLevel(Level.ERROR);
      } else if (Level.OFF.toString().equalsIgnoreCase(logLevel)) {
        rootLogger.setLevel(Level.OFF);
      }
    }
    LOG.debug("Current LOG Level: {}", rootLogger.getLevel());
  }

}