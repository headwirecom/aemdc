package com.headwire.aemdc.companion;

import java.util.ArrayList;
import java.util.List;


/**
 * Constants
 */
public class Constants {

  public static final String ENCODING = "UTF-8";

  public static final String DYNAMIC_CONFIG_PROPS_FILENAME = "config.properties";
  public static final String LAZYBONES_CONFIG_PROPS_FILE_PATH = ".lazybones/stored-params.properties";
  public static final String REFLECTION_PROPS_FILE_PATH = "reflection/typeRunner.properties";
  public static final String FILE_EXT_XML = "xml";
  public static final String FILE_EXT_JAVA = "java";
  public static final String FILE_EXT_HTML = "html";
  public static final String FILE_EXT_JSP = "jsp";
  public static final String FILE_EXT_JS = "js";
  public static final String FILE_EXT_CSS = "css";
  public static final String FILE_EXT_PROPS = "properties";
  // ALSO ADD EXTENSION TO THE PROPERTIES FILE!!!
  public static final String[] FILES_PH_EXTENSIONS_DEFAULT = { FILE_EXT_XML, FILE_EXT_JAVA, FILE_EXT_HTML, FILE_EXT_JSP,
      FILE_EXT_JS, FILE_EXT_CSS, FILE_EXT_PROPS };

  // configuration constants: source folders
  public static final String CONFIGPROP_SOURCE_FOLDER = "SOURCE_FOLDER";
  public static final String CONFIGPROP_SOURCE_TYPES_FOLDER = "SOURCE_TYPES_FOLDER";

  // configuration constants: target folders
  // public static final String CONFIGPROP_TARGET_PROJECT_NAME = "TARGET_PROJECT_NAME";
  public static final String CONFIGPROP_TARGET_UI_FOLDER = "TARGET_UI_FOLDER";
  public static final String CONFIGPROP_TARGET_PROJECT_APPS_FOLDER = "TARGET_PROJECT_APPS_FOLDER";
  public static final String CONFIGPROP_TARGET_PROJECT_CONF_FOLDER = "TARGET_PROJECT_CONF_FOLDER";
  public static final String CONFIGPROP_TARGET_PROJECT_DESIGN_FOLDER = "TARGET_PROJECT_DESIGN_FOLDER";
  public static final String CONFIGPROP_TARGET_PROJECT_ROOT = "TARGET_PROJECT_ROOT";
  public static final String CONFIGPROP_TARGET_JAVA_FOLDER = "TARGET_JAVA_FOLDER";
  public static final String CONFIGPROP_TARGET_JAVA_PACKAGE = "TARGET_JAVA_PACKAGE";
  public static final String CONFIGPROP_TARGET_JAVA_PACKAGE_FOLDER = "TARGET_JAVA_PACKAGE_FOLDER";
  public static final String CONFIGPROP_TARGET_JAVA_MODEL_SUBPACKAGE = "TARGET_JAVA_MODEL_SUBPACKAGE";
  public static final String CONFIGPROP_TARGET_OSGI_SUBFOLDER = "TARGET_OSGI_SUBFOLDER";

  // configuration constants: others
  public static final String CONFIGPROP_FILES_WITH_PLACEHOLDERS_EXTENSIONS = "FILES_WITH_PLACEHOLDERS_EXTENSIONS";
  public static final String CONFIGPROP_FORBIDDEN_TEMPLATE_TYPES = "FORBIDDEN_TEMPLATE_TYPES";
  public static final String CONFIGPROP_EXISTING_DESTINATION_RESOURCES_REPLACEMENT = "EXISTING_DESTINATION_RESOURCES_REPLACEMENT";
  public static final String CONFIGPROP_LOG_LEVEL = "LOG_LEVEL";

  public static final List<String> SOURCE_PATHS = new ArrayList<String>();
  public static final List<String> CONFIGPROPS_OTHER = new ArrayList<String>();
  static {
    SOURCE_PATHS.add(CONFIGPROP_SOURCE_FOLDER);
    SOURCE_PATHS.add(CONFIGPROP_SOURCE_TYPES_FOLDER);

    CONFIGPROPS_OTHER.add(CONFIGPROP_TARGET_UI_FOLDER);
    CONFIGPROPS_OTHER.add(CONFIGPROP_TARGET_PROJECT_APPS_FOLDER);
    CONFIGPROPS_OTHER.add(CONFIGPROP_TARGET_PROJECT_CONF_FOLDER);
    CONFIGPROPS_OTHER.add(CONFIGPROP_TARGET_PROJECT_DESIGN_FOLDER);
    CONFIGPROPS_OTHER.add(CONFIGPROP_TARGET_PROJECT_ROOT);
    CONFIGPROPS_OTHER.add(CONFIGPROP_TARGET_JAVA_FOLDER);
    CONFIGPROPS_OTHER.add(CONFIGPROP_TARGET_JAVA_PACKAGE);
    CONFIGPROPS_OTHER.add(CONFIGPROP_TARGET_JAVA_PACKAGE_FOLDER);
    CONFIGPROPS_OTHER.add(CONFIGPROP_TARGET_JAVA_MODEL_SUBPACKAGE);
    CONFIGPROPS_OTHER.add(CONFIGPROP_FILES_WITH_PLACEHOLDERS_EXTENSIONS);
    CONFIGPROPS_OTHER.add(CONFIGPROP_FORBIDDEN_TEMPLATE_TYPES);
    CONFIGPROPS_OTHER.add(CONFIGPROP_EXISTING_DESTINATION_RESOURCES_REPLACEMENT);
    CONFIGPROPS_OTHER.add(CONFIGPROP_LOG_LEVEL);
  }

