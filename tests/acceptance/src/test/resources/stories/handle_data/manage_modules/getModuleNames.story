Story: Get Module Names
In order to know what modules are in the database
As an anonymous user
I want to be able to display all module names

Scenario: Getting module names

Given SimpleModuleCase loaded in the database
When I look for module names
Then I got a table that contains the name of SimpleModuleCase's module