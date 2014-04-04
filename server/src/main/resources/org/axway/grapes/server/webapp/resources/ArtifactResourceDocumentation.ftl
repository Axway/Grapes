<html>
    <head>
        <meta charset="utf-8"/>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <meta name="author" content="jdcoffre"/>
        <meta name="description" content="Artifact Resource Documentation"/>

        <title>Artifact API Documentation</title>

        <!-- Bootstrap -->
        <link href="/public/twitter-bootstrap-2.3.2/css/bootstrap.css" rel="stylesheet"/>
		<link href="/public/twitter-bootstrap-2.3.2/css/bootstrap-responsive.css" rel="stylesheet">
		<link href="/public/twitter-bootstrap-2.3.2/css/docs.css" rel="stylesheet">

        <link rel="shortcut icon" type="image/x-icon" href="assets/img/grapes_small.gif"/>

    </head>
    <body>
        <div class="row-fluid">
            <div class="navbar navbar-inverse navbar-fixed-top">
                <div class="navbar-inner">
                    <div class="container">
                        <a class="brand active" href="/">Grapes</a>
                        <div class="nav-collapse collapse">
                            <ul class="nav">
                                <li class="">
                                <a class="dropdown-toggle" data-toggle="dropdown" href="#">Documentations</a>
                                    <ul class="dropdown-menu" role="menu" aria-labelledby="drop">
                                        <#if getOnlineDocumentation()??>
                                        <li><a tabindex="-1" href="${getOnlineDocumentation()}">Online Documentation</a></li>
                                        </#if>
                                        <li><a tabindex="-1" href="/module">Module API</a></li>
                                        <li><a tabindex="-1" href="/artifact">Artifact API</a></li>
                                        <li><a tabindex="-1" href="/license">License API</a></li>
                                    </ul>
                                </li>
                                <li class="">
                                    <a href="/sequoia">Sequoïa</a>
                                </li>
                                <li class="">
                                    <a href="/webapp">Data Browser</a>
                                </li>
                                <#if getIssueTrackerUrl()??>
                                <li class="">
                                    <a href="${getIssueTrackerUrl()}">Report an issue</a>
                                </li>
                                </#if>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
		</div>

        <div class="container-fluid">
            <h1>Artifact Resource API Documentation</h1>

            <p>
                <table class="table table-bordered">
                    <thead>
                        <tr>
                            <td>Method</td>
                            <td>Resource Path</td>
                            <td>Details</td>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>GET</td>
                            <td>/artifact</td>
                            <td>Provide the documentation of the Artifact resource</td>
                        </tr>
                        <tr>
                            <td>POST</td>
                            <td>/artifact</td>
                            <td>Publish an Artifact on Grapes (credentials are mandatory).</td>
                        </tr>
                        <tr>
                            <td>GET</td>
                            <td>/artifact/gavcs</td>
                            <td>Return a list of gavc.<br/>
                                Available parameters: corporate, promoted, groupId, artifactId, version, classifier, type.</td>
                        </tr>
                        <tr>
                            <td>GET</td>
                            <td>/artifact/{gavc}</td>
                            <td>Display all the information the dependency has stored about the artifact.</td>
                        </tr>
                        <tr>
                            <td>DELETE</td>
                            <td>/artifact/{gavc}</td>
                            <td>Delete the targeted artifact (credentials are mandatory).</td>
                        </tr>
                        <tr>
                            <td>GET</td>
                            <td>/artifact/{gavc}/versions</td>
                            <td>Display all the available versions of an artifact.</td>
                        </tr>
                        <tr>
                            <td>GET</td>
                            <td>/artifact/{gavc}/lastversion</td>
                            <td>Returns the last version of an artifact.</td>
                        </tr>
                        <tr>
                            <td>GET</td>
                            <td>/artifact/{gavc}/ancestors</td>
                            <td>Display the information about the artifact ancestors.<br/>
                                Available parameters: scopeComp, scopePro, scopeRun, scopeTest.</td>
                        </tr>
                        <tr>
                            <td>POST</td>
                            <td>/artifact/{gavc}/donotuse</td>
                            <td>Add "DO_NOT_USE" flag to the targeted artifact (credentials are mandatory).<br/>
                                Mandatory parameter: doNotUse (boolean).</td>
                        </tr>
                        <tr>
                            <td>GET</td>
                            <td>/artifact/{gavc}/donotuse</td>
                            <td>Return true if the targeted artifact is flagged with "DO_NOT_USE".</td>
                        </tr>
                        <tr>
                            <td>GET</td>
                            <td>/artifact/{gavc}/licenses</td>
                            <td>Provide the list of licenses used by the third party of the artifact.
                                Available parameters: validated, toBeValidated.</td>
                        </tr>
                        <tr>
                            <td>POST</td>
                            <td>/artifact/{gavc}/licenses</td>
                            <td>Add a license to the artifact (credentials are mandatory).<br/>
                                Mandatory parameter: licenseId.</td>
                        </tr>
                        <tr>
                            <td>DELETE</td>
                            <td>/artifact/{gavc}/licenses</td>
                            <td>Remove a license from the artifact (credentials are mandatory).<br/>
                                Mandatory parameter: licenseId.</td>
                        </tr>
                        (gavc = groupid:artifactid:version:classifier)
                    </tbody>
                </table>
            </p>
        </div><!--/.fluid-container-->

        <footer>
            <p>Grapes ${programVersion!?html}.</p>
        </footer>

        <!-- ==Javascript== -->
        <script src="/public/jquery-1.9.1/jquery.js"></script>
        <script src="/public/twitter-bootstrap-2.3.2/js/bootstrap.js"></script>

    </body>
</html>