Story: Promote Module
In order to follow the module life-cycle
As identified user
I want to be able to promote an existing module

Scenario: Promote a module

Given SimpleModuleCase loaded in the database
When I promote the module using grapes' client
Then I want to be able to check the module has been promoted
