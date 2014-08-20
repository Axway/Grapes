<html>
	<head>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<meta name="author" content="jdcoffre">
		<meta name="description" content="List view report.">
		
		<title>Promotion Report View</title>
		
		<!-- Bootstrap -->
		<link href="/public/twitter-bootstrap-2.3.2/css/bootstrap.css" rel="stylesheet">
		<link href="/public/twitter-bootstrap-2.3.2/css/bootstrap-responsive.css" rel="stylesheet">
		<link href="/public/twitter-bootstrap-2.3.2/css/docs.css" rel="stylesheet">
        <link href="/assets/css/grapes.css" rel="stylesheet">

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
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="container" style="">
            <div class="row-fluid">
                <h1>Promotion Report</h1>
            </div>
        </div>

        <div class="container" style="">
            <div class="row-fluid" id="list">
                </table>
                <div class="row-fluid" id="list">
                    <#if canBePromoted()>
                        <div id="promotion_ok">The module can be promoted.<br/></div>
                    <#else>
                        <div id="promotion_ko"><strong>The module cannot be promoted!!!</strong><br/></div>

                        <#if getReportsWithDoNotUseArtifacts()?has_content >
                            <h3>ThirdParty that should not be used</h3>
                            <div id="should_not_be_used">
                                <#list getReportsWithDoNotUseArtifacts() as report>
                                    <table class="table table-bordered table-hover">
                                        <thead>
                                        <tr>
                                            <td><strong>${report.rootModule.getName()}</strong></td>
                                        </tr>
                                        </thead>
                                        <tbody>
                                            <#list report.doNotUseArtifacts as artifact>
                                            <td>${artifact.getGavc()}</td>
                                            </#list>
                                        </tbody>
                                    </table>
                                </#list>
                            </div>
                        </#if>

                        <#if getPromotionPlan()?has_content >
                            <h3>Promotion Plan</h3>
                            <table class="table table-bordered table-hover" id="has_to_be_promoted">
                                <thead>
                                <tr>
                                    <td><span><strong>Ordered dependencies to promote</strong></td>
                                </tr>
                                </thead>
                                <tbody>
                                    <#list getPromotionPlan() as module>
                                    <tr>
                                        <td>${module}</td>
                                    </tr>
                                    </#list>
                                </tbody>
                            </table>
                        </#if>
                    </#if>

                    <#if getMisMatchModules()?has_content >
                        <h3>Warning: some dependencies occurs in different versions</h3>
                        <div id="mismatchVersions">
                            <table class="table table-bordered table-hover">
                                <thead>
                                <tr>
                                    <td><strong>Module Name</strong></td>
                                    <td><strong>Versions</strong></td>
                                </tr>
                                </thead>
                                <tbody>
                                    <#list getMisMatchModules() as module>
                                    <tr>
                                        <td>${module}</td>
                                        <td><#list getMisMatchVersions(module) as version> ${version} </#list></td>
                                        </#list>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </#if>

                </div>
            </div>
		</div>
	 </body>

    <!-- JavaScript -->
    <script src="/public/jquery-1.9.1/jquery.js"></script>
    <script src="/public/twitter-bootstrap-2.3.2/js/bootstrap.min.js"></script>

</html>