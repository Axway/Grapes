This sample contains a project called myProject that uses grapes-maven-plugin.
The integration of the plugin is made in the root pom file of the project (see at myProject/pom.xml).

 - To execute the sample run the command 'mvn clean install'.
 - By default the sample is configured to serialize the notification that should be sent to Grapes (in target/grapes/module.json).
 - The notification is sent by default at localhost:12345 using user 'test' with password 'test'
 - The option FailedOnError has been set to false though the command line 'mvn clean install' won't failed even if no Grapes server is installed at localhost:12345

To integrate the plugin into your CI you can use profiles to run the plugin only from your build farms.