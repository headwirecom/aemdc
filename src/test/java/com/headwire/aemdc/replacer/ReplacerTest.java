package com.headwire.aemdc.replacer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


/**
 * Replacer tests
 *
 */
public class ReplacerTest {

  public static String getWithoutSpecialChars(final String text) {
    return text.replaceAll("[^a-zA-Z0-9\\.]", "");
  }

  @Test
  public void testGetWithoutSpecialChars() {
    assertEquals(Replacer.getWithoutSpecialChars("my-minus-Package.my-Class-Name"), "myminusPackage.myClassName");
    assertEquals(Replacer.getWithoutSpecialChars("my-minus-Package/my-Class-Name"), "myminusPackage/myClassName");
    assertEquals(Replacer.getWithoutSpecialChars("my-_minus-Package\\/my_-Class-Name"), "myminusPackage/myClassName");
  }

}