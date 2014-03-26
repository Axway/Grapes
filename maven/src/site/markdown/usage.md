Usage
============

Grapes Maven plugin attempts to gathers and then send information about dependencies of a maven project. It assumes that you are already using a Grapes server. For more information about Grapes, visit our [wiki].

Grapes maven plugin provides two goals:

* [grapes:generate]: used to generate a JSON file that gathers all the dependencies of a maven project
* [grapes:notify]: used to send the JSON file produced by the generate goal to a Grapes server instance


Gathering dependencies without changing the pom:
------------------------------------------------
Here is the command line to gather the dependencies of a project:

        mvn org.axway.grapes:grapes-maven-plugin:generate


At the end of the execution a file "module.json" is created in ${project.build.directory}/grapes/.


Gathering dependencies changing project pom configuration:
--------------------------------------------------------

Add the following information in your pom file:

        <project>
            ...
            <build>
                </plugins>
                    ...
                     <plugin>
                        <groupId>org.axway.grapes</groupId>
                        <artifactId>grapes-maven-plugin</artifactId>
                        <version>1.2.0</version>
                        <executions>
                            <execution>
                                <phase>package</phase>
                                <goals>
                                    <goal>generate</goal>
                                </goals>
                            </execution>
                        </executions>
                        </plugin>
                </plugins>
            </build>
        </project>


Then in your favorite CI server or in command line run the following script:

        mvn clean package

At the end of the execution a file "module.json" is created in ${project.build.directory}/grapes/.

Sending the report to a Grapes server:
--------------------------------------

Once the report has been built by the execution of "generate" goal, the following command line sends the report to a Grape server:

        mvn org.axway.grapes:maven:notify -Dgrapes.host=somewhere.com -Dgrapes.user=usr -Dgrapes.password=pwd


Here we assume that we are using a Grapes server is configured as follow:

* host: somewhere.com
* user: usr
* password: pwd


Of course, if you want you can add the configuration of the plugin in the pom file of your project:

        <project>
            ...
            <build>
                </plugins>
                    ...
                     <plugin>
                        <groupId>org.axway.grapes</groupId>
                        <artifactId>grapes-maven-plugin</artifactId>
                        <version>1.0.0-SNAPSHOT</version>
                        <configuration>
                            <host>somewhere.com</host>
                            <port>8080</port>
                            ...
                        </configuration>
                        </plugin>
                </plugins>
            </build>
        </project>


We suggest you to avoid setting the user / password in your pom file to avoid to spread it to everybody. Though with this configuration you can run:

        mvn org.axway.grapes:maven:notify -Dgrapes.user=usr -Dgrapes.password=pwd



[wiki]:https://github.com/Axway/Grapes/wiki
[grapes:generate]:generate_goal.html
[grapes:notify]:notify_goal.html