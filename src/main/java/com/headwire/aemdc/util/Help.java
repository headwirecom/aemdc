package com.headwire.aemdc.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.companion.Config;
import com.headwire.aemdc.companion.Constants;
import com.headwire.aemdc.companion.Reflection;
import com.headwire.aemdc.companion.Resource;
import com.headwire.aemdc.replacer.Replacer;
import com.headwire.aemdc.runner.BasisRunner;
import com.headwire.aemdc.runner.CompoundRunner;


/**
 * Help Util
 *
 * @author Marat Saitov, 03.11.2016
 */
public class Help {

  private static final Logger LOG = LoggerFactory.getLogger(Help.class);
  public static final String HELP_FOLDER = "help";
  public static final String HELP_FOLDER_PATH = Constants.TYPES_STATIC_FOLDER + "/" + HELP_FOLDER;
  public static final String HELP_COMMON_FOLDER = "common";
  public static final String HELP_FILE_START = "help-start.txt";
  public static final String HELP_FILE_OPTIONS = "help-options.txt";
  public static final String HELP_FILE_TYPE = "help-type.txt";
  public static final String HELP_FILE_NAME = "help-name.txt";
  public static final String HELP_FILE_TARGET_NAME = "help-targetname.txt";
  public static final String HELP_FILE_ARGS = "help-args.txt";
  public static final String HELP_FILE_GUI = "help-gui.txt";

  private final Resource resource;
  private final Config config;

  /**
   * Constructor
   */
  public Help(final Resource resource, final Config config) {
    this.resource = resource;
    this.config = config;
  }

  /**
   * Shows help text.
   */
  public void showHelp() {
    System.out.print(getHelpText());
  }

  /**
   * Build help text from helper files.
   *
   * @return help text
   */
  public String getHelpText() {
    String helpText = "";

    final String type = resource.getType();

    if (StringUtils.isBlank(type)) {
      // no type
      helpText = getCompleteHelpText();
    } else {
      helpText = getSpecificHelpText();
    }

    // get complete help
    if (StringUtils.isBlank(helpText)) {
      helpText = getCompleteHelpText();
    }
    return helpText;
  }

  /**
   * Get complete help text from helper files.
   *
   * @return help text
   */
  public String getCompleteHelpText() {
    final StringBuilder helpText = new StringBuilder();

    // get complete help
    helpText.append(getTextFromResourceFile(HELP_FILE_START));
    helpText.append(getTextFromResourceFile(HELP_FILE_OPTIONS));
    helpText.append(getTextFromResourceFile(HELP_FILE_TYPE));
    helpText.append(getTextFromResourceFile(HELP_FILE_TYPE, Constants.TYPE_CONFIG_PROPS));

    // adding the all type helps
    for (final String type : config.getDynamicTypes()) {
      final String helpPath = config.getProperty(Constants.CONFIGPROP_SOURCE_TYPES_FOLDER) + "/" + type + "/"
          + HELP_FOLDER + "/" + HELP_FILE_TYPE;
      helpText.append(getTextFromFile(helpPath));
    }

    // name option
    helpText.append(getTextFromResourceFile(HELP_FILE_NAME));
    helpText.append(getTextFromResourceFile(HELP_COMMON_FOLDER + "/" + HELP_FILE_NAME));

    // targetname option
    helpText.append(getTextFromResourceFile(HELP_FILE_TARGET_NAME));
    helpText.append(getTextFromResourceFile(HELP_COMMON_FOLDER + "/" + HELP_FILE_TARGET_NAME));

    // args option
    helpText.append(getTextFromResourceFile(HELP_FILE_ARGS));
    helpText.append(getTextFromResourceFile(HELP_COMMON_FOLDER + "/" + HELP_FILE_ARGS));

    return helpText.toString();
  }

