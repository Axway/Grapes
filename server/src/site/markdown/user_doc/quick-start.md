<img src="./start.png" class="pull-left" style="padding:30px"/>
<span class="page-header">
<h1>Quick start</h1>
</span>

<p class="clearfix"/>

##Prerequisites

* Mongodb database
* Java 1.6 (or > )
* Two open ports (one for the server web-application, one for the administration panel web-application)

## Installation

###Install from sources

1. run <code>mvn clean install</code> from sources
1. create a configuration file based on [this template](./server-config-template.yml)
1. run <code>java -jar path to Grapes jar server \<path to the configuration file\></code>

###Install from RPM (OpenSuse or SLES)

Get the RPM and run <code> zypper install "path_to_The_RPM"</code>
It will install Grapes on your server regarding the following conventions:

1. installation folder is "/opt/grapes"
1. logs are in "/var/log/grapes"
1. grapes is installed as a service on the machine (service is called grapes)

<strong>WARNING:</strong> The package installs MongoDb RPM if it is not installed. It will also configure your MongoDb instance as follow:

1. port: 12341
1. host: 127.0.0.1 (database is not visible from outside)
1. no credential management
1. database name: grapes

## Configuration

###Technical configuration
The technical configuration is stored in a single YAML file. If you have installed Grapes thanks to the RPM, the configuration file is in <code>/opt/grapes/conf/</code>.
[Here](./server-config-template.yml) is a sample of a Grapes configuration file.

###Functional configuration

* <strong>authentication management:</strong>
To protect Grapes database and configuration, an authentication policy has been defined with different kinds of users.
All you need to know about authentication is available [here](./authentication.html).

* <strong>corporate groupIds:</strong>
Grapes differentiates the corporate production from the third party libraries thanks to artifacts groupIds.
Corporate groupIds are defined in Grapes configuration thanks to the administration web-app.

<table>
    <tr>
        <td>Method</td>
        <td>URL</td>
        <td>Query Parameters</td>
    </tr>
    <tr>
        <td>POST</td>
        <td>http://dm-url:admin-port/tasks/addCorporateGroupId</td>
        <td><ul><li><p><strong>groupId:</strong> the new corporate groupId</p></li></ul></td>
    </tr>
    <tr>
        <td>POST</td>
        <td>http://dm-url:admin-port/tasks/removeCorporateGroupId</td>
        <td><ul><li><p><strong>groupId:</strong> the groupId to remove</p></li></ul></td>
    </tr>
</table>

Notice that special characters (*, /, ', " ...) are prohibited from corporate groupIds.<br/>
Notice also that if "com.mycompany" is defined as corporate groupId, all the artifacts with a groupId that starts by "com.mycompany" will be considered as corporate production.

## Start using

### Provide dependency information

To send dependency information to Grapes, you have to use its REST API. Many solutions are available:

 1. Create your own HTTP REST Client: specifications are available [here](../tech_doc/clients-specs.html)
 1. Grapes web-app
<!-- 1. Use Jenkins plugin: documentation is [here](../tech_doc/clients.html) under development-->
 1. Use maven plugin

<strong>N.B.</strong> To get the documentation of Grapes Maven plug-in, run "mvn site" on the sources of Grapes Maven plug-in project.

###Try out the reports

Many kinds of tools are provided by Grapes to generate reports:

|-----------------------------------------------------------|---------|
| REST API      | The most complete and configurable reports are provided in JSON or in HTML |
| Grapes Web-app | Provides a quick feedback about the data stored in Grapes |
| Sequoia       | Generates graphical about dependencies |


## To go further
|-----------------------------------------------------------|---------|
| [Authentication management](./authentication.html)	                | Authentication and roles policy |
| [Grapes clients](../tech_doc/clients.html)			    | Grapes clients |