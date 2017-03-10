package com.headwire.aemdc.command;

import java.io.File;
import java.io.IOException;

import com.headwire.aemdc.util.FilesDirsUtil;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.companion.Config;
import com.headwire.aemdc.companion.Resource;


/**
 * Concrete command to copy file
 *
 */
public class CopyFileCommand implements Command {

  private static final Logger LOG = LoggerFactory.getLogger(CopyFileCommand.class);
  public static final String NAME = "COPY_FILE";

  private final Resource resource;
  private final Config config;

  /**
   * Constructor
   *
   * @param resource
   *          - resource
   */
  public CopyFileCommand(final Resource resource, final Config config) {
    this.resource = resource;
    this.config = config;
  }

  @Override
  public void execute() throws IOException {
    final String sourcePath = resource.getSourceFolderPath() + "/" + resource.getSourceName();
    final String targetPath = resource.getTargetFolderPath() + "/" + resource.getTargetName();
    final File srcFile = FilesDirsUtil.getFile(config.getBaseFolder(), sourcePath);
//    final File srcFile = new File(sourcePath);
    final File targetFile = new File(targetPath);

    LOG.debug("Copying file from [{}] to [{}] ...", sourcePath, targetPath);

    // copy file
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

      if (!srcFile.exists()) {
        LOG.error("Can't get available templates. File {} doesn't exist.", srcFile);
      } else {
        FileUtils.copyFile(srcFile, destFile);
        LOG.info("File {} created.", destFile);
      }

    } catch (final IOException e) {
      LOG.error("Can't copy source file [{}] to destination file [{}]", srcFile, destFile);
      throw new IOException(e);
    }
  }

  @Override
  public Resource getResource() {
    return resource;
  }

  @Override
  public Config getConfig() {
    return config;
  }

}
