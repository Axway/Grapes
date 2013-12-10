<#-- @ftlvariable name="" type="com.axway.ecd.d2d.dm.server.views.NotifierView" -->
<html>
	<head>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<meta name="author" content="jdcoffre">
		<meta name="description" content="Sequoia">

		<title>Sequoia</title>

		<!-- Bootstrap -->
		<link href="/public/twitter-bootstrap-2.3.2/css/bootstrap.css" rel="stylesheet">
		<link href="/public/twitter-bootstrap-2.3.2/css/bootstrap-responsive.css" rel="stylesheet">
		<link href="/public/twitter-bootstrap-2.3.2/css/docs.css" rel="stylesheet">
		<link href="/assets/css/sequoia.css" rel="stylesheet">

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
                                <li class="active">
                                    <a href="#">Sequoia</a>
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

        <div class="container" style="width:90%;margin-top:5px;">
            <div class="row">
                <div class="span3" style="margin-top:60px;">
                    <div class="well sidebar-nav">
                        <ul class="nav nav-list">
                            <li class="nav-header">Module</li>
                            <li class="active">
                                <select id="moduleNames">
                                </select>
                            </li>
                            <li class="active">
                                <select id="moduleVersions"></select>
                            </li>
                            <li class="nav-header filters">Filters</li>
                            <li class="active">
                                <label class="checkbox filters">
                                    <input id="thirdParty" type="checkbox"> Show thirdparty
                                </label>
                            </li>
                            <li class="active">
                                <label class="checkbox filters">
                                    <input id="scopeRuntime" type="checkbox"> Show scope runtime
                                </label>
                            </li>
                            <li class="active">
                                <label class="checkbox filters">
                                    <input id="scopeTest" type="checkbox"> Show scope test
                                </label>
                            </li>

                            <li class="nav-header"><button id="getChart" type="submit" class="btn btn-primary">Get Chart</button></li>
                        </ul>
                    </div>
                </div>

                <div class="span9">
                    <ul id="myTab" class="nav nav-tabs">
                        <li id="graphTab" class="active"><a href="#graph" data-toggle="tab">Dependency Graph</a></li>
                        <li id="treeTab" class=""><a href="#tree" data-toggle="tab">Module Tree</a></li>
                    </ul>
                    <div id="myTabContent" class="tab-content">
                        <div class="tab-pane fade active in" id="graph">
                            <div class="hero-unit" id="chart-container">
                                <div id="graphChart" style="width:inherit; height:680"></div>
                            </div>
                        </div>
                        <div class="tab-pane fade" id="tree">
                            <div class="hero-unit" id="chart-container">
                                <div id="treeChart" style="width:inherit; height:680"></div>
                            </div>
                        </div>

                    </div>
                </div>
            </div>
            <div class="row">

                <footer>
                    Grapes ${programVersion!?html}.</p>
                </footer>

            </div>
        </div>

		<!-- == javascript == -->

        <script src="/public/jquery-1.9.1/jquery.js"></script>
        <script src="/public/twitter-bootstrap-2.3.2/js/bootstrap.js"></script>
        <script src="/public/d3js-2/d3.js"></script>
        <script src="/public/d3js-2/d3.layout.js"></script>

    	<!--  The Raphael JavaScript library for vector graphics display  -->
    	<script type="text/javascript" src="/public/raphael/raphael.js"></script>

    	<!--  Dracula JavaScript library that handles graphs  -->
    	<script type="text/javascript" src="/public/dracula/dracula_graffle.js"></script>
    	<script type="text/javascript" src="/public/dracula/dracula_graph.js"></script>
    	<script type="text/javascript" src="/public/dracula/dracula_graph.js"></script>
    	<script type="text/javascript" src="/public/dracula/dracula_algorithms.js"></script>

        <!-- Home made JavaScript library that handles Sequoia stuff -->
        <script src="/assets/js/sequoia.js"></script>

        <script type="text/javascript">
            function manageUrlParams(){
                if(getURLParameter('module') != "null" && getURLParameter('version') != "null"){
                    $("#moduleNames").val(getURLParameter('module'));
                    loadModuleVersions("/module/" + $("#moduleNames").val() + "/versions").done(function(){$("#moduleVersions").val(getURLParameter('version'));});
                }

            }

            function getChart(type){
                var dm_url = $("#moduleNames").val() + "/" + $("#moduleVersions").val() + "?";
                dm_url += "scopeTest=" + $("#scopeTest").is(':checked');
                dm_url += "&scopeRuntime=" + $("#scopeRuntime").is(':checked');
                dm_url += "&showThirdparty=" + $("#thirdParty").is(':checked');

                if(type == "graph" || $("#graphTab").hasClass("active")){
                    $("#graphChart").html('');
                    $("#graphChart").html('<img src="/assets/img/spinner.gif" alt="" id="loader-indicator" />');
                    getModuleGraph("/sequoia/graph/" + dm_url, "graphChart");
                }
                if(type == "tree" || $("#treeTab").hasClass("active")){
                    $("#treeChart").html('');
                    $("#treeChart").html('<img src="/assets/img/spinner.gif" alt="" id="loader-indicator" />');
                    getModuleTree("/sequoia/tree/" + dm_url, "treeChart", $("#treeChart").width(), $("#treeChart").height());
                }
            }

            $("#moduleNames").change(function () {
                loadModuleVersions("/module/" + $("#moduleNames").val() + "/versions");
            })

            $("#getChart").click(function () {getChart("")})

            $("#treeTab").click(function () {
                    getChart("tree");
                    $(".filters").css('visibility', 'hidden');
                }
            )

            $("#graphTab").click(function () {
                    getChart("graph");
                    $(".filters").css('visibility', 'visible');
                }
            )

            $(document).ready(function() {
                loadModuleNames().done(function(){
                    loadModuleVersions("/module/" + $("#moduleNames").val() + "/versions").done(function(){manageUrlParams();});
                    });
                }
            )
        </script>

	</body>
</html>