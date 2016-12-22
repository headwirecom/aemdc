package com.headwire.aemdc.runner;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.command.CommandMenu;
import com.headwire.aemdc.command.CopyDirCommand;
import com.headwire.aemdc.command.CopyFileCommand;
import com.headwire.aemdc.command.ReplacePlaceHoldersCommand;
import com.headwire.aemdc.companion.Config;
import com.headwire.aemdc.companion.Constants;
import com.headwire.aemdc.companion.Resource;
import com.headwire.aemdc.replacer.EditableTemplateStructureReplacer;
import com.headwire.aemdc.replacer.Replacer;
import com.headwire.aemdc.util.FilesDirsUtil;


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
  private final Config config;

  /**
   * Constructor
   *
   * @param resource
   *          - resource object
   */
  public EditableTemplateStructureRunner(final Resource resource) {
    this.resource = resource;

    // Get Properties Config from config file
    config = new Config();

    LOG.debug("Editable Template Structure runner starting...");

    resource.setSourceFolderPath(config.getProperty(Constants.CONFIGPROP_SOURCE_CONF_FOLDER));
    resource.setTargetFolderPath(config.getProperty(Constants.CONFIGPROP_TARGET_CONF_FOLDER));

    // Set global config properties in the resource
    setGlobalConfigProperties(config, resource);

    // Creates Invoker object, command object and configure them
    menu.setCommand(1, new CopyDirCommand(resource));
    menu.setCommand(2, new ReplacePlaceHoldersCommand(resource, getPlaceHolderReplacer()));

    // copy /conf/.content.xml
    final Resource confResource = resource.clone();
    confResource.setSourceName(".content.xml");
    confResource.setTargetName(".content.xml");
    menu.setCommand(3, new CopyFileCommand(confResource));
  }

  /**
   * Run commands
   *
   * @throws IOException
   */
  @Override
  public void run() throws IOException {
    // Invoker invokes command
    menu.runCommands();
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
    final Collection<File> fileList = FilesDirsUtil.listRootDirs(dir);
    return fileList;
  }

  @Override
  public Replacer getPlaceHolderReplacer() {
    return new EditableTemplateStructureReplacer(resource);
  }
}
