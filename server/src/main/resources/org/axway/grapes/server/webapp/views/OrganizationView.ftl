<html>
	<head>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<meta name="author" content="jdcoffre">
		<meta name="description" content="Organization view displays a license of Grapes.">
		
		<title>Grapes Organization View</title>
		
		<!-- Bootstrap -->
		<link href="/public/twitter-bootstrap-2.3.2/css/bootstrap.css" rel="stylesheet">
		<link href="/public/twitter-bootstrap-2.3.2/css/bootstrap-responsive.css" rel="stylesheet">
		<link href="/public/twitter-bootstrap-2.3.2/css/docs.css" rel="stylesheet">

        <link rel="shortcut icon" type="image/x-icon" href="assets/img/grapes_small.gif"/>

	</head>
    <body>
    	<div class="row-fluid">  
			<h3>Organization</h3>
			<p>
				<strong>Name: </strong>${organization.getName()}<br/>
			</p>

			<table class="table table-bordered table-hover">
                <thead>
                    <tr>
                        <td>Corporate GroupId prefixes</td>
                    </tr>
                </thead>
			    <tbody>
			        <#list organization.getCorporateGroupIdPrefixes() as corporateGid>
			            <tr class="corporateGroupId">
                            <td>${corporateGid}</td>
                        </tr>
                    </#list>
			    </tbody>
			</table>
		</div>
	 </body>
</html>