package com.headwire.aemdc.companion;

import org.apache.commons.lang3.StringUtils;


/**
 * Argumets parameter
 */
public class Param {

  private String key;
  private String value;

  /**
   * Constructor
   */
  public Param() {
  }

  /**
   * Constructor
   *
   * @param arg
   *          - command argument
   */
  public Param(final String arg) {
    // check for valid params like "paramName=paramValue"
    final String message = "Parameter [" + arg + "] must be in form \"paramName=paramValue\"";
    if (StringUtils.isBlank(arg)) {
      throw new IllegalArgumentException(message);
    }
    final int splitPos = arg.indexOf("=");
    if (splitPos < 1) {
      throw new IllegalArgumentException(message);
    }

    // get param key and value
    key = StringUtils.trim(arg.substring(0, splitPos));
    if (StringUtils.isBlank(key)) {
      throw new IllegalArgumentException(message);
    }

    if (arg.length() > (splitPos + 1)) {
      value = StringUtils.trim(arg.substring(splitPos + 1));
    } else {
      value = "";
    }
  }

  /**
   * @return the key
   */
  public String getKey() {
    return key;
  }

  /**
   * @return the value
   */
  public String getValue() {
    return value;
  }
}