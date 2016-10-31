package com.headwire.aemc.companion;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemc.menu.BasisRunner;
import com.headwire.aemc.menu.OsgiRunner;
import com.headwire.aemc.menu.TemplateRunner;
import com.headwire.aemc.util.Utils;


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
    if (args == null || args.length < 3) {
      LOG.info(Utils.getHelpText());
      return;
    }

    // Set mandatories from arguments
    final Resource resource = new Resource();
    resource.setType(args[0]);
    resource.setSourceName(args[1]);
    resource.setTargetName(args[2]);

    // Set JCR Properties from arguments
    resource.setJcrProperties(convertArgsToMaps(args, 3));

    // Get Config Properties from config file
    final Properties configProps = Utils.getConfigProperties(true);

    // Set target project jcr path from config file
    final String targetProjectJcrPath = configProps.getProperty(Constants.CONFIGPROP_TARGET_PROJECT_JCR_PATH);
    resource.setTargetProjectJcrPath(targetProjectJcrPath);

    // Set extentions from config file
    final String extentionsAsString = configProps.getProperty(Constants.CONFIGPROP_FILES_WITH_PLACEHOLDERS_EXTENSIONS);
    String[] extentions = Constants.FILES_PH_EXTENSIONS_DEFAULT;
    if (StringUtils.isNotBlank(extentionsAsString)) {
      extentions = extentionsAsString.split(",");
    }
    resource.setExtentions(extentions);

    // Set overwriting methods from config file
    if (Constants.EXISTING_DESTINATION_RESOURCES_WARN
        .equals(configProps.getProperty(Constants.CONFIGPROP_EXISTING_DESTINATION_RESOURCES))) {
      resource.setToWarnDestDir(true);
    } else if (Constants.EXISTING_DESTINATION_RESOURCES_DELETE
        .equals(configProps.getProperty(Constants.CONFIGPROP_EXISTING_DESTINATION_RESOURCES))) {
      resource.setToDeleteDestDir(true);
    }

    // Set source and destination paths from config file
    BasisRunner runner;
    switch (resource.getType()) {
      case Constants.TYPE_TEMPLATE:
        resource.setSourceFolderPath(configProps.getProperty(Constants.CONFIGPROP_SOURCE_TEMPLATES_FOLDER));
        resource.setTargetFolderPath(configProps.getProperty(Constants.CONFIGPROP_TARGET_TEMPLATES_FOLDER));
        runner = new TemplateRunner(resource);
        break;
      case Constants.TYPE_COMPONENT:
        resource.setSourceFolderPath(configProps.getProperty(Constants.CONFIGPROP_SOURCE_COMPONENTS_FOLDER));
        resource.setTargetFolderPath(configProps.getProperty(Constants.CONFIGPROP_TARGET_COMPONENTS_FOLDER));
        // runner = new ComponentRunner(resource);
        runner = new TemplateRunner(resource);
        break;
      case Constants.TYPE_OSGI:
        resource.setSourceFolderPath(configProps.getProperty(Constants.CONFIGPROP_SOURCE_OSGI_FOLDER));
        // config.runmode to the target path
        final Map<String, String> commonJcrProps = resource.getJcrPropsSet(Constants.PLACEHOLDERS_PROPS_SET_COMMON);
        final String runmode = commonJcrProps.get(Constants.PARAM_RUNMODE);
        if (StringUtils.isNotBlank(runmode)) {
          resource
              .setTargetFolderPath(configProps.getProperty(Constants.CONFIGPROP_TARGET_OSGI_FOLDER) + "." + runmode);
        } else {
          resource.setTargetFolderPath(configProps.getProperty(Constants.CONFIGPROP_TARGET_OSGI_FOLDER));
        }
        runner = new OsgiRunner(resource);
        break;
      default:
        LOG.info(Utils.getHelpText());
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
        // LOG.info("ph_key=" + ph_key + ", value=" + value);
        jcrPropAllSets.put(phSetKey, jcrPropsSet);
      } else {
        jcrPropsCommon.put(key, value);
        // LOG.info("key=" + key + ", value=" + value);
      }
    }
    jcrPropAllSets.put(Constants.PLACEHOLDERS_PROPS_SET_COMMON, jcrPropsCommon);

    return jcrPropAllSets;
  }
}