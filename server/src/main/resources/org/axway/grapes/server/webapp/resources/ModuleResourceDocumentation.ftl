<html>
    <head>
        <meta charset="utf-8"/>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <meta name="author" content="jdcoffre"/>
        <meta name="description" content="Module Resource Documentation"/>

        <title>Module API Documentation</title>

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
            <h1>Module Resource API Documentation</h1>

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
                            <td>/module</td>
                            <td>Provide the documentation of the Module resource</td>
                        </tr>
                        <tr>
                            <td>POST</td>
                            <td>/module</td>
                            <td>Publish a module on Grapes (credentials are mandatory).</td>
                        </tr>
                        <tr>
                            <td>GET</td>
                            <td>/module/names</td>
                            <td>Provide the list of module names that are stored into Grapes.<br/>
                                Available parameter: promoted.</td>
                        </tr>
                        <tr>
                            <td>GET</td>
                            <td>/module/{name}/versions</td>
                            <td>Provide the list of versions of a specific module available Grapes.<br/>
                                Available parameter: promoted.</td>
                        </tr>
                        <tr>
                            <td>GET</td>
                            <td>/module/{name}/{version}</td>
                            <td>Display all the information the dependency has stored about the module.</td>
                        </tr>
                        <tr>
                            <td>DELETE</td>
                            <td>/module/{name}/{version}</td>
                            <td>Delete the targeted module (credentials are mandatory).</td>
                        </tr>
                        <tr>
                            <td>GET</td>
                            <td>/module/{name}/{version}/promotion</td>
                            <td>Get the promotion status of a module.</td>
                        </tr>
                        <tr>
                            <td>POST</td>
                            <td>/module/{name}/{version}/promotion</td>
                            <td>Promote the targeted module (credentials are mandatory).</td>
                        </tr>
                        <tr>
                            <td>GET</td>
                            <td>/module/{name}/{version}/promotion/doable</td>
                            <td>Return a boolean which is true if the module can be promoted.</td>
                        </tr>
                        <tr>
                            <td>GET</td>
                            <td>/module/{name}/{version}/promotion/report</td>
                            <td>Return a feedback about the promotion (steps to fulfill before performing the promotion).<br/>
                                Available parameter: fullRecursive</td>
                        </tr>
                        <tr>
                            <td>GET</td>
                            <td>/module/{name}/{version}/ancestors</td>
                            <td>Display the information about the module ancestors.<br/>
                                Available parameter: scopeComp, scopeRun, scopePro, scopeTest.</td>
                        </tr>
                        <tr>
                            <td>GET</td>
                            <td>/module/{name}/{version}/dependencies</td>
                            <td>Display the information about the module dependencies.<br/>
                                Available parameters are: fullRecursive, depth, scopeComp, scopeRun, scopePro, scopeTest, corporate, promoted, doNotUse, showScopes, showLicenses, showSources, showThirdparty, toUpdate.</td>
                        </tr>
                        <tr>
                            <td>GET</td>
                            <td>/module/{name}/{version}/dependencies/report</td>
                            <td>Provide a report about the targeted module dependencies.<br/>
                                Available parameters are: fullRecursive, depth, scopeComp, scopeRun, scopePro, scopeTest, corporate, promoted, doNotUse, showScopes, showLicenses, showSources, showThirdparty, showProviders.</td>
                        </tr>
                        <tr>
                            <td>GET</td>
                            <td>/module/{name}/{version}/licenses</td>
                            <td>Provide the list of licenses used by the third party of the module.<br/>
                                Available parameter: validated, toBeValidated.</td>
                        </tr>
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