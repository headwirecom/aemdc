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
 * Concrete command to replace path place holders
 *
 */
public class ReplacePathPlaceHoldersCommand implements Command {

  private static final Logger LOG = LoggerFactory.getLogger(ReplacePathPlaceHoldersCommand.class);

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
  public ReplacePathPlaceHoldersCommand(final Resource resource, final Replacer replacer) throws IOException {
    this.resource = resource;
    this.replacer = replacer;
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
      final String newPath = replacer.replacePathPlaceHolders(filePath);

      // if the new path is different to old path then rename file
      if (!filePath.equals(newPath)) {
        final File destFile = new File(newPath);

        if (resource.isToWarnDestDir() && destFile.exists()) {
          final String message = "File " + destFile + " already exists and will be not overwritten.";
          LOG.error(message);
          throw new IllegalStateException(message);
        }

        if (!srcFile.exists()) {
          LOG.error("Can't get available templates. File {} doesn't exist.", srcFile);
        } else {
          FileUtils.deleteQuietly(destFile);
          FileUtils.moveFile(srcFile, destFile);
          LOG.debug("File {} removed to {}", srcFile, destFile);
        }
      }

    } catch (final IOException e) {
      LOG.error("Can't replace path place holders in the file [{}]", srcFile);
      throw new IOException(e);
    }
  }
}