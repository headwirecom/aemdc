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
  private final Config config;
  private final Replacer replacer;

  /**
   * Constructor
   *
   * @param resource
   *          - resource
   */
  public ReplacePlaceHoldersCommand(final Resource resource, final Config config, final Replacer replacer) {
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

    // final String targetPath = resource.getTargetFolderPath() + "/" + resource.getTargetName();
    LOG.debug("Replacing place holders in the directory/file [{}] ...", targetPath);

    final File dest = new File(targetPath);

    if (!dest.exists()) {
      final String message = "Can't replace place holders. Directory/file " + dest + " doesn't exist.";
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
          replacer.replacePlaceHolders(targetFile);
        }
      }

    } else {
      final String extension = FilenameUtils.getExtension(dest.getName());
      if (allExtList.contains(extension)) {
        replacer.replacePlaceHolders(dest);
      }
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