<html>
	<head>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<meta name="author" content="jdcoffre">
		<meta name="description" content="License Resource Documentation">
		
		<title>License API Documentation</title>
		
		<!-- Bootstrap -->
		<link href="/public/twitter-bootstrap-2.3.2/css/bootstrap.css" rel="stylesheet">
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
                                        <li><a tabindex="-1" href="/searchdoc">Search API</a></li>
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
                    <h1>License REST API Documentation</h1>
                </div>
            </div>
        </header>

        <div class="container">
        <div class="row">
        <div class="span4 bs-docs-sidebar">
            <ul class="nav nav-list bs-docs-sidenav" data-spy="affix" data-offset-top="80">
                <li class=""><a data-toggle="collapse" data-target="#accordion1" href="#license"><i class="icon-chevron-right"></i> Resource documentation</a></li>
                <li class=""><a data-toggle="collapse" data-target="#accordion1" href="#license"><i class="icon-chevron-right"></i> Add/update a license</a></li>
                <li class=""><a data-toggle="collapse" data-target="#accordion2" href="#license-names"><i class="icon-chevron-right"></i> Get all license names</a></li>
                <li class=""><a data-toggle="collapse" data-target="#accordion3" href="#license-target"><i class="icon-chevron-right"></i> Get a license</a></li>
                <li class=""><a data-toggle="collapse" data-target="#accordion3" href="#license-target"><i class="icon-chevron-right"></i> Remove a license</a></li>
                <li class=""><a data-toggle="collapse" data-target="#accordion3" href="#artifact-target"><i class="icon-chevron-right"></i> Validate/unvalidate license</a></li>
            </ul>
        </div>
        <div class="span8">
        <section id="license">
            <a class="page-header btn-link" data-toggle="collapse" data-target="#accordion1">
                <h2>@ /license</h2>
            </a>
            <div id="accordion1" class="collapse">
                <ul>
                    <li>
                        <h3>GET</h3>
                        <ul>
                            <li>Get License resource documentation</li>
                            <li>Returns HTML</li>
                        </ul>
                    </li>
                    <li>
                        <h3>POST</h3>
                        <ul>
                            <li>Add/update license</li>
                            <li>Expects a JSON license in the request content</li>
                            <li>Return status 201 if ok 400 if the Json does not suits the model</li>
                            <li>Json License example:
                                <pre>${getLicenseJsonModel()}</pre>
                            </li>
                        </ul>
                    </li>
                </ul>
            </div>
        </section>
        <section id="artifact-names">
            <a class="page-header btn-link" data-toggle="collapse" data-target="#accordion2">
                <h2>@ /license/names</h2>
            </a>
            <div id="accordion2" class="collapse">
                <ul>
                    <li>
                        <h3>GET</h3>
                        <ul>
                            <li>Get all license names</li>
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
                                        <td>approved</td>
                                        <td>null</td>
                                        <td>Filter licenses regarding if the "approve" field is true/false</td>
                                    </tr>
                                    <tr>
                                        <td>toBeValidated</td>
                                        <td>null</td>
                                        <td>Filter licenses regarding if the "approve" field is null or not</td>
                                    </tr>
                                    </tbody>
                                </table>
                            </li>
                        </ul>
                    </li>
                </ul>
            </div>
        </section>
        <section id="license-target">
            <a class="page-header btn-link" data-toggle="collapse" data-target="#accordion3">
                <h2>@ /license/{name}</h2>
            </a>
            <div id="accordion3" class="collapse">
                <ul>
                    <li>
                        <h3>GET</h3>
                        <ul>
                            <li>Get a license</li>
                            <li>
                                Returns HTML view or a Json license
                                <pre>${getLicenseJsonModel()}</pre>
                            </li>
                        </ul>
                    </li>
                    <li>
                        <h3>DELETE</h3>
                        <ul>
                            <li>Remove a license</li>
                            <li>Return status 200 if ok</li>
                        </ul>
                    </li>
                    <li>
                        <h3>POST</h3>
                        <ul>
                            <li>Validate or un validate a license</li>
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
                                        <td>approved</td>
                                        <td>Boolean to approve or reject the license</td>
                                    </tr>
                                    </tbody>
                                </table>
                            </li>
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