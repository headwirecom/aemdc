package com.headwire.aemc.util;

import java.io.IOException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemc.companion.Constants;
import com.headwire.aemc.companion.Resource;


/**
 * Replace place holders inside of templates.
 *
 */
public class TextReplacer {

  private static final Logger LOG = LoggerFactory.getLogger(TextReplacer.class);

  /**
   * Constructor
   */
  private TextReplacer() {
  }

  /**
   * Replace place holders in the java files
   *
   * @param text
   *          the text
   * @param resource
   *          the resource
   * @return result text
   */
  public static String replaceJavaPlaceHolders(final String text, final Resource resource) {
    String result = text;

    // {{ java-class }}
    final String javaClassName = resource.getJavaClassName();
    result = result.replace(Constants.PLACEHOLDER_JAVA_CLASS, javaClassName);

    // {{ java-package }}
    final String javaPackage = resource.getJavaClassPackage();
    result = result.replace(Constants.PLACEHOLDER_JAVA_PACKAGE, javaPackage);

    // all other placeholders
    result = replaceTextPlaceHolders(result, resource);

    return result;
  }

  /**
   * Replace place holders in all other files (jsp, js, css)
   *
   * @param text
   *          the text
   * @param resource
   *          the resource
   * @return result text
   */
  public static String replaceTextPlaceHolders(final String text, final Resource resource) {
    String result = text;

    // get Jcr Properties Sets
    final Map<String, Map<String, String>> jcrPropsSets = resource.getJcrProperties();

    // get COMMON Properties Set
    final Map<String, String> commonProps = jcrPropsSets.get(Constants.PLACEHOLDERS_PROPS_SET_COMMON);
    final Iterator<Entry<String, String>> iter = commonProps.entrySet().iterator();
    while (iter.hasNext()) {
      // replace all other placeholders
      final Entry<String, String> prop = iter.next();
      result = result.replace("{{ " + prop.getKey() + " }}", prop.getValue());
      LOG.debug("'{{ {} }}' replacing with '{}'", prop.getKey(), prop.getValue());
    }
    return result;
  }

  /**
   * Replace place holders in the XML text
   *
   * @param text
   *          the text
   * @param resource
   *          the resource
   * @return result text
   * @throws IOException
   *           - IOException
   */
  public static String replaceXmlPlaceHolders(final String text, final Resource resource) throws IOException {
    String result = text;

    // get Jcr Properties Sets
    final Map<String, Map<String, String>> jcrPropsSets = resource.getJcrProperties();
    final Iterator<Entry<String, Map<String, String>>> iter = jcrPropsSets.entrySet().iterator();

    while (iter.hasNext()) {
      final Entry<String, Map<String, String>> propsSet = iter.next();
      final String propsSetKey = propsSet.getKey();

      LOG.debug("propsSetKey={}", propsSetKey);

      if (Constants.PLACEHOLDERS_PROPS_SET_COMMON.equals(propsSetKey)) {
        result = replaceCommonXmlPlaceHolders(result, resource, propsSet.getValue());
      } else {
        result = replaceXmlPlaceHoldersSets(result, resource, propsSet.getValue(), propsSetKey);
      }
    }
    return result;
  }

  /**
   * Replace common place holders in the XML text
   *
   * @param text
   *          the text
   * @param resource
   *          the resource
   * @param jcrProperties
   *          jcr properties
   * @return result text
   * @throws IOException
   *           - IOException
   */
  public static String replaceCommonXmlPlaceHolders(final String text, final Resource resource,
      final Map<String, String> jcrProperties) throws IOException {
    // jcr:tile
    String jcrTitle = jcrProperties.get(Constants.PARAM_PROP_JCR_TITLE);
    if (StringUtils.isBlank(jcrTitle)) {
      jcrTitle = resource.getTargetName();
    }
    String result = text.replace("{{ jcr:title }}", getCrxXMLValue(jcrTitle));

    // jcr:description
    String jcrDescription = jcrProperties.get(Constants.PARAM_PROP_JCR_DESCRIPTION);
    if (StringUtils.isBlank(jcrDescription)) {
      jcrDescription = resource.getTargetName() + " description";
    }
    result = result.replace("{{ jcr:description }}", getCrxXMLValue(jcrDescription));

    // ranking
    String ranking = jcrProperties.get(Constants.PARAM_PROP_RANKING);
    if (StringUtils.isBlank(ranking)) {
      ranking = Constants.DEFAULT_RANKING;
    }
    result = result.replace("{{ ranking }}", getCrxXMLValue(ranking));

    // sling:resourceType
    String slingResourceType = jcrProperties.get(Constants.PARAM_PROP_SLING_RESOURCE_TYPE);
    if (StringUtils.isBlank(slingResourceType)) {
      // Get Config Properties from config file
      final Properties configProps = ConfigUtil.getConfigProperties();

      // get target components folder
      final String targetCompFolder = configProps.getProperty(Constants.CONFIGPROP_TARGET_COMPONENTS_FOLDER);
      final int pos = targetCompFolder.indexOf(resource.getTargetProjectJcrPath());
      if (pos == -1) {
        throw new IllegalStateException("The /apps root path from " + Constants.CONFIGPROP_TARGET_COMPONENTS_FOLDER
            + " is different to " + Constants.CONFIGPROP_TARGET_PROJECT_JCR_PATH);
      }

      // set like "/apps/my-aem-project/components/contentpage";
      slingResourceType = targetCompFolder.substring(pos) + "/" + resource.getTargetName();
    }
    result = result.replace("{{ sling:resourceType }}", getCrxXMLValue(slingResourceType));

    // sling:resourceSuperType="foundation/components/page"
    String slingResourceSuperType = jcrProperties.get(Constants.PARAM_PROP_SLING_RESOURCE_SUPER_TYPE);
    if (StringUtils.isBlank(slingResourceSuperType)) {
      slingResourceSuperType = Constants.DEFAULT_SLING_RESOURCE_SUPER_TYPE;
    }
    result = result.replace("{{ sling:resourceSuperType }}", getCrxXMLValue(slingResourceSuperType));

    return result;
  }

