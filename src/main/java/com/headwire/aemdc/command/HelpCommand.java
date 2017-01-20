package com.headwire.aemdc.command;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.companion.Config;
import com.headwire.aemdc.companion.Resource;
import com.headwire.aemdc.util.Help;


/**
 * Concrete command to show help
 *
 */
public class HelpCommand implements Command {

  private static final Logger LOG = LoggerFactory.getLogger(HelpCommand.class);
  public static final String NAME = "HELP";

  private final Resource resource;
  private final Config config;

  /**
   * Constructor
   *
   * @param resource
   *          - resource
   */
  public HelpCommand(final Resource resource, final Config config) {
    this.resource = resource;
    this.config = config;
  }

  @Override
  public void execute() throws IOException {
    LOG.debug("Showing help...");
    final Help help = new Help(config);
    help.showHelp(resource);
  }
}
