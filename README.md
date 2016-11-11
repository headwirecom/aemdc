# aemdc
AEM Developer Companion allows to create AEM templates, components, osgi configurations, AEM 6.2 editable templates structure, java models, services and servlets from predefined templates.

# Using placeholders
Clone the aemdc-files from GitHub to a parallel folder to your AEM maven project and modify the templates from aemdc-files for you needs. 
	
	git clone https://github.com/headwirecom/aemdc-files.git ../aemdc-files

In the XML templates you can define placeholders with similar names as the arguments keys. In the java classes the java class name and java package can be also replaced by placeholders "{{ java-class }}" and "{{ java-package }}". See examples of used placeholders in the /aemdc-files/placeholders/apps/project/templates/contentpage/.content.xml

	{{ jcr:title }}
	{{ jcr:description }}
	{{ ranking }}
	{{ sling:resourceType }}
	{{ ph_1_1 }}
	{{ ph_1_2 }}

Source code of  .content.xml 

	<?xml version="1.0" encoding="UTF-8"?><jcr:root xmlns:sling="http://sling.apache.org/jcr/sling/1.0" xmlns:cq="http://www.day.com/jcr/cq/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0"
    jcr:primaryType="cq:Template"
    jcr:title="{{ jcr:title }}"
    jcr:description="{{ jcr:description }}"
    {{ ph_1_1 }}
    ranking="{{ ranking }}">
    <jcr:content
        jcr:primaryType="cq:PageContent"
        {{ ph_1_2 }}
        sling:resourceType="{{ sling:resourceType }}"/>
    </jcr:root>

To replace the placeholders used above your arguments in the command line must be like this
	
	java -jar aemdc-1.0.0-jar-with-dependencies.jar temp contentpage mycontentpage jcr:title='my title' jcr:description='my description' ranking={Long}10 sling:resourceType=my-aem-project/components/mycontentpage ph_1_1:property1=value1 ph_1_1:property2=value2 ph_1_2:property3=value3

where "ph\_1\_2":

	- "ph_1" in the placeholders prefix means placeholders properties set
	- "_2" in the placeholders prefix means the offset for the property position in the modified file. 2 means 2*4 = 8 blank spaces from left.

# Running

Please create a configuration file in the root of your AEM maven project and modify configuration for your needs. For example
 
	/my-aem-project/aemdc-config.properties

Usage:

	java [-options] -jar aemdc.jar [help [config]] <type> <name> <targetname> [args...]
	
	-options:
        Includes usual for java command options.
	help:
	    ""            This help text.
	    config        Shows current configuration properties.
	    <type>        Shows list of possible templates.
	    <type> <name> Shows list of possible place holders.
	type:
	    temp(late)  Template to be created.
	    comp(onent) Component to be created.
	    osgi        Osgi config to be created.
	    confstr     Editable templates structure to be created.
	    model       Model java class to be created.
	    servlet     Servlet java class to be created.
	    service     Service java class to be created.
	name:
	    Source AEM template name under ../templates folder.
	    Source AEM component name under ../components folder.
	    Source OSGI file name under ../config folder.
	    Source editable templates config folder name.
	    Source model file name under ../models java package.
	    Source service file name under ../services java package.
	    Source servlet file name under ../servlets java package.
	targetname:
	    Target AEM template name.
	    Target AEM component name.
	    Target osgi service PID file name.
	    Target editable templates config folder name.
	    Target model java class file name.
	    Target service java class file name.
	    Target servlet java class file name.
	args:
	    runmode=<value>
	        Osgi config folder definition runmode.
	    <placeholder name>=<value>
	        Placeholder used in all template files.
	    <properties placeholder set name>:<property name>=<property value>
	        Properties placeholder set used in the template xml files.

# Development

To compile:

- mvn clean install

In the compile target folder with dependencies under /lib call:

	java -jar aemdc-1.0.0.jar temp contentpage mycontentpage 'jcr:title=my title' 'ph_1_1:singlePropExample1_1=my test&value' ph_1_1:singlePropExample1_2=my-test-value2  ph_1_2:singlePropExample2_1=my-test-value2_1

A target files will be created and placeholders will be replaced with arguments 

	"src/main/content/jcr_root/apps/my-aem-project/templates/mycontentpage/.content.xml"
	"src/main/content/jcr_root/apps/my-aem-project/templates/mycontentpage/thumbnail.png"

Standalone call.
Go to the parent project of your "ui.apps" or "core" projects. Copy aemdc-1.0.0-jar-with-dependencies.jar there or set the classpath to "aemdc-1.0.0-jar-with-dependencies.jar" directory. 

	java -jar aemdc-1.0.0-jar-with-dependencies.jar temp contentpage mycontentpage 'jcr:title=my title' 'ph_1_1:singlePropExample1_1=my test&value' ph_1_1:singlePropExample1_2={Boolean}true  ph_1_2:singlePropExample2_1=[/content/my-aem-project,/content/my-aem-project/en/index]
	
	java -jar aemdc-1.0.0-jar-with-dependencies.jar comp title mytitle 'jcr:title=my title' 'ph_1_1:singlePropExample1_1=my test&value' ph_1_1:singlePropExample1_2={Boolean}true  ph_1_2:singlePropExample2_1=[/content/my-aem-project,/content/my-aem-project/en/index]
	
	java -jar aemdc-1.0.0-jar-with-dependencies.jar comp contentpage mycontentpage 'jcr:title=my title' 'ph_1_1:singlePropExample1_1=my test&value' ph_1_1:singlePropExample1_2={Boolean}true  ph_1_2:singlePropExample2_1=[/content/my-aem-project,/content/my-aem-project/en/index]
	
	java -jar aemdc-1.0.0-jar-with-dependencies.jar osgi osgi.1.PID.xml com.day.cq.wcm.mobile.core.impl.MobileEmulatorProvider-medianewsletterconfig.xml runmode=author.prod ph_1_1:mobile.resourceTypes=[my-aem-project/components/medianewsletter]  ph_1_1:README="Use these configuration settings to indicate which resource types should be supported by the mobile emulators."
	
	java -jar aemdc-1.0.0-jar-with-dependencies.jar confstr project my-aem-project 'jcr:title=My AEM Project'
	
	java -jar aemdc-1.0.0-jar-with-dependencies.jar model SampleSlingModel.java MySampleSlingModel.java
	
	java -jar aemdc-1.0.0-jar-with-dependencies.jar service SampleService.java MySampleService.java
	
	java -jar aemdc-1.0.0-jar-with-dependencies.jar service impl/SampleServiceImpl.java impl/MySampleServiceImpl.java
	
	java -jar aemdc-1.0.0-jar-with-dependencies.jar servlet impl/SampleSafeMethodsServlet.java impl/MySampleSafeMethodsServlet.java
	