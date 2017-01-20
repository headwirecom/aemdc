# AEM Developer Companion (AEMDC)
AEMDC allows the developer to create AEM templates, components, content pages, osgi configurations, AEM 6.2 editable template structures, java models, services and servlets from predefined templates.

# Using placeholders
Clone the aemdc-files from GitHub to a parallel folder to your AEM maven project and modify the templates from aemdc-files for you needs. 
	
	git clone https://github.com/headwirecom/aemdc-files.git ../aemdc-files

In the XML templates you can define placeholders with similar names as the arguments keys. The java class name and java package in java classes will can be also replaced by placeholders "{{ java-class }}" and "{{ java-package }}". See examples of used placeholders in the /aemdc-files/placeholders/content/src/main/content/jcr_root/apps/project/templates/contentpage/.content.xml

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

To replace the placeholders used above, your arguments in the command line must be like this
	
	aemdc temp contentpage mycontentpage "jcr:title=my title" "jcr:description=my description" "ranking={Long}10" "sling:resourceType=my-aem-project/components/mycontentpage" "ph_contentpage_1:property1=value1" "ph_contentpage_1:property2=value2" "ph_contentpage_2:property3=value3"

where "ph\_1\_2":

	- "ph_contentpage" in the placeholders prefix means placeholders properties set
	- "_2" in the placeholders prefix means the offset for the property position in the modified file. 2 means 2*4 = 8 blank spaces from left.

# Running

Download the released .zip or tar.gz file, extract it to your tools directory and set the global ENV and PATH variables like:

    set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_102
    set GIT_HOME=C:\Program Files\Git
    set AEMDC_HOME=C:\Program Files\aemdc
    set PATH=%PATH%;%JAVA_HOME%\bin;%GIT_HOME%\bin;%AEMDC_HOME%\bin

Go to your AEM maven parent project of "ui.apps" or "core" projects and create a configuration file: 

	aemdc config

The command creates the next configuration file, modify it for your needs:
 
	/my-aem-project/aemdc-config.properties

To create a new AEM template run: 

	aemdc temp contentpage mycontentpage "jcr:title=my title" "ph_contentpage_1:singlePropExample1_1=my test&value" "ph_contentpage_1:singlePropExample1_2=my-test-value2"  "ph_contentpage_2:singlePropExample2_1=my-test-value2_1"

Target files will be created and placeholders will be replaced with arguments defined in the command line

	"src/main/content/jcr_root/apps/my-aem-project/templates/mycontentpage/.content.xml"
	"src/main/content/jcr_root/apps/my-aem-project/templates/mycontentpage/thumbnail.png"


# Usage
The following command line options are available:

	aemdc [-options] [help] <type> [name] [targetname] [args...]
	OR
	java [-options] -jar aemdc-X.X.X-jar-with-dependencies.jar [help] <type> [name] [targetname] [args...]
	
	-options:
        Includes usual for java command options.
	help:
	    ""            This help text.
	    config        Shows initial default configuration properties.
	    <type>        Shows list of possible templates.
	    <type> <name> Shows list of possible place holders.    
	type:
	    config      Create configuration properties file.
	    temp(late)  Template to be created.
	    comp(onent) Component to be created.
	    osgi        Osgi config to be created.
	    confstr     Editable templates structure to be created.
	    page        Content page to be created.
	    model       Model java class to be created.
	    servlet     Servlet java class to be created.
	    service     Service java class to be created.	name:
	name:
    	Source template name (folder or file).
    targetname:
	    Target template name (folder or file).
	args:
	    runmode=<value>
	        Osgi config folder definition runmode.    
	    <placeholder name>=<value>
	        Placeholder used in all template files. 
	    <properties placeholder set name>:<property name>=<property value>
	        Properties placeholder set used in the template xml files.


# Development

To compile:

	mvn clean install

Extract the generated .zip or tar.gz file from target directory to your tools directory and set the global ENV and PATH variables:

    set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_102
    set GIT_HOME=C:\Program Files\Git
    set AEMDC_HOME=C:\Program Files\aemdc
    set PATH=%PATH%;%JAVA_HOME%\bin;%GIT_HOME%\bin;%AEMDC_HOME%\bin

Go to your AEM maven parent project of "ui.apps" or "core" projects and run: 

	aemdc temp contentpage mycontentpage "jcr:title=my title" "ph_contentpage_1:singlePropExample1_1=my test&value" "ph_contentpage_1:singlePropExample1_2=my-test-value2"  "ph_contentpage_2:singlePropExample2_1=my-test-value2_1"

Target files will be created and placeholders will be replaced with arguments defined in the command line

	"src/main/content/jcr_root/apps/my-aem-project/templates/mycontentpage/.content.xml"
	"src/main/content/jcr_root/apps/my-aem-project/templates/mycontentpage/thumbnail.png"
