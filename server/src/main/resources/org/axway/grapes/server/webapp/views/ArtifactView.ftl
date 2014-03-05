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

        <link rel="shortcut icon" type="image/x-icon" href="assets/img/grapes_small.gif"/>

	</head>
    <body>
    	<div class="row-fluid"  id="artifact.info">
			<h3>Artifact</h3>
			<p>
				<strong>GroupId: </strong>${artifact.getGroupId()}<br/>
				<strong>ArtifactId: </strong>${artifact.getArtifactId()}<br/>
				<strong>Version: </strong>${artifact.getVersion()}<br/>
				<strong>Classifier: </strong>${artifact.getClassifier()}<br/>
				<strong>Type: </strong>${artifact.getType()}<br/>
				<strong>Extension: </strong>${artifact.getExtension()}<br/>
				<strong>Provider: </strong>${artifact.getProvider()}<br/>
				<strong>DownloadUrl: </strong>${artifact.getDownloadUrl()}<br/>
				<#if isCorporate() && moduleName??>
				<strong>Module: </strong>${moduleName} in version ${moduleVersion}<br/>
				</#if>
				<#if shouldNotBeUsed()>
				<strong>This artifact should not be used!!!</strong><br/>
				</#if>
			</p>
			<br/>
		</div>
	 </body>
</html>