package com.headwire.aemdc.replacer;

import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.headwire.aemdc.companion.Constants;
import com.headwire.aemdc.companion.Resource;
import com.headwire.aemdc.util.ConfigUtil;


/**
 * Content Page place holders replacer.
 *
 */
public class PageReplacer extends Replacer {

  private static final Logger LOG = LoggerFactory.getLogger(PageReplacer.class);

  /**
   * Constructor
   */
  public PageReplacer(final Resource resource) {
    this.resource = resource;
  }

  @Override
  protected String replaceCustomXmlPlaceHolders(final String text, final Map<String, String> placeholders) {

    // Get Config Properties from config file
    final Properties configProps = ConfigUtil.getConfigProperties();

    // jcr:title
    String jcrTitle = placeholders.get(Constants.PLACEHOLDER_JCR_TITLE);
    if (StringUtils.isBlank(jcrTitle)) {
      jcrTitle = getTargetLastName();
    }
    String result = text.replace(getPH(Constants.PLACEHOLDER_JCR_TITLE), getCrxXMLValue(jcrTitle));

    // cq:template="/conf/my-aem-project/settings/wcm/templates/page"
    String cqTemplate = placeholders.get(Constants.PLACEHOLDER_CONTENT_CQ_TEMPLATE);
    if (StringUtils.isBlank(cqTemplate)) {
      final String targetProjectName = configProps.getProperty(Constants.CONFIGPROP_TARGET_PROJECT_NAME);
      cqTemplate = Constants.PH_DEFAULT_CONTENT_CQ_TEMPLATE.replace(getPathPH(Constants.CONFIGPROP_TARGET_PROJECT_NAME),
          targetProjectName);
    }
    result = result.replace(getPH(Constants.PLACEHOLDER_CONTENT_CQ_TEMPLATE), cqTemplate);

    // sling:resourceType
    String slingResourceType = placeholders.get(Constants.PLACEHOLDER_SLING_RESOURCE_TYPE);
    if (StringUtils.isBlank(slingResourceType)) {
      slingResourceType = Constants.PH_DEFAULT_CONTENT_SLING_RESOURCE_TYPE;
    }
    result = result.replace(getPH(Constants.PLACEHOLDER_SLING_RESOURCE_TYPE), slingResourceType);

    // cq:allowedTemplates="/conf/my-aem-project/settings/wcm/templates/.*
    String cqAllowedTemplates = placeholders.get(Constants.PLACEHOLDER_CONTENT_CQ_ALLOWED_TEMPLATES);
    if (StringUtils.isBlank(cqAllowedTemplates)) {
      final String targetProjectName = configProps.getProperty(Constants.CONFIGPROP_TARGET_PROJECT_NAME);
      cqAllowedTemplates = Constants.PH_DEFAULT_CONTENT_CQ_ALLOWED_TEMPLATES.replace(
          getPathPH(Constants.CONFIGPROP_TARGET_PROJECT_NAME),
          targetProjectName);
    }
    result = result.replace(getPH(Constants.PLACEHOLDER_CONTENT_CQ_ALLOWED_TEMPLATES), cqAllowedTemplates);

    // cq:designPath="/etc/designs/my-aem-project"
    String cqDesignPath = placeholders.get(Constants.PLACEHOLDER_CONTENT_CQ_DESIGN_PATH);
    if (StringUtils.isBlank(cqDesignPath)) {
      final String targetProjectName = configProps.getProperty(Constants.CONFIGPROP_TARGET_PROJECT_NAME);
      cqDesignPath = Constants.PH_DEFAULT_CONTENT_CQ_DESIGN_PATH.replace(
          getPathPH(Constants.CONFIGPROP_TARGET_PROJECT_NAME),
          targetProjectName);
    }
    result = result.replace(getPH(Constants.PLACEHOLDER_CONTENT_CQ_DESIGN_PATH), cqDesignPath);

    return result;
  }

  @Override
  protected String replaceCustomTextPlaceHolders(final String text, final Map<String, String> placeholders) {
    return text;
  }

}