package com.headwire.aemdc.command;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.companion.Resource;
import com.headwire.aemdc.util.FilesDirsUtil;


/**
 * Concrete command to copy directory
 *
 */
public class CopyDirCommand implements Command {

  private static final Logger LOG = LoggerFactory.getLogger(CopyDirCommand.class);
  public static final String NAME = "COPY_DIR";

  private final Resource resource;

  /**
   * Constructor
   *
   * @param resource
   *          - resource
   */
  public CopyDirCommand(final Resource resource) {
    this.resource = resource;
  }

  @Override
  public void execute() throws IOException {
    final String sourcePath = resource.getSourceFolderPath() + "/" + resource.getSourceName();
    final String targetPath = resource.getTargetFolderPath() + "/" + resource.getTargetName();
    final File srcDir = new File(sourcePath);
    final File targetDir = new File(targetPath);

    LOG.debug("Copying files from [{}] to [{}] ...", sourcePath, targetPath);

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

      if (!srcDir.exists()) {
        LOG.error("Can't get available templates. Directory {} doesn't exist.", srcDir);
      } else {
        // set copied template names
        final List<String> copiedTemplateNames = FilesDirsUtil.getTemplateNames(srcDir);
        resource.setCopiedTemplateNames(copiedTemplateNames);

        FileUtils.copyDirectory(srcDir, destDir);
        LOG.info("Directory {} created.", destDir);
      }

    } catch (final IOException e) {
      LOG.error("Can't create destination directory [{}] from source directory [{}]", srcDir, destDir);
      throw new IOException(e);
    }
  }

}
