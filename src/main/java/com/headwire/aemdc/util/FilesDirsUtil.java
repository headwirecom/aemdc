package com.headwire.aemdc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.companion.Constants;


/**
 * Files and Dirs Util
 *
 * @author Marat Saitov, 15.11.2016
 */
public class FilesDirsUtil {

  private static final Logger LOG = LoggerFactory.getLogger(FilesDirsUtil.class);

  /**
   * Constructor
   *
   */
  private FilesDirsUtil() {
  }

  /**
   * Get properties from property file
   *
   * @param filepath
   *          - file path
   * @return properties object
   */
  public static Properties getProperties(final String filepath) {
    final Properties props = new Properties();
    InputStream input = null;

    try {
      input = new FileInputStream(filepath);
      // load a properties file from class path
      props.load(input);
    } catch (final IOException e) {
      LOG.error("Sorry, unable to find or read properties from file [{}].", filepath);
    } finally {
      if (input != null) {
        try {
          input.close();
        } catch (final IOException e) {
          LOG.error("Sorry, unable to close input stream from file [{}].", filepath);
        }
      }
    }
    return props;
  }

  /**
   * Get properties from property file from Context Class Loader
   *
   * @param filepath
   *          - file path
   * @return properties object
   */
  public static Properties getPropertiesFromContextClassLoader(final String filepath) {
    final Properties props = new Properties();
    InputStream input = null;

    try {
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      input = cl.getResourceAsStream(filepath);

      if (input == null) {
        cl = FilesDirsUtil.class.getClassLoader();
        input = cl.getResourceAsStream(filepath);
      }

      if (input != null) {
        // load a properties file from class path
        props.load(input);
      } else {
        LOG.error("Sorry, unable to find or read properties from file [{}].", filepath);
      }

    } catch (final IOException e) {
      LOG.error("Sorry, unable to find or read properties from file [{}].", filepath);
    } finally {
      if (input != null) {
        try {
          input.close();
        } catch (final IOException e) {
          LOG.error("Sorry, unable to close input stream from file {}.", filepath);
        }
      }
    }
    return props;
  }

  /**
   * Get list of all existing root sub directories
   *
   * @param rootDir
   *          - the root directory to list
   * @return list of sub directories
   */
  public static Collection<File> listRootDirs(final File rootDir) {
    final Collection<File> dirList = new ArrayList<File>();

    for (final File dir : FileUtils.listFilesAndDirs(rootDir, FalseFileFilter.INSTANCE,
        DirectoryFileFilter.INSTANCE)) {

      LOG.debug("Dir: {}", dir);

      // get only root directories
      final String name = getTemplateName(rootDir, dir);

      if (StringUtils.isNotBlank(name) && !name.contains("/")) {
        dirList.add(dir);
      }
    }
    return dirList;
  }

  /**
   * Get list of all existing files recursive
   *
   * @param rootDir
   *          - the root directory to list
   * @return file list
   */
  public static Collection<File> listFiles(final File rootDir) {
    final Collection<File> fileList = FileUtils.listFiles(rootDir, null, true);
    return fileList;
  }

  /**
   * Get list of all existing java files recursive
   *
   * @param rootDir
   *          - the root directory to list
   * @return file list
   */
  public static Collection<File> listJavaFiles(final File rootDir) {
    final Collection<File> fileList = FileUtils.listFiles(rootDir, new String[] { Constants.FILE_EXT_JAVA }, true);
    return fileList;
  }

  /**
   * Get list of XML files in the root folder
   *
   * @param rootDir
   *          - the root directory to list
   * @return file list
   */
  public static Collection<File> listXmlFiles(final File rootDir) {
    final Collection<File> fileList = FileUtils.listFiles(rootDir, new String[] { Constants.FILE_EXT_XML }, false);
    return fileList;
  }

  /**
   * Get list of all existing root sub directory names
   *
   * @param rootDir
   *          - the root directory to list
   * @return list of sub directories names
   */
  public static Collection<String> listRootDirNames(final File rootDir) {
    final Collection<String> dirList = new ArrayList<String>();
    for (final File dir : listRootDirs(rootDir)) {
      dirList.add(dir.getName());
    }
    return dirList;
  }

  /**
   * Get template name incl. subfolders.
   *
   * @param sourceDir
   *          - source templates directory
   * @param templateFile
   *          - template file under the directory
   * @return template name
   */
  public static String getTemplateName(final File sourceDir, final File templateFile) {
    // get template name incl. subfolders, for ex. "impl/SampleServiceImpl.java"
    String name = StringUtils.substringAfter(templateFile.getPath(), sourceDir.getPath());

    // convert to unix path format
    name = name.replace("\\", "/");

    // cut first slash
    if (name.indexOf("/") == 0) {
      name = name.substring(1);
    }
    LOG.debug("Template Name: {}", name);

    return name;
  }

  /**
   * Get template names incl. subfolders.
   *
   * @param sourceDir
   *          - source templates directory
   * @return template names
   */
  public static List<String> getTemplateNames(final File sourceDir) {
    final Collection<File> fileList = FileUtils.listFiles(sourceDir, null, true);
    final List<String> names = getTemplateNames(sourceDir, fileList);
    return names;
  }

  /**
   * Get template names incl. subfolders.
   *
   * @param sourceDir
   *          - source templates directory
   * @param templateFiles
   *          - list of template files under the directory
   * @return template names
   */
  public static List<String> getTemplateNames(final File sourceDir, final Collection<File> templateFiles) {
    final List<String> names = new ArrayList<String>();
    for (final File nextFile : templateFiles) {
      names.add(getTemplateName(sourceDir, nextFile));
    }
    return names;
  }

}