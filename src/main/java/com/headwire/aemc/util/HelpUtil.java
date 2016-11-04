package com.headwire.aemc.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
   * Build help text from helper files.
   *
   * @return help text
   * @throws IOException
   *           - IOException
   */
  public static String getHelpText(final String[] args) throws IOException {
    boolean addCompleteHelp = false;
    String helpText = "\n";
    helpText += getTextFromFile(AEMC_HELP_FILE_START);

    // no args or help
    if (args == null || args.length == 0 || (args.length == 1 && Constants.PARAM_HELP.equals(args[0]))) {
      addCompleteHelp = true;

    } else if (Constants.PARAM_HELP.equals(args[0])) {
      // help + config
      if (Constants.TYPE_CONFIG_PROPS.equals(args[1])) {
        // show current config properties
        ConfigUtil.getConfigProperties(true);
      }

      // help + arg
      if (args.length == 2) {
        if (Constants.TYPE_APPS_UI_LIST.contains(args[1]) || Constants.TYPE_CORE_LIST.contains(args[1])) {
          helpText += getTextFromFile(AEMC_HELP_FILE_NAME);
          helpText += getTextFromFile(AEMC_HELP_FILE_TARGET_NAME);
          helpText += getTextFromFile(AEMC_HELP_FILE_ARGS);
          helpText += getTextFromFile(AEMC_HELP_FILE_END);
        } else {
          addCompleteHelp = true;
        }
      }

      // help + arg + arg
      if (args.length == 3) {
        // help + <type> + <name>
        if (Constants.TYPE_APPS_UI_LIST.contains(args[1]) || Constants.TYPE_CORE_LIST.contains(args[1])) {
          helpText += getTextFromFile(AEMC_HELP_FILE_TARGET_NAME);
          helpText += getTextFromFile(AEMC_HELP_FILE_ARGS);
          helpText += getTextFromFile(AEMC_HELP_FILE_END);
          // show all placeholders
          helpText += getPlaceHolders(args[1], args[2]);
          helpText += getTextFromFile(AEMC_HELP_FILE_END);
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
      helpText += getTextFromFile(AEMC_HELP_FILE_TYPE);
      helpText += getTextFromFile(AEMC_HELP_FILE_NAME);
      helpText += getTextFromFile(AEMC_HELP_FILE_TARGET_NAME);
      helpText += getTextFromFile(AEMC_HELP_FILE_ARGS);
      helpText += getTextFromFile(AEMC_HELP_FILE_END);
    }

    return helpText;
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
    String helpText = "";

    try {
      IOUtils.copy(in, writer, Constants.ENCODING);
      helpText += writer.toString();
    } catch (final IOException e) {
      LOG.error("Sorry, can't show you help text from file " + fileName);
      throw new IOException(e);
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (final IOException e) {
          LOG.error("Sorry, unable to close input stream from help file " + fileName);
          throw new IOException(e);
        }
      }
    }
    helpText += "\n";
    return helpText;
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

    String placeHolders = "";

    if (!dir.exists()) {
      placeHolders = "Can't get place holders. Directory/file " + dir + " doesn't exist.";
    } else {
      placeHolders = "Found next placeholders: \n";

      if (dir.isDirectory()) {
        // get files list recursive only with predefined extentions
        final String[] extentions = ConfigUtil.getConfigExtensions(configProps);
        final Collection<File> fileList = FileUtils.listFiles(dir, extentions, true);
        final Iterator<File> iter = fileList.iterator();
        while (iter.hasNext()) {
          final File nextFile = iter.next();
          // find place holders
          placeHolders += findPlaceHolders(nextFile);
        }
      } else {
        // find place holders
        placeHolders += findPlaceHolders(dir);
      }
    }

    return placeHolders;
  }

  /**
   * Find place holders in file
   *
   * @param file
   *          - file to find placeholders there
   * @return
   * @throws IOException
   *           - IOException
   */
  private static String findPlaceHolders(final File file) throws IOException {
    String placeHolders = "";
    try {
      final String text = FileUtils.readFileToString(file, Constants.ENCODING);
      final Pattern pattern = Pattern.compile("\\{\\{ (.*) \\}\\}");
      final Matcher matcher = pattern.matcher(text);
      // find placeholders
      while (matcher.find()) {
        placeHolders += matcher.group();
        placeHolders += "\n";
      }
    } catch (final IOException e) {
      LOG.error("Can't get place holders from " + file);
      throw new IOException(e);
    }
    return placeHolders;
  }
}