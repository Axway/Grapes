<img src="./grapes.gif" class="pull-left" style="padding:30px" width="15%"/>

<span class="page-header">
<h1>Grapes Client Specifications</h1>
</span>

Grapes clients can be implemented to send information to Grapes (notifications) or to get information from Grapes (reporting). Grapes clients <strong>must</strong> use Grapes server REST API.

<strong>Warning:</strong> some clients has already been implemented. Before implementing a new client, please check that none of the existing ones can fits your need.

<p class="clearfix"/>

## Grapes Data Model
The current version of Grapes is based on 4 objects. Because Grapes API is an HTTP REST API, the objects are represented using JSON.

### Module

A module is the smallest part of your software that has it own life-cycle. A module can contain:

1. sub-modules if they all have the same lifecycle
1. artifacts if your module builds binaries (example: jar, zip, ...)
1. dependencies if your module requires external libraries to be built


<strong>Example</strong>
<code>
{
   name:"#moduleNames#",
   version: "#moduleVersion#",
   artifacts: [#listOfArtifacts#],
   submodules: [#listOfModule#],
   dependencies: [#listOfDependencies#]
}
</code>

<strong>WARNING:</strong> The module name and version are mandatory.

### Artifact

An artifact is a binary delivered by a module. It could be any delivery of your production (jar, doc, test delivery, source-jar, zip, iso, exe, rpm ...). An artifact object contains the following fields:

1. groupId: brings information about where the artifact comes from and about the hierarchy between artifacts
1. artifactId: artifact name
1. version
1. classifier: holds some more details about the artifact (for example the targeted platform)
1. type: kind of artifact
1. extension (zip, jar ...)
1. size
1. download url
1. provider

<strong>Example</strong>
<code>
{
   groupId: "#groupId#",
   artifactId: "#artifactId#",
   version: "#version#",
   classifier: "#classifier#",
   type: "#type#",
   extension: "#extension#",
   size: "#size#",
   downloadUrl: "#downloadUrl#",
   provider: "#provider#"
}
</code>

<strong>WARNING:</strong> artifactId, groupId, version fields are mandatory.

### Dependency

The dependencies represent links between modules (or sub-modules) and external libraries. This link between the module and an external artifact is tainted thanks to the field "scope". Scope must either be “COMPILE”, “RUNTIME”, “PROVIDED” or “TEST”.

<strong>Example</strong>
<code>
{
   target: { #artifact# },
   scope: "#dependencyScope#"
</code>

### License

License objects are used to represent the third-party library licenses (ex: Apache-2.0, MIT ...).

<strong>Example</strong>
{
   name:"#licenseName#",
   longName: "#fullLicenseName#",
   url: "#url#",
   comment<: "#comments#",
   regexp: "#regexp#"
}

<strong>WARNING:</strong> Name field should be unique for each license

## Notifications & Reporting

<p>Grapes notifications and reporting are performed via the HTTP REST API. Use HTTP GET/POST/DELETE, to interact with grapes using the data-model described in the previous paragraph.</p>
<p>Each Grapes instance holds the documentation of its API. To check the API go on the home page of your grapes instance.</p>

Other topics
-----------

|-----------------------------------------------------------|---------|
| [Quick start](../user_doc/quick-start.html)					| Minimal information to install and start Grapes |
| [Authentication management](../user_doc/authentication.html)	| Authentication and roles policy |
| [Grapes clients](clients.html)			        | Grapes clients |