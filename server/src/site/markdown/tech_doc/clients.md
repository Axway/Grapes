<img src="./client.png" class="pull-left" style="padding:30px"/>

<span class="page-header">
<h1>Grapes Clients</h1>
</span>

Grapes clients can by used to create or update information via Grapes REST API.

<p class="clearfix"/>

## Implemented clients

* <strong>Jenkins plugin :</strong>
 This Jenkins plugin is able to send dependency information to Grapes at each maven build.

* <strong>Data Browser :</strong>
Grapes web application which is able to update licenses and artifacts information.

* <strong> License utils:</strong>
Tool able to fill the license information of the third party libraries.

* <strong> Maven plugin:</strong>
A maven plugin is available to send dependency information of a maven project.

<strong>N.B.</strong> To get the documentation of Grapes Maven plug-in, run "mvn site" on the sources of Grapes Maven plug-in project.

## Client specification

It is possible to implement its own Grapes client. You will find some more information and a detailed specification [here](../tech_doc/clients-specs.html).

Other topics
-----------

|-----------------------------------------------------------|---------|
| [Quick start](../user_doc/quick-start.html)					| Minimal information to install and start Grapes |
| [Authentication management](../user_doc/authentication.html)	| Authentication and roles policy |
| [Grapes client specification](clients-specs.html)| Specifications to create a new Grapes client |