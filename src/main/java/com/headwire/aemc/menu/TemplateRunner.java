package com.headwire.aemc.menu;

import java.util.Map;

import com.headwire.aemc.command.CreateCommand;
import com.headwire.aemc.command.DeleteCommand;
import com.headwire.aemc.command.Menu;
import com.headwire.aemc.companion.Constants;

/**
 * Template creator
 *
 */
public class TemplateRunner implements BasisRunner {

	// Invoker
	private Menu menu = new Menu();
	public Map<String, String> params;

	/**
	 * Constructor
	 * 
	 * @param params
	 *            - params
	 */
	public TemplateRunner(Map<String, String> params) {
		this.params = params;

		params.put(Constants.PARAM_PATH, Constants.PATH_JCR_ROOT + Constants.PATH_APPS + "/"
				+ Constants.PARAM_APPS_FOLDER_NAME + Constants.PATH_TEMPLATES + "/" + Constants.FILE_NAME_TEMPLATE);

		// Creates Invoker object, command object and configure them
		menu.setCommand("Create", new CreateCommand(params));
		menu.setCommand("Delete", new DeleteCommand(params));
	}

	/**
	 * Run commands
	 */
	@Override
	public void run() {
		// Invoker invokes command
		menu.runCommand("Create");
		menu.runCommand("Delete");
	}
}
