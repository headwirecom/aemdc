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
import com.headwire.aemdc.runner.DynamicRunner;


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

  // Get Properties Config from config file
  final Config config = new Config();

  /**
   * Constructor
   */
  public Help() {
  }

  /**
   * Shows help text.
   *
   * @param resource
   *          - resource object
   */
  public void showHelp(final Resource resource) {
    System.out.print(getHelpText(resource));
  }

  /**
   * Build help text from helper files.
   *
   * @param resource
   *          - resource object
   * @return help text
   */
  public String getHelpText(final Resource resource) {
    String helpText = "";

    final String type = resource.getType();

    if (StringUtils.isBlank(type)) {
      // no type
      helpText = getCompleteHelpText();
    } else {
      helpText = getSpecificHelpText(resource);
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
   * @param resource
   *          - resource object
   * @return type specific help text
   */
  public String getSpecificHelpText(final Resource resource) {
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
      String templateSrcPath = runner.getSourceFolder();
      if (config.isDirTemplateStructure(resource.getType(), resource.getSourceName())) {
        templateSrcPath += "/" + name;
      }

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
        helpText.append(getPlaceHolders(templateSrcPath, config));

      } else if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(targetname)) {
        // if <type> + <name> + <targetname>
        helpText.append(getTextFromResourceFile(HELP_FILE_START));
        helpText.append(getTextFromResourceFile(HELP_FILE_ARGS));
        helpText.append(getTextFromFile(runner, HELP_FILE_ARGS));
        // get all placeholders
        helpText.append(getPlaceHolders(templateSrcPath, config));
      }
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
  private String getTextFromFile(final BasisRunner runner, final String fileName) {
    final String filePath = runner.getHelpFolder() + "/" + fileName;
    String helpText = "";
    if (runner instanceof DynamicRunner) {
      helpText = getTextFromFile(filePath);
    } else {
      helpText = getTextFromResourceFileByPath(filePath);
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
   * @param templateSrcPath
   *          - template source path
   * @param config
   *          - configuration properties
   * @return list of placeholders
   */
  private String getPlaceHolders(final String templateSrcPath, final Config config) {
    final File dir = new File(templateSrcPath);

    final StringBuilder placeHolders = new StringBuilder();

    if (!dir.exists()) {
      LOG.error("Can't get place holders. Directory/file {} doesn't exist.", templateSrcPath);
    } else {
      placeHolders.append("Found next placeholders: \n");

      // List<String> listPlaceholders = getPlaceHoldersAsList(dir);
      if (dir.isDirectory()) {
        // get files list recursive only with predefined extentions
        final String[] extentions = config.getFileExtensions();
        final Collection<File> fileList = FileUtils.listFiles(dir, extentions, true);
        final Iterator<File> iter = fileList.iterator();
        while (iter.hasNext()) {
          final File nextFile = iter.next();
          // find place holders
          placeHolders.append(getPlaceHolders(nextFile));
        }
      } else {
        // find place holders
        placeHolders.append(getPlaceHolders(dir));
      }
    }
    return placeHolders.toString();
  }

  public List<String> getPlaceHoldersAsList(final File dir) {
    System.out.println(dir);
    final ArrayList<String> placeholders = new ArrayList<>();

    if (dir.isDirectory()) {
      // get files list recursive only with predefined extentions
      final String[] extentions = config.getFileExtensions();
      final Collection<File> fileList = FileUtils.listFiles(dir, extentions, true);
      final Iterator<File> iter = fileList.iterator();
      while (iter.hasNext()) {
        final File nextFile = iter.next();
        // find place holders
        placeholders.addAll(getPlaceHoldersFromFileAsList(nextFile));
      }
    } else {
      // find place holders
      placeholders.addAll(getPlaceHoldersFromFileAsList(dir));
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
   * @return
   */
  private String getPlaceHolders(final File file) {
    final StringBuilder placeHolders = new StringBuilder();
    try {
      final String text = FileUtils.readFileToString(file, Constants.ENCODING);
      // find placeholders
      final List<String> phList = Replacer.findTextPlaceHolders(text);
      final Iterator<String> iter = phList.iterator();
      while (iter.hasNext()) {
        // add offset for help
        placeHolders.append("    ");
        placeHolders.append(iter.next());
        placeHolders.append("\n");
      }
    } catch (final IOException e) {
      LOG.error("Can't get place holders from {}", file);
    }
    return placeHolders.toString();
  }

  private List<String> getPlaceHoldersFromFileAsList(final File file) {
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