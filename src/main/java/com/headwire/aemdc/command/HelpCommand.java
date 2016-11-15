package com.headwire.aemdc.command;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.companion.Resource;
import com.headwire.aemdc.util.HelpUtil;


/**
 * Concrete command to show help
 *
 */
public class HelpCommand implements Command {

  private static final Logger LOG = LoggerFactory.getLogger(HelpCommand.class);

  private final Resource resource;

  /**
   * Constructor
   *
   * @param resource
   *          - resource
   */
  public HelpCommand(final Resource resource) {
    this.resource = resource;
  }

  @Override
  public void execute() throws IOException {
    LOG.debug("Showing help...");
    HelpUtil.showHelp(resource);
  }
}
