package com.headwire.aemdc.gui;

import com.headwire.aemdc.companion.Config;
import com.headwire.aemdc.companion.Reflection;
import com.headwire.aemdc.companion.Resource;
import com.headwire.aemdc.runner.BasisRunner;
import com.headwire.aemdc.runner.HelpRunner;
import com.headwire.aemdc.util.Help;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by rr on 12/29/2016.
 */
public class Model {

    private transient Config config = new Config();

    // setup of types that will be available
    private ArrayList<String> types = new ArrayList<String>();

    public Model() {
    }

    @ElementMap(entry="config", key="type", attribute = true, inline = true)
    private HashMap<String, TypeRoot> values = new HashMap<>();

    public List<String> getTypes() {

        return new ArrayList<String>(config.getDynamicTypes());
    }

    public List<String> getTemplatesForType(String type) {
        Resource resource = new Resource(new String[] {type} );
        BasisRunner helpRunner = new HelpRunner(resource);

        final Reflection reflection = new Reflection(config);
        final BasisRunner runner = reflection.getRunner(resource);

        Help helper = new Help();
        return (helper.getTemplatesAsList(runner));
    }

    public List<String> getPlaceHoldersForName(String type, String template) {
        Resource resource = new Resource(new String[] {type, template} );
        BasisRunner helpRunner = new HelpRunner(resource);

        final Reflection reflection = new Reflection(config);
        final BasisRunner runner = reflection.getRunner(resource);

        Help helper = new Help();

        final String name = resource.getSourceName();
        final String templateSrcPath = runner.getSourceFolder() + "/" + name;

        return (helper.getPlaceHoldersAsList(new File(templateSrcPath)));

    }

    public String getValue(String type, String template, String placeholder) {
        TypeRoot typeMap = values.get(type);
        if(typeMap != null) {
            Template templateMap = typeMap.get(template);
            if(templateMap != null) {
                String value = templateMap.get(placeholder);
                if(value != null) return value;
            }
        }
        return "";
    }

    public String getHelpTextForType(String type) {
        return "help for "+type;
    }

    public String getHelpTextForTemplate(String type, String template) {
        return "help for "+type+" "+template;
    }


    public void setValue(String type, String template, String paramName, String paramValue) {
        TypeRoot typeMap = values.get(type);
        if(typeMap == null) {
            typeMap = new TypeRoot();
            values.put(type, typeMap);
        }
        Template templateMap = typeMap.get(template);
        if(templateMap == null) {
            templateMap = new Template();
            typeMap.put(template, templateMap);
        }
        templateMap.put(paramName, paramValue);
    }

    public HashMap<String, String> getPlaceHolders(String type, String template, String phName) {
        TypeRoot typeMap = values.get(type);
        if(typeMap != null) {
            Template templateMap = typeMap.get(template);
            if(templateMap != null) {
                Value value = templateMap.getValue(phName);
                if(value != null) {
                    return value.getEntries();
                }
            }
        }
        return new HashMap<>();
    }

    public void reset(String type, String template) {
        TypeRoot typeMap = values.get(type);
        if(typeMap != null) {
            typeMap.put(template, new Template());
        }
    }

    public void setTypes(Collection<String> dynamicTypes) {
        types.addAll(dynamicTypes);
    }
}

class TypeRoot {

    @ElementMap(entry="template", key="name", attribute = true, inline = true)
    private HashMap<String, Template> templates = new HashMap<String, Template>();

    public Template get(String template) {
        return templates.get(template);
    }

    public void put(String template, Template templateMap) {
        templates.put(template, templateMap);
    }
}

class Template {

    @ElementMap(entry="params", key="name", attribute = true, inline = true)
    private HashMap<String, Value> params = new HashMap<String, Value>();

    public void put(String paramName, String paramValue) {
        if(paramName.startsWith("ph_")) {
            String placeholder = paramName.substring(0, paramName.indexOf(':'));
            String name = paramName.substring(paramName.indexOf(':')+1);
            Value val = params.get(placeholder);
            if(val == null) {
                val = new Value(placeholder);
                params.put(placeholder, val);
            }
            val.add(name, paramValue);
        } else {
            params.put(paramName, new Value(paramValue));
        }
    }

    public String get(String placeholder) {
        Value val = params.get(placeholder);
        return val == null? "" : val.getValue();
    }

    public Value getValue(String name) {
        return params.get(name);
    }
}

class Value {

    @Element(required = false)
    private String name;

    @Element
    private String value;

    @ElementMap(entry="entries", key="name", attribute = true, inline = true, required = false)
    private HashMap<String, String> entries = new HashMap<String, String>();

    public Value() {

    }

    public Value(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void add(String name, String paramValue) {
        entries.put(name, paramValue);
    }

    public HashMap<String, String> getEntries() {
        return entries;
    }
}