grapes:notify
================

Full name
---------------

org.axway.grapes:grapes-maven-plugin:${project.version}:notify

Description
---------------

Goal which gathers and send dependencies information to Grapes.

Attributes
---------------

* Requires a Maven project to be executed.
* The goal is thread-safe and supports parallel builds
* Binds by default to the lifecycle phase: install

| Name | Type | Since | Description |
|---|---|---|---|
| host | String | 1.0.0 | Host of the targeted Grapes server (mandatory) |
| port | String | 1.0.0 | Port of the targeted Grapes server |
| user | String | 1.0.0 | Grapes user to use during the notification |
| password | String | 1.0.0 | Password of the Grapes user |
| failOnError | Boolean | 1.0.0 | Indicates whether the build will continue even if there are clean errors (default value true) |
