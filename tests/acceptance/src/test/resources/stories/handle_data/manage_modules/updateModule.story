Story: Update Module
In order to be able to update the information of the dependency manager
As identified user
I want to be able to send a module that already exist to the dependency manager to update its information

Scenario: Update module information

Given SimpleModuleCase loaded in the database
When I send SimpleModuleCase's module adding a new artifact and changing the type of the existing one
Then I am able to check that the module has these two artifacts and that the information of the first artifact are updated
