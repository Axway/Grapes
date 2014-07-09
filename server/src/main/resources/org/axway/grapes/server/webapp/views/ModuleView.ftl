<html>
	<head>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<meta name="author" content="jdcoffre">
		<meta name="description" content="Module view displays a module of Grapes.">

		<title>Grapes Module View</title>

		<!-- Bootstrap -->
		<link href="/public/twitter-bootstrap-2.3.2/css/bootstrap.css" rel="stylesheet">
		<link href="/public/twitter-bootstrap-2.3.2/css/bootstrap-responsive.css" rel="stylesheet">
        <link href="/public/twitter-bootstrap-2.3.2/css/docs.css" rel="stylesheet">
        <link href="/assets/css/grapes.css" rel="stylesheet">
        <link href="/assets/css/grapes-table.css" rel="stylesheet">

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
                <h1>Module</h1>
            </div>
            <div class="row-fluid">
                <div class="navbar grapes-ariane-navbar">
                    <div class="navbar-inner">
                        <div class="container">
                            <div class="brand">Grapes path:</div>
                            <ul class="nav">
                                <li class="divider-vertical"></li>
                                <li><a href="/module">/module</a></li>
                                <li class="divider-vertical"></li>
                                <li><a href="/module/${module.getName()}/versions">/${module.getName()}</a></li>
                                <li class="divider-vertical"></li>
                                <li><a class="active">/${module.getVersion()}</a></li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="container module-all">
            <div class="row-fluid" id="module.info">
                <h3>Overview</h3>
                <p>
                    <strong>Name: </strong>${module.getName()}<br/>
                    <strong>Version: </strong>${module.getVersion()}<br/>
                <#if module.isPromoted()>
                    <strong>Promoted</strong><br/>
                <#else>
                    <strong>Not promoted</strong><br/>
                </#if>
                    <strong>Organization: </strong>${getOrganization()}<br/>
                </p>
                <br/>
            </div>
            <div class="row-fluid" id="module.artifacts">
                <h3>Artifacts</h3>
                <table class="table table-bordered table-hover sortable">
                    <thead>
                    <tr>
                        <th>Submodule</th>
                        <th>GroupId</th>
                        <th>ArtifactId</th>
                        <th>Version</th>
                        <th>Classifier</th>
                        <th>Type</th>
                        <th>Extension</th>
                    </tr>
                    </thead>
                    <tbody>
                    <#list module.getArtifacts() as artifact>
                    <tr class="artifact" id="${artifact.gavc}">
                        <td class="artifact.submodule">${module.getName()}</td>
                        <td class="artifact.groupId">${artifact.groupId}</td>
                        <td class="artifact.artifactId">${artifact.artifactId}</td>
                        <td class="artifact.version">${artifact.version}</td>
                        <td class="artifact.classifier">${artifact.classifier}</td>
                        <td class="artifact.type">${artifact.type}</td>
                        <td class="artifact.extension">${artifact.extension}</td>
                    </tr>
                    </#list>
                    <#list submodules as submodule>
                        <#list submodule.artifacts as artifact>
                        <tr class="artifact" id="${artifact.gavc}">
                            <td class="artifact.submodule">${submodule.getName()}</td>
                            <td class="artifact.groupId">${artifact.groupId}</td>
                            <td class="artifact.artifactId">${artifact.artifactId}</td>
                            <td class="artifact.version">${artifact.version}</td>
                            <td class="artifact.classifier">${artifact.classifier}</td>
                            <td class="artifact.type">${artifact.type}</td>
                            <td class="artifact.extension">${artifact.extension}</td>
                        </tr>
                        </#list>
                    </#list>
                    </tbody>
                </table>
            </div>
        </div>
	</body>

    <!-- Make the table sortable -->
    <script src="/public/jquery-1.9.1/jquery.js"></script>
    <script src="/public/jquery-tablesorter-1.10.2/jquery.tablesorter.min.js"></script>
    <script type="text/javascript">
        $(function(){
            $('.sortable').tablesorter();
        });
    </script>
</html>