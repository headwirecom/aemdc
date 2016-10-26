package com.headwire.aemc.menu;

import java.util.Map;

import com.headwire.aemc.command.CreateCommand;
import com.headwire.aemc.command.Menu;
import com.headwire.aemc.command.ReplacePlaceHoldersCommand;
import com.headwire.aemc.companion.Constants;


/**
 * Template creator
 *
 */
public class TemplateRunner implements BasisRunner {

  // Invoker
  private final Menu menu = new Menu();
  public Map<String, String> params;

  /**
   * Constructor
   *
   * @param params
   *          - params
   */
  public TemplateRunner(final Map<String, String> params) {
    this.params = params;

    params.put(Constants.PARAM_SOURCE_PATH, Constants.PATH_PLACEHOLDERS_ROOT + Constants.PATH_APPS + "/"
        + Constants.PLACEHOLDER_YOUR_PROJECT_NAME + Constants.PATH_TEMPLATES + "/"
        + Constants.PLACEHOLDER_YOUR_TEMPLATE_NAME + "/" + Constants.FILE_NAME_TEMPLATE);

    params.put(Constants.PARAM_TARGET_PATH, Constants.PATH_JCR_ROOT + Constants.PATH_APPS + "/"
        + params.get(Constants.PARAM_PROJECT_NAME) + Constants.PATH_TEMPLATES + "/"
        + params.get(Constants.PARAM_TEMPLATE_NAME) + "/" + Constants.FILE_NAME_TEMPLATE);

    // Creates Invoker object, command object and configure them
    menu.setCommand("ReplacePlaceHolders", new ReplacePlaceHoldersCommand(params));
    menu.setCommand("Create", new CreateCommand(params));
  }

  /**
   * Run commands
   */
  @Override
  public void run() {
    // Invoker invokes command
    menu.runCommand("ReplacePlaceHolders");
    menu.runCommand("Create");
  }
}
