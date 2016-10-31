package com.headwire.aemc.command;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemc.companion.Resource;


/**
 * Concrete command to create directory
 *
 */
public class CreateDirCommand implements Command {

  private static final Logger LOG = LoggerFactory.getLogger(CreateDirCommand.class);

  private final Resource resource;

  /**
   * Constructor
   *
   * @param resource
   *          - resource
   */
  public CreateDirCommand(final Resource resource) {
    this.resource = resource;
  }

  @Override
  public void execute() throws IOException {
    final String sourcePath = resource.getSourceFolderPath() + "/" + resource.getSourceName();
    final String targetPath = resource.getTargetFolderPath() + "/" + resource.getTargetName();
    final File srcDir = new File(sourcePath);
    final File targetDir = new File(targetPath);

    LOG.info("Coping files from [" + sourcePath + "] to [" + targetPath + "] ...");

    // copy directory
    copyDirectory(srcDir, targetDir);
  }

  /**
   * Copy source directory to destination directory
   *
   * @param srcDir
   *          - source directory
   * @param destDir
   *          - destination directory
   * @throws IOException
   *           - IOException
   */
  private void copyDirectory(final File srcDir, final File destDir) throws IOException {
    try {
      if (resource.isToWarnDestDir() && destDir.exists()) {
        final String message = "Directory " + destDir + " already exists and will be not overwritten.";
        LOG.error(message);
        throw new IllegalStateException(message);
      }
      if (resource.isToDeleteDestDir()) {
        final File toDeleteDir = FileUtils.getFile(destDir.getPath());
        if (toDeleteDir.exists()) {
          FileUtils.deleteDirectory(toDeleteDir);
        }
      }

      FileUtils.copyDirectory(srcDir, destDir);
      LOG.info("Directory " + destDir + " created.");

    } catch (final IOException e) {
      LOG.error("Can't create destination directory [" + srcDir + "] from source directory [" + destDir + "]");
      throw new IOException(e);
    }
  }

}
