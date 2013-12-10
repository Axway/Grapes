<img src="./authentication.png" class="pull-left" style="padding:30px"/>
<span class="page-header">
<h1>Authentication management</h1>
</span>
To protect grapes database and configuration an authentication policy has been defined with different kinds of user: <a href="#admin">administrator</a>, <a href="#contributors">contributors</a> and <a href="#viewers">viewers</a>.
Basic HTTP authentication is used during the authentication.

<p class="clearfix"/>

<h2 id="admin">Administrator</h2>

The administrator is the only one user able to modify at runtime the configuration of Grapes.
The administrator is able to:

 * create, update and attribute roles to contributors
 * set Grapes in maintenance mode
 * add or remove corporate groupIds
 * perform a garbage collection

The administrator user is created while running Grapes. The name and the password are defined in the server configuration file ([see the server configuration|quick-start.html]).

	http:
      adminUsername: test
      adminPassword: test

####Create & update contributor users

<table>
    <tr>
        <td>Method</td>
        <td>URL</td>
        <td>Query Parameters</td>
    </tr>
    <tr>
        <td>POST</td>
        <td>http://dm-url:admin-port/tasks/addUser</td>
        <td><ul><li><p><strong>user:</strong> name of the created or updated contributor</p></li><li><p><strong>password:</strong> password of the created or updated user</p></li></ul></td>
    </tr>
</table>

####Attribute a role to a contributor

<table>
    <tr>
        <td>Method</td>
        <td>URL</td>
        <td>Query Parameters</td>
    </tr>
    <tr>
        <td>POST</td>
        <td>http://dm-url:admin-port/tasks/addRole</td>
        <td><ul><li><p><strong>user:</strong> name an existing contributor</p></li><li><p><strong>role:</strong> role to give to the user</p></li></ul></td>
    </tr>
</table>

####Remove a role to a contributor

<table>
    <tr>
        <td>Method</td>
        <td>URL</td>
        <td>Query Parameters</td>
    </tr>
    <tr>
        <td>POST</td>
        <td>http://dm-url:admin-port/tasks/removeRole</td>
        <td><ul><li><p><strong>user:</strong> name an existing contributor</p></li><li><p><strong>role:</strong> role to remove to the user</p></li></ul></td>
    </tr>
</table>

####Add a corporate groupId

<table>
    <tr>
        <td>Method</td>
        <td>URL</td>
        <td>Query Parameters</td>
    </tr>
    <tr>
        <td>POST</td>
        <td>http://dm-url:admin-port/tasks/addCorporateGroupId</td>
        <td><ul><li><p><strong>groupId:</strong> groupId considered as internal production</p></li></ul></td>
    </tr>
</table>

####Remove a corporate groupId

<table>
    <tr>
        <td>Method</td>
        <td>URL</td>
        <td>Query Parameters</td>
    </tr>
    <tr>
        <td>POST</td>
        <td>http://dm-url:admin-port/tasks/removeCorporateGroupId</td>
        <td><ul><li><p><strong>groupId:</strong> groupId to remove from the corporate groupId list</p></li></ul></td>
    </tr>
</table>

####Set or disable Grapes in maintenance mode

<table>
    <tr>
        <td>Method</td>
        <td>URL</td>
    </tr>
    <tr>
        <td>POST</td>
        <td>http://dm-url:admin-port/tasks/maintenance</td>
    </tr>
</table>

####Perform a java garbage collection

<table>
    <tr>
        <td>Method</td>
        <td>URL</td>
    </tr>
    <tr>
        <td>POST</td>
        <td>http://dm-url:admin-port/tasks/gc</td>
    </tr>
</table>

<h2 id="contributors">Contributors</h2>

Contributor users has to be created by the <a href="#admin">administrator</a>. The aim of this profile is to enhance the information stored in Grapes' database. Users with such profile interacts with Grapes via the REST API or the Data-Browser.

A contributor can have different roles:

* <strong>dependency_notifier:</strong> create and update modules and artifacts
* <strong>data_updater:</strong> update information fields of artifacts, create and update license information
* <strong>data_deleter:</strong> able delete database objects
* <strong>license_checker:</strong> approve or reject licenses
* <strong>artifact_checker:</strong> able to update "DO_NOT_USE" artifact field

These roles are attributed by the administrator.

<h2 id="viewers">Viewers</h2>

The viewers are anonymous users that can get information from Grapes via the REST API, the Data-Browser or via Sequoia.