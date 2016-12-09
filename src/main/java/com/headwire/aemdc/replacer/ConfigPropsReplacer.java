package com.headwire.aemdc.replacer;

import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.companion.Resource;
import com.headwire.aemdc.util.ConfigUtil;


/**
 * AEMDC Config properties place holders replacer.
 *
 */
public class ConfigPropsReplacer extends Replacer {

  private static final Logger LOG = LoggerFactory.getLogger(ConfigPropsReplacer.class);

  // lazybones props
  public static final String LAZYBONES_PROP_USE_NEW_NAMING_CONVENTION = "useNewNamingConvention";
  public static final String LAZYBONES_PROP_BUNDLE_IN_BUNDLES_DIR = "bundleInBundlesDirectory";
  public static final String LAZYBONES_PROP_APPS_FOLDER_NAME = "appsFolderName";
  public static final String LAZYBONES_PROP_CONF_FOLDER_NAME = "confFolderName";
  public static final String LAZYBONES_PROP_DESIGN_FOLDER_NAME = "designFolderName";
  public static final String LAZYBONES_PROP_SLING_MODELS_PACKAGRE = "slingModelsPackage";
  public static final String LAZYBONES_PROP_GROUP_ID = "groupId";
  // projectName=My AEM Project
  // contentFolderName=mynewproject

  // path placeholders
  public static final String PLACEHOLDER_TARGET_PROJECT_NAME = "PH_TARGET_PROJECT_NAME";
  public static final String PLACEHOLDER_TARGET_PROJECT_CONF_FOLDER = "PH_TARGET_PROJECT_CONF_FOLDER";
  public static final String PLACEHOLDER_TARGET_PROJECT_DESIGN_FOLDER = "PH_TARGET_PROJECT_DESIGN_FOLDER";
  public static final String PLACEHOLDER_TARGET_UI_PROJECT_FOLDER = "PH_TARGET_UI_PROJECT_FOLDER";
  public static final String PLACEHOLDER_TARGET_CORE_PROJECT_FOLDER = "PH_TARGET_CORE_PROJECT_FOLDER";
  public static final String PLACEHOLDER_TARGET_OSGI_FOLDER = "PH_TARGET_OSGI_FOLDER";
  public static final String PLACEHOLDER_TARGET_JAVA_PACKAGE = "PH_TARGET_JAVA_PACKAGE";

  // default values
  public static final String PH_DEFAULT_TARGET_PROJECT_NAME = "my-aem-project";
  public static final String PH_DEFAULT_TARGET_UI_PROJECT_FOLDER = "ui.apps";
  public static final String PH_DEFAULT_TARGET_UI_PROJECT_FOLDER_OLD = "content";
  public static final String PH_DEFAULT_TARGET_CORE_PROJECT_FOLDER = "core";
  public static final String PH_DEFAULT_TARGET_CORE_PROJECT_FOLDER_OLD = "bundle";
  public static final String PH_DEFAULT_TARGET_CORE_BUNDLES_SUBFOLDER = "bundles";
  public static final String PH_DEFAULT_TARGET_JAVA_PACKAGE = "com/headwire/aemdc/samples";
  public static final String PH_DEFAULT_TARGET_OSGI_FOLDER = "/configuration";

  /**
   * Constructor
   */
  public ConfigPropsReplacer(final Resource resource) {
    this.resource = resource;
  }

  @Override
  protected String replaceCustomXmlPlaceHolders(final String text, final Map<String, String> placeholders) {
    return text;
  }

