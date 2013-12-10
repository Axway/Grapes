<html>
	<head>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<meta name="author" content="jdcoffre">
		<meta name="description" content="List view report.">
		
		<title>Grapes ${title} View</title>
		
		<!-- Bootstrap -->
		<link href="/public/twitter-bootstrap-2.3.2/css/bootstrap.css" rel="stylesheet">
		<link href="/public/twitter-bootstrap-2.3.2/css/bootstrap-responsive.css" rel="stylesheet">
		<link href="/public/twitter-bootstrap-2.3.2/css/docs.css" rel="stylesheet">

        <link rel="shortcut icon" type="image/x-icon" href="assets/img/grapes_small.gif"/>

	</head>
    <body>
    	<h1>${title}</h1>
    	<div class="row-fluid" id="list">
			<table class="table table-bordered table-hover">
				<thead>
					<tr>
						<td>${itemName}</td>
					</tr>
				</thead>
				<tbody>
					<#assign results = 0>
					<#list items as item>
						<tr>
		    				<td>${item}</td>	
						</tr>   		
						<#assign results = results + 1>			
					</#list>
				</tbody>
			</table>
		</div>
		<div class="row-fluid">
			(results: ${results})
		</div>
	    
	 </body>
</html>