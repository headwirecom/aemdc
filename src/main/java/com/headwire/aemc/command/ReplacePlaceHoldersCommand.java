package com.headwire.aemc.command;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemc.companion.Constants;
import com.headwire.aemc.companion.Resource;
import com.headwire.aemc.util.TextReplacer;


/**
 * Concrete command to replace place holders
 *
 */
public class ReplacePlaceHoldersCommand implements Command {

  private static final Logger LOG = LoggerFactory.getLogger(ReplacePlaceHoldersCommand.class);

  private final Resource resource;

  /**
   * Constructor
   *
   * @param resource
   *          - resource
   */
  public ReplacePlaceHoldersCommand(final Resource resource) {
    this.resource = resource;
  }

  @Override
  public void execute() throws IOException {
    final String targetPath = resource.getTargetFolderPath() + "/" + resource.getTargetName();
    LOG.info("Replacing place holders in the directory/file [" + targetPath + "] ...");

    final File dest = new File(targetPath);

    if (!dest.exists()) {
      final String message = "Can't replace place holders. Directory/file " + dest + " doesn't exist.";
      LOG.error(message);
      throw new IllegalStateException(message);
    }

    if (dest.isDirectory()) {
      // get file list recursive with defined extentions
      final Collection<File> fileList = FileUtils.listFiles(dest, resource.getExtentions(), true);
      final Iterator<File> iter = fileList.iterator();
      while (iter.hasNext()) {
        final File nextFile = iter.next();

        // replace place holders
        replacePlaceHolders(nextFile);
      }
    } else {
      replacePlaceHolders(dest);
    }
  }

  /**
   * Replace place holders in file
   *
   * @param destFile
   *          - destination file
   * @throws IOException
   *           - IOException
   */
  private void replacePlaceHolders(final File destFile) throws IOException {
    try {
      String fileText = FileUtils.readFileToString(destFile, Constants.ENCODING);

      final List<String> allExtList = resource.getExtentionsList();
      final String extention = FilenameUtils.getExtension(destFile.getName());

      if (Constants.FILE_EXT_XML.equals(extention)) {
        fileText = TextReplacer.replaceXmlPlaceHolders(fileText, resource);
      } else if (Constants.FILE_EXT_JAVA.equals(extention)) {
        fileText = TextReplacer.replaceJavaPlaceHolders(fileText, resource);
      } else if (allExtList.contains(extention)) {
        fileText = TextReplacer.replaceTextPlaceHolders(fileText, resource);
      }

      FileUtils.writeStringToFile(destFile, fileText, Constants.ENCODING);

      LOG.info("Place holders replaced in the file [" + destFile + "]");

    } catch (final IOException e) {
      LOG.error("Can't replace place holders in the file [" + destFile + "]");
      throw new IOException(e);
    }
  }
}