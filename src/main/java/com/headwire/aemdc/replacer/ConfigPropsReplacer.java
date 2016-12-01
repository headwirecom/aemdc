package com.headwire.aemdc.replacer;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.companion.Resource;


/**
 * AEMDC Config properties place holders replacer.
 *
 */
public class ConfigPropsReplacer extends Replacer {

  private static final Logger LOG = LoggerFactory.getLogger(ConfigPropsReplacer.class);

  /**
   * Constructor
   */
  public ConfigPropsReplacer(final Resource resource) {
    this.resource = resource;
  }

  @Override
  protected String replaceCustomXmlPlaceHolders(final String text, final Map<String, String> placeholders)
      throws IOException {
    return text;
  }

  @Override
  protected String replaceCustomTextPlaceHolders(final String text, final Map<String, String> placeholders) {
    return text;
  }
}