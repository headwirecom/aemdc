package com.headwire.aemc.replacer;

import java.util.Map;

import com.headwire.aemc.companion.Constants;


/**
 * Replace place holders inside of templates.
 *
 */
public final class TextReplacer {

  private TextReplacer() {
  }

  /**
   * Replace place holders in the text
   *
   * @param text
   *          the text
   * @param params
   *          the params
   * @return result text
   */
  public static String replaceTextPlaceHolders(final String text, final Map<String, String> params) {
    final String result = text.replace("{{ jcr:title }}", params.get(Constants.PARAM_PROP_JCR_TITLE));
    return result;
  }

}