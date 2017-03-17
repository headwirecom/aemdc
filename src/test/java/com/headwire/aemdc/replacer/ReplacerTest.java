package com.headwire.aemdc.replacer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


/**
 * Replacer tests
 *
 */
public class ReplacerTest {

  @Test
  public void testGetWithoutSpecialChars() {
    assertEquals(Replacer.getWithoutSpecialChars("my-minus-Package.my-Class-Name"), "myminusPackage.myClassName");
    assertEquals(Replacer.getWithoutSpecialChars("my-minus-Package/my-Class-Name"), "myminusPackage/myClassName");
    assertEquals(Replacer.getWithoutSpecialChars("my-_minus-Package\\/my_-Class-Name"), "myminusPackage/myClassName");
  }

}