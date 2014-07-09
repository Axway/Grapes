<html>
	<head>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<meta name="author" content="jdcoffre">
		
		<title>Grapes Dependency List</title>
		<meta name="description" content="Dependency List">

        <link rel="shortcut icon" type="image/x-icon" href="assets/img/grapes_small.gif"/>

		<!-- Bootstrap -->
		<link href="/public/twitter-bootstrap-2.3.2/css/bootstrap.css" rel="stylesheet">
		<link href="/public/twitter-bootstrap-2.3.2/css/bootstrap-responsive.css" rel="stylesheet">
		<link href="/public/twitter-bootstrap-2.3.2/css/docs.css" rel="stylesheet">

		<!-- Grapes css -->
		<link href="/assets/css/grapes.css" rel="stylesheet">
		<link href="/assets/css/grapes-table.css" rel="stylesheet">

	</head>
    <body>

    	<h1>${title}</h1>

        <#assign table = getTable()>
        <#assign headers = table.getHeaders()>
        <#assign rows = table.getRows()>
        <div class="row-fluid" id='table_div'>
            <table class="table table-bordered table-hover sortable">
                <thead>
                    <tr>
                    <#list headers as header>
                        <th><span>${header}</span></th>
                    </#list>
                    </tr>
                </thead>
                <tbody>
                    <#list rows as row>
                        <tr>
                            <#list row as cell>
                                <td>${cell}</td>
                            </#list>
                        </tr>
                    </#list>
                </tbody>
            </table>
        </div>
	    
	 </body>

    <!-- Make the table sortable -->
    <script src="/public/jquery-1.9.1/jquery.js"></script>
    <script src="/public/jquery-tablesorter-1.10.2/jquery.tablesorter.min.js"></script>
    <script type="text/javascript">
        $(function(){
          $('.sortable').tablesorter();
        });
    </script>
	 
</html>