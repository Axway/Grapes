
<img src="./grapes_docs.svg" width="75%" style="padding:30px"/>

Grapes tracks the dependencies of your software production.

- Use HTTP REST API or simply use a Grapes client in your CI to send the information to Grapes
- Get feedback on the dependencies of a project: versions, origin, known issues...
- List all the licenses of your third party libraries

<blockquote class="pull-right">
<p>Philosophy</p>
<small>To control your production,<br/>
Identify your dependencies.</small>
</blockquote>

<p class="clearfix"/>

History
-----------
Grapes used to be an internal CI tool of [Axway] to govern the flow of dependencies. The project started in 2011 and has been made open source in 2013.

Tech
-----------

Written in Java, Grapes uses a number of other open source projects :

* [Dropwizard] - awesome out-of-the-box tool to build web applications
* [Jongo] - a super fast way to make requests on MongoDb using the MongoShell language
* [Twitter Bootstrap] - great UI boilerplate for modern web apps
* [jQuery] - JavaScript library

Documentation
-----------

|-----------------------------------------------------------|---------|
| [Quick start](user_doc/quick-start.html)					| Minimal information to install and start Grapes |
| [Authentication management](user_doc/authentication.html)	| Authentication and roles policy |
| [Grapes clients](tech_doc/clients.html)			        | Grapes clients |
| [Grapes client specification](tech_doc/clients-specs.html)| Specifications to create a new Grapes client |
<br/>
<br/>

License
-----------

Under the Apache 2 license

  [Axway]: http://www.axway.com/
  [Dropwizard]: http://dropwizard.codahale.com/
  [Jongo]: http://jongo.org/
  [Twitter Bootstrap]: http://twitter.github.com/bootstrap/
  [jQuery]: http://jquery.com