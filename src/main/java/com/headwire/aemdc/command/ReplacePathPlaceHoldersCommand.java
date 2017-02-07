package com.headwire.aemdc.command;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.companion.Config;
import com.headwire.aemdc.companion.Resource;
import com.headwire.aemdc.replacer.Replacer;


/**
 * Concrete command to replace path place holders
 *
 */
public class ReplacePathPlaceHoldersCommand implements Command {

  private static final Logger LOG = LoggerFactory.getLogger(ReplacePathPlaceHoldersCommand.class);
  public static final String NAME = "REPLACE_PATH_PH";

  private final Resource resource;
  private final Config config;
  private final Replacer replacer;

  /**
   * Constructor
   *
   * @param resource
   *          - resource
   */
  public ReplacePathPlaceHoldersCommand(final Resource resource, final Config config, final Replacer replacer) {
    this.resource = resource;
    this.config = config;
    this.replacer = replacer;
  }

  @Override
  public void execute() throws IOException {
    String targetPath = resource.getTargetFolderPath();
    if (config.isDirTemplateStructure(resource.getType(), resource.getSourceName())) {
      targetPath += "/" + resource.getTargetName();
    } else {
      targetPath += getTargetSubPath();
    }

    LOG.debug("Replacing path place holders in the directory/file [{}] ...", targetPath);

    final File dest = new File(targetPath);

    if (!dest.exists()) {
      final String message = "Can't replace path place holders. Directory/file " + dest + " doesn't exist.";
      LOG.error(message);
      return;
    }

    final List<String> allExtList = resource.getExtensionsList();

    if (dest.isDirectory()) {
      // replace PH in only copied files
      final List<String> copiedTemplateNames = resource.getCopiedTemplateNames();
      for (final String nextName : copiedTemplateNames) {
        final File targetFile = new File(targetPath + "/" + nextName);
        final String extension = FilenameUtils.getExtension(targetFile.getName());
        if (allExtList.contains(extension)) {
          // replace path place holders
          replacePathPlaceHolders(targetFile);
        }
      }
    } else {
      final String extension = FilenameUtils.getExtension(dest.getName());
      if (allExtList.contains(extension)) {
        replacePathPlaceHolders(dest);
      }
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
          FileUtils.copyFile(srcFile, destFile);
          FileUtils.deleteQuietly(srcFile);
          LOG.debug("File {} removed to {}", srcFile, destFile);
        }
      }

    } catch (final IOException e) {
      LOG.error("Can't replace path place holders in the file [{}]", srcFile);
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