package com.headwire.aemdc.replacer;

import java.io.File;
import java.io.IOException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.companion.Constants;
import com.headwire.aemdc.companion.Resource;


/**
 * Replace place holders inside of any templates.
 *
 */
public abstract class Replacer {

  private static final Logger LOG = LoggerFactory.getLogger(Replacer.class);

  protected Resource resource;

  /**
   * Replace place holders in XML file
   *
   * @param text
   *          - text to replace placeholders there
   * @param placeholders
   *          placeholders list
   * @return result text with replaced placeholders
   * @throws IOException
   *           IOException
   */
  protected abstract String replaceCustomXmlPlaceHolders(final String text, final Map<String, String> placeholders)
      throws IOException;

  /**
   * Replace place holders in all other files (html, jsp, js, css, ...)
   *
   * @param text
   *          - text to replace placeholders there
   * @param placeholders
   *          placeholders list
   * @return result text with replaced placeholders
   * @throws IOException
   *           IOException
   */
  protected abstract String replaceCustomTextPlaceHolders(final String text, final Map<String, String> placeholders)
      throws IOException;

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
   * Replace path place holders in the file path
   *
   * @param path
   *          the path where to replace placeholders
   * @return replaced path
   */
  public String replacePathPlaceHolders(final String path) {
    // {{ targetname }}
    final String result = path.replace(getPathPH(Constants.PLACEHOLDER_TARGET_NAME), getTargetLastName());
    return result;
  }

  /**
   * Replace place holders in file
   *
   * @param file
   *          - input and destination file
   * @throws IOException
   *           - IOException
   */
  public void replacePlaceHolders(final File file) throws IOException {
    try {
      String fileText = FileUtils.readFileToString(file, Constants.ENCODING);

      final String extention = FilenameUtils.getExtension(file.getName());
      final List<String> allExtList = resource.getExtentionsList();

      if (Constants.FILE_EXT_XML.equals(extention)) {
        fileText = replaceXmlPlaceHolders(fileText);
      } else if (allExtList.contains(extention)) {
        fileText = replaceTextPlaceHolders(fileText);
      }

      // replace the rest placeholders with default values
      if (allExtList.contains(extention)) {
        fileText = replacePlaceHoldersByDefault(fileText);
      }

      FileUtils.writeStringToFile(file, fileText, Constants.ENCODING);

      LOG.debug("Place holders replaced in the file [{}]", file);

    } catch (final IOException e) {
      LOG.error("Can't replace place holders in the file [{}]", file);
      throw new IOException(e);
    }
  }

  /**
   * Replace place holders in the XML text
   *
   * @param text
   *          the text
   * @return result text
   * @throws IOException
   *           - IOException
   */
  private String replaceXmlPlaceHolders(final String text) throws IOException {
    String result = text;

    // get Jcr Properties Sets
    final Map<String, Map<String, String>> jcrPropsSets = resource.getJcrProperties();
    final Iterator<Entry<String, Map<String, String>>> iter = jcrPropsSets.entrySet().iterator();

    while (iter.hasNext()) {
      final Entry<String, Map<String, String>> propsSet = iter.next();
      final String propsSetKey = propsSet.getKey();

      LOG.debug("propsSetKey={}", propsSetKey);

      if (Constants.PLACEHOLDER_PROPS_SET_COMMON.equals(propsSetKey)) {
        result = replaceCustomXmlPlaceHolders(result, propsSet.getValue());
      } else {
        result = replaceXmlPlaceHoldersSets(result, propsSetKey, propsSet.getValue());
      }
    }
    return result;
  }

  /**
   * Replace place holders in all other files (html, jsp, js, css, ...)
   *
   * @param text
   *          the text
   * @param resource
   *          the resource
   * @return result text
   * @throws IOException
   *           IOException
   */
  private String replaceTextPlaceHolders(final String text) throws IOException {
    String result = text;

    // get COMMON Properties Set
    final Map<String, Map<String, String>> jcrPropsSets = resource.getJcrProperties();
    final Map<String, String> commonProps = jcrPropsSets.get(Constants.PLACEHOLDER_PROPS_SET_COMMON);

    // replace custom placeholders
    result = replaceCustomTextPlaceHolders(result, commonProps);

    // replace all other placeholders
    final Iterator<Entry<String, String>> iter = commonProps.entrySet().iterator();
    while (iter.hasNext()) {
      final Entry<String, String> prop = iter.next();
      final String ph = getPH(prop.getKey());
      result = result.replace(ph, prop.getValue());
      LOG.debug("'{}' replacing with '{}'", ph, prop.getValue());
    }
    return result;
  }

  /**
   * Replace place holders sets in the XML text
   *
   * @param text
   *          the text
   * @param propsSetKey
   *          key of properties set
   * @param jcrProperties
   *          jcr properties
   * @return result text
   */
  private String replaceXmlPlaceHoldersSets(final String text, final String propsSetKey,
      final Map<String, String> jcrProperties) {

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

    final String result = text.replace(getPH(propsSetKey), phValue.toString());
    LOG.debug("PropsSet {} replaced by {}", propsSetKey, phValue.toString());
    return result;
  }

  /**
   * Replace all place holders with default values.
   *
   * @param text
   *          the text
   * @return result text
   */
  private String replacePlaceHoldersByDefault(final String text) {
    String result = text;

    // get all placeholders
    final List<String> restPlaceHolders = Replacer.findTextPlaceHolders(text);

    // replace all other placeholders
    final Iterator<String> iter = restPlaceHolders.iterator();
    while (iter.hasNext()) {
      final String placeHolder = iter.next();
      result = result.replace(placeHolder, "");
      LOG.debug("'{}' replacing with empty string", placeHolder);
    }
    return result;
  }

