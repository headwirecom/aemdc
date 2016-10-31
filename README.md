# aemcompanion
AEM Companion

# Using placeholders
In the templates (xml,js,java,css) you can define placeholders similar to the argument keys

Examples of used placeholders in the /aemc-files/aemc-placeholders/apps/project/templates/contentpage/.content.xml

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

To replace the placeholders used above your arguments in the command line must be
	
	java -jar aemcompanion-1.0.0-jar-with-dependencies.jar temp contentpage mycontentpage jcr:title='my title' jcr:description='my description' ranking={Long}10 sling:resourceType=my-aem-project/components/mycontentpage ph_1_1:property1=value1 ph_1_1:property2=value2 ph_1_2:property3=value3

where "ph\_1\_2":

	- "ph_1" in the placeholders prefix means placeholders properties set
	- "_2" in the placeholders prefix means the offset for the property position in the modified file. 2 means 2*4 = 8 blank spaces from left.

# Running
Clone the aemc-files from GitHub
	
	https://github.com/headwirecom/aemc-files.git

Copy '/aemc-files/aemc-placeholders' to your apps.ui project and modify for you needs.

To run

	java [-options] -jar aemc.jar <type> <name> <targetname> [args...]

Where [-options] includes usual for java command options.

	type:
	    temp        Template to be created.
	    comp        Component to be created.
	    osgi        Osgi config to be created.
	name:
	    Template name under /templates used to create a target template (ex. template1)
	    OR
	    Component name under /components used to create a target conponent (ex. component1)
	    OR
	    Osgi file name under /config used to create a target osgi config (ex. osgi.1.PID.xml)
	targetname:
	    Target template name (ex. homepage).
	    Target component name (ex. my-teaser).
	    Osgi service PID file name (ex. org.apache.sling.serviceusermapping.impl.ServiceUserMapperImpl.amended-myaemproject.xml).
	args:
	    runmode=<value>
	    <property name>=<property value>
	                Properties used in the template/component/osgi definition (ex. sling:resourceType=/apps/my-aem-project/components/contentpage)    
	    <properties set>:<property name>=<property value>
	                Properties used in the template/component/osgi and belongs to one set definition (ex. ph_1_2:property1=value1 ph_1_2:property2=value2)    



# Development

To compile:

- mvn clean install

In the compile target folder with dependencies under /lib call:

	java -jar aemcompanion-1.0.0.jar temp contentpage mycontentpage 'jcr:title=my title' 'ph_1_1:singlePropExample1_1=my test&value' ph_1_1:singlePropExample1_2=my-test-value2  ph_1_2:singlePropExample2_1=my-test-value2_1

A target files will be created and placeholders will be replaced with arguments 

	"src/main/content/jcr_root/apps/my-aem-project/templates/mycontentpage/.content.xml"
	"src/main/content/jcr_root/apps/my-aem-project/templates/mycontentpage/thumbnail.png"

Standalone call.
Go to your "ui.apps" project. Copy aemcompanion-1.0.0-jar-with-dependencies.jar there or set the classpath to "aemcompanion-1.0.0-jar-with-dependencies.jar" directory. 

	java -jar aemcompanion-1.0.0-jar-with-dependencies.jar temp contentpage mycontentpage 'jcr:title=my title' 'ph_1_1:singlePropExample1_1=my test&value' ph_1_1:singlePropExample1_2={Boolean}true  ph_1_2:singlePropExample2_1=[/content/my-aem-project,/content/my-aem-project/en/index]
	
	java -jar aemcompanion-1.0.0-jar-with-dependencies.jar comp title mytitle 'jcr:title=my title' 'ph_1_1:singlePropExample1_1=my test&value' ph_1_1:singlePropExample1_2={Boolean}true  ph_1_2:singlePropExample2_1=[/content/my-aem-project,/content/my-aem-project/en/index]
	
	java -jar aemcompanion-1.0.0-jar-with-dependencies.jar comp contentpage mycontentpage 'jcr:title=my title' 'ph_1_1:singlePropExample1_1=my test&value' ph_1_1:singlePropExample1_2={Boolean}true  ph_1_2:singlePropExample2_1=[/content/my-aem-project,/content/my-aem-project/en/index]
	
	java -jar aemcompanion-1.0.0-jar-with-dependencies.jar osgi osgi.1.PID.xml com.day.cq.wcm.mobile.core.impl.MobileEmulatorProvider-medianewsletterconfig.xml runmode=author.prod ph_1_1:mobile.resourceTypes=[my-aem-project/components/medianewsletter]  ph_1_1:README="Use these configuration settings to indicate which resource types should be supported by the mobile emulators."
