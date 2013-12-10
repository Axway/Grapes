Story: Get Product Licenses
In order to check if all the licenses used by my product has been validated
As an anonymous user
I want to be able to display the licenses of my product's thirdparty

Scenario: Get all the licenses used by a product

Given ProductCase loaded in the database
When I look for ProductCase's licenses
Then I should see all the licenses used by the product's thirdparty used in COMPILE and PROVIDED scopes