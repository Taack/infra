<%@ page import="taack.ui.dsl.helper.Utils; taack.app.TaackAppRegisterService" %>
<!doctype html>
<html>
<head>
    <title>Welcome to Grails</title>
</head>
<body>

<div id="content" role="main">
    <div class="container">
        <section class="row colset-2-its">
            <h1>Welcome to Grails</h1>

            <p>
                Congratulations, you have successfully started your first Grails application! At the moment
                this is the default page, feel free to modify it to either redirect to a controller or display
                whatever content you may choose. Below is a list of controllers that are currently deployed in
                this application, click on each to execute its default action:
            </p>

            <div id="controllers" role="navigation">
                <h2>Available Apps:</h2>
                <div class="controller">
                    <g:each var="a" in="${TaackAppRegisterService.apps }">
                            <g:link controller="${Utils.getControllerName(a.entryPoint)}" action="${a.entryPoint.method}">
                                <div class="taack-app" style="width: 220px; padding: 35px; text-align: center;display: inline-grid;">${raw(a.svg)}<div>${a.label}: ${a.desc}</div></div>

                            </g:link>
                    </g:each>
                </div>

                <h2>Available Controllers:</h2>
                <ul>
                    <g:each var="c" in="${grailsApplication.controllerClasses.sort { it.fullName } }">
                        <li class="controller">
                            <g:link controller="${c.logicalPropertyName}">${c.fullName}</g:link>
                        </li>
                    </g:each>
                </ul>
            </div>
        </section>
    </div>
</div>

</body>
</html>
