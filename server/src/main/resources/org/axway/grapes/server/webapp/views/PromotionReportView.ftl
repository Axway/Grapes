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

        <link rel="shortcut icon" type="image/x-icon" href="assets/img/grapes_small.gif"/>

	</head>
    <body>

    <#macro recurse_macro report depth>
        <#list report.unPromotedDependencies as module>
            <tr>
               <td align='left'>
                    <#if (depth>0) >|
                        <#list 1..depth as i>--</#list>
                    </#if>
                     ${module.getName()} (${module.getVersion()})
               </td>
            </tr>
            <#assign moduleId=getModuleUid(module)>
            <#if getTargetedDependencyReport(moduleId)??>
                <#assign child=getTargetedDependencyReport(moduleId)>
                <#if !child.canBePromoted()>
                    <@recurse_macro report=child depth=depth+1/>
                </#if>
            </#if>
        </#list>
    </#macro>

    </table>
    	<div class="row-fluid" id="list">
    	    <#if canBePromoted()>
                <div id="promotion_ok">The module can be promoted.<br/></div>
            <#else>
                <div id="promotion_ko"><strong>The module cannot be promoted!!!</strong><br/></div>

    	        <h3>Promotion Plan</h3>
                <table class="table table-bordered table-hover" id="has_to_be_promoted">
                    <thead>
                        <tr>
                            <td><strong>Dependencies to promote</strong></td>
                        </tr>
                    </thead>
                    <tbody>
                        <@recurse_macro report=getThis() depth=0/>
                    </tbody>
                </table>

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
            </#if>
		</div>
	    
	 </body>
</html>