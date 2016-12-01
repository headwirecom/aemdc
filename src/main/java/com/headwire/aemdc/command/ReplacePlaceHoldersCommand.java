package com.headwire.aemdc.command;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.companion.Resource;
import com.headwire.aemdc.replacer.Replacer;


/**
 * Concrete command to replace place holders
 *
 */
public class ReplacePlaceHoldersCommand implements Command {

  private static final Logger LOG = LoggerFactory.getLogger(ReplacePlaceHoldersCommand.class);

  private final Resource resource;
  private final Replacer replacer;

  /**
   * Constructor
   *
   * @param resource
   *          - resource
   * @throws IOException
   *           IOException
   */
  public ReplacePlaceHoldersCommand(final Resource resource, final Replacer replacer) throws IOException {
    this.resource = resource;
    this.replacer = replacer;
  }

  @Override
  public void execute() throws IOException {
    final String targetPath = resource.getTargetFolderPath() + "/" + resource.getTargetName();
    LOG.debug("Replacing place holders in the directory/file [{}] ...", targetPath);

    final File dest = new File(targetPath);

    if (!dest.exists()) {
      final String message = "Can't replace place holders. Directory/file " + dest + " doesn't exist.";
      LOG.error(message);
      throw new IllegalStateException(message);
    }

    if (dest.isDirectory()) {
      // get file list recursive with defined extentions
      final Collection<File> fileList = FileUtils.listFiles(dest, resource.getExtentions(), true);
      final Iterator<File> iter = fileList.iterator();
      while (iter.hasNext()) {
        final File nextFile = iter.next();

        // replace place holders
        replacer.replacePlaceHolders(nextFile);
      }
    } else {
      replacer.replacePlaceHolders(dest);
    }
  }
}