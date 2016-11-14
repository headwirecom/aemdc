package com.headwire.aemdc.companion;

import java.util.ArrayList;
import java.util.List;


/**
 * Constants
 */
public class Constants {

  public static final String ENCODING = "UTF-8";

  public static final String CONFIG_FILENAME = "aemdc-config.properties";
  public static final String FILE_EXT_XML = "xml";
  public static final String FILE_EXT_JAVA = "java";
  public static final String FILE_EXT_JSP = "jsp";
  public static final String FILE_EXT_JS = "js";
  public static final String FILE_EXT_CSS = "css";
  public static final String[] FILES_PH_EXTENSIONS_DEFAULT = { FILE_EXT_XML, FILE_EXT_JAVA, FILE_EXT_JSP, FILE_EXT_JS,
      FILE_EXT_CSS };

  // configuration constants: source folders
  public static final String CONFIGPROP_SOURCE_FOLDER = "SOURCE_FOLDER";
  public static final String CONFIGPROP_SOURCE_UI_FOLDER = "SOURCE_UI_FOLDER";
  public static final String CONFIGPROP_SOURCE_PROJECT_ROOT = "SOURCE_PROJECT_ROOT";
  public static final String CONFIGPROP_SOURCE_JAVA_FOLDER = "SOURCE_JAVA_FOLDER";
  public static final String CONFIGPROP_SOURCE_TEMPLATES_FOLDER = "SOURCE_TEMPLATES_FOLDER";
  public static final String CONFIGPROP_SOURCE_COMPONENTS_FOLDER = "SOURCE_COMPONENTS_FOLDER";
  public static final String CONFIGPROP_SOURCE_OSGI_FOLDER = "SOURCE_OSGI_FOLDER";
  public static final String CONFIGPROP_SOURCE_CONF_FOLDER = "SOURCE_CONF_FOLDER";
  public static final String CONFIGPROP_SOURCE_MODELS_FOLDER = "SOURCE_MODELS_FOLDER";
  public static final String CONFIGPROP_SOURCE_SERVICES_FOLDER = "SOURCE_SERVICES_FOLDER";
  public static final String CONFIGPROP_SOURCE_SERVLETS_FOLDER = "SOURCE_SERVLETS_FOLDER";

  // configuration constants: target folders
  public static final String CONFIGPROP_TARGET_UI_FOLDER = "TARGET_UI_FOLDER";
  public static final String CONFIGPROP_TARGET_PROJECT_NAME = "TARGET_PROJECT_NAME";
  public static final String CONFIGPROP_TARGET_PROJECT_ROOT = "TARGET_PROJECT_ROOT";
  public static final String CONFIGPROP_TARGET_JAVA_FOLDER = "TARGET_JAVA_FOLDER";
  public static final String CONFIGPROP_TARGET_JAVA_PACKAGE = "TARGET_JAVA_PACKAGE";
  public static final String CONFIGPROP_TARGET_TEMPLATES_FOLDER = "TARGET_TEMPLATES_FOLDER";
  public static final String CONFIGPROP_TARGET_COMPONENTS_FOLDER = "TARGET_COMPONENTS_FOLDER";
  public static final String CONFIGPROP_TARGET_OSGI_FOLDER = "TARGET_OSGI_FOLDER";
  public static final String CONFIGPROP_TARGET_CONF_FOLDER = "TARGET_CONF_FOLDER";
  public static final String CONFIGPROP_TARGET_MODELS_FOLDER = "TARGET_MODELS_FOLDER";
  public static final String CONFIGPROP_TARGET_SERVICES_FOLDER = "TARGET_SERVICES_FOLDER";
  public static final String CONFIGPROP_TARGET_SERVLETS_FOLDER = "TARGET_SERVLETS_FOLDER";

  // configuration constants: others
  public static final String CONFIGPROP_FILES_WITH_PLACEHOLDERS_EXTENSIONS = "FILES_WITH_PLACEHOLDERS_EXTENSIONS";
  public static final String CONFIGPROP_EXISTING_DESTINATION_RESOURCES_REPLACEMENT = "EXISTING_DESTINATION_RESOURCES_REPLACEMENT";
  public static final String CONFIGPROP_LOG_LEVEL = "LOG_LEVEL";

  // configuration constants values
  public static final String EXISTING_DESTINATION_RESOURCES_WARN = "WARN";
  public static final String EXISTING_DESTINATION_RESOURCES_DELETE = "DELETE";
  public static final String EXISTING_DESTINATION_RESOURCES_MERGE = "MERGE";

  // params constants
  public static final String PARAM_HELP = "help";
  public static final String PARAM_TYPE = "type";
  public static final String PARAM_TEMPLATE_NAME = "templateName";
  public static final String PARAM_TARGET_NAME = "targetName";

  public static final String TYPE_CONFIG_PROPS = "config";
  public static final String TYPE_TEMPLATE = "temp";
  public static final String TYPE_TEMPLATE_FULL = "template";
  public static final String TYPE_COMPONENT = "comp";
  public static final String TYPE_COMPONENT_FULL = "component";
  public static final String TYPE_OSGI = "osgi";
  public static final String TYPE_EDITABLE_TEMPLATE_STRUCTURE = "confstr";
  public static final String TYPE_MODEL = "model";
  public static final String TYPE_SERVICE = "service";
  public static final String TYPE_SERVLET = "servlet";
  public static List<String> TYPE_APPS_UI_LIST = new ArrayList<String>();
  public static List<String> TYPE_CONF_UI_LIST = new ArrayList<String>();
  public static List<String> TYPE_CORE_LIST = new ArrayList<String>();

  // arguments constants
  public static final String PARAM_RUNMODE = "runmode";
  public static final String PARAM_PROP_JCR_TITLE = "jcr:title";
  public static final String PARAM_PROP_JCR_DESCRIPTION = "jcr:description";
  public static final String PARAM_PROP_RANKING = "ranking";
  public static final String PARAM_PROP_ALLOWED_PATHS = "allowedPaths";
  public static final String PARAM_PROP_SLING_RESOURCE_TYPE = "sling:resourceType";
  public static final String PARAM_PROP_SLING_RESOURCE_SUPER_TYPE = "sling:resourceSuperType";

  public static final String PLACEHOLDERS_PROPS_SET_COMMON = "common";
  public static final String PLACEHOLDERS_PROPS_SET_PREFIX = "ph_";
  public static final String PLACEHOLDER_JAVA_PACKAGE = "{{ java-package }}";
  public static final String PLACEHOLDER_JAVA_CLASS = "{{ java-class }}";

  // default values
  public static final String DEFAULT_RANKING = "{Long}100";
  public static final String DEFAULT_SLING_RESOURCE_SUPER_TYPE = "foundation/components/page";
  public static final String DEFAULT_ALLOWED_PATHS = "/content(/.*)?";

  static {
    TYPE_APPS_UI_LIST.add(TYPE_TEMPLATE);
    TYPE_APPS_UI_LIST.add(TYPE_TEMPLATE_FULL);
    TYPE_APPS_UI_LIST.add(TYPE_COMPONENT);
    TYPE_APPS_UI_LIST.add(TYPE_COMPONENT_FULL);
    TYPE_APPS_UI_LIST.add(TYPE_OSGI);

    TYPE_CONF_UI_LIST.add(TYPE_EDITABLE_TEMPLATE_STRUCTURE);

    TYPE_CORE_LIST.add(TYPE_MODEL);
    TYPE_CORE_LIST.add(TYPE_SERVICE);
    TYPE_CORE_LIST.add(TYPE_SERVLET);
  }
}