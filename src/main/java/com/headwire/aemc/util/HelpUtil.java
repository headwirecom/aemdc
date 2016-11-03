package com.headwire.aemc.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

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
  public static final String AEMC_HELP_FILE_PATH = "help.txt";

  /**
   * Constructor
   */
  private HelpUtil() {
  }

  /**
   * Read help text from helper file.
   *
   * @return help text
   * @throws IOException
   *           - IOException
   */
  public static String getHelpText() throws IOException {
    final InputStream in = Thread.currentThread().getContextClassLoader()
        .getResourceAsStream(AEMC_HELP_FOLDER + "/" + AEMC_HELP_FILE_PATH);
    final StringWriter writer = new StringWriter();
    String helpText = "\n";
    try {
      IOUtils.copy(in, writer, Constants.ENCODING);
      helpText += writer.toString();
    } catch (final IOException e) {
      LOG.error("Sorry, can't show you help text");
      throw new IOException(e);
    }
    return helpText;
  }
}