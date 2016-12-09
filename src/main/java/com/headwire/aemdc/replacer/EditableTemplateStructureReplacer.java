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
 * Editable Template Structure place holders replacer.
 *
 */
public class EditableTemplateStructureReplacer extends Replacer {

  private static final Logger LOG = LoggerFactory.getLogger(EditableTemplateStructureReplacer.class);

  /**
   * Constructor
   */
  public EditableTemplateStructureReplacer(final Resource resource) {
    this.resource = resource;
  }

  @Override
  protected String replaceCustomXmlPlaceHolders(final String text, final Map<String, String> placeholders) {

    String result = text;

    // Get Config Properties from config file
    final Properties configProps = ConfigUtil.getConfigProperties();

    // jcr:title
    String jcrTitle = placeholders.get(Constants.PLACEHOLDER_JCR_TITLE);
    if (StringUtils.isBlank(jcrTitle)) {
      jcrTitle = getTargetLastName();
    }
    result = result.replace(getPH(Constants.PLACEHOLDER_JCR_TITLE), getCrxXMLValue(jcrTitle));

    // ranking
    String ranking = placeholders.get(Constants.PLACEHOLDER_RANKING);
    if (StringUtils.isBlank(ranking)) {
      ranking = Constants.PH_DEFAULT_RANKING;
    }
    result = result.replace(getPH(Constants.PLACEHOLDER_RANKING), ranking);

    // componentGroup
    String componentGroup = placeholders.get(Constants.PLACEHOLDER_COMPONENT_GROUP);
    if (StringUtils.isBlank(componentGroup)) {
      // get target project name
      componentGroup = configProps.getProperty(Constants.CONFIGPROP_TARGET_PROJECT_NAME);
    }
    result = result.replace(getPH(Constants.PLACEHOLDER_COMPONENT_GROUP), getCrxXMLValue(componentGroup));

    // template-type aemdc-page-title
    String templTypeJcrTitle = placeholders.get(Constants.PLACEHOLDER_TEMPL_TYPE_JCR_TITLE);
    if (StringUtils.isBlank(templTypeJcrTitle)) {
      templTypeJcrTitle = Constants.PH_DEFAULT_TEMPL_TYPE_JCR_TITLE;
    }
    result = result.replace(getPH(Constants.PLACEHOLDER_TEMPL_TYPE_JCR_TITLE), getCrxXMLValue(templTypeJcrTitle));

    // template-type aemdc-page-description
    String templTypeJcrDesc = placeholders.get(Constants.PLACEHOLDER_TEMPL_TYPE_JCR_DESCRIPTION);
    if (StringUtils.isBlank(templTypeJcrDesc)) {
      templTypeJcrDesc = Constants.PH_DEFAULT_TEMPL_TYPE_JCR_TITLE;
    }
    result = result.replace(getPH(Constants.PLACEHOLDER_TEMPL_TYPE_JCR_DESCRIPTION), getCrxXMLValue(templTypeJcrDesc));

    // sling:resourceType="wcm/foundation/components/page"
    String slingResourceType = placeholders.get(Constants.PLACEHOLDER_TEMPL_TYPE_SLING_RESOURCE_TYPE);
    if (StringUtils.isBlank(slingResourceType)) {
      slingResourceType = Constants.PH_DEFAULT_TEMPL_TYPE_SLING_RESOURCE_TYPE;
    }
    result = result.replace(getPH(Constants.PLACEHOLDER_TEMPL_TYPE_SLING_RESOURCE_TYPE), slingResourceType);

    return result;
  }

  @Override
  protected String replaceCustomTextPlaceHolders(final String text, final Map<String, String> placeholders) {
    return text;
  }

}