<html>
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <meta name="author" content="dramadan"/>
    <meta name="description" content="Search Resource Documentation"/>

    <title>Search API Documentation</title>

    <!-- Bootstrap -->
    <link href="/public/twitter-bootstrap-2.3.2/css/bootstrap.css" rel="stylesheet"/>
    <link href="/public/twitter-bootstrap-2.3.2/css/bootstrap-responsive.css" rel="stylesheet">
    <link href="/public/twitter-bootstrap-2.3.2/css/docs.css" rel="stylesheet">

    <link rel="shortcut icon" type="image/x-icon" href="assets/img/grapes_small.gif"/>

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
                            <li class="">
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

    <header>
        <div class="container" >
            <div class="row">
                <h1>Search REST API Documentation</h1>
            </div>
        </div>
    </header>

    <div class="container">
        <div class="row">
            <div class="span4 bs-docs-sidebar">
                <ul class="nav nav-list bs-docs-sidenav" data-spy="affix" data-offset-top="80">
                    <li class=""><a data-toggle="collapse" data-target="#accordion1" href="#websearch"><i class="icon-chevron-right"></i>Web Search section</a></li>
                    <li class=""><a data-toggle="collapse" data-target="#accordion2" href="#search"><i class="icon-chevron-right"></i>Get Search result</a></li>
                </ul>
            </div>

            <div class="span8">
                <section id="websearch">
                    <a class="page-header btn-link" data-toggle="collapse" data-target="#accordion1">
                        <h2>@ /search</h2>
                    </a>
                    <div id="accordion1" class="collapse">
                        <ul>
                            <li>
                                <h3>GET</h3>
                                <ul>
                                    <li>Get Web search section</li>
                                    <li>Returns HTML</li>
                                </ul>
                            </li>
                        </ul>
                    </div>
                </section>
                <section id="search">
                    <a class="page-header btn-link" data-toggle="collapse" data-target="#accordion2">
                        <h2>@ /search/{text}</h2>
                    </a>
                    <div id="accordion2" class="collapse">
                        <ul>
                            <li>
                                <h3>GET</h3>
                                <ul>
                                    <li>Get search result</li>
                                    <li>Returns HTML view or JSON list of strings</li>
                                    <li>
                                        Optional parameters:
                                        <br/>
                                        <table class="table table-bordered table-hover" style="font-size:90%;margin-top:8px;">
                                            <thead>
                                            <tr>
                                                <td><strong>Parameter</strong></td>
                                                <td><strong>Default Value</strong></td>
                                                <td><strong>Description</strong></td>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            <tr>
                                                <td>modules</td>
                                                <td>true</td>
                                                <td>Filter search result - include modules. Use false to exclude.</td>
                                            </tr>
                                            <tr>
                                                <td>artifacts</td>
                                                <td>true</td>
                                                <td>Filter search result - include artifacts. Use false to exclude.</td>
                                            </tr>
                                            </tbody>
                                        </table>
                                    </li>
                                    <pre>${getSearchJsonModel()}</pre>
                                </ul>
                            </li>
                        </ul>
                    </div>
                </section>
            </div>
        </div>
    </div>

    <footer class="text-right" style="margin-top:20px">
        <p>Grapes ${programVersion!?html} </p>
    </footer>

    <!-- ==Javascript== -->
    <script src="/public/jquery-1.9.1/jquery.js"></script>
    <script src="/public/twitter-bootstrap-2.3.2/js/bootstrap.js"></script>

</body>
</html>