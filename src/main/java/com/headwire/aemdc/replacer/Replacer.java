package com.headwire.aemdc.replacer;

import java.io.File;
import java.io.IOException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.companion.Config;
import com.headwire.aemdc.companion.Constants;
import com.headwire.aemdc.companion.Resource;


/**
 * Replace place holders inside of any templates.
 *
 */
public abstract class Replacer {

  private static final Logger LOG = LoggerFactory.getLogger(Replacer.class);

  protected Config config;
  protected Resource resource;

  /**
   * Replace place holders in XML file
   *
   * @param text
   *          - text to replace placeholders there
   * @param placeholders
   *          placeholders list
   * @return result text with replaced placeholders
   */
  protected abstract String replaceCustomXmlPlaceHolders(final String text, final Map<String, String> placeholders);

  /**
   * Replace place holders in all other files (html, jsp, js, css, ...)
   *
   * @param text
   *          - text to replace placeholders there
   * @param placeholders
   *          placeholders list
   * @return result text with replaced placeholders
   */
  protected abstract String replaceCustomTextPlaceHolders(final String text, final Map<String, String> placeholders);

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
    String result = path;

    // {{targetname}}
    result = result.replace(getPathPH(Constants.PLACEHOLDER_TARGET_NAME), getTargetLastName());

    // {{java-class}}
    result = result.replace(getPathPH(Constants.PLACEHOLDER_JAVA_CLASS), getTargetJavaClassName());

