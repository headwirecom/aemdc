package com.headwire.aemdc.replacer;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.companion.Resource;


/**
 * OSGI Config place holders replacer.
 *
 */
public class OsgiReplacer extends Replacer {

  private static final Logger LOG = LoggerFactory.getLogger(OsgiReplacer.class);

  /**
   * Constructor
   */
  public OsgiReplacer(final Resource resource) {
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