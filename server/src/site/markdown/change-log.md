1.9.1
-------------
* [ECDDEV-903](https://techweb.axway.com/jira/browse/ECDDEV-903) - Promotion report messages need to support tags
* [ECDDEV-902](https://techweb.axway.com/jira/browse/ECDDEV-902) - Artifact version value with incorrect format generates too many verbose stack traces
* [ECDDEV-897](https://techweb.axway.com/jira/browse/ECDDEV-897) - Artifact version value with incorrect format generates too many verbose stack traces
* [ECDDEV-897](https://techweb.axway.com/jira/browse/ECDDEV-897) - Null PromotionReportView are generating NPE
* minor updates on the logging of the configuration related to promotion validation 


1.9.0
-------------
* [ECDDEV-868](https://techweb.axway.com/jira/browse/ECDDEV-858) - Promotion warnings and errors
* [ECDDEV-849](https://techweb.axway.com/jira/browse/ECDDEV-849) - Add LICENSE_SETTER among existent privileges in Grapes
* [ECDDEV-858](https://techweb.axway.com/jira/browse/ECDDEV-858) - Use promotion report to display Module / Promotion Report section
* [ECDDEV-863](https://techweb.axway.com/jira/browse/ECDDEV-863) - Add artifact links inside commercial delivery details page
* [ECDDEV-860](https://techweb.axway.com/jira/browse/ECDDEV-860) - Sanitize the values of regular expressions
* [ECDDEV-861](https://techweb.axway.com/jira/browse/ECDDEV-861) - Don't allow multiple reports matching the same report id


1.8.0
-------------
* [ECDDEV-813](https://techweb.axway.com/jira/browse/ECDDEV-813) Improve the search for modules and artifacts
* [ECDDEV-509](https://techweb.axway.com/jira/browse/ECDDEV-509) Add links to artifact details for module dependencies
* [ECDDEV-840](https://techweb.axway.com/jira/browse/ECDDEV-840) Enhance the detection of the artifact license association
* [ECDDEV-852](https://techweb.axway.com/jira/browse/ECDDEV-852) Include administrative reports on license management


1.5.0
-------------
* [ECDDEV-434](https://techweb.axway.com/jira/browse/ECDDEV-434) Allow external systems to check if an arbitrary artifact is promoted.

1.4.4
-------------
* [ECDDEV-391](https://techweb.axway.com/jira/browse/ECDDEV-391) Allow module page to directly display details of a module
* [ECDDEV-452](https://techweb.axway.com/jira/browse/ECDDEV-452) Verification against product existing in Grapes should be case insensitive
* [ECDDEV-469](https://techweb.axway.com/jira/browse/ECDDEV-469) Include the technical version inside the project release

1.4.3
-------------
* [Github issue #34](https://github.com/Axway/Grapes/issues/34) Fix potential NPE while performing module deletion
* Extends Grapes utils API

1.4.2
-------------
* [Pull request #33](https://github.com/Axway/Grapes/pull/33) Fix promotion report

1.4.1
-------------
* Fix webapp reports display
* Enhance promotion report

1.4.0
-------------
* Add Product [see](https://github.com/Axway/Grapes/wiki/Main-concepts#product)
* Add Organizations [see](https://github.com/Axway/Grapes/wiki/Main-concepts#organization)
* [Github issue #29](https://github.com/Axway/Grapes/issues/29) Add build info to modules
* [Github issue #17](https://github.com/Axway/Grapes/issues/17) Manage cache for costly requests
* [Github issue #18](https://github.com/Axway/Grapes/issues/18) Fix getting all promoted modules
* Update REST API documentation

1.3.0
-------------
* Manage Maven project configuration for Maven central deployment
* Add Sonatype OSS parent heritage

1.2.0
-------------
* Add get Corporate groupId request to retrieve all the groupId that are configured as corporate dependencies
* Add get last version of an artifact request
* Fix duplication of dependencies during the notifications
* [Github issue #8](https://github.com/Axway/Grapes/issues/8) Remove module internal dependencies from ancestor lists
* Remove module internal dependencies for module dependency lists
* Make dependency lists sortable
* Enhance module license report in data browser
* Update license resolution during module notification
* Add spinner in data browser during request processing

1.1.0
-------------
* [Github issue #7](https://github.com/Axway/Grapes/issues/7) Handle regexp resolution for licenses
* [Github issue #5](https://github.com/Axway/Grapes/issues/5) Use Jongo manual index to merge technical and functional index of DB objects
* Fix DB element duplication
* Fix "do not use" filter on Artifact objects in the web-app

1.0.0
-------------
* Creation of Grapes project
