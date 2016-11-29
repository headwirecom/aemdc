package com.headwire.aemdc.replacer;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.companion.Constants;
import com.headwire.aemdc.companion.Resource;


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
  protected String replaceCustomTextPlaceHolders(final String text, final Map<String, String> placeholders) {
    String result = text;

    // {{ java-class }}
    final String javaClassName = resource.getJavaClassName();
    result = result.replace(getPH(Constants.PLACEHOLDER_JAVA_CLASS), javaClassName);

    // {{ java-package }}
    final String javaPackage = resource.getJavaClassPackage();
    result = result.replace(getPH(Constants.PLACEHOLDER_JAVA_PACKAGE), javaPackage);

    return result;
  }

}