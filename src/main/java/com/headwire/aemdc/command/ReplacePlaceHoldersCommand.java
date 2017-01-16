package com.headwire.aemdc.command;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.companion.Config;
import com.headwire.aemdc.companion.Resource;
import com.headwire.aemdc.replacer.Replacer;


/**
 * Concrete command to replace place holders
 *
 */
public class ReplacePlaceHoldersCommand implements Command {

  private static final Logger LOG = LoggerFactory.getLogger(ReplacePlaceHoldersCommand.class);
  public static final String NAME = "REPLACE_PH";

  private final Resource resource;
  private final Replacer replacer;
  private final Config config;

  /**
   * Constructor
   *
   * @param resource
   *          - resource
   */
  public ReplacePlaceHoldersCommand(final Resource resource, final Replacer replacer, final Config config) {
    this.resource = resource;
    this.replacer = replacer;
    this.config = config;
  }

  @Override
  public void execute() throws IOException {
    String targetPath = resource.getTargetFolderPath();
    if (config.isDirTemplateStructure(resource.getType(), resource.getSourceName())) {
      targetPath += "/" + resource.getTargetName();
    }

    // final String targetPath = resource.getTargetFolderPath() + "/" + resource.getTargetName();
    LOG.debug("Replacing place holders in the directory/file [{}] ...", targetPath);

    final File dest = new File(targetPath);

    if (!dest.exists()) {
      final String message = "Can't replace place holders. Directory/file " + dest + " doesn't exist.";
      LOG.error(message);
      throw new IllegalStateException(message);
    }

    final List<String> allExtList = resource.getExtentionsList();

    if (dest.isDirectory()) {
      /*
      // get file list recursive with defined extentions
      final Collection<File> fileList = FileUtils.listFiles(dest, resource.getExtentions(), true);
      final Iterator<File> iter = fileList.iterator();
      while (iter.hasNext()) {
        final File nextFile = iter.next();

        // replace place holders
        replacer.replacePlaceHolders(nextFile);
      }
      */
      final List<String> copiedTemplateNames = resource.getCopiedTemplateNames();
      for (final String nextName : copiedTemplateNames) {
        final File targetFile = new File(targetPath + "/" + nextName);
        final String extention = FilenameUtils.getExtension(targetFile.getName());
        if (allExtList.contains(extention)) {
          replacer.replacePlaceHolders(targetFile);
        }
      }

    } else {
      final String extention = FilenameUtils.getExtension(dest.getName());
      if (allExtList.contains(extention)) {
        replacer.replacePlaceHolders(dest);
      }
    }
  }
}