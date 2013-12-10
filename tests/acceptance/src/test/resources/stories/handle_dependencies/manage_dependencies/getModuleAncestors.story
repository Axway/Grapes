Story: Get Module Ancestors
In order to determine who is using my module
As an anonymous user
I want to be able to display all dependency ancestor of a module providing its name and its version

Scenario: Get Compile ancestor

Given SimpleAncestorCase loaded in the database
When I look for SimpleAncestorCase's module ancestors
Then I got the SimpleAncestorCase's ancestor