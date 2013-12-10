Story: Delete Module
In order to remove old data
As identified user
I want to delete a module

Scenario: Delete a module from the dependency manager

Given SimpleModuleCase loaded in the database
When I delete the module using grapes' client
Then SimpleModuleCase's module and its artifacts are not anymore in the database