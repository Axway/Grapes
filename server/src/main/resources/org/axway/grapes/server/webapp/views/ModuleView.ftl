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

        <link rel="shortcut icon" type="image/x-icon" href="assets/img/grapes_small.gif"/>

	</head>
    <body>
    	<div class="row-fluid" id="module.info">
			<h3>Module</h3>
			<p>
				<strong>Name: </strong>${module.getName()}<br/>
				<strong>Version: </strong>${module.getVersion()}<br/>
				<#if module.isPromoted()>
					<strong>Promoted</strong><br/>
			    <#else>
					<strong>Not promoted</strong><br/>
				</#if>

			</p>
			<br/>
		</div>
		<div class="row-fluid" id="module.artifacts">
            <table class="table table-bordered table-hover">
                <thead>
                    <tr>
                        <td>Submodule</td>
                        <td>GroupId</td>
                        <td>ArtifactId</td>
                        <td>Version</td>
                        <td>Classifier</td>
                        <td>Type</td>
                        <td>Extension</td>
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
	</body>
</html>