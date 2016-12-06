package com.headwire.aemdc.replacer;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.companion.Constants;
import com.headwire.aemdc.companion.Resource;
import com.headwire.aemdc.util.ConfigUtil;


/**
 * Template place holders replacer.
 *
 */
public class TemplateReplacer extends Replacer {

  private static final Logger LOG = LoggerFactory.getLogger(TemplateReplacer.class);

  /**
   * Constructor
   */
  public TemplateReplacer(final Resource resource) {
    this.resource = resource;
  }

  @Override
  protected String replaceCustomXmlPlaceHolders(final String text, final Map<String, String> placeholders)
      throws IOException {

    // Get Config Properties from config file
    final Properties configProps = ConfigUtil.getConfigProperties();

    // jcr:title
    String jcrTitle = placeholders.get(Constants.PLACEHOLDER_JCR_TITLE);
    if (StringUtils.isBlank(jcrTitle)) {
      jcrTitle = getTargetLastName();
    }
    String result = text.replace(getPH(Constants.PLACEHOLDER_JCR_TITLE), getCrxXMLValue(jcrTitle));

    // jcr:description
    String jcrDescription = placeholders.get(Constants.PLACEHOLDER_JCR_DESCRIPTION);
    if (StringUtils.isBlank(jcrDescription)) {
      jcrDescription = getTargetLastName();
    }
    result = result.replace(getPH(Constants.PLACEHOLDER_JCR_DESCRIPTION), getCrxXMLValue(jcrDescription));

    // ranking
    String ranking = placeholders.get(Constants.PLACEHOLDER_RANKING);
    if (StringUtils.isBlank(ranking)) {
      ranking = Constants.PH_DEFAULT_RANKING;
    }
    result = result.replace(getPH(Constants.PLACEHOLDER_RANKING), ranking);

    // allowedPaths
    String allowedPaths = placeholders.get(Constants.PLACEHOLDER_ALLOWED_PATHS);
    if (StringUtils.isBlank(allowedPaths)) {
      allowedPaths = Constants.PH_DEFAULT_ALLOWED_PATHS;
    }
    result = result.replace(getPH(Constants.PLACEHOLDER_ALLOWED_PATHS), getCrxXMLValue(allowedPaths));

    // sling:resourceType
    String slingResourceType = placeholders.get(Constants.PLACEHOLDER_SLING_RESOURCE_TYPE);
    if (StringUtils.isBlank(slingResourceType)) {
      // get target components folder
      final String targetCompFolder = configProps.getProperty(Constants.CONFIGPROP_TARGET_COMPONENTS_FOLDER);

      // set like "/apps/my-aem-project/components/contentpage";
      final String targetUIFolder = configProps.getProperty(Constants.CONFIGPROP_TARGET_UI_FOLDER);
      slingResourceType = StringUtils.substringAfter(targetCompFolder, targetUIFolder) + "/" + resource.getTargetName();
    }
    result = result.replace(getPH(Constants.PLACEHOLDER_SLING_RESOURCE_TYPE), slingResourceType);

    return result;
  }

  @Override
  protected String replaceCustomTextPlaceHolders(final String text, final Map<String, String> placeholders)
      throws IOException {
    return text;
  }

}