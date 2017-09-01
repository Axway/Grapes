<#-- @ftlvariable name="" type="com.axway.ecd.d2d.dm.server.views.NotifierView" -->
    <html>
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="author" content="dramadan">
        <meta name="description" content="Search view display results from search">

        <title>Search</title>

        <!-- Bootstrap -->
        <meta name="description" content="The web application provides a ui over the grapes REST API">
        <link href="/public/twitter-bootstrap-2.3.2/css/bootstrap.css" rel="stylesheet">
        <link href="/public/twitter-bootstrap-2.3.2/css/bootstrap-responsive.css" rel="stylesheet">
        <link href="/public/twitter-bootstrap-2.3.2/css/docs.css" rel="stylesheet">

        <link rel="shortcut icon" type="image/x-icon" href="assets/img/grapes_small.gif"/>

        <!-- Grapes css -->
        <link href="/assets/css/grapes-table.css" rel="stylesheet">
        <link href="/assets/css/grapes.css" rel="stylesheet">
        <link href="/assets/css/grapes-webapp.css" rel="stylesheet">
        <link href="/assets/css/axway-loader.css" rel="stylesheet">

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
                                        <#if getOnlineDocumentation()??>
                                            <li><a tabindex="-1" href="${getOnlineDocumentation()}">Online Documentation</a></li>
                                        </#if>
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
                                <li class="active">
                                    <a href="/search">Search</a>
                                </li>
                                <#if getIssueTrackerUrl()??>
                                    <li class="">
                                        <a href="${getIssueTrackerUrl()}">Report an issue</a>
                                    </li>
                                </#if>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div id="searchForm">
            <div class="searchText form-group">
                <input id="s" name="s" class="form-control" type="text" placeholder="Search" tabindex="1" autofocus pattern="^[^.*]([^\s]+)*([^.*])?$"/>
                <input type="submit" class="btn btn-primary" value="Search" id="submitButton" onclick="getSearchResult()" tabindex="2" disabled/>
            </div>
            <div class="optionWrapper">
                <div class="searchRadio">
                    <input type="radio" value="all" id="all" name="filter" checked="true" onclick="filterRadioOptions(this)" tabindex="3"/>
                    <label for="all" id="siteNameLabel">All</label>

                    <input type="radio" value="filter" id="filtered" name="filter" onclick="filterRadioOptions(this)" tabindex="4"/>
                    <label for="filtered">Filtered</label>
                </div>
                <div class="searchCheckbox">
                    <input id="modules" type="checkbox" name="modules" value="modules" disabled class="options" onclick="filterCheckBoxOptions(this);" tabindex="5">
                    <label class="options" for="modules">Modules</label>

                    <input id="artifacts" type="checkbox" name="artifacts" value="artifacts" disabled class="options" onclick="filterCheckBoxOptions(this);" tabindex="6">
                    <label class="options" for="artifacts">Artifacts</label>
                </div>
            </div>
        </div>
        <div id="searchResultWrapper">
            <div class="overlay">
                <div class="loadingMessage"><i>Searching for data</i><div class="loader"><span>Loading...</span></div></div>
            </div>
            <div id="searchResult">
            </div>
        </div>

        <script src="/public/jquery-1.9.1/jquery.js"></script>
        <script src="/public/twitter-bootstrap-2.3.2/js/bootstrap.min.js"></script>
        <script src="/public/twitter-bootstrap-2.3.2/js/bootstrapValidator.js"></script>
        <script src="/assets/js/grapes-webApp.js"></script>
        <script src="/assets/js/grapes-commons.js"></script>

        <!-- Make the table sortable -->
        <script src="/public/jquery-tablesorter-1.10.2/jquery.tablesorter.min.js"></script>

    </body>
    </html>