package com.headwire.aemdc.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;

import com.headwire.aemdc.companion.Config;
import com.headwire.aemdc.companion.Reflection;
import com.headwire.aemdc.companion.Resource;
import com.headwire.aemdc.runner.BasisRunner;
import com.headwire.aemdc.runner.HelpRunner;
import com.headwire.aemdc.util.Help;


/**
 * Created by rr on 12/29/2016.
 */
public class Model {

  private transient Config config = new Config();

  // setup of types that will be available
  private ArrayList<String> types = new ArrayList<String>();

  public Model() {
  }

  @ElementMap(entry = "config", key = "type", attribute = true, inline = true)
  private HashMap<String, TypeRoot> values = new HashMap<>();

  public List<String> getTypes() {

    return new ArrayList<String>(config.getDynamicTypes());
  }

  public List<String> getTemplatesForType(final String type) {
    return new ArrayList<String>(config.getTemplateNames(type));
  }

  public List<String> getPlaceHoldersForName(final String type, final String template) {
    final Resource resource = new Resource(new String[] { type, template });
    final BasisRunner helpRunner = new HelpRunner(resource, config);

    final Reflection reflection = new Reflection(config);
    final BasisRunner runner = reflection.getRunner(resource);

    final Help helper = new Help(config);

    final String name = resource.getSourceName();
    final String templateSrcPath = runner.getSourceFolder() + "/" + name;

    return (helper.getPlaceHoldersAsList(new File(templateSrcPath)));

  }

  public String getValue(final String type, final String template, final String placeholder) {
    final TypeRoot typeMap = values.get(type);
    if (typeMap != null) {
      final Template templateMap = typeMap.get(template);
      if (templateMap != null) {
        final String value = templateMap.get(placeholder);
        if (value != null) {
          return value;
        }
      }
    }
    return "";
  }

  public String getHelpTextForType(final String type) {
    return "help for " + type;
  }

  public String getHelpTextForTemplate(final String type, final String template) {
    return "help for " + type + " " + template;
  }

  public void setValue(final String type, final String template, final String paramName, final String paramValue) {
    TypeRoot typeMap = values.get(type);
    if (typeMap == null) {
      typeMap = new TypeRoot();
      values.put(type, typeMap);
    }
    Template templateMap = typeMap.get(template);
    if (templateMap == null) {
      templateMap = new Template();
      typeMap.put(template, templateMap);
    }
    templateMap.put(paramName, paramValue);
  }

  public HashMap<String, String> getPlaceHolders(final String type, final String template, final String phName) {
    final TypeRoot typeMap = values.get(type);
    if (typeMap != null) {
      final Template templateMap = typeMap.get(template);
      if (templateMap != null) {
        final Value value = templateMap.getValue(phName);
        if (value != null) {
          return value.getEntries();
        }
      }
    }
    return new HashMap<>();
  }

  public void reset(final String type, final String template) {
    final TypeRoot typeMap = values.get(type);
    if (typeMap != null) {
      typeMap.put(template, new Template());
    }
  }

  public void setTypes(final Collection<String> dynamicTypes) {
    types.addAll(dynamicTypes);
  }
}

class TypeRoot {

  @ElementMap(entry = "template", key = "name", attribute = true, inline = true)
  private HashMap<String, Template> templates = new HashMap<String, Template>();

  public Template get(final String template) {
    return templates.get(template);
  }

  public void put(final String template, final Template templateMap) {
    templates.put(template, templateMap);
  }
}

class Template {

  @ElementMap(entry = "params", key = "name", attribute = true, inline = true)
  private HashMap<String, Value> params = new HashMap<String, Value>();

  public void put(final String paramName, final String paramValue) {
    if (paramName.startsWith("ph_")) {
      final String placeholder = paramName.substring(0, paramName.indexOf(':'));
      final String name = paramName.substring(paramName.indexOf(':') + 1);
      Value val = params.get(placeholder);
      if (val == null) {
        val = new Value(placeholder);
        params.put(placeholder, val);
      }
      val.add(name, paramValue);
    } else {
      params.put(paramName, new Value(paramValue));
    }
  }

  public String get(final String placeholder) {
    final Value val = params.get(placeholder);
    return val == null ? "" : val.getValue();
  }

  public Value getValue(final String name) {
    return params.get(name);
  }
}

class Value {

  @Element(required = false)
  private String name;

  @Element
  private String value;

  @ElementMap(entry = "entries", key = "name", attribute = true, inline = true, required = false)
  private HashMap<String, String> entries = new HashMap<String, String>();

  public Value() {

  }

  public Value(final String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public void add(final String name, final String paramValue) {
    entries.put(name, paramValue);
  }

  public HashMap<String, String> getEntries() {
    return entries;
  }
}