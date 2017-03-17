package com.headwire.aemdc.runner;

import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.companion.Config;
import com.headwire.aemdc.companion.Constants;
import com.headwire.aemdc.companion.Reflection;
import com.headwire.aemdc.companion.Resource;
import com.headwire.aemdc.replacer.Replacer;


/**
 * Compound Runner for set of different template types.
 *
 */
public class CompoundRunner extends BasisRunner {

  private static final Logger LOG = LoggerFactory.getLogger(CompoundRunner.class);
  private static final String HELP_FOLDER = "help";

  /**
   * Invoker
   */
  private final List<BasisRunner> runners = new ArrayList<BasisRunner>();
  private final Config config;

  /**
   * Constructor
   *
   * @param pResource
   *          - resource object
   * @throws IOException
   */
  public CompoundRunner(final Resource pResource, final Config pConfig) {
    resource = pResource;
    config = pConfig;

    LOG.debug("Compound runner starting...");

    final Properties dynProps = config.getDynamicProperties(resource.getType(), resource.getSourceName());
    if (dynProps == null) {
      LOG.error("Unknown <type>=[{}] and [name]=[{}] argument.", resource.getType(), resource.getSourceName());
      return;
    }
    resource.setSourceFolderPath(dynProps.getProperty(Constants.DYN_CONFIGPROP_SOURCE_TYPE_FOLDER));

    if (StringUtils.isNotBlank(resource.getSourceName())) {
      // get compound template list
      final List<SimpleEntry<String, String>> compoundList = config.getCompoundList(resource.getSourceName());
      if (compoundList.isEmpty()) {
        LOG.error("Can't get compound list for template name [{}].", resource.getSourceName());
      }

      for (final SimpleEntry<String, String> entry : compoundList) {
        final String templateType = entry.getKey();
        final String templateName = entry.getValue();

        // Creates Invoker object, command object and configure them
        final Resource templateResource = resource.clone();
        templateResource.setType(templateType);
        templateResource.setSourceName(templateName);

        // Get Runner
        final Reflection reflection = new Reflection(config);
        final BasisRunner runner = reflection.getRunner(templateResource);
        if (runner != null) {
          runners.add(runner);
        } else {
          LOG.error("Unknown configurated compound <type>:<name>={}:{}.", templateType, templateName);
        }
      }
    }
  }

  /**
   * Run commands
   *
   * @throws IOException
   */
  @Override
  public void run() throws IOException {
    // Run to create template structure
    for (final BasisRunner runner : runners) {
      runner.run();
    }
  }

  @Override
  public String getHelpFolder() {
    final String helpPath = config.getProperty(Constants.CONFIGPROP_SOURCE_TYPES_FOLDER) + "/"
        + resource.getType() + "/" + HELP_FOLDER;
    return helpPath;
  }

  @Override
  public String getTemplateHelpFolder() {
    String helpPath = getHelpFolder();
    if (StringUtils.isNoneBlank(resource.getSourceName())) {
      helpPath = config.getProperty(Constants.CONFIGPROP_SOURCE_TYPES_FOLDER) + "/"
          + resource.getType() + "/" + resource.getSourceName() + "/" + HELP_FOLDER;
    }
    return helpPath;
  }

  @Override
  public String getSourceFolder() {
    return resource.getSourceFolderPath();
  }

  @Override
  public Replacer getPlaceHolderReplacer() {
    return null;
  }

  /**
   * Get compound runners
   *
   * @return the runners
   */
  public List<BasisRunner> getRunners() {
    return runners;
  }

}
