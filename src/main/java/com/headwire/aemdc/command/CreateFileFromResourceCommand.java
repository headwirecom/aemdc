package com.headwire.aemdc.command;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.companion.Config;
import com.headwire.aemdc.companion.Resource;


/**
 * Concrete command to create file from resource file
 *
 */
public class CreateFileFromResourceCommand implements Command {

  private static final Logger LOG = LoggerFactory.getLogger(CreateFileFromResourceCommand.class);
  public static final String NAME = "CREATE_FILE_FROM_RESOURCE";

  private final Resource resource;
  private final Config config;

  /**
   * Constructor
   *
   * @param resource
   *          - resource
   */
  public CreateFileFromResourceCommand(final Resource resource, final Config config) {
    this.resource = resource;
    this.config = config;
  }

  @Override
  public void execute() throws IOException {
    final String sourcePath = resource.getSourceFolderPath() + "/" + resource.getSourceName();
    final String targetPath = resource.getTargetFolderPath() + "/" + resource.getTargetName();

    LOG.debug("Create file from [{}] to [{}] ...", sourcePath, targetPath);

    // create file
    createFile(sourcePath, targetPath);
  }

  /**
   * Create destination file from source file under resources folder
   *
   * @param sourcePath
   *          - source file path from resources
   * @param targetPath
   *          - destination file path
   * @throws IOException
   *           - IOException
   */
  private void createFile(final String sourcePath, final String targetPath) throws IOException {
    InputStream input = null;
    final File destFile = new File(targetPath);

    try {
      if (destFile.exists()) {
        LOG.error("File [{}] already exists and will be not overwritten.", destFile);
      } else {
        input = Thread.currentThread().getContextClassLoader().getResourceAsStream(sourcePath);
        FileUtils.copyInputStreamToFile(input, destFile);
        LOG.info("File {} created.", destFile);
      }
    } catch (final IOException e) {
      LOG.error("Can't create destination file [{}] from source file [{}]", destFile, sourcePath);
      throw new IOException(e);
    } finally {
      if (input != null) {
        try {
          input.close();
        } catch (final IOException e) {
          LOG.error("Sorry, unable to close input stream from file {}.", sourcePath);
          throw new IOException(e);
        }
      }
    }
  }

}
