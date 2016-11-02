package com.headwire.aemc.companion;

/**
 * Constants
 */
public class Constants {

  public static final String ENCODING = "UTF-8";

  public static final String AEMC_HELP_FILE_PATH = "help.txt";
  public static final String AEMC_FILES_FOLDER = "aemc-placeholders";
  public static final String CONFIG_FILENAME = "config.properties";
  public static final String CONFIG_FILEPATH = AEMC_FILES_FOLDER + "/" + CONFIG_FILENAME;
  public static final String[] FILES_PH_EXTENSIONS_DEFAULT = { "xml", "js", "java", "css" };

  // configuration constants
  public static final String CONFIGPROP_SOURCE_TEMPLATES_FOLDER = "SOURCE_TEMPLATES_FOLDER";
  public static final String CONFIGPROP_SOURCE_COMPONENTS_FOLDER = "SOURCE_COMPONENTS_FOLDER";
  public static final String CONFIGPROP_SOURCE_OSGI_FOLDER = "SOURCE_OSGI_FOLDER";
  public static final String CONFIGPROP_SOURCE_MODELS_FOLDER = "SOURCE_MODELS_FOLDER";
  public static final String CONFIGPROP_SOURCE_SERVICES_FOLDER = "SOURCE_SERVICES_FOLDER";
  public static final String CONFIGPROP_SOURCE_SERVLETS_FOLDER = "SOURCE_SERVLETS_FOLDER";
  public static final String CONFIGPROP_TARGET_TEMPLATES_FOLDER = "TARGET_TEMPLATES_FOLDER";
  public static final String CONFIGPROP_TARGET_COMPONENTS_FOLDER = "TARGET_COMPONENTS_FOLDER";
  public static final String CONFIGPROP_TARGET_OSGI_FOLDER = "TARGET_OSGI_FOLDER";
  public static final String CONFIGPROP_TARGET_MODELS_FOLDER = "TARGET_MODELS_FOLDER";
  public static final String CONFIGPROP_TARGET_SERVICES_FOLDER = "TARGET_SERVICES_FOLDER";
  public static final String CONFIGPROP_TARGET_SERVLETS_FOLDER = "TARGET_SERVLETS_FOLDER";
  public static final String CONFIGPROP_TARGET_PROJECT_JCR_PATH = "TARGET_PROJECT_JCR_PATH";
  public static final String CONFIGPROP_EXISTING_DESTINATION_RESOURCES = "EXISTING_DESTINATION_RESOURCES";
  public static final String CONFIGPROP_FILES_WITH_PLACEHOLDERS_EXTENSIONS = "FILES_WITH_PLACEHOLDERS_EXTENSIONS";

  // configuration constants values
  public static final String EXISTING_DESTINATION_RESOURCES_WARN = "WARN";
  public static final String EXISTING_DESTINATION_RESOURCES_DELETE = "DELETE";
  public static final String EXISTING_DESTINATION_RESOURCES_MERGE = "MERGE";

  // params constants
  public static final String PARAM_TYPE = "type";
  public static final String PARAM_TEMPLATE_NAME = "templateName";
  public static final String PARAM_TARGET_NAME = "targetName";
  public static final String PARAM_SOURCE_PATH = "sourcePath";
  public static final String PARAM_TARGET_PATH = "targetPath";

  public static final String TYPE_TEMPLATE = "temp";
  public static final String TYPE_COMPONENT = "comp";
  public static final String TYPE_OSGI = "osgi";
  public static final String TYPE_MODEL = "model";
  public static final String TYPE_SERVICE = "service";
  public static final String TYPE_SERVLET = "servlet";

  // arguments constants
  public static final String PARAM_RUNMODE = "runmode";
  public static final String PARAM_PROP_JCR_TITLE = "jcr:title";
  public static final String PARAM_PROP_JCR_DESCRIPTION = "jcr:description";
  public static final String PARAM_PROP_RANKING = "ranking";
  public static final String PARAM_PROP_SLING_RESOURCE_TYPE = "sling:resourceType";
  public static final String PARAM_PROP_SLING_RESOURCE_SUPER_TYPE = "sling:resourceSuperType";

  public static final String PLACEHOLDERS_PROPS_SET_COMMON = "common";
  public static final String PLACEHOLDERS_PROPS_SET_PREFIX = "ph_";

  // default values
  public static final String DEFAULT_RANKING = "{Long}90";
  public static final String DEFAULT_SLING_RESOURCE_SUPER_TYPE = "foundation/components/page";

}