<html>
	<head>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<meta name="author" content="jdcoffre">
		<meta name="description" content="Artifact view displays an artifact of Grapes.">
		
		<title>Grapes Artifact View</title>
		
		<!-- Bootstrap -->
		<link href="/public/twitter-bootstrap-2.3.2/css/bootstrap.css" rel="stylesheet">
		<link href="/public/twitter-bootstrap-2.3.2/css/bootstrap-responsive.css" rel="stylesheet">
		<link href="/public/twitter-bootstrap-2.3.2/css/docs.css" rel="stylesheet">


        <!-- Grapes css -->
        <link href="/assets/css/grapes.css" rel="stylesheet">
        <link href="/assets/css/grapes-table.css" rel="stylesheet">

        <link rel="shortcut icon" type="image/x-icon" href="assets/img/grapes_small.gif"/>

        <script src="/public/jquery-1.9.1/jquery.js"></script>
        <script src="/public/twitter-bootstrap-2.3.2/js/bootstrapValidator.js"></script>
        <script src="/assets/js/grapes-commons.js"></script>


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
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="container">
            <div class="row-fluid">
                <h1>Artifact</h1>
            </div>
        </div>

        <div class="container" id="artifact_info">
            <div class="row-fluid"  id="artifact_overview">
                <h3>Overview</h3>

                <p>
                    <button type="button"
                            class="btn btn-inverse"
                            aria-label="Left Align"
                            onclick="navigateToArtifactInDB('${artifact.getGroupId()}', '${artifact.getArtifactId()}', '${artifact.getVersion()}')">
                        <span class="icon-white icon-list" aria-hidden="true"></span>
                        Select in Data Browser
                    </button>
                </p>

                <p>
                    <strong>GroupId: </strong>${artifact.getGroupId()}<br/>
                    <strong>ArtifactId: </strong>${artifact.getArtifactId()}<br/>
                    <strong>Version: </strong>${artifact.getVersion()}<br/>
                    <strong>Classifier: </strong>${artifact.getClassifier()}<br/>
                    <strong>Type: </strong>${artifact.getType()}<br/>
                    <strong>Extension: </strong>${artifact.getExtension()}<br/>
                    <strong>Origin: </strong>${artifact.getOrigin()}<br/>
                    <strong>SHA-256: </strong>${artifact.getSha256()}<br/>
                    <strong>Provider: </strong>${artifact.getProvider()}<br/>
                    <strong>DownloadUrl: </strong>${artifact.getDownloadUrl()}<br/>
                    <strong>Description: </strong>${artifact.getDescription()}<br/>
                    <#if artifact.getCreatedDateTime()??>
                        <strong>Created date: </strong>${artifact.getCreatedDateTime()?datetime}<br/>
                    </#if>
                    <#if artifact.getUpdatedDateTime()??>
                        <strong>Last updated date: </strong>${artifact.getUpdatedDateTime()?datetime}<br/>
                    </#if>
                    <#if isCorporate() && moduleName??>
                    <strong>Module: </strong>${moduleName} in version ${moduleVersion}<br/>
                    </#if>
                    <#if shouldNotBeUsed()>
                    <strong>This artifact should not be used!!!</strong><br/>
                        <#if comment??>
                        <table class="table table-bordered table-hover">
                            <thead>
                                <tr>
                                    <th>Comment</th>
                                    <th>Commented by</th>
                                    <th>Date</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td>${comment.getCommentText()}</td>
                                    <td>${comment.getCommentedBy()}</td>
                                    <td>${comment.getCreatedDateTime()?datetime}</td>
                                </tr>
                            </tbody>
                        </table>
                        </#if>
                    </#if>
                </p>
                <br/>
            </div>
        </div>
	 </body>
</html>