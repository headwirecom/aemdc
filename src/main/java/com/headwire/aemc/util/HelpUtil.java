package com.headwire.aemc.util;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemc.companion.Constants;


/**
 * Help Util
 *
 * @author Marat Saitov, 03.11.2016
 */
public class HelpUtil {

  private static final Logger LOG = LoggerFactory.getLogger(HelpUtil.class);
  public static final String AEMC_HELP_FOLDER = "help";
  public static final String AEMC_HELP_FILE_START = "help-start.txt";
  public static final String AEMC_HELP_FILE_END = "help-end.txt";
  public static final String AEMC_HELP_FILE_CONFIG = "help-config.txt";
  public static final String AEMC_HELP_FILE_TYPE = "help-type.txt";
  public static final String AEMC_HELP_FILE_NAME = "help-name.txt";
  public static final String AEMC_HELP_FILE_TARGET_NAME = "help-targetname.txt";
  public static final String AEMC_HELP_FILE_ARGS = "help-args.txt";

  /**
   * Constructor
   */
  private HelpUtil() {
  }

  /**
   * Shows help text.
   *
   * @throws IOException
   *           - IOException
   */
  public static void showHelp(final String[] args) throws IOException {
    System.out.print(getHelpText(args));
  }

  /**
   * Build help text from helper files.
   *
   * @return help text
   * @throws IOException
   *           - IOException
   */
  public static String getHelpText(final String[] args) throws IOException {
    boolean addCompleteHelp = false;
    final StringBuilder helpText = new StringBuilder();

    // no args or help
    if (args == null || args.length == 0 || (args.length == 1 && Constants.PARAM_HELP.equals(args[0]))) {
      addCompleteHelp = true;

    } else if (Constants.PARAM_HELP.equals(args[0])) {
      // help + config
      if (Constants.TYPE_CONFIG_PROPS.equals(args[1])) {
        // show current config properties
        helpText.append(ConfigUtil.getConfigPropertiesAsText());

      } else if (args.length == 2) {
        // help + arg
        if (Constants.TYPE_APPS_UI_LIST.contains(args[1]) || Constants.TYPE_CORE_LIST.contains(args[1])) {
          helpText.append(getTextFromFile(AEMC_HELP_FILE_START));
          helpText.append(getTextFromFile(AEMC_HELP_FILE_NAME));
          helpText.append(getTextFromFile(AEMC_HELP_FILE_TARGET_NAME));
          helpText.append(getTextFromFile(AEMC_HELP_FILE_ARGS));
          // helpText.append(getTextFromFile(AEMC_HELP_FILE_END));
        } else {
          addCompleteHelp = true;
        }

      } else if (args.length == 3) {
        // help + arg + arg
        // help + <type> + <name>
        if (Constants.TYPE_APPS_UI_LIST.contains(args[1]) || Constants.TYPE_CORE_LIST.contains(args[1])) {
          helpText.append(getTextFromFile(AEMC_HELP_FILE_START));
          helpText.append(getTextFromFile(AEMC_HELP_FILE_TARGET_NAME));
          helpText.append(getTextFromFile(AEMC_HELP_FILE_ARGS));
          // helpText.append(getTextFromFile(AEMC_HELP_FILE_END));
          // get all placeholders
          helpText.append(getPlaceHolders(args[1], args[2]));
          // helpText.append(getTextFromFile(AEMC_HELP_FILE_END));
        } else {
          addCompleteHelp = true;
        }
      } else {
        // in all other cases
        addCompleteHelp = true;
      }

    } else {
      // not help and args.length < 3
      addCompleteHelp = true;
    }

    // get complete help
    if (addCompleteHelp) {
      helpText.append(getTextFromFile(AEMC_HELP_FILE_START));
      helpText.append(getTextFromFile(AEMC_HELP_FILE_CONFIG));
      helpText.append(getTextFromFile(AEMC_HELP_FILE_TYPE));
      helpText.append(getTextFromFile(AEMC_HELP_FILE_NAME));
      helpText.append(getTextFromFile(AEMC_HELP_FILE_TARGET_NAME));
      helpText.append(getTextFromFile(AEMC_HELP_FILE_ARGS));
      // helpText.append(getTextFromFile(AEMC_HELP_FILE_END));
    }

    return helpText.toString();
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
        .getResourceAsStream(AEMC_HELP_FOLDER + "/" + fileName);
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
   * @param type
   *          - template type
   * @param name
   *          - template source name
   * @return list of placeholders
   * @throws IOException
   *           - IOException
   */
  private static String getPlaceHolders(final String type, final String name) throws IOException {
    // Get Config Properties from config file
    final Properties configProps = ConfigUtil.getConfigProperties();
    final String templateSrcPath = ConfigUtil.getTypeSourceFolder(configProps, type) + "/" + name;
    final File dir = new File(templateSrcPath);

    final StringBuilder placeHolders = new StringBuilder();

    if (!dir.exists()) {
      placeHolders.append("Can't get place holders. Directory/file " + templateSrcPath + " doesn't exist.");
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
}