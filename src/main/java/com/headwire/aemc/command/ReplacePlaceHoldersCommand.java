package com.headwire.aemc.command;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.headwire.aemc.companion.Constants;
import com.headwire.aemc.replacer.TextReplacer;


/**
 * Concrete command to replace place holders
 *
 */
public class ReplacePlaceHoldersCommand implements Command {

  Map<String, String> params;

  public ReplacePlaceHoldersCommand(final Map<String, String> params) {
    this.params = params;
  }

  @Override
  public void execute() {
    final String sourceFilePath = params.get(Constants.PARAM_SOURCE_PATH);
    System.out.println("Replacing place holders... in the " + sourceFilePath);
    try {

      // final File sourceFile = new File(sourceFilePath);
      // String fileText = FileUtils.readFileToString(sourceFile, Constants.ENCODING);

      final InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(sourceFilePath);
      final StringWriter writer = new StringWriter();
      IOUtils.copy(in, writer, Constants.ENCODING);
      String fileText = writer.toString();

      fileText = TextReplacer.replaceTextPlaceHolders(fileText, params);
      params.put(Constants.PARAM_FILETEXT, fileText);

      System.out.println("Replaced file text:");
      System.out.println(fileText);

    } catch (final IOException e) {
      System.out.println("Can't read from source file [" + sourceFilePath + "] : " + e);
    }
  }
}
