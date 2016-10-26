package com.headwire.aemc.companion;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.headwire.aemc.menu.BasisRunner;
import com.headwire.aemc.menu.ComponentRunner;
import com.headwire.aemc.menu.OsgiRunner;
import com.headwire.aemc.menu.TemplateRunner;


/**
 * Runnable Companion Main Class
 *
 * @author Marat Saitov, 25.10.2016
 */
public class RunnableCompanion {

  public static void main(final String[] args) throws InterruptedException {
    System.out.println("Project name under /apps: " + args[0]);
    System.out.println("Type: " + args[1]);
    System.out.println("Template/Component Name : " + args[2]);
    System.out.println("jcr:title : " + args[3]);
    // System.out.println("ResourceType : " + args[3]);
    // System.out.println("Java Package : " + args[4]);

    final Map<String, String> params = new HashMap<String, String>();

    if (StringUtils.isNotBlank(args[0])) {
      params.put(Constants.PARAM_PROJECT_NAME, args[0]);
    }
    if (StringUtils.isNotBlank(args[1])) {
      params.put(Constants.PARAM_TYPE, args[1]);
    }
    if (StringUtils.isNotBlank(args[2])) {
      params.put(Constants.PARAM_TEMPLATE_NAME, args[2]);
    }
    if (StringUtils.isNotBlank(args[3])) {
      params.put(Constants.PARAM_PROP_JCR_TITLE, args[3]);
    }

    BasisRunner runner;
    switch (params.get(Constants.PARAM_TYPE)) {
      case Constants.TYPE_TEMPLATE:
        runner = new TemplateRunner(params);
        break;
      case Constants.TYPE_COMPONENT:
        runner = new ComponentRunner(params);
        break;
      case Constants.TYPE_OSGI:
        runner = new OsgiRunner(params);
        break;
      default:
        runner = new TemplateRunner(params);
        break;
    }

    // create structure
    runner.run();
  }
}