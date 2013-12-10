Grapes
=========

Grapes tracks the dependencies of your software production:

  - Use HTTP REST API or a Grapes client to send your dependencies to Grapes
  - Get feedback on the dependencies of a project: versions, origin, known issues...
  - List all the licenses of your third party libraries


History
-----------
Grapes used to be an internal CI tool of [Axway] to govern the flow of dependencies. The project started in 2011 and has been made open source in 2013.

Requirements
-----------
* Grapes is in Java6
* It is compiled using Maven3

Tech
-----------

Written in Java, Grapes uses a number of other open source projects :

* [Dropwizard] - awesome out-of-the-box tool to build web applications
* [Jongo] - a super fast way to make requests on MongoDb using the MongoShell language
* [Twitter Bootstrap] - great UI boilerplate for modern web apps
* [jQuery] - JavaScript library 

Documentation
-----------

More documentation is available on the Grapes-server maven site.

```sh
git clone [git-repo-url] grapes-server
cd grapes-server
mvn site
```
Then open *target/site/index.html* in your favorite web-browser.

License
-----------

Under the Apache 2 license
  
  [Axway]: http://www.axway.com/
  [Dropwizard]: http://dropwizard.codahale.com/
  [Jongo]: http://jongo.org/
  [Twitter Bootstrap]: http://twitter.github.com/bootstrap/
  [jQuery]: http://jquery.com  
