<html>
	<head>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<meta name="author" content="jdcoffre">
		
		<title>Grapes Product View</title>
		
		<!-- Bootstrap -->
		<link href="/public/twitter-bootstrap-2.3.2/css/bootstrap.css" rel="stylesheet">
		<link href="/public/twitter-bootstrap-2.3.2/css/bootstrap-responsive.css" rel="stylesheet">
		<link href="/public/twitter-bootstrap-2.3.2/css/docs.css" rel="stylesheet">

        <link rel="shortcut icon" type="image/x-icon" href="assets/img/grapes_small.gif"/>

	</head>
    <body>
    	<div class="row-fluid">  
			<h3>Product</h3>
			<p>
                <strong>Name: </strong>${product.name}<br/>
                <strong>Organization: </strong>${product.organization}<br/>
			</p>

            <table class="table table-bordered table-hover">
                <thead>
                <tr>
                    <th>Module Names</th>
                </tr>
                </thead>
                <tbody>
                <#list getProduct().getModules() as moduleName>
                <tr>
                    <td class="moduleName" >${moduleName}</td>
                </tr>
                </#list>
                </tbody>
            </table>

            <table class="table table-bordered table-hover">
                <thead>
                <tr>
                    <th>Deliveries</th>
                </tr>
                </thead>
                <tbody>
                <#list getDeliveriesVersions() as versions>
                <tr>
                    <td class="delivery" >${versions}</td>
                </tr>
                </#list>
                </tbody>
            </table>
		</div>
	 </body>
</html>