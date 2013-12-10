<html>
	<head>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<meta name="author" content="jdcoffre">
		<meta name="description" content="License view displays a license of Grapes.">
		
		<title>Grapes License View</title>
		
		<!-- Bootstrap -->
		<link href="/public/twitter-bootstrap-2.3.2/css/bootstrap.css" rel="stylesheet">
		<link href="/public/twitter-bootstrap-2.3.2/css/bootstrap-responsive.css" rel="stylesheet">
		<link href="/public/twitter-bootstrap-2.3.2/css/docs.css" rel="stylesheet">

        <link rel="shortcut icon" type="image/x-icon" href="assets/img/grapes_small.gif"/>

	</head>
    <body>
    	<div class="row-fluid">  
			<h3>License</h3>
			<p>
				<strong>Name: </strong>${license.getName()}<br/>
				<strong>Long Name: </strong>${license.getLongName()}<br/>
				<strong>Comments: </strong>${license.getComments()}<br/>
				<strong>Regular Expression: </strong>${license.getRegexp()}<br/>
				<strong>Url: </strong>${license.getUrl()}<br/>
				<#if license.isApproved()??>
					<strong>Accepted: </strong>${license.isApproved()?string("yes", "refused")}<br/>
				<#else>
					<strong>To be validated.</strong><br/>
				</#if>
			</p>
			<br/>
		</div>
	 </body>
</html>