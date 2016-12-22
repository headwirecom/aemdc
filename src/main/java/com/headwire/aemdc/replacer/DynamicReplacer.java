package com.headwire.aemdc.replacer;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.companion.Resource;


/**
 * Dynamic place holders replacer.
 *
 */
public class DynamicReplacer extends Replacer {

  private static final Logger LOG = LoggerFactory.getLogger(DynamicReplacer.class);

  /**
   * Constructor
   */
  public DynamicReplacer(final Resource resource) {
    this.resource = resource;
  }

  @Override
  protected String replaceCustomXmlPlaceHolders(final String text, final Map<String, String> placeholders) {
    return text;
  }

  @Override
  protected String replaceCustomTextPlaceHolders(final String text, final Map<String, String> placeholders) {
    return text;
  }

}