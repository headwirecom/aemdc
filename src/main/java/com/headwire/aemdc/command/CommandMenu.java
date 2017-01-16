package com.headwire.aemdc.command;

import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.companion.Config;
import com.headwire.aemdc.companion.Resource;
import com.headwire.aemdc.replacer.Replacer;


/**
 * Invoker class, which holds command object and invokes method
 */
public class CommandMenu {

  private static final Logger LOG = LoggerFactory.getLogger(CommandMenu.class);

  SortedMap<Integer, Command> menuItems = new TreeMap<Integer, Command>();

  public void setCommand(final Integer operationNumber, final Command cmd) {
    menuItems.put(operationNumber, cmd);
  }

  public void runCommand(final Integer operationNumber) throws IOException {
    menuItems.get(operationNumber).execute();
  }

  public void setCommands(final String[] operations, final Resource resource, final Replacer replacer,
      final Config config) {
    int i = 0;
    for (final String operation : operations) {
      final Command command;
      switch (operation) {
        case CopyDirCommand.NAME:
          command = new CopyDirCommand(resource);
          break;
        case CopyFileCommand.NAME:
          command = new CopyFileCommand(resource);
          break;
        case CopyFilesCommand.NAME:
          command = new CopyFilesCommand(resource);
          break;
        case CreateFileFromResourceCommand.NAME:
          command = new CreateFileFromResourceCommand(resource);
          break;
        case ReplacePlaceHoldersCommand.NAME:
          command = new ReplacePlaceHoldersCommand(resource, replacer, config);
          break;
        case ReplacePathPlaceHoldersCommand.NAME:
          command = new ReplacePathPlaceHoldersCommand(resource, replacer, config);
          break;
        case HelpCommand.NAME:
          command = new HelpCommand(resource);
          break;
        default:
          LOG.error("Unknown command name [{}] for resource type [{}].", operation, resource.getType());
          return;
      }
      menuItems.put(new Integer(i), command);
      i++;
    }
  }

  public void runCommands() throws IOException {
    for (final Map.Entry<Integer, Command> entry : menuItems.entrySet()) {
      final Command command = entry.getValue();
      command.execute();
    }
  }

}
