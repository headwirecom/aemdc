package com.headwire.aemdc.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.companion.Constants;
import com.headwire.aemdc.companion.Resource;
import com.headwire.aemdc.menu.BasisRunner;


/**
 * Help Util
 *
 * @author Marat Saitov, 03.11.2016
 */
public class HelpUtil {

  private static final Logger LOG = LoggerFactory.getLogger(HelpUtil.class);
  public static final String HELP_ROOT_FOLDER = "help";
  public static final String HELP_COMMON_FOLDER = "common";
  public static final String HELP_FILE_START = "help-start.txt";
  public static final String HELP_FILE_OPTIONS = "help-options.txt";
  public static final String HELP_FILE_CONFIG = "help-config.txt";
  public static final String HELP_FILE_TYPE = "help-type.txt";
  public static final String HELP_FILE_NAME = "help-name.txt";
  public static final String HELP_FILE_TARGET_NAME = "help-targetname.txt";
  public static final String HELP_FILE_ARGS = "help-args.txt";

  /**
   * Constructor
   */
  private HelpUtil() {
  }

  /**
   * Shows help text.
   *
   * @param resource
   *          - resource object
   * @throws IOException
   *           - IOException
   */
  public static void showHelp(final Resource resource) throws IOException {
    System.out.print(getHelpText(resource));
  }

