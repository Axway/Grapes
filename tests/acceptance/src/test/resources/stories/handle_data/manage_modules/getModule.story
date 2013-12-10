Story: Get Module
In order to get information about a module
As an anonymous user
I want to be able to display all the module information thanks to its name and its version

Scenario: Searching a module that does not exist

Given A database without any module
When I look for a module
Then I got a 404 NOT FOUND exception

Scenario: Searching a module that is in the database

Given SimpleModuleCase loaded in the database
When I look for SimpleModuleCase's module
Then I got the SimpleModuleCase's module information