    // {{runmode}}
    result = result.replace(getPathPH(Constants.PLACEHOLDER_RUNMODE), getRunmodeSuffix());

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
   */
  private String replaceXmlPlaceHolders(final String text) {
    String result = text;

    // get Jcr Properties Sets
    final Map<String, Map<String, String>> jcrPropsSets = resource.getJcrProperties();

    for (final Map.Entry<String, Map<String, String>> entry : jcrPropsSets.entrySet()) {
      final String propsSetKey = entry.getKey();
      final Map<String, String> propsSet = entry.getValue();
      LOG.debug("propsSetKey={}", propsSetKey);

      if (Constants.PLACEHOLDER_PROPS_SET_COMMON.equals(propsSetKey)) {
        // replace all placeholders defined by argument params at first
        for (final Map.Entry<String, String> prop : propsSet.entrySet()) {
          final String ph = getPH(prop.getKey());
          final String phValue = getCrxXMLValue(prop.getValue());
          result = result.replace(ph, phValue);
          LOG.debug("'{}' replacing with '{}'", ph, phValue);
        }

        // replace all other custom placeholders
        result = replaceCustomXmlPlaceHolders(result, propsSet);

      } else {
        result = replaceXmlPlaceHoldersSets(result, propsSetKey, propsSet);
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
   */
  private String replaceTextPlaceHolders(final String text) {
    String result = text;

    // get COMMON Properties Set
    final Map<String, Map<String, String>> jcrPropsSets = resource.getJcrProperties();
    final Map<String, String> commonProps = jcrPropsSets.get(Constants.PLACEHOLDER_PROPS_SET_COMMON);

    // replace all placeholders defined by argument params at first
    for (final Map.Entry<String, String> prop : commonProps.entrySet()) {
      final String ph = getPH(prop.getKey());
      result = result.replace(ph, prop.getValue());
      LOG.debug("'{}' replacing with '{}'", ph, prop.getValue());
    }

    // replace all other custom placeholders
    result = replaceCustomTextPlaceHolders(result, commonProps);

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

    // get second number from ph key "ph_<name>_XXX"
    final int pos = propsSetKey.lastIndexOf("_");

    // offset = number * 4 blanks
    final int offset = Integer.valueOf(propsSetKey.substring(pos + 1)) * 4;

    boolean first = true;
    for (final Map.Entry<String, String> entry : jcrProperties.entrySet()) {
      if (!first) {
        phValue.append("\n");
        for (int i = 0; i < offset; i++) {
          phValue.append(" ");
        }
      }
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
    // ex. "page/comppage" --> "comppage"
    if (StringUtils.isNotBlank(targetName) && targetName.contains("/")) {
      targetName = StringUtils.substringAfterLast(targetName, "/");
    }

    // to avoid NullPointerException
    if (StringUtils.isBlank(targetName)) {
      targetName = "";
    }

    return targetName;
  }

  /**
   * Get runmode suffix to add to the target path
   * like ".&lt;runmode&gt;"
   *
   * @return runmode suffix
   */
  protected String getRunmodeSuffix() {
    String runmode = "";
    final Map<String, String> commonJcrProps = resource.getJcrPropsSet(Constants.PLACEHOLDER_PROPS_SET_COMMON);
    if (commonJcrProps != null) {
      runmode = commonJcrProps.get(Constants.PLACEHOLDER_RUNMODE);
      if (StringUtils.isNotBlank(runmode)) {
        // add ".<runmode>" to the target path
        runmode = "." + runmode;
      } else {
        runmode = "";
      }
    }
    return runmode;
  }

  /**
   * Get target component model name incl. target subpackage.
   * For example "page/hero" will be "page.Hero".
   *
   * @return target component model name
   */
  protected String getTargetCompModelName() {
    String compModel = "";

    // like "page/HeRo"
    final String targetName = resource.getTargetName();

    if (StringUtils.isNotBlank(targetName)) {
      // get Java Class Name from arguments
      String javaClassName = "";
      final Map<String, String> commonJcrProps = resource.getJcrPropsSet(Constants.PLACEHOLDER_PROPS_SET_COMMON);
      if (commonJcrProps != null) {
        // {{ java-class }}
        javaClassName = commonJcrProps.get(Constants.PLACEHOLDER_JAVA_CLASS);
      }

      // generate Java Class Name from targetName
      // "page/HeRo" to "Hero"
      if (StringUtils.isBlank(javaClassName)) {
        javaClassName = FilenameUtils.getBaseName(targetName);
        javaClassName = WordUtils.capitalize(javaClassName);
      }

      // "page/HeRo" to "page.hero"
      compModel = StringUtils.replace(targetName, "/", ".").toLowerCase();

      // to "page.hero.Hero"
      compModel += "." + javaClassName;
    }
    return compModel;
  }

  /**
   * Get target java class name
   *
   * @return name of java class
   */
  protected String getTargetJavaClassName() {
    String javaClassName = "";
    final Map<String, String> commonJcrProps = resource.getJcrPropsSet(Constants.PLACEHOLDER_PROPS_SET_COMMON);
    if (commonJcrProps != null) {
      // {{ java-class }}
      javaClassName = commonJcrProps.get(Constants.PLACEHOLDER_JAVA_CLASS);
      if (StringUtils.isBlank(javaClassName)) {
        javaClassName = getTargetCompModelName();
        javaClassName = StringUtils.substringAfterLast(javaClassName, ".");
      }
    }
    return javaClassName;
  }

  /**
   * Get target java package
   *
   * @return java package
   */
  protected String getTargetJavaPackage() {
    // {{ java-package }}
    final String targetPath = resource.getTargetFolderPath() + "/" + resource.getTargetName();
    final String targetJavaSrcFolder = config.getProperty(Constants.CONFIGPROP_TARGET_JAVA_FOLDER);

    // cut java file name, replace "/" with "."
    String javaPackage = StringUtils.substringAfter(targetPath, targetJavaSrcFolder + "/");
    javaPackage = StringUtils.replace(javaPackage, "/", ".");
    javaPackage = javaPackage.toLowerCase();

    return javaPackage;
  }

  /**
   * Get text in placeholder format like "{{ phName }}"
   *
   * @param phName
   *          - placeholder name
   * @return placeholder as string in placeholder format.
   */
  public String getPH(final String phName) {
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
  public String getPathPH(final String phName) {
    final String result = "{{" + phName + "}}";
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