  /**
   * Build help text from helper files.
   *
   * @param resource
   *          - resource object
   * @return help text
   * @throws IOException
   *           - IOException
   */
  public static String getHelpText(final Resource resource) throws IOException {
    String helpText = "";

    final String type = resource.getType();

    if (StringUtils.isBlank(type)) {
      // no type
      helpText = getCompleteHelpText();

    } else if (!isExistingType(type)) {
      // not existing type
      LOG.debug("Unknown <type> argument: " + type);
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
   * @throws IOException
   *           - IOException
   */
  public static String getCompleteHelpText() throws IOException {
    final StringBuilder helpText = new StringBuilder();

    // get complete help
    helpText.append(getTextFromFile(HELP_FILE_START));
    helpText.append(getTextFromFile(HELP_FILE_OPTIONS));
    helpText.append(getTextFromFile(HELP_FILE_CONFIG));
    helpText.append(getTextFromFile(HELP_FILE_TYPE));
    helpText.append(getTextFromFile(HELP_COMMON_FOLDER + "/" + HELP_FILE_TYPE));

    // name option
    helpText.append(getTextFromFile(HELP_FILE_NAME));
    helpText.append(getTextFromFile(HELP_COMMON_FOLDER + "/" + HELP_FILE_NAME));

    // targetname option
    helpText.append(getTextFromFile(HELP_FILE_TARGET_NAME));
    helpText.append(getTextFromFile(HELP_COMMON_FOLDER + "/" + HELP_FILE_TARGET_NAME));

    // args option
    helpText.append(getTextFromFile(HELP_FILE_ARGS));
    helpText.append(getTextFromFile(HELP_COMMON_FOLDER + "/" + HELP_FILE_ARGS));

    return helpText.toString();
  }

  /**
   * Get template type specific help text.
   *
   * @param resource
   *          - resource object
   * @return type specific help text
   * @throws IOException
   *           - IOException
   */
  public static String getSpecificHelpText(final Resource resource) throws IOException {
    final StringBuilder helpText = new StringBuilder();

    final String type = resource.getType();
    final String name = resource.getSourceName();
    final String targetname = resource.getTargetName();

    // Get Runner
    final Reflection reflection = new Reflection();
    final BasisRunner runner = reflection.getRunner(resource);

    if (runner != null) {
      final String helpFolder = runner.getHelpFolder();
      final String templateSrcPath = runner.getSourceFolder() + "/" + name;

      // config type
      if (Constants.TYPE_CONFIG_PROPS.equals(type)) {
        // show current config properties
        helpText.append(ConfigUtil.getConfigPropertiesAsText());

      } else if (StringUtils.isBlank(name)) {
        // if only <type>
        helpText.append(getTextFromFile(HELP_FILE_START));
        helpText.append(getTextFromFile(HELP_FILE_NAME));
        helpText.append(getTextFromFile(helpFolder + "/" + HELP_FILE_NAME));
        helpText.append(getTextFromFile(HELP_FILE_TARGET_NAME));
        helpText.append(getTextFromFile(helpFolder + "/" + HELP_FILE_TARGET_NAME));
        helpText.append(getTextFromFile(HELP_FILE_ARGS));
        helpText.append(getTextFromFile(helpFolder + "/" + HELP_FILE_ARGS));
        // get all available templates
        helpText.append(getTemplatesAsString(runner));

      } else if (StringUtils.isNotBlank(name) && StringUtils.isBlank(targetname)) {
        // if <type> + <name>
        helpText.append(getTextFromFile(HELP_FILE_START));
        helpText.append(getTextFromFile(HELP_FILE_TARGET_NAME));
        helpText.append(getTextFromFile(helpFolder + "/" + HELP_FILE_TARGET_NAME));
        helpText.append(getTextFromFile(HELP_FILE_ARGS));
        helpText.append(getTextFromFile(helpFolder + "/" + HELP_FILE_ARGS));
        // get all placeholders
        helpText.append(getPlaceHolders(templateSrcPath));

      } else if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(targetname)) {
        // if <type> + <name> + <targetname>
        helpText.append(getTextFromFile(HELP_FILE_START));
        helpText.append(getTextFromFile(HELP_FILE_ARGS));
        helpText.append(getTextFromFile(helpFolder + "/" + HELP_FILE_ARGS));
        // get all placeholders
        helpText.append(getPlaceHolders(templateSrcPath));
      }
    }
    return helpText.toString();
  }

  /**
   * Is existing type
   *
   * @param type
   *          - template type
   * @return true if type exists
   */
  public static boolean isExistingType(final String type) {
    if (Constants.TYPE_APPS_UI_LIST.contains(type) || Constants.TYPE_CORE_LIST.contains(type)
        || Constants.TYPE_CONF_UI_LIST.contains(type)) {
      return true;
    }
    return false;
  }

  /**
   * Read help text from helper file.
   *
   * @param fileName
   *          - help file name
   * @return help text
   * @throws IOException
   *           - IOException
   */
  private static String getTextFromFile(final String fileName) throws IOException {
    final InputStream in = Thread.currentThread().getContextClassLoader()
        .getResourceAsStream(HELP_ROOT_FOLDER + "/" + fileName);
    final StringWriter writer = new StringWriter();
    final StringBuilder helpText = new StringBuilder();

    try {
      IOUtils.copy(in, writer, Constants.ENCODING);
      helpText.append(writer.toString());
    } catch (final IOException e) {
      LOG.error("Sorry, can't show you help text from file {}", fileName);
      throw new IOException(e);
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (final IOException e) {
          LOG.error("Sorry, unable to close input stream from help file {}", fileName);
          throw new IOException(e);
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
   * @return list of placeholders
   * @throws IOException
   *           - IOException
   */
  private static String getPlaceHolders(final String templateSrcPath) throws IOException {
    // Get Config Properties from config file
    final Properties configProps = ConfigUtil.getConfigProperties();
    final File dir = new File(templateSrcPath);

    final StringBuilder placeHolders = new StringBuilder();

    if (!dir.exists()) {
      LOG.error("Can't get place holders. Directory/file {} doesn't exist.", templateSrcPath);
    } else {
      placeHolders.append("Found next placeholders: \n");

      if (dir.isDirectory()) {
        // get files list recursive only with predefined extentions
        final String[] extentions = ConfigUtil.getConfigExtensions(configProps);
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

  /**
   * Get all placeholders from the file
   *
   * @param file
   *          - file to find placeholders there
   * @return
   * @throws IOException
   *           - IOException
   */
  private static String getPlaceHolders(final File file) throws IOException {
    final StringBuilder placeHolders = new StringBuilder();
    try {
      final String text = FileUtils.readFileToString(file, Constants.ENCODING);
      // find placeholders
      final List<String> phList = TextReplacer.findTextPlaceHolders(text);
      final Iterator<String> iter = phList.iterator();
      while (iter.hasNext()) {
        // add offset for help
        placeHolders.append("    ");
        placeHolders.append(iter.next());
        placeHolders.append("\n");
      }
    } catch (final IOException e) {
      LOG.error("Can't get place holders from {}", file);
      throw new IOException(e);
    }
    return placeHolders.toString();
  }

  /**
   * Get list of all existing templates as String.
   *
   * @param runner
   *          - template runner
   * @return list of all existing templates
   */
  private static String getTemplatesAsString(final BasisRunner runner) {
    final StringBuilder templs = new StringBuilder();

    templs.append("available names: \n");

    // find available templates
    final Collection<File> fileList = runner.getAvailableTemplates();
    final Iterator<File> iter = fileList.iterator();
    while (iter.hasNext()) {
      final File nextFile = iter.next();
      final String templateName = runner.getTemplateName(runner.getSourceFolder(), nextFile.getPath());
      templs.append("    ");
      templs.append(templateName);
      templs.append("\n");
      LOG.debug("Found: {}", nextFile);
    }

    return templs.toString();
  }
}