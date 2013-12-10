Story: Get Module Dependencies
In order to get information about module dependencies
As an anonymous user
I want to be able to display all the module dependencies thanks to its name and its version

Scenario: Get the dependencies of a module

Given ModuleWithAllKindOfDependenciesCase loaded in the database
When I look for module dependencies
Then I got the ModuleWithAllKindOfDependenciesCase's dependencies