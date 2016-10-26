package com.headwire.aemc.command;

import java.util.Map;

/**
 * Concrete command to delete files
 *
 */
public class DeleteCommand implements Command {

	Map<String, String> params;

	public DeleteCommand(Map<String, String> params) {
		this.params = params;
	}

	@Override
	public void execute() {
		System.out.println("Deleting file " + params.get("path"));
	}
}
