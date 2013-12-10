<html>
	<head>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<meta name="author" content="jdcoffre">
		<meta name="description" content="Table Of Dependencies.">
		<!-- Bootstrap -->
		<link href="/public/twitter-bootstrap-2.3.2/css/bootstrap.css" rel="stylesheet">
		<link href="/public/twitter-bootstrap-2.3.2/css/bootstrap-responsive.css" rel="stylesheet">
		<link href="/public/twitter-bootstrap-2.3.2/css/docs.css" rel="stylesheet">


	</head>
    <body>
		<table class="table table-bordered table-hover" id="list">
			<thead>
				<tr>
					<td>GroupId</td>
					<td>ArtifactId</td>
					<td>Current Version</td>
					<td>Last Release Version</td>
					<td>Scope</td>
					<td>Sources</td>
				</tr>
			</thead>
			<tbody>
				<#list dependencyTargets as target>
					<#assign firstVersion = 1>
					<#assign nbEntries = getNbEntry(target)>
					<#list getVersions(target) as version>
						<#assign firstDep = 1>
						<#assign nbDeps = getDependencies(target, version)?size>
						<#list getDependencies(target, version) as dependency>
						<#assign doNotUse = shouldNotBeUsed(target.getGavc())>
		    				<tr>
		    					<#if firstVersion == 1 && firstDep == 1>
								<td rowspan="${nbEntries}"><#if doNotUse == 1><strong></#if>${target.getGroupId()}<#if doNotUse == 1>*</strong></#if></td>
								</#if>
								<#if firstVersion == 1 && firstDep == 1>
								<td rowspan="${nbEntries}"><#if doNotUse == 1><strong></#if>${target.getArtifactId()}<#if doNotUse == 1>*</strong></#if></td>
								</#if>
								<#if firstDep == 1>
								<td rowspan="${nbDeps}"><#if doNotUse == 1><strong></#if>${version}<#if doNotUse == 1>*</strong></#if></td>
								</#if>
								<#if firstVersion == 1 && firstDep == 1>
								<td rowspan="${nbEntries}">${getLastVersion(target)}</td>
								</#if>
								<td>${dependency.getScope()?lower_case}</td>
								<td>${dependency.getSourceName()} (${dependency.getSourceVersion()})</td>
							</tr>
							<#assign firstDep = 0>
						</#list>
					<#assign firstVersion = 0>
					</#list>
				</#list>
			</tbody>
		</table>
        Artifact with "*" should not be used.
	 </body>
</html>