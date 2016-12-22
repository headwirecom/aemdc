package com.headwire.aemdc.command;

import java.io.IOException;


/**
 * Command interface for implementing concrete command
 *
 */
interface Command {

  String NAME = "SET_OWN_COMMAND_NAME";

  void execute() throws IOException;
}
