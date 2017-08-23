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

        <div class="row-fluid">
            <div class="navbar navbar-inverse navbar-fixed-top">
                <div class="navbar-inner">
                    <div class="container">
                        <a class="brand active" href="/">Grapes</a>
                        <div class="nav-collapse collapse">
                            <ul class="nav">
                                <li class="">
                                    <a class="dropdown-toggle" data-toggle="dropdown" href="#">Documentations</a>
                                    <ul class="dropdown-menu" role="menu" aria-labelledby="drop">
                                        <li><a tabindex="-1" href="/organization">Organization API</a></li>
                                        <li><a tabindex="-1" href="/product">Product API</a></li>
                                        <li><a tabindex="-1" href="/module">Module API</a></li>
                                        <li><a tabindex="-1" href="/artifact">Artifact API</a></li>
                                        <li><a tabindex="-1" href="/license">License API</a></li>
                                        <li><a tabindex="-1" href="/report">Report API</a></li>
                                        <li><a tabindex="-1" href="/searchdoc">Search API</a></li>
                                    </ul>
                                </li>
                                <li class="">
                                    <a href="/sequoia">Sequoïa</a>
                                </li>
                                <li class="">
                                    <a href="/webapp">Data Browser</a>
                                </li>
                                <li class="">
                                    <a href="/search">Search</a>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="container" style="">
            <div class="row-fluid">
                <h1>${title}</h1>
            </div>
        </div>

        <#assign table = getTable()>
        <#assign headers = table.getHeaders()>
        <#assign rows = table.getRows()>
        <div class="container" style="">
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
                                <#list 0..row?size-1 as cell>
                                    <#if cell == 0>
                                        <td><a href="javascript:void(0)" onclick="getDependencyDirectLink(this)">${row[cell]}</a></td>
                                    <#else>
                                        <td>${row[cell]}</td>
                                    </#if>
                                </#list>
                            </tr>
                        </#list>
                    </tbody>
                </table>
            </div>
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