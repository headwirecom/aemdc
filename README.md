# aemcompanion
AEM Companion

To compile:

- mvn clean install

In the compile target folder with dependencies under /lib call:

- java -jar aemcompanion-1.0.0.jar my-aem-project template my-template-name my-jcr-title
- A target file will be created and jcr:title will be changed to "my-jcr-title"
"src/main/content/jcr_root/apps/my-aem-project/templates/my-template-name/.content.xml"

Standalone call.
Go to your "ui.apps" project. Copy aemcompanion-1.0.0-jar-with-dependencies.jar there or set the classpath to "aemcompanion-1.0.0-jar-with-dependencies.jar" directory. 

- java -jar aemcompanion-1.0.0-jar-with-dependencies.jar my-aem-project template my-template-name my-jcr-title
- A target file will be created and jcr:title will be changed to "my-jcr-title"
"src/main/content/jcr_root/apps/my-aem-project/templates/my-template-name/.content.xml"
