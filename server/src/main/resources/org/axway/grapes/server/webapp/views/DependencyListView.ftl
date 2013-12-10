<html>
	<head>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<meta name="author" content="jdcoffre">
		<meta name="description" content="Gavcs view report list the gavcs contained in Grapes.">
		
		<title>Grapes Dependency List View</title>
		
		<!-- Bootstrap -->
		<link href="/public/twitter-bootstrap-2.3.2/css/bootstrap.css" rel="stylesheet">
		<link href="/public/twitter-bootstrap-2.3.2/css/bootstrap-responsive.css" rel="stylesheet">
		<link href="/public/twitter-bootstrap-2.3.2/css/docs.css" rel="stylesheet">

        <link rel="shortcut icon" type="image/x-icon" href="assets/img/grapes_small.gif"/>

	</head>
    <body>
    	<h1>${title}</h1>
    	<div class="row-fluid">
    	    <#if getFilters(). getDecorator().getShowSources()>
                <table class="table table-bordered table-hover" id="list">
                    <thead>
                        <tr>
                            <td>Source</td>
                            <#if showTarget>
                                <td>Target</td>
                            </#if>
                            <#if getFilters(). getDecorator().getShowScopes()>
                                <td>Scope</td>
                            </#if>
                            <#if getFilters(). getDecorator().getShowLicenses()>
                                <td>License(s)</td>
                            </#if>
                        </tr>
                    </thead>
                    <tbody>
                        <#assign results = 0>
                        <#list getDependencies() as dependency>
                            <tr>
                                <td>${dependency.sourceName} (${dependency.sourceVersion})</td>
                                <#if showTarget>
                                    <td>${dependency.target.gavc} <#if dependency.target.moduleName!?length != 0 >(${dependency.target.moduleName})</#if></td>
                                </#if>
                                <#if getFilters(). getDecorator().getShowScopes()>
                                    <td>${dependency.scope.toString()?lower_case}</td>
                                </#if>
                                <#if getFilters(). getDecorator().getShowLicenses()>
                                    <td><#list dependency.target.licenses as license>${license} </#list></td>
                                </#if>
                            </tr>
                            <#assign results = results + 1>
                        </#list>
                    </tbody>
                </table>
			<#else>
                <table class="table table-bordered table-hover" id="list">
                    <thead>
                        <tr>
                            <td>Target</td>
                            <#if getFilters(). getDecorator().getShowProviders()>
                                <td>Provider</td>
                            </#if>
                            <#if getFilters(). getDecorator().getShowScopes()>
                                <td>Scope</td>
                            </#if>
                            <#if getFilters(). getDecorator().getShowLicenses()>
                                <td>License(s)</td>
                            </#if>
                        </tr>
                    </thead>
                    <tbody>
                        <#assign results = 0>
                        <#list getDependencyTarget() as target>
                            <tr>
                                <td>${target.getGavc()}</td>
                                <#if filters.decorator.showProviders>
                                    <td>${target.getProvider()}</td>
                                </#if>
                                <#if getFilters(). getDecorator().getShowScopes()>
                                    <td><#list getScopes(target.getGavc()) as scope>${scope?lower_case}  </#list></td>
                                </#if>
                                <#if getFilters(). getDecorator().getShowLicenses()>
                                    <td><#list target.licenses as license>${license} </#list></td>
                                </#if>
                            </tr>
                            <#assign results = results + 1>
                        </#list>
                    </tbody>
                </table>
			</#if>
		</div>
		<div class="row-fluid">
			(results: ${results})
		</div>
	    
	 </body>
</html>