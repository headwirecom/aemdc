package com.headwire.aemdc.companion;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.menu.BasisRunner;
import com.headwire.aemdc.util.ConfigUtil;
import com.headwire.aemdc.util.HelpUtil;
import com.headwire.aemdc.util.Reflection;

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

    // check for mandatory arguments
    if (args == null || args.length < 3 || Constants.PARAM_HELP.equals(args[0])) {
      HelpUtil.showHelp(args);
      return;
    }

    // Set mandatories from arguments
    final Resource resource = new Resource();
    resource.setType(args[0]);
    resource.setSourceName(args[1]);
    resource.setTargetName(args[2]);

    // Set JCR Properties from arguments
    resource.setJcrProperties(convertArgsToMaps(args, 3));

    // Get Runner
    final Reflection reflection = new Reflection();
    final BasisRunner runner = reflection.getRunner(resource);

    // Run to create template structure
    if (runner != null) {
      runner.run();
    } else {
      HelpUtil.showHelp(args);
    }
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
  private static Map<String, Map<String, String>> convertArgsToMaps(final String[] args, final int start) {
    final Map<String, Map<String, String>> jcrPropAllSets = new HashMap<String, Map<String, String>>();

    // common jcr props set
    final Map<String, String> jcrPropsCommon = new HashMap<String, String>();
    for (int i = start; i < args.length; i++) {

      // check for valid params like "paramName=paramValue"
      final int splitPos = args[i].indexOf("=");
      if (splitPos < 1) {
        throw new IllegalArgumentException("Params must be in form \"paramName=paramValue\"");
      }

      // get param key and value
      final String key = args[i].substring(0, splitPos);
      String value = "";
      if (args[i].length() > (splitPos + 1)) {
        value = args[i].substring(splitPos + 1);
      }

      if (key.startsWith(Constants.PLACEHOLDERS_PROPS_SET_PREFIX)) {
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
    jcrPropAllSets.put(Constants.PLACEHOLDERS_PROPS_SET_COMMON, jcrPropsCommon);

    return jcrPropAllSets;
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
  }
}