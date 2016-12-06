package com.headwire.aemdc.replacer;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.companion.Constants;
import com.headwire.aemdc.companion.Resource;
import com.headwire.aemdc.util.ConfigUtil;


/**
 * Java Code place holders replacer.
 *
 */
public class JavaReplacer extends Replacer {

  private static final Logger LOG = LoggerFactory.getLogger(JavaReplacer.class);

  /**
   * Constructor
   */
  public JavaReplacer(final Resource resource) {
    this.resource = resource;
  }

  @Override
  protected String replaceCustomXmlPlaceHolders(final String text, final Map<String, String> placeholders)
      throws IOException {
    return text;
  }

  @Override
  protected String replaceCustomTextPlaceHolders(final String text, final Map<String, String> placeholders)
      throws IOException {
    String result = text;

    // Get Config Properties from config file
    final Properties configProps = ConfigUtil.getConfigProperties();

    // {{ java-class }}
    final String javaClassName = FilenameUtils.getBaseName(resource.getTargetName());
    result = result.replace(getPH(Constants.PLACEHOLDER_JAVA_CLASS), javaClassName);

    // {{ java-package }}
    final String javaClassFileName = FilenameUtils.getName(resource.getTargetName());
    final String targetPath = resource.getTargetFolderPath() + "/" + resource.getTargetName();
    final String targetJavaSrcFolder = configProps.getProperty(Constants.CONFIGPROP_TARGET_JAVA_FOLDER);

    // cut java file name, replace "/" with "."
    String javaPackage = StringUtils.substringAfter(targetPath, targetJavaSrcFolder + "/");
    javaPackage = StringUtils.substringBefore(javaPackage, "/" + javaClassFileName);
    javaPackage = StringUtils.replace(javaPackage, "/", ".");
    result = result.replace(getPH(Constants.PLACEHOLDER_JAVA_PACKAGE), javaPackage);

    return result;
  }

}