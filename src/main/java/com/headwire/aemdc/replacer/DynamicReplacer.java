package com.headwire.aemdc.replacer;

import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.companion.Constants;
import com.headwire.aemdc.companion.Resource;


/**
 * Dynamic place holders replacer.
 *
 */
public class DynamicReplacer extends Replacer {

  private static final Logger LOG = LoggerFactory.getLogger(DynamicReplacer.class);

  private final Properties dynProps;

  /**
   * Constructor
   */
  public DynamicReplacer(final Resource resource) {
    this.resource = resource;
    dynProps = config.getDynamicProperties(resource.getType());
  }

  @Override
  protected String replaceCustomXmlPlaceHolders(final String text, final Map<String, String> placeholders) {
    final String result = replaceDynamicPlaceHolders(text, true);
    return result;
  }

  @Override
  protected String replaceCustomTextPlaceHolders(final String text, final Map<String, String> placeholders) {
    String result = replaceDynamicPlaceHolders(text, false);

    // {{ java-class }}
    result = result.replace(getPH(Constants.PLACEHOLDER_JAVA_CLASS), getTargetJavaClassName());

    // {{ java-package }}
    result = result.replace(getPH(Constants.PLACEHOLDER_JAVA_PACKAGE), getTargetJavaPackage());

    return result;
  }

  /**
   * Replace placeholders with predefined default values from dynamic type properties config
   *
   * @param text
   *          - text where to replace placeholders
   * @param xmlType
   *          - true replacement in xml file
   * @return replaced text
   */
  private String replaceDynamicPlaceHolders(final String text, final boolean xmlType) {
    String result = text;
    final Enumeration<?> e = dynProps.propertyNames();
    while (e.hasMoreElements()) {
      final String key = (String) e.nextElement();

      // get only default placeholders predefined in the type properties config
      if (key.startsWith("_")) {
        // get key without "_"
        final String ph = getPH(key.substring(1));
        String phValue = dynProps.getProperty(key);

        // {{targetname}}
        phValue = phValue.replace(getPathPH(Constants.PLACEHOLDER_TARGET_NAME), getTargetLastName());

        // {{targetCompModel}}
        phValue = phValue.replace(getPathPH(Constants.PLACEHOLDER_TARGET_COMP_MODEL), getTargetCompModelName());

        // convert to XML format?
        phValue = xmlType ? getCrxXMLValue(phValue) : phValue;

        result = result.replace(ph, phValue);

        LOG.debug("'{}' replacing with '{}'", ph, phValue);
      }
    }
    return result;
  }

}