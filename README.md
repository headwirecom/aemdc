# AEM Developer Companion (AEMDC)
AEMDC is a scaffolding tool to help a developer quickly create AEM templates,
components, content pages, osgi configurations, AEM 6.2 editable template structures,
java models, services, servlets and filters from predefined templates.

AEMDC works best with an AEM project created by the
[aem lazybones archetype](https://github.com/Adobe-Consulting-Services/lazybones-aem-templates),
but can also be used with other project structures.

# Prerequisites

AEMDC requires the following:

* Java 8+
* Git

# Installation

Download the released .zip or tar.gz file, extract it to your tools directory, and
set the global ENV and PATH variables like:

    set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_102
    set GIT_HOME=C:\Program Files\Git
    set AEMDC_HOME=C:\Program Files\aemdc
    set PATH=%PATH%;%JAVA_HOME%\bin;%GIT_HOME%\bin;%AEMDC_HOME%\bin

# First Run

Go to your AEM maven parent project root (one level up from the "ui.apps" or "core" modules) and run the following command to create the initial configuration file:

	aemdc config

A file called **aemdc-config.properties** will be created at the project root, e.g.:

	/my-aem-project/aemdc-config.properties

You may modify this file to suit your needs.

If your project was created by the
[aem lazybones archetype](https://github.com/Adobe-Consulting-Services/lazybones-aem-templates),
the properties will be auto discovered.

# Example Run

To create a new AEM template, run:

	aemdc template contentpage mycontentpage "jcr:title=my title" "ph_contentpage_1:singlePropExample1_1=my test&value" "ph_contentpage_1:singlePropExample1_2=my-test-value2"  "ph_contentpage_2:singlePropExample2_1=my-test-value2_1"

Target files will be created and placeholders will be replaced with arguments defined in the command line

	"ui.apps/src/main/content/jcr_root/apps/my-aem-project/templates/mycontentpage/.content.xml"
	"ui.apps/src/main/content/jcr_root/apps/my-aem-project/templates/mycontentpage/thumbnail.png"

# Usage - GUI

To start the GUI for AEMDC, run the following command:

    aemdcgui

and follow the on screen instructions

# Usage - Command Line
The following command line options are available:

    aemdc [options] <type> [name] [targetname] [args...]

    options:
        help               This help text.
        help config        Shows initial default configuration properties.
        help <type>        Shows list of possible templates.
        help <type> <name> Shows list of possible place holders.
        -temp=<path>       Create all templates under temp folder.
	type:
	    config      Create configuration properties file.
	    component   Component to be created.
	    compound    Set of different templates to be created.
	    confstr     Editable templates structure to be created.
	    filter      Filter java class to be created.
	    model       Model java class to be created.
	    osgi        Osgi config to be created.
	    page        Content page to be created.
	    service     Service java class to be created.
	    servlet     Servlet java class to be created.
	    template    Template to be created.
	name:
	    Source template name.
	targetname:
	    Target resource name (folder or file w/o extension).
	args:
	    <placeholder name>=<value>
	        Placeholder used in all template files.
	    <properties placeholder set name>:<property name>=<property value>
	        Properties placeholder set used in the template xml files.


# Creating your own Templates

Clone the aemdc-files from GitHub to a folder parallel to your AEM maven project root and modify the templates from aemdc-files for you needs.
	
	git clone https://github.com/headwirecom/aemdc-files.git ../aemdc-files

In the XML templates, you can define placeholders with similar names to the arguments keys. The component start file name, java class name, and java package in java classes will can be also replaced by placeholders "{{ targetname }}", "{{ java-class }}", "{{ java-package }}" and "{{ java-interface-package }}". See examples of placeholders used in /aemdc-files/template/contentpage/files/..

    {{ jcr:title }}
    {{ jcr:description }}
    {{ ranking }}
    {{ allowedPaths }}
    {{ ph_contentpage_1 }}
    {{ sling:resourceType }}
    {{ ph_contentpage_2 }}

Source code of  .content.xml 

	<?xml version="1.0" encoding="UTF-8"?>
	<jcr:root xmlns:sling="http://sling.apache.org/jcr/sling/1.0" xmlns:cq="http://www.day.com/jcr/cq/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0"
	    jcr:primaryType="cq:Template"
	    jcr:title="{{ jcr:title }}"
	    jcr:description="{{ jcr:description }}"
	    ranking="{{ ranking }}"
	    allowedPaths="[{{ allowedPaths }}]"
	    {{ ph_contentpage_1 }}
	    >
	    <jcr:content
	        jcr:primaryType="cq:PageContent"
	        sling:resourceType="{{ sling:resourceType }}"
	        {{ ph_contentpage_2 }}
	        />
	</jcr:root>

When running aemdc, the placeholders will be replaced with the arguments provided
	
	aemdc template contentpage mycontentpage "jcr:title=my title" "jcr:description=my description" "ranking={Long}10" "sling:resourceType=my-aem-project/components/mycontentpage" "ph_contentpage_1:property1=value1" "ph_contentpage_1:property2=value2" "ph_contentpage_2:property3=value3"

placeholders with the syntax ph_<name>_<number> are meant to be lists of name-value pairs.
The number indicates how many spaces will be emitted before each name value pair in the output file

for example: "ph\_contentpage\_2":

	- "ph_contentpage" in the placeholders prefix defines the name
	- "_2" in the placeholders suffix means the offset for the property position in the modified file. 2 means 2*4 = 8 blank spaces from left.

#Wiki

For more technical information and best practices see Wiki page

https://github.com/headwirecom/aemdc/wiki

# Development

To compile:

	mvn clean install

Extract the generated .zip or tar.gz file from target directory to your tools directory and set the global ENV and PATH variables:

    set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_102
    set GIT_HOME=C:\Program Files\Git
    set AEMDC_HOME=C:\Program Files\aemdc
    set PATH=%PATH%;%JAVA_HOME%\bin;%GIT_HOME%\bin;%AEMDC_HOME%\bin

Go to your AEM maven parent project root (one level up from the "ui.apps" or "core" modules) and run the following:

	aemdc template contentpage mycontentpage "jcr:title=my title" "ph_contentpage_1:singlePropExample1_1=my test&value" "ph_contentpage_1:singlePropExample1_2=my-test-value2"  "ph_contentpage_2:singlePropExample2_1=my-test-value2_1"

Target files will be created and placeholders will be replaced with arguments defined in the command line

	"ui.apps/src/main/content/jcr_root/apps/my-aem-project/templates/mycontentpage/.content.xml"
	"ui.apps/src/main/content/jcr_root/apps/my-aem-project/templates/mycontentpage/thumbnail.png"
