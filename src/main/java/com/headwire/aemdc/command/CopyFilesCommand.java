package com.headwire.aemdc.command;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.companion.Config;
import com.headwire.aemdc.companion.Resource;
import com.headwire.aemdc.util.FilesDirsUtil;


/**
 * Concrete command to copy files
 *
 */
public class CopyFilesCommand implements Command {

  private static final Logger LOG = LoggerFactory.getLogger(CopyFilesCommand.class);
  public static final String NAME = "COPY_FILES";

  private final Resource resource;
  private final Config config;

  /**
   * Constructor
   *
   * @param resource
   *          - resource
   */
  public CopyFilesCommand(final Resource resource, final Config config) {
    this.resource = resource;
    this.config = config;
  }

  @Override
  public void execute() throws IOException {
    final String sourcePath = resource.getSourceFolderPath();
    final String targetPath = resource.getTargetFolderPath();
    final File srcDir = new File(sourcePath);

    LOG.debug("Copying files from [{}] to [{}] ...", sourcePath, targetPath);

    if (!srcDir.exists()) {
      LOG.error("Can't get available templates. Directory {} doesn't exist.", srcDir);
    } else {
      final Collection<File> fileList = FileUtils.listFiles(srcDir, null, true);

      // set copied template names
      final List<String> copiedTemplateNames = FilesDirsUtil.getTemplateNames(srcDir, fileList);
      resource.setCopiedTemplateNames(copiedTemplateNames);

      // copy files
      for (final File nextFile : fileList) {
        final File targetFile = new File(targetPath + "/" + nextFile.getName());
        copyFile(nextFile, targetFile);
      }
    }
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

}
