Story: Get Module Versions
In order to know what are the available versions of a module
As an anonymous user
I want to be able to display all the versions of a module by providing its name

Scenario: Getting module names

Given SimpleModuleCase loaded in the database
When I look for SimpleModuleCase's module versions providing its name
Then I got a table that contains the version of SimpleModuleCase's module