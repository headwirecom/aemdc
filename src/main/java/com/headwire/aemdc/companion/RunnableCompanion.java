package com.headwire.aemdc.companion;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.menu.BasisRunner;
import com.headwire.aemdc.menu.ComponentRunner;
import com.headwire.aemdc.menu.EditableTemplateStructureRunner;
import com.headwire.aemdc.menu.ModelRunner;
import com.headwire.aemdc.menu.OsgiRunner;
import com.headwire.aemdc.menu.ServiceRunner;
import com.headwire.aemdc.menu.ServletRunner;
import com.headwire.aemdc.menu.TemplateRunner;
import com.headwire.aemdc.util.ConfigUtil;
import com.headwire.aemdc.util.HelpUtil;

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

    // Set source and destination paths from config file
    BasisRunner runner;
    switch (resource.getType()) {
      case Constants.TYPE_TEMPLATE:
      case Constants.TYPE_TEMPLATE_FULL:
        runner = new TemplateRunner(resource);
        break;
      case Constants.TYPE_COMPONENT:
      case Constants.TYPE_COMPONENT_FULL:
        runner = new ComponentRunner(resource);
        break;
      case Constants.TYPE_OSGI:
        runner = new OsgiRunner(resource);
        break;
      case Constants.TYPE_EDITABLE_TEMPLATE_STRUCTURE:
        runner = new EditableTemplateStructureRunner(resource);
        break;
      case Constants.TYPE_MODEL:
        runner = new ModelRunner(resource);
        break;
      case Constants.TYPE_SERVICE:
        runner = new ServiceRunner(resource);
        break;
      case Constants.TYPE_SERVLET:
        runner = new ServletRunner(resource);
        break;
      default:
        HelpUtil.showHelp(args);
        return;
    }

    // create structure
    runner.run();
  }

  /**
   * Convert arguments to jcr properties with values
   *
   * @param args
   *          - arguments
   * @return map of jcr properties sets with values
   */
  private static Map<String, Map<String, String>> convertArgsToMaps(final String[] args, final int start) {
    final Map<String, Map<String, String>> jcrPropAllSets = new HashMap<String, Map<String, String>>();

    // common jcr props set
    final Map<String, String> jcrPropsCommon = new HashMap<String, String>();
    for (int i = start; i < args.length; i++) {
      final String[] splited = args[i].split("=");
      final String key = splited[0];
      final String value = splited[1];

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
    jcrPropAllSets.put(Constants.PLACEHOLDERS_PROPS_SET_COMMON, jcrPropsCommon);

    return jcrPropAllSets;
  }

  /**
   * Set log level based on the LOG_LEVEL configuration param.
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