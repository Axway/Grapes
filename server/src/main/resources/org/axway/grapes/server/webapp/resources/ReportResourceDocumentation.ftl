<html>
	<head>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<meta name="description" content="Report Resource Documentation">
		
		<title>Reports API Documentation</title>
		
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
                    <h1>Report REST API Documentation</h1>
                </div>
            </div>
        </header>

        <div class="container">
        <div class="row">
        <div class="span4 bs-docs-sidebar">
            <ul class="nav nav-list bs-docs-sidenav" data-spy="affix" data-offset-top="80">
                <li class=""><a data-toggle="collapse" data-target="#accordion1" href="#report"><i class="icon-chevron-right"></i> Resource documentation</a></li>
                <li class=""><a data-toggle="collapse" data-target="#accordion2" href="#execute-report"><i class="icon-chevron-right"></i> Execute report</a></li>
            </ul>
        </div>
        <div class="span8">
        <section id="license">
            <a class="page-header btn-link" data-toggle="collapse" data-target="#accordion1">
                <h2>@ /report</h2>
            </a>
            <div id="accordion1" class="collapse">
                <ul>
                    <li>
                        <h3>GET</h3>
                        <ul>
                            <li>Lists all the available reports</li>
                            <li>Returns JSON</li>
                        </ul>
                    </li>
                </ul>
            </div>
        </section>
        <section id="artifact-names">
            <a class="page-header btn-link" data-toggle="collapse" data-target="#accordion2">
                <h2>@ /reports/execution</h2>
            </a>
            <div id="accordion2" class="collapse">
                <ul>
                    <li>
                        <h3>POST</h3>
                        <ul>
                            <li>Executes a report on the server</li>
                            <li>Returns CSV or Json structure</li>
                            <li>
                                Parameters:
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
                                        <td>reportId</td>
                                        <td>Report unique id</td>
                                    </tr>
                                    <tr>
                                        <td>paramValues</td>
                                        <td>An object containing a variable collection of names and values</td>
                                    </tr>
                                    </tbody>
                                </table>
                            </li>

                            <strong>Available Reports</strong>
                            <#list getReportSamples() as sample>
                                <li>
                                    <pre>${sample}</pre> <br/>
                                </li>
                            </#list>
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