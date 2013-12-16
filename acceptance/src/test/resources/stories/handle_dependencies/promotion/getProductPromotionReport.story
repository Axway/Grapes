Story: Get Product Promotion Report
In order to check a module can be promoted
As an anonymous user
I want to be able to display a report that give me a feedback about the promotion status and the steps to do to be able to promote

Scenario: Promotion report of a module that can be promoted

Given SimpleModuleCase loaded in the database
When I look for tc01Module's promotion report in version 1.0.0-SNAPSHOT
Then The report says that I can promote the module

Scenario: Get promotion report of a product with recursive option

Given ProductCase loaded in the database
When I look for tc04Module1's promotion report in version 2.1.0-SNAPSHOT
Then I see in the report the artifacts that I should not use and the module I should promote for ProductCase