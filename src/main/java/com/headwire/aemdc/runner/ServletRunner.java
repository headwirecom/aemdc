package com.headwire.aemdc.runner;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.command.CommandMenu;
import com.headwire.aemdc.command.CreateFileCommand;
import com.headwire.aemdc.command.ReplacePlaceHoldersCommand;
import com.headwire.aemdc.companion.Constants;
import com.headwire.aemdc.companion.Resource;
import com.headwire.aemdc.replacer.JavaReplacer;
import com.headwire.aemdc.replacer.Replacer;
import com.headwire.aemdc.util.ConfigUtil;


/**
 * Java Servlet creator
 *
 */
public class ServletRunner extends BasisRunner {

  private static final Logger LOG = LoggerFactory.getLogger(ServletRunner.class);
  private static final String HELP_FOLDER = "servlet";

  /**
   * Invoker
   */
  private final CommandMenu menu = new CommandMenu();
  private final Resource resource;
  private final Properties configProps;

  /**
   * Constructor
   *
   * @param resource
   *          - resource object
   * @throws IOException
   *           - IOException
   */
  public ServletRunner(final Resource resource) throws IOException {
    this.resource = resource;

    // Get Config Properties from config file
    configProps = ConfigUtil.getConfigProperties();

    LOG.debug("Servlet runner starting...");

    resource.setSourceFolderPath(configProps.getProperty(Constants.CONFIGPROP_SOURCE_SERVLETS_FOLDER));
    resource.setTargetFolderPath(configProps.getProperty(Constants.CONFIGPROP_TARGET_SERVLETS_FOLDER));

    // Set all other config properties in the resource
    setGlobalConfigProperties(configProps, resource);

    // Creates Invoker object, command object and configure them
    menu.setCommand("CreateFile", new CreateFileCommand(resource));
    menu.setCommand("ReplacePlaceHolders", new ReplacePlaceHoldersCommand(resource, getPlaceHolderReplacer()));
  }

  /**
   * Run commands
   *
   * @throws IOException
   */
  @Override
  protected void run() throws IOException {
    // Invoker invokes command
    menu.runCommand("CreateFile");
    menu.runCommand("ReplacePlaceHolders");
  }

  @Override
  public String getHelpFolder() {
    return HELP_FOLDER;
  }

  @Override
  public String getSourceFolder() {
    return resource.getSourceFolderPath();
  }

  @Override
  public Collection<File> listAvailableTemplates(final File dir) {
    final Collection<File> fileList = FileUtils.listFiles(dir, new String[] { Constants.FILE_EXT_JAVA }, true);
    return fileList;
  }

  @Override
  public boolean checkConfiguration() {
    // get target folder
    final String targetPath = resource.getTargetFolderPath();
    if (StringUtils.isBlank(targetPath)) {
      LOG.error("The target folder {} is blank in the config file.", Constants.CONFIGPROP_TARGET_SERVLETS_FOLDER);
      return false;
    }

    // get target java source folder
    final String targetJavaSrcFolder = configProps.getProperty(Constants.CONFIGPROP_TARGET_JAVA_FOLDER);
    if (StringUtils.isBlank(targetJavaSrcFolder)) {
      LOG.error("The target java source folder {} is blank in the config file.",
          Constants.CONFIGPROP_TARGET_JAVA_FOLDER);
      return false;
    }

    final int pos = targetPath.indexOf(targetJavaSrcFolder);
    if (pos == -1) {
      LOG.error("The target java source folder {} is different to target path {} in the config file.",
          Constants.CONFIGPROP_TARGET_JAVA_FOLDER, targetPath);
      return false;
    }
    return true;
  }

  @Override
  public Replacer getPlaceHolderReplacer() {
    return new JavaReplacer(resource);
  }

}
