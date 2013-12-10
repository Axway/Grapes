Usage
============

Grapes Maven plugin attempts to send dependencies information of a maven project to a Grapes instance. It assumes that you already have a Grapes server.

In this page, let's assume that you use an Grapes instance configured as follow:

* host: http://somewhere.com
* user: usr
* password: pwd


Sending dependencies without changing the pom:
--------------------------------------

Here is the command line to execute on the maven project:

        mvn org.axway.grapes:maven:notify -Dgrapes.host=http://somewhere.com -Dgrapes.user=usr -Dgrapes.password=pwd


Sending dependencies changing project pom configuration:
--------------------------------------

Add the following information in your pom file:

        <project>
            ...
            <build>
                </plugins>
                    ...
                     <plugin>
                        <groupId>org.axway.grapes</groupId>
                        <artifactId>grapes-maven-plugin</artifactId>
                        <version>1.0.0-SNAPSHOT</version>
                        <executions>
                            <execution>
                                <phase>install</phase>
                                <goals>
                                    <goal>notify</goal>
                                </goals>
                                <configuration>
                                    <host>http://somewhere.com</host>
                                </configuration>
                            </execution>
                        </executions>
                        </plugin>
                </plugins>
            </build>
        </project>


Then in your favorite CI server or in command line run the following script:

        mvn clean install -Dgrapes.user=usr -Dgrapes.password=pwd



