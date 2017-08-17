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
                                    <li><a tabindex="-1" href="/organization">Organization API</a></li>
                                    <li><a tabindex="-1" href="/product">Product API</a></li>
                                    <li><a tabindex="-1" href="/module">Module API</a></li>
                                    <li><a tabindex="-1" href="/artifact">Artifact API</a></li>
                                    <li><a tabindex="-1" href="/license">License API</a></li>
                                    <li><a tabindex="-1" href="/report">Report API</a></li>
                                    <li><a tabindex="-1" href="searchdoc">Search API</a></li>
                                </ul>
                            </li>
                            <li class="">
                                <a href="/sequoia">Sequoïa</a>
                            </li>
                            <li class="">
                                <a href="/webapp">Data Browser</a>
                            </li>
                            <li class="">
                                <a href="/search">Search</a>
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

        <header>
            <div class="container" >
                <div class="row">
                    <h1>Module REST API Documentation</h1>
                </div>
            </div>
        </header>

        <div class="container">
            <div class="row">
                <div class="span4 bs-docs-sidebar">
                    <ul class="nav nav-list bs-docs-sidenav" data-spy="affix" data-offset-top="80">
                        <li class=""><a data-toggle="collapse" data-target="#accordion1" href="#module"><i class="icon-chevron-right"></i> Resource documentation</a></li>
                        <li class=""><a data-toggle="collapse" data-target="#accordion1" href="#module"><i class="icon-chevron-right"></i> Add/update a module</a></li>
                        <li class=""><a data-toggle="collapse" data-target="#accordion2" href="#module-names"><i class="icon-chevron-right"></i> Get all names</a></li>
                        <li class=""><a data-toggle="collapse" data-target="#accordion3" href="#module-versions"><i class="icon-chevron-right"></i> Get all versions</a></li>
                        <li class=""><a data-toggle="collapse" data-target="#accordion4" href="#module-target"><i class="icon-chevron-right"></i> Get a module</a></li>
                        <li class=""><a data-toggle="collapse" data-target="#accordion4" href="#module-target"><i class="icon-chevron-right"></i> Delete a module</a></li>
                        <li class=""><a data-toggle="collapse" data-target="#accordion5" href="#module-organization"><i class="icon-chevron-right"></i> Get a module organization</a></li>
                        <li class=""><a data-toggle="collapse" data-target="#accordion6" href="#module-ancestors"><i class="icon-chevron-right"></i> Who is using my module?</a></li>
                        <li class=""><a data-toggle="collapse" data-target="#accordion7" href="#module-dependencies"><i class="icon-chevron-right"></i> Module dependencies</a></li>
                        <li class=""><a data-toggle="collapse" data-target="#accordion8" href="#module-licenses"><i class="icon-chevron-right"></i> Get module licenses</a></li>
                        <li class=""><a data-toggle="collapse" data-target="#accordion9" href="#module-promotion"><i class="icon-chevron-right"></i> Get promotion status</a></li>
                        <li class=""><a data-toggle="collapse" data-target="#accordion9" href="#module-promotion"><i class="icon-chevron-right"></i> Promote a module</a></li>
                        <li class=""><a data-toggle="collapse" data-target="#accordion10" href="#module-promotion-doable"><i class="icon-chevron-right"></i> Can it be promoted?</a></li>
                        <li class=""><a data-toggle="collapse" data-target="#accordion11" href="#module-buildinfo"><i class="icon-chevron-right"></i> Get/Update build info</a></li>
                    </ul>
                </div>
                <div class="span8">
                    <section id="module">
                        <a class="page-header btn-link" data-toggle="collapse" data-target="#accordion1">
                            <h2>@ /module</h2>
                        </a>
                        <div id="accordion1" class="collapse">
                            <ul>
                                <li>
                                    <h3>GET</h3>
                                    <ul>
                                        <li>Get Module resource documentation</li>
                                        <li>Returns HTML</li>
                                    </ul>
                                </li>
                                <li>
                                    <h3>POST</h3>
                                    <ul>
                                        <li>Add/update module</li>
                                        <li>Expects a JSON module in the request content</li>
                                        <li>Return status 201 if ok 400 if the Json does not suits the model</li>
                                        <li>Json Module example:
                                            <pre>${getModuleJsonModel()}</pre>
                                        </li>
                                        <li>Json Artifact example:
                                            <pre>${getArtifactJsonModel()}</pre>
                                        </li>
                                        <li>Json Dependency example:
                                            <pre>${getDependencyJsonModel()}</pre>
                                        </li>
                                        <li>Available dependency scopes:
                                            <pre>${getScopes()}</pre>
                                        </li>
                                    </ul>
                                </li>
                            </ul>
                        </div>
                    </section>
                    <section id="module-names">
                        <a class="page-header btn-link" data-toggle="collapse" data-target="#accordion2">
                            <h2>@ /module/names</h2>
                        </a>
                        <div id="accordion2" class="collapse">
                            <ul>
                                <li>
                                    <h3>GET</h3>
                                    <ul>
                                        <li>Get modules names</li>
                                        <li>Returns HTML view or a Json list of string</li>
                                        <li>
                                            Optional parameters:
                                            <br/>
                                            <table class="table table-bordered table-hover" style="font-size:90%;margin-top:8px;">
                                                <thead>
                                                    <tr>
                                                        <td><strong>Parameter</strong></td>
                                                        <td><strong>Default Value</strong></td>
                                                        <td><strong>Description</strong></td>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                <tr>
                                                    <td>promoted</td>
                                                    <td>null</td>
                                                    <td>Filter the modules regarding their promotion status</td>
                                                </tr>
                                                <tr>
                                                    <td>organization</td>
                                                    <td>null</td>
                                                    <td>Filter the modules regarding their organization</td>
                                                </tr>
                                                </tbody>
                                            </table>
                                        </li>
                                    </ul>
                                </li>
                            </ul>
                        </div>
                    </section>
                    <section id="module-versions">
                        <a class="page-header btn-link" data-toggle="collapse" data-target="#accordion3">
                            <h2>@ /module/{name}/versions</h2>
                        </a>
                        <div id="accordion3" class="collapse">
                            <ul>
                                <li>
                                    <h3>GET</h3>
                                    <ul>
                                        <li>Get the versions of a module</li>
                                        <li>Returns HTML view or a Json list of string</li>
                                        <li>
                                            Optional parameters:
                                            <br/>
                                            <table class="table table-bordered table-hover" style="font-size:90%;margin-top:8px;">
                                                <thead>
                                                <tr>
                                                    <td><strong>Parameter</strong></td>
                                                    <td><strong>Default Value</strong></td>
                                                    <td><strong>Description</strong></td>
                                                </tr>
                                                </thead>
                                                <tbody>
                                                <tr>
                                                    <td>promoted</td>
                                                    <td>null</td>
                                                    <td>Filter the modules regarding their promotion status</td>
                                                </tr>
                                                <tr>
                                                    <td>organization</td>
                                                    <td>null</td>
                                                    <td>Filter the modules regarding their organization</td>
                                                </tr>
                                                </tbody>
                                            </table>
                                        </li>
                                    </ul>
                                </li>
                            </ul>
                        </div>
                    </section>
                    <section id="module-target">
                        <a class="page-header btn-link" data-toggle="collapse" data-target="#accordion4">
                            <h2>@ /module/{name}/{version}</h2>
                        </a>
                        <div id="accordion4" class="collapse">
                            <ul>
                                <li>
                                    <h3>GET</h3>
                                    <ul>
                                        <li>Get a module</li>
                                        <li>
                                            Returns HTML view or a module in Json
                                            <pre>${getModuleJsonModel()}</pre>
                                        </li>
                                    </ul>
                                </li>
                                <li>
                                    <h3>DELETE</h3>
                                    <ul>
                                        <li>Remove a module</li>
                                        <li>Return status 200 if ok</li>
                                    </ul>
                                </li>
                            </ul>
                        </div>
                    </section>
                    <section id="module-organization">
                        <a class="page-header btn-link" data-toggle="collapse" data-target="#accordion5">
                            <h2>@ /module/{name}/{version}/organization</h2>
                        </a>
                        <div id="accordion5" class="collapse">
                            <ul>
                                <li>
                                    <h3>GET</h3>
                                    <ul>
                                        <li>Get the organization of a module</li>
                                        <li>
                                            Returns HTML view or an organization in Json
                                            <pre>${getOrganizationJsonModel()}</pre>
                                        </li>
                                    </ul>
                                </li>
                            </ul>
                        </div>
                    </section>
                    <section id="module-ancestors">
                        <a class="page-header btn-link" data-toggle="collapse" data-target="#accordion6">
                            <h2>@ /module/{name}/{version}/ancestors</h2>
                        </a>
                        <div id="accordion6" class="collapse">
                            <ul>
                                <li>
                                    <h3>GET</h3>
                                    <ul>
                                        <li>Get projects that use a module</li>
                                        <li>Returns HTML view or a list of dependencies in Json</li>
                                        <li>
                                            Optional parameters:
                                            <br/>
                                            <table class="table table-bordered table-hover" style="font-size:90%;margin-top:8px;">
                                                <thead>
                                                <tr>
                                                    <td><strong>Parameter</strong></td>
                                                    <td><strong>Default Value</strong></td>
                                                    <td><strong>Description</strong></td>
                                                </tr>
                                                </thead>
                                                <tbody>
                                                <tr>
                                                    <td>scopeComp</td>
                                                    <td>true</td>
                                                    <td>Includes dependencies with the scope COMPILE</td>
                                                </tr>
                                                <tr>
                                                    <td>scopePro</td>
                                                    <td>true</td>
                                                    <td>Includes dependencies with the scope PROVIDED</td>
                                                </tr>
                                                <tr>
                                                    <td>scopeRun</td>
                                                    <td>false</td>
                                                    <td>Includes dependencies with the scope RUNTIME</td>
                                                </tr>
                                                <tr>
                                                    <td>scopeTest</td>
                                                    <td>true</td>
                                                    <td>Includes dependencies with the scope TEST</td>
                                                </tr>
                                                <tr>
                                                    <td>showScopes</td>
                                                    <td>true</td>
                                                    <td>Add or remove the Scope column in HTML results</td>
                                                </tr>
                                                <tr>
                                                    <td>showSources</td>
                                                    <td>true</td>
                                                    <td>Add or remove the Source column in HTML results</td>
                                                </tr>
                                                </tbody>
                                            </table>
                                        </li>
                                    </ul>
                                </li>
                            </ul>
                        </div>
                    </section>
                    <section id="module-dependencies">
                        <a class="page-header btn-link" data-toggle="collapse" data-target="#accordion7">
                            <h2>@ /module/{name}/{version}/dependencies</h2>
                        </a>
                        <div id="accordion7" class="collapse">
                            <ul>
                                <li>
                                    <h3>GET</h3>
                                    <ul>
                                        <li>Get module dependencies</li>
                                        <li>Returns HTML view or a list of dependencies in Json</li>
                                        <li>
                                            Optional parameters:
                                            <br/>
                                            <table class="table table-bordered table-hover" style="font-size:90%;margin-top:8px;">
                                                <thead>
                                                <tr>
                                                    <td><strong>Parameter</strong></td>
                                                    <td><strong>Default Value</strong></td>
                                                    <td><strong>Description</strong></td>
                                                </tr>
                                                </thead>
                                                <tbody>
                                                <tr>
                                                    <td>fullRecursive</td>
                                                    <td>false</td>
                                                    <td>Returns the dependencies of the entire corporate tree</td>
                                                </tr>
                                                <tr>
                                                    <td>depth</td>
                                                    <td>null</td>
                                                    <td>Returns the dependencies of the corporate tree until provided depth</td>
                                                </tr>
                                                <tr>
                                                    <td>scopeComp</td>
                                                    <td>true</td>
                                                    <td>Includes dependencies with the scope COMPILE</td>
                                                </tr>
                                                <tr>
                                                    <td>scopePro</td>
                                                    <td>true</td>
                                                    <td>Includes dependencies with the scope PROVIDED</td>
                                                </tr>
                                                <tr>
                                                    <td>scopeRun</td>
                                                    <td>false</td>
                                                    <td>Includes dependencies with the scope RUNTIME</td>
                                                </tr>
                                                <tr>
                                                    <td>scopeTest</td>
                                                    <td>true</td>
                                                    <td>Includes dependencies with the scope TEST</td>
                                                </tr>
                                                <tr>
                                                    <td>showCorporate</td>
                                                    <td>true</td>
                                                    <td>Includes the corporate dependencies in the report</td>
                                                </tr>
                                                <tr>
                                                    <td>showThirdparty</td>
                                                    <td>false</td>
                                                    <td>Includes the third party libraries in the report</td>
                                                </tr>
                                                <tr>
                                                    <td>doNotUse</td>
                                                    <td>null</td>
                                                    <td>Filters the dependencies regarding the artifact field DO_NOT_USE</td>
                                                </tr>
                                                <tr>
                                                    <td>showScopes</td>
                                                    <td>true</td>
                                                    <td>Add or remove the Scope column in HTML results</td>
                                                </tr>
                                                <tr>
                                                    <td>showLicenses</td>
                                                    <td>false</td>
                                                    <td>Add or remove the License column in HTML results</td>
                                                </tr>
                                                <tr>
                                                    <td>showSources</td>
                                                    <td>true</td>
                                                    <td>Add or remove the Source column in HTML results</td>
                                                </tr>
                                                </tbody>
                                            </table>
                                        </li>
                                    </ul>
                                </li>
                            </ul>
                        </div>
                    </section>
                    <section id="module-licenses">
                        <a class="page-header btn-link" data-toggle="collapse" data-target="#accordion8">
                            <h2>@ /module/{name}/{version}/licenses</h2>
                        </a>
                        <div id="accordion8" class="collapse">
                            <ul>
                                <li>
                                    <h3>GET</h3>
                                    <ul>
                                        <li>List the license(s) of a module</li>
                                        <li>
                                            Returns HTML view or a list of licenses in Json
                                            <pre>${getLicenseJsonModel()}</pre>
                                        </li>
                                    </ul>
                                </li>
                            </ul>
                        </div>
                    </section>
                    <section id="module-promotion">
                        <a class="page-header btn-link" data-toggle="collapse" data-target="#accordion9">
                            <h2>@ /module/{name}/{version}/promotion</h2>
                        </a>
                        <div id="accordion9" class="collapse">
                            <ul>
                                <li>
                                    <h3>GET</h3>
                                    <ul>
                                        <li>Get the promotion status of a module</li>
                                        <li>Returns a boolean</li>
                                    </ul>
                                </li>
                                <li>
                                    <h3>POST</h3>
                                    <ul>
                                        <li>Promote a module</li>
                                        <li>Return status 200 if ok</li>
                                    </ul>
                                </li>
                            </ul>
                        </div>
                    </section>
                    <section id="module-promotion-doable">
                        <a class="page-header btn-link" data-toggle="collapse" data-target="#accordion10">
                            <h2>@ /module/{name}/{version}/promotion/doable</h2>
                        </a>
                        <div id="accordion10" class="collapse">
                            <ul>
                                <li>
                                    <h3>GET</h3>
                                    <ul>
                                        <li>Checks if a module can be promoted</li>
                                        <li>Returns a boolean</li>
                                    </ul>
                                </li>
                            </ul>
                        </div>
                    </section>
                    <section id="module-promotion-report">
                        <a class="page-header btn-link" data-toggle="collapse" data-target="#accordion11">
                            <h2>@ /module/{name}/{version}/promotion/report</h2>
                        </a>
                        <div id="accordion11" class="collapse">
                            <ul>
                                <li>
                                    <h3>GET</h3>
                                    <ul>
                                        <li>Get module promotion details</li>
                                        <li>
                                        	Returns HTML view or an module promotion details in Json. The structure of the
                                            response contains a boolean field indicating if the module is promotable and
                                            a set of error messages representing the list of validation errors.
                                        	<pre>${getPromotionDetailsJsonModel()}</pre>
                                        </li>
                                    </ul>
                                </li>
                            </ul>
                        </div>
                    </section>
                    <section id="module-buildinfo">
                        <a class="page-header btn-link" data-toggle="collapse" data-target="#accordion12">
                            <h2>@ /module/{name}/{version}/buildinfo</h2>
                        </a>
                        <div id="accordion12" class="collapse">
                            <ul>
                                <li>
                                    <h3>GET</h3>
                                    <ul>
                                        <li>Get module's build info</li>
                                        <li>Returns a Json Map&lt;String,String&gt;</li>
                                    </ul>
                                </li>
                                <li>
                                    <h3>POST</h3>
                                    <ul>
                                        <li>Update a build info</li>
                                        <li>If existing information are posted, they will be overrided</li>
                                        <li>Return status 200 if ok</li>
                                    </ul>
                                </li>
                            </ul>
                        </div>
                    </section>
                </div>
            </div>
        </div>

        <footer class="text-right" style="margin-top:20px">
            <p>Grapes ${programVersion!?html} </p>
        </footer>

        <!-- ==Javascript== -->
        <script src="/public/jquery-1.9.1/jquery.js"></script>
        <script src="/public/twitter-bootstrap-2.3.2/js/bootstrap.js"></script>

    </body>
</html>