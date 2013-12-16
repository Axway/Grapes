Story: Get Product Dependency Report
In order to check the dependencies of my project
As an anonymous user
I want to be able to display a report that give me a feedback about the dependencies to update

Scenario: Get dependency report of a product

Given ProductCase loaded in the database
When I look for ProductCase's dependency report
Then I should see that module4 is not up-to-date