  // dynamic configuration constants
  public static final String DYN_CONFIGPROP_SOURCE_TYPE_FOLDER = "SOURCE_TYPE_FOLDER";
  public static final String DYN_CONFIGPROP_TARGET_TYPE_FOLDER = "TARGET_TYPE_FOLDER";
  public static final String DYN_CONFIGPROP_COMMAND_MENU = "COMMAND_MENU";
  public static final String DYN_CONFIGPROP_TEMPLATE_STRUCTURE = "TEMPLATE_STRUCTURE";
  public static final String DYN_CONFIGPROP_COMPOUND = "COMPOUND";
  public static final List<String> DYN_SOURCE_PATHS = new ArrayList<String>();
  public static final List<String> DYN_CONFIGPROPS_OTHER = new ArrayList<String>();
  static {
    DYN_SOURCE_PATHS.add(DYN_CONFIGPROP_SOURCE_TYPE_FOLDER);

    DYN_CONFIGPROPS_OTHER.add(DYN_CONFIGPROP_TARGET_TYPE_FOLDER);
    DYN_CONFIGPROPS_OTHER.add(DYN_CONFIGPROP_COMMAND_MENU);
    DYN_CONFIGPROPS_OTHER.add(DYN_CONFIGPROP_TEMPLATE_STRUCTURE);
  }

  // configuration constants values
  public static final String EXISTING_DESTINATION_RESOURCES_WARN = "WARN";
  public static final String EXISTING_DESTINATION_RESOURCES_DELETE = "DELETE";
  public static final String EXISTING_DESTINATION_RESOURCES_MERGE = "MERGE";

  // template types
  public static final String TEMPLATE_STRUCTURE_DIR = "DIR";
  public static final String TEMPLATE_STRUCTURE_FILE = "FILE";

  // params constants
  public static final String PARAM_OPTION_HELP = "help";
  public static final String PARAM_OPTION_TEMP = "-temp";
  public static final String PARAM_TYPE = "type";
  public static final String PARAM_TEMPLATE_NAME = "templateName";
  public static final String PARAM_TARGET_NAME = "targetName";
  public static final String PARAM_PROJECT_ROOT = "projectroot";

  // static template types
  public static final String TYPES_STATIC_FOLDER = "types";
  public static final String TYPE_CONFIG_PROPS = "config";
  public static final String TYPE_OSGI = "osgi";
  public static final String TYPE_COMPOUND = "compound";

  // forbidden template types
  public static final String FORBIDDEN_TYPE_GIT = ".git";
  public static final String FORBIDDEN_TYPE_SETTINGS = ".settings";
  public static final String FORBIDDEN_TYPE_TARGET = "target";
  public static final String FORBIDDEN_TYPE_HELP = "help";
  // ALSO ADD FORBIDDEN TYPES TO THE PROPERTIES FILE!!!
  public static final String[] FORBIDDEN_TYPES_DEFAULT = { FORBIDDEN_TYPE_GIT, FORBIDDEN_TYPE_SETTINGS,
      FORBIDDEN_TYPE_TARGET, FORBIDDEN_TYPE_HELP };

  // arguments constants
  public static final String PLACEHOLDER_PROPS_SET_COMMON = "common";
  public static final String PLACEHOLDER_PROPS_SET_PREFIX = "ph_";
  public static final String PLACEHOLDER_RUNMODE = "runmode";
  public static final String PLACEHOLDER_JAVA_PACKAGE = "java-package";
  public static final String PLACEHOLDER_JAVA_INTERFACE_PACKAGE = "java-interface-package";
  public static final String PLACEHOLDER_JAVA_CLASS = "java-class";
  public static final String PLACEHOLDER_TARGET_NAME = "targetname";
  public static final String PLACEHOLDER_TARGET_COMP_MODEL = "targetCompModel";
  public static final List<String> FORBIDDEN_PLACEHOLDERS = new ArrayList<String>();
  static {
    FORBIDDEN_PLACEHOLDERS.add(PLACEHOLDER_JAVA_PACKAGE);
    FORBIDDEN_PLACEHOLDERS.add(PLACEHOLDER_JAVA_INTERFACE_PACKAGE);
    FORBIDDEN_PLACEHOLDERS.add(PLACEHOLDER_TARGET_NAME);
    FORBIDDEN_PLACEHOLDERS.add(PLACEHOLDER_TARGET_COMP_MODEL);
  }

  // default values
}