  /**
   * Get target last name w/o target sub folders.
   * For example from "page/contentpage" will be get "contentpage".
   *
   * @return target last name
   */
  protected String getTargetLastName() {
    // {{ targetname }}
    String targetName = resource.getTargetName();

    // get target name w/o target subfolders
    // ex. "page/contentpage" --> "contentpage"
    if (targetName.contains("/")) {
      targetName = StringUtils.substringAfterLast(targetName, "/");
    }
    return targetName;
  }

  /**
   * Get text in placeholder format like "{{ phName }}"
   *
   * @param phName
   *          - placeholder name
   * @return placeholder as string in placeholder format.
   */
  protected String getPH(final String phName) {
    final String result = "{{ " + phName + " }}";
    return result;
  }

  /**
   * Get text in path placeholder format like "{phName}"
   *
   * @param phName
   *          - placeholder name
   * @return placeholder as string in path placeholder format.
   */
  protected String getPathPH(final String phName) {
    final String result = "{{" + phName + "}}";
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
  /*
  public static String replaceCommonXmlPlaceHolders(final String text, final Resource resource,
      final Map<String, String> jcrProperties) throws IOException {

    // Get Config Properties from config file
    final Properties configProps = ConfigUtil.getConfigProperties();

    // jcr:tile
    String jcrTitle = jcrProperties.get(Constants.PLACEHOLDER_JCR_TITLE);
    if (StringUtils.isBlank(jcrTitle)) {
      jcrTitle = getTargetLastName(resource);
    }
    String result = text.replace(getPH(Constants.PLACEHOLDER_JCR_TITLE), getCrxXMLValue(jcrTitle));

    // jcr:description
    String jcrDescription = jcrProperties.get(Constants.PLACEHOLDER_JCR_DESCRIPTION);
    if (StringUtils.isBlank(jcrDescription)) {
      jcrDescription = getTargetLastName(resource);
    }
    result = result.replace(getPH(Constants.PLACEHOLDER_JCR_DESCRIPTION), getCrxXMLValue(jcrDescription));

    // ranking
    String ranking = jcrProperties.get(Constants.PLACEHOLDER_RANKING);
    if (StringUtils.isBlank(ranking)) {
      ranking = Constants.PH_DEFAULT_RANKING;
    }
    result = result.replace(getPH(Constants.PLACEHOLDER_RANKING), ranking);

    // allowedPaths
    String allowedPaths = jcrProperties.get(Constants.PLACEHOLDER_ALLOWED_PATHS);
    if (StringUtils.isBlank(allowedPaths)) {
      allowedPaths = Constants.PH_DEFAULT_ALLOWED_PATHS;
    }
    result = result.replace(getPH(Constants.PLACEHOLDER_ALLOWED_PATHS), getCrxXMLValue(allowedPaths));

    // componentGroup
    String componentGroup = jcrProperties.get(Constants.PLACEHOLDER_COMPONENT_GROUP);
    if (StringUtils.isBlank(componentGroup)) {
      // get target project name
      componentGroup = configProps.getProperty(Constants.CONFIGPROP_TARGET_PROJECT_NAME);
    }
    result = result.replace(getPH(Constants.PLACEHOLDER_COMPONENT_GROUP), getCrxXMLValue(componentGroup));

    // sling:resourceType
    String slingResourceType = jcrProperties.get(Constants.PLACEHOLDER_SLING_RESOURCE_TYPE);
    if (StringUtils.isBlank(slingResourceType)) {
      // get target components folder
      final String targetCompFolder = configProps.getProperty(Constants.CONFIGPROP_TARGET_COMPONENTS_FOLDER);
      // Set target project root jcr path from config file
      final String targetProjectRoot = configProps.getProperty(Constants.CONFIGPROP_TARGET_PROJECT_ROOT);
      final int pos = targetCompFolder.indexOf(targetProjectRoot);
      if (pos == -1) {
        throw new IllegalStateException("The /apps root path from " + Constants.CONFIGPROP_TARGET_COMPONENTS_FOLDER
            + " is different to " + Constants.CONFIGPROP_TARGET_PROJECT_ROOT);
      }

      // set like "/apps/my-aem-project/components/contentpage";
      final String targetUIFolder = configProps.getProperty(Constants.CONFIGPROP_TARGET_UI_FOLDER);
      slingResourceType = StringUtils.substringAfter(targetCompFolder, targetUIFolder) + "/" + resource.getTargetName();
    }
    result = result.replace(getPH(Constants.PLACEHOLDER_SLING_RESOURCE_TYPE), slingResourceType);

    // sling:resourceSuperType="/libs/wcm/foundation/components/page"
    String slingResourceSuperType = jcrProperties.get(Constants.PLACEHOLDER_SLING_RESOURCE_SUPER_TYPE);
    if (StringUtils.isBlank(slingResourceSuperType)) {
      slingResourceSuperType = Constants.PH_DEFAULT_SIGHTLY_SLING_RESOURCE_SUPER_TYPE;
    }
    result = result.replace(getPH(Constants.PLACEHOLDER_SLING_RESOURCE_SUPER_TYPE), slingResourceSuperType);

    return result;
  }
  */

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
  protected String getCrxXMLValue(final String value) {
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