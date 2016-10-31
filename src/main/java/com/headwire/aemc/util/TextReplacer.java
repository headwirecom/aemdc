package com.headwire.aemc.util;

import java.io.IOException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

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
   * Replace place holders in the text
   *
   * @param text
   *          the text
   * @param resource
   *          the resource
   * @return result text
   * @throws IOException
   *           - IOException
   */
  public static String replaceTextPlaceHolders(final String text, final Resource resource) throws IOException {
    // Jcr Properties Sets to set
    final Map<String, Map<String, String>> jcrPropsSets = resource.getJcrProperties();
    final Iterator<Entry<String, Map<String, String>>> iter = jcrPropsSets.entrySet().iterator();

    String result = text;

    while (iter.hasNext()) {
      final Entry<String, Map<String, String>> propsSet = iter.next();
      final String propsSetKey = propsSet.getKey();
      // LOG.info("propsSetKey=" + propsSetKey);
      if (Constants.PLACEHOLDERS_PROPS_SET_COMMON.equals(propsSetKey)) {
        result = replaceCommonTextPlaceHolders(result, resource, propsSet.getValue());
      } else {
        result = replaceTextPlaceHoldersSets(result, resource, propsSet.getValue(), propsSetKey);
      }
    }
    return result;
  }

  /**
   * Replace common place holders in the text
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
  public static String replaceCommonTextPlaceHolders(final String text, final Resource resource,
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
      final Properties configProps = Utils.getConfigProperties();

      // get target components folder
      final String targetCompFolder = configProps.getProperty(Constants.CONFIGPROP_TARGET_COMPONENTS_FOLDER);
      final int pos = targetCompFolder.indexOf(resource.getTargetProjectJcrPath());
      if (pos == -1) {
        throw new IllegalStateException("The root path of " + Constants.CONFIGPROP_TARGET_COMPONENTS_FOLDER
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
   * Replace place holders sets in the text
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
   * @throws IOException
   *           - IOException
   */
  public static String replaceTextPlaceHoldersSets(final String text, final Resource resource,
      final Map<String, String> jcrProperties, final String propsSetKey) throws IOException {

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
      // LOG.info("key=" + key + ", value=" + value);
    }

    // LOG.info("phValue=" + phValue.toString());
    final String result = text.replace("{{ " + propsSetKey + " }}", phValue.toString());
    return result;
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