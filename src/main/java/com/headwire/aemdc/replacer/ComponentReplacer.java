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
 * Component place holders replacer.
 *
 */
public class ComponentReplacer extends Replacer {

  private static final Logger LOG = LoggerFactory.getLogger(ComponentReplacer.class);

  /**
   * Constructor
   */
  public ComponentReplacer(final Resource resource) {
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

    // jcr:description
    String jcrDescription = placeholders.get(Constants.PLACEHOLDER_JCR_DESCRIPTION);
    if (StringUtils.isBlank(jcrDescription)) {
      jcrDescription = getTargetLastName();
    }
    result = result.replace(getPH(Constants.PLACEHOLDER_JCR_DESCRIPTION), getCrxXMLValue(jcrDescription));

    // componentGroup
    String componentGroup = placeholders.get(Constants.PLACEHOLDER_COMPONENT_GROUP);
    if (StringUtils.isBlank(componentGroup)) {
      // get target project name
      componentGroup = configProps.getProperty(Constants.CONFIGPROP_TARGET_PROJECT_NAME);
    }
    result = result.replace(getPH(Constants.PLACEHOLDER_COMPONENT_GROUP), getCrxXMLValue(componentGroup));

    // sling:resourceSuperType="/libs/wcm/foundation/components/page"
    String slingResourceSuperType = placeholders.get(Constants.PLACEHOLDER_SLING_RESOURCE_SUPER_TYPE);
    if (StringUtils.isBlank(slingResourceSuperType)) {
      slingResourceSuperType = Constants.PH_DEFAULT_COMP_SLING_RESOURCE_SUPER_TYPE;
    }
    result = result.replace(getPH(Constants.PLACEHOLDER_SLING_RESOURCE_SUPER_TYPE), slingResourceSuperType);

    return result;
  }

  @Override
  protected String replaceCustomTextPlaceHolders(final String text, final Map<String, String> placeholders) {
    String result = text;

    // {{ targetname }}
    result = result.replace(getPH(Constants.PLACEHOLDER_TARGET_NAME), getTargetLastName());

    // {{ comp-model }}
    String compModel = placeholders.get(Constants.PLACEHOLDER_COMP_MODEL);
    if (StringUtils.isBlank(compModel)) {
      compModel = Constants.PH_DEFAULT_COMP_MODEL;
    }
    result = result.replace(getPH(Constants.PLACEHOLDER_COMP_MODEL), compModel);

    return result;
  }

}