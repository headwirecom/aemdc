package com.headwire.aemc.command;

import java.util.Map;

/**
 * Concrete command to create file
 *
 */
public class CreateCommand implements Command {

	Map<String, String> params;

	public CreateCommand(Map<String, String> params) {
		this.params = params;
	}

	@Override
	public void execute() {
		System.out.println("Creating file " + params.get("path"));
	}

}
