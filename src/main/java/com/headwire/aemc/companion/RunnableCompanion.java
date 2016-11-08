package com.headwire.aemc.companion;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemc.menu.BasisRunner;
import com.headwire.aemc.menu.ComponentRunner;
import com.headwire.aemc.menu.ModelRunner;
import com.headwire.aemc.menu.OsgiRunner;
import com.headwire.aemc.menu.ServiceRunner;
import com.headwire.aemc.menu.ServletRunner;
import com.headwire.aemc.menu.TemplateRunner;
import com.headwire.aemc.util.HelpUtil;


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
}