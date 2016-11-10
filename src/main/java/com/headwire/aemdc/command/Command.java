package com.headwire.aemdc.command;

import java.io.IOException;


/**
 * Command interface for implementing concrete command
 *
 */
interface Command {

  public void execute() throws IOException;
}
