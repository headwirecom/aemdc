package com.headwire.aemc.companion;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.headwire.aemc.menu.BasisRunner;
import com.headwire.aemc.menu.TemplateRunner;

/**
 * Runnable Companion Main Class
 *
 * @author Marat Saitov, 25.10.2016
 */
public class RunnableCompanion {

	// private static final Logger LOG =
	// LoggerFactory.getLogger(RunnableCompanion.class);

	public static void main(String[] args) throws InterruptedException {
		System.out.println("Type: " + args[0]);
		System.out.println("Folder name under /apps: " + args[1]);
		// System.out.println("Template/Component Name : " + args[2]);
		// System.out.println("ResourceType : " + args[3]);
		// System.out.println("Java Package : " + args[4]);

		Map<String, String> params = new HashMap<String, String>();

		if (StringUtils.isNotBlank(args[0])) {
			params.put(Constants.PARAM_TYPE, args[0]);
		}
		if (StringUtils.isNotBlank(args[1])) {
			params.put(Constants.PARAM_APPS_FOLDER_NAME, args[1]);
		}

		BasisRunner runner = new TemplateRunner(params);

		if (Constants.TYPE_COMPONENT.equals(params.get(Constants.PARAM_TYPE))) {
			runner = new TemplateRunner(params);
		}

		runner.run();
	}
}