Story: Add Module
In order to be able to notify the dependency manager
As identified user
I want to be able send a module to the dependency manager

Scenario: Send a module to the dependency manager

Given SimpleModuleCase's module
When I send the module using grapes' client to the dependency manager server
Then SimpleModuleCase's module is in the database