  @Override
  protected String replaceCustomTextPlaceHolders(final String text, final Map<String, String> placeholders) {
    String result = text;

    // Get lazybones properties
    final Properties lazybonesProps = ConfigUtil.getLazybonesProperties();

    // apps folder name
    String appsFolderName = lazybonesProps.getProperty(LAZYBONES_PROP_APPS_FOLDER_NAME);
    if (StringUtils.isBlank(appsFolderName)) {
      appsFolderName = PH_DEFAULT_TARGET_PROJECT_NAME;
    }
    result = result.replace(getPH(PLACEHOLDER_TARGET_PROJECT_NAME), appsFolderName);

    // conf folder name
    String confFolderName = lazybonesProps.getProperty(LAZYBONES_PROP_CONF_FOLDER_NAME);
    if (StringUtils.isBlank(confFolderName)) {
      confFolderName = PH_DEFAULT_TARGET_PROJECT_NAME;
    }
    result = result.replace(getPH(PLACEHOLDER_TARGET_PROJECT_CONF_FOLDER), confFolderName);

    // design folder name
    String designFolderName = lazybonesProps.getProperty(LAZYBONES_PROP_DESIGN_FOLDER_NAME);
    if (StringUtils.isBlank(designFolderName)) {
      designFolderName = PH_DEFAULT_TARGET_PROJECT_NAME;
    }
    result = result.replace(getPH(PLACEHOLDER_TARGET_PROJECT_DESIGN_FOLDER), designFolderName);

    // UI project folder name
    final String useNewNamingConvention = lazybonesProps.getProperty(LAZYBONES_PROP_USE_NEW_NAMING_CONVENTION);
    String targetUIProjectFolder = PH_DEFAULT_TARGET_UI_PROJECT_FOLDER;
    if ("no".equalsIgnoreCase(useNewNamingConvention)) {
      targetUIProjectFolder = PH_DEFAULT_TARGET_UI_PROJECT_FOLDER_OLD;
    }
    result = result.replace(getPH(PLACEHOLDER_TARGET_UI_PROJECT_FOLDER), targetUIProjectFolder);

    // CORE project folder name
    final String bundleInBundlesDirectory = lazybonesProps.getProperty(LAZYBONES_PROP_BUNDLE_IN_BUNDLES_DIR);
    String targetCoreProjectFolder = PH_DEFAULT_TARGET_CORE_PROJECT_FOLDER;
    if ("no".equalsIgnoreCase(useNewNamingConvention)) {
      targetCoreProjectFolder = PH_DEFAULT_TARGET_CORE_PROJECT_FOLDER_OLD;
    }
    if ("yes".equalsIgnoreCase(bundleInBundlesDirectory)) {
      // like "bundles/core" or "bundles/bundle"
      targetCoreProjectFolder = PH_DEFAULT_TARGET_CORE_BUNDLES_SUBFOLDER + "/" + targetCoreProjectFolder;
    }
    result = result.replace(getPH(PLACEHOLDER_TARGET_CORE_PROJECT_FOLDER), targetCoreProjectFolder);

    // osgi configuration folder name
    String osgiConfigFolder = PH_DEFAULT_TARGET_OSGI_FOLDER;
    if (!lazybonesProps.isEmpty()) {
      osgiConfigFolder = "";
    }
    result = result.replace(getPH(PLACEHOLDER_TARGET_OSGI_FOLDER), osgiConfigFolder);

    // target java package
    String javaTargetPackage = lazybonesProps.getProperty(LAZYBONES_PROP_SLING_MODELS_PACKAGRE);
    if (StringUtils.isNotBlank(javaTargetPackage)) {
      javaTargetPackage = javaTargetPackage.replace('.', '/');
    } else {
      final String groupId = lazybonesProps.getProperty(LAZYBONES_PROP_GROUP_ID);
      if (StringUtils.isBlank(groupId)) {
        javaTargetPackage = PH_DEFAULT_TARGET_JAVA_PACKAGE;
      } else {
        javaTargetPackage = groupId.replace('-', '.');
        javaTargetPackage = javaTargetPackage.replace('.', '/');
      }
    }
    result = result.replace(getPH(PLACEHOLDER_TARGET_JAVA_PACKAGE), javaTargetPackage);

    return result;
  }

}