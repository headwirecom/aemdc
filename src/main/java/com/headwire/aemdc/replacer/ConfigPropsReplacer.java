package com.headwire.aemdc.replacer;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.companion.Constants;
import com.headwire.aemdc.companion.Resource;
import com.headwire.aemdc.util.PropsUtil;


/**
 * AEMDC Config properties place holders replacer.
 *
 */
public class ConfigPropsReplacer extends Replacer {

  private static final Logger LOG = LoggerFactory.getLogger(ConfigPropsReplacer.class);

  // lazybones props
  public static final String LAZYBONES_PROP_APPS_FOLDER_NAME = "appsFolderName";
  public static final String LAZYBONES_PROP_GROUP_ID = "groupId";
  public static final String LAZYBONES_PROP_SLING_MODELS_PACKAGRE = "slingModelsPackage";

  // path placeholders
  public static final String PLACEHOLDER_TARGET_PROJECT_NAME = "PH_TARGET_PROJECT_NAME";
  public static final String PLACEHOLDER_TARGET_JAVA_PACKAGE = "PH_TARGET_JAVA_PACKAGE";

  // default values
  public static final String PH_DEFAULT_TARGET_PROJECT_NAME = "my-aem-project";
  public static final String PH_DEFAULT_TARGET_JAVA_PACKAGE = "com/headwire/aemdc/samples";

  /*
  //appsFolderName=my-aem-project
  //groupId=com.headwire.myaem
  //slingModelsPackage=com.headwire.myaem.models
  
  
  
  
  
  
  confFolderName=my-aem-project
  contentFolderName=my-aem-project
  designFolderName=my-aem-project
  
  contentArtifactId=my-aem-project.ui.apps
  bundleArtifactId=my-aem-project.core
  
  useNewNamingConvention=yes
  bundleInBundlesDirectory=no

  createAuthorAndPublishPerEnv=yes
  createRunModeConfigFolders=yes
   */

  /**
   * Constructor
   */
  public ConfigPropsReplacer(final Resource resource) {
    this.resource = resource;
  }

  @Override
  protected String replaceCustomXmlPlaceHolders(final String text, final Map<String, String> placeholders)
      throws IOException {
    return text;
  }

  @Override
  protected String replaceCustomTextPlaceHolders(final String text, final Map<String, String> placeholders)
      throws IOException {
    String result = text;

    // Get lazybones properties
    final Properties props = PropsUtil.getProperties(Constants.LAZYBONES_CONFIG_PROPS_FILE_PATH);

    // appsFolderName
    String appsFolderName = props.getProperty(LAZYBONES_PROP_APPS_FOLDER_NAME);
    if (StringUtils.isBlank(appsFolderName)) {
      appsFolderName = PH_DEFAULT_TARGET_PROJECT_NAME;
    }
    result = result.replace(getPH(PLACEHOLDER_TARGET_PROJECT_NAME), appsFolderName);

    // slingModelsPackage & slingModelsFolderName
    String javaTargetPackage = props.getProperty(LAZYBONES_PROP_SLING_MODELS_PACKAGRE);
    if (StringUtils.isNotBlank(javaTargetPackage)) {
      javaTargetPackage = javaTargetPackage.replace('.', '/');
    } else {
      final String groupId = props.getProperty(LAZYBONES_PROP_GROUP_ID);
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