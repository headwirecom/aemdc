package com.headwire.aemc.command;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.headwire.aemc.companion.Constants;


/**
 * Concrete command to create file
 *
 */
public class CreateCommand implements Command {

  Map<String, String> params;

  public CreateCommand(final Map<String, String> params) {
    this.params = params;
  }

  @Override
  public void execute() {
    System.out.println("Creating file... " + params.get(Constants.PARAM_TARGET_PATH));
    final File targetFile = new File(params.get(Constants.PARAM_TARGET_PATH));
    createTargetFile(targetFile);
  }

  private void createTargetFile(final File targetFile) {
    try {
      final String fileText = params.get(Constants.PARAM_FILETEXT);
      FileUtils.writeStringToFile(targetFile, fileText, Constants.ENCODING);
      System.out.println("File " + targetFile + " created.");
    } catch (final IOException e) {
      System.out.println("Can't create target file [" + targetFile + "] : " + e);
    }
  }

}
