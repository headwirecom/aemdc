package com.headwire.aemdc.replacer;

import java.io.File;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.companion.Config;
import com.headwire.aemdc.companion.Resource;


/**
 * AEMDC Config properties place holders replacer.
 *
 */
public class ConfigPropsReplacer extends Replacer {

  private static final Logger LOG = LoggerFactory.getLogger(ConfigPropsReplacer.class);

  // lazybones props
  // projectName=My AEM Project
  // contentFolderName=mynewproject
  public static final String LAZYBONES_PROP_USE_NEW_NAMING_CONVENTION = "useNewNamingConvention";
  public static final String LAZYBONES_PROP_BUNDLE_IN_BUNDLES_DIR = "bundleInBundlesDirectory";
  public static final String LAZYBONES_PROP_APPS_FOLDER_NAME = "appsFolderName";
  public static final String LAZYBONES_PROP_CONF_FOLDER_NAME = "confFolderName";
  public static final String LAZYBONES_PROP_DESIGN_FOLDER_NAME = "designFolderName";
  public static final String LAZYBONES_PROP_SLING_MODELS_PACKAGRE = "slingModelsPackage";
  public static final String LAZYBONES_PROP_GROUP_ID = "groupId";

  // path placeholders
  public static final String PLACEHOLDER_TARGET_PROJECT_APPS_FOLDER = "PH_TARGET_PROJECT_APPS_FOLDER";
  public static final String PLACEHOLDER_TARGET_PROJECT_CONF_FOLDER = "PH_TARGET_PROJECT_CONF_FOLDER";
  public static final String PLACEHOLDER_TARGET_PROJECT_DESIGN_FOLDER = "PH_TARGET_PROJECT_DESIGN_FOLDER";
  public static final String PLACEHOLDER_TARGET_UI_PROJECT_FOLDER = "PH_TARGET_UI_PROJECT_FOLDER";
  public static final String PLACEHOLDER_TARGET_CORE_PROJECT_FOLDER = "PH_TARGET_CORE_PROJECT_FOLDER";
  public static final String PLACEHOLDER_TARGET_OSGI_SUBFOLDER = "PH_TARGET_OSGI_SUBFOLDER";
  public static final String PLACEHOLDER_TARGET_JAVA_PACKAGE = "PH_TARGET_JAVA_PACKAGE";
  public static final String PLACEHOLDER_TARGET_JAVA_PACKAGE_FOLDER = "PH_TARGET_JAVA_PACKAGE_FOLDER";
  public static final String PLACEHOLDER_TARGET_JAVA_MODEL_SUBPACKAGE = "PH_TARGET_JAVA_MODEL_SUBPACKAGE";

  // default values
  public static final String PH_DEFAULT_TARGET_PROJECT_APPS_FOLDER = "my-aem-project";
  public static final String PH_DEFAULT_TARGET_UI_PROJECT_FOLDER = "ui.apps";
  public static final String PH_DEFAULT_TARGET_UI_PROJECT_FOLDER_OLD = "content";
  public static final String PH_DEFAULT_TARGET_CORE_PROJECT_FOLDER = "core";
  public static final String PH_DEFAULT_TARGET_CORE_PROJECT_FOLDER_OLD = "bundle";
  public static final String PH_DEFAULT_TARGET_CORE_BUNDLES_SUBFOLDER = "bundles";
  public static final String PH_DEFAULT_TARGET_JAVA_PACKAGE = "com.headwire.aemdc.samples";
  public static final String PH_DEFAULT_TARGET_JAVA_MODEL_SUBPACKAGE = "model";
  public static final String PH_DEFAULT_TARGET_OSGI_SUBFOLDER = "/configuration";

  /**
   * Constructor
   */
  public ConfigPropsReplacer(final Resource resource, final Config config) {
    this.resource = resource;
    this.config = config;
  }

  @Override
  protected String replaceCustomXmlPlaceHolders(final String text, final Map<String, String> placeholders) {
    return text;
  }

  @Override
  protected String replaceCustomTextPlaceHolders(final String text, final Map<String, String> placeholders,
      final File targetFile) {
    String result = text;

    // Get lazybones properties
    final Properties lazybonesProps = config.getLazybonesProperties();

    // apps folder name
    String appsFolderName = lazybonesProps.getProperty(LAZYBONES_PROP_APPS_FOLDER_NAME);
    if (StringUtils.isBlank(appsFolderName)) {
      appsFolderName = PH_DEFAULT_TARGET_PROJECT_APPS_FOLDER;
    }
    result = result.replace(getPH(PLACEHOLDER_TARGET_PROJECT_APPS_FOLDER), appsFolderName);

    // conf folder name
    String confFolderName = lazybonesProps.getProperty(LAZYBONES_PROP_CONF_FOLDER_NAME);
    if (StringUtils.isBlank(confFolderName)) {
      confFolderName = PH_DEFAULT_TARGET_PROJECT_APPS_FOLDER;
    }
    result = result.replace(getPH(PLACEHOLDER_TARGET_PROJECT_CONF_FOLDER), confFolderName);

    // design folder name
    String designFolderName = lazybonesProps.getProperty(LAZYBONES_PROP_DESIGN_FOLDER_NAME);
    if (StringUtils.isBlank(designFolderName)) {
      designFolderName = PH_DEFAULT_TARGET_PROJECT_APPS_FOLDER;
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
    String osgiConfigFolder = PH_DEFAULT_TARGET_OSGI_SUBFOLDER;
    if (!lazybonesProps.isEmpty()) {
      osgiConfigFolder = "";
    }
    result = result.replace(getPH(PLACEHOLDER_TARGET_OSGI_SUBFOLDER), osgiConfigFolder);

    // target java package & sling models subpackage
    String javaTargetPackage = lazybonesProps.getProperty(LAZYBONES_PROP_SLING_MODELS_PACKAGRE);
    String slingModelsSubPackage = PH_DEFAULT_TARGET_JAVA_MODEL_SUBPACKAGE;
    if (StringUtils.isBlank(javaTargetPackage)) {
      final String groupId = lazybonesProps.getProperty(LAZYBONES_PROP_GROUP_ID);
      if (StringUtils.isBlank(groupId)) {
        javaTargetPackage = PH_DEFAULT_TARGET_JAVA_PACKAGE;
      } else {
        javaTargetPackage = groupId.replace('-', '.');
        if (javaTargetPackage.indexOf('.') > 0) {
          slingModelsSubPackage = StringUtils.substringAfterLast(javaTargetPackage, ".");
          javaTargetPackage = StringUtils.substringBeforeLast(javaTargetPackage, ".");
        }
      }
    } else {
      if (javaTargetPackage.indexOf('.') > 0) {
        slingModelsSubPackage = StringUtils.substringAfterLast(javaTargetPackage, ".");
        javaTargetPackage = StringUtils.substringBeforeLast(javaTargetPackage, ".");
      }
    }
    result = result.replace(getPH(PLACEHOLDER_TARGET_JAVA_PACKAGE), javaTargetPackage);
    result = result.replace(getPH(PLACEHOLDER_TARGET_JAVA_MODEL_SUBPACKAGE), slingModelsSubPackage);

    // target java package folder
    final String javaTargetPackageFolder = javaTargetPackage.replace('.', '/');
    result = result.replace(getPH(PLACEHOLDER_TARGET_JAVA_PACKAGE_FOLDER), javaTargetPackageFolder);

    return result;
  }

}