package com.headwire.aemc.command;

import java.io.IOException;


/**
 * Command interface for implementing concrete command
 *
 */
interface Command {

  public void execute() throws IOException;
}
