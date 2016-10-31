# aemcompanion
AEM Companion

To compile:

- mvn clean install

Clone the aemc-files from GitHub
	
	https://github.com/headwirecom/aemc-files.git

Copy '/aemc-files/aemc-placeholders' to your apps.ui project.

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
