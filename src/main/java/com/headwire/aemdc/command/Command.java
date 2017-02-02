package com.headwire.aemdc.command;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import com.headwire.aemdc.companion.Config;
import com.headwire.aemdc.companion.Constants;
import com.headwire.aemdc.companion.Resource;
import com.headwire.aemdc.replacer.Replacer;


/**
 * Command interface for implementing concrete command
 *
 */
interface Command {

  String NAME = "SET_OWN_COMMAND_NAME";

  void execute() throws IOException;

  Resource getResource();

  default String getTargetNameAsPath(final Resource resource, final Config config) {
    String targetName = resource.getTargetName();

    final Properties dynProps = config.getDynamicProperties(resource.getType(), resource.getSourceName());

    // set target name in low case for Java subpackage name
    final String targetJavaFolder = config.getProperty(Constants.CONFIGPROP_TARGET_JAVA_FOLDER);
    final String targetTypeFolder = dynProps.getProperty(Constants.DYN_CONFIGPROP_TARGET_TYPE_FOLDER);
    if (targetTypeFolder.startsWith(targetJavaFolder)) {
      if (StringUtils.isNotBlank(targetName)) {
        targetName = targetName.toLowerCase();
      }
    }
    return targetName;
  }

  /**
   * Get target sub path.
   * For example get "/own" from target name "own/MyServlet"
   *
   * @return
   */
  default String getTargetSubPath() {
    String targetSubPath = "";
    final String targetName = Replacer.getUnixPath(getResource().getTargetName());
    if (targetName.contains("/")) {
      targetSubPath = "/" + StringUtils.substringBeforeLast(targetName, "/");
    }
    return targetSubPath;
  }

}
