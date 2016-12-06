package com.headwire.aemdc.runner;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.command.CommandMenu;
import com.headwire.aemdc.command.CreateDirCommand;
import com.headwire.aemdc.command.CreateFileCommand;
import com.headwire.aemdc.command.ReplacePlaceHoldersCommand;
import com.headwire.aemdc.companion.Constants;
import com.headwire.aemdc.companion.Resource;
import com.headwire.aemdc.replacer.PageReplacer;
import com.headwire.aemdc.replacer.Replacer;
import com.headwire.aemdc.util.ConfigUtil;


/**
 * Content Page creator
 *
 */
public class PageRunner extends BasisRunner {

  private static final Logger LOG = LoggerFactory.getLogger(PageRunner.class);
  private static final String HELP_FOLDER = "page";

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
  public PageRunner(final Resource resource) throws IOException {
    this.resource = resource;

    // Get Config Properties from config file
    configProps = ConfigUtil.getConfigProperties();

    LOG.debug("Page runner starting...");

    resource.setSourceFolderPath(configProps.getProperty(Constants.CONFIGPROP_SOURCE_CONTENT_FOLDER));
    resource.setTargetFolderPath(configProps.getProperty(Constants.CONFIGPROP_TARGET_CONTENT_FOLDER));

    // Set global config properties in the resource
    setGlobalConfigProperties(configProps, resource);

    // Creates Invoker object, command object and configure them
    menu.setCommand("CreateDir", new CreateDirCommand(resource));
    menu.setCommand("ReplacePlaceHolders", new ReplacePlaceHoldersCommand(resource, getPlaceHolderReplacer()));

    // copy /content/.content.xml
    final Resource contentResource = resource.clone();
    contentResource.setSourceName(".content.xml");
    contentResource.setTargetName(".content.xml");
    menu.setCommand("CreateFile", new CreateFileCommand(contentResource));
  }

  /**
   * Run commands
   *
   * @throws IOException
   */
  @Override
  protected void run() throws IOException {
    // Invoker invokes command
    menu.runCommand("CreateDir");
    menu.runCommand("ReplacePlaceHolders");
    menu.runCommand("CreateFile");
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
    final Collection<File> fileList = listRootDirs(dir);
    return fileList;
  }

  @Override
  public boolean checkConfiguration() {
    // get target content folder
    final String targetPath = resource.getTargetFolderPath();
    if (StringUtils.isBlank(targetPath)) {
      LOG.error("The target folder {} is blank in the config file.", Constants.CONFIGPROP_TARGET_CONTENT_FOLDER);
      return false;
    }

    // get target UI folder
    final String targetUIFolder = configProps.getProperty(Constants.CONFIGPROP_TARGET_UI_FOLDER);
    if (StringUtils.isBlank(targetUIFolder)) {
      LOG.error("The target target UI folder {} is blank in the config file.", Constants.CONFIGPROP_TARGET_UI_FOLDER);
      return false;
    }

    final int pos = targetPath.indexOf(targetUIFolder);
    if (pos == -1) {
      LOG.error("The target target UI folder {} is different to target path {} in the config file.",
          Constants.CONFIGPROP_TARGET_UI_FOLDER, targetPath);
      return false;
    }
    return true;
  }

  @Override
  public Replacer getPlaceHolderReplacer() {
    return new PageReplacer(resource);
  }

}