  /**
   * Replace all place holders with default values.
   *
   * @param text
   *          the text
   * @return result text
   */
  public static String replacePlaceHoldersByDefault(final String text) {
    String result = text;

    // get all placeholders
    final List<String> restPlaceHolders = TextReplacer.findTextPlaceHolders(text);

    // replace all other placeholders
    final Iterator<String> iter = restPlaceHolders.iterator();
    while (iter.hasNext()) {
      final String placeHolder = iter.next();
      result = result.replace(placeHolder, "");
      LOG.debug("'{}' replacing with ''", placeHolder);
    }
    return result;
  }

  /**
   * Replace place holders sets in the XML text
   *
   * @param text
   *          the text
   * @param resource
   *          the resource
   * @param jcrProperties
   *          jcr properties
   * @param propsSetKey
   *          key value of properties set
   * @return result text
   */
  public static String replaceXmlPlaceHoldersSets(final String text, final Resource resource,
      final Map<String, String> jcrProperties, final String propsSetKey) {

    final StringBuilder phValue = new StringBuilder();

    // get second number from ph key "ph_1_XXX"
    final int pos = propsSetKey.lastIndexOf("_");

    // offset = number * 4 blanks
    final int offset = Integer.valueOf(propsSetKey.substring(pos + 1)) * 4;

    final Iterator<Entry<String, String>> iter = jcrProperties.entrySet().iterator();
    boolean first = true;
    while (iter.hasNext()) {
      if (!first) {
        phValue.append("\n");
        for (int i = 0; i < offset; i++) {
          phValue.append(" ");
        }
      }
      final Entry<String, String> entry = iter.next();
      final String key = entry.getKey();
      final String value = entry.getValue();
      phValue.append(key);
      phValue.append("=\"");
      phValue.append(getCrxXMLValue(value));
      phValue.append("\"");
      first = false;
    }

    final String result = text.replace("{{ " + propsSetKey + " }}", phValue.toString());
    LOG.debug("PropsSet {} replaced by {}", propsSetKey, phValue.toString());
    return result;
  }

  /**
   * Find place holders in text
   *
   * @param text
   *          - text to find placeholders there
   * @return list of place holders
   */
  public static List<String> findTextPlaceHolders(final String text) {
    final List<String> phList = new ArrayList<String>();
    final Pattern pattern = Pattern.compile("\\{\\{ (.*) \\}\\}");
    final Matcher matcher = pattern.matcher(text);
    // find placeholders
    while (matcher.find()) {
      phList.add(matcher.group());
    }
    return phList;
  }

  /**
   * Escape characters for text appearing as CRX XML Parameter value.
   *
   * <P>The following characters are replaced with corresponding character entities :
   * <table border='1' cellpadding='3' cellspacing='0'>
   * <tr><th> Character </th><th> Replacer </th></tr>
   * <tr><td> " </td><td> &quot; </td></tr>
   * <tr><td> < </td><td> &lt; </td></tr>
   * <tr><td> > </td><td> &gt; </td></tr>
   * <tr><td> & </td><td> &amp; </td></tr>
   * <tr><td> "," </td><td> &quot;\,&quot; </td></tr>
   * </table>
   *
   */
  private static String getCrxXMLValue(final String value) {
    String resultValue = StringUtils.trimToEmpty(value);

    // escape special characters
    if (StringUtils.isNotBlank(resultValue)) {
      final StringBuilder resultBuilder = new StringBuilder();
      final StringCharacterIterator iterator = new StringCharacterIterator(resultValue);
      char character = iterator.current();
      while (character != CharacterIterator.DONE) {
        if (character == '\"') {
          resultBuilder.append("&quot;");
        } else if (character == '<') {
          resultBuilder.append("&lt;");
        } else if (character == '>') {
          resultBuilder.append("&gt;");
        } else if (character == '&') {
          resultBuilder.append("&amp;");
        } else if (character == '\\') {
          resultBuilder.append("\\\\");
        } else {
          // the char is not a special one
          // add it to the result as is
          resultBuilder.append(character);
        }
        character = iterator.next();
      }

      // special case for parameters pairs
      // {"value":"key1","text":"key1value"}
      resultValue = StringUtils.replace(resultBuilder.toString(), "&quot;,&quot;", "&quot;\\,&quot;");
    }
    return resultValue;
  }
}