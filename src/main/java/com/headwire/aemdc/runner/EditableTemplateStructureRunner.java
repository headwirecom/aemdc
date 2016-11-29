package com.headwire.aemdc.runner;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.command.CommandMenu;
import com.headwire.aemdc.command.CreateDirCommand;
import com.headwire.aemdc.command.CreateFileCommand;
import com.headwire.aemdc.command.ReplacePlaceHoldersCommand;
import com.headwire.aemdc.companion.Constants;
import com.headwire.aemdc.companion.Resource;
import com.headwire.aemdc.replacer.EditableTemplateStructureReplacer;
import com.headwire.aemdc.replacer.Replacer;
import com.headwire.aemdc.util.ConfigUtil;


/**
 * Editable Template Structure creator
 *
 */
public class EditableTemplateStructureRunner extends BasisRunner {

  private static final Logger LOG = LoggerFactory.getLogger(EditableTemplateStructureRunner.class);
  private static final String HELP_FOLDER = "confstr";

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
  public EditableTemplateStructureRunner(final Resource resource) throws IOException {
    this.resource = resource;

    // Get Config Properties from config file
    configProps = ConfigUtil.getConfigProperties();

    LOG.debug("Editable Template Structure runner starting...");

    resource.setSourceFolderPath(configProps.getProperty(Constants.CONFIGPROP_SOURCE_CONF_FOLDER));
    resource.setTargetFolderPath(configProps.getProperty(Constants.CONFIGPROP_TARGET_CONF_FOLDER));

    // Set global config properties in the resource
    setGlobalConfigProperties(configProps, resource);

    // Creates Invoker object, command object and configure them
    menu.setCommand("CreateDir", new CreateDirCommand(resource));
    menu.setCommand("ReplacePlaceHolders", new ReplacePlaceHoldersCommand(resource));

    // copy /conf/.content.xml
    final Resource confResource = resource.clone();
    confResource.setSourceName(".content.xml");
    confResource.setTargetName(".content.xml");
    menu.setCommand("CreateFile", new CreateFileCommand(confResource));
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
    final String targetPath = resource.getTargetFolderPath();
    // get target UI folder
    final String targetUIFolder = configProps.getProperty(Constants.CONFIGPROP_TARGET_UI_FOLDER);
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
    return new EditableTemplateStructureReplacer(resource);
  }
}
