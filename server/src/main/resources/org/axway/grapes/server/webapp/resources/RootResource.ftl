<#-- @ftlvariable name="" type="com.axway.ecd.d2d.dm.server.views.NotifierView" -->
<html>
	<head>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<meta name="author" content="jdcoffre">
		<meta name="description" content="Grapes home page.">

		<title>Grapes</title>

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
                        <a class="brand active" href="#">Grapes</a>
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
                                        <li><a tabindex="-1" href="module">Module API</a></li>
                                        <li><a tabindex="-1" href="artifact">Artifact API</a></li>
                                        <li><a tabindex="-1" href="license">License API</a></li>
                                        <li><a tabindex="-1" href="/report">Report API</a></li>
                                    </ul>
                                </li>
                                <li class="">
                                    <a href="sequoia">Sequoïa</a>
                                </li>
                                <li class="">
                                    <a href="webapp">Data Browser</a>
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

		<div class="container">

            <div class="marketing">
                <img src="/assets/img/grapes_big.svg" style="margin-top:10%"/>
                <p class="marketing-byline">Ease the dependency management of your projects.</p>

                <hr class="soften">

                <h2>Enter the Grapes</h2>
                <p class="marketing-byline">Three API are available to help you to handle your dependencies.</p>

                <div class="row-fluid">
                  <div class="span4" onclick="location.href='sequoia';" style="cursor:pointer;">
                    <img class="marketing-img" src="assets/img/tree.png">
                    <h2>Sequoia</h2>
                    <p>Graph rendering tool able to display the dependencies and the architecture of your modules.</p>
                  </div>
                  <div class="span4" onclick="location.href='webapp';" style="cursor:pointer;">
                    <img class="marketing-img" src="assets/img/architecture.png">
                    <h2>Data Browser</h2>
                    <p>Web-application that provides to accredited users the possibility to modify or complete the dependencies metadata information.</p>
                  </div>
                  <div class="span4">
                    <img class="marketing-img" src="assets/img/web.png">
                    <h2>HTTP REST API</h2>
                    <p>Designed for complex reports or automated tasks around dependency management. Please, refer to the <a tabindex="-1" href="https://github.com/Axway/Grapes/wiki">documentation</a>.</p>
                  </div>
                </div>

                <hr class="soften">

                <h2>To nerds</h2>
                <p class="marketing-byline">For those who are interested in the implementation, Grapes is developed in Java & JavaScript.
                It uses <a href="http://dropwizard.codahale.com/" target="_blank">Dropwizard</a>, <a href="http://twitter.github.com/bootstrap/index.html">Twitter Bootstrap</a>, <a href="http://d3js.org/">D3js</a> & <a href="http://www.mongodb.org/">Mongodb</a>. </p>
            </div>
        </div>

		<footer>
			<p>Grapes ${programVersion!?html}.</p>
		</footer>

		<script src="public/jquery-1.9.1/jquery.js"></script>
		<script src="public/twitter-bootstrap-2.3.2/js/bootstrap.js"></script>

	</body>
</html>