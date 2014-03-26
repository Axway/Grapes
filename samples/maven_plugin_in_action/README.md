Grapes Maven Plugin Sample
===========================

MyProject is a sample Maven project that uses grapes-maven-plugin.
The integration of the plugin is made in the root pom file of the project (see at myProject/pom.xml).

Generates the Grapes report
---------------------------
Run the following command:

        mvn clean package

The project is configure to generate a Grapes report in ${project.build.directory}/grapes/.

Send the report to a Grapes server
----------------------------------
Run the command:

        mvn org.axway.grapes:grapes-maven-plugin:1.2.0-SNAPSHOT:notify

You will see that the plugin try to establish a connection to grapes.org to send the report. Of course no Grapes instance is available at grapes.org thought the notification will failed.

If you want that the failure of the Grapes Maven plugin does not impact the maven execution you can run:

        mvn org.axway.grapes:grapes-maven-plugin:1.2.0-SNAPSHOT:notify -Dgrapes.failOnError=false