  /**
   * Get template type specific help text.
   *
   * @return type specific help text
   */
  public String getSpecificHelpText() {
    final StringBuilder helpText = new StringBuilder();

    final String type = resource.getType();
    final String name = resource.getSourceName();
    final String targetname = resource.getTargetName();

    // Get Runner
    final Reflection reflection = new Reflection(config);
    final BasisRunner runner = reflection.getRunner(resource);

    if (runner == null) {
      // not existing type
      helpText.append(getCompleteHelpText());

    } else {
      // config type
      if (Constants.TYPE_CONFIG_PROPS.equals(type)) {
        // show default config properties
        helpText.append(getTextFromResourceFile(HELP_FILE_START, Constants.TYPE_CONFIG_PROPS));
        helpText.append(config.getDefaultPropertiesAsText());

      } else if (StringUtils.isBlank(name)) {
        // if only <type>
        helpText.append(getTextFromResourceFile(HELP_FILE_START));
        helpText.append(getTextFromResourceFile(HELP_FILE_NAME));
        helpText.append(getTextFromFile(runner, HELP_FILE_NAME));
        helpText.append(getTextFromResourceFile(HELP_FILE_TARGET_NAME));
        helpText.append(getTextFromFile(runner, HELP_FILE_TARGET_NAME));
        helpText.append(getTextFromResourceFile(HELP_FILE_ARGS));
        helpText.append(getTextFromFile(runner, HELP_FILE_ARGS));
        // get all available templates
        helpText.append(getTemplatesAsString(resource));

      } else if (StringUtils.isNotBlank(name) && StringUtils.isBlank(targetname)) {
        // if <type> + <name>
        helpText.append(getTextFromResourceFile(HELP_FILE_START));
        helpText.append(getTextFromResourceFile(HELP_FILE_TARGET_NAME));
        helpText.append(getTextFromFile(runner, HELP_FILE_TARGET_NAME));
        helpText.append(getTextFromResourceFile(HELP_FILE_ARGS));
        helpText.append(getTextFromFile(runner, HELP_FILE_ARGS));
        // get all placeholders
        helpText.append(getPlaceHoldersAsString(runner));

      } else if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(targetname)) {
        // if <type> + <name> + <targetname>
        helpText.append(getTextFromResourceFile(HELP_FILE_START));
        helpText.append(getTextFromResourceFile(HELP_FILE_ARGS));
        helpText.append(getTextFromFile(runner, HELP_FILE_ARGS));
        // get all placeholders
        helpText.append(getPlaceHoldersAsString(runner));
      }
    }
    return helpText.toString();
  }

  /**
   * Get help text for GUI.
   *
   * @param type
   *          - template type
   * @param name
   *          - template name
   * @return help text
   */
  public String getGuiHelpText() {
    final StringBuilder helpText = new StringBuilder();

    // Get Runner
    final Reflection reflection = new Reflection(config);
    final BasisRunner runner = reflection.getRunner(resource);

    if (runner == null) {
      // not existing type
      helpText.append("No help for type ");
      helpText.append(resource.getType());
    } else {
      helpText.append(getTextFromFile(runner, HELP_FILE_GUI));
    }
    return helpText.toString();
  }

  /**
   * Read help text from dynamic type helper file.
   *
   * @param runner
   *          - template type runner
   * @param fileName
   *          - help file name
   * @return help text
   */
  public String getTextFromFile(final BasisRunner runner, final String fileName) {
    final String typeHelpPath = runner.getHelpFolder() + "/" + fileName;
    final String templateHelpPath = runner.getTemplateHelpFolder() + "/" + fileName;
    final String type = runner.getResource().getType();

    String helpText = "";
    if (config.isDynamicType(type)) {
      final File file = new File(templateHelpPath);
      if (file.exists() && file.isFile() && file.canRead()) {
        helpText = getTextFromFile(templateHelpPath);
      } else {
        helpText = getTextFromFile(typeHelpPath);
      }
    } else {
      if (isResource(templateHelpPath)) {
        helpText = getTextFromResourceFileByPath(templateHelpPath);
      } else {
        helpText = getTextFromResourceFileByPath(typeHelpPath);
      }
    }
    return helpText;
  }

  /**
   * Read help text from helper file from file system.
   *
   * @param filePath
   *          - help file path
   * @return help text
   */
  private String getTextFromFile(final String filePath) {
    final StringBuilder helpText = new StringBuilder();
    try {
      final File helpFile = new File(filePath);
      final String fileText = FileUtils.readFileToString(helpFile, Constants.ENCODING);
      helpText.append(fileText);
    } catch (final IOException e) {
      LOG.error("Sorry, can't show you help text from file [{}]", filePath);
    }
    helpText.append("\n");
    return helpText.toString();
  }

  /**
   * Read help text from helper file from resources.
   *
   * @param fileName
   *          - help file name
   * @return help text
   */
  private String getTextFromResourceFile(final String fileName) {
    return getTextFromResourceFile(fileName, null);
  }

  /**
   * Read help text from helper file from resources.
   *
   * @param fileName
   *          - help file name
   * @param type
   *          - template type
   * @return help text
   */
  private String getTextFromResourceFile(final String fileName, final String type) {
    String filePath = Constants.TYPES_STATIC_FOLDER;
    if (StringUtils.isNotBlank(type)) {
      filePath += "/" + type;
    }
    filePath += "/" + HELP_FOLDER + "/" + fileName;
    return getTextFromResourceFileByPath(filePath);
  }

  /**
   * Is file under resources exists?
   *
   * @param filePath
   *          - path to file under resources
   * @return true if resource exists, false - otherwise
   */
  private boolean isResource(final String filePath) {
    final InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
    if (in == null) {
      return false;
    }
    return true;
  }

  /**
   * Read help text from helper file from project resources.
   *
   * @param filePath
   *          - help file path
   * @return help text
   */
  private String getTextFromResourceFileByPath(final String filePath) {
    final StringBuilder helpText = new StringBuilder();
    InputStream in = null;
    try {
      in = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
      final StringWriter writer = new StringWriter();

      IOUtils.copy(in, writer, Constants.ENCODING);
      helpText.append(writer.toString());
    } catch (final IOException e) {
      LOG.error("Sorry, can't show you help text from file [{}]", filePath);
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (final IOException e) {
          LOG.error("Sorry, unable to close input stream from help file [{}]", filePath);
        }
      }
    }
    helpText.append("\n");
    return helpText.toString();
  }

  /**
   * Get list of all existing placeholders in the template.
   *
   * @param runner
   *          - template runner
   * @return list of placeholders as String
   */
  private String getPlaceHoldersAsString(final BasisRunner runner) {
    final StringBuilder placeHolders = new StringBuilder();
    final List<String> phsList = getPlaceHolders(runner);
    if (phsList.size() > 0) {
      placeHolders.append("Found next placeholders: \n");
      for (final String ph : phsList) {
        // add offset for help
        placeHolders.append("    ");
        placeHolders.append(ph);
        placeHolders.append("\n");
      }
    }
    return placeHolders.toString();
  }

  /**
   * Get list of all existing placeholders in the template.
   *
   * @param runner
   *          - template runner
   * @return list of placeholders
   */
  public List<String> getPlaceHolders(final BasisRunner runner) {
    final List<String> phsList = new ArrayList<String>();
    if (runner instanceof CompoundRunner) {
      LOG.debug("runner is instance of CompoundRunner");
      for (final BasisRunner compoundRunner : ((CompoundRunner) runner).getRunners()) {
        phsList.addAll(getPlaceHoldersFromOneRunner(compoundRunner));
      }
    } else {
      phsList.addAll(getPlaceHoldersFromOneRunner(runner));
    }
    return phsList;
  }

  /**
   * Get list of all existing placeholders in the template.
   *
   * @param runner
   *          - template runner
   * @return list of placeholders
   */
  private List<String> getPlaceHoldersFromOneRunner(final BasisRunner runner) {
    List<String> phsList = new ArrayList<String>();

    final Resource runnerResource = runner.getResource();
    final String templateSrcPath = runner.getSourceFolder() + "/" + runnerResource.getSourceName();

    final File dir = new File(templateSrcPath);
    if (!dir.exists()) {
      LOG.error("Can't get place holders. Directory/file {} doesn't exist.", templateSrcPath);
    } else {
      phsList = getPlaceHolders(dir);
      // add runmode ph
      if (Constants.TYPE_OSGI.equals(runnerResource.getType()) && !phsList.contains(Constants.PLACEHOLDER_RUNMODE)) {
        phsList.add(Constants.PLACEHOLDER_RUNMODE);
      }
    }
    return phsList;
  }

  /**
   * Get list of all existing placeholders in the directory/file.
   *
   * @param dir/file
   *          - directory or file
   * @return list of placeholders
   */
  private List<String> getPlaceHolders(final File dir) {
    LOG.debug("get PH from [{}]", dir);
    final ArrayList<String> placeholders = new ArrayList<>();

    if (dir.isDirectory()) {
      // get files list recursive only with predefined extentions
      final String[] extentions = config.getFileExtensions();
      final Collection<File> fileList = FileUtils.listFiles(dir, extentions, true);
      final Iterator<File> iter = fileList.iterator();
      while (iter.hasNext()) {
        final File nextFile = iter.next();
        // find place holders
        placeholders.addAll(getPlaceHoldersFromFile(nextFile));
      }
    } else {
      // find place holders
      placeholders.addAll(getPlaceHoldersFromFile(dir));
    }

    final ArrayList<String> ret = new ArrayList<>();

    for (final String ph : placeholders) {
      if (!ret.contains(ph) && !"targetname".equals(ph)) {
        ret.add(ph);
      }
    }

    return ret;
  }

  /**
   * Get all placeholders from the file
   *
   * @param file
   *          - file to find placeholders there
   * @return list of placeholders
   */
  private List<String> getPlaceHoldersFromFile(final File file) {
    final ArrayList<String> placeHolders = new ArrayList<>();
    try {
      final String text = FileUtils.readFileToString(file, Constants.ENCODING);
      // find placeholders
      final List<String> phList = Replacer.findTextPlaceHolders(text);
      final Iterator<String> iter = phList.iterator();
      while (iter.hasNext()) {
        // add offset for help
        String ph = iter.next();
        ph = ph.replace("{{", "");
        ph = ph.replace("}}", "");
        ph = ph.trim();
        if (!placeHolders.contains(ph)) {
          placeHolders.add(ph);
        }
      }
    } catch (final IOException e) {
      LOG.error("Can't get place holders from {}", file);
    }
    return placeHolders;
  }

  /**
   * Get list of all existing templates as String.
   *
   * @param resource
   *          - template resource
   * @return list of all existing templates
   */
  public String getTemplatesAsString(final Resource resource) {
    final StringBuilder templs = new StringBuilder();
    templs.append("available names: \n");

    // get available templates
    for (final String templateName : config.getTemplateNames(resource.getType())) {
      templs.append("    ");
      templs.append(templateName);
      templs.append("\n");
      LOG.debug("For type [{}] found: [{}]", resource.getType(), templateName);
    }

    return templs.toString();
  }
}