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
                                        <li><a tabindex="-1" href="/organization">Organization API</a></li>
                                        <li><a tabindex="-1" href="/product">Product API</a></li>
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



        <header>
            <div class="container" >
                <div class="row">
                    <h1>Artifact REST API Documentation</h1>
                </div>
            </div>
        </header>

        <div class="container">
        <div class="row">
        <div class="span4 bs-docs-sidebar">
            <ul class="nav nav-list bs-docs-sidenav" data-spy="affix" data-offset-top="80">
                <li class=""><a data-toggle="collapse" data-target="#accordion1" href="#artifact"><i class="icon-chevron-right"></i> Resource documentation</a></li>
                <li class=""><a data-toggle="collapse" data-target="#accordion1" href="#artifact"><i class="icon-chevron-right"></i> Add/update an artifact</a></li>
                <li class=""><a data-toggle="collapse" data-target="#accordion2" href="#artifact-gavcs"><i class="icon-chevron-right"></i> List all artifact ids</a></li>
                <li class=""><a data-toggle="collapse" data-target="#accordion3" href="#artifact-promotion"><i class="icon-chevron-right"></i> Check promotion status</a></li>
                <li class=""><a data-toggle="collapse" data-target="#accordion4" href="#artifact-versions"><i class="icon-chevron-right"></i> List all versions of an artifact</a></li>
                <li class=""><a data-toggle="collapse" data-target="#accordion5" href="#artifact-last-version"><i class="icon-chevron-right"></i> Last version of an artifact</a></li>
                <li class=""><a data-toggle="collapse" data-target="#accordion6" href="#artifact-target"><i class="icon-chevron-right"></i> Get an artifact</a></li>
                <li class=""><a data-toggle="collapse" data-target="#accordion6" href="#artifact-target"><i class="icon-chevron-right"></i> Remove an artifact</a></li>
                <li class=""><a data-toggle="collapse" data-target="#accordion7" href="#artifact-module"><i class="icon-chevron-right"></i> Get artifact module</a></li>
                <li class=""><a data-toggle="collapse" data-target="#accordion8" href="#artifact-organization"><i class="icon-chevron-right"></i> Get artifact organization</a></li>
                <li class=""><a data-toggle="collapse" data-target="#accordion9" href="#artifact-ancestors"><i class="icon-chevron-right"></i> Who use this artifact?</a></li>
                <li class=""><a data-toggle="collapse" data-target="#accordion10" href="#artifact-licenses"><i class="icon-chevron-right"></i> Get artifact licenses</a></li>
                <li class=""><a data-toggle="collapse" data-target="#accordion10" href="#artifact-licenses"><i class="icon-chevron-right"></i> Add artifact license</a></li>
                <li class=""><a data-toggle="collapse" data-target="#accordion10" href="#artifact-licenses"><i class="icon-chevron-right"></i> Remove artifact license</a></li>
                <li class=""><a data-toggle="collapse" data-target="#accordion11" href="#artifact-notuse"><i class="icon-chevron-right"></i> DO_NOT_USE flag</a></li>
            </ul>
        </div>
        <div class="span8">
        <section id="artifact">
            <a class="page-header btn-link" data-toggle="collapse" data-target="#accordion1">
                <h2>@ /artifact</h2>
            </a>
            <div id="accordion1" class="collapse">
                <ul>
                    <li>
                        <h3>GET</h3>
                        <ul>
                            <li>Get Artifact resource documentation</li>
                            <li>Returns HTML</li>
                        </ul>
                    </li>
                    <li>
                        <h3>POST</h3>
                        <ul>
                            <li>Add/update artifact</li>
                            <li>Expects a JSON artifact in the request content</li>
                            <li>Return status 201 if ok 400 if the Json does not suits the model</li>
                            <li>Json Artifact example:
                                <pre>${getArtifactJsonModel()}</pre>
                            </li>
                        </ul>
                    </li>
                </ul>
            </div>
        </section>
        <section id="artifact-gavcs">
            <a class="page-header btn-link" data-toggle="collapse" data-target="#accordion2">
                <h2>@ /artifact/gavcs</h2>
            </a>
            <div id="accordion2" class="collapse">
                <ul>
                    <li>
                        <h3>GET</h3>
                        <ul>
                            <li>Get artifact gavcs</li>
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
                                        <td>groupId</td>
                                        <td>null</td>
                                        <td>Filter the artifacts regarding their groupid</td>
                                    </tr>
                                    <tr>
                                        <td>artifactId</td>
                                        <td>null</td>
                                        <td>Filter the artifacts regarding their artifactId</td>
                                    </tr>
                                    <tr>
                                        <td>version</td>
                                        <td>null</td>
                                        <td>Filter the artifacts regarding their version</td>
                                    </tr>
                                    <tr>
                                        <td>classifier</td>
                                        <td>null</td>
                                        <td>Filter the artifacts regarding their classifier</td>
                                    </tr>
                                    <tr>
                                        <td>type</td>
                                        <td>null</td>
                                        <td>Filter the artifacts regarding their type</td>
                                    </tr>
                                    </tbody>
                                </table>
                            </li>
                        </ul>
                    </li>
                </ul>
            </div>
        </section>
        <section id="artifact-promotion">
            <a class="page-header btn-link" data-toggle="collapse" data-target="#accordion3">
                <h2>@ /artifact/isPromoted</h2>
            </a>
            <div id="accordion3" class="collapse">
                <ul>
                    <li>
                        <h3>GET</h3>
                        <ul>
                            <li>Checks for the promotion state of an artifact based on the SHA256 checksum</li>
                            <li>Performs two kinds of verifications: if the checksum is present in the database, and, if it is promoted </li>
                            <li>It returns JSON text only
                            	<pre>${getArtifactPromtotionResponseMessage()}</pre>
                            </li>
                            <li>Return status 400 if input is not correct (e.g. any field is missing or empty or SHA256 hash length is not 64)</li>
                            <li>Return status 422 if validation type is not supported</li>
                            <li>Return status 404 if Artifact not found</li>
                            <li>Return status 200 if Artifact is promoted or not promoted</li>
                            <li>
                                Mandatory parameter:
                                <br/>
                                <table class="table table-bordered table-hover" style="font-size:90%;margin-top:8px;">
                                    <thead>
                                    <tr>
                                        <td><strong>Parameter</strong></td>
                                        <td><strong>Description</strong></td>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <tr>
                                        <td>user</td>
                                        <td>User who is requesting</td>
                                    </tr>
                                    <tr>
                                        <td>stage</td>
                                        <td>0(for uploading) or 1(for publishing)</td>
                                    </tr>
                                    <tr>
                                        <td>name</td>
                                        <td>Name of file</td>
                                    </tr>
                                    <tr>
                                        <td>sha256</td>
                                        <td>File checksum (SHA-256 hash)</td>
                                    </tr>
                                    <tr>
                                        <td>type</td>
                                        <td>Type of file. Supported types are : [ 
                                						<#list getArtifactValidationTypes() as type>
						                                ${type}<#if type_has_next>, </#if>
						                                </#list> ]
                                		</td>
                                    </tr>
                                    </tbody>
                                </table>
                            </li>
                        </ul>
                    </li>
                </ul>
            </div>
        </section>
        <section id="artifact-versions">
            <a class="page-header btn-link" data-toggle="collapse" data-target="#accordion4">
                <h2>@ /artifact/{gavc}/versions</h2>
            </a>
            <div id="accordion4" class="collapse">
                <ul>
                    <li>
                        <h3>GET</h3>
                        <ul>
                            <li>Get the versions of an artifact</li>
                            <li>Returns HTML view or a Json list of string</li>
                        </ul>
                        <p style="font-style:italic">gavc = groupid:artifactid:version:classifier:extension</p>
                    </li>
                </ul>
            </div>
        </section>
        <section id="artifact-last-version">
            <a class="page-header btn-link" data-toggle="collapse" data-target="#accordion5">
                <h2>@ /artifact/{gavc}/lastversion</h2>
            </a>
            <div id="accordion5" class="collapse">
                <ul>
                    <li>
                        <h3>GET</h3>
                        <ul>
                            <li>Get the latest version of an artifact</li>
                            <li>Returns a string</li>
                        </ul>
                        <p style="font-style:italic">gavc = groupid:artifactid:version:classifier:extension</p>
                    </li>
                </ul>
            </div>
        </section>
        <section id="artifact-target">
            <a class="page-header btn-link" data-toggle="collapse" data-target="#accordion6">
                <h2>@ /artifact/{gavc}</h2>
            </a>
            <div id="accordion6" class="collapse">
                <ul>
                    <li>
                        <h3>GET</h3>
                        <ul>
                            <li>Get an artifact</li>
                            <li>
                                Returns HTML view or a Json artifact
                                <pre>${getArtifactJsonModel()}</pre>
                            </li>
                        </ul>
                        <p style="font-style:italic">gavc = groupid:artifactid:version:classifier:extension</p>
                    </li>
                    <li>
                        <h3>DELETE</h3>
                        <ul>
                            <li>Remove an artifact</li>
                            <li>Return status 200 if ok</li>
                        </ul>
                        <p style="font-style:italic">gavc = groupid:artifactid:version:classifier:extension</p>
                    </li>
                </ul>
            </div>
        </section>
        <section id="artifact-module">
            <a class="page-header btn-link" data-toggle="collapse" data-target="#accordion7">
                <h2>@ /artifact/{gavc}/module</h2>
            </a>
            <div id="accordion7" class="collapse">
                <ul>
                    <li>
                        <h3>GET</h3>
                        <ul>
                            <li>Get the module of an artifact</li>
                            <li>Returns HTML view or a module in Json</li>
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
                        <p style="font-style:italic">gavc = groupid:artifactid:version:classifier:extension</p>
                    </li>
                </ul>
            </div>
        </section>
        <section id="artifact-organization">
            <a class="page-header btn-link" data-toggle="collapse" data-target="#accordion8">
                <h2>@ /artifact/{gavc}/organization</h2>
            </a>
            <div id="accordion8" class="collapse">
                <ul>
                    <li>
                        <h3>GET</h3>
                        <ul>
                            <li>Get the organization of an artifact</li>
                            <li>
                                Returns HTML view or an organization in Json
                                <pre>${getOrganizationJsonModel()}</pre>
                            </li>
                        </ul>
                        <p style="font-style:italic">gavc = groupid:artifactid:version:classifier:extension</p>
                    </li>
                </ul>
            </div>
        </section>
        <section id="artifact-ancestors">
            <a class="page-header btn-link" data-toggle="collapse" data-target="#accordion9">
                <h2>@ /artifact/{gavc}/ancestors</h2>
            </a>
            <div id="accordion9" class="collapse">
                <ul>
                    <li>
                        <h3>GET</h3>
                        <ul>
                            <li>Get projects that use an artifact</li>
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
                        <p style="font-style:italic">gavc = groupid:artifactid:version:classifier:extension</p>
                    </li>
                </ul>
            </div>
        </section>
        <section id="artifact-licenses">
            <a class="page-header btn-link" data-toggle="collapse" data-target="#accordion10">
                <h2>@ /artifact/{gavc}/licenses</h2>
            </a>
            <div id="accordion10" class="collapse">
                <ul>
                    <li>
                        <h3>GET</h3>
                        <ul>
                            <li>List the license(s) of an artifact</li>
                            <li>
                                Returns HTML view or a list of licenses in Json
                                <pre>${getLicenseJsonModel()}</pre>
                            </li>
                        </ul>
                        <p style="font-style:italic">gavc = groupid:artifactid:version:classifier:extension</p>
                    </li>
                    <li>
                        <h3>POST</h3>
                        <ul>
                            <li>Add a license to an Artifact</li>
                            <li>Return status 200 if ok</li>
                            <li>
                                Mandatory parameter:
                                <br/>
                                <table class="table table-bordered table-hover" style="font-size:90%;margin-top:8px;">
                                    <thead>
                                    <tr>
                                        <td><strong>Parameter</strong></td>
                                        <td><strong>Description</strong></td>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <tr>
                                        <td>licenseId</td>
                                        <td>The license id to add to the artifact</td>
                                    </tr>
                                    </tbody>
                                </table>
                            </li>
                        </ul>
                        <p style="font-style:italic">gavc = groupid:artifactid:version:classifier:extension</p>
                    </li>
                    <li>
                        <h3>DELETE</h3>
                        <ul>
                            <li>Remove a license from an Artifact</li>
                            <li>Return status 200 if ok</li>
                            <li>
                                Mandatory parameter:
                                <br/>
                                <table class="table table-bordered table-hover" style="font-size:90%;margin-top:8px;">
                                    <thead>
                                    <tr>
                                        <td><strong>Parameter</strong></td>
                                        <td><strong>Description</strong></td>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <tr>
                                        <td>licenseId</td>
                                        <td>The license id to remove from the artifact</td>
                                    </tr>
                                    </tbody>
                                </table>
                            </li>
                        </ul>
                        <p style="font-style:italic">gavc = groupid:artifactid:version:classifier:extension</p>
                    </li>
                </ul>
            </div>
        </section>
        <section id="artifact-notuse">
            <a class="page-header btn-link" data-toggle="collapse" data-target="#accordion11">
                <h2>@ /artifact/{gavc}/donotuse</h2>
            </a>
            <div id="accordion11" class="collapse">
                <ul>
                    <li>
                        <h3>GET</h3>
                        <ul>
                            <li>Get the DO_NOT_USE flag of an artifact</li>
                            <li>Returns a boolean</li>
                        </ul>
                        <p style="font-style:italic">gavc = groupid:artifactid:version:classifier:extension</p>
                    </li>
                    <li>
                        <h3>POST</h3>
                        <ul>
                            <li>Set the DO_NOT_USE flag of an artifact</li>
                            <li>Return status 200 if ok</li>
                            <li>
                                Mandatory parameter:
                                <br/>
                                <table class="table table-bordered table-hover" style="font-size:90%;margin-top:8px;">
                                    <thead>
                                    <tr>
                                        <td><strong>Parameter</strong></td>
                                        <td><strong>Description</strong></td>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <tr>
                                        <td>doNotUse</td>
                                        <td>The boolean to set in the DO_NOT_USE flag of the artifact</td>
                                    </tr>
                                    </tbody>
                                </table>
                            </li>
                        </ul>
                        <p style="font-style:italic">gavc = groupid:artifactid:version:classifier:extension</p>
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