package com.headwire.aemdc.command;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.companion.Resource;
import com.headwire.aemdc.util.TextReplacer;


/**
 * Concrete command to replace path place holders
 *
 */
public class ReplacePathPlaceHoldersCommand implements Command {

  private static final Logger LOG = LoggerFactory.getLogger(ReplacePathPlaceHoldersCommand.class);

  private final Resource resource;

  /**
   * Constructor
   *
   * @param resource
   *          - resource
   */
  public ReplacePathPlaceHoldersCommand(final Resource resource) {
    this.resource = resource;
  }

  @Override
  public void execute() throws IOException {
    final String targetPath = resource.getTargetFolderPath() + "/" + resource.getTargetName();
    LOG.debug("Replacing path place holders in the directory/file [{}] ...", targetPath);

    final File dest = new File(targetPath);

    if (!dest.exists()) {
      final String message = "Can't replace path place holders. Directory/file " + dest + " doesn't exist.";
      LOG.error(message);
      throw new IllegalStateException(message);
    }

    if (dest.isDirectory()) {
      // get complete file list recursive
      final Collection<File> fileList = FileUtils.listFiles(dest, null, true);
      final Iterator<File> iter = fileList.iterator();
      while (iter.hasNext()) {
        final File nextFile = iter.next();

        // replace path place holders
        replacePathPlaceHolders(nextFile);
      }
    } else {
      replacePathPlaceHolders(dest);
    }
  }

  /**
   * Replace path place holders in file
   *
   * @param srcFile
   *          - file to be renamed/removed
   * @throws IOException
   *           - IOException
   */
  private void replacePathPlaceHolders(final File srcFile) throws IOException {
    try {
      final String filePath = srcFile.getPath();
      final String newPath = TextReplacer.replacePathPlaceHolders(filePath, resource);

      // rename file
      if (!filePath.equals(newPath)) {
        final File destFile = new File(newPath);
        FileUtils.moveFile(srcFile, destFile);
        LOG.debug("File {} removed to {}", srcFile, destFile);
      }

    } catch (final IOException e) {
      LOG.error("Can't replace path place holders in the file [{}]", srcFile);
      throw new IOException(e);
    }
  }
}