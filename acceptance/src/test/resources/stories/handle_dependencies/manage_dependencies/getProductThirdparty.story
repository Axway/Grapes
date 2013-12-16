Story: Get Product Thirdparty
In order to check the dependencies of my project
As an anonymous user
I want to be able to display all the transitive dependencies of a module

Scenario: Get thirdparty of a product

Given ProductCase loaded in the database
When I look for ProductCase's module thirdparty
Then I got ProductCase's thirdparty