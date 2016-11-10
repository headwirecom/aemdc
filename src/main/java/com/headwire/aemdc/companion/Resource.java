package com.headwire.aemdc.companion;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * Resource object
 *
 */
public class Resource {

  private String type;
  private String sourceName;
  private String targetName;
  private String sourceFolderPath;
  private String targetFolderPath;
  private String targetProjectJcrPath;
  private String[] extentions;
  private boolean toDeleteDestDir;
  private boolean toWarnDestDir;
  private String javaClassName;
  private String javaClassPackage;
  private Map<String, Map<String, String>> jcrProperties;

  /**
   * Constructor
   */
  public Resource() {
  }

  /**
   * @return the type
   */
  public String getType() {
    return type;
  }

  /**
   * @param type
   *          the type to set
   */
  public void setType(final String type) {
    this.type = type;
  }

  /**
   * @return the sourceName
   */
  public String getSourceName() {
    return sourceName;
  }

  /**
   * @param sourceName
   *          the sourceName to set
   */
  public void setSourceName(final String sourceName) {
    this.sourceName = sourceName;
  }

  /**
   * @return the targetName
   */
  public String getTargetName() {
    return targetName;
  }

  /**
   * @param targetName
   *          the targetName to set
   */
  public void setTargetName(final String targetName) {
    this.targetName = targetName;
  }

  /**
   * @return the sourceFolderPath
   */
  public String getSourceFolderPath() {
    return sourceFolderPath;
  }

  /**
   * @param sourceFolderPath
   *          the sourceFolderPath to set
   */
  public void setSourceFolderPath(final String sourceFolderPath) {
    this.sourceFolderPath = sourceFolderPath;
  }

  /**
   * @return the targetFolderPath
   */
  public String getTargetFolderPath() {
    return targetFolderPath;
  }

  /**
   * @param targetFolderPath
   *          the targetFolderPath to set
   */
  public void setTargetFolderPath(final String targetFolderPath) {
    this.targetFolderPath = targetFolderPath;
  }

  /**
   * @return the targetProjectJcrPath
   */
  public String getTargetProjectJcrPath() {
    return targetProjectJcrPath;
  }

  /**
   * @param targetProjectJcrPath
   *          the targetProjectJcrPath to set
   */
  public void setTargetProjectJcrPath(final String targetProjectJcrPath) {
    this.targetProjectJcrPath = targetProjectJcrPath;
  }

  /**
   * @return the extentions
   */
  public String[] getExtentions() {
    return extentions;
  }

  /**
   * @return the extentions as list
   */
  public List<String> getExtentionsList() {
    final List<String> extAsList = Arrays.asList(extentions);
    return extAsList;
  }

  /**
   * @param extentions
   *          the extentions to set
   */
  public void setExtentions(final String[] extentions) {
    this.extentions = extentions;
  }

  /**
   * @return the toDeleteDestDir
   */
  public boolean isToDeleteDestDir() {
    return toDeleteDestDir;
  }

  /**
   * @param toDeleteDestDir
   *          the toDeleteDestDir to set
   */
  public void setToDeleteDestDir(final boolean toDeleteDestDir) {
    this.toDeleteDestDir = toDeleteDestDir;
  }

  /**
   * @return the toWarnDestDir
   */
  public boolean isToWarnDestDir() {
    return toWarnDestDir;
  }

  /**
   * @param toWarnDestDir
   *          the toWarnDestDir to set
   */
  public void setToWarnDestDir(final boolean toWarnDestDir) {
    this.toWarnDestDir = toWarnDestDir;
  }

  /**
   * @return the jcrProperties
   */
  public Map<String, Map<String, String>> getJcrProperties() {
    return jcrProperties;
  }

  /**
   * @param key
   *          properties set key
   * @return the jcrPropertiesSet by key
   */
  public Map<String, String> getJcrPropsSet(final String key) {
    return jcrProperties.get(key);
  }

  /**
   * @param jcrProperties
   *          the jcrProperties to set
   */
  public void setJcrProperties(final Map<String, Map<String, String>> jcrProperties) {
    this.jcrProperties = jcrProperties;
  }

  /**
   * @return the javaClassName
   */
  public String getJavaClassName() {
    return javaClassName;
  }

  /**
   * @param javaClassName
   *          the javaClassName to set
   */
  public void setJavaClassName(final String javaClassName) {
    this.javaClassName = javaClassName;
  }

  /**
   * @return the javaClassPackage
   */
  public String getJavaClassPackage() {
    return javaClassPackage;
  }

  /**
   * @param javaClassPackage
   *          the javaClassPackage to set
   */
  public void setJavaClassPackage(final String javaClassPackage) {
    this.javaClassPackage = javaClassPackage;
  }

}
