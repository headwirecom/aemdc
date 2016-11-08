package com.headwire.aemc.command;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemc.companion.Resource;


/**
 * Concrete command to create file
 *
 */
public class CreateFileCommand implements Command {

  private static final Logger LOG = LoggerFactory.getLogger(CreateFileCommand.class);

  private final Resource resource;

  /**
   * Constructor
   *
   * @param resource
   *          - resource
   */
  public CreateFileCommand(final Resource resource) {
    this.resource = resource;
  }

  @Override
  public void execute() throws IOException {
    final String sourcePath = resource.getSourceFolderPath() + "/" + resource.getSourceName();
    final String targetPath = resource.getTargetFolderPath() + "/" + resource.getTargetName();
    final File srcFile = new File(sourcePath);
    final File targetFile = new File(targetPath);

    LOG.info("Coping file from [{}] to [{}] ...", sourcePath, targetPath);

    // copy directory
    copyFile(srcFile, targetFile);
  }

  /**
   * Copy source file to destination file
   *
   * @param srcFile
   *          - source file
   * @param destFile
   *          - destination file
   * @throws IOException
   *           - IOException
   */
  private void copyFile(final File srcFile, final File destFile) throws IOException {
    try {
      if (resource.isToWarnDestDir() && destFile.exists()) {
        final String message = "File " + destFile + " already exists and will be not overwritten.";
        LOG.error(message);
        throw new IllegalStateException(message);
      }

      FileUtils.copyFile(srcFile, destFile);
      LOG.info("File {} created.", destFile);

    } catch (final IOException e) {
      LOG.error("Can't copy source file [{}] to destination file [{}]", srcFile, destFile);
      throw new IOException(e);
    }
  